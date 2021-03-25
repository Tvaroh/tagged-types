import sbt._
import sbtcrossproject.CrossPlugin.autoImport.crossProject
import xerial.sbt.Sonatype._
import ReleaseTransformations._

name := "tagged-types-root"
ThisBuild / organization := "io.treev"
ThisBuild / scalaVersion  := "2.13.4"
ThisBuild / crossScalaVersions := Seq(scalaVersion.value, "2.12.13")
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
skip in publish := true

val publishSettings = Seq(
  sonatypeProfileName := "io.treev",
  publishMavenStyle := true,
  publishTo := sonatypePublishToBundle.value,
  sonatypeProjectHosting := Some(GitHubHosting("Tvaroh", "tagged-types", "Alexander Semenov", "tvaroh@icloud.com")),

  releaseCrossBuild := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  releaseProcess :=
    Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      releaseStepCommandAndRemaining("+publishSigned"),
      releaseStepCommand("sonatypeBundleRelease"),
      setNextVersion,
      commitNextVersion,
      pushChanges
    )
)

lazy val cross =
  crossProject(JSPlatform, JVMPlatform)
    .in(file("."))
    .settings(publishSettings)
    .settings(
      name := "tagged-types",
      description := "Zero-dependency boilerplate-free tagged types for Scala",

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

      libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.6" % Test
    )

lazy val jvm = cross.jvm
lazy val js = cross.js
