package com.dicoding.aplikasipertama

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dicoding.aplikasipertama.ui.theme.AplikasiPertamaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AplikasiPertamaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }

        // inisalisasi komponen
        var edTglLahir = findViewById<EditText>(R.id.ed_tanggal_lahir)
        var btnKlik = findViewById<Button>(R.id.btn_klik)
        var tvHasil = findViewById<TextView>(R.id.tv_hasil)

        btnKlik.setOnClickListener {
            val input = edTglLahir.text.toString()
            val hasil = when (input.toInt()) {
                in 1946..1964 -> "Baby boomers"
                in 1965..1980 -> "X"
                in 1981..1995 -> "Millenial"
                in 1996..2010 -> "Z"
                else -> "Gak tau mas"
            }

            tvHasil.text = "Kamu generasi $hasil"
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AplikasiPertamaTheme {
        Greeting("Android")
    }
}