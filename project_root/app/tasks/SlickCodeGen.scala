package com.example

import com.typesafe.config.ConfigFactory
import slick.codegen.SourceCodeGenerator

object SlickCodeGen extends App {
  val config      = ConfigFactory.load()
  val defaultPath = "slick.dbs.default"

  // 末尾の$を削除
  val profile   = config.getString(s"$defaultPath.profile").dropRight(1)
  val driver    = config.getString(s"$defaultPath.db.driver")
  val url       = config.getString(s"$defaultPath.db.url")
  val user      = config.getString(s"$defaultPath.db.user")
  val password  = config.getString(s"$defaultPath.db.password")

  val outputDir = config.getString("slick.codegen.outputDir")
  val pkg       = config.getString("application.package")

  SourceCodeGenerator.main(
    Array(profile, driver, url, outputDir, pkg, user, password)
  )
}


