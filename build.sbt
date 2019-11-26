organization := "com.rememberthemilk"

name := "akka-amqp"

version := "2.6-SNAPSHOT"

scalaVersion := "2.13.1"

resolvers ++= Seq(
  "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases/",
)

libraryDependencies ++= Seq(
  "com.rabbitmq" % "amqp-client" % "5.7.3",
  "com.typesafe.akka" %% "akka-actor" % "2.6.0"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-testkit" % "2.6.0" % Test,
  "org.mockito" % "mockito-all" % "1.10.19" % Test,
  "org.scalatest" %% "scalatest" % "3.0.8" % Test,
  "com.github.fridujo" % "rabbitmq-mock" % "1.0.12" % Test
)

scalacOptions ++= Seq(
  "-Ywarn-dead-code",
  "-deprecation",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-opt:l:inline",
  "-opt:l:method",
  "-unchecked"
)
