package ru.rosteelton.transactions.accounts.endpoint

import ru.rosteelton.transactions.accounts.view.{ AccountRepo, AccountView }
import ru.rosteelton.transactions.common.models.{ AccountId, UserId }

class DefaultUserAccountEndpoint[F[_]](accountRepo: AccountRepo[F]) extends UserAccountEndpoint[F] {
  def getUserAccount(accountId: AccountId): F[Option[AccountView]] =
    accountRepo.getUserAccount(accountId)

  def getAccountsForUser(userId: UserId): F[List[AccountView]] =
    accountRepo.getAccountsForUser(userId)
}

object DefaultUserAccountEndpoint {
  def apply[F[_]](accountRepo: AccountRepo[F]): UserAccountEndpoint[F] = new DefaultUserAccountEndpoint[F](accountRepo)
}
