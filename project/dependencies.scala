import sbt._

object dependencies {
  def Scalatest    = "org.scalatest" % "scalatest_2.11" % "2.2.2" % "test"       // ApacheV2
  def scalaActorsForScalaTest = "org.scala-lang" % "scala-actors" % "2.11.8" % "test"
  def AmqpClient = "com.rabbitmq" % "amqp-client" % "4.0.0"                               // ApacheV2


  def AkkaAgent = "com.typesafe.akka" % "akka-agent_2.11" % "2.4.14"

  def Specs2      = "org.specs2"                 % "specs2-core_2.11"              % "3.7.2"        % "test"  // MIT
  def JUnit = "junit" % "junit" % "4.11" % "test"                                   // Common Public License 1.0
  def AkkaTestKit = "com.typesafe.akka" % "akka-testkit_2.11" % "2.4.14" % "test"
  def Mockito = "org.mockito" % "mockito-all" % "1.10.19" % "test"                          // MIT
}
