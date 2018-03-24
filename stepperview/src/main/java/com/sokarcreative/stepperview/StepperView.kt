package com.sokarcreative.stepperview

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import java.util.concurrent.atomic.AtomicInteger




/**
 * Created by sokarcreative on 24/02/2018.
 */
class StepperView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr){

    var count: Int = 0
        set(value){
            field = value
            isBuilt = false
        }
    var currentPosition: Int = -1
        private set

    var titles: Array<CharSequence>? = null
        set(value){
            field = value
            if(isBuilt){
                for(i: Int in 0 until titleTextViews.count()){
                    titleTextViews[i].apply {
                        text = if(value != null && value.size > i) value[i] else ""

                        when{
                            text.isBlank() -> visibility = View.GONE
                            showTitles == SHOW_NONE -> visibility = View.GONE
                            showTitles == SHOW_ALL_TITLES -> visibility = View.VISIBLE
                            showTitles == SHOW_CURRENT_TITLE && currentPosition == i -> visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

    var stepDimensions: Int = convertDpToPixel(context, 15f).toInt()
        set(value){
            field = value
            if(isBuilt){
                for(i:Int in 0 until stepViews.size){
                    stepViews[i].apply {
                        layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                            width = stepDimensions
                            height = stepDimensions
                        }
                    }
                }
            }
        }
    var lineHeight: Int = convertDpToPixel(context, 1f).toInt()
        set(value){
            field = value
            if(isBuilt){
                for(i:Int in 0 until lineViews.size){
                    lineViews[i].apply {
                        layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                            width = 0
                            height = lineHeight
                        }
                    }
                }
            }
        }
    var titleMarginTop: Int = convertDpToPixel(context, 5f).toInt()
        set(value){
            field = value
            if(isBuilt){
                for(i:Int in 0 until titleTextViews.size){
                    val textViewTitle = titleTextViews[i]

                    val constraintSet = ConstraintSet()
                    constraintSet.clone(this)
                    constraintSet.connect(textViewTitle.id, ConstraintSet.TOP, stepViews.get(i).id, ConstraintSet.BOTTOM, titleMarginTop)
                    constraintSet.applyTo(this)
                }
            }
        }

    var showTitles: Int = SHOW_CURRENT_TITLE

    var drawablePrevStep: Drawable
        private set
    var drawableCurrentStep: Drawable
        private set
    var drawableNextStep: Drawable
        private set
    var drawablePrevLine: Drawable
        private set
    var drawableNextLine: Drawable
        private set

    private var textViewTitleResourceId: Int = R.layout.stepper_textview_title

    private var drawablePrevStepResourceId: Int = R.drawable.circle_prev_step
    private var drawableCurrentStepResourceId: Int = R.drawable.circle_current_step
    private var drawableNextStepResourceId: Int = R.drawable.circle_next_step
    private var drawablePrevLineResourceId: Int = R.drawable.line_prev
    private var drawableNextLineResourceId: Int = R.drawable.line_next

    private var stepViews: ArrayList<View>
    private var lineViews: ArrayList<View>
    private var titleTextViews: ArrayList<TextView>

    private var isBuilt = false

    init {
        if (attrs != null) {
            val a = this.context.obtainStyledAttributes(attrs,  R.styleable.StepperView)
            val N = a.indexCount

            for (i in 0 until N) {
                val attr = a.getIndex(i)
                if (attr == R.styleable.StepperView_count) {
                    this.count = a.getInteger(attr, this.count)
                } else if (attr == R.styleable.StepperView_titles) {
                    this.titles = a.getTextArray(attr)
                }else if (attr == R.styleable.StepperView_stepDimensions) {
                    this.stepDimensions = a.getDimensionPixelOffset(attr, this.stepDimensions)
                }else if (attr == R.styleable.StepperView_lineHeight) {
                    this.lineHeight = a.getDimensionPixelOffset(attr, this.lineHeight)
                }else if (attr == R.styleable.StepperView_titleMarginTop) {
                    this.titleMarginTop = a.getDimensionPixelOffset(attr, this.titleMarginTop)
                }else if (attr == R.styleable.StepperView_textViewTitle) {
                    this.textViewTitleResourceId = a.getResourceId(attr, this.textViewTitleResourceId)
                }else if (attr == R.styleable.StepperView_showTitles) {
                    this.showTitles = a.getInteger(attr, this.showTitles)
                }else if (attr == R.styleable.StepperView_drawablePrevStep) {
                    this.drawablePrevStepResourceId = a.getResourceId(attr, this.drawablePrevStepResourceId)
                }else if (attr == R.styleable.StepperView_drawableCurrentStep) {
                    this.drawableCurrentStepResourceId = a.getResourceId(attr, this.drawableCurrentStepResourceId)
                }else if (attr == R.styleable.StepperView_drawableNextStep) {
                    this.drawableNextStepResourceId = a.getResourceId(attr, this.drawableNextStepResourceId)
                }else if (attr == R.styleable.StepperView_drawablePrevLine) {
                    this.drawablePrevLineResourceId = a.getResourceId(attr, this.drawablePrevLineResourceId)
                }else if (attr == R.styleable.StepperView_drawableNextLine) {
                    this.drawableNextLineResourceId = a.getResourceId(attr, this.drawableNextLineResourceId)
                }
            }
            a.recycle()
        }
        this.drawablePrevStep = ContextCompat.getDrawable(context, this.drawablePrevStepResourceId)!!
        this.drawableCurrentStep = ContextCompat.getDrawable(context, this.drawableCurrentStepResourceId)!!
        this.drawableNextStep = ContextCompat.getDrawable(context, this.drawableNextStepResourceId)!!
        this.drawablePrevLine = ContextCompat.getDrawable(context, this.drawablePrevLineResourceId)!!
        this.drawableNextLine = ContextCompat.getDrawable(context, this.drawableNextLineResourceId)!!

        this.stepViews = arrayListOf()
        this.lineViews = arrayListOf()
        this.titleTextViews = arrayListOf()

        visibility = GONE
    }

    private fun build(count: Int){
        removeAllViews()
        val constraintSet = ConstraintSet()
        constraintSet.applyTo(this)
        stepViews.clear()
        lineViews.clear()
        titleTextViews.clear()
        for (i in 0 until count){
            addStepView(i)
            addLineView(i)
            addTitleTextView(i)
        }

        isBuilt = true
    }

    fun step(position: Int){
        if(count <= 0 || position >= count){
            return
        }
        if(!isBuilt){
            build(count)
        }
        for(i:Int in 0 until titleTextViews.size){
            if((showTitles == SHOW_ALL_TITLES || (i == position && showTitles == SHOW_CURRENT_TITLE)) &&  titleTextViews[i].text.isNotBlank()){
                titleTextViews[i].visibility = View.VISIBLE
            }else{
                titleTextViews[i].visibility = View.GONE
            }
            if(i <= position){
                if(i == position){
                    stepViews[i].background = drawableCurrentStep
                }else{
                    stepViews[i].background = drawablePrevStep
                }

            }else{
                stepViews.get(i).background = drawableNextStep
            }
            if(i > 0){
                if(i <= position){
                    lineViews[i-1].background = drawablePrevLine
                }else{
                    lineViews[i-1].background = drawableNextLine
                }
            }
        }
        currentPosition = position
        visibility = View.VISIBLE
        invalidate()
        requestLayout()
    }

    private fun addStepView(i: Int){
        val view = View(context).apply {
            id = generateViewIdCompat()
            if(stepDimensions != 0){
                layoutParams = ConstraintLayout.LayoutParams(stepDimensions, stepDimensions)
            }
        }
        addView(view)
        stepViews.add(view)
        val constraintSet = ConstraintSet()
        constraintSet.clone(this)
        constraintSet.connect(view.id, ConstraintSet.TOP, this.id, ConstraintSet.TOP, 0)
        if(i == 0){
            if(count == 1){
                constraintSet.connect(view.id, ConstraintSet.RIGHT, this.id, ConstraintSet.RIGHT, 0)
            }
            constraintSet.connect(view.id, ConstraintSet.LEFT, this.id, ConstraintSet.LEFT, 0)
        }else{
            val prevView = stepViews[i-1]
            constraintSet.connect(view.id, ConstraintSet.RIGHT, this.id, ConstraintSet.RIGHT, 0)
            if(i != count -1){
                constraintSet.connect(view.id, ConstraintSet.LEFT, prevView.id, ConstraintSet.RIGHT, 0)
            }
            if(i != 1){
                constraintSet.connect(prevView.id, ConstraintSet.RIGHT, view.id, ConstraintSet.LEFT, 0)
            }
        }
        constraintSet.applyTo(this)
    }

    private fun addLineView(i: Int){
        if(i == 0)
            return
        val view = View(context)
        addView(view)
        view.apply {
            id = generateViewIdCompat()
            layoutParams = ConstraintLayout.LayoutParams(0, lineHeight)
        }
        lineViews.add(view)
        val constraintSet = ConstraintSet()
        constraintSet.clone(this)
        constraintSet.connect(view.id, ConstraintSet.TOP, stepViews.get(i).id, ConstraintSet.TOP, 0)
        constraintSet.connect(view.id, ConstraintSet.BOTTOM, stepViews.get(i).id, ConstraintSet.BOTTOM, 0)
        constraintSet.connect(view.id, ConstraintSet.RIGHT, stepViews.get(i).id, ConstraintSet.LEFT, 0)
        constraintSet.connect(view.id, ConstraintSet.LEFT, stepViews.get(i-1).id, ConstraintSet.RIGHT, 0)


        constraintSet.applyTo(this)
    }

    private fun addTitleTextView(i: Int){
        val textView = (LayoutInflater.from(context).inflate(this.textViewTitleResourceId, this, false) as TextView).apply {
            id = generateViewIdCompat()
            if(titles != null && titles!!.size > i){
                text = titles!!.get(i)
            }
        }
        addView(textView)
        titleTextViews.add(textView)
        val constraintSet = ConstraintSet()
        constraintSet.clone(this)

        constraintSet.connect(textView.id, ConstraintSet.TOP, stepViews.get(i).id, ConstraintSet.BOTTOM, titleMarginTop)

        if(i == 0){
            if(count == 1){
                constraintSet.connect(textView.id, ConstraintSet.RIGHT, this.id, ConstraintSet.RIGHT, 0)
            }

            constraintSet.connect(textView.id, ConstraintSet.LEFT, this.id, ConstraintSet.LEFT, 0)
        }else{
            val prevTextView = titleTextViews[i-1]
            constraintSet.connect(textView.id, ConstraintSet.RIGHT, this.id, ConstraintSet.RIGHT, 0)
            if(i != count -1){
                constraintSet.connect(textView.id, ConstraintSet.LEFT, stepViews.get(i-1).id, ConstraintSet.RIGHT, 0)
            }
            if(i != 1){
                constraintSet.connect(prevTextView.id, ConstraintSet.RIGHT, stepViews.get(i).id, ConstraintSet.LEFT, 0)
            }
        }

        constraintSet.applyTo(this)
    }

    private val sNextGeneratedId = AtomicInteger(1)

    private fun generateViewIdCompat(): Int{
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            while (true) {
                val result = sNextGeneratedId.get()
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                var newValue = result + 1
                if (newValue > 0x00FFFFFF) newValue = 1 // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result
                }
            }
        } else {
            return View.generateViewId()
        }
    }

    private fun convertDpToPixel(context: Context, dp: Float): Float = dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)

    companion object {
        private val TAG = "StepperView"
        val SHOW_NONE = 0
        val SHOW_CURRENT_TITLE = 1
        val SHOW_ALL_TITLES = 2
    }
}