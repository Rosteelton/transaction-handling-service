package ru.rosteelton.transactions.accounts.view

import ru.rosteelton.transactions.common.models.AccountId

trait AccountRepo[F[_]] {
  def saveAccountView(view: AccountView): F[Unit]
  def getUserAccount(accountId: AccountId): F[Option[AccountView]]
}
