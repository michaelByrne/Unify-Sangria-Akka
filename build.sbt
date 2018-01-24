name := """unify-id"""
version := "0.1.0-SNAPSHOT"

description := "akka-http and sangria server for unify id demo"

scalaVersion := "2.12.4"
scalacOptions ++= Seq("-deprecation", "-feature")

libraryDependencies ++= Seq(
  "org.sangria-graphql" %% "sangria" % "1.3.0",
  "org.sangria-graphql" %% "sangria-spray-json" % "1.0.0",
  "com.typesafe.akka" %% "akka-http" % "10.0.10",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.10",

  "org.scalatest" %% "scalatest" % "3.0.4" % Test
)

libraryDependencies += "org.sangria-graphql" %% "sangria-play-json" % "1.0.4"

libraryDependencies += "ch.megard" %% "akka-http-cors" % "0.2.2"



herokuProcessTypes in Compile := Map(
  "web" -> "target/universal/stage/bin/unify-id -Dhttp.port=$PORT",
)

Revolver.settings
enablePlugins(JavaAppPackaging)


fork in run := true