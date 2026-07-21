package com.ishaan.roadroot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ishaan.roadroot.ui.RoadRootNavGraph
import com.ishaan.roadroot.ui.theme.RoadRootTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RoadRootTheme {
                RoadRootNavGraph()
            }
        }
    }
}
