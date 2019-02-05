package ru.rosteelton.transactions.accounts.serialization

import aecor.data.Enriched
import aecor.journal.postgres.PostgresEventJournal
import aecor.journal.postgres.PostgresEventJournal.Serializer.TypeHint
import ru.rosteelton.transactions.accounts.aggregate.UserAccountEvent.{
  AccountCreated,
  AccountCredited,
  AccountDebited
}
import ru.rosteelton.transactions.accounts.aggregate.{ UserAccountEvent, UserAccountMeta }
import ru.rosteelton.transactions.userAccounts.protobuf.msg
import cats.syntax.either._

object UserAccountProtobufSerializer
    extends PostgresEventJournal.Serializer[Enriched[UserAccountMeta, UserAccountEvent]] {

  def serialize(a: Enriched[UserAccountMeta, UserAccountEvent]): (TypeHint, Array[Byte]) =
    a.event match {
      case AccountCreated(userId, withSum) =>
        "A" -> msg
          .AccountCreated(userId, withSum, a.metadata.timestamp)
          .toByteArray
      case AccountDebited(transactionId, sum) =>
        "B" -> msg
          .AccountDebited(transactionId, sum, a.metadata.timestamp)
          .toByteArray
      case AccountCredited(transactionId, sum) =>
        "C" -> msg
          .AccountCredited(transactionId, sum, a.metadata.timestamp)
          .toByteArray
    }

  def deserialize(
    typeHint: TypeHint,
    bytes: Array[Byte]
  ): Either[Throwable, Enriched[UserAccountMeta, UserAccountEvent]] =
    Either.catchNonFatal {
      typeHint match {
        case "A" =>
          val rawEvent = msg.AccountCreated.parseFrom(bytes)
          Enriched(
            UserAccountMeta(rawEvent.timestamp),
            AccountCreated(rawEvent.userId, rawEvent.withSum)
          )
        case "B" =>
          val rawEvent = msg.AccountDebited.parseFrom(bytes)
          Enriched(
            UserAccountMeta(rawEvent.timestamp),
            AccountDebited(rawEvent.transactionId, rawEvent.sum)
          )
        case "C" =>
          val rawEvent = msg.AccountCredited.parseFrom(bytes)
          Enriched(
            UserAccountMeta(rawEvent.timestamp),
            AccountCredited(rawEvent.transactionId, rawEvent.sum)
          )
      }
    }
}