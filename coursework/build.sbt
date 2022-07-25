ThisBuild / scalaVersion := "2.13.8"

lazy val circeVersion = "0.14.1"
lazy val tapirVersion = "0.20.1"
val DoobieVersion = "1.0.0-RC1"
val NewTypeVersion = "0.4.4"

ThisBuild / libraryDependencies ++= Seq(
  "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server" % tapirVersion,
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
  "de.heikoseeberger" %% "akka-http-circe" % "1.39.2",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "com.beachape" %% "enumeratum" % "1.7.0",
  "com.beachape" %% "enumeratum-circe" % "1.7.0",
  "com.h2database"  %   "h2"                % "2.1.210",
  "org.tpolecat"    %%  "doobie-core"       % DoobieVersion,
  "org.tpolecat"    %%  "doobie-h2"         % DoobieVersion,
  "org.tpolecat"    %%  "doobie-hikari"     % DoobieVersion,
  "org.tpolecat"    %%  "doobie-specs2"     % DoobieVersion % "test",
  "org.tpolecat"    %%  "doobie-scalatest"  % DoobieVersion % "test",
  "org.typelevel"   %%  "cats-core"         % "2.7.0",
  "io.estatico"     %%  "newtype"           % NewTypeVersion
)

lazy val coursework = project
