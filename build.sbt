name := """portoman"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
  ws,
  jdbc,
  guice,
  evolutions,
  "com.typesafe.play" %% "anorm" % "2.5.3",
  "javax.xml.bind" % "jaxb-api" % "2.3.0",
  "org.xerial" % "sqlite-jdbc" % "3.36.0.3",
  "org.joda" % "joda-money" % "0.12",
  "joda-time" % "joda-time" % "2.10",
  "org.apache.commons" % "commons-csv" % "1.5",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
)


// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
