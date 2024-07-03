package com.softsuave.phonecaller

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.softsuave.phonecaller.presentation.ContactsScreen
import com.softsuave.phonecaller.presentation.FavouritesScreen
import com.softsuave.phonecaller.presentation.RecentsScreen
import com.softsuave.phonecaller.utils.Screens

@Composable
fun PhoneCallerApp(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screens.Recents.route,
    viewModel: CallerViewModel
) {
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: startDestination
    val navigationActions = remember(navController) {
        CallerNavigationActions(navController)
    }
    Scaffold(
        topBar = {
            CallerTopAppbar(viewModel = viewModel)
        }, bottomBar = {
            CallerBottomNavigationBar(
                selectedDestination = currentRoute,
                navigateToDestination = navigationActions::navigateTo
            )
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
        ) {
            composable(Screens.Recents.route) {
                RecentsScreen(Modifier.padding(padding),viewModel)
            }
            composable(Screens.Contacts.route) {
                ContactsScreen(Modifier.padding(padding), viewModel)
            }
            composable(Screens.Favourites.route) {
                FavouritesScreen(Modifier.padding(padding))
            }
        }
    }
}