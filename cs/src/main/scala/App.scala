package com.github.xuwei_k.repl

import sbt._
import Keys._
import sbt.test.ScriptedTests
import scala.util.control.Exception.allCatch

final class App extends xsbti.AppMain {
  def run(config: xsbti.AppConfiguration) = {
    Exit(App.run(config))
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

  def run(config: xsbti.AppConfiguration) = {
    config.arguments match{
      case null => System.err.println("args is Empty") ; -1
      case Array(FileExtractor(f),opt @ _*) => run0(config,f,opt) ; 0
      case _ => System.err.println("bad options: " + config.arguments.mkString(" ")); -1
    }
  }

  val REPL = "repl"

  // https://github.com/harrah/xsbt/blob/v0.11.2/main/Main.scala#L36
  def run0(conf:xsbti.AppConfiguration,f:File,option:Seq[String] = Nil){
    import BuiltinCommands.initialAttributes
    val commands = REPL +: conf.arguments.map(_.trim)
    val state = State( conf,ReplCommands, Set.empty, None, commands, State.newHistory, initialAttributes, State.KeepLastLog)
    val loggedState = state.put(globalLogging,GlobalLogging(LogManager.defaultScreen,ConsoleLogger(),GlobalLogBacking(MainLoop.newBackingFile(), None)))
    val ret = MainLoop.runLoggedLoop(loggedState,GlobalLogBacking(MainLoop.newBackingFile(), None))
    println(ret)
    ret
  }

  def ReplCommands = {
    import BuiltinCommands._
    Seq(ignore, exit, replCommand, act, nop)
  }

  def header(version:String) =
    """
      |/***
      |
      |libraryDependencies += "org.scala-lang" % "scala-partest" % "2.9.1"
      |
      |*/
      |""".stripMargin

  def createSource(f:File,opt:Seq[String],version:String):String = {
    header(version) ++ "\n" ++Core.generate(f,opt,Nil) ++ "\n\n" ++ f.getName ++ ".eval.foreach(println)\n\n"
  }

  import EvaluateConfigurations.{evaluateConfiguration => evaluate}
  // https://github.com/harrah/xsbt/blob/v0.11.2/main/Script.scala#L14
  lazy val replCommand = 
    Command.command(REPL) { state =>
      val Seq(scriptArg,opt @ _*) = state.remainingCommands
      println(state.remainingCommands)
      val originalFile = new File(scriptArg).getAbsoluteFile
      val hash = Hash.halve(Hash.toHex(Hash(originalFile.getAbsolutePath)))
      val base = new File(CommandSupport.bootDirectory(state), hash)
      IO.createDirectory(base)
      val script = (base / "script").getAbsoluteFile
      val str = createSource(originalFile,opt,"2.9.1")
      println(str)
      IO.write(script,str)

      val (eval, structure) = Load.defaultLoad(state, base, state.log)
      val session = Load.initialSession(structure, eval)
      val extracted = Project.extract(session, structure)
              import extracted._

      val embeddedSettings = Script.blocks(script).flatMap { block =>
              evaluate(eval(), script.getPath, block.lines, currentUnit.imports, block.offset+1)(currentLoader)
      }
      val scriptAsSource = sources in Compile := script :: Nil
      val asScript = scalacOptions ++= Seq("-Xscript", script.getName.stripSuffix(".scala"))
      val scriptSettings = Seq(asScript, scriptAsSource, logLevel in Global := Level.Debug, showSuccess in Global := false)
      val append = Load.transformSettings(Load.projectScope(currentRef), currentRef.build, rootProject, scriptSettings ++ embeddedSettings)
 
      val newStructure = Load.reapply(session.original ++ append, structure)
      val arguments = state.remainingCommands.drop(1)
      val newState = arguments.mkString("run ", " ", "") :: state.copy(remainingCommands = Nil)
      Project.setProject(session, newStructure, newState)

   }
}

case class Exit(val code: Int) extends xsbti.Exit

