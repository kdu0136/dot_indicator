package kim.dongun.dotindicator.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearSnapHelper
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val picasso = Picasso.Builder(this).build()

        // RecyclerView
        val adapter = MyAdapter(picasso)
        list.adapter = adapter
        LinearSnapHelper().attachToRecyclerView(list)
        adapter.swapData(LIST_ITEMS)
        pageIndicator attachTo list
        pageIndicator2 attachTo list

        // ViewPager
        val myPagerAdapter = MyPagerAdapter(picasso, LIST_ITEMS)
        pager.adapter = myPagerAdapter
        pagerPageIndicator attachTo pager

        // Manual
        manualPageIndicator.count = 20
        leftBtn.setOnClickListener { manualPageIndicator.swipePrevious() }
        rightBtn.setOnClickListener { manualPageIndicator.swipeNext() }
    }

    companion object {
        private val LIST_ITEMS = listOf(
            MyAdapter.MyItem(
                "Cormorant fishing at sunset",
                "Patryk Wojciechowicz",
                "https://cdn.dribbble.com/users/3178178/screenshots/6287074/cormorant_fishing_1600x1200_final_04_05_2019_4x.jpg"
            ),
            MyAdapter.MyItem(
                "Mountain House",
                "Alex Pasquarella",
                "https://cdn.dribbble.com/users/989466/screenshots/6100954/cabin-2-dribbble-alex-pasquarella_4x.png"),
            MyAdapter.MyItem(
                "journey",
                "Febin_Raj",
                "https://cdn.dribbble.com/users/1803663/screenshots/6163551/nature-4_4x.png"),
            MyAdapter.MyItem(
                "Explorer",
                "Uran",
                "https://cdn.dribbble.com/users/1355613/screenshots/6441984/landscape_4x.jpg"),
            MyAdapter.MyItem(
                "Fishers Peak Limited Edition Print",
                "Brian Edward Miller ",
                "https://cdn.dribbble.com/users/329207/screenshots/6128300/bemocs_fisherspeak_dribbble.jpg"),
            MyAdapter.MyItem(
                "First Man",
                "Lana Marandina",
                "https://cdn.dribbble.com/users/1461762/screenshots/6280906/first_man_lana_marandina_4x.png"),
            MyAdapter.MyItem(
                "First Man",
                "Lana Marandina",
                "https://cdn.dribbble.com/users/1461762/screenshots/6280906/first_man_lana_marandina_4x.png"),
            MyAdapter.MyItem(
                "First Man",
                "Lana Marandina",
                "https://cdn.dribbble.com/users/1461762/screenshots/6280906/first_man_lana_marandina_4x.png"),
            MyAdapter.MyItem(
                "First Man",
                "Lana Marandina",
                "https://cdn.dribbble.com/users/1461762/screenshots/6280906/first_man_lana_marandina_4x.png"),
            MyAdapter.MyItem(
                "First Man",
                "Lana Marandina",
                "https://cdn.dribbble.com/users/1461762/screenshots/6280906/first_man_lana_marandina_4x.png"),
            MyAdapter.MyItem(
                "On The Road Again",
                "Brian Edward Miller",
                "https://cdn.dribbble.com/users/329207/screenshots/6522800/2026_nationwide_02_train_landscape_v01.00.jpg")
        )
    }
}
