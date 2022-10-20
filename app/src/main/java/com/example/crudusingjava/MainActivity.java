package com.example.crudusingjava;

import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.crudusingjava.model.Category;
import com.example.crudusingjava.model.CategoryAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RequestQueue requestQueue;
    private SwipeRefreshLayout refreshLayout;
    private final ArrayList<Category> categories = new ArrayList<>();
    private JsonArrayRequest arrayRequest;
    private RecyclerView recyclerView;
    private Dialog dialog;
    private CategoryAdapter categoryAdapter;
    private String url = "http://192.168.101.165/perpus/api/category/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeDown);
        recyclerView = (RecyclerView) findViewById(R.id.category);
        dialog= new Dialog(this);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                categories.clear();
                getData();
            }
        });
    }

    private void getData() {
        refreshLayout.setRefreshing(true);
        arrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        Category cat = new Category();
                        cat.setId(jsonObject.getInt("id"));
                        cat.setCategory(jsonObject.getString("kategori"));
                        categories.add(cat);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapterPush(categories);
                refreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(arrayRequest);
    }

    private void adapterPush(ArrayList<Category> categories) {
        categoryAdapter = new CategoryAdapter(this, categories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(categoryAdapter);
    }
    @Override
    public void onRefresh () {
        categories.clear();
        getData();
    }
}