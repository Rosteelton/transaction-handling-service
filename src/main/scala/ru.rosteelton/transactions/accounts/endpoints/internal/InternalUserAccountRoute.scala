package ru.rosteelton.transactions.accounts.endpoints.internal
import cats.effect.Effect
import org.http4s.circe._
import cats.implicits._
import io.circe.syntax._
import org.http4s.dsl.Http4sDsl._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import ru.rosteelton.transactions.common.models.{AccountId, Money, UserId}

final class InternalUserAccountRoute[F[_]: Effect](ops: InternalAccountEndpoint[F]) extends Http4sDsl[F] {

  final case class CreateAccountDTO(sum: Option[Money])

  val createAccountRoute: HttpRoutes[F] = HttpRoutes.of {
    case r @ POST -> Root / "users" / userId / "accounts" / accountId =>
      r.as[CreateAccountDTO].flatMap(r => ops.createAccount(AccountId(accountId), UserId(userId), r.sum))
  }

  val routes: HttpRoutes[F] = createAccountRoute

}
