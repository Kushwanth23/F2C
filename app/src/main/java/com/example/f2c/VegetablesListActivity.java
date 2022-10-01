package com.example.f2c;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

public class VegetablesListActivity extends AppCompatActivity {

    ListView list;
    String vegetables[] = {
            "abcd",
            "aaaa"
    };

    Integer[] imageid = {
            R.drawable.food1
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vegetables_list);

        CustomList adapter = new CustomList(VegetablesListActivity.this, vegetables,imageid);
        list = findViewById(R.id.vegetables_listview);
        list.setAdapter(adapter);

    }
}