package com.example.vanbites.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vanbites.Model.Menu_model;
import com.example.vanbites.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class MenuItems extends AppCompatActivity {

    DatabaseReference menu;

    FirebaseRecyclerOptions<Menu_model> options;
    FirebaseRecyclerAdapter adapter;

    RecyclerView recyclerViewMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_items);

        // make activity fullscreen
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


        String cat = getIntent().getExtras().getString("CAT", "");

        // Firebase initiation
        menu = FirebaseDatabase.getInstance().getReference().child(cat);

        recyclerViewMenu = findViewById(R.id.recyclerViewMenu);
        recyclerViewMenu.setHasFixedSize(true);
        recyclerViewMenu.setLayoutManager(new LinearLayoutManager(this));
        loadMenu();


    }


    // https://firebaseopensource.com/projects/firebase/firebaseui-android/database/readme/
    // FirebaseRecyclerAdapter — binds a Query to a RecyclerView and responds to all real-time events
    // included items being added, removed, moved, or changed.
    // Best used with small result sets since all results are loaded at once.


    private void loadMenu() {
        options = new FirebaseRecyclerOptions.Builder<Menu_model>().setQuery(menu, Menu_model.class).build();
        adapter = new FirebaseRecyclerAdapter<Menu_model, MenuViewHolder>(options) {
            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Create a new instance of the MenuViewHolder, in this case we are using a custom
                // layout called R.layout.menu_item for each item
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_menu_item, parent, false);
                return new MenuViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull Menu_model model) {
                // Bind the Category object to the MenuViewHolder
                holder.txtViewMenuItemName.setText(model.getName());

                // https://square.github.io/picasso/
                Picasso.get().load(model.getImage()).into(holder.imgViewMenuImage);

                // Category clickItem = model;
            }
        };

        adapter.startListening();
        recyclerViewMenu.setAdapter(adapter);
    }
}