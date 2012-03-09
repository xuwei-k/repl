package com.github.xuwei_k.repl

import sbt._
import sbt.test.ScriptedTests
import scala.util.control.Exception.allCatch

class App extends xsbti.AppMain {
  def run(config: xsbti.AppConfiguration) = {
    Exit(App.run(config.arguments))
  }
}

object App {
  object FileExtractor{
    def unapply(str:String):Option[File] = {
      val f = file(str)
      if( f.exists && f.isFile ) Some(f)
      else None
    }
  }

  def run(args: Array[String]): Int = {
    args match{
      case null => System.err.println("args is Empty") ; -1
      case Array(FileExtractor(f),opt @ _*) => run0(f,opt) ; 0
      case _ => System.err.println("bad options: " + args.mkString(" ")); -1
    }
  }

  // TODO should not use other process !
  def run0(f:File,option:Seq[String] = Nil){
    val str = Core.generate(f,option,Nil)
    println(str)
    IO.withTemporaryDirectory{ dir =>
      IO.write(dir/"build.sbt","import com.github.xuwei_k.repl._\n\nseq(replSettings: _*)")
      IO.write(dir/"src"/"main"/"repl",str)
      IO.write(dir/"test","> run\n")
      IO.write(dir/"project"/"plugins.sbt",
        """addCompilerPlugin("com.github.xuwei_k" % "repl-plugin" % "0.1-SNAPSHOT" )""")
      val jar = xsbt.boot.Launch.getClass.getProtectionDomain.getCodeSource.getLocation.getFile
      println(dir,jar)
      ScriptedTests.run(dir,false,"0.11.2","2.9.1","2.9.1",Array("foo/bar"),file(jar))
      Thread.sleep(20000)
    }
  }

  def main(args: Array[String]) {
    System.exit(run(args))
  }
}

case class Exit(val code: Int) extends xsbti.Exit

