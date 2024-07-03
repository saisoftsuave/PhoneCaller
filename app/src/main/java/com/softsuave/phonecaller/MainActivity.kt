package com.softsuave.phonecaller

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.softsuave.phonecaller.ui.theme.PhoneCallerTheme

class MainActivity : ComponentActivity() {
    private val REQUEST_CONTACTS_PERMISSION = 1
    private lateinit var viewModel: CallerViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = CallerViewModel()
        requestContactsPermission()
        enableEdgeToEdge()
        setContent {
            PhoneCallerTheme {
                Surface {
                    PhoneCallerApp(viewModel = viewModel)
                }
            }
        }
    }

    private fun requestContactsPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_CONTACTS
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_CONTACTS),
                REQUEST_CONTACTS_PERMISSION
            )
        } else {
            // Permission is already granted, proceed to access contacts
            viewModel.fetchContacts(contentResolver)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CONTACTS_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted, proceed to access contacts
                    viewModel.fetchContacts(contentResolver)
                } else {
                    // Permission denied, show a message to the user
                    Toast.makeText(this, "Permission denied to read contacts", Toast.LENGTH_SHORT)
                        .show()
                }
                return
            }
        }
    }

}