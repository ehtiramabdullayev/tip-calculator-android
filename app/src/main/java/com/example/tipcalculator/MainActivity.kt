package com.example.tipcalculator

import android.animation.ArgbEvaluator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.tipcalculator.logging.Keys.EMPTY_STRING
import com.example.tipcalculator.logging.Keys.INITIAL_TIP_PERCENT
import com.example.tipcalculator.logging.Keys.TAG
import com.example.tipcalculator.logging.Keys.TIP_DESCRIPTION_ACCEPTABLE
import com.example.tipcalculator.logging.Keys.TIP_DESCRIPTION_AMAZING
import com.example.tipcalculator.logging.Keys.TIP_DESCRIPTION_GOOD
import com.example.tipcalculator.logging.Keys.TIP_DESCRIPTION_GREAT
import com.example.tipcalculator.logging.Keys.TIP_DESCRIPTION_POOR
import com.example.tipcalculator.logging.Keys.TIP_FORMAT


class MainActivity : AppCompatActivity() {
    private lateinit var etBaseAmount: EditText
    private lateinit var seekBarTip: SeekBar
    private lateinit var tvTipPercentLabel: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipDescription: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etBaseAmount = findViewById(R.id.etBaseAmount)
        seekBarTip = findViewById(R.id.seekBarTip)
        tvTipPercentLabel = findViewById(R.id.tvTipPercentLabel)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipDescription = findViewById(R.id.tvTipDescription)

        seekBarTip.progress = INITIAL_TIP_PERCENT
        tvTipPercentLabel.text = "$INITIAL_TIP_PERCENT%"
        updateTipDescription(INITIAL_TIP_PERCENT)
        seekBarTip.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "onProgressChanged $progress")
                tvTipPercentLabel.text = "$progress%"
                computeTipAndTotal()
                updateTipDescription(progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
        etBaseAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable?) {
                Log.i(TAG, "afterTextChanged $s")
                computeTipAndTotal()
            }
        })
    }

    private fun updateTipDescription(tipPercent: Int) {
        val tipDescription = when(tipPercent){
            in 0..9 -> TIP_DESCRIPTION_POOR
            in 10..14 -> TIP_DESCRIPTION_ACCEPTABLE
            in 15..19 -> TIP_DESCRIPTION_GOOD
            in 20..24 -> TIP_DESCRIPTION_GREAT
            else ->  TIP_DESCRIPTION_AMAZING
        }
        tvTipDescription.text = tipDescription
        // update the color based on the tipPercent
        val color = ArgbEvaluator().evaluate(
            tipPercent .toFloat()/ seekBarTip.max,
            ContextCompat.getColor(this,R.color.color_worst_tip),
            ContextCompat.getColor(this,R.color.color_best_tip)
        ) as Int
        tvTipDescription.setTextColor(color)
    }

    private fun computeTipAndTotal() {
        if (etBaseAmount.text.isEmpty()) {
            tvTipAmount.text = EMPTY_STRING
            tvTotalAmount.text = EMPTY_STRING
            return
        }
        val baseAmount = etBaseAmount.text.toString().toDouble()
        val tipPercent = seekBarTip.progress
        val tipAmount = baseAmount * tipPercent / 100
        val totalAmount = baseAmount + tipAmount
        tvTipAmount.text = TIP_FORMAT.format(tipAmount)
        tvTotalAmount.text = TIP_FORMAT.format(totalAmount)
    }
}