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
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.max
import kotlin.math.min

class DotIndicator @JvmOverloads constructor(context: Context,
                                             attrs: AttributeSet? = null,
                                             defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr), DotManager.TargetScrollListener {
    private lateinit var dotAnimators: Array<ValueAnimator>

    private val defaultPaint = Paint().apply { isAntiAlias = true }
    private val selectedPaint = Paint().apply { isAntiAlias = true }

    private val maxDotSize: Int
    private val dotStateMap: Map<DotManager.DotState, Int>
    private val indicatorPadding: Int
    private val dotSpacing: Int
    private val animDuration: Long
    private val animInterpolator: Interpolator

    private lateinit var dotManager: DotManager
    private var scrollAmount: Int = 0
    private var scrollAnimator: ValueAnimator? = null
    private var startPadding: Int = 0

    private lateinit var scrollListener: RecyclerView.OnScrollListener
    private lateinit var pagerChangeListener: ViewPager.OnPageChangeListener
    private lateinit var pager2ChangeListener: ViewPager2.OnPageChangeCallback

    var count: Int = 0
        set(value) {
            dotManager = DotManager(
                count = value,
                visibleDotCnt = MOST_VISIBLE_COUNT,
                dotPadding = maxDotSize + dotSpacing,
                targetScrollListener = this)

            dotAnimators = Array(value) { ValueAnimator() }

            startPadding = when {
                indicatorPadding != 0 -> indicatorPadding + dotSpacing
                else -> dotSpacing
            }

            field = value
            invalidate()
        }

    init {
        val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.DotIndicator)
        dotStateMap = mapOf(
            DotManager.DotState.SELECT to typedArray.getDimensionPixelSize(R.styleable.DotIndicator_dotSelectSize, 8f.dp),
            DotManager.DotState.LARGE to typedArray.getDimensionPixelSize(R.styleable.DotIndicator_dotLargeSize, 6.5f.dp),
            DotManager.DotState.MEDIUM to typedArray.getDimensionPixelSize(R.styleable.DotIndicator_dotMediumSize, 4.5f.dp),
            DotManager.DotState.SMALL to typedArray.getDimensionPixelSize(R.styleable.DotIndicator_dotSmallSize, 3f.dp),
            DotManager.DotState.GONE to 0.dp
        )
        maxDotSize = dotStateMap.values.max() ?: 0
        dotSpacing = typedArray.getDimensionPixelSize(R.styleable.DotIndicator_dotSpacing, 4.dp)
        indicatorPadding = typedArray.getDimensionPixelSize(R.styleable.DotIndicator_indicatorPadding, 0.dp)

        animDuration = typedArray.getInteger(
            R.styleable.DotIndicator_indicatorAnimDuration, DEFAULT_ANIM_DURATION).toLong()
        defaultPaint.color = typedArray.getColor(
            R.styleable.DotIndicator_dotDefaultColor,
            ContextCompat.getColor(getContext(), R.color.pi_default_color))
        selectedPaint.color = typedArray.getColor(
            R.styleable.DotIndicator_dotSelectedColor,
            ContextCompat.getColor(getContext(), R.color.pi_selected_color))
        animInterpolator = AnimationUtils.loadInterpolator(context, typedArray.getResourceId(
            R.styleable.DotIndicator_indicatorAnimInterpolator,
            R.anim.pi_default_interpolator))
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(min(count, MOST_VISIBLE_COUNT) * (maxDotSize + dotSpacing) + indicatorPadding + startPadding, maxDotSize)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // draw init dots
        var paddingStart = startPadding
        val (start, end) = getDrawingRange()

        paddingStart += (maxDotSize + dotSpacing) * start
        (start until end).forEach {
            canvas?.drawCircle(
                paddingStart + maxDotSize / 2f - scrollAmount, // x
                maxDotSize / 2f, // y
                dotSize(state = dotManager.dots[it]) / 2f, // radius
                when (dotManager.dots[it]) {
                    DotManager.DotState.SELECT -> selectedPaint
                    else -> defaultPaint
                }) // paint
            paddingStart += maxDotSize + dotSpacing
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        if (superState == null) {
            return superState
        }

        val savedState = SavedState(superState = superState)
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
            pageUp()
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

    /**
     * attach to recyclerView
     */
    infix fun attachTo(recyclerView: RecyclerView) {
        if (::scrollListener.isInitialized) {
            recyclerView.removeOnScrollListener(scrollListener)
        }
        count = recyclerView.adapter?.itemCount ?: 0
        scrollListener = ScrollListener(indicator = this)
        recyclerView.addOnScrollListener(scrollListener)
        scrollToTarget(0)
    }

    /**
     * attach to viewPager
     */
    infix fun attachTo(viewPager: ViewPager) {
        if (::pagerChangeListener.isInitialized) {
            viewPager.removeOnPageChangeListener(pagerChangeListener)
        }
        count = (viewPager.adapter as PagerAdapter).count
        pagerChangeListener = PagerChangeListener(indicator = this)
        viewPager.addOnPageChangeListener(pagerChangeListener)
        scrollToTarget(0)
    }

    /**
     * attach to viewPager2
     */
    infix fun attachTo(viewPager: ViewPager2) {
        if (::pager2ChangeListener.isInitialized) {
            viewPager.unregisterOnPageChangeCallback(pager2ChangeListener)
        }
        count = viewPager.adapter?.itemCount ?: 0
        pager2ChangeListener = Pager2ChangeListener(indicator = this)
        viewPager.registerOnPageChangeCallback(pager2ChangeListener)
        scrollToTarget(0)
    }

    fun pageUp() {
        dotManager.onPageUp()
        animateDots()
    }

    fun pageDown() {
        dotManager.onPageDown()
        animateDots()
    }

    /**
     * dot size change animation
     */
    private fun animateDots() {
        dotManager.let {
            val (start, end) = getDrawingRange()
            (start until end).forEach { index ->
                dotAnimators[index].cancel()
                dotAnimators[index] = ValueAnimator.ofInt(dotSize(state = dotManager.dots[index]), dotSize(state = it.dots[index])).apply {
                    duration = animDuration
                    interpolator = DEFAULT_INTERPOLATOR
                    addUpdateListener { invalidate() }
                }
                dotAnimators[index].start()
            }
        }
    }

    /**
     * get drawing dot indicator range
     */
    private fun getDrawingRange(): Pair<Int, Int> =
        Pair(max(0, dotManager.scrollStartIndex - 1), min(count, dotManager.scrollEndIndex + 2))

    /**
     * get dot size
     *
     * @param state dot state
     *
     * @return dot size for the state
     */
    private fun dotSize(state: DotManager.DotState) = dotStateMap[state] ?: 0.dp

    companion object {
        private const val MOST_VISIBLE_COUNT = 6
        private const val DEFAULT_ANIM_DURATION = 300

        private val DEFAULT_INTERPOLATOR = DecelerateInterpolator()
    }
}