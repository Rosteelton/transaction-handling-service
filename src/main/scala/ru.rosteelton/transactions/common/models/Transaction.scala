package ru.rosteelton.transactions.common.models

final case class Transaction(transactionId: TransactionId,
                             from: Gateway,
                             to: Gateway,
                             sum: Money)
