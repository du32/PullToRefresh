package com.handicape.MarketCreators;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Serializable {

    static ArrayList<Product> products;
    static ProductAdapter mAdapter;
    private ListView productListView;

    private SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        products = new ArrayList<Product>();
        productListView = (ListView) findViewById(R.id.list_view);
        mSwipeRefreshLayout = findViewById(R.id.swiperefresh_items);


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                        Product p = products.get(position);
                        intent.putExtra("product_name", p.getName_product());
                        intent.putExtra("product_details", p.getDetails_product());
                        intent.putExtra("MyClass", (Serializable) products.get(position));
                        startActivity(intent);
                    }
                });

                mAdapter = new ProductAdapter(this, R.id.list_item_1,products);
                productListView.setAdapter(mAdapter);

                initDatabsae();


                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mSwipeRefreshLayout.isRefreshing()) {
                           mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 1000);
            }




        });
    {


        }


    }

    private void initDatabsae() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());

                                Product pojo = new Product(document.getString("name"),
                                        document.getString("price"),
                                        document.getString("number_of_pieces"),
                                        document.getString("name_owner"),
                                        document.getString("address_owner"),
                                        document.getString("details_product"),
                                        document.getString("url_image")
                                );
                                products.add(pojo);
                                mAdapter.notifyDataSetChanged();
                                hide_progress();
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                            showText();

                        }
                    }

                });

    }

    private void hide_progress() {
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.loading_indicator);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void showText() {
        hide_progress();
        TextView empty_text = (TextView)findViewById(R.id.empty_view);
        empty_text.setVisibility(View.VISIBLE);
    }
}
