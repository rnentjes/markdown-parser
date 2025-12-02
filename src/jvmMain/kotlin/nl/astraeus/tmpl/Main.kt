package nl.astraeus.tmpl

import com.zaxxer.hikari.HikariConfig
import nl.astraeus.logger.Logger
import nl.astraeus.tmpl.db.Database

val log = Logger()

val REPO_NAME = "dummy so the gitea template compiles, please remove"

fun main() {
  Thread.currentThread().uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { t, e ->
    log.warn(e) {
      e.message
    }
  }

  Runtime.getRuntime().addShutdownHook(
    object : Thread() {
      override fun run() {
        Database.vacuumDatabase()
        Database.closeDatabase()
      }
    }
  )

  Class.forName("nl.astraeus.jdbc.Driver")
  Database.initialize(HikariConfig().apply {
    driverClassName = "nl.astraeus.jdbc.Driver"
    jdbcUrl = "jdbc:stat:webServerPort=6001:jdbc:sqlite:data/markdown-parser.db"
    username = "sa"
    password = ""
    maximumPoolSize = 25
    isAutoCommit = false

    validate()
  })

}
