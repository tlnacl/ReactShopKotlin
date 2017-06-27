package com.tlnacl.reactiveapp.ui.widgets

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

class AspectRatioImageView : ImageView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @TargetApi(21) constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int,
                               defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val measuredHeight = (measuredWidth.toDouble() / 0.68636363).toInt()
        setMeasuredDimension(measuredWidth, measuredHeight)
    }
}
