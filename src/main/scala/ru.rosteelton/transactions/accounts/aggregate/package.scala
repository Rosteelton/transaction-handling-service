package ru.rosteelton.transactions.accounts

import aecor.runtime.Eventsourced.Entities

package object aggregate {
  type UserAccounts[F[_]] = Entities.Rejectable[UserAccountKey,
                                                UserAccountAggregate,
                                                F,
                                                UserAccountRejection]
}
