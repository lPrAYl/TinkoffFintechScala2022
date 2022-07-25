ThisBuild / scalaVersion := "2.13.8"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.11" % Test

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value

lazy val lecture = project
lazy val homework = (project in file("."))
