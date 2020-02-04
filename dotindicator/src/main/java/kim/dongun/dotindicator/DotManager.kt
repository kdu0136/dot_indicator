package kim.dongun.dotindicator

import android.util.Log

internal class DotManager(
  count: Int,
  private val visibleDotCnt: Int,
  private val dotSize: Int,
  private val dotSpacing: Int,
  private val targetScrollListener: TargetScrollListener? = null
) {
  enum class DotState(value: Byte) { SELECT(value = 4), UNSELECT(value = 3), MEDIUM(value = 2), SMALL(value = 1), GONE(value = 0) }

  internal var dots: ByteArray = ByteArray(count) // save each dot size (1~6 -> visible & 0 -> invisible)
  internal var selectedIndex = 0

  private var scrollAmount = 0
  var scrollStartIndex = 0
  var scrollEndIndex = visibleDotCnt - 1

  private val dotSizeArray: ByteArray = byteArrayOf(5, 4, 4, 3, 2, 1)

  init {
    if (count > 0) {
      dots[0] = 5
    }

    // 최대 노출 dot 보다 작거나 같을 경우 -> 나머지 dot size = 4
    if (count <= visibleDotCnt) {
      (1 until count).forEach { i -> dots[i] = 4 }
    } else { // 최대 노출 dot 보다 클 경우
      // dot index 1~2 까지 dot size = 5
      // etc dot size decrease minus 1
      setDotSize()
    }
    Log.d("test dot init", dots())
  }

  internal fun dots() = dots.joinToString(" ") + " selectedIndex $selectedIndex"

//  fun dotSizeFor(size: Byte) = dotStates[size] ?: 0

  fun goToNext() {
    if (selectedIndex >= dots.size - 1) {
      return
    }

    ++selectedIndex

    if (dots.size <= visibleDotCnt) {
      goToNextSmall()
    } else {
      goToNextLarge()
    }
    Log.d("test goToNext", "scrollStartIndex: $scrollStartIndex scrollEndIndex: $scrollEndIndex")
    Log.d("test dot goToNext", dots())
  }

  fun goToPrevious() {
    if (selectedIndex == 0) {
      return
    }

    --selectedIndex

    if (dots.size <= visibleDotCnt) {
      goToPreviousSmall()
    } else {
      goToPreviousLarge()
    }
    Log.d("test goToPrevious", "scrollStartIndex: $scrollStartIndex scrollEndIndex: $scrollEndIndex")
    Log.d("test dot goToPrevious", dots())
  }

  private fun goToNextSmall() {
    dots[selectedIndex] = 5
    dots[selectedIndex - 1] = 4
  }

  private fun goToNextLarge() {
    setDotSize()

    if (selectedIndex < dots.size - 1 && selectedIndex == scrollEndIndex) {
      Log.d("test up", "selectedIndex: $selectedIndex")
      scrollStartIndex++
      scrollEndIndex++
      scrollAmount += dotSize + dotSpacing
      targetScrollListener?.scrollToTarget(scrollAmount)
    }
  }

  private fun goToPreviousSmall() {
    dots[selectedIndex] = 6
    dots[selectedIndex + 1] = 5
  }

  private fun goToPreviousLarge() {
    setDotSize()

    if (selectedIndex > 0 && selectedIndex == scrollStartIndex) {
      Log.d("test down", "selectedIndex: $selectedIndex")
      scrollStartIndex--
      scrollEndIndex--
      scrollAmount -= dotSize + dotSpacing
      targetScrollListener?.scrollToTarget(scrollAmount)
    }
  }

  /**
   * set dot size
   *
   * ex) selected index = 6 -> dots = [0 1 2 3 4 4 5 4 4 3 2 1 0 0]
   */
  private fun setDotSize() {
    (selectedIndex until dots.size).forEach { i ->
      dots[i] = if (i - selectedIndex < dotSizeArray.size) dotSizeArray[i - selectedIndex] else 0
    }
    (selectedIndex - 1 downTo 0).forEach { i ->
      dots[i] = if (selectedIndex - i < dotSizeArray.size) dotSizeArray[selectedIndex - i] else 0
    }
  }

  interface TargetScrollListener {
    fun scrollToTarget(target: Int)
  }
}