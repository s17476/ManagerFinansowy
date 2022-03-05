package com.lnasoftware.managerfinansowy.provider

import android.content.*
import android.database.Cursor
import android.net.Uri
import com.lnasoftware.managerfinansowy.database.Transaction
import com.lnasoftware.managerfinansowy.database.TransactionsDb

class MyContentProvider : ContentProvider() {

    companion object {
        val AUTHORITY = "com.lnasoftware.managerfinansowy.provider"
        val TABLE_NAME = Transaction::class.java.simpleName
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        if (context != null) {
            val transactionId = ContentUris.parseId(uri)
            val db = TransactionsDb.getInstance(context!!)
            val cursor = db!!.transactionDao().getItemsWithCursor(transactionId)
            cursor.setNotificationUri(context!!.contentResolver, uri)
            return cursor
            }
        throw IllegalArgumentException("Failed to query row for uri $uri")
    }

    override fun getType(uri: Uri): String? {
        return "vnd.android.cursor.item/$AUTHORITY.$TABLE_NAME"
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        if (context != null) {
            val db = TransactionsDb.getInstance(context!!)
            val id = Transaction.fromContentValues(contentValues!!).id.toLong()
            db!!.transactionDao().insertTransaction(Transaction.fromContentValues(contentValues!!))
            if (id != 0L) {
                context!!.contentResolver.notifyChange(uri, null)
                return ContentUris.withAppendedId(uri, id)
            }
        }
        throw IllegalArgumentException("Failed to insert row into $uri")
    }

    override fun delete(uri: Uri, s: String?, strings: Array<String>?): Int {
        if (context != null) {
            val db = TransactionsDb.getInstance(context!!)
            val count = Transaction(ContentUris.parseId(uri).toInt(),"", null, "", 0f).id
            db!!.transactionDao().deleteTransaction(Transaction(ContentUris.parseId(uri).toInt(),"", null, "", 0f))
            context!!.contentResolver.notifyChange(uri, null)
            return count
        }
        throw IllegalArgumentException("Failed to delete row into $uri")
    }

    override fun update(uri: Uri, contentValues: ContentValues?, s: String?, strings: Array<String>?): Int {
        if (context != null) {
            val db = TransactionsDb.getInstance(context!!)
            val count = Transaction.fromContentValues(contentValues!!).id
            db!!.transactionDao().updateTransaction(Transaction.fromContentValues(contentValues!!))
            context!!.contentResolver.notifyChange(uri, null)
            return count
        }
        throw IllegalArgumentException("Failed to update row into $uri")
    }
}