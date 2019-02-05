package ru.rosteelton.transactions

import akka.actor.ActorSystem
import cats.effect.{ConcurrentEffect, ContextShift, IO, Resource}
import com.typesafe.config.ConfigFactory
import pureconfig.generic.auto._
import ru.rosteelton.transactions.config.AppConfig
import ru.rosteelton.transactions.wirings.PostgresWirings
import cats.implicits._
import doobie.util.transactor.Transactor

class AppZ[F[_]: ContextShift](implicit F: ConcurrentEffect[F]) {

  case class Resources(appConfig: AppConfig, actorSystem: ActorSystem, transactor: Transactor[F])

  def createResources: Resource[F, Resources] =
    for {
      config <- Resource.liftF(F.delay(ConfigFactory.load()))
      appConfig <- Resource.liftF(F.delay(pureconfig.loadConfigOrThrow[AppConfig](config)))
      actorSystem <- Resource.make(F.delay(ActorSystem(appConfig.cluster.systemName, config)))(
                      system => F.liftIO(IO.fromFuture(IO.delay(system.terminate()))).void
                    )
      transactor <- PostgresWirings.createTransactor(appConfig.postgres)
    } yield Resources(appConfig, actorSystem, transactor)

  def program(resources: Resources): F[Unit] = {
    for {
      _ <- PostgresWirings(resources.transactor, resources.appConfig.eventJournals)
    } yield ()
  }
}
