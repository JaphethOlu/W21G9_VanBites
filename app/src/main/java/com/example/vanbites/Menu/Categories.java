package com.example.vanbites.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.vanbites.Interface.ItemClickListener;
import com.example.vanbites.Model.Category_model;
import com.example.vanbites.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


public class Categories extends AppCompatActivity {

    DatabaseReference category;

    FirebaseRecyclerOptions<Category_model> options;
    FirebaseRecyclerAdapter adapter;

    RecyclerView recyclerViewCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        // make activity fullscreen
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        // Firebase initiation
        category = FirebaseDatabase.getInstance().getReference().child("Category");

        recyclerViewCategory = findViewById(R.id.recyclerViewCategory);
        recyclerViewCategory.setHasFixedSize(true);
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this));
        loadCategories();

    }

    private void loadCategories() {
        // https://firebaseopensource.com/projects/firebase/firebaseui-android/database/readme/
        // FirebaseRecyclerAdapter — binds a Query to a RecyclerView and responds to all real-time events
        // included items being added, removed, moved, or changed.
        // Best used with small result sets since all results are loaded at once.


        options =  new FirebaseRecyclerOptions.Builder<Category_model>().setQuery(category, Category_model.class).build();
        adapter = new FirebaseRecyclerAdapter<Category_model, CategoryViewHolder>(options) {
            @NonNull
            @Override
            public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Create a new instance of the MenuViewHolder, in this case we are using a custom
                // layout called R.layout.category_item for each item
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_category_item, parent, false);
                return new CategoryViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull CategoryViewHolder holder, int position, @NonNull Category_model model) {
                // Bind the Category object to the MenuViewHolder
                holder.txtViewCategoryName.setText(model.getName());

                // https://square.github.io/picasso/
                Picasso.get().load(model.getImage()).into(holder.imgViewCategoryImage);

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Bundle bundle = new Bundle();
                        bundle.putString("CAT", model.getName());

                        Intent cat = new Intent(Categories.this, MenuItems.class);
                        cat.putExtras(bundle);
                        startActivity(cat);

                        // Toast.makeText(Categories.this, "Clicked on " + model.getName(), Toast.LENGTH_SHORT).show();

                    }
                });

            }
        };

        adapter.startListening();
        recyclerViewCategory.setAdapter(adapter);

    }
}