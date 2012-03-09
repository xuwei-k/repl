package com.github.xuwei_k.repl

import sbt._

object Core{

  def generate(objName:String,code:Seq[String],options:Seq[String]):String = {
    "object `" + objName + "` extends scala.tools.partest.ReplTest{def code={\"\"\"" +
      code.mkString("\n") + "\"\"\"}" + """
      |  override def extraSettings = "%s"
      |}""".stripMargin.format(options.mkString(" "))
  }

  def generate(input:File,options:Seq[String],path:Seq[File]):String =
    generate(
      input.getName.replace(".scala",""),
      IO.readLines(input),
      options :+ path.mkString("-cp ",":","") // TODO windows ?
    )

}

