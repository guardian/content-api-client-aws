import sbtrelease.ReleaseStateTransformations._
import sbtversionpolicy.withsbtrelease.ReleaseVersion


name:= "content-api-client-aws"
description:= "AWS helper functionality for using Guardian's Content API scala client"
organization := "com.gu"
licenses := Seq(License.Apache2)
scalaVersion := "2.13.14"
crossScalaVersions := Seq(scalaVersion.value, "2.12.19")
scalacOptions ++= Seq("-feature", "-deprecation", "-unchecked", "-Xfatal-warnings", "-release:8")

Test / testOptions +=
  Tests.Argument(TestFrameworks.ScalaTest, "-u", s"test-results/scala-${scalaVersion.value}", "-o")

releaseCrossBuild := true
releaseVersion := ReleaseVersion.fromAggregatedAssessedCompatibilityWithLatestRelease().value
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  setNextVersion,
  commitNextVersion
)

resolvers ++= Resolver.sonatypeOssRepos("releases")
libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk-core" % "1.12.770",
  "org.scalatest" %% "scalatest" % "3.0.9" % Test,
  "org.scala-lang.modules" %% "scala-collection-compat" % "2.12.0"
)
