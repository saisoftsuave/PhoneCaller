package com.softsuave.phonecaller

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.database.Cursor
import android.provider.CallLog
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softsuave.phonecaller.utils.CALL_TYPE
import com.softsuave.phonecaller.utils.CallLogEntry
import com.softsuave.phonecaller.utils.Constants
import com.softsuave.phonecaller.utils.Constants.countries
import com.softsuave.phonecaller.utils.Contact
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CallerViewModel(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()


    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts = _contacts.asStateFlow()

    private val _callLogs = MutableStateFlow<List<CallLogEntry>>(emptyList())
    val callLogs = _callLogs.asStateFlow()

    fun updateContacts(newContacts: List<Contact>) {
        _contacts.value = newContacts
            .associateBy { it.name }
            .values
            .toList()
    }

    val searchResult = searchText
        .combine(_contacts) { text, contacts ->
            if (text.isBlank()) {
                contacts
            }
            contacts.filter { contact ->
                contact.name.uppercase().contains(text.trim().uppercase())
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _contacts.value
        )

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }




    @SuppressLint("Range")
    private fun loadContacts(contentResolver: ContentResolver): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val contentResolver: ContentResolver = contentResolver
        val cursor: Cursor? = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null
        )

        cursor?.let {
            if (it.count > 0) {
                while (it.moveToNext()) {
                    val id = it.getString(it.getColumnIndex(ContactsContract.Contacts._ID))
                    val name =
                        it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                    // Check if contact has a phone number
                    if (it.getInt(it.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        val pCursor: Cursor? = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            arrayOf(id),
                            null
                        )
                        pCursor?.let { pc ->
                            while (pc.moveToNext()) {
                                val phoneNumber =
                                    pc.getString(pc.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                contacts.add(Contact(name, phoneNumber))
                            }
                            pc.close()
                        }
                    }
                }
            }
            it.close()
        }
        return contacts
    }

    fun getCallLogs(contentResolver: ContentResolver): List<CallLogEntry> {
        val callLogs = mutableListOf<CallLogEntry>()

        val cursor: Cursor? = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null,
            null,
            null,
            "${CallLog.Calls.DATE} DESC"
        )

        cursor?.use {
            val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
            val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)
            val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
            val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)
            val contactNameIndex = it.getColumnIndex(CallLog.Calls.CACHED_NAME)

            while (it.moveToNext()) {
                val number = it.getString(numberIndex)
                val type = when (it.getInt(typeIndex)) {
                    CallLog.Calls.OUTGOING_TYPE -> CALL_TYPE.OUTGOING
                    CallLog.Calls.INCOMING_TYPE -> CALL_TYPE.INCOMING
                    CallLog.Calls.MISSED_TYPE -> CALL_TYPE.MISSED
                    else -> CALL_TYPE.UNKNOWN
                }
                val date = it.getLong(dateIndex)
                val duration = it.getString(durationIndex)
                val contactName = it.getString(contactNameIndex)

                callLogs.add(CallLogEntry(number, type, date, duration, contactName))
            }
        }

        return callLogs
    }


    fun fetchContacts(contentResolver: ContentResolver) {
        viewModelScope.launch {
            withContext(dispatcher) {
                val contacts = loadContacts(contentResolver)
                Log.d("Contacts", contacts.toString())
                _contacts.value = contacts
                updateContacts(contacts)
            }
        }
    }

    fun fetchCallLogs(contentResolver: ContentResolver) {
        viewModelScope.launch {
            withContext(dispatcher) {
                val callLogs = getCallLogs(contentResolver)
                _callLogs.value = callLogs
                Log.d("CallLogs", callLogs.toString())
            }
        }
    }
}