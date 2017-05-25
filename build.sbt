import sbt._

name := "tagged-types"
organization in ThisBuild := "io.treev"
description := "Zero-dependency boilerplate-free tagged types for Scala"

scalaVersion := "2.12.2"
crossScalaVersions := Seq(scalaVersion.value, "2.11.11")
scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfuture",
  "-Ywarn-unused-import",
  "-Yno-predef"
)

licenses += ("BSD New", url("https://opensource.org/licenses/BSD-3-Clause"))
scmInfo := Some(
  ScmInfo(
    url("https://github.com/Treev-io/tagged-types"),
    "scm:git:git@github.com:Treev-io/tagged-types.git",
    Some("scm:git:ssh://github.com:Treev-io/tagged-types.git")
  )
)
developers += Developer("Tvaroh", "Alexander Semenov", "bohtvaroh@gmail.com", url("https://github.com/Tvaroh"))
publishMavenStyle := true

bintrayOrganization := Some("treevio")
bintrayRepository := "maven"
bintrayPackage := "tagged-types"
bintrayVcsUrl := Some("git:git@github.com:Treev-io/tagged-types.git")
bintrayReleaseOnPublish in ThisBuild := false
bintrayPackageLabels := Seq("scala", "tagged", "tagged-types")

enablePlugins(TutPlugin)
scalacOptions in Tut --= Seq("-Xlint", "-Ywarn-dead-code", "-Ywarn-unused-import")
tutTargetDirectory := baseDirectory.value
