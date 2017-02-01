lazy val versions = project
  .in(file("."))
  .settings(
    scalaVersion := "2.11.8",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "fastparse" % "0.4.2",
      "org.specs2" %% "specs2-core" % "3.8.6" % Test
    ),
    scalacOptions in Test ++= Seq("-Yrangepos")
  )
