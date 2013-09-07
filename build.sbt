sbtPlugin := true

name := "repl"

organization := "com.github.xuwei-k"

version := "0.1-SNAPSHOT"

scalacOptions := Seq("-deprecation", "-unchecked")

ScriptedPlugin.scriptedSettings

scriptedBufferLog := false

scriptedLaunchOpts ++= sys.process.javaVmArguments.filter(
  a => Seq("-Xmx","-Xms","-XX").exists(a.startsWith)
)

publishTo := sys.env.get("MAVEN_DIRECTORY").map{ dir =>
  Resolver.file("gh-pages",file(dir))(Patterns(true, Resolver.mavenStyleBasePattern))
}
