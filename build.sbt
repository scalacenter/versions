lazy val commonSettings = Seq(
  version in ThisBuild := "0.1",
  organization in ThisBuild := "ch.epfl.scala"
)

lazy val bintraySettings = Seq(
  bintrayPackageLabels := Seq("semantic-versioning", "scala"),
  bintrayRepository := "maven",
  bintrayOrganization := Some("auroredea")
)

lazy val versions = project
  .in(file("."))
  .settings(
    commonSettings,
    scalaVersion := "2.11.8",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "fastparse" % "0.4.2",
      "org.specs2" %% "specs2-core" % "3.8.6" % Test
    ),
    scalacOptions in Test ++= Seq("-Yrangepos"),
    name := "versions",
    description := "Library to parse and ordering version",
    licenses += ("BSD", url("http://opensource.org/licenses/BSD-3-Clause")),
    bintraySettings
  )

