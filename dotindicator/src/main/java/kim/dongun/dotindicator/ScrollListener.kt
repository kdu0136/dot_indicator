package kim.dongun.dotindicator

import androidx.recyclerview.widget.RecyclerView
import kotlin.math.floor

internal class ScrollListener(private val indicator: DotIndicator): RecyclerView.OnScrollListener() {
  private var midPos = 0
  private var scrollX = 0

  override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
    super.onScrolled(recyclerView, dx, dy)
    scrollX += dx
    recyclerView.getChildAt(0)?.width?.let {
      val midPos = floor(((scrollX + it / 2f) / it)).toInt()
      if (this.midPos != midPos) {
        when {
          this.midPos < midPos -> indicator.pageUp()
          else -> indicator.pageDown()
        }
      }
      this.midPos = midPos
    }
  }
}
