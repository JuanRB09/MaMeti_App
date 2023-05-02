package it.grp.mameti.Dashboard.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotificationsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Pantalla del cachorro"
    }
    val text: LiveData<String> = _text
}