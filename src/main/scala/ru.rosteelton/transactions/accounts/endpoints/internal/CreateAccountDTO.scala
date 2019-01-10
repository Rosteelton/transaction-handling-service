package ru.rosteelton.transactions.accounts.endpoints.internal

import io.circe.Decoder
import io.circe.derivation.deriveDecoder
import ru.rosteelton.transactions.common.models.{ Money, TransactionId, UserId }

final case class CreateAccountDTO(userId: UserId, sum: Option[Money])
object CreateAccountDTO {
  implicit val createAccountCirceDecoder: Decoder[CreateAccountDTO] = deriveDecoder[CreateAccountDTO]
}

final case class DebitAccountDTO(transactionId: TransactionId, sum: Money)
object DebitAccountDTO {
  implicit val debitAccountCirceDecoder: Decoder[DebitAccountDTO] = deriveDecoder[DebitAccountDTO]
}

final case class CreditAccountDTO(transactionId: TransactionId, sum: Money)
object CreditAccountDTO {
  implicit val creditAccountCirceDecoder: Decoder[CreditAccountDTO] = deriveDecoder[CreditAccountDTO]
}
