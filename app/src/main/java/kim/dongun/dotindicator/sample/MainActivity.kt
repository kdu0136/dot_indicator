package kim.dongun.dotindicator.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearSnapHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val items = makeItem(count = 10)

        // RecyclerView
        val adapter = ItemAdapter().apply {
            updateData(updateData = items)
        }
        list.adapter = adapter
        LinearSnapHelper().attachToRecyclerView(list)
        pageIndicator attachTo list

        // ViewPager
        val pagerAdapter = ItemPagerAdapter(items = items)
        pager.adapter = pagerAdapter
        pagerPageIndicator attachTo pager

        // ViewPager2
        val pager2Adapter = ItemAdapter().apply {
            updateData(updateData = items)
        }
        pager2.adapter = pager2Adapter
        pager2PageIndicator attachTo pager2

        // Button
        manualPageIndicator.count = 20
        leftBtn.setOnClickListener { manualPageIndicator.pageDown() }
        rightBtn.setOnClickListener { manualPageIndicator.pageUp() }
    }

    companion object {
        fun makeItem(count: Int): ArrayList<ItemAdapter.Data> {
            val dataList = ArrayList<ItemAdapter.Data>()
            for (i in 0 until count) {
                dataList.add(ItemAdapter.Data(text = (i+1).toString()))
            }
            return dataList
        }
    }
}
