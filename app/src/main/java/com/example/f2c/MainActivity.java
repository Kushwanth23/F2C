//Kushwanth23
package com.example.f2c;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    Button vegetables,grains;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        vegetables=findViewById(R.id.vegetables);
        grains = findViewById(R.id.grains);

        vegetables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, VegetablesListActivity.class));
                //finish();
            }
        });

        grains.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,GrainsListActivity.class));
                //finish();
            }
        });

        /**Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null){
            setSupportActionBar(toolbar);
        }**/

    }

    /**@Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;

    }**/

    //@Override
    /**public boolean onOptionsItemsSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.signout){
            FirebaseAuth.getInstance().signOut();
            finish();
        }
        else if(item.getItemId()==R.id.settings){
            Toast.makeText(MainActivity.this,"You have selected settings", Toast.LENGTH_LONG).show();

        }
        return true;
    }**/
}