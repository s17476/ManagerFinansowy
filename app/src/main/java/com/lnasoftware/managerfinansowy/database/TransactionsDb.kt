package com.lnasoftware.managerfinansowy.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Transaction::class], version = 1)
@TypeConverters(Converters::class)
abstract class TransactionsDb : RoomDatabase(){
    abstract fun transactionDao() : TransactionDao

    companion object{
        @Volatile
        private var INSTANCE: TransactionsDb? = null

        fun getInstance(context: Context): TransactionsDb? {
           if (INSTANCE == null) {
                synchronized(TransactionsDb::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        TransactionsDb::class.java, "database.db"
                        )
                        .build()
                    }
                }
           }
           return INSTANCE
        }
    }
}