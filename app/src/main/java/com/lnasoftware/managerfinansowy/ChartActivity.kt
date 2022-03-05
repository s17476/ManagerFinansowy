package com.lnasoftware.managerfinansowy

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.room.Room
import com.lnasoftware.managerfinansowy.database.Transaction
import com.lnasoftware.managerfinansowy.database.TransactionsDb
import kotlinx.android.synthetic.main.activity_chart.*
import java.text.SimpleDateFormat
import kotlin.concurrent.thread

class ChartActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var drawView: DrawView? = null
    private val month = SimpleDateFormat("MM-yyyy")

//    database initialization
    private val db by lazy {
        Room.databaseBuilder(
            this,
            TransactionsDb::class.java,
            "database.db"
        ).build()
    }

//    all transactions from DB
    var transactions: List<Transaction>? = null

//    transactions to be shown in chart view
    private var selectedTransactions = mutableListOf<Transaction>()

//    list of months
    private var dateList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        thread {
            transactions = db.transactionDao().getAllAsc()
            runOnUiThread{

//                get available months
                transactions!!.forEach {
                    val month = month.format(it.date)
                    if (!dateList.contains(month)){
                        dateList.add(month)
                    }
                }
                dateList = dateList.reversed().toMutableList()

//                add data to the spinner
                val aa = ArrayAdapter(this, R.layout.spinner_item, dateList)
                aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                dateSpinner!!.adapter = aa
                dateSpinner.onItemSelectedListener = this

                drawView = DrawView(this, drawFrame.height, drawFrame.width, selectedTransactions)
                drawView!!.setBackgroundColor(Color.parseColor("#323232"))

                drawFrame.addView(drawView)
            }
        }
    }

//    spinner listener method
    override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, pos: Int, p3: Long) {
        selectedTransactions.clear()
        transactions!!.forEach {
            val month = month.format(it.date)
            if (dateList[pos] == month){
                selectedTransactions.add(it)
            }
        }

//        update transactions selected to be shown in chart view
        drawView = DrawView(this, drawFrame.height, drawFrame.width, selectedTransactions)
        drawFrame.addView(drawView)
    }
    override fun onNothingSelected(p0: AdapterView<*>?) {}
}