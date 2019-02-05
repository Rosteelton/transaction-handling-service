package ru.rosteelton.transactions.accounts.aggregate

import aecor.MonadActionLiftReject
import aecor.data._
import cats.Monad
import mouse.boolean._
import ru.rosteelton.transactions.accounts.aggregate.UserAccountEvent.{
  AccountCreated,
  AccountCredited,
  AccountDebited
}
import ru.rosteelton.transactions.common.models.{ Money, TransactionId, UserId }
import cats.syntax.all._

class DefaultUserAccountAggregate[F[_], I[_]](
  implicit I: MonadActionLiftReject[I, F, Option[UserAccountState], UserAccountEvent, UserAccountRejection]
) extends UserAccountAggregate[I] {

  import I._

  def readExisting: I[UserAccountState] =
    read.flatMap {
      case Some(state) => state.pure[I]
      case _ =>
        reject[UserAccountState](UserAccountRejection.AccountDoesNotExist)
    }

  def createAccount(userId: UserId, withSum: Option[Money]): I[Unit] =
    read.flatMap {
      case None =>
        append(AccountCreated(userId, withSum.getOrElse(Money.zero)))
      case _ => unit
    }

  def debitAccount(transactionId: TransactionId, sum: Money): I[Unit] =
    readExisting.flatMap { state =>
      DefaultUserAccountAggregate
        .ifTransactionCompleted(state, transactionId)
        .fold(
          unit,
          state
            .isSufficientBalance(sum)
            .fold(append(AccountDebited(transactionId, sum)), reject(UserAccountRejection.InsufficientBalance))
        )
    }

  def creditAccount(transactionId: TransactionId, sum: Money): I[Unit] =
    readExisting.flatMap { state =>
      DefaultUserAccountAggregate
        .ifTransactionCompleted(state, transactionId)
        .fold(unit, append(AccountCredited(transactionId, sum)))
    }

  def getAccount: I[UserAccountState] = readExisting
}

object DefaultUserAccountAggregate {

  def behavior[F[_]: Monad]: EventsourcedBehavior[EitherK[
    UserAccountAggregate,
    UserAccountRejection,
    ?[_]
  ], F, Option[UserAccountState], UserAccountEvent] =
    EventsourcedBehavior
      .optionalRejectable(new DefaultUserAccountAggregate, UserAccountState.init, _.handleEvent(_))

  val entityName: String = "UserAccount"
  val entityTag: EventTag = EventTag(entityName)
  val tagging: Tagging[UserAccountKey] = Tagging.const(entityTag)

  def ifTransactionCompleted(state: UserAccountState, transactionId: TransactionId): Boolean =
    state.processedTransactions.contains(transactionId)
}
