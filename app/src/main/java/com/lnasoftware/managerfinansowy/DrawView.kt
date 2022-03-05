package com.lnasoftware.managerfinansowy

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import com.lnasoftware.managerfinansowy.database.Transaction
import java.text.SimpleDateFormat
import java.util.*


class DrawView : View {
    var parentHeight: Int = 0
    var parentWidth: Int = 0
    var transactions: List<Transaction>?

    var maxAmount: Float? = 0f
    var minAmount: Float? = 0f

    var blockSizeVertical: Float = 0f
    var blockSizeHorizontal: Float = 0f

    private val balanceList = mutableListOf<Float?>()

    var zeroLevel: Float = 0f

    constructor(
        context: Context?,
        parentHeight: Int,
        parentWidth: Int,
        transactions: List<Transaction>?,
        ) : super(context) {
        this.parentHeight = parentHeight - 200
        this.parentWidth = parentWidth
        this.transactions = transactions

//        variable initialization
        init()
    }

//    helper functions to operate on chart width and height
    private fun hSize(blocks: Int): Float = blockSizeVertical * blocks
    private fun wSize(blocks: Int): Float = blockSizeHorizontal * blocks

    private fun init() {

//        get a list of total daily transactions
        minMaxBalance()

        blockSizeHorizontal = parentWidth / 35f
        maxAmount = 0f

//        calculate highest monthly balance
        var tmpAmount = 0f
        balanceList.forEach {
            if(it != null){
                tmpAmount += it
                if (tmpAmount > maxAmount!!){
                    maxAmount = tmpAmount
                }
            }
        }

//        calculate lowest monthly balance
        minAmount = 0f
        tmpAmount = 0f
        balanceList.forEach {
            if(it != null){
                tmpAmount += it
                if (tmpAmount < minAmount!!){
                    minAmount = tmpAmount
                }
            }
        }

//        calculate chart single block size used to show data
        blockSizeVertical = parentHeight / ((maxAmount!!)?.minus(minAmount!!))!!

//        calculate level zero on the chart
        zeroLevel = (maxAmount!! * blockSizeVertical) + 100
    }

    public override fun onDraw(canvas: Canvas) {

//        reset canvas
        canvas.drawColor(Color.parseColor("#323232"))


        val blackVerticalLinePaint = Paint()
        blackVerticalLinePaint.color = Color.BLACK
        blackVerticalLinePaint.strokeWidth = 14f

        val blackHorizontalLinePaint = Paint()
        blackHorizontalLinePaint.color = Color.BLACK
        blackHorizontalLinePaint.strokeWidth = 7f

        val textPaint = Paint()
        textPaint.color = Color.GRAY
        textPaint.strokeWidth = 7f

        val greenPaint = Paint()
        greenPaint.color = Color.GREEN
        greenPaint.strokeWidth = 10f

        val redPaint = Paint()
        redPaint.color = Color.RED
        redPaint.strokeWidth = 10f

        var currentPaint: Paint

//        paint vertical line
        canvas.drawLine(
            wSize(4),
            0f,
            wSize(4),
            parentHeight.toFloat() + 200,
            blackVerticalLinePaint
            )

//        paint horizontal line
        canvas.drawLine(
            wSize(4),
            zeroLevel,
            wSize(100),
            zeroLevel,
            blackHorizontalLinePaint
        )

//        vertical scale
//        zero
        textPaint.textSize = 50f
        canvas.drawText(
            "0 zl",
            0f,
            zeroLevel,
            textPaint
        )
//        below zero
        for(i: Int in 1..((-minAmount!!/100) + 1).toInt()){
            val yPos = zeroLevel + hSize(i * 100)
            canvas.drawText(
                "-${i}00",
                0f,
                yPos,
                textPaint
            )
            canvas.drawLine(
                wSize(4) - 6,
                yPos,
                wSize(4) + 6,
                yPos,
                textPaint
            )
        }
//        above zero
        for(i: Int in 1..((maxAmount!!/100) + 1).toInt()){
            val yPos = zeroLevel - hSize(i * 100)
            canvas.drawText(
                "${i}00",
                0f,
                yPos,
                textPaint
            )
            canvas.drawLine(
                wSize(4) - 6,
                yPos,
                wSize(4) + 6,
                yPos,
                textPaint
            )
        }

//        horizontal scale
//        text above zero level
        textPaint.textSize = 35f
        for (i: Int in 1..balanceList.size step 2){
            canvas.drawText(
                "$i",
                wSize(4 + i),
                zeroLevel - 40 ,
                textPaint
            )
            canvas.drawLine(
                wSize(4 + i),
                zeroLevel - 6 ,wSize(4 + i),
                zeroLevel + 6,
                textPaint
            )
        }
//        text below zero level
        for (i: Int in 2..balanceList.size step 2){
            canvas.drawText(
                "$i",
                wSize(4 + i),
                zeroLevel + 40,
                textPaint)
            canvas.drawLine(
                wSize(4 + i),
                zeroLevel - 6 ,
                wSize(4 + i),
                zeroLevel + 6,
                textPaint
            )
        }

//        calculate and draw chart lines
        var lastBalance: Float? = null
        var lastDate: Int = 0
        for(dailyBalance: Int in 0..balanceList.size-1){
//            if there was transaction
            if (balanceList[dailyBalance] != null){
                if(lastBalance != null){
//                    calculate current Y axis position
                    val currentPos = (zeroLevel - (blockSizeVertical*(lastBalance + balanceList[dailyBalance]!!)))
//                    choose paint
                    if(currentPos > zeroLevel){
                        currentPaint = redPaint
                    }else{
                        currentPaint = greenPaint
                    }
//                    draw line from previous balance change
                    canvas.drawLine(
                        wSize(4) + ((lastDate + 1) * blockSizeHorizontal),
                        (zeroLevel - (blockSizeVertical*(lastBalance!!).toFloat())),
                        wSize(4) + ((dailyBalance + 1) * blockSizeHorizontal),
                        currentPos,
                        currentPaint
                    )
//                    update last balance changes data
                    lastBalance += balanceList[dailyBalance]!!
                    lastDate = dailyBalance
                }else{
//                    calculate current Y axis position
                    val currentPos = (zeroLevel - (blockSizeVertical*(balanceList[dailyBalance]!!).toFloat()))
//                    choose paint
                    if (currentPos > zeroLevel){
                        currentPaint = redPaint
                    }else{
                        currentPaint = greenPaint
                    }
//                    draw vertical line from 0 to day 1
                    canvas.drawLine(
                        wSize(4),
                        currentPos,
                        wSize(4) + ((dailyBalance + 1) * blockSizeHorizontal),
                        currentPos,
                        currentPaint
                    )
//                    update last balance changes data
                    lastBalance = balanceList[dailyBalance]
                    lastDate = dailyBalance
                }
            }
        }
    }

//    helper function returning day number from string date
    private fun dateToDayNumber(date: Date): Int{
        val formatter = SimpleDateFormat("dd")
        return formatter.format(date).toInt()
    }

//    create list of total daily transactions
    fun minMaxBalance(){
        for (i: Int in 1..31){
            var dailyBalance: Float? = null
            for (j: Int in 0..transactions!!.size-1){
                if(dateToDayNumber(transactions!![j]?.date!!) == i){
                    if(dailyBalance == null){
                        dailyBalance = 0f
                    }
                    dailyBalance += transactions!![j]?.amount
                }
            }
            balanceList.add(dailyBalance)
        }
    }

}