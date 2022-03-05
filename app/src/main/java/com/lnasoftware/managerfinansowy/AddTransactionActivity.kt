package com.lnasoftware.managerfinansowy

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_add_transaction.*
import java.util.*
import kotlin.math.roundToInt

class AddTransactionActivity : AppCompatActivity() {

    private val calendar = Calendar.getInstance()
    var year = calendar.get(Calendar.YEAR)
    var month = calendar.get(Calendar.MONTH) + 1
    var day = calendar.get(Calendar.DAY_OF_MONTH)

    var position: String = ""
    var oldAmount: Float = 0f

    var transactionType: Int = 0

    var isValidationOk: Boolean = true
    var toastMessage: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

//        lock screen orientation
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        dateEditText.setText("$day-$month-$year")

//        add buttons listeners

//        plus button
        plusImageButton.setOnClickListener{
            it.background
                .setTintList(ContextCompat.getColorStateList(
                    this,
                    R.color.plusButtonActiveColor
                ))
            minusImageButton.background
                .setTintList(ContextCompat.getColorStateList(
                    this,
                    R.color.defaultButtonColor
                ))
            transactionType = 1
        }

//        minus button
        minusImageButton.setOnClickListener{
            it.background
                .setTintList(ContextCompat.getColorStateList(
                    this,
                    R.color.minusButtonActiveColor
                ))
            plusImageButton.background
                .setTintList(ContextCompat.getColorStateList(
                    this,
                    R.color.defaultButtonColor
                ))
            transactionType = -1
        }

//        pick date button
        pickTextViewButton.setOnClickListener{pickDate()}

//        share button
        shareButton.setOnClickListener {shareTransaction()}

//        save button
        saveButton.setOnClickListener { addTransaction() }


//        check if it is adding or editing operation
//        if editing - set values to visible fields
        position = intent.getStringExtra("position").toString()
        if(position != "null"){
            placeEditText.setText(intent.getStringExtra("place"))
            dateEditText.setText(intent.getStringExtra("date").toString())
            categoryEditText.setText(intent.getStringExtra("category").toString())
            oldAmount = intent.getFloatExtra("amount", 0f)
            amountEditText.setText(oldAmount.toString().removePrefix("-"))

            if(oldAmount > 0){
                plusImageButton.callOnClick()
            } else {
                minusImageButton.callOnClick()
            }
        }
    }

//    helper function to pick transaction date
    private fun pickDate(){
        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                dateEditText.setText("$dayOfMonth-${(monthOfYear.toInt() + 1)}-$year")
            }, year, month, day)
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

//    user input validation
    fun validateInput(){

        isValidationOk = true

        if(placeEditText.text.length < 2){
            isValidationOk = false
            toastMessage = "Place name is to short (min 2 characters)"
        }else if(categoryEditText.text.length < 2){
            isValidationOk = false
            toastMessage = "Category name is to short (min 2 characters)"
        }else if(amountEditText.text.toString() == ""
            || (amountEditText.text.toString().toFloat()) <= 0.0
        ){
            isValidationOk = false
            toastMessage = "Amount has to be bigger than zero"
        }else if(amountEditText.text.toString().contains(",")){
            isValidationOk = false
            toastMessage = "Invalid amount format"
        }else if(transactionType == 0){
            isValidationOk = false
            toastMessage = "Choose transaction type"
        }

//        show toast if validation fails
        if(!isValidationOk){
            Toast.makeText(
                this,
                toastMessage,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun shareTransaction(){
//        user input validation
        validateInput()

//        share current transaction (even not saved) if input is valid
        if (isValidationOk){
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT,
                    "Hay. Check out my last transaction in ${placeEditText.text}. Only ${amountEditText.text}")
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }

    private fun addTransaction(){

//        user input validation
        validateInput()

        if (isValidationOk){
            //            round amount
            var amount = ((amountEditText.text.toString().toFloat()*100)
                .roundToInt()
                .toFloat() / 100) * transactionType

//            pass data to parent activity
            intent.putExtra("place", placeEditText.text.toString())
            intent.putExtra("date", dateEditText.text.toString())
            intent.putExtra("category", categoryEditText.text.toString())
            intent.putExtra("amount", amount.toString())
            intent.putExtra("position", position)
            intent.putExtra("difference", (oldAmount - amount))

            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}