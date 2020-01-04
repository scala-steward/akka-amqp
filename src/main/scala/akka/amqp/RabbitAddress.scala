package akka.amqp

class RabbitAddress(host: String, port: Int) extends com.rabbitmq.client.Address(host, port) {
  def this(host: String) = this(host, -1)
}

object RabbitAddress {
  def parseAddress = com.rabbitmq.client.Address.parseAddress _
}
