package ru.rosteelton.transactions.accounts.aggregate

import aecor.macros.boopickleWireProtocol
import cats.tagless.autoFunctorK
import ru.rosteelton.transactions.common.models.{Money, TransactionId, UserId}
import boopickle.Default._
import io.circe.Encoder
import scodec.Codec

@autoFunctorK(false)
@boopickleWireProtocol
trait UserAccountAggregate[F[_]] {
  def createAccount(userId: UserId, withSum: Option[Money]): F[Unit]
  def debitAccount(transactionId: TransactionId, sum: Money): F[Unit]
  def creditAccount(transactionId: TransactionId, sum: Money): F[Unit]
  def getAccount: F[UserAccountState]
}

sealed trait UserAccountRejection extends Product with Serializable
object UserAccountRejection {
  case object AccountDoesNotExist extends UserAccountRejection
  case object InsufficientBalance extends UserAccountRejection


  implicit val userAccountRejectionEncoder: Encoder[UserAccountRejection] =
    Encoder[String].contramap(_.toString)
}

object UserAccountAggregate {
  implicit val rejectionPickler: boopickle.Pickler[UserAccountRejection] =
    compositePickler[UserAccountRejection]
      .addConcreteType[UserAccountRejection.AccountDoesNotExist.type]
      .addConcreteType[UserAccountRejection.InsufficientBalance.type]

  implicit val rejectionCodec: Codec[UserAccountRejection] =
    aecor.macros.boopickle.BoopickleCodec.codec[UserAccountRejection]
}