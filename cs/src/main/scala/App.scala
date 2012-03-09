package com.github.xuwei_k.repl

class App extends xsbti.AppMain {
  def run(config: xsbti.AppConfiguration) = {
    Exit(App.run(config.arguments))
  }
}

object App {

  def run(args: Array[String]): Int = {
    println("Hello World: " + args.mkString(" "))
    0
  }

  def main(args: Array[String]) {
    System.exit(run(args))
  }
}

case class Exit(val code: Int) extends xsbti.Exit

