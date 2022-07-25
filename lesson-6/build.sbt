
ThisBuild / scalaVersion := "2.13.8"
ThisBuild / libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.11" % Test
)

lazy val lecture = project

lazy val homework1 = project
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.3.3",
      "com.iheart" %% "ficus" % "1.5.0",
      "org.slf4j" % "slf4j-api" % "1.7.25",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
      "com.github.t3hnar" %% "scala-bcrypt" % "4.3.0",
    )
  )


lazy val homework2 = project
  .settings(
    libraryDependencies ++= Seq(
      "org.scalacheck" %% "scalacheck" % "1.14.0" % Test
    )
  )
