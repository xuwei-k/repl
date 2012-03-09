package com.github.xuwei_k.repl

import sbt._
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
    IO.withTemporaryFile("repl",".scala"){ file =>
      IO.write(file,str + "\n\n" + f.getName.replace(".scala","") + " main null" )
      val cmd = "scala " + file.toString
      println(cmd)
      cmd.lines_! foreach println
    }
  }

  def main(args: Array[String]) {
    System.exit(run(args))
  }
}

case class Exit(val code: Int) extends xsbti.Exit

