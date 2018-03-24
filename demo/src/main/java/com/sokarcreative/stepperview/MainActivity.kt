package com.sokarcreative.stepperview

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.widget.RadioGroup
import kotlinx.android.synthetic.main.activity_main.*





class MainActivity : AppCompatActivity() {

    private val handler = Handler()

    private val runnableStepCountUpdate = Runnable {
        setupStepperView()
    }

    private fun setupStepperView(){
        val stepCount = try{ editTextStepCount.text.toString().toIntOrNull() } catch (e: Exception){ null }
        if(stepCount != null){
            stepperView.count  = stepCount
            val titles = arrayListOf<CharSequence>()
            for (i in 0 until stepCount){
                titles.add("Step ${i+1}")
            }
            stepperView.titles = titles.toTypedArray()
            stepperView.step(0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupStepperView()
        stepperView.step(2)
        buttonPrevStep.setOnClickListener { if(stepperView.currentPosition-1 >= 0 && stepperView.currentPosition-1 < stepperView.count) stepperView.step(stepperView.currentPosition-1) }
        buttonNextStep.setOnClickListener { if(stepperView.currentPosition+1 < stepperView.count && stepperView.currentPosition+1 >= 0) stepperView.step(stepperView.currentPosition+1) }
        editTextStepCount.filters = arrayOf(InputFilterMinMax(0, 10))
        editTextStepCount.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                handler.removeCallbacks(runnableStepCountUpdate)
                handler.postDelayed(runnableStepCountUpdate, 200)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        radioGroupTitles.setOnCheckedChangeListener(({ radioGroup: RadioGroup, _: Int ->
            stepperView.showTitles = when(radioGroup.checkedRadioButtonId){
                R.id.radioButtonHideTitles -> StepperView.HIDE_TITLES
                R.id.radioButtonShowCurrentTitles -> StepperView.SHOW_CURRENT_TITLE
                R.id.radioButtonShowAllTitles -> StepperView.SHOW_ALL_TITLES
                else -> StepperView.HIDE_TITLES
            }
            stepperView.step(stepperView.currentPosition)
        }))
        radioButtonShowCurrentTitles.isChecked = true
    }

    inner class InputFilterMinMax(val min: Int = 0, val max: Int = 0) : InputFilter {

        override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
            try {
                val input = Integer.parseInt(dest.toString() + source.toString())
                if (isInRange(min, max, input))
                    return null
            } catch (nfe: NumberFormatException) {
            }

            return ""
        }

        private fun isInRange(a: Int, b: Int, c: Int): Boolean {
            return if (b > a) c >= a && c <= b else c >= b && c <= a
        }
    }
}
