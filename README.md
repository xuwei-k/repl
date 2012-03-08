# sbt repl plugin

## How To Use

`project/project/build.scala`

```scala
import sbt._

object Plugins extends Build {
  lazy val root = Project("root", file(".")) dependsOn(
    uri("git://github.com/xuwei-k/repl.git")
  )
}
```

Then, add the following in your `build.sbt`:

```scala
import com.github.xuwei_k.repl.Plugin._

seq(replSettings: _*)
```

