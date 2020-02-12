lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """play-scala-hello-world-tutorial""",
    organization := "com.example",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.1",
    libraryDependencies ++= Seq(
      guice,
      "org.scalatestplus.play" %% "scalatestplus-play"    % "5.0.0" % Test,
      "com.typesafe.play"      %% "play-slick"            % "5.0.0",
      "com.typesafe.play"      %% "play-slick-evolutions" % "5.0.0",
      "com.typesafe.slick"     %% "slick-codegen"         % "3.3.2",
      "mysql"                   % "mysql-connector-java"  % "8.0.19",

    ),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"
    )
  )
