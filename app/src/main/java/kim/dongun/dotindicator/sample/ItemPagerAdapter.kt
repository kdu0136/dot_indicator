package kim.dongun.dotindicator.sample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter

class ItemPagerAdapter(private val items: ArrayList<ItemAdapter.Data>) : PagerAdapter() {

  override fun getCount() = items.size

  override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

  override fun instantiateItem(container: ViewGroup, position: Int): Any {
    val view = LayoutInflater
        .from(container.context)
        .inflate(R.layout.item_card, container, false)

    val item = items[position]
    val text: TextView = view.findViewById(R.id.text)

      text.text = item.text

    container.addView(view)
    return view
  }

  override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
    container.removeView(view as View)
  }
}