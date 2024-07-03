package com.softsuave.phonecaller.utils

sealed class Screens(val route: String){
    data object Contacts : Screens("contacts")
    data object Recents : Screens("recents")
    data object Favourites : Screens("favourites")
}