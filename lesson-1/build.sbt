ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "Tinkoff_Scala"
  )

resolvers += "test" at "https://gitlab.com/api/v4/projects/33751126/packages/maven"

libraryDependencies ++= Seq(
  "ru.tinkoff" %% "scala-course-john" % "0.2",
  "ru.tinkoff" %% "scala-course-tom" % "0.2"
)

dependencyOverrides += "ru.tinkoff" %% "scala-course-clerk" % "0.1"

conflictManager := ConflictManager.strict
