package com.example.fixedassetinventory.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class AssetFormState {
    var asssetNo by mutableStateOf("")
    var description by mutableStateOf("")
    var location by mutableStateOf("")
    var remarks by mutableStateOf("")

    fun getFields(isEdit: Boolean): List<AssetFieldData> {
        return listOf(
            AssetFieldData("Asset Number", asssetNo, enabled = !isEdit) { asssetNo = it},
            AssetFieldData("Description", description) { description = it },
            AssetFieldData("Location", location) { location = it },
            AssetFieldData("Remarks", remarks) { remarks = it }
        )
    }

    fun reset() {
        asssetNo = ""
        description = ""
        location = ""
        remarks = ""
    }
}

data class AssetFieldData(
    val label: String,
    val value: String,
    val enabled: Boolean = true,
    val onValueChange: (String) -> Unit
)



