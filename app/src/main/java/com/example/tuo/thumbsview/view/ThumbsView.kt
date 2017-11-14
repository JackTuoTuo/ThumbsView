package com.example.tuo.thumbsview.view

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.example.tuo.thumbsview.R

/**
 * @describe TODO
 * 作者：Tuo on 2017/10/23 17:08
 * 邮箱：839539179@qq.com
 */
class ThumbsView(mContext: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : View(mContext, attributeSet, defStyleAttr) {

    // 存储分割后的文字
    private var array = arrayOf("", "", "")

    private val minWidth = dip2px(100f)
    private var minHeight = dip2px(100f)

    // 文字开始画的地方
    private var startX: Float = 0f

    // 文字移动距离
    private var textOffset: Float = 0f

    private var mCount: Int = 0

    private var mTextSize: Float

    private var mNoChangeTextColor: Int

    private var mChangedTextColor: Int

    private var mTextSpace: Float

    private var mStep: Int

    // 变化模式  0 为全部变化 1 为部分变化
    private var mChangeMode: Int

    private var yOffset: Float = 0f

    @Suppress("unused")
    fun setYOffset(yOffset: Float) {
        this.yOffset = yOffset
        invalidate()
    }

    @Suppress("unused")
    fun getYOffset() = yOffset


    // 标记是否已经点赞
    private var hasThumbs: Boolean = false

    // 文字高度一半
    private var halfOfTextHeight: Float = 0f


    // 画笔
    private var mPaint: Paint


    constructor(mContext: Context) : this(mContext, null)

    constructor(mContext: Context, attributeSet: AttributeSet?) : this(mContext, attributeSet, 0)


    init {
        // 初始自定义属性
        val array: TypedArray = mContext.obtainStyledAttributes(attributeSet, R.styleable.ThumbsView, defStyleAttr, 0)
        mCount = array.getInteger(R.styleable.ThumbsView_tv_count, 0)
        mTextSize = array.getFloat(R.styleable.ThumbsView_tv_text_size, sp2px(16f))
        mNoChangeTextColor = array.getColor(R.styleable.ThumbsView_tv_no_change_text_color, Color.GRAY)
        mChangedTextColor = array.getColor(R.styleable.ThumbsView_tv_changed_text_color, Color.GRAY)
        mTextSpace = array.getFloat(R.styleable.ThumbsView_tv_text_space, dip2px(1.5f))
        mStep = array.getInteger(R.styleable.ThumbsView_tv_step, 1)
        mChangeMode = array.getInt(R.styleable.ThumbsView_tv_change_mode, 1)
        array.recycle()

        // 初始画笔
        mPaint = Paint()
        mPaint.textSize = mTextSize
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        initMinHeight()
        setMeasuredDimension(initWidth(widthMeasureSpec), initHeight(heightMeasureSpec))
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 模式为全部改变时 间隙为0f
        if (mChangeMode == 0) {
            mTextSpace = 0f
        }



        initText()
        drawText(canvas)


    }

    private fun initMinHeight() {
        var fontMetrics = mPaint.fontMetrics

        minHeight = (fontMetrics.bottom - fontMetrics.top)

    }

    private fun initText() {

        calculateChangeNum(mStep)

    }


    private fun drawText(canvas: Canvas) {

        var fontMetrics = mPaint.fontMetrics

        // 文字基线y轴坐标 为了 让文字 垂直居中
        val baseLineY = height / 2 - fontMetrics.top / 2 - fontMetrics.bottom / 2

        halfOfTextHeight = (fontMetrics.bottom - fontMetrics.top) / 2


        textOffset = (halfOfTextHeight + height / 2)

        // 此线是为了 验证 文字是不是垂直居中
//        canvas.drawLine(0f, height / 2.toFloat(), width.toFloat(), height / 2.toFloat(), mPaint)

        // 为了显示效果 根据是否是全部改变 设置不同的绘制方式

        if (mChangeMode === 0) {

            mPaint.color = mChangedTextColor

            canvas.drawText(array[1], width / 2.toFloat() - halfTextWidth(array[1]), baseLineY + yOffset, mPaint)
            canvas.drawText(array[2], width / 2.toFloat() - halfTextWidth(array[2]), baseLineY + height / 2 + +halfOfTextHeight + yOffset, mPaint)

        } else if (mChangeMode === 1) {

            // 获取部分改变的模式时的绘制文字其实起始位置
            startX = width / 2.toFloat() - (2 * halfTextWidth(array[0]) + mTextSpace + 2 * halfTextWidth(array[1])) / 2

            mPaint.color = mNoChangeTextColor
            canvas.drawText(array[0], startX, baseLineY, mPaint)

            mPaint.color = mChangedTextColor
            canvas.drawText(array[1], startX + 2 * halfTextWidth(array[0]) + mTextSpace, baseLineY + yOffset, mPaint)
            canvas.drawText(array[2], startX + 2 * halfTextWidth(array[0]) + mTextSpace, baseLineY + height / 2 + +halfOfTextHeight + yOffset, mPaint)
        }
    }

    /**
     * 计算文字一半的宽度
     */
    private fun halfTextWidth(text: String): Float {
        val bounds = Rect()
        mPaint.getTextBounds(text, 0, text.length, bounds)

        return (bounds.right - bounds.left).toFloat() / 2
    }


    fun setCount(count: Int) {
        this.mCount = count
        invalidate()
    }


    /**
     * 计算不变，原来，和改变后各部分的数字
     */
    private fun calculateChangeNum(change: Int) {

        val oldNum = mCount.toString()
        val newNum = (mCount + change).toString()

        val oldNumLen = oldNum.length
        val newNumLen = newNum.length


        // 0为全部变化  1 为部分变化
        if (mChangeMode === 0) {
            array[0] = ""
            array[1] = oldNum
            array[2] = newNum
        } else {
            if (oldNumLen != newNumLen) {
                array[0] = ""
                array[1] = oldNum
                array[2] = newNum
                // 鉴于此种情况 也将模式设置为 全部转换
                mChangeMode = 0
            } else {
                for (i in 0 until oldNumLen) {
                    val oldC1 = oldNum[i]
                    val newC1 = newNum[i]
                    if (oldC1 != newC1) {
                        if (i == 0) {
                            array[0] = ""
                        } else {
                            array[0] = newNum.substring(0, i)
                        }
                        array[1] = oldNum.substring(i)
                        array[2] = newNum.substring(i)
                        break
                    }
                }
            }
        }
    }


    private fun initWidth(widthMeasureSpec: Int): Int {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        return if (widthMode != View.MeasureSpec.EXACTLY) {
            minWidth.toInt()
        } else {
            Math.max(minWidth.toInt(), widthSize)
        }

    }


    private fun initHeight(heightMeasureSpec: Int): Int {

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        return if (heightMode != View.MeasureSpec.EXACTLY)
            minHeight.toInt()
        else {
            Math.max(minHeight.toInt(), heightSize)
        }

    }


    fun show() {

        hasThumbs = if (hasThumbs) {
            val animator = ObjectAnimator.ofFloat(this, "yOffset", -textOffset, 0f)
            animator.duration = 500
            animator.start()
            false
        } else {
            val animator = ObjectAnimator.ofFloat(this, "yOffset", 0f, -textOffset)
            animator.duration = 500
            animator.start()
            true
        }
    }


    private fun dip2px(dpValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f)
    }


    private fun sp2px(spValue: Float): Float {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f)
    }

}