package com.example.vanbites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vanbites.Model.Category_model;
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

//    String firebaseUrl = getString(R.string.storageReferenceImages);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        // Firebase initiation
        category = FirebaseDatabase.getInstance().getReference().child("Category");

        recyclerViewCategory = findViewById(R.id.recyclerViewCategory);
        recyclerViewCategory.setHasFixedSize(true);
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this));
        loadMenu();

    }

    private void loadMenu() {


        // https://firebaseopensource.com/projects/firebase/firebaseui-android/database/readme/
        // FirebaseRecyclerAdapter — binds a Query to a RecyclerView and responds to all real-time events
        // included items being added, removed, moved, or changed.
        // Best used with small result sets since all results are loaded at once.


        options =  new FirebaseRecyclerOptions.Builder<Category_model>().setQuery(category, Category_model.class).build();
        adapter = new FirebaseRecyclerAdapter<Category_model, MenuViewHolder>(options) {
            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Create a new instance of the MenuViewHolder, in this case we are using a custom
                // layout called R.layout.menu_item for each item
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_menu_category_item, parent, false);
                return new MenuViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull Category_model model) {
                // Bind the Category object to the MenuViewHolder
                holder.txtViewCategoryName.setText(model.getName());

                // https://square.github.io/picasso/
                Picasso.get().load(model.getImage()).into(holder.imgViewCategoryImage);

               // Category clickItem = model;
            }
        };

        adapter.startListening();
        recyclerViewCategory.setAdapter(adapter);

    }
}