package ru.rosystem.hawk.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_project.view.*
import ru.rosystem.hawk.R
import ru.rosystem.hawk.WorkspacesQuery

class ProjectAdapter(private val listener:OnClickProject) : RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {

    private var items: List<WorkspacesQuery.Project> = emptyList()
    fun updateItems(_items: List<WorkspacesQuery.Project>) {
        items = _items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_project, parent, false)
        return ViewHolder(view,listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class ViewHolder(itemView: View, clickListener: OnClickProject) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }
        private val listener = clickListener
        private var itemId: String = ""
        private val icon = itemView.view_image
        private val title = itemView.view_title
        private val description = itemView.view_description
        //   @drawable/notification_form_blue
        private val notification = itemView.view_notification
        fun bind(item: WorkspacesQuery.Project) {
            val projectInfo = item.fragments().projectsList()
            itemId = projectInfo.id()
            title.text = projectInfo.name()
            description.text = projectInfo.description() ?: "no description."
            Glide.with(icon.context)
                .load(projectInfo.image())
                .into(icon)
            val notificationSize = projectInfo.events()?.size ?: 0
            if (notificationSize > 0) {
                notification.text = "$notificationSize"
                notification.visibility = View.VISIBLE
            } else {
                notification.visibility = View.GONE
            }
        }

        override fun onClick(v: View?) {
            listener.onClickProject(itemId)
        }
    }

    interface OnClickProject {
        fun onClickProject(projectId:String)
    }
}