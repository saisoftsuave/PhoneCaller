package com.softsuave.phonecaller.presentation

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.softsuave.phonecaller.CallerViewModel
import com.softsuave.phonecaller.R
import com.softsuave.phonecaller.ui.theme.PhoneCallerTheme
import com.softsuave.phonecaller.utils.CALL_TYPE
import com.softsuave.phonecaller.utils.CallLogEntry
import java.util.Date

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RecentsScreen(modifier: Modifier = Modifier, viewModel: CallerViewModel) {
    val context = LocalContext.current
    val callLogs by viewModel.callLogs.collectAsState()

    val permissionState =
        rememberPermissionState(permission = Manifest.permission.READ_CALL_LOG)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = { permissionState.launchPermissionRequest() }) {
            Text("Request Call Log Permission")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (permissionState.status.isGranted) {
            viewModel.fetchCallLogs(contentResolver = context.contentResolver)

            LazyColumn {
                items(callLogs.size) { callLog ->
                    CallLogItem(callLog = callLogs[callLog]) {
                        Toast.makeText(
                            context,
                            "Calling ${it.name}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } else {
            Text("Permission is not granted.")
        }
    }
}

@Composable
fun CallLogItem(
    modifier: Modifier = Modifier,
    callLog: CallLogEntry,
    onCallClick: (CallLogEntry) -> Unit
) {

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
            .clickable {

            },
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
                Text(text = callLog.name.getOrNull(0)?.toString()?.uppercase() ?: "")
            }
            Column(
                modifier = modifier.padding(start = 16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = callLog.name)
                Row {
                    when (callLog.type) {
                        CALL_TYPE.INCOMING -> Icon(
                            painter = painterResource(id = R.drawable.incoming_call),
                            contentDescription = "Incoming Call"
                        )

                        CALL_TYPE.OUTGOING -> Icon(
                            painter = painterResource(id = R.drawable.outgoing_call),
                            contentDescription = "Outgoing Call"
                        )

                        CALL_TYPE.MISSED -> Icon(
                            painter = painterResource(id = R.drawable.missed_call),
                            contentDescription = "Missed Call"
                        )

                        CALL_TYPE.UNKNOWN -> Icon(
                            painter = painterResource(id = R.drawable.unknown_call),
                            contentDescription = "Unknown Call"
                        )
                    }
                    Text("")
                }
            }
        }
        IconButton(onClick = {
            onCallClick(callLog)
        }) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "Contact Image"
            )
        }
    }
}


@Preview(showSystemUi = true)
@Composable
private fun CallLogItemPreview() {
    PhoneCallerTheme {
        CallLogItem(
            callLog = CallLogEntry(
                number = "1234567890",
                type = CALL_TYPE.INCOMING,
                date = System.currentTimeMillis(),
                duration = "123",
                name = "Sai Srujan"
            )
        ) {

        }
    }
}