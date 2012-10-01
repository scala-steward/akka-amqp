package akka.amqp

import akka.actor.FSM.{ UnsubscribeTransitionCallBack, CurrentState, Transition, SubscribeTransitionCallBack }
import akka.dispatch.{ Terminate }
import akka.testkit.{ AkkaSpec, TestKit, TestFSMRef }
import akka.actor.{ ActorSystem, PoisonPill }
import scala.concurrent.util.duration._
import scala.concurrent.{ Await }
import com.typesafe.config.ConfigFactory
import scala.concurrent.Promise

class ValidConnectionSpec extends AkkaSpec(AmqpConfig.Valid.config) {
  "Durable Connection Actor" should {

    val connectionActor = TestFSMRef(new DurableConnectionActor(AmqpConfig.Valid.settings))

    "start disconnected" in {
      connectionActor.stateName must be === Disconnected
    }
    "connect" in {
      connectionActor.stateName must be === Disconnected
      connectionActor ! Connect
      connectionActor.stateName must be === Connected
    }
    
        "reconnect on ShutdownSignalException" in new TestKit(ActorSystem("reconnect",AmqpConfig.Valid.config)) with AmqpMock {
      try {
        within(5 second) {
          connectionActor ! SubscribeTransitionCallBack(testActor)
          expectMsg(CurrentState(connectionActor, Connected))
          connectionActor ! new ShutdownSignalException(true, false, "Test (Mock Exception for testing)", connection)
          expectMsg(Transition(connectionActor, Connected, Disconnected))
          expectMsg(Transition(connectionActor, Disconnected, Connected))
        }
      } finally {
        connectionActor ! UnsubscribeTransitionCallBack(testActor)
        testActor ! Terminate()
      }
    }
    "disconnect" in {
      connectionActor.stateName must be === Connected
      connectionActor ! Disconnect
      connectionActor.stateName must be === Disconnected
    }
        "dispose" in {
      connectionActor ! Disconnect
      connectionActor ! PoisonPill
      connectionActor.isTerminated must be === true
    }
        "close the Rabbit Connection when the ActorSystem shuts down" in new TestKit(ActorSystem("callback",AmqpConfig.Valid.config)) {
          val conn : RabbitConnection = Await.result(AmqpExtension(system).connection.withConnection(x =>x),5 seconds)
          conn.isOpen() must be === true
          system.shutdown
          
          awaitCond(conn.isOpen() == false, 5 seconds, 100 milli)
          
        } 
}
  
    "Durable Connection" should {
    "execute callback on connection when connected" in new TestKit(ActorSystem("callback",AmqpConfig.Valid.config)) {
def conn = AmqpExtension(system).connection
      try {
        val connectionActor = conn.durableConnectionActor
        connectionActor ! SubscribeTransitionCallBack(testActor)
        expectMsg(CurrentState(connectionActor, Connected))
        val portFuture = conn.withConnection(_.getPort)
        val promise = Promise.successful(5672).future
        Await.ready(portFuture, 5 seconds).value must be === Await.ready(promise, 5 seconds).value
      } finally {
        conn.dispose()
      }
    }
  }
}


class NoConnectionSpec extends AkkaSpec(AmqpConfig.Invalid.config) {
  "Durable Connection" should {
 "never connect using non existing host addresses" in {
      val connectionActor = TestFSMRef(new DurableConnectionActor( AmqpConfig.Invalid.settings))
      try {
        connectionActor ! Connect
        within(2500 milli) {
          connectionActor ! SubscribeTransitionCallBack(testActor)
          expectMsg(CurrentState(connectionActor, Disconnected))
        }
        connectionActor.stateName must be === Disconnected
      } finally {
        testActor ! Terminate()
        connectionActor ! Disconnect // to cancel reconnect timer
        connectionActor ! PoisonPill
      }
    }
  }
}
  



   
  

