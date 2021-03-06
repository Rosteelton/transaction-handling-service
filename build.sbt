lazy val aecorVersion = "0.18.0"
lazy val aecorPostgresVersion = "0.3.0"
lazy val mouseVersion = "0.19"
lazy val metaParadiseVersion = "3.0.0-M11"
lazy val http4sVersion = "0.20.0-M3"
lazy val doobieVersion = "0.6.0"
lazy val circeDerivationVersion = "0.10.0-M1"
lazy val circeVersion = "0.10.1"
lazy val log4CatsVersion = "0.2.0"
lazy val pureConfigVersion = "0.10.0"
lazy val akkaVersion = "2.5.18"
lazy val logbackVersion = "1.2.3"

lazy val pureConfig = Seq(
  "com.github.pureconfig" %% "pureconfig" % pureConfigVersion,
  "com.github.pureconfig" %% "pureconfig-http4s" % pureConfigVersion
)

lazy val aecor = Seq(
  "io.aecor" %% "core" % aecorVersion,
  "io.aecor" %% "schedule" % aecorVersion,
  "io.aecor" %% "akka-cluster-runtime" % aecorVersion,
  "io.aecor" %% "distributed-processing" % aecorVersion,
  "io.aecor" %% "boopickle-wire-protocol" % aecorVersion,
  "io.aecor" %% "aecor-postgres-journal" % aecorPostgresVersion,
  "io.aecor" %% "test-kit" % aecorVersion % Test
)

lazy val doobie = Seq(
  "org.tpolecat" %% "doobie-core" % doobieVersion,
  "org.tpolecat" %% "doobie-postgres" % doobieVersion,
  "org.tpolecat" %% "doobie-hikari" % doobieVersion,
)

lazy val http4s = Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
)

lazy val circe = Seq(
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-derivation" % circeDerivationVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-java8" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion
)

val logger = Seq(
  "io.chrisdavenport" %% "log4cats-core" % log4CatsVersion,
  "io.chrisdavenport" %% "log4cats-slf4j" % log4CatsVersion,
)

lazy val transactions =
  project
    .in(file("."))
    .settings(
      baseSettings,
      sources in(Compile, doc) := Nil,
      libraryDependencies ++=
        aecor ++ doobie ++ http4s ++ circe ++ logger ++ pureConfig ++
          Seq(
            compilerPlugin("org.spire-math" %% "kind-projector" % "0.9.9"),
            "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
            compilerPlugin("org.scalameta" % "paradise" % metaParadiseVersion cross CrossVersion.full),
            "org.typelevel" %% "mouse" % mouseVersion,
            "io.monix" %% "monix" % "3.0.0-RC2",
            "io.chrisdavenport" %% "cats-par" % "0.2.0",
            "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
            "ch.qos.logback" % "logback-classic" % logbackVersion
          )
    ).enablePlugins(DockerPlugin, JavaAppPackaging)

lazy val baseSettings = Seq(
  name := "transaction-handling-service",
  scalaVersion in ThisBuild := "2.12.7",
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"),
  ),
  scalacOptions ++= commonScalacOptions,
  cancelable in Global := true,
  PB.targets in Compile := Seq(
    scalapb.gen(flatPackage = true) -> (sourceManaged in Compile).value
  ),
  sources in(Compile, doc) := Nil,
  dockerExposedPorts := Seq(9000),
  dockerBaseImage := "java:8.161",
  mainClass in Compile := Some("ru.rosteelton.transactions.App"),
  scalacOptions in(Compile, console) ~= {
    _.filterNot(unusedWarnings.toSet + "-Ywarn-value-discard")
  },
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
) ++ unusedWarnings

lazy val unusedWarnings = Seq("-Ywarn-unused", "-Ywarn-unused-import")

