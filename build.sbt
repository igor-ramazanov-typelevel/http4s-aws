import org.typelevel.sbt.gha.WorkflowStep.Run
import org.typelevel.sbt.gha.WorkflowStep.Sbt

import com.typesafe.tools.mima.core._

ThisBuild / githubOwner := "igor-ramazanov-typelevel"
ThisBuild / githubRepository := "http4s-aws"

ThisBuild / githubWorkflowPublishPreamble := List.empty
ThisBuild / githubWorkflowUseSbtThinClient := true
ThisBuild / githubWorkflowPublish := List(
  Run(
    commands = List("echo \"$PGP_SECRET\" | gpg --import"),
    id = None,
    name = Some("Import PGP key"),
    env = Map("PGP_SECRET" -> "${{ secrets.PGP_SECRET }}"),
    params = Map(),
    timeoutMinutes = None,
    workingDirectory = None
  ),
  Sbt(
    commands = List("+ publish"),
    id = None,
    name = Some("Publish"),
    cond = None,
    env = Map("GITHUB_TOKEN" -> "${{ secrets.GB_TOKEN }}"),
    params = Map.empty,
    timeoutMinutes = None,
    preamble = true
  )
)
ThisBuild / gpgWarnOnFailure := false

val awsRegionsVersion = "1.1.0-M1"
val caseInsensitiveVersion = "1.5.0"
val catsEffectVersion = "3.7-4972921"
val catsParseVersion = "1.1.0"
val catsVersion = "2.13.0"
val circeVersion = "0.14.13"
val fs2Version = "3.14.0-M1"
val http4sVersion = "0.23.31-M1"
val munitCatsEffectVersion = "2.2.0-M1"
val scala213Version = "2.13.16"
val scala3Version = "3.3.6"
val scalaCheckEffectMunitVersion = "2.1.0-M1"

inThisBuild(
  Seq(
    crossScalaVersions := Seq(scala213Version, scala3Version),
    developers := List(
      tlGitHubDev("janina9395", "Janina Komarova"),
      tlGitHubDev("jesperoman", "Jesper Öman"),
      tlGitHubDev("vlovgr", "Viktor Rudebeck")
    ),
    githubWorkflowJavaVersions := Seq(JavaSpec.temurin("17")),
    githubWorkflowTargetBranches := Seq("**"),
    licenses := Seq(License.Apache2),
    mimaBinaryIssueFilters ++= Seq(
      // format: off
      ProblemFilters.exclude[Problem]("com.magine.http4s.aws.internal.*"),
      /* TODO: Remove below filters for 7.0 release. */
      ProblemFilters.exclude[DirectMissingMethodProblem]("com.magine.http4s.aws.CredentialsProvider.securityTokenService"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("com.magine.http4s.aws.CredentialsProvider.securityTokenService"),
      ProblemFilters.exclude[DirectMissingMethodProblem]("com.magine.http4s.aws.AwsPresigning.presignRequest"),
      ProblemFilters.exclude[DirectMissingMethodProblem]("com.magine.http4s.aws.AwsPresigning.apply"),
      ProblemFilters.exclude[DirectMissingMethodProblem]("com.magine.http4s.aws.AwsSigning.signRequest"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("com.magine.http4s.aws.CredentialsProvider.credentialsFile"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("com.magine.http4s.aws.TokenCodeProvider.default")
      // format: on
    ),
    organization := "com.magine",
    organizationName := "Magine Pro",
    scalaVersion := scala3Version,
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    startYear := Some(2025),
    tlBaseVersion := "6.2",
    tlCiHeaderCheck := true,
    tlCiScalafixCheck := true,
    tlCiScalafmtCheck := true,
    tlFatalWarnings := false,
    tlJdkRelease := Some(8),
    tlUntaggedAreSnapshots := false,
    versionScheme := Some("early-semver")
  )
)

lazy val root = tlCrossRootProject
  .aggregate(core)

lazy val core = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .in(file("modules/core"))
  .settings(
    name := "http4s-aws",
    libraryDependencies ++= Seq(
      "co.fs2" %%% "fs2-core" % fs2Version,
      "com.magine" %%% "aws-regions" % awsRegionsVersion,
      "io.circe" %%% "circe-parser" % circeVersion,
      "org.http4s" %%% "http4s-circe" % http4sVersion,
      "org.http4s" %%% "http4s-client" % http4sVersion,
      "org.http4s" %%% "http4s-core" % http4sVersion,
      "org.typelevel" %%% "case-insensitive" % caseInsensitiveVersion,
      "org.typelevel" %%% "cats-core" % catsVersion,
      "org.typelevel" %%% "cats-effect-kernel" % catsEffectVersion,
      "org.typelevel" %%% "cats-effect-testkit" % catsEffectVersion % Test,
      "org.typelevel" %%% "cats-effect" % catsEffectVersion,
      "org.typelevel" %%% "cats-parse" % catsParseVersion,
      "org.typelevel" %%% "munit-cats-effect" % munitCatsEffectVersion % Test,
      "org.typelevel" %%% "scalacheck-effect-munit" % scalaCheckEffectMunitVersion % Test
    ),
    publishTo := githubPublishTo.value,
    publishConfiguration := publishConfiguration.value.withOverwrite(true),
    publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)
  )
  .jsSettings(
    tlVersionIntroduced := List("2.13", "3").map(_ -> "6.2.0").toMap,
    scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule))
  )
  .nativeSettings(
    test := {}
  )
