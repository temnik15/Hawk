package ru.rosystem.hawk.ui.home

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.home_fragment.view.*
import ru.rosystem.hawk.R
import ru.rosystem.hawk.ui.project.ProjectFragment

class HomeFragment : Fragment(), ProjectAdapter.OnClickProject,
    NavigationView.OnNavigationItemSelectedListener {
    companion object {
        val fragmentTag = HomeFragment::class.java.simpleName
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var toggle: ActionBarDrawerToggle
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.home_fragment, container, false)
        (activity as AppCompatActivity).setSupportActionBar(rootView.view_toolbar)
        (activity as AppCompatActivity).supportActionBar?.show()
        rootView.view_toolbar.title = getString(R.string.title_home)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        setHasOptionsMenu(true)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toggle = ActionBarDrawerToggle(activity, view.view_drawer, R.string.open, R.string.close)
        view.view_drawer.addDrawerListener(toggle)
        toggle.syncState()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        view_recycler.layoutManager = LinearLayoutManager(context)
        val adapter = ProjectAdapter(this)
        view_recycler.adapter = adapter
        homeViewModel.observeProjects().observe(viewLifecycleOwner) { adapter.updateItems(it) }
        setupDrawer(view)
    }

    private fun setupDrawer(rootView: View) {
        /*
        Setting a listener on the header menu.
        Displays all user projects when clicked
         */
        val header = rootView.view_navigation?.getHeaderView(0)
        header?.setOnClickListener {
            rootView.view_toolbar?.title = getString(R.string.title_home)
            homeViewModel.searchProjects("")
        }

        rootView.view_navigation?.itemIconTintList = null
        rootView.view_navigation?.setNavigationItemSelectedListener(this)
        val userName = header?.findViewById<TextView>(R.id.view_user_name)
        val userImage = header?.findViewById<ImageView>(R.id.view_user_image)

        // Subscribing to user data fields
        homeViewModel.observeUserName().observe(viewLifecycleOwner) {
            userName?.text = it
        }
        homeViewModel.observeUserPhoto().observe(viewLifecycleOwner) {
            if (userImage != null) {
                Glide.with(this)
                    .load(it)
                    .into(userImage)
            }
        }
        // Formation of menus from user workspaces
        val menu = rootView.view_navigation?.menu
        homeViewModel.observeWorkspaces().observe(viewLifecycleOwner) { workspaces ->
            if (menu != null) {
                for (workspace in workspaces) {
                    val menuItem = menu.add(Menu.NONE, menu.size(), Menu.NONE, workspace.name())
                    val context = context
                    if (context != null) {
                        Observable.fromCallable {
                            Glide.with(context)
                                .asDrawable()
                                .load(workspace.image() ?: R.drawable.menu_placeholder)
                                .submit()
                                .get()
                        }.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                menuItem.icon = it
                            }

                    }
                }
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handling open / close navigation drawer
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        /*
        Since it remains so far, only clicks on workspaces are processed,
         then just search for projects by pressing item workspace.
         */
        homeViewModel.searchWorkspace(item.title.toString())
        return super.onOptionsItemSelected(item)
    }

    // Creating a separate menu for search functions by projects
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView: SearchView = searchItem?.actionView as SearchView
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            // every input change in SearchView filters the list of displayed projects
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    homeViewModel.searchProjects(it)
                }
                return false
            }

        })
    }

    /*
    This method is triggered when you click on any project.
    It replaces the current snippet with a snippet
    with detailed information about the project
     */
    override fun onClickProject(projectId: String) {
        val project = homeViewModel.observeProjects().value?.find {
            it.fragments().projectsList().id() == projectId
        }
        parentFragmentManager
            .beginTransaction()
            .addToBackStack(ProjectFragment.fragmentTag)
            .replace(
                R.id.activity_main_frame,
                ProjectFragment.newInstance(project!!),
                ProjectFragment.fragmentTag
            )
            .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val name = item.title.toString()
        if (name != getString(R.string.add_workspace_title)) {
            homeViewModel.searchWorkspace(name)
            view?.view_toolbar?.title = name
        } else {
            //TODO(Добавить функционал создания нового рабочего пространсва)
        }
        return true
    }


}