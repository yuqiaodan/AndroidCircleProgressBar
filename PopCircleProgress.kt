//自己改一下包名和路径
package com.tomato.amelia.customviewstudy.view

//自己改一下包名和路径
import com.tomato.amelia.R

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Cap
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import kotlin.math.abs
import kotlin.math.max


/**
 * author: created by yuqiaodan on 2024/2/22 10:40
 * description:
 * 自定义View步骤：
 * 1.获取相关属性，定义相关属性 init 中实现
 * 2.测量自己的宽高
 * 3.创建画笔 根据具体绘制view的内容 按需要创建几支画笔 （也可以用一只画笔 每次绘制不同内容前修改其属性 例如颜色等）
 * 想象为准备n支不同颜色的笔 绘制不同部分。或者准备一支笔，绘制不同部分前确定其颜色。
 * 画笔有很多属性除了颜色外，还有抗锯齿等等设置，笔尖宽度等内容
 * 4.绘制View内容
 * 5.添加功能接口
 * 6.处理事件
 */
//kotlin 可以设置参数默认值 所以可以采用如下写法
class PopCircleProgress @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    //当前进度
    private var mProgress: Int

    //最大进度
    private var mMaxProgress: Int

    //进度条宽度
    private val mProgressWidth: Float

    //进度条颜色
    @ColorInt
    private val mMainProgressColor: Int

    //子进度条颜色
    @ColorInt
    private val mChildProgressColor: Int

    //是否显示子进度条
    private val mIsShowChildProgress: Boolean

    //子进度条和主进度条收尾距离
    private val mProgressRepelAngle: Float

    //轨道颜色
    @ColorInt
    private val mPathColor: Int

    //轨道padding
    private val mPathPadding: Float

    //是否为顺时针方向
    private val mIsClockwise: Boolean

    init {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.PopCircleProgress)
        mProgress = typeArray.getInt(R.styleable.PopCircleProgress_progress, 0)
        mMaxProgress = typeArray.getInt(R.styleable.PopCircleProgress_maxProgress, 100)
        mProgressWidth = typeArray.getDimension(R.styleable.PopCircleProgress_progressWidth, dip2px(context, 10f))
        mMainProgressColor = typeArray.getColor(R.styleable.PopCircleProgress_mainProgressColor, Color.parseColor("#E07A5F"))
        mChildProgressColor = typeArray.getColor(R.styleable.PopCircleProgress_childProgressColor, Color.parseColor("#81B29A"))
        mIsShowChildProgress = typeArray.getBoolean(R.styleable.PopCircleProgress_isShowChildProgress, true)
        mProgressRepelAngle = typeArray.getFloat(R.styleable.PopCircleProgress_progressRepelAngle, 0f)
        mPathColor = typeArray.getColor(R.styleable.PopCircleProgress_pathColor, Color.parseColor("#FFFFFF"))
        mPathPadding = typeArray.getDimension(R.styleable.PopCircleProgress_pathPadding, 0f)
        mIsClockwise = typeArray.getBoolean(R.styleable.PopCircleProgress_isClockwise, true)
        typeArray.recycle()

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        var d = max(width, height)
        if (d == 0) {
            d = dip2px(context, 30f).toInt()
        }
        setMeasuredDimension(d, d)
        initPaint()
        initRectF()
    }

    lateinit var mainProgressPaint: Paint
    lateinit var childProgressPaint: Paint
    lateinit var pathPaint: Paint
    fun initPaint() {
        //主进度条画笔
        mainProgressPaint = Paint()
        mainProgressPaint.color = mMainProgressColor
        mainProgressPaint.isAntiAlias = true
        mainProgressPaint.strokeCap = Cap.ROUND
        mainProgressPaint.style = Paint.Style.STROKE
        mainProgressPaint.strokeWidth = mProgressWidth

        //子进度条画笔
        childProgressPaint = Paint()
        childProgressPaint.color = mChildProgressColor
        childProgressPaint.isAntiAlias = true
        childProgressPaint.strokeCap = Cap.ROUND

        childProgressPaint.style = Paint.Style.STROKE
        childProgressPaint.strokeWidth = mProgressWidth

        //路径画笔
        pathPaint = Paint()
        pathPaint.color = mPathColor
        pathPaint.isAntiAlias = true
        pathPaint.strokeCap = Cap.ROUND
        pathPaint.style = Paint.Style.STROKE
        pathPaint.strokeWidth = mProgressWidth + mPathPadding

    }


    private lateinit var progressCircleRectF: RectF
    private fun initRectF() {
        val progressOffset = mProgressWidth / 2 + mPathPadding / 2
        progressCircleRectF =
            RectF(0f + progressOffset, 0f + progressOffset, measuredWidth.toFloat() - progressOffset, measuredWidth.toFloat() - progressOffset)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        //如果不是顺时针方向 则将画布水平翻转后再绘制内容
        if (!mIsClockwise) {
            // 水平翻转：将 X 轴方向上的缩放比例设置为 -1
            canvas.scale(-1f, 1f, measuredWidth / 2f, 0f);
            // 垂直翻转：将 Y 轴方向上的缩放比例设置为 -1
            // canvas.scale(1f, -1f, 0f,measuredHeight / 2f)
        }
        //绘制Path
        canvas.drawArc(progressCircleRectF, -90f, 360f, false, pathPaint)
        if (mProgress != 0) {
            //绘主制进度条
            val progressAngle = (mProgress.toFloat() / mMaxProgress.toFloat()) * 360
            canvas.drawArc(progressCircleRectF, -90f, progressAngle, false, mainProgressPaint)
            //绘制子进度条
            if (mIsShowChildProgress) {
                //圆环直径
                val d = abs(progressCircleRectF.left - progressCircleRectF.right)
                //圆环周长
                val circle = d * Math.PI
                //算出绘制进度条 圆角突出部分占用的角度 绘制子进度条时减去
                val roundAngle = 360f * ((mProgressWidth / 2 * 2) / circle.toFloat())
                //子进度条开始的位置角度
                val childStartAngle = progressAngle - 90f + roundAngle + mProgressRepelAngle
                //子进度条总弧度角度 画个草稿计算一下就知道怎么算了
                val childSweepAngle = 360f - progressAngle - roundAngle * 2 - mProgressRepelAngle * 2
                if (childSweepAngle > 0) {
                    canvas.drawArc(progressCircleRectF, childStartAngle, childSweepAngle, false, childProgressPaint)
                }
                Log.d("PopCircleProgress", "onDraw: childStartAngle: ${childStartAngle} childSweepAngle:$childSweepAngle")
            }
            Log.d("PopCircleProgress", "onDraw: ${progressAngle}")
        }
        canvas.restore()
    }

    private fun dip2px(context: Context, dp: Float): Float {
        return dp * context.resources.displayMetrics.density + 0.5f
    }

    fun setProgress(progress: Int) {
        mProgress = progress
        invalidate()
    }

    fun setMaxProgress(progress: Int) {
        mMaxProgress = progress
    }

    fun getProgress(): Int {
        return mProgress
    }

    fun getMaxProgress(): Int {
        return mMaxProgress
    }

    fun setProgressSmooth(startProgress: Int, endProgress: Int, during: Long) {
        val anim = ValueAnimator.ofInt(startProgress, endProgress)
        anim.interpolator = LinearInterpolator()
        anim.addUpdateListener {
            val value = it.animatedValue as Int
            setProgress(value)
        }
        anim.duration = during
        anim.repeatCount = ValueAnimator.INFINITE
        anim.start()
    }

}

