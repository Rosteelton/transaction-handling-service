package ru.rosteelton.transactions.accounts.view

import java.time.Instant

import ru.rosteelton.transactions.accounts.aggregate.UserAccountState
import ru.rosteelton.transactions.common.models._

case class AccountView(accountId: AccountId,
                       userId: UserId,
                       balance: Money,
                       processedTransactions: Set[TransactionId],
                       createdAt: Instant,
                       version: Long) {
  def toUserAccountState: UserAccountState =
    UserAccountState(userId, balance, processedTransactions)
}
