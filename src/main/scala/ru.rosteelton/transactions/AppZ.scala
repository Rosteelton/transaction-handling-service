package ru.rosteelton.transactions

import akka.actor.ActorSystem
import cats.effect._
import com.typesafe.config.ConfigFactory
import pureconfig.generic.auto._
import ru.rosteelton.transactions.config.AppConfig
import ru.rosteelton.transactions.wirings.{EndpointWirings, EntityWirings, PostgresWirings, ProcessWirings}
import cats.implicits._
import cats.temp.par.Par
import doobie.util.transactor.Transactor

class AppZ[F[_]: ContextShift: Timer: Par](implicit F: ConcurrentEffect[F]) {

  case class Resources(appConfig: AppConfig, actorSystem: ActorSystem, transactor: Transactor[F])

  def createResources: Resource[F, Resources] =
    for {
      config <- Resource.liftF(F.delay(ConfigFactory.load()))
      _ = println(config.getString("postgres"))
      appConfig <- Resource.liftF(F.delay(pureconfig.loadConfigOrThrow[AppConfig](config)))
      actorSystem <- Resource.make(F.delay(ActorSystem(appConfig.cluster.systemName, config)))(
                      system => F.liftIO(IO.fromFuture(IO.delay(system.terminate()))).void
                    )
      transactor <- PostgresWirings.createTransactor(appConfig.postgres)
    } yield Resources(appConfig, actorSystem, transactor)

  def program(resources: Resources): F[Unit] = {
    val clock = Clock.create[F]
    for {
      postgresWirings <- PostgresWirings(resources.transactor, resources.appConfig.eventJournals)
      entityWirings <- EntityWirings(resources.actorSystem, clock, postgresWirings)
      _ <- ProcessWirings(resources.actorSystem, postgresWirings).startProcesses.void
      _ <- EndpointWirings(resources.appConfig.httpServer, postgresWirings, entityWirings).startHttpServer
    } yield ()
  }
}
