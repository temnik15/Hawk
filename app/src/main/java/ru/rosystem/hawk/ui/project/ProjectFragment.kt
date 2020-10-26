package ru.rosystem.hawk.ui.project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.project_fragment.view.*
import ru.rosystem.hawk.R
import ru.rosystem.hawk.WorkspacesQuery

class ProjectFragment : Fragment() {
    companion object {
        val fragmentTag = ProjectFragment::class.java.simpleName

        // Current project item
        var item: WorkspacesQuery.Project? = null
        fun newInstance(project: WorkspacesQuery.Project): ProjectFragment {
            item = project
            return ProjectFragment()
        }
    }

    private lateinit var projectViewModel: ProjectViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.project_fragment, container, false)
        projectViewModel = ViewModelProvider(this).get(ProjectViewModel::class.java)
        val toolbar = rootView.view_toolbar
        (activity as AppCompatActivity).apply {
            setSupportActionBar(toolbar)
            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        toolbar.apply {
            title = projectViewModel.getTitle()
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }
        val adapter = EventAdapter()
        adapter.updateItems(projectViewModel.getEvents())
        rootView.view_recycler.layoutManager = LinearLayoutManager(context)
        rootView.view_recycler.adapter = adapter
        return rootView
    }

    override fun onDestroy() {
        item = null
        super.onDestroy()
    }
}