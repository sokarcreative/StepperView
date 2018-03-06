package com.sokarcreative.stepperview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        stepperView.count = 10
        //stepperView.titles = arrayOf("Step 1", "Step 2 sdfd fsdf sdf sdf df", "Step 3", "Step 4", "Step 5", "Step 6", "Step 7", "Step 8", "Step 9", "Step 10")
        stepperView.step(2)
        buttonPrevStep.setOnClickListener { if(stepperView.currentPosition-1 >= 0 && stepperView.currentPosition-1 < stepperView.count) stepperView.step(stepperView.currentPosition-1) }
        buttonNextStep.setOnClickListener { if(stepperView.currentPosition+1 < stepperView.count && stepperView.currentPosition+1 >= 0) stepperView.step(stepperView.currentPosition+1) }
    }
}
