package ru.rosteelton.transactions.accounts.endpoint.internal

import ru.rosteelton.transactions.accounts.aggregate.UserAccountRejection
import ru.rosteelton.transactions.common.models.{AccountId, Money, TransactionId, UserId}

trait InternalAccountEndpoint[F[_]] {
  def createAccount(accountId: AccountId, userId: UserId, sum: Option[Money]): F[Unit]
  def creditAccount(accountId: AccountId, transactionId: TransactionId, sum: Money): F[Either[UserAccountRejection, Unit]]
  def debitAccount(accountId: AccountId, transactionId: TransactionId, sum: Money): F[Either[UserAccountRejection, Unit]]
}
