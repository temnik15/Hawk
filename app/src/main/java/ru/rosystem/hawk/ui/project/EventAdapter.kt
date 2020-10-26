package ru.rosystem.hawk.ui.project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_project.view.*
import ru.rosystem.hawk.R
import ru.rosystem.hawk.fragment.ProjectsList


// Not completely figured out the essence of events, so onBind is almost empty
class EventAdapter() : RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    private var items: List<ProjectsList.Event> = emptyList()
    fun updateItems(_items: List<ProjectsList.Event>) {
        items = _items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_project_info, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val title = itemView.view_title
        fun bind(item: ProjectsList.Event) {
            title.text=item.fragments().eventsList().payload().title()
        }
    }
}