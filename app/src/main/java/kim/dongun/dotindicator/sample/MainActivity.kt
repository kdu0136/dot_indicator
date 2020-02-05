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
        recyclerView.adapter = adapter
        LinearSnapHelper().attachToRecyclerView(recyclerView)
        dotIndicator attachTo recyclerView

        // ViewPager
        val pagerAdapter = ItemPagerAdapter(items = items)
        viewPager.adapter = pagerAdapter
        pagerDotIndicator attachTo viewPager

        // ViewPager2
        val pager2Adapter = ItemAdapter().apply {
            updateData(updateData = items)
        }
        viewPager2.adapter = pager2Adapter
        pager2DotIndicator attachTo viewPager2

        // Button
        buttonDotIndicator.count = 20
        downBtn.setOnClickListener { buttonDotIndicator.pageDown() }
        upBtn.setOnClickListener { buttonDotIndicator.pageUp() }
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
