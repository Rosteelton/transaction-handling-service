package ru.rosteelton.transactions.wirings

import aecor.data._
import aecor.distributedprocessing.DistributedProcessing
import akka.actor.ActorSystem
import cats.effect.{ConcurrentEffect, Timer}
import cats.implicits._
import cats.temp.par.Par
import cats.temp.par._
import ru.rosteelton.transactions.accounts.aggregate.DefaultUserAccountAggregate
import ru.rosteelton.transactions.accounts.view.AccountViewProjectionWiring

class ProcessWirings[F[_]: Timer: ConcurrentEffect: Par](system: ActorSystem,
                                                         postgresWirings: PostgresWirings[F]) {

  val distributedProcessing = DistributedProcessing(system)

  val accountEvents = postgresWirings.accountsJournal
    .queries(postgresWirings.eventJournalConfig.accounts.pollingInterval)
    .withOffsetStore(postgresWirings.offsetKeyValueStore)

  def accountEventSource(eventTag: EventTag, consumerId: ConsumerId) =
    fs2.Stream.force(accountEvents.eventsByTag(eventTag, consumerId).map(_.map(_.map(_._2))))

  val accountViewProjectionWiring = AccountViewProjectionWiring(
    postgresWirings.accountViewRepo,
    DefaultUserAccountAggregate.tagging,
    accountEventSource
  )

  def startProcesses: F[List[DistributedProcessing.KillSwitch[F]]] =
    List("accountViewProjectionWiring" -> accountViewProjectionWiring.process).parTraverse {
      case (name, process) => distributedProcessing.start(name, process)
    }
}

object ProcessWirings {
  def apply[F[_]: Timer: ConcurrentEffect: Par](system: ActorSystem,
                                                postgresWirings: PostgresWirings[F]): ProcessWirings[F] =
    new ProcessWirings(system, postgresWirings)
}