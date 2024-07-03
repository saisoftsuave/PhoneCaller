package com.softsuave.phonecaller

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults.colors
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.softsuave.phonecaller.presentation.ContactCard
import com.softsuave.phonecaller.utils.Screens


@Composable
fun CallerBottomNavigationBar(
    selectedDestination: String,
    navigateToDestination: (BottomNavigationDestination) -> Unit,

    ) {
    NavigationBar(modifier = Modifier.fillMaxWidth()) {
        BOTTOM_NAVIGATION_DESTINATIONS.forEach { replyDestination ->
/*            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ic_call_animation))
            var isPlaying by remember { mutableStateOf(true) }
            val progress by animateLottieCompositionAsState(
                composition = composition,
                isPlaying = isPlaying
            )
            LaunchedEffect(progress) {
                if (progress == 1f) {
                    isPlaying = false
                }
                if (progress == 0f) {
                    isPlaying = true
                }
            }*/
            NavigationBarItem(
                selected = selectedDestination == replyDestination.route,
                onClick = {
                    navigateToDestination(replyDestination)

                },
                icon = {
                   /* Box(
                        modifier = Modifier
                            .size(44.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LottieAnimation(
                            composition = composition,
                            progress = {
                                if (isPlaying) {
                                    progress
                                } else {
                                    1f
                                }
                            })
                    }*/
                    Icon(
                        imageVector = replyDestination.selectedIcon,
                        contentDescription = stringResource(id = replyDestination.iconTextId)
                    )
                },
                label = { Text(stringResource(id = replyDestination.iconTextId)) }
            )
        }
    }
}

data class BottomNavigationDestination(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val iconTextId: Int
)

class CallerNavigationActions(private val navController: NavHostController) {

    fun navigateTo(destination: BottomNavigationDestination) {
        navController.navigate(destination.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}

val BOTTOM_NAVIGATION_DESTINATIONS = listOf(
    BottomNavigationDestination(
        route = Screens.Favourites.route,
        selectedIcon = Icons.Default.Favorite,
        unselectedIcon = Icons.Default.Favorite,
        iconTextId = R.string.favourites
    ),
    BottomNavigationDestination(
        route = Screens.Recents.route,
        selectedIcon = Icons.Default.Call,
        unselectedIcon = Icons.Default.Call,
        iconTextId = R.string.recents
    ),
    BottomNavigationDestination(
        route = Screens.Contacts.route,
        selectedIcon = Icons.Outlined.AccountBox,
        unselectedIcon = Icons.Outlined.AccountBox,
        iconTextId = R.string.contacts
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallerTopAppbar(modifier: Modifier = Modifier, viewModel: CallerViewModel) {
    val searchText by viewModel.searchText.collectAsState()
    var isExpanded by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val contacts by viewModel.searchResult.collectAsState()
    val context = LocalContext.current

    // BackHandler to detect back button press
    BackHandler(enabled = isExpanded) {
        isExpanded = false
        focusManager.clearFocus() // Clear focus on back press
    }

    TextFieldDefaults.colors().copy(unfocusedContainerColor = Color.Red)
    SearchBar(inputField = {
        TextField(
            value = searchText,
            onValueChange = {
                viewModel.onSearchTextChange(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        isExpanded = true
                    }
                }
                .focusRequester(focusRequester), // Apply focusRequester to TextField
            placeholder = { Text(text = "Search") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon", modifier = Modifier.padding(start = 16.dp)
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More Icon", modifier = Modifier.padding(end = 16.dp)
                )
            },
            shape = MaterialTheme.shapes.extraLarge,
            colors = TextFieldDefaults.colors().copy(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            )
        )
    }, expanded = isExpanded, onExpandedChange = {
        isExpanded = it
        if (!it) {
            focusManager.clearFocus()
        }
    }, shape = MaterialTheme.shapes.extraLarge, colors = colors(dividerColor = Color.Transparent)) {

        LazyColumn(
            contentPadding = PaddingValues(16.dp)
        ) {
            items(contacts.size) { index ->
                ContactCard(contact = contacts[index]) {
                    Toast.makeText(
                        context,
                        "Calling ${it.name}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}


