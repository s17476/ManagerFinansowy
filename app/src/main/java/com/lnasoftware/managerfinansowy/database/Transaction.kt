package com.lnasoftware.managerfinansowy.database

import android.content.ContentValues
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

@Entity
data class Transaction (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var place: String,
    var date: Date?,
    var category: String,
    var amount: Float
) {


    companion object {
        fun fromContentValues(values: ContentValues): Transaction {
            var place: String = ""
            var date: Date? = null
            var category: String = ""
            var amount: Float = 0f
            if (values.containsKey("place")){
                place = values.getAsString("place")
            }
            if (values.containsKey("date")){
                date = stringToDate(values.getAsString("date"))
            }
            if (values.containsKey("category")){
                category = values.getAsString("category")
            }
            if (values.containsKey("amount")){
                amount = values.getAsFloat("amount")
            }
            return Transaction(
                0,
                place,
                date,
                category,
                amount
            )
        }

        fun stringToDate(date: String): Date{
            val pos = ParsePosition(0)
            val simpledateformat = SimpleDateFormat("dd-MM-yyyy")
            return simpledateformat.parse(date, pos)
        }
    }
}


