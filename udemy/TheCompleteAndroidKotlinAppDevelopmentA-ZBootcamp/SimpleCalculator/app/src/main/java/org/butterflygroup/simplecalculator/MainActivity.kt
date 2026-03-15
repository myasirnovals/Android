package org.butterflygroup.simplecalculator

import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initListeners()
    }

    fun initListeners() {
        findViewById<MaterialButton>(R.id.btnAdd).setOnClickListener {
            val edtFirstInput = findViewById<EditText>(R.id.edtFirstValue)
            val edtSecondInput = findViewById<EditText>(R.id.edtSecondValue)

            if (TextUtils.isEmpty(edtFirstInput.text)) {
                edtFirstInput.error = getText(R.string.str_enter_first_number)
            } else if (TextUtils.isEmpty(edtSecondInput.text)) {
                edtSecondInput.error = getText(R.string.str_enter_second_number)
            } else {
                val edtResult =
                    Integer.valueOf(edtFirstInput.text.toString()) + edtSecondInput.text.toString()
                        .toInt()

                findViewById<TextView>(R.id.txtResult).text = edtResult.toString()
            }
        }

        findViewById<MaterialButton>(R.id.btnMinus).setOnClickListener {
            val edtFirstInput = findViewById<EditText>(R.id.edtFirstValue)
            val edtSecondInput = findViewById<EditText>(R.id.edtSecondValue)

            if (TextUtils.isEmpty(edtFirstInput.text)) {
                edtFirstInput.error = getText(R.string.str_enter_first_number)
            } else if (TextUtils.isEmpty(edtSecondInput.text)) {
                edtSecondInput.error = getText(R.string.str_enter_second_number)
            } else {
                val edtResult =
                    Integer.valueOf(edtFirstInput.text.toString()) - edtSecondInput.text.toString()
                        .toInt()

                findViewById<TextView>(R.id.txtResult).text = edtResult.toString()
            }
        }
    }
}