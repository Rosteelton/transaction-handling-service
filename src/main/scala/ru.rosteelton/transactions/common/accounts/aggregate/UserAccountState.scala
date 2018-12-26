package ru.rosteelton.transactions.common.accounts.aggregate

import aecor.data.Folded
import ru.rosteelton.transactions.common.models._
import aecor.data.Folded.syntax._
import ru.rosteelton.transactions.common.accounts.aggregate.UserAccountEvent.{
  AccountCreated,
  AccountCredited,
  AccountDebited
}
final case class UserAccountState(userId: UserId,
                                  balance: Money,
                                  processedTransactions: Set[TransactionId]) {
  def isSufficientBalance(sumToDebit: Money): Boolean =
    sumToDebit.value <= balance.value

  def handleEvent(event: UserAccountEvent): Folded[UserAccountState] =
    event match {
      case AccountDebited(transactionId, sum) =>
        copy(balance = balance - sum,
             processedTransactions = processedTransactions + transactionId).next
      case AccountCredited(transactionId, sum) =>
        copy(balance = balance + sum,
             processedTransactions = processedTransactions + transactionId).next
      case _: AccountCreated => this.next
    }
}

object UserAccountState {
  def init(event: UserAccountEvent): Folded[UserAccountState] =
    event match {
      case AccountCreated(userId, withSum) =>
        UserAccountState(userId, withSum, Set.empty).next
      case _ => impossible
    }
}
