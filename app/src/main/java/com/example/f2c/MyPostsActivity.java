package com.example.f2c;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.example.f2c.Adapter.ItemAdapter;
import com.example.f2c.Adapter.OrderAdapter;
import com.example.f2c.Model.ItemModel;
import com.example.f2c.Model.OrderModel;
import com.example.f2c.databinding.ActivityMyPostsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyPostsActivity extends AppCompatActivity {

    ActivityMyPostsBinding binding;

    DatabaseReference reference;

    ArrayList<OrderModel> list = new ArrayList<>();
    ItemAdapter adapter;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyPostsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        reference = FirebaseDatabase.getInstance().getReference().child("Orders");

        user = FirebaseAuth.getInstance().getCurrentUser();

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        new Handler().postDelayed(this::getData,300);

    }

    private void getData(){
        Query query = reference.orderByChild("publisher").equalTo(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    list.clear();
                    binding.progressBar.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.VISIBLE);

                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        OrderModel model = dataSnapshot.getValue(OrderModel.class);
                        if(model!= null)
                        {
                            list.add(model);
                        }
                    }

                    //adapter = new OrderAdapter(MyPostsActivity.this,list,"my");
                    binding.recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(MyPostsActivity.this, "No items!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyPostsActivity.this, "Error: "+
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}