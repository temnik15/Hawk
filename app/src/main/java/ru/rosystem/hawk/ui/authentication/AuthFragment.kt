package ru.rosystem.hawk.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.ViewModelStore
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import kotlinx.android.synthetic.main.auth_fragment.*
import kotlinx.android.synthetic.main.auth_fragment.view.*
import ru.rosystem.hawk.LoginMutation
import ru.rosystem.hawk.R
import ru.rosystem.hawk.network.NetworkState
import ru.rosystem.hawk.network.TokenInterceptor
import ru.rosystem.hawk.ui.home.HomeFragment


class AuthFragment : Fragment() {
    companion object {
        fun newInstance(): AuthFragment {
            return AuthFragment()
        }

        val fragmentTag = AuthFragment::class.java.simpleName
        private const val KEY_DISPLAY_MODE = "DISPLAY_MODE"

    }

    private lateinit var authViewModel: AuthViewModel

    /*
    AuthFragment has two display modes:
    for authorization and for registration.
    The displayMode field contains information
    about the current display mode
    TODO(change enum to IntDef)
     */
    private var displayMode = DisplayMode.AUTHORIZATION

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.auth_fragment, container, false)
        /*
        Restore display mode when re-creating
         */
        savedInstanceState?.let {
            val mode = it.get(KEY_DISPLAY_MODE) as Int
            displayMode =
                if (mode == DisplayMode.AUTHORIZATION.ordinal) DisplayMode.AUTHORIZATION else DisplayMode.REGISTRATION
        }
        return rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        setOnClickListeners(view)
    }

    private fun setOnClickListeners(view: View) {
        view.view_btn_fast_auth.setOnClickListener {
            displayMode = DisplayMode.AUTHORIZATION
            changeDisplay(view)
        }
        view.view_btn_signup.setOnClickListener {
            displayMode = DisplayMode.REGISTRATION
            changeDisplay(view)
        }
        view.view_btn_enter.setOnClickListener {
            if (displayMode == DisplayMode.AUTHORIZATION) {
                authViewModel.email = view.view_edit_email.text.toString()
                authViewModel.password = view.view_edit_password.text.toString()
                authViewModel.authorization(getAttemptToLoginCallback())
            } else {
                //TODO(ADD sign up)
            }
        }
    }

    /*
    Method responsible for changing the display mode.
    It hides / shows the required views and repaints
    the fast auth and sign up buttons
     */
    private fun changeDisplay(view: View) {
        val colorFastAuth: Int
        val colorSignUp: Int
        val visible = if (displayMode == DisplayMode.AUTHORIZATION) {
            colorFastAuth = ContextCompat.getColor(view.context, R.color.colorSelectText)
            colorSignUp = ContextCompat.getColor(view.context, R.color.colorText)
            View.VISIBLE
        } else {
            colorFastAuth = ContextCompat.getColor(view.context, R.color.colorText)
            colorSignUp = ContextCompat.getColor(view.context, R.color.colorSelectText)
            View.GONE
        }
        view.view_label_password.visibility = visible
        view.view_edit_password.visibility = visible
        view.view_btn_fast_auth.setTextColor(colorFastAuth)
        view.view_btn_signup.setTextColor(colorSignUp)
    }


    private fun getAttemptToLoginCallback() = object : ApolloCall.Callback<LoginMutation.Data>() {
        override fun onResponse(response: Response<LoginMutation.Data>) {
            //todo(обработка возможных ошибок)
            TokenInterceptor.instance.token = response.data?.login()?.accessToken() ?: ""
            parentFragmentManager
                .beginTransaction()
                .replace(
                    R.id.activity_main_frame,
                    HomeFragment.newInstance(),
                    HomeFragment.fragmentTag
                )
                .commit()
        }

        override fun onFailure(e: ApolloException) {
            authViewModel.isError.value = true
            //todo(обработка возможных ошибок)
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //  Saving display mode before re-creating
        outState.putInt(KEY_DISPLAY_MODE, displayMode.ordinal)
    }
}



