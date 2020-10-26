package ru.rosystem.hawk.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import ru.rosystem.hawk.UserInfoQuery
import ru.rosystem.hawk.WorkspacesQuery
import ru.rosystem.hawk.network.NetworkState

class HomeViewModel : ViewModel() {

    // Field for projects
    private val workspaceItems = MutableLiveData<List<WorkspacesQuery.Workspace>>()
    private val projectItems = MutableLiveData<List<WorkspacesQuery.Project>>()
    private val allProjects = mutableListOf<WorkspacesQuery.Project>()

    // Field for user info
    private val userEmail = MutableLiveData<String>()
    private val userName = MutableLiveData<String>()
    private val userImage = MutableLiveData<String>()


    init {
        requestWorkspaces()
        requestUserInfo()
    }


    /*
        Method that is triggered when the text in the searchView changes.
        Filters projects by the entered string.
     */
    fun searchProjects(query: String) {
        val filteredProjects = if (query.isNotBlank()) {
            val filterPattern = query.trim()
            allProjects.filter {
                it.fragments().projectsList().name().contains(filterPattern, true)
            }
        } else {
            allProjects
        }
        projectItems.postValue(filteredProjects)
    }

    /*
    Method that fires when the workspace is selected in the navigation view drawer.
     */
    fun searchWorkspace(workspaceName: String) {
        val filteredWorkspace = workspaceItems.value?.find { it.name() == workspaceName }
        projectItems.postValue(filteredWorkspace?.projects() ?: emptyList())
    }

    /*
    Request to get all user workspaces
     */
    private fun requestWorkspaces() {
        val client = NetworkState.getApolloClient()
        val query = WorkspacesQuery.builder()
            .ids(emptyList())
            .build()
        client.query(query)
            .enqueue(object : ApolloCall.Callback<WorkspacesQuery.Data>() {
                override fun onResponse(response: Response<WorkspacesQuery.Data>) {
                    //TODO Добавить обработку ошибок
                    val workspaces = response.data?.workspaces()
                    if (workspaces != null) {
                        for (workspace in workspaces) {
                            workspace.projects()?.let { allProjects.addAll(it) }
                        }
                        projectItems.postValue(allProjects)
                        workspaceItems.postValue(workspaces)
                    }

                }

                override fun onFailure(e: ApolloException) {
                    //TODO(Добавить обработку ошибок)
                }
            })

    }


    /*
    Request for information about a user
     */
    private fun requestUserInfo() {
        val client = NetworkState.getApolloClient()
        val query = UserInfoQuery.builder()
            .build()
        client.query(query)
            .enqueue(object : ApolloCall.Callback<UserInfoQuery.Data>() {
                override fun onResponse(response: Response<UserInfoQuery.Data>) {
                    val name = response.data?.me()?.name() ?: "no name"
                    val email = response.data?.me()?.email() ?: "no email"
                    val imageUrl = response.data?.me()?.image() ?: ""
                    userEmail.postValue(email)
                    userName.postValue(name)
                    userImage.postValue(imageUrl)
                }

                override fun onFailure(e: ApolloException) {
                    //TODO(Добавить обработку ошибок)
                }
            })
    }

    fun observeWorkspaces(): LiveData<List<WorkspacesQuery.Workspace>> {
        return workspaceItems
    }

    fun observeProjects(): LiveData<List<WorkspacesQuery.Project>> {
        return projectItems
    }

    fun observeUserEmail(): LiveData<String> {
        return userEmail
    }

    fun observeUserName(): LiveData<String> {
        return userName
    }

    fun observeUserPhoto(): LiveData<String> {
        return userImage
    }
}