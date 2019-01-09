package ru.rosteelton.transactions.accounts.endpoints

import ru.rosteelton.transactions.accounts.view.AccountView
import ru.rosteelton.transactions.common.models.{AccountId, UserId}

trait UserAccountEndpoint[F[_]] {
  def getUserAccount(accountId: AccountId): F[Option[AccountView]]
  def getAccountsForUser(userId: UserId): F[List[AccountView]]
}
