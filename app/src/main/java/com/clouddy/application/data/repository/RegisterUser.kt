package com.clouddy.application.data.repository

import com.google.firebase.auth.FirebaseAuth

fun RegisterUser( name: String, password:String, email: String, repeatPassword: String , onResult: (Boolean) -> Unit ) {
    val auth = FirebaseAuth.getInstance()
    auth.createUserWithEmailAndPassword(email,password)
        .addOnCompleteListener { task ->
            onResult(task.isSuccessful)
        }
}