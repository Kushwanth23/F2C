package com.example.f2c;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.f2c.Adapter.DetailsAdapter;
import com.example.f2c.Model.CartModel;
import com.example.f2c.Model.OrderModel;
import com.example.f2c.Model.UserModel;
import com.example.f2c.ICartLoadListener;
import com.example.f2c.databinding.ActivityDetailsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class DetailsActivity extends AppCompatActivity implements ICartLoadListener {

    @NonNull ActivityDetailsBinding binding;

    DatabaseReference reference;
    private String orderId,userId;

    ArrayList<CartModel> list = new ArrayList<>();
    DetailsAdapter adapter;

    ICartLoadListener cartLoadListener;
    String currentData;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userId = getIntent().getStringExtra("userId");
        orderId = getIntent().getStringExtra("orderId");

        reference = FirebaseDatabase.getInstance().getReference();

        cartLoadListener = this;

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        progressDialog = new ProgressDialog(this);

        getCurrentDate();
        checkOrderStatus();
    }

    private void checkOrderStatus(){
        reference.child("Order").child(userId).child(orderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            binding.recyclerView.setVisibility(View.VISIBLE);

                            OrderModel model = snapshot.getValue(OrderModel.class);

                            binding.orderId.setText("Order ID: "+model.getOrderId());

                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                            String date = dateFormat.format(model.getTimestamp());

                            binding.date.setText("Date: "+date);

                            String status = model.getStatus();

                            binding.address.setText(model.getAddressId());
                            getItems();

                            //checkUserAddress();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DetailsActivity.this, "Error: "+
                                error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getCurrentDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        currentData = dateFormat.format(new Date());
    }

    public void onCartLoadListener(ArrayList<CartModel> list){

        double sum = 0;
        int count = 0;
        double fullAmount = 0;
        for(CartModel cartModel : list){
            sum += cartModel.getTotalPrice();
            count += cartModel.getQuantity();
            fullAmount += cartModel.getQuantity() * cartModel.getTotalPrice();

        }
        binding.sumTotal.setText(new StringBuilder("₹ ").append(sum));

        binding.sumRealPrice.setText(new StringBuilder("₹ ").append(fullAmount));
        binding.sumItems.setText(String.valueOf(count));

    }

    private void getItems() {
        binding.recyclerView.setHasFixedSize(true);
        reference.child("Orders")
                .child(userId).child(orderId).child("items").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){

                            list.clear();


                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                CartModel model = dataSnapshot.getValue(CartModel.class);
                                list.add(model);
                            }

                            cartLoadListener.onCartLoadListener(list);
                            adapter = new DetailsAdapter(DetailsActivity.this,list);
                            binding.recyclerView.setAdapter(adapter);

                            getCustomerDetails();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DetailsActivity.this, "Error: "+
                                error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void getCustomerDetails(){
        reference.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    UserModel model = snapshot.getValue(UserModel.class);
                    assert model !=null;

                    binding.customerName.setText(model.getUsername());
                    binding.customerNumber.setText(model.getPhone());
                    binding.email.setText(model.getEmil());


                }else {
                    Toast.makeText(DetailsActivity.this, "No user exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DetailsActivity.this, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

}