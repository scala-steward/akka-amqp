//package akka.amqp
//
//import java.util.concurrent.CountDownLatch
//import akka.actor.{ ActorSystem, Actor, Props }
//
//object Scratch extends App {
//
//  val system = ActorSystem("amqp")
//  implicit val ext = AmqpExtension(system)
//
//  val nrOfMessages = 2000
//  val latch = new CountDownLatch(nrOfMessages)
//
//  val deliveryHandler = system.actorOf(Props(new Actor {
//    def receive = {
//      case delivery @ Delivery(payload, routingKey, deliveryTag, isRedeliver, properties, sender) ⇒
//        println("D: " + new String(payload))
//
//        delivery.acknowledge()
//        latch.countDown()
//    }
//  }))
//  val consumerChannel = ext.connection.newChannelForConsumer(false)
//  
//  val mq = consumerChannel ? Queue("test") 
//  
//  consumerChannel.newConsumer(mq, deliveryHandler, autoAcknowledge, queueBindings).newConsumer(Queue("test"), deliveryHandler)
//  consumer.awaitStart()
//
//  val publisher = ext.connection.newStashingPublisher(DefaultExchange)
//
//  for (i ← 1 to nrOfMessages) {
//    publisher.publish(Message("Message[%s]".format(i).getBytes, "test"))
//  }
//
//  latch.await()
//  connection.dispose()
//}
