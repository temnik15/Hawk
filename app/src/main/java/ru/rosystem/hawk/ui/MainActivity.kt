package ru.rosystem.hawk.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.home_fragment.view.*
import ru.rosystem.hawk.R
import ru.rosystem.hawk.ui.authentication.AuthFragment
import ru.rosystem.hawk.ui.home.HomeFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //set current theme (from launch screen theme)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .addToBackStack(AuthFragment.fragmentTag)
                .add(R.id.activity_main_frame, AuthFragment.newInstance(), AuthFragment.fragmentTag)
                .commit()
        }
    }

    override fun onBackPressed() {
        val size = supportFragmentManager.fragments.size
        if (size == 1) {
            finish()
        }
        super.onBackPressed()
    }

}