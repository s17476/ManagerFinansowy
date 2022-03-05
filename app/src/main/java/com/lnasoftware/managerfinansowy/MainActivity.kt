package com.lnasoftware.managerfinansowy

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.lnasoftware.managerfinansowy.database.TransactionsDb
import com.lnasoftware.managerfinansowy.database.Transaction
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

enum class RequestCode{ADD, UPDATE}

class MainActivity : AppCompatActivity(), TransactionsAdapter.OnClickListeners{

    var floatingMenuVisibility = View.INVISIBLE

//    database initialization
    val db by lazy {
        Room.databaseBuilder(
            this,
            TransactionsDb::class.java,
            "database.db"
        ).build()
    }

//    recycler view adapter
    private val adapter = TransactionsAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        lock portrait mode
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

//        toggle floating menu visibility
        fabMenu.setOnClickListener {
            if (floatingMenuVisibility == View.INVISIBLE) {
                floatingMenuVisibility = View.VISIBLE
                fabAdd.visibility = View.VISIBLE
                fabChart.visibility = View.VISIBLE
            } else {
                floatingMenuVisibility = View.INVISIBLE
                fabAdd.visibility = View.INVISIBLE
                fabChart.visibility = View.INVISIBLE
            }
        }

//        add new transaction button listener
        fabAdd.setOnClickListener{
            val intent = Intent(this, AddTransactionActivity::class.java)
                .apply {
                    putExtra("position", "null")
                }
            floatingMenuVisibility = View.INVISIBLE
            fabAdd.visibility = View.INVISIBLE
            fabChart.visibility = View.INVISIBLE
            startActivityForResult(intent, RequestCode.ADD.ordinal)
        }

//        add chart activity button listener
        fabChart.setOnClickListener {
            if(adapter.transactions.isEmpty()){
                Toast.makeText(
                    this@MainActivity,
                    "Add some operations first!",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            val intent = Intent(this, ChartActivity::class.java)
            floatingMenuVisibility = View.INVISIBLE
            fabAdd.visibility = View.INVISIBLE
            fabChart.visibility = View.INVISIBLE
            startActivity(intent)
        }

//        recycler view setup
        recyclerView.let {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = this.adapter
        }

//        fetch transactions data
        thread {
            val transactions = db.transactionDao().getAll()
            runOnUiThread{
                adapter.addTransactions(transactions)
                for (o in adapter.transactions) balanceTextView.text =
                    (balanceTextView.text.toString().toFloat() + o.amount).toString()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
//            if transaction was added
            if (requestCode == RequestCode.ADD.ordinal) {
                val newTransaction = Transaction(
                    0,
                    data?.getStringExtra("place").toString(),
                    stringToDate(data?.getStringExtra("date").toString()),
                    data?.getStringExtra("category").toString(),
                    data?.getStringExtra("amount").toString().toFloat()
                )
//                add transaction to DB and adapter
                thread {
                    db.transactionDao().insertTransaction(newTransaction)
                    runOnUiThread{
                        adapter.addTransactions(listOf(newTransaction))
                    }
                }
//                update balance
                balanceTextView.text = (balanceTextView.text.toString().toFloat() + newTransaction.amount).toString()
//                if transaction was edited
            }else if (requestCode == RequestCode.UPDATE.ordinal){
                val position = data?.getStringExtra("position").toString()
                val updatedTransaction = Transaction(
                    data?.getIntExtra("id", 0)!!,
                    data?.getStringExtra("place").toString(),
                    stringToDate(data?.getStringExtra("date").toString()),
                    data?.getStringExtra("category").toString(),
                    data?.getStringExtra("amount").toString().toFloat()
                )
//                update transaction in DB and adapter
                thread {
                    db.transactionDao().updateTransaction(updatedTransaction)
                    runOnUiThread{
                        adapter.updateTransaction(updatedTransaction, position.toInt())
                    }
                }
//                update balance
                balanceTextView.text =
                    (balanceTextView.text.toString().toFloat()
                            - data?.getFloatExtra("difference", 0F)!!).toString()
            }
        }
    }

//    recycler view item click listener - edit operation
    override fun onClickListener(holder: TransactionViewHolder, transaction: Transaction) {
        val context = holder.itemView.context
        val editIntent = Intent(context, AddTransactionActivity::class.java)
        editIntent.putExtra("date", dateToString(transaction.date!!))
        editIntent.putExtra("place", transaction.place)
        editIntent.putExtra("category", transaction.category)
        editIntent.putExtra("amount", transaction.amount)
        editIntent.putExtra("position", holder.adapterPosition.toString())
        editIntent.putExtra("id", transaction.id)
        startActivityForResult(editIntent, RequestCode.UPDATE.ordinal)
    }

//    recycler view item long click listener - delete operation
    override fun onLongClickListener(transaction: Transaction):Boolean {
//        show confirm dialog
        AlertDialog.Builder(this@MainActivity)
            .setMessage("Are you sure you want to delete transaction?")
            .setCancelable(true)
            .setPositiveButton("Yes") { dialog, id ->
//                delete transaction from DB and adapter
                try{
                    thread {
                        db.transactionDao().deleteTransaction(transaction)
                        runOnUiThread{
                            adapter.deleteTransaction(transaction)
                        }
                    }
//                    update balance
                    balanceTextView.text = (balanceTextView.text.toString().toFloat() - transaction.amount).toString()
                }catch (e: Exception){}
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    return true
    }

//    helper function
    fun stringToDate(date: String): Date{
        val pos = ParsePosition(0)
        val simpledateformat = SimpleDateFormat("dd-MM-yyyy")
        return simpledateformat.parse(date, pos)
    }

//    helper function
    fun dateToString(date: Date): String{
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        return formatter.format(date)
    }
}

