package ru.rosteelton.transactions.accounts.view

import aecor.data.{Enriched, EntityEvent, Folded}
import cats.Functor
import cats.implicits._
import cats.syntax.option._
import ru.rosteelton.transactions.accounts.aggregate.{UserAccountEvent, UserAccountKey, UserAccountMeta, UserAccountState}
import ru.rosteelton.transactions.common.projection.Projection

class AccountViewProjection[F[_]: Functor](repo: AccountRepo[F])
    extends Projection[F, EntityEvent[UserAccountKey, Enriched[UserAccountMeta, UserAccountEvent]], AccountView] {

  def fetchVersionAndState(
    event: EntityEvent[UserAccountKey, Enriched[UserAccountMeta, UserAccountEvent]]
  ): F[(Long, Option[AccountView])] =
    repo
      .getUserAccount(event.entityKey.toAccountId)
      .map(optView => optView.fold(0L -> optView)(view => view.version -> view.some))

  def saveNewVersion(s: AccountView, version: Long): F[Unit] =
    repo.saveAccountView(s.copy(version = version))

  def applyEvent(
    s: Option[AccountView]
  )(event: EntityEvent[UserAccountKey, Enriched[UserAccountMeta, UserAccountEvent]]): Folded[AccountView] =
    s match {
      case Some(view) =>
        view.toUserAccountState
          .handleEvent(event.payload.event)
          .map(
            _ =>
              AccountView(
                event.entityKey.toAccountId,
                view.userId,
                view.balance,
                view.processedTransactions,
                view.createdAt,
                view.version
            )
          )
      case None =>
        UserAccountState
          .init(event.payload.event)
          .map(
            state =>
              AccountView(
                event.entityKey.toAccountId,
                state.userId,
                state.balance,
                state.processedTransactions,
                event.payload.metadata.timestamp, //safe because it only could happens for created event
                0L
            )
          )
    }
}