package ru.rosteelton.transactions.accounts.aggregate

import ru.rosteelton.transactions.common.models.{Money, TransactionId, UserId}

sealed trait UserAccountEvent extends Product with Serializable
object UserAccountEvent {
  case class AccountCreated(userId: UserId, withSum: Money) extends UserAccountEvent
  case class AccountDebited(transactionId: TransactionId, sum: Money) extends UserAccountEvent
  case class AccountCredited(transactionId: TransactionId, sum: Money) extends UserAccountEvent
}