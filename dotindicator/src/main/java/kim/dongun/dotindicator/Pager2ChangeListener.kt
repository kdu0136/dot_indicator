package kim.dongun.dotindicator

import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

internal class Pager2ChangeListener(private val indicator: DotIndicator): ViewPager2.OnPageChangeCallback() {
  private var selectedPage = 0

  override fun onPageSelected(position: Int) {
    if (position != selectedPage) {
      val diff = abs(selectedPage - position)
      (0 until diff).forEach { _ ->
        if (selectedPage < position) indicator.pageUp()
        else indicator.pageDown()
      }
    }
    selectedPage = position
  }
}
