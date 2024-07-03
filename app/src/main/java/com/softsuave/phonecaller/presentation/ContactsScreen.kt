package com.softsuave.phonecaller.presentation

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softsuave.phonecaller.CallerViewModel
import com.softsuave.phonecaller.ui.theme.PhoneCallerTheme
import com.softsuave.phonecaller.utils.Contact

@Composable
fun ContactsScreen(modifier: Modifier = Modifier, viewModel: CallerViewModel) {
    val contacts = viewModel.contacts.collectAsState()
    CategorizedLazyColumn(
        contactDetails = contacts.value,
        modifier = modifier
    )
}


@Composable
fun ContactCard(modifier: Modifier = Modifier, contact: Contact, onCallClick: (Contact) -> Unit) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row {
            Box(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(100)
                    )
                    .height(55.dp)
                    .width(55.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = contact.name[0].toString())
            }
            Column(
                modifier = modifier.padding(start = 16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = contact.name)
                Text(text = contact.phoneNumber)
            }
        }
        IconButton(onClick = {
            onCallClick(contact)
        }) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "Contact Image"
            )
        }
    }
}

@Preview
@Composable
private fun ContactCardPreview() {
    PhoneCallerTheme {
        ContactCard(contact = Contact("Contact Name", "1234567890")) {}
    }
}

@Composable
fun ContactShimmerBody() {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .width((50..200).random().dp)
                    .height(20.dp)
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .width((150..300).random().dp)
                    .height(10.dp)
                    .shimmerEffect()
            )
        }
    }
}

@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition(label = "")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 + size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            tween(
                durationMillis = 1000
            )
        ),
        label = ""
    )
    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFB6B3B3), Color(0xFF757575), Color(0xFFB6B3B3)
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    ).onGloballyPositioned {
        size = it.size
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategorizedLazyColumn(
    contactDetails: List<Contact>,
    modifier: Modifier = Modifier
) {
    val groupedContacts = groupByFirstLetter(contactDetails)
    val context = LocalContext.current

    Column(modifier = modifier){
        Text(
            text = "My Contact",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        )
        LazyColumn(
            contentPadding = PaddingValues(16.dp)
        ) {
            groupedContacts.forEach { (letter, contacts) ->
                stickyHeader {
                    Text(
                        fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                        text = letter.toString(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                    )
                }
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
}

private fun groupByFirstLetter(contactDetails: List<Contact>): Map<Char, List<Contact>> {
    return contactDetails.groupBy { it.name.first().uppercaseChar() }
        .toSortedMap()
}
