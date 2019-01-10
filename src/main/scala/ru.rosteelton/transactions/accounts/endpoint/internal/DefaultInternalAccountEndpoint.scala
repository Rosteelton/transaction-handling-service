package ru.rosteelton.transactions.accounts.endpoint.internal

import cats.Functor
import cats.implicits._
import ru.rosteelton.transactions.accounts.aggregate.{UserAccountKey, UserAccountRejection, UserAccounts}
import ru.rosteelton.transactions.common.models.{AccountId, Money, TransactionId, UserId}

class DefaultInternalAccountEndpoint[F[_]: Functor](userAccountAggregate: UserAccounts[F])
    extends InternalAccountEndpoint[F] {

  def createAccount(accountId: AccountId, userId: UserId, sum: Option[Money]): F[Unit] =
    userAccountAggregate(UserAccountKey(accountId.value)).createAccount(userId, sum).void

  def creditAccount(accountId: AccountId,
                    transactionId: TransactionId,
                    sum: Money): F[Either[UserAccountRejection, Unit]] =
    userAccountAggregate(UserAccountKey(accountId.value)).creditAccount(transactionId, sum)

  def debitAccount(accountId: AccountId,
                   transactionId: TransactionId,
                   sum: Money): F[Either[UserAccountRejection, Unit]] =
    userAccountAggregate(UserAccountKey(accountId.value)).debitAccount(transactionId, sum)
}
