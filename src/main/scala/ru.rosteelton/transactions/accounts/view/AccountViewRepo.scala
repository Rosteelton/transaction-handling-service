package ru.rosteelton.transactions.accounts.view

import java.sql.Timestamp
import java.time.Instant
import cats.Monad
import cats.implicits._
import doobie.implicits._
import doobie.util.transactor.Transactor
import doobie.{Fragment, Meta, Update, Update0}
import io.circe.parser.parse
import io.circe.{Decoder, Encoder, Json}
import org.postgresql.util.PGobject
import ru.rosteelton.transactions.common.models.{AccountId, TransactionId, UserId}

final class AccountViewRepo[F[_]: Monad](transactor: Transactor[F], tableName: String = "account")
    extends AccountRepo[F] {

  implicit val jsonMeta: Meta[Json] =
    Meta.Advanced
      .other[PGobject]("json")
      .timap[Json](a => parse(a.getValue).leftMap[Json](e => throw e).merge)(a => {
      val o = new PGobject
      o.setType("json")
      o.setValue(a.noSpaces)
      o
    })

  implicit val transactionsMeta: Meta[Set[TransactionId]] = jsonMeta.timap(
    j => Decoder[Set[TransactionId]].decodeJson(j).right.get
  )(s => Encoder[Set[TransactionId]].apply(s))

  implicit val instantMeta: Meta[Instant] =
    Meta[Timestamp].timap(_.toInstant)(Timestamp.from)

  def saveAccountView(view: AccountView): F[Unit] =
    Update[AccountView](insertQuery).run(view).transact(transactor).void

  def getUserAccount(accountId: AccountId): F[Option[AccountView]] =
    getByIdQuery(accountId).option.transact(transactor)

  def getAccountsForUser(userId: UserId): F[List[AccountView]] =
    getAccountsByUserIdQuery(userId).to[List].transact(transactor)

  def createTable: F[Unit] =
    (createTableQuery >> createIndexQuery).transact(transactor).void

  private val insertQuery =
    s"""INSERT INTO $tableName
    (account_id, user_id, balance, processed_transactions, created_at, version)
    VALUES (?,?,?,?,?,?)
    ON CONFLICT (account_id)
    DO UPDATE SET
     user_id = EXCLUDED.user_id,
     balance = EXCLUDED.balance,
     processed_transactions = EXCLUDED.processed_transactions,
     created_at = EXCLUDED.created_at,
     version = EXCLUDED.version;"""

  private def getByIdQuery(accountId: AccountId) =
    (fr"SELECT * FROM " ++ Fragment.const(tableName) ++
      fr"WHERE account_id = $accountId;")
      .query[AccountView]

  private def getAccountsByUserIdQuery(userId: UserId) =
    (fr"SELECT * FROM " ++ Fragment.const(tableName) ++
      fr"WHERE user_id = $userId;")
      .query[AccountView]

  private val createTableQuery = (fr"""
    CREATE TABLE IF NOT EXISTS """ ++ Fragment.const(tableName) ++
    fr""" (
    account_id              text            NOT NULL PRIMARY KEY,
    user_id                 text            NOT NULL,
    balance                 text            NOT NULL,
    processed_transactions  json            NOT NULL,
    created_at              timestamptz     NOT NULL,
    version                 bigint          NOT NULL
    );
  """).update.run

  private val createIndexQuery =
    Update0(s"CREATE UNIQUE INDEX IF NOT EXISTS ${tableName}_user_id_index ON $tableName user_id", none).run
}

object AccountViewRepo {
  def apply[F[_]: Monad](transactor: Transactor[F], tableName: String = "account"): AccountRepo[F] =
    new AccountViewRepo(transactor, tableName)
}
