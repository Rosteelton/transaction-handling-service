package ru.rosteelton.transactions.wirings

import aecor.data.TagConsumer
import aecor.journal.postgres.{Offset, PostgresEventJournal, PostgresOffsetStore}
import aecor.runtime.KeyValueStore
import cats.implicits._
import cats.effect.{Async, ContextShift, Resource}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import ru.rosteelton.transactions.accounts.aggregate.DefaultUserAccountAggregate
import ru.rosteelton.transactions.accounts.serialization.UserAccountProtobufSerializer
import ru.rosteelton.transactions.accounts.view.AccountViewRepo
import ru.rosteelton.transactions.config.{EventJournals, PostgresConfig}

class PostgresWirings[F[_]: Async](transactor: Transactor[F], eventJournalConfig: EventJournals) {
  val accountViewRepo = AccountViewRepo(transactor)
  val offsetStore = PostgresOffsetStore("consumer_offset")
  val offsetKeyValueStore: KeyValueStore[F, TagConsumer, Offset] =
    offsetStore.mapK(transactor.trans)

  val accountsJournal = PostgresEventJournal(
    transactor,
    eventJournalConfig.accounts.tableName,
    DefaultUserAccountAggregate.tagging,
    UserAccountProtobufSerializer
  )
}

object PostgresWirings {
  def createTransactor[F[_]: Async: ContextShift](config: PostgresConfig): Resource[F, HikariTransactor[F]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[F](32)
      te <- ExecutionContexts.cachedThreadPool[F]
      tr <- HikariTransactor.newHikariTransactor[F](
             "org.postgresql.Driver",
             s"jdbc:postgresql://${config.contactPoints}:${config.port}/${config.database}",
             config.username,
             config.password,
             ce,
             te
           )
      _ <- Resource.liftF(tr.configure(tr => Async[F].delay(tr.setAutoCommit(false))))
    } yield tr

  def apply[F[_]: Async](transactor: Transactor[F], eventJournalConfig: EventJournals): F[PostgresWirings[F]] = {
    val wirings = new PostgresWirings(transactor, eventJournalConfig)
    for {
      _ <- wirings.accountsJournal.createTable
      _ <- transactor.trans.apply(wirings.offsetStore.createTable)
      _ <- wirings.accountViewRepo.createTable
    } yield wirings
  }
}
