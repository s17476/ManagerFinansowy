package com.lnasoftware.managerfinansowy.database

import android.database.Cursor
import androidx.room.*

@Dao
interface TransactionDao {

//    get all transactions as a cursor
    @Query("SELECT * FROM `Transaction` WHERE id = :transactionId")
    fun getItemsWithCursor(transactionId: Long): Cursor

//    get all transactions from DB DESC
    @Query("SELECT * FROM `Transaction` ORDER BY DATE DESC")
    fun getAll() : List<Transaction>

//    get all transactions from DB ASC
    @Query("SELECT * FROM `Transaction` ORDER BY DATE")
    fun getAllAsc() : List<Transaction>

//    insert new transaction to DB
    @Insert
    fun insertTransaction(transaction: Transaction)

//    update item
    @Update
    fun updateTransaction(transaction: Transaction)

//    delete item
    @Delete
    fun deleteTransaction(transaction: Transaction)
}