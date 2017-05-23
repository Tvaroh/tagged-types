import sbt._

name := "tagged-types"
organization in ThisBuild := "io.treev"

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
  "-Ywarn-unused-import"
)

licenses += ("BSD New", url("https://opensource.org/licenses/BSD-3-Clause"))
publishMavenStyle := true

bintrayOrganization := Some("treevio")
bintrayRepository := "maven"
bintrayPackage := "tagged-types"
bintrayVcsUrl := Some("git:git@github.com:Treev-io/tagged-types.git")
bintrayReleaseOnPublish in ThisBuild := false
bintrayPackageLabels := Seq("scala", "tagged", "tagged-types")
