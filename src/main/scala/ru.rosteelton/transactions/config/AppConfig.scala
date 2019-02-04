package ru.rosteelton.transactions.config

import scala.concurrent.duration.FiniteDuration

final case class AppConfig(httpServer: HttpServer,
                           cluster: AkkaSystemName,
                           postgres: PostgresConfig,
                           eventJournals: EventJournals)

final case class PostgresConfig(contactPoints: String, port: Int, database: String, username: String, password: String)

final case class HttpServer(host: String, port: Int)

final case class AkkaSystemName(systemName: String)

final case class EventJournals(accounts: EventJournalSettings)

final case class EventJournalSettings(tableName: String, pollingInterval: FiniteDuration)
