package ru.rosteelton.transactions.accounts.view
import aecor.data._
import aecor.distributedprocessing.DistributedProcessing
import cats.effect.ConcurrentEffect
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import ru.rosteelton.transactions.accounts.aggregate.{UserAccountEvent, UserAccountKey, UserAccountMeta}
import ru.rosteelton.transactions.common.projection.DefaultProjectionFlow
import ru.rosteelton.transactions.common.streaming.Fs2Process

class AccountViewProjectionWiring[F[_]: ConcurrentEffect](
                                         repo: AccountRepo[F],
                                         eventSource: ConsumerId => fs2.Stream[F, Committable[F, EntityEvent[UserAccountKey, Enriched[UserAccountMeta, UserAccountEvent]]]],
                                       ) {

  val consumerId = ConsumerId("AccountViewProjection")
  val flow = DefaultProjectionFlow(Slf4jLogger.unsafeCreate[F], AccountViewProjection[F](repo))
  val stream: fs2.Stream[F, Unit] = eventSource(consumerId).through(flow)
  def process: DistributedProcessing.Process[F] = Fs2Process(stream)
}

