//Kushwanth23
package com.example.f2c;

import static com.example.f2c.ItemAdapter.productsList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.example.f2c.Listeners.ICartLoadListener;
import com.example.f2c.Listeners.MyUpdateCartEvent;
import com.example.f2c.Model.CartModel;
import com.example.f2c.Model.Products;
import com.example.f2c.Model.UserModel;
import com.example.f2c.databinding.ActivityDetailBinding;

import com.example.f2c.databinding.DialogueDdressBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;

public class DetailActivity extends AppCompatActivity implements ICartLoadListener {

    public static final String TAG="Database";

    ActivityDetailBinding binding;

    Products model;
    int position;

    DatabaseReference reference;
    FirebaseUser user;

    String email,username,phone;
    ICartLoadListener cartLoadListener;

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().hasSubscriberForEvent(MyUpdateCartEvent.class))
            EventBus.getDefault().removeStickyEvent(MyUpdateCartEvent.class);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onUpdateCart(MyUpdateCartEvent event){
        checkCartStatus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        reference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        cartLoadListener = this;

        position = getIntent().getIntExtra("pos",0);

        model = productsList.get(position);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        if (model !=null){



            if(model.getStock()){
                binding.txtSold.setVisibility(View.GONE);
                binding.btnPurchase.setVisibility(View.VISIBLE);


            }else{
                checkCartStatus();
                binding.txtSold.setVisibility(View.VISIBLE);
                binding.btnPurchase.setVisibility(View.GONE);

            }
            binding.name.setText(model.getName());
            binding.price.setText("₹"+model.getPrice()+".0");
            try{
                Picasso.get().load(model.getImage()).placeholder(R.drawable.food1)
                        .into(binding.productImage);
            }catch (Exception e){
                e.getMessage();
            }
            binding.description.setText(model.getDescription());

            binding.category.setText(model.getCategory());
            binding.txtQuantity.setText(model.getQuantity());

            binding.btnPurchase.setOnClickListener(v -> {
                addToCart(model);
            });
        }

        getUserData();


    }

    private void addToCart(Products products){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Cart")
                .child(user.getUid());

        CartModel model = new CartModel();
        model.setKey(products.getId());
        model.setQuantity(1);
        model.setName(products.getName());
        model.setImage(products.getImage());
        model.setPrice(products.getPrice());
        model.setTotalPrice(products.getPrice());



        reference.child(products.getId()).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    binding.btnPurchase.setVisibility(View.GONE);
                    binding.txtAdded.setVisibility(View.VISIBLE);

                    Toast.makeText(DetailActivity.this, "Item added to cart", Toast.LENGTH_SHORT).show();
                }else {

                    binding.btnPurchase.setVisibility(View.VISIBLE);
                    binding.txtAdded.setVisibility(View.GONE);
                    Toast.makeText(DetailActivity.this, "Failed: "+
                            task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });


        EventBus.getDefault().postSticky(new MyUpdateCartEvent());


    }

    private void checkCartStatus(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        ArrayList<CartModel> cartList = new ArrayList<>();
        assert user != null;
        reference.child("Cart").child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(model.getId()).hasChildren()){
                            binding.btnPurchase.setVisibility(View.GONE);
                            binding.txtSold.setVisibility(View.GONE);
                            binding.txtAdded.setVisibility(View.VISIBLE);
                            binding.cartItemCount.setVisibility(View.VISIBLE);

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                CartModel model = dataSnapshot.getValue(CartModel.class);
                                cartList.add(model);
                            }

                            if (cartList.size() > 0){
                                binding.cartItemCount.setVisibility(View.VISIBLE);

                            }else {
                                binding.cartItemCount.setVisibility(View.GONE);

                            }

                            cartLoadListener.onCartLoadListener(cartList);
                        }else {
                            binding.btnPurchase.setVisibility(View.VISIBLE);

                            binding.txtSold.setVisibility(View.GONE);
                            binding.txtAdded.setVisibility(View.GONE);


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DetailActivity.this, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onCartLoadListener(ArrayList<CartModel> list) {
        int cartSum = 0;
        for (CartModel cartModel : list){
            cartSum +=cartModel.getQuantity();

        }
        binding.cartItemCount.setText(String.valueOf(cartSum));


    }



//    private void showOrderDialogue(Products model) {
//        DialogueDdressBinding binding = DialogueDdressBinding.inflate(getLayoutInflater());
//        Dialog dialog = new Dialog(DetailActivity.this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(binding.getRoot());
//        dialog.setCancelable(true);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//
//        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String address = binding.inputAddress.getText().toString();
//                if (address.isEmpty()){
//                    Toast.makeText(DetailActivity.this, "Enter address to continue!", Toast.LENGTH_SHORT).show();
//                }else {
//                    dialog.dismiss();
//                    saveOrder(model);
//                }
//            }
//        });
//
//        dialog.show();
//    }
//
//    private void saveOrder(Products model) {
//        ProgressDialog progressDialog = new ProgressDialog(DetailActivity.this);
//        progressDialog.setMessage("Order Placing...");
//        progressDialog.setCancelable(false);
//        progressDialog.show();
//
//        HashMap<String,Object> map = new HashMap<>();
//        map.put("username",username);
//        map.put("email",email);
//        map.put("phone",phone);
//        map.put("price",model.getPrice());
//        map.put("productName",model.getName());
//        map.put("timestamp",System.currentTimeMillis());
//
//
//        reference.child("Orders").push().setValue(map)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()){
//                        progressDialog.dismiss();
//                        finish();
//                        Toast.makeText(DetailActivity.this, "Order has been sent!", Toast.LENGTH_SHORT).show();
//                    }else {
//                        progressDialog.dismiss();
//                        Toast.makeText(DetailActivity.this, "Failed: "+
//                                task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//
//
//
//
//
//    }

    private void getUserData(){
        reference.child("Users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    UserModel model = snapshot.getValue(UserModel.class);
                    if (model !=null){
                        email = model.getEmil();
                        phone = model.getPhone();
                        username = model.getUsername();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DetailActivity.this, "Error: "+
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}