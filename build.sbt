name := "geocoder"

organization := "com.koddi"

version := "1.0.0"

scalaVersion := "2.11.7"

scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")

crossScalaVersions := Seq("2.10.4", "2.11.7")

libraryDependencies ++= scalaxml(scalaVersion.value) ++ scalatest(scalaVersion.value)

def scalaxml(scalaVersion: String) = {
    val lib =
    if (scalaVersion startsWith "2.11") {
        "org.scala-lang.modules" %% "scala-xml" % "1.0.6"
    } else {
        null
    }

    if (null == lib) {
        Seq.empty[sbt.ModuleID]
    } else {
        Seq(lib)
    }
}

def scalatest(scalaVersion: String) = {
    Seq((scalaVersion match {
        case "2.10.4" => "org.scalatest" %% "scalatest" % "2.1.3"
        case _        => "org.scalatest" %% "scalatest" % "3.0.1"
    }) % "test")
}

