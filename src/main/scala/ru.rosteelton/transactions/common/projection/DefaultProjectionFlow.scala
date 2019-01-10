package ru.rosteelton.transactions.common.projection

import aecor.data.{ Committable, EntityEvent }
import cats.effect.Sync
import io.chrisdavenport.log4cats.Logger
import cats.implicits._
import mouse.boolean._

object DefaultProjectionFlow {
  def apply[F[_], K, E, S](logger: Logger[F], projection: Projection[F, EntityEvent[K, E], S])(
    implicit F: Sync[F]
  ): fs2.Sink[F, Committable[F, EntityEvent[K, E]]] = {

    def foldEvent(event: EntityEvent[K, E], state: Option[S]): F[S] = {
      val newVersion = projection.applyEvent(state)(event)
      logger.debug(s"New version [$newVersion]") >>
        newVersion
          .fold(
            F.raiseError[S](new IllegalStateException(s"Projection failed for state = [$state], event = [$event]"))
          )(_.pure[F])
    }

    def runProjection(event: EntityEvent[K, E]): F[Unit] =
      projection.fetchVersionAndState(event).flatMap {
        case ((currentVersion, currentState)) =>
          for {
            _ <- logger.debug(s"Current $currentVersion [$currentState]")
            _ <- (currentVersion < event.sequenceNr).fold(foldEvent(event, currentState).flatMap { state =>
                  projection.saveNewVersion(state, currentVersion + 1)
                }, ().pure[F])
          } yield ()
      }

    _.evalMap(_.traverse(runProjection)).evalMap(_.commit)
  }
}
