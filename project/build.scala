import sbt._
import Keys._
import sbt.ScriptedPlugin._

object build extends Build{

  lazy val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "com.github.xuwei-k",
    version := "0.1-SNAPSHOT",
    scalacOptions := Seq("-deprecation", "-unchecked")
  )

  lazy val repl = Project(
    "repl",
    file("."),
    settings = buildSettings ++ Seq(
    )
  )aggregate(core,plugin,cs)

  lazy val core = Project(
    "core",
    file("core"),
    settings = buildSettings ++ Seq(
    )
  )

  lazy val plugin = Project(
    "repl-plugin",
    file("plugin"),
    settings = buildSettings ++ scriptedSettings ++ Seq(
      libraryDependencies <++= (sbtDependency, sbtVersion) { (sd, sv) =>
        Seq(sd,"org.scala-tools.sbt" %% "scripted-plugin" % sv)
      },
      scriptedBufferLog := false,
      sbtPlugin := true
    )
  )

  lazy val cs = Project(
    "cs",
    file("cs"),
    settings = buildSettings ++ conscript.Harness.conscriptSettings ++ Seq(
    )
  )

}

