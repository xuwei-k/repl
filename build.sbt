sbtPlugin := true

name := "repl"

organization := "com.github.xuwei-k"

version := "0.1-SNAPSHOT"

scalacOptions := Seq("-deprecation", "-unchecked")

seq(ScriptedPlugin.scriptedSettings: _*)

scriptedBufferLog := false

