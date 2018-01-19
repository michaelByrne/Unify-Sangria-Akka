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

Revolver.settings
enablePlugins(JavaAppPackaging)


fork in run := true