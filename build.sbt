name := "geocoder"

version := "1.0.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
    "org.scalaj" %% "scalaj-http" % "2.3.0",
    "com.owlike" % "genson" % "1.4",
    "com.owlike" % "genson-scala_2.11" % "1.4",
    "org.scalactic" %% "scalactic" % "3.0.1" % "test",
    "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

