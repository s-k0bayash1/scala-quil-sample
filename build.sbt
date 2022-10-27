import Dependencies._
import com.typesafe.config.ConfigFactory

ThisBuild / scalaVersion := "2.13.8"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"

lazy val slickGen = taskKey[Unit]("")

lazy val codegen = (project in file("codegen"))
  .settings(
    libraryDependencies ++= Seq(
      "io.getquill" %% "quill-codegen-jdbc" % "4.4.1",
      "org.postgresql" % "postgresql" % "42.5.0"
    ),
    javaOptions += "src/main/resources/application.conf"
  )

lazy val root = (project in file("."))
  .settings(
    name := "quill-sample",
    libraryDependencies ++= Seq(
      "io.getquill" %% "quill-jdbc" % "4.4.1",
      "org.postgresql" % "postgresql" % "42.5.0"
    )
  )
  .dependsOn(codegen)
