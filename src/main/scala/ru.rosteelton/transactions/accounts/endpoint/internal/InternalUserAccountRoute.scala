package ru.rosteelton.transactions.accounts.endpoint.internal

import cats.effect.Effect
import org.http4s.circe._
import cats.implicits._
import org.http4s.{ EntityDecoder, HttpRoutes }
import org.http4s.dsl.Http4sDsl
import ru.rosteelton.transactions.common.endpoint._
import ru.rosteelton.transactions.common.models.AccountId

final class InternalUserAccountRoute[F[_]: Effect](ops: InternalAccountEndpoint[F]) extends Http4sDsl[F] {

  implicit val createAccountDecoder: EntityDecoder[F, CreateAccountDTO] = jsonOf[F, CreateAccountDTO]

  val createAccountRoute: HttpRoutes[F] = HttpRoutes.of {
    case r @ POST -> Root / "accounts" / accountId =>
      r.as[CreateAccountDTO]
        .flatMap { r =>
          ops
            .createAccount(AccountId(accountId), r.userId, r.sum)
            .toResponse
        }
  }

  implicit val debitAccountDecoder: EntityDecoder[F, DebitAccountDTO] = jsonOf[F, DebitAccountDTO]

  val debitAccountRoute: HttpRoutes[F] = HttpRoutes.of {
    case r @ POST -> Root / "accounts" / accountId / "debit" =>
      r.as[DebitAccountDTO]
        .flatMap { r =>
          ops
            .debitAccount(AccountId(accountId), r.transactionId, r.sum)
            .toResponse
        }
  }

  implicit val creditAccountDecoder: EntityDecoder[F, CreditAccountDTO] = jsonOf[F, CreditAccountDTO]

  val creditAccountRoute: HttpRoutes[F] = HttpRoutes.of {
    case r @ POST -> Root / "accounts" / accountId / "credit" =>
      r.as[CreditAccountDTO]
        .flatMap { r =>
          ops
            .creditAccount(AccountId(accountId), r.transactionId, r.sum)
            .toResponse
        }
  }

  val routes: HttpRoutes[F] = createAccountRoute <+> debitAccountRoute <+> creditAccountRoute
}
