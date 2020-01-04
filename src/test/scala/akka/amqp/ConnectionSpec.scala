package akka.amqp

import akka.actor.FSM.{CurrentState, SubscribeTransitionCallBack, Transition, UnsubscribeTransitionCallBack}
import akka.testkit.{AkkaSpec, TestFSMRef, TestActors}
import akka.actor.{ActorRef, PoisonPill}
import com.github.fridujo.rabbitmq.mock.MockConnectionFactory
import scala.concurrent.duration._
import scala.concurrent.{Await}
import scala.concurrent.Promise
import akka.pattern.ask
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpec

class ValidConnectionSpec extends AnyWordSpec with BeforeAndAfterAll {
  abstract class AkkaContext extends AkkaSpec(AmqpConfig.Valid.config) with AmqpTest with AmqpMock {
    val connectionActor = TestFSMRef(new ConnectionActor(new MockConnectionFactory, AmqpConfig.Valid.settings))

    def addConnectionChildren(refs: ActorRef*): Unit =
      refs.foreach(ref => connectionActor.underlyingActor.context.actorOf(TestActors.forwardActorProps(ref)))
  }

  def withAkkaContext(f: AkkaContext => Any) = {
    val context = new AkkaContext {}
    context.beforeAll
    f(context)
    context.afterAll
  }

  "Durable Connection Actor" should {
    "start disconnected" in withAkkaContext { context =>
      import context._
      connectionActor.stateName shouldBe Disconnected
    }
    "connect" in withAkkaContext { context =>
      import context._
      connectionActor.stateName shouldBe Disconnected
      connectionActor ! Connect
      connectionActor.stateName shouldBe Connected
    }
    "reconnect on ShutdownSignalException" in withAkkaContext { context =>
      import context._
      try {
        within(5.second) {
          connectionActor ! Connect
          connectionActor ! SubscribeTransitionCallBack(testActor)
          expectMsg(CurrentState(connectionActor, Connected))
          val method = new com.rabbitmq.client.impl.AMQImpl.Access.RequestOk(1)
          connectionActor ! new ShutdownSignalException(true, false, method, connection)
          expectMsg(Transition(connectionActor, Connected, Disconnected))
          expectMsg(Transition(connectionActor, Disconnected, Connected)) //reconnect success
        }
      } finally {
        connectionActor ! UnsubscribeTransitionCallBack(testActor)
        testActor ! PoisonPill
      }
    }
    "disconnect" in withAkkaContext { context =>
      import context._
      connectionActor ! Connect
      connectionActor.stateName shouldBe Connected
      connectionActor ! Disconnect
      connectionActor.stateName shouldBe Disconnected
    }
    "dispose (PoisonPill)" in withAkkaContext { context =>
      import context._
      connectionActor ! Connect
      val conn = rabbitConnectionAwait
      conn.isOpen() shouldBe true
      connectionActor.stateName shouldBe Connected
      connectionActor ! PoisonPill
      connectionActor.isTerminated shouldBe true
      conn.isOpen() shouldBe false //make sure that the connection is really closed.
    }
    "close the Rabbit Connection when the ActorSystem shuts down" in withAkkaContext { context =>
      import context._
      connectionActor ! Connect
      val conn: RabbitConnection = rabbitConnectionAwait
      conn.isOpen() shouldBe true
      system.terminate

      awaitCond(conn.isOpen() == false, 5.seconds, 100.milli)
    }
    "propagate state transition to child Actors" in withAkkaContext { context =>
      import context._
      import ChannelActor.{Available, Unavailable}

      val channel = Await.result((connectionActor ? CreateChannel(false)).mapTo[akka.actor.ActorRef], 1.second)
      channel ! SubscribeTransitionCallBack(testActor)
      expectMsg(CurrentState(channel, Unavailable))

      connectionActor ! Connect
      expectMsg(Transition(channel, Unavailable, Available))

      connectionActor ! Disconnect
      expectMsg(Transition(channel, Available, Unavailable))

      // reconnect
      connectionActor ! Connect
      expectMsg(Transition(channel, Unavailable, Available))
    }
    "send new channels to child Actors on reconnect" in withAkkaContext { context =>
      import context._

      addConnectionChildren(testActor, testActor, testActor)

      connectionActor ! Connect

      expectMsgType[NewChannel]
      expectMsgType[NewChannel]
      expectMsgType[NewChannel]
      expectNoMessage
    }
    "send disconnect message to child Actors on disconnect" in withAkkaContext { context =>
      import context._

      addConnectionChildren(testActor, testActor, testActor)

      connectionActor ! Connect

      receiveN(3)

      connectionActor ! Disconnect

      expectMsgAllOf(ConnectionDisconnected, ConnectionDisconnected, ConnectionDisconnected)
      expectNoMessage
    }
    "send NewChannel message to child actor upon creation in Connected state" in pending
    "do not send NewChannel message to child actor upon creation in Disconnected state" in pending

    "execute callback on connection when connected" in withAkkaContext { context =>
      import context._
      connectionActor ! Connect
      connectionActor ! SubscribeTransitionCallBack(testActor)
      expectMsg(CurrentState(connectionActor, Connected))
      val portFuture = withConnection(_.getPort)
      val promise    = Promise.successful(5672).future
      Await.ready(portFuture, 5.seconds).value shouldBe Await.ready(promise, 5.seconds).value
    }
  }
}

class NoConnectionSpec extends AkkaSpec(AmqpConfig.Invalid.config) {
  "Durable Connection" should {
    "never connect using non existing host addresses" in {
      val connectionActor = TestFSMRef(new ConnectionActor(new ConnectionFactory, AmqpConfig.Invalid.settings))
      try {
        connectionActor ! Connect
        within(2500.milli) {
          connectionActor ! SubscribeTransitionCallBack(testActor)
          expectMsg(CurrentState(connectionActor, Disconnected))
        }
        connectionActor.stateName shouldBe Disconnected
      } finally {
        testActor ! PoisonPill
        connectionActor ! Disconnect // to cancel reconnect timer
        connectionActor ! PoisonPill
      }
    }
  }
}
