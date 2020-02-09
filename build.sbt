organization := "com.rememberthemilk"

name := "akka-amqp"

version := "2.6-SNAPSHOT"

scalaVersion := "2.13.1"

val akkaVersion          = "2.6.1"
val rabbitmqVersion      = "5.8.0"
val mockitoVersion       = "1.10.19"
val scalatestVersion     = "3.1.0"
val scalatestPlusVersion = "1.0.0-M2"
val rabbitmqMockVersion  = "1.0.14"

libraryDependencies ++= Seq(
  "com.rabbitmq"      % "amqp-client" % rabbitmqVersion,
  "com.typesafe.akka" %% "akka-actor" % akkaVersion
)

libraryDependencies ++= Seq(
  "com.typesafe.akka"  %% "akka-testkit"          % akkaVersion          % Test,
  "org.mockito"        % "mockito-all"            % mockitoVersion       % Test,
  "org.scalatest"      %% "scalatest"             % scalatestVersion     % Test,
  "org.scalatestplus"  %% "scalatestplus-mockito" % scalatestPlusVersion % Test,
  "com.github.fridujo" % "rabbitmq-mock"          % rabbitmqMockVersion  % Test
)

resolvers ++= Seq(
  "Sonatype OSS Releases".at("https://oss.sonatype.org/content/repositories/releases/")
)

scalacOptions ++= Seq(
  "-Xfatal-warnings",
  "-Xlint",
  "-Ywarn-dead-code",
  "-Ywarn-unused",
  "-Ywarn-value-discard",
  "-deprecation",
  "-feature",
  "-language:implicitConversions",
  "-opt:l:inline",
  "-opt:l:method",
  "-unchecked"
)
