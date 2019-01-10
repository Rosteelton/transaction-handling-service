package ru.rosteelton.transactions.accounts.view

import java.time.Instant

import io.circe.Encoder
import ru.rosteelton.transactions.accounts.aggregate.UserAccountState
import ru.rosteelton.transactions.common.models._
import io.circe.derivation.deriveEncoder
import io.circe.java8.time._

case class AccountView(accountId: AccountId,
                       userId: UserId,
                       balance: Money,
                       processedTransactions: Set[TransactionId],
                       createdAt: Instant,
                       version: Long) {
  def toUserAccountState: UserAccountState =
    UserAccountState(userId, balance, processedTransactions)
}

object AccountView {
  implicit val accountViewEncoder: Encoder[AccountView] = deriveEncoder[AccountView]
}
