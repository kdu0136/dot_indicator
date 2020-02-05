package kim.dongun.dotindicator

import androidx.viewpager.widget.ViewPager

internal class PagerChangeListener(private val indicator: DotIndicator) : ViewPager.OnPageChangeListener {
  private var selectedPage = 0

  override fun onPageScrollStateChanged(state: Int) {}

  override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

  override fun onPageSelected(position: Int) {
    if (position != selectedPage) {
      when {
        this.selectedPage < position -> indicator.pageUp()
        else -> indicator.pageDown()
      }
    }
    selectedPage = position
  }
}
