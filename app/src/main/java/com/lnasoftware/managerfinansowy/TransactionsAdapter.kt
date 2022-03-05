package com.lnasoftware.managerfinansowy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lnasoftware.managerfinansowy.database.Transaction


class TransactionsAdapter(private val listener: MainActivity) : RecyclerView.Adapter<TransactionViewHolder>() {

    interface OnClickListeners{
        fun onClickListener(holder: TransactionViewHolder, transaction: Transaction)
        fun onLongClickListener(transaction: Transaction):Boolean
    }

    val transactions = mutableListOf<Transaction>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder =
        TransactionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false))

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.bindOperation(transaction)

        holder.itemView.setOnClickListener{
            listener.onClickListener(holder, transaction)
        }

        holder.itemView.setOnLongClickListener{
            listener.onLongClickListener(transaction)
        }
    }

    override fun getItemCount(): Int = transactions.size

    fun addTransactions(transactions: List<Transaction>){
        if(transactions.size > 1){
            val oldSize = this.transactions.size
            this.transactions.addAll(transactions)
            notifyDataSetChanged()
            notifyItemRangeInserted(oldSize, this.transactions.size)
        } else if(transactions.isNotEmpty()){
            var index = this.transactions.indexOfFirst {
                (transactions[0].date!!.after(it.date))
                        || (transactions[0].date!!.equals(it.date))
            }
            if(index < 0) this.transactions.add(transactions[0])
            else this.transactions.add(index, transactions[0])
            notifyItemInserted(index)
        }
    }

    fun updateTransaction(transaction: Transaction, position: Int){
        transactions.removeAt(position)
        var index = transactions.indexOfFirst {
            (transaction.date!!.after(it.date))
                    || (transaction.date!!.equals(it.date))
        }
        if(index == -1){
            index = transactions.indexOfLast {
                (transaction.date!!.before(it.date))
            } + 1
        }
        if(index < 0) transactions.add(transaction)
        else transactions.add(index, transaction)

        if(position == index){
            notifyItemChanged(index)
        }else{
            notifyItemRemoved(position)
            notifyItemInserted(index)
        }
    }

    fun deleteTransaction(transaction: Transaction){
        val position = transactions.indexOf(transaction)
        transactions.remove(transaction)
        notifyItemRemoved(position)
    }
}
