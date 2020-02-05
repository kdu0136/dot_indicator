package kim.dongun.dotindicator

import androidx.viewpager2.widget.ViewPager2

internal class Pager2ChangeListener(private val indicator: DotIndicator): ViewPager2.OnPageChangeCallback() {
  private var selectedPage = 0

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
