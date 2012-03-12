import sbt._
import Keys._
import sbt.ScriptedPlugin._

object build extends Build{

  lazy val buildSettings = {
    Defaults.defaultSettings ++ Seq(
      organization := "com.github.xuwei-k",
      version := "0.1-SNAPSHOT",
      scalacOptions := Seq("-deprecation", "-unchecked")
    )
  }

  lazy val repl = Project(
    "root",
    file("."),
    settings = buildSettings ++ Seq(
    )
  )aggregate(core,plugin,cs)

  lazy val core = Project(
    "core",
    file("core"),
    settings = buildSettings ++ Seq(
      libraryDependencies <++= (sbtDependency, sbtVersion) { (sd, sv) =>
        Seq(sd % "compile","org.scala-tools.sbt" %% "scripted-sbt" % sv)
      }
    )
  )

  lazy val plugin = Project(
    "repl-plugin",
    file("plugin"),
    settings = buildSettings ++ scriptedSettings ++ Seq(
      scriptedBufferLog := false,
      sbtPlugin := true
    )
  )dependsOn(core)

  lazy val cs = Project(
    "repl",
    file("cs"),
    settings = buildSettings ++ conscript.Harness.conscriptSettings ++ Seq(
      libraryDependencies <++= (sbtVersion){ v =>
        Seq(
          "org.scala-tools.sbt" %% "api" % v,
          "org.scala-tools.sbt" %  "launcher" % v,
          "org.scala-tools.sbt" %  "interface" % v % "compile"
        )
      }
    )
  )dependsOn(core)

}

