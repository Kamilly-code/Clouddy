package com.clouddy.application.ui.viewModel

import com.google.firebase.auth.FirebaseAuth

fun RegisterUser( name: String, password:String, email: String, repeatPassword: String , onResult: (Boolean) -> Unit ) {
    val auth = FirebaseAuth.getInstance()
    auth.createUserWithEmailAndPassword(email,password)
        .addOnCompleteListener { task ->
            onResult(task.isSuccessful)
        }
}