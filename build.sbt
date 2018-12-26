lazy val aecorVersion = "0.18.0"
lazy val aecorPostgresVersion = "0.3.0"
lazy val mouseVersion = "0.19"

lazy val aecor = Seq(
  "io.aecor" %% "core" % aecorVersion,
  "io.aecor" %% "schedule" % aecorVersion,
  "io.aecor" %% "akka-cluster-runtime" % aecorVersion,
  "io.aecor" %% "distributed-processing" % aecorVersion,
  "io.aecor" %% "boopickle-wire-protocol" % aecorVersion,
  "io.aecor" %% "aecor-postgres-journal" % aecorPostgresVersion,
  "io.aecor" %% "test-kit" % aecorVersion % Test
)

lazy val transactions =
  project
    .in(file("."))
    .settings(
      baseSettings,
      libraryDependencies ++=
        aecor ++
          Seq(
            compilerPlugin("org.spire-math" %% "kind-projector" % "0.9.9"),
            "org.typelevel" %% "mouse" % mouseVersion,
          )
    )

lazy val baseSettings = Seq(
  name := "transaction-handling-service",
  scalaVersion in ThisBuild := "2.12.8",
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"),
  ),
  scalacOptions ++= commonScalacOptions,
  cancelable in Global := true
)

lazy val commonScalacOptions = Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:experimental.macros",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfuture",
  "-Ypartial-unification",
  "-Ywarn-unused",
  "-Ywarn-unused-import"
)
