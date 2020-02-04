package kim.dongun.dotindicator

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import kim.dongun.dotindicator.DotManager.TargetScrollListener
import kotlin.math.max
import kotlin.math.min

open class PageIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), TargetScrollListener {

    private lateinit var dotSizes: IntArray
    private lateinit var dotAnimators: Array<ValueAnimator>

    private val defaultPaint = Paint().apply { isAntiAlias = true }
    private val selectedPaint = Paint().apply { isAntiAlias = true }

    private val dotSize: Int
    private val dotSizeMap: Map<DotManager.DotState, Int>
    private val dotBound: Int
    private val dotSpacing: Int
    private val animDuration: Long
    private val animInterpolator: Interpolator

    private lateinit var dotManager: DotManager
    private var scrollAmount: Int = 0
    private var scrollAnimator: ValueAnimator? = null
    private var startPadding: Int = 0

    private lateinit var scrollListener: RecyclerView.OnScrollListener
    private lateinit var pageChangeListener: ViewPager.OnPageChangeListener

    var count: Int = 0
        set(value) {
            dotManager = DotManager(
                count = value,
                visibleDotCnt = MOST_VISIBLE_COUNT,
                dotSize = dotSize,
                dotSpacing = dotSpacing,
                targetScrollListener = this)

            dotSizes = IntArray(value)
            dotManager.dots.forEachIndexed { index, dot ->
                dotSizes[index] = dotSizeMap[dot] ?: 0
            }
            dotAnimators = Array(value) { ValueAnimator() }

            startPadding = when {
                dotBound != 0 -> dotBound + dotSpacing
                else -> dotSpacing
            }

            field = value
            invalidate()
        }

    init {
        val ta = getContext().obtainStyledAttributes(attrs, R.styleable.PageIndicator)
        dotSizeMap = mapOf(
//        BYTE_6 to ta.getDimensionPixelSize(R.styleable.PageIndicator_piSize1, 12.dp),
            DotManager.DotState.SELECT to ta.getDimensionPixelSize(R.styleable.PageIndicator_piSize2, 13f.dp),
            DotManager.DotState.UNSELECT to ta.getDimensionPixelSize(R.styleable.PageIndicator_piSize3, 10f.dp),
            DotManager.DotState.MEDIUM to ta.getDimensionPixelSize(R.styleable.PageIndicator_piSize4, 8f.dp),
            DotManager.DotState.SMALL to ta.getDimensionPixelSize(R.styleable.PageIndicator_piSize5, 6f.dp),
            DotManager.DotState.GONE to 0.dp
        )
        dotSize = dotSizeMap.values.max() ?: 0
        dotSpacing = ta.getDimensionPixelSize(R.styleable.PageIndicator_piDotSpacing, 4.dp)
        dotBound = ta.getDimensionPixelSize(R.styleable.PageIndicator_piDotBound, 0.dp)

        animDuration = ta.getInteger(
            R.styleable.PageIndicator_piAnimDuration, DEFAULT_ANIM_DURATION).toLong()
        defaultPaint.color = ta.getColor(
            R.styleable.PageIndicator_piDefaultColor,
            ContextCompat.getColor(getContext(), R.color.pi_default_color))
        selectedPaint.color = ta.getColor(
            R.styleable.PageIndicator_piSelectedColor,
            ContextCompat.getColor(getContext(), R.color.pi_selected_color))
        animInterpolator = AnimationUtils.loadInterpolator(context, ta.getResourceId(
            R.styleable.PageIndicator_piAnimInterpolator,
            R.anim.pi_default_interpolator))
        ta.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // FIXME: add support for `match_parent`
        setMeasuredDimension(MOST_VISIBLE_COUNT * (dotSize + dotSpacing) + dotBound + startPadding, dotSize)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        var paddingStart = startPadding
        val (start, end) = getDrawingRange()

        paddingStart += (dotSize + dotSpacing) * start
        (start until end).forEach {
            canvas?.drawCircle(
                paddingStart + dotSize / 2f - scrollAmount,
                dotSize / 2f,
                dotSizes[it] / 2f,
                when (dotManager.dots[it]) {
                    BYTE_5 -> selectedPaint
                    else -> defaultPaint
                })
            paddingStart += dotSize + dotSpacing
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        if (superState == null) {
            return superState
        }

        val savedState = SavedState(superState)
        savedState.count = this.count
        savedState.selectedIndex = this.dotManager.selectedIndex
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }

        super.onRestoreInstanceState(state.superState)

        this.count = state.count
        for (i in 0 until state.selectedIndex) {
            swipeNext()
        }
    }

    override fun scrollToTarget(target: Int) {
        scrollAnimator?.cancel()
        scrollAnimator = ValueAnimator.ofInt(scrollAmount, target).apply {
            duration = animDuration
            interpolator = DEFAULT_INTERPOLATOR
            addUpdateListener { animation ->
                scrollAmount = animation.animatedValue as Int
                invalidate()
            }
            start()
        }
    }

    infix fun attachTo(recyclerView: RecyclerView) {
        if (::scrollListener.isInitialized) {
            recyclerView.removeOnScrollListener(scrollListener)
        }
        count = recyclerView.adapter?.itemCount ?: 0
        scrollListener = ScrollListener(this)
        recyclerView.addOnScrollListener(scrollListener)
        scrollToTarget(0)
    }

    infix fun attachTo(viewPager: ViewPager) {
        if (::pageChangeListener.isInitialized) {
            viewPager.removeOnPageChangeListener(pageChangeListener)
        }
        count = (viewPager.adapter as PagerAdapter).count
        pageChangeListener = PageChangeListener(this)
        viewPager.addOnPageChangeListener(pageChangeListener)
        scrollToTarget(0)
    }

    fun swipePrevious() {
        dotManager.goToPrevious()
        animateDots()
    }

    fun swipeNext() {
        dotManager.goToNext()
        animateDots()
    }

    private fun animateDots() {
        dotManager.let {
            val (start, end) = getDrawingRange()
            (start until end).forEach { index ->
                dotAnimators[index].cancel()
                dotAnimators[index] = ValueAnimator.ofInt(dotSizes[index], dotSizeMap[(it.dots[index])] ?: 0)
                    .apply {
                        duration = animDuration
                        interpolator = DEFAULT_INTERPOLATOR
                        addUpdateListener { animation ->
                            dotSizes[index] = animation.animatedValue as Int
                            invalidate()
                        }
                    }
                dotAnimators[index].start()
            }
        }
    }

    private fun getDrawingRange(): Pair<Int, Int> =
        Pair(max(0, dotManager.scrollStartIndex - 1), min(count, dotManager.scrollEndIndex + 2))

    private fun dotSize(state: DotManager.DotState) = dotSizeMap[state] ?: 0

    companion object {
//        private const val BYTE_5 = 5.toByte()
//        private const val BYTE_4 = 4.toByte()
//        private const val BYTE_3 = 3.toByte()
//        private const val BYTE_2 = 2.toByte()
//        private const val BYTE_1 = 1.toByte()

        private const val MOST_VISIBLE_COUNT = 5
        private const val DEFAULT_ANIM_DURATION = 300

        private val DEFAULT_INTERPOLATOR = DecelerateInterpolator()
    }
}