import sbt._
import sbtcrossproject.CrossPlugin.autoImport.crossProject
import xerial.sbt.Sonatype._

name := "tagged-types-root"
scalaVersion in ThisBuild := "2.13.1"
crossScalaVersions in ThisBuild := Seq(scalaVersion.value, "2.12.10")
scalacOptions in ThisBuild ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-Xfatal-warnings",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Ywarn-unused:imports"
)
releaseCrossBuild := true
ThisBuild / publishTo := sonatypePublishToBundle.value
ThisBuild / sonatypeProfileName := "io.treev"

lazy val cross =
  crossProject(JSPlatform, JVMPlatform)
    .in(file("."))
    .settings(
      name := "tagged-types",
      organization := "io.treev",
      description := "Zero-dependency boilerplate-free tagged types for Scala",

      releasePublishArtifactsAction := PgpKeys.publishSigned.value,

      publishMavenStyle := true,
      sonatypeProjectHosting := Some(GitHubHosting("Tvaroh", "tagged-types", "Alexander Semenov", "tvaroh@icloud.com")),

      licenses += ("BSD New", url("https://opensource.org/licenses/BSD-3-Clause")),
      homepage := Some(url("https://github.com/Tvaroh/tagged-types")),
      scmInfo := Some(
        ScmInfo(
          url("https://github.com/Tvaroh/tagged-types"),
          "scm:git:git@github.com:Tvaroh/tagged-types.git",
          Some("scm:git:ssh://github.com:Tvaroh/tagged-types.git")
        )
      ),
      developers += Developer("Tvaroh", "Alexander Semenov", "bohtvaroh@gmail.com", url("https://github.com/Tvaroh")),

      libraryDependencies += "org.scalatest" %%% "scalatest" % "3.1.0" % Test
    )

lazy val jvm = cross.jvm
lazy val js = cross.js
