package ru.rosystem.hawk.ui.project

import androidx.lifecycle.ViewModel
import ru.rosystem.hawk.fragment.ProjectsList

class ProjectViewModel() : ViewModel() {

    private val projectItem = ProjectFragment.item


    fun getEvents():List<ProjectsList.Event>{
        return projectItem?.fragments()?.projectsList()?.events()?: emptyList()
    }

    fun getTitle():String{
        return projectItem?.fragments()?.projectsList()?.name()?:"no title"
    }

}