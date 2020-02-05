package kim.dongun.dotindicator

internal class DotManager(count: Int,
                          private val visibleDotCnt: Int,
                          private val dotPadding: Int,
                          private val targetScrollListener: TargetScrollListener) {
  enum class DotState(val value: Byte) { SELECT(value = 4), LARGE(value = 3), MEDIUM(value = 2), SMALL(value = 1), GONE(value = 0) }

  internal var dots: Array<DotState> = Array(count) { DotState.LARGE } // save each dot state
  internal var selectedIndex = 0

  private var scrollAmount = 0
  var scrollStartIndex = 0
  var scrollEndIndex = visibleDotCnt - 1

  private val dotSizeArray: ArrayList<DotState> = ArrayList()

  init {
    if (count > 0) dots[0] = DotState.SELECT

    // init visible dot size array
    // ex) count 4 -> SELECT LARGE MEDIUN SMALL / count 6 -> SELECT LARGE LARGE MEDIUM MEDIUM SMALL
    val unSelectIndex = (visibleDotCnt-1) / 2
    (0 until visibleDotCnt).forEach { i ->
      val state = when (true) {
        i == 0 -> DotState.SELECT
        unSelectIndex >= i -> DotState.LARGE
        i == visibleDotCnt - 1 -> DotState.SMALL
        else -> DotState.MEDIUM
      }
      dotSizeArray.add(state)
    }

    if (count > visibleDotCnt) setDotSize()
  }

  /**
   * page up
   */
  fun onPageUp() {
    if (selectedIndex >= dots.size - 1) return

    selectedIndex++

    if (dots.size <= visibleDotCnt) onPageUpNormal()
    else onPageUpWithAnimation()
  }

  /**
   * page down
   */
  fun onPageDown() {
    if (selectedIndex == 0) return

    selectedIndex--

    if (dots.size <= visibleDotCnt) onPageDownNormal()
    else onPageDownWithAnimation()
  }

  /**
   * page up - dot size under visible dot count
   */
  private fun onPageUpNormal() {
    dots[selectedIndex] = DotState.SELECT
    dots[selectedIndex - 1] = DotState.LARGE
  }

  /**
   * page up - dot size over visible dot count
   */
  private fun onPageUpWithAnimation() {
    setDotSize()

    // page up animation
    if (selectedIndex < dots.size - 1 && selectedIndex == scrollEndIndex) {
      scrollStartIndex++
      scrollEndIndex++
      scrollAmount += dotPadding
      targetScrollListener.scrollToTarget(scrollAmount)
    }
  }

  /**
   * page down - dot size under visible dot count
   */
  private fun onPageDownNormal() {
    dots[selectedIndex] = DotState.SELECT
    dots[selectedIndex + 1] = DotState.LARGE
  }

  /**
   * page down - dot size over visible dot count
   */
  private fun onPageDownWithAnimation() {
    setDotSize()

    // page down animation
    if (selectedIndex > 0 && selectedIndex == scrollStartIndex) {
      scrollStartIndex--
      scrollEndIndex--
      scrollAmount -= dotPadding
      targetScrollListener.scrollToTarget(scrollAmount)
    }
  }

  /**
   * set dot size
   */
  private fun setDotSize() {
    (selectedIndex until dots.size).forEach { i ->
      dots[i] = if (i - selectedIndex < dotSizeArray.size) dotSizeArray[i - selectedIndex] else DotState.GONE
    }
    (selectedIndex - 1 downTo 0).forEach { i ->
      dots[i] = if (selectedIndex - i < dotSizeArray.size) dotSizeArray[selectedIndex - i] else DotState.GONE
    }
  }

  interface TargetScrollListener {
    fun scrollToTarget(target: Int)
  }
}