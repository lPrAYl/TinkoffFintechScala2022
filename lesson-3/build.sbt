ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.4"

lazy val root = (project in file("."))
  .settings(
    name := "lesson-3",
    scalacOptions ++= Seq("-Xfatal-warnings")
  )
