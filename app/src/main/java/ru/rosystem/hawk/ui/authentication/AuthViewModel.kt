package ru.rosystem.hawk.ui.authentication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.ApolloCall
import ru.rosystem.hawk.LoginMutation
import ru.rosystem.hawk.network.NetworkState

class AuthViewModel:ViewModel(){
    //Debug field
    val myPassword = "no pass"
    //
    var email = ""
    var password = ""
    val isError = MutableLiveData<Boolean>()

    fun authorization(callback:ApolloCall.Callback<LoginMutation.Data>) {
        //Debug value
        email = "temnik15@bk.ru"
        password=myPassword
        //
        if (validationAuthArgs()) {
            isError.value=true
         //   TODO(Обработка ошибки некорректного ввода)
            return
        }
        val client = NetworkState.getApolloClient()
        val loginMutation = LoginMutation.builder()
            .email(email)
            .password(password)
            .build()
        client.mutate(loginMutation)
            .enqueue(callback)
    }

    // Primitive processing of input fields
    private fun validationAuthArgs(): Boolean {
        return (email.isBlank() || password.isBlank())
    }


}