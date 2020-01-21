import sbt._
import sbtcrossproject.CrossPlugin.autoImport.crossProject

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
publishTo in ThisBuild := Some {
  if (isSnapshot.value) Opts.resolver.sonatypeSnapshots else Opts.resolver.sonatypeStaging
}

lazy val cross =
  crossProject(JSPlatform, JVMPlatform)
    .in(file("."))
    .settings(
      name := "tagged-types",
      organization := "io.treev",
      description := "Zero-dependency boilerplate-free tagged types for Scala",

      releasePublishArtifactsAction := PgpKeys.publishSigned.value,

      publishMavenStyle := true,
      pomIncludeRepository := (_ => false),

      licenses += ("BSD New", url("https://opensource.org/licenses/BSD-3-Clause")),
      scmInfo := Some(
        ScmInfo(
          url("https://github.com/Tvaroh/tagged-types"),
          "scm:git:git@github.com:Tvaroh/tagged-types.git",
          Some("scm:git:ssh://github.com:Tvaroh/tagged-types.git")
        )
      ),
      developers += Developer("Tvaroh", "Alexander Semenov", "bohtvaroh@gmail.com", url("https://github.com/Tvaroh")),
      homepage := Some(url("https://github.com/Tvaroh/tagged-types")),

      libraryDependencies += "org.scalatest" %%% "scalatest" % "3.1.0" % Test
    )

lazy val jvm = cross.jvm
lazy val js = cross.js
