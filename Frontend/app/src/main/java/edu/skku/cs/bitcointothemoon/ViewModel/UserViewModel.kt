package edu.skku.cs.bitcointothemoon.ViewModel

import androidx.lifecycle.ViewModel

class UserViewModel: ViewModel() {

    private lateinit var username : String
    private lateinit var nickname : String

    fun initUser(username: String, nickname : String) {
        this.username = username
        this.nickname = nickname
    }

    fun getUsername(): String {
        return this.username
    }

    fun getNickname(): String {
        return this.nickname
    }

}