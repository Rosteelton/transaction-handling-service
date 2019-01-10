package ru.rosteelton.transactions.accounts.view

import ru.rosteelton.transactions.common.models.{AccountId, UserId}

trait AccountRepo[F[_]] {
  def saveAccountView(view: AccountView): F[Unit]
  def getUserAccount(accountId: AccountId): F[Option[AccountView]]
  def getAccountsForUser(userId: UserId): F[List[AccountView]]
}
