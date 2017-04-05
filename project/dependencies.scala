import sbt._

object dependencies {
  def AmqpClient = "com.rabbitmq" % "amqp-client" % "4.1.0"
  def AkkaAgent = "com.typesafe.akka" %% "akka-agent" % "2.4.17"
}
