/** ********* PROJECT INFO ******************/
name := "geocoder"

organization := "com.koddi"

version := "1.2-SNAPSHOT"

scalaVersion := "2.13.0"

scalacOptions ++= Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")

scalafmtOnCompile := true

/** ********* PROJECT DEPENDENCIES *****************/
libraryDependencies ++= Seq(
    "org.scala-lang.modules" %% "scala-xml" % "1.2.0",
    "org.scalatest"          %% "scalatest" % "3.0.8" % Test
)

/** ********* PROJECT RESOLVERS *****************/
resolvers ++= Seq(
    Opts.resolver.sonatypeReleases,
    Opts.resolver.sonatypeSnapshots
)

/** ********* COMMANDS ALIASES ******************/
addCommandAlias("t", "test")
addCommandAlias("to", "testOnly")
addCommandAlias("tq", "testQuick")
addCommandAlias("tsf", "testShowFailed")

addCommandAlias("c", "compile")
addCommandAlias("tc", "test:compile")

addCommandAlias("f", "scalafmt") // Format production files according to ScalaFmt
addCommandAlias("fc", "scalafmtCheck") // Check if production files are formatted according to ScalaFmt
addCommandAlias("tf", "test:scalafmt") // Format test files according to ScalaFmt
addCommandAlias("tfc", "test:scalafmtCheck") // Check if test files are formatted according to ScalaFmt
addCommandAlias("fmt", ";f;tf") // Format all files according to ScalaFmt

// All the needed tasks before pushing to the repository (compile, compile test, format check in prod and test)
addCommandAlias("prep", ";tc;fc;tfc")

/** ********* PUBLISH SETTINGS ******************/
publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ =>
  false
}

licenses := Seq("MIT-style" -> url("http://www.opensource.org/licenses/mit-license.php"))

homepage := Some(url("https://koddidev.github.io/geocoder/"))

sonatypeProfileName := "com.koddi"

scmInfo := Some(
    ScmInfo(url("https://github.com/KoddiDev/geocoder"), "scm:git@github.com:KoddiDev/geocoder.git")
)

developers := List(
    Developer(
        id = "geocoder",
        name = "Koddi Developers",
        email = "dev@koddi.com",
        url = url("http://www.koddi.com")
    )
)

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
