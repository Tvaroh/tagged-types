import sbt._

organization in ThisBuild := "io.treev"
description in ThisBuild := "Zero-dependency boilerplate-free tagged types for Scala"

scalaVersion in ThisBuild := "2.12.2"
crossScalaVersions in ThisBuild := Seq(scalaVersion.value, "2.11.11")
scalacOptions in ThisBuild ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-Xfatal-warnings",
  "-Yno-adapted-args",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfuture",
  "-Ywarn-unused-import"
)

lazy val root =
  project.in(file("."))
    .aggregate(jvm, js)
    .settings(
      publish := {},
      publishLocal := {}
    )

lazy val cross =
  crossProject.in(file("."))
    .settings(
      name := "tagged-types",
      libraryDependencies += "org.scalatest" %%% "scalatest" % "3.0.3" % Test
    )

lazy val jvm = cross.jvm
lazy val js = cross.js

licenses += ("BSD New", url("https://opensource.org/licenses/BSD-3-Clause"))
scmInfo := Some(
  ScmInfo(
    url("https://github.com/Treev-io/tagged-types"),
    "scm:git:git@github.com:Treev-io/tagged-types.git",
    Some("scm:git:ssh://github.com:Treev-io/tagged-types.git")
  )
)
developers += Developer("Tvaroh", "Alexander Semenov", "bohtvaroh@gmail.com", url("https://github.com/Tvaroh"))
homepage := Some(url("https://github.com/Treev-io/tagged-types"))

publishMavenStyle := true
publishTo := Some(if (isSnapshot.value) Opts.resolver.sonatypeSnapshots else Opts.resolver.sonatypeStaging)
pomIncludeRepository := (_ => false)

releaseCrossBuild := true
releasePublishArtifactsAction := PgpKeys.publishSigned.value
