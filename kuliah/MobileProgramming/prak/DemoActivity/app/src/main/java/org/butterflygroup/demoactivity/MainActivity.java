package org.butterflygroup.demoactivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    Button bt_google, bt_youtube, bt_telpon, bt_kirim;
    String nim, nama;
    EditText etNim, etNama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //koneksi object .java dg .xml
        bt_google = (Button) findViewById(R.id.btn_Imp1);
        bt_youtube = (Button) findViewById(R.id.btn_imp2);
        bt_telpon = (Button) findViewById(R.id.btn_imp3);
        bt_kirim = (Button) findViewById(R.id.btn_kirim);
        etNim = (EditText) findViewById(R.id.etNim);
        etNama = (EditText) findViewById(R.id.etNama);
        //buat event untuk masing2 button
        bt_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://google.com";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
        bt_youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://youtube.com";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
        bt_telpon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomor = "08123456";
                Intent panggil = new Intent(Intent.ACTION_DIAL);
                panggil.setData(Uri.fromParts("tel", nomor, null));
                startActivity(panggil);
            }
        });
        bt_kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ExplicitActivity.class);
                intent.putExtra("data1", etNim.getText().toString());
                intent.putExtra("data2", etNama.getText().toString());
                startActivity(intent);

            }
        });
    }
}