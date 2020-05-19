package ru.rosteelton.transactions.wirings

import cats.effect._
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.server.blaze.BlazeBuilder
import ru.rosteelton.transactions.accounts.endpoint.internal.{DefaultInternalAccountEndpoint, InternalUserAccountRoute}
import ru.rosteelton.transactions.accounts.endpoint.{DefaultUserAccountEndpoint, UserAccountEndpointRoute}
import ru.rosteelton.transactions.config.HttpServer

class EndpointWirings[F[_]: ConcurrentEffect: ContextShift: Timer](httpServer: HttpServer,
                                                  postgresWirings: PostgresWirings[F],
                                                  entityWirings: EntityWirings[F]) {

  val userAccountEndpoint = DefaultUserAccountEndpoint(postgresWirings.accountViewRepo)
  val userAccountEndpointRoute = UserAccountEndpointRoute(userAccountEndpoint)

  val internalAccountEndpoint = DefaultInternalAccountEndpoint(entityWirings.accounts)
  val internalAccountEndpointRoute = InternalUserAccountRoute(internalAccountEndpoint)

  val routes: HttpRoutes[F] = userAccountEndpointRoute <+> internalAccountEndpointRoute

  def startHttpServer: F[Unit] =
    BlazeBuilder[F]
      .bindHttp(httpServer.port, httpServer.host)
      .mountService(routes, "/")
      .serve
      .compile
      .drain
}

object EndpointWirings {
  def apply[F[_]: ConcurrentEffect: ContextShift: Timer](httpServer: HttpServer,
            postgresWirings: PostgresWirings[F],
            entityWirings: EntityWirings[F]): EndpointWirings[F] = new EndpointWirings[F](httpServer, postgresWirings, entityWirings)
}
