package com.github.xuwei_k.repl

import sbt._
import Keys._

object Plugin extends sbt.Plugin{
  
  object ReplKeys{
    val replSourceDirectory = SettingKey[File]("repl-source-directory")    
    val replSourceFiles = TaskKey[Seq[File]]("repl-source-files")    
  }

  import ReplKeys._

  lazy val replSettings: Seq[Project.Setting[_]] = Seq(
    (replSourceDirectory in Compile) <<= (sourceDirectory in Compile){_ / "repl"},
    (replSourceFiles in Compile) <<= (replSourceDirectory in Compile)map{ _ ** "*.scala" get},
    watchSources in GlobalScope <++= replSourceFiles in Compile,
    fork := true,
    libraryDependencies <+= (scalaVersion){
      "org.scala-lang" % "scala-partest" % _
    },
    (sourceGenerators in Compile) <+= (
      replSourceFiles in Compile,sourceManaged in Compile,scalacOptions in Compile
    ).map{ (in,out,opt) =>
      in.map{ f =>
        val src = {
          "object `" + f.name.replace(".scala","") + "` extends scala.tools.partest.ReplTest{def code={\"\"\"" +
          IO.readLines(f).mkString("\n") + "\"\"\"}" + """
          |  override def extraSettings = "%s"
          |}""".stripMargin.format(opt.mkString(" "))
        }
        val file = out / f.name
        IO.write(file,src)
        file
      }.toSeq
    }
  )
}

