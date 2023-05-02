package it.grp.mameti.Dashboard.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Pantalla de monitoreo"
    }
    val text: LiveData<String> = _text
}