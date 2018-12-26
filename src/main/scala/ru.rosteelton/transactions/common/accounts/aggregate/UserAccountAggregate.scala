package ru.rosteelton.transactions.common.accounts.aggregate

import aecor.macros.boopickleWireProtocol
import cats.tagless.autoFunctorK
import ru.rosteelton.transactions.common.models.{Money, TransactionId, UserId}

@autoFunctorK(false)
@boopickleWireProtocol
trait UserAccountAggregate[F[_]] {
  def createAccount(userId: UserId, withSum: Option[Money]): F[Unit]
  def debitAccount(transactionId: TransactionId, sum: Money): F[Unit]
  def creditAccount(transactionId: TransactionId, sum: Money): F[Unit]
  def getAccount: F[UserAccountState]
}

sealed trait UserAccountRejection extends Product with Serializable
object UserAccountRejection {
  case object AccountDoesNotExist extends UserAccountRejection
  case object InsufficientBalance extends UserAccountRejection
}


