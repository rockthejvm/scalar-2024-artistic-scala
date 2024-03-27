ThisBuild / version := "1.0.1"
ThisBuild / scalaVersion := "3.3.0"
ThisBuild / scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature"
)

lazy val app = (project in file("app"))
  .settings(
    libraryDependencies ++= Seq(
      "io.frontroute" %%% "frontroute" % "0.18.1" // Brings in Laminar 16
    ),
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
    semanticdbEnabled := true,
    autoAPIMappings := true,
    scalaJSUseMainModuleInitializer := true,
    Compile / mainClass := Some("com.rockthejvm.Scalar2024")
  )
  .enablePlugins(ScalaJSPlugin)

lazy val root = (project in file("."))
  .settings(
    name := "scalar-2024"
  )
  .aggregate(app)
  .dependsOn(app)
