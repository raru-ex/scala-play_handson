lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name         := """play-scala-hello-world-tutorial""",
    organization := "com.example",
    version      := "1.0-SNAPSHOT",
    scalaVersion := "2.13.1",
    libraryDependencies ++= Seq(
      guice,
      evolutions,
      "org.scalatestplus.play" %% "scalatestplus-play"    % "5.0.0" % Test,
      "com.typesafe.play"      %% "play-slick"            % "5.0.0",
      "com.typesafe.play"      %% "play-slick-evolutions" % "5.0.0",
      "com.typesafe.slick"     %% "slick-codegen"         % "3.3.2",
      // https://scala-slick.org/doc/3.3.1/database.html
      "mysql"                   % "mysql-connector-java"  % "6.0.6",
      "com.typesafe"            % "config"                % "1.4.0"
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"
    )
  )


// add code generation task
lazy val slickCodeGen = taskKey[Unit]("execute Slick CodeGen")
slickCodeGen         := (runMain in Compile).toTask(" com.example.CustomSlickCodeGen").value
