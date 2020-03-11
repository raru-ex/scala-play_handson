name := """play-handson"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  guice,
  evolutions,
  "org.scalatestplus.play" %% "scalatestplus-play"    % "5.0.0" % Test,
  "com.typesafe.play"      %% "play-slick"            % "5.0.0",
  "com.typesafe.play"      %% "play-slick-evolutions" % "5.0.0",
  // play-slickの5.0.0ではslick 3.3.2を利用しているため、codegenも同様に3.3.2を指定しています。
  // https://github.com/playframework/play-slick#all-releases
  "com.typesafe.slick"     %% "slick-codegen"         % "3.3.2",
  "mysql"                   % "mysql-connector-java"  % "6.0.6",
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
