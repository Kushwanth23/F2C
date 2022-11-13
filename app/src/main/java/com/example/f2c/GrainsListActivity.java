package com.example.f2c;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class GrainsListActivity extends AppCompatActivity {

    ListView list;
    String grains[] = {
            "abcd"
    };

    Integer[] imageid = {
            R.drawable.food1
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grains_list);

        CustomList adapter = new CustomList(GrainsListActivity.this,grains,imageid);
        list = findViewById(R.id.grains_listview);
        list.setAdapter(adapter);
    }
}