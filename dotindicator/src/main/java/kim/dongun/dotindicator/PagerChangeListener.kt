package kim.dongun.dotindicator

import androidx.viewpager.widget.ViewPager
import kotlin.math.abs

internal class PagerChangeListener(private val indicator: DotIndicator) : ViewPager.OnPageChangeListener {
  private var selectedPage = 0

  override fun onPageScrollStateChanged(state: Int) {}

  override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

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
