package com.example.finalsimpletodo

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.finalsimpletodo.viewmodel.AuthViewModel
import com.example.finalsimpletodo.pages.HomePage
import com.example.finalsimpletodo.pages.LoginPage
import com.example.finalsimpletodo.pages.SignupPage

@Composable
fun AppNavigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login", builder = {
        composable("login") {
            LoginPage(modifier,navController,authViewModel)
        }
        composable("signup") {
            SignupPage(modifier,navController,authViewModel)
        }
        composable("home") {
            HomePage(modifier,navController,authViewModel)
        }
    })
}