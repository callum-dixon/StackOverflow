package com.pixel.stackoverflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pixel.stackoverflow.ui.theme.StackOverflowTheme
import com.pixel.stackoverflow.ui.view.UserListView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            StackOverflowTheme {
                UserListView()
            }
        }
    }
}