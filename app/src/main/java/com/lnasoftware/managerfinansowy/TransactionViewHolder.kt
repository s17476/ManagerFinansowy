package com.lnasoftware.managerfinansowy

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.lnasoftware.managerfinansowy.database.Transaction
import kotlinx.android.synthetic.main.item_transaction.view.*
import java.text.SimpleDateFormat
import java.util.*

class TransactionViewHolder(view: View): RecyclerView.ViewHolder(view){

    private var view: View = view
    private var transaction: Transaction? = null

    fun bindOperation(transaction: Transaction) {
        this.transaction = transaction
//        show green icon if transaction is plus
        if(this.transaction!!.amount.toDouble() > 0){
            view.roundGreenImageView.visibility = View.VISIBLE
            view.arrowDown.visibility = View.VISIBLE

            view.roundRedImageView.visibility = View.INVISIBLE
            view.arrowUp.visibility = View.INVISIBLE
//        show green icon if transaction is plus
        } else {
            view.roundRedImageView.visibility = View.VISIBLE
            view.arrowUp.visibility = View.VISIBLE

            view.roundGreenImageView.visibility = View.INVISIBLE
            view.arrowDown.visibility = View.INVISIBLE
        }
        view.placeTextView.text = transaction.place
        view.dateTextView.text = dateToString(transaction.date!!)
        view.categoryTextView.text = transaction.category
        view.amountTextView.text = transaction.amount.toString().trim()
    }

    fun dateToString(date: Date): String{
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        return formatter.format(date)
    }
}