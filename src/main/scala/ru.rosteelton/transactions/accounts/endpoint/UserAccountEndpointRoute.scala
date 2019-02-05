package ru.rosteelton.transactions.accounts.endpoint

import cats.effect.Effect
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._
import cats.implicits._
import ru.rosteelton.transactions.common.models.{ AccountId, UserId }
import io.circe.syntax._

final class UserAccountEndpointRoute[F[_]: Effect](ops: UserAccountEndpoint[F]) extends Http4sDsl[F] {

  val getAccountById: HttpRoutes[F] = HttpRoutes.of {
    case GET -> Root / "accounts" / accountId =>
      ops.getUserAccount(AccountId(accountId)).flatMap(_.fold(NotFound())(view => Ok(view.asJson)))
  }

  val getAccountsByUserId: HttpRoutes[F] = HttpRoutes.of {
    case GET -> Root / "users" / userId / "accounts" =>
      ops.getAccountsForUser(UserId(userId)).flatMap(accounts => Ok(accounts.asJson))
  }

  val routes: HttpRoutes[F] = getAccountById <+> getAccountsByUserId
}

object UserAccountEndpointRoute {
  def apply[F[_]: Effect](ops: UserAccountEndpoint[F]): HttpRoutes[F] =
    new UserAccountEndpointRoute[F](ops).routes
}
