package com.example.fixedassetinventory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.fixedassetinventory.data.AppDatabase
import com.example.fixedassetinventory.ui.navigation.NavGraph
import com.example.fixedassetinventory.ui.theme.FixedAssetInventoryTheme
import com.example.fixedassetinventory.viewmodel.AssetViewModel
import com.example.fixedassetinventory.viewmodel.AuthViewModel
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FixedAssetInventoryTheme {
                val navController = rememberNavController()
                val database = AppDatabase.getDatabase(this)

                val authViewModel: AuthViewModel = viewModel(
                    factory = AuthViewModel.provideFactory(database.userDao())
                )

                val assetViewModel: AssetViewModel = viewModel(
                    factory = AssetViewModel.provideFactory(database.assetDao())
                )

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(
                        navController = navController,
                        authViewModel = authViewModel,
                        assetViewModel = assetViewModel
                    )
                }
            }
        }
    }
}
