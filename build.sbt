organization := "ch.epfl.scala"

name := "versions"

version := "0.2.0"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "fastparse" % "1.0.0",
  "org.scalatest" %% "scalatest" % "3.0.4" % Test
)

description := "Library to parse and order versions"

// publishing: 
commands += Command.command("ci-release") { s =>
  "clean" ::
    "versions/publishSigned" ::
    "sonatypeReleaseAll" ::
    s
}

publishTo := Some(
  if (isSnapshot.value) Opts.resolver.sonatypeSnapshots
  else Opts.resolver.sonatypeStaging
)

sonatypeProfileName := "ch.epfl.scala"

publishMavenStyle := true

licenses += ("BSD", url("http://opensource.org/licenses/BSD-3-Clause"))

homepage := Some(url("https://github.com/scalacenter/versions"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/scalacenter/versions"),
    "scm:git@github.com:scalacenter/versions.git"
  )
)

developers := List(
  Developer(
    id = "MasseGuillaume",
    name = "Guillaume Massé",
    email = "masgui@gmail.com",
    url = url("https://github.com/MasseGuillaume/")
  )
)

credentials ++= (for {
  username <- sys.env.get("SONATYPE_USERNAME")
  password <- sys.env.get("SONATYPE_PASSWORD")
} yield
  Credentials(
    "Sonatype Nexus Repository Manager",
    "oss.sonatype.org",
    username,
    password)
).toSeq

PgpKeys.pgpPassphrase in Global := sys.env.get("PGP_PASSPHRASE").map(_.toCharArray())
