[![Build Status](https://travis-ci.org/scalacenter/versions.svg?branch=master)](https://travis-ci.org/scalacenter/versions)

#  Versions

```scala
libraryDependencies += "ch.epfl.scala" %% "versions" % "0.1.0"
```

```scala
import ch.epfl.scala.version._

val v1 = Version("1.0.0").get
val v2 = Version("2.0.0").get

v2 > v1 // true
```