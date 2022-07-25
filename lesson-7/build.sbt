
ThisBuild / scalaVersion := "2.13.8"
ThisBuild / libraryDependencies ++= Seq(
  "io.monix" %% "monix" % "3.4.0",
  "org.scalatest" %% "scalatest" % "3.2.4" % Test
)

lazy val homework = project