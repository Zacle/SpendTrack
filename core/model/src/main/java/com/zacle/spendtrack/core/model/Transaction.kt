package com.zacle.spendtrack.core.model

import kotlinx.datetime.Instant

/**
 * Represents a financial transaction, which could either be an expense or an income.
 * This interface is designed to store information related to a single transaction, including details
 * about the amount, associated category, optional receipt images (both local and cloud-stored),
 * and synchronization status with a remote database like Firebase.
 *
 * Each transaction is uniquely identified by its ID and can be linked to a specific user through the userId field.
 * It supports offline-first functionality by keeping track of local receipt image paths and sync states,
 * enabling delayed uploads when offline. The interface is flexible enough to handle multiple types
 * of transactions, including those with or without receipt images.
 */
interface Transaction {
    /**
     * Unique identifier for the transaction. This is typically generated when the transaction is created.
     */
    val id: String

    /**
     * The user ID associated with this transaction. Helps in identifying which user this transaction belongs to.
     */
    val userId: String

    /**
     * A short name or title for the transaction. It can be a brief label like "Grocery Shopping" or "Rent Payment".
     */
    val name: String

    /**
     * A more detailed description of the transaction. This can provide additional context, such as specific items purchased.
     */
    val description: String

    /**
     * The amount of money involved in the transaction. This value represents the total cost or income associated with the transaction.
     */
    val amount: Double

    /**
     * The exact date and time when the transaction took place. Stored as an Instant to ensure precision and easy time zone handling.
     */
    val transactionDate: Instant

    /**
     * The category associated with this transaction, such as "Food", "Utilities", or "Entertainment".
     * Helps in grouping similar transactions for reporting or budgeting purposes.
     */
    val category: Category

    /**
     * The URL of the receipt image if the receipt has been uploaded to a cloud service like Firebase Storage.
     * This URL can be used to retrieve the image from the remote server.
     */
    val receiptUrl: String?

    /**
     * The local file path of the receipt image stored on the device.
     * Used to reference the receipt image before it is uploaded to the cloud.
     * This field will be null if no image is stored locally.
     */
    val localReceiptImagePath: String?

    /**
     * The date and time when the transaction was last updated.
     * This is useful for keeping track of modifications made to the transaction after it was created.
     */
    val updatedAt: Instant?

    /**
     * A flag indicating whether this transaction has been synced with the remote server (e.g., Firebase).
     * If false, the transaction is still local and needs to be synced.
     */
    val synced: Boolean
}