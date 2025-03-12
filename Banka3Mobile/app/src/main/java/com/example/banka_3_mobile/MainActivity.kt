package com.example.banka_3_mobile

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.banka_3_mobile.navigation.AppNavigation
import com.example.compose.Banka3MobileTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Banka3MobileTheme {
                Log.d("raf", "pOKRENUO SI ME")
                AppNavigation()
            }
        }
    }
}