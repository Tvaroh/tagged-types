import sbt._
import sbtcrossproject.CrossPlugin.autoImport.crossProject
import xerial.sbt.Sonatype._

name := "tagged-types-root"
ThisBuild / scalaVersion  := "2.13.1"
ThisBuild / crossScalaVersions := Seq(scalaVersion.value, "2.12.10")
ThisBuild / scalacOptions ++= Seq(
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
sourcesInBase := false
sonatypeProfileName := "io.treev"
skip in publish := true
releaseCrossBuild := true

lazy val cross =
  crossProject(JSPlatform, JVMPlatform)
    .in(file("."))
    .settings(
      name := "tagged-types",
      organization := "io.treev",
      description := "Zero-dependency boilerplate-free tagged types for Scala",

      releasePublishArtifactsAction := PgpKeys.publishSigned.value,

      publishMavenStyle := true,
      publishTo := sonatypePublishToBundle.value,
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
