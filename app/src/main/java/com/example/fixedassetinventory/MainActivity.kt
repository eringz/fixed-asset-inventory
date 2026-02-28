package com.example.fixedassetinventory

// Import for Navigation


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fixedassetinventory.data.AppDatabase
import com.example.fixedassetinventory.ui.screens.DashboardScreen
import com.example.fixedassetinventory.ui.screens.LoginScreen
import com.example.fixedassetinventory.ui.theme.FixedAssetInventoryTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FixedAssetInventoryTheme {
                val navController = rememberNavController()
                val database = AppDatabase.getDatabase(this)
                val assetDao = database.assetDao()

                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    NavHost(navController = navController, startDestination = "login") {

                        composable("login") {
                            LoginScreen(onLoginSuccess = {
                                navController.navigate("dashboard") {
                                    popUpTo("login")
                                }
                            })
                        }
                        composable("dashboard") {
                            DashboardScreen(
//                                assetDao = assetDao,
                                onLogout = {
                                    navController.navigate("login")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

//
//@Composable
//fun DashboardScreen(onLogout: () -> Unit) {
//    Column (
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(24.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(text = "Welcome to Dashboard!")
//        Spacer(modifier = Modifier.height(24.dp))
//        Button(onClick = onLogout) {
//            Text("Logout")
//        }
//    }
//}

//@Composable
//fun ValidationSection(assetDao: AssetDao) {
//    var searchInput by remember { mutableStateOf("") }
//    var validationStatus by remember { mutableStateOf<String>("IDLE") }
//    val scope = rememberCoroutineScope()
//
//    Column(modifier = Modifier.padding(16.dp)) {
//        OutlinedTextField(
//            value = searchInput,
//            onValueChange = { searchInput = it },
//            label = { Text("Enter Asset Number")},
//            modifier = Modifier.fillMaxWidth()
//        )
//    }
//
//    Button(onClick = {
//        val cleanInput = searchInput.trim()
//        if (cleanInput.IsEmpty()) return@Button
//
//        scope.launch {
//            val asset = assetDao.findAssetByNumber(cleanInput)
//            asset?.let { assetDao.update(it.copy(validate = "Found"))}
//            validationStatus = if (asset != NULL) "FOUND" : "NOT_FOUND"
//        }
//
//    }) {
//        Text("Validate")
//    }
//
//}