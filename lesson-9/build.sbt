scalaVersion := "2.13.8"

val DoobieVersion = "1.0.0-RC1"
val NewTypeVersion = "0.4.4"

libraryDependencies ++= Seq(
  "com.h2database"  %   "h2"                % "2.1.210",
  "org.tpolecat"    %%  "doobie-core"       % DoobieVersion,
  "org.tpolecat"    %%  "doobie-h2"         % DoobieVersion,
  "org.tpolecat"    %%  "doobie-hikari"     % DoobieVersion,
  "org.tpolecat"    %%  "doobie-specs2"     % DoobieVersion % "test",
  "org.tpolecat"    %%  "doobie-scalatest"  % DoobieVersion % "test",
  "org.typelevel"   %%  "cats-core"         % "2.7.0",
  "io.estatico"     %%  "newtype"           % NewTypeVersion
)
