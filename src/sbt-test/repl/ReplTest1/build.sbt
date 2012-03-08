import com.github.xuwei_k.repl.Plugin._

seq(replSettings: _*)

scalacOptions += "-Xmacros"

scalaVersion := "2.10.0-M2"

libraryDependencies ++= Seq(
  "org.scalaz" % "scalaz-core_2.9.1" % "6.0.4"
)

