package matheus.costa.shareit.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import matheus.costa.shareit.R;
import matheus.costa.shareit.firebase.Authentication;
import matheus.costa.shareit.firebase.Database;
import matheus.costa.shareit.firebase.DatabaseCallback;
import matheus.costa.shareit.objects.Message;
import matheus.costa.shareit.objects.User;
import matheus.costa.shareit.settings.GlobalSettings;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String LTAG = getClass().getSimpleName();

    private FloatingActionButton fab;
    private RecyclerView lvFeed;
    private List<Message> messages;
    private FeedAdapter adapter;
    private ImageView ivProfileImageToolbar;
    private TextView tvNameToolbar;
    private User user;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        (fab = (FloatingActionButton) findViewById(R.id.fab)).setOnClickListener(this);
        lvFeed = (RecyclerView) findViewById(R.id.lvFeed);
        ivProfileImageToolbar = (ImageView) findViewById(R.id.ivProfileImageToolbar);
        tvNameToolbar = (TextView) findViewById(R.id.tvNameToolbar);

        messages = new ArrayList<>();
        load();
        adapter = new FeedAdapter(this,messages);
        lvFeed.setAdapter(adapter);

        loadUserInfo();

        //Setando o layout do RecyclerView para LinearLayout
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        //Setando o Divider
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(lvFeed.getContext(), LinearLayout.HORIZONTAL);
        dividerItemDecoration.setDrawable(getBaseContext().getResources().getDrawable(R.drawable.divider));
        lvFeed.setLayoutManager(layout);
        lvFeed.addItemDecoration(dividerItemDecoration);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                Intent it = new Intent(MainActivity.this, NewMessageActivity.class);
                startActivity(it);
                break;
        }
    }



    private void load(){
        Log.i(LTAG,"LoadFeed");
        Database.getInstance().loadFeed(new DatabaseCallback() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot) {
                Message m = dataSnapshot.getValue(Message.class);
                messages.add(m);
                Collections.sort(messages);
                Collections.reverse(messages);
                Log.i(LTAG,"Retrieve one message");
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot) {
                Message m = dataSnapshot.getValue(Message.class);
                messages.add(m);
                Log.i(LTAG,"Change one message");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Message m = dataSnapshot.getValue(Message.class);
                if (messages.contains(m)){
                    messages.remove(m);
                }
                Log.i(LTAG,"Remove one message");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCanceled(DatabaseError databaseError) {
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            Authentication.getInstance().logout();
            Intent it = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(it);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }



    private void loadUserInfo(){
        user = GlobalSettings.getInstance().getAuthenticatedUser();

        Log.i(LTAG,"User: " + user.toString());

        if (user != null){
            if (user.getUserProfileImage() != null){
                //Criando referencia de um arquivo a partir de uma url HTTPS do firebase storage
                StorageReference imagemPerfilReference = FirebaseStorage.getInstance().getReferenceFromUrl(user.getUserProfileImage());
                Glide.with(this).using(new FirebaseImageLoader()).load(imagemPerfilReference).bitmapTransform(new CenterCrop(this)).into(ivProfileImageToolbar);
            }

            tvNameToolbar.setText(user.getUserName());
        }
    }
}
