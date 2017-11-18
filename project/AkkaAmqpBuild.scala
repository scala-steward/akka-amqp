import sbt._
import Keys._
import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys

object AkkaAmqpBuild extends Build {
  import dependencies._

  lazy val formatSettings = SbtScalariform.scalariformSettings ++ Seq(
    ScalariformKeys.preferences in Compile := formattingPreferences,
    ScalariformKeys.preferences in Test    := formattingPreferences
  )

  def formattingPreferences = {
    import scalariform.formatter.preferences._
    FormattingPreferences()
    .setPreference(RewriteArrowSymbols, true)
    .setPreference(AlignParameters, true)
    .setPreference(AlignSingleLineCaseStatements, true)
  }

  lazy val standardSettings = Project.defaultSettings ++ formatSettings ++ Seq(
    resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/",
    resolvers += "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/releases/",
    organization := "com.github.cessationoftime",
    version      := "2.5-SNAPSHOT",
    scalaVersion := "2.12.4"
  )

  lazy val root = Project(
    id        = "akka-amqp",
    base      = file("."),
    settings = standardSettings ++ Seq(
      libraryDependencies ++= Seq(
        AmqpClient,
     		AkkaAgent)
    )
  )
}
