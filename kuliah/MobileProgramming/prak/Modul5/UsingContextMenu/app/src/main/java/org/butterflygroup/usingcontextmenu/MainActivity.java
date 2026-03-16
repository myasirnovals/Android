package org.butterflygroup.usingcontextmenu;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    EditText etMessage1;
    EditText etMessage2;
    Integer[] arrayPointSize = {10, 20, 30, 40, 50};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etMessage1 = (EditText) findViewById(R.id.etMessage1);
        etMessage2 = (EditText) findViewById(R.id.etMessage2);

        // menambahkan masing-masing context menu untuk setiap view
        registerForContextMenu(etMessage1);
        registerForContextMenu(etMessage2);
    }

    // detect what view is calling and create its context menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // decide what context menu needs to be made
        if (v.getId() == etMessage1.getId())
            // create a menu for etMessage1 box
            tampilMenu1(menu);
        if (v.getId() == etMessage2.getId()) {
            // create a menu for etMessage2 box
            tampilMenu2(menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //get id selected item menu
        int menuItemId = item.getItemId();
        switch (menuItemId) {
            case 1:
                Toast.makeText(this, "Android", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(this, "Mobile Programming",
                        Toast.LENGTH_SHORT).show();
                break;
            case 3:
                etMessage2.setText("UNJANI");
                break;
            case 4:
                etMessage2.setText("Sains dan Informatika");
                break;
            case 5:
                etMessage2.setText("Informatika");
        }
        return true;
    }

    private void tampilMenu1(Menu menu) {
        int groupId = 0;
        int order = 0;
        //arguments: groupId, optionId, order, title
        menu.add(groupId, 1, 1, "Android");
        menu.add(groupId, 2, 2, "Mobile Programming");
    }

    private void tampilMenu2(Menu menu) {
        int groupId = 0;
        int order = 0;
        //arguments: groupId, optionId, order, title
        menu.add(groupId, 3, 1, "Universitas");
        menu.add(groupId, 4, 2, "Fakultas");
        menu.add(groupId, 5, 3, "Jurusan");
    }
}