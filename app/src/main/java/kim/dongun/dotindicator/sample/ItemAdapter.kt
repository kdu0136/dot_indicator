package kim.dongun.dotindicator.sample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kim.dongun.dotindicator.sample.ItemAdapter.ItemViewHolder

class ItemAdapter: RecyclerView.Adapter<ItemViewHolder>() {
    private val items: ArrayList<Data> = ArrayList()

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemViewHolder(view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false))

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(item = items[holder.adapterPosition])
    }

    fun updateData(updateData: ArrayList<Data>) {
        this.items.clear()
        this.items.addAll(updateData)
        notifyDataSetChanged()
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val text: TextView = view.findViewById(R.id.text)

        fun bind(item: Data) {
            text.text = item.text
        }
    }

    data class Data(val text: String)
}