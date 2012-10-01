import sbt._
import Keys._

object AkkaAmqpBuild extends Build {
  import dependencies._
 

  
  lazy val standardSettings = Project.defaultSettings ++ Seq(
  	resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/",
    organization := "com.github.momania",
    version			 := "2.2-SNAPSHOT",
    scalaVersion := "2.10.0-M7"
  )

  //  lazy val amqp = Project(
  //  id = "akka-amqp",
  //  base = file("akka-amqp"),
  //  dependencies = Seq(actor, actorTests % "test->test", testkit % "test->test"),
  //  settings = defaultSettings ++ Seq(
  //    libraryDependencies ++= Dependencies.amqp
  //  )
 // )
  
  lazy val root = Project(
    id        = "akka-amqp",
    base      = file("."),
    settings = standardSettings ++ Seq(
    	libraryDependencies ++= Seq( 
	      AmqpClient,
        AkkaActor,
	      Specs2,
        JUnit,
		Scalatest,
		scalaActorsForScalaTest,
		//ActorTests,
        AkkaTestKit,
        Mockito)
    )
  )
}
