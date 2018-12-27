package ru.rosteelton.transactions.accounts.aggregate

import ru.rosteelton.transactions.common.models.AccountId

final case class UserAccountKey(value: String) extends AnyVal {
  def toAccountId: AccountId = AccountId(value)
}
