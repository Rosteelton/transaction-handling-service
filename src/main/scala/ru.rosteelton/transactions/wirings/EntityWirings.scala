package ru.rosteelton.transactions.wirings

import java.time.Instant
import java.util.concurrent.TimeUnit

import aecor.data.EitherK
import aecor.runtime.Eventsourced
import aecor.runtime.akkageneric.GenericAkkaRuntime
import akka.actor.ActorSystem
import cats.effect.{Clock, ConcurrentEffect}
import cats.implicits._
import ru.rosteelton.transactions.accounts.aggregate.UserAccountAggregate._
import ru.rosteelton.transactions.accounts.aggregate._

class EntityWirings[F[_]](val accounts: UserAccounts[F])

object EntityWirings {
  def apply[F[_]: ConcurrentEffect](system: ActorSystem,
                                    clock: Clock[F],
                                    postgresWirings: PostgresWirings[F]): F[EntityWirings[F]] = {
    val genericAkkaRuntime = GenericAkkaRuntime(system)

    val timestamp = clock.realTime(TimeUnit.MILLISECONDS).map(Instant.ofEpochMilli).map(UserAccountMeta)

    val accountsAggregateBehaviour: UserAccountKey => F[EitherK[UserAccountAggregate, UserAccountRejection, F]] =
      Eventsourced(DefaultUserAccountAggregate.behavior[F].enrich[UserAccountMeta](timestamp), postgresWirings.accountsJournal)

    val userAccountsF: F[UserAccounts[F]] = genericAkkaRuntime
      .runBehavior(DefaultUserAccountAggregate.entityName, accountsAggregateBehaviour)
      .map(Eventsourced.Entities.fromEitherK(_))

    for {
      userAccounts <- userAccountsF
    } yield new EntityWirings[F](userAccounts)
  }
}
