package org.butterflygroup.fifthapps;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    CheckBox chkCream;
    CheckBox chkSugar;
    Button btnOrder;
    RadioGroup radCoffeeType;
    RadioButton radDecaf;
    RadioButton radExpresso;
    RadioButton radColombian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chkCream = (CheckBox) findViewById(R.id.chkCream);
        chkSugar = (CheckBox) findViewById(R.id.chkSugar);
        btnOrder = (Button) findViewById(R.id.btnOrder);
        radCoffeeType = (RadioGroup) findViewById(R.id.radGroupCoffeeType);
        radDecaf = (RadioButton) findViewById(R.id.radDecaf);
        radExpresso = (RadioButton) findViewById(R.id.radExpresso);
        radColombian = (RadioButton) findViewById(R.id.radColombian);
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = "Coffee ";
                if (chkCream.isChecked())
                    msg += " & cream ";
                if (chkSugar.isChecked())
                    msg += " & Sugar";
                // get radio buttons ID number
                int radioId = radCoffeeType.getCheckedRadioButtonId();
                // Membandingkan Id dengan id tiap pilihan
                if (radColombian.getId() == radioId)
                    msg = "Colombian " + msg;
                // atau dapat langsung menggunakan .isChecked() pada setiap RadioButton
                if (radExpresso.isChecked())
                    msg = "Expresso" + msg;
                Toast.makeText(getApplicationContext(), msg,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
