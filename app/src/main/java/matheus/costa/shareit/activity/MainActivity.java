package matheus.costa.shareit.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import matheus.costa.shareit.service.NotificationService;
import matheus.costa.shareit.settings.GlobalSettings;
import matheus.costa.shareit.sqlite.UserLocalSave;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String LTAG = getClass().getSimpleName();

    private FloatingActionButton fab;
    private RecyclerView lvFeed;
    private List<Message> messages;
    private FeedAdapter adapter;
    private ImageView ivProfileImageToolbar;
    private TextView tvNameToolbar;
    private User user;
    private RelativeLayout btnLayoutUserAuth;
    private ImageButton btnNotifications;

    protected static final int REQUEST_MESSAGE = 10;
    protected static final int RESULT_MESSAGE = 20;
    protected static final String USER_EXTRA = "user";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        (fab = (FloatingActionButton) findViewById(R.id.fab)).setOnClickListener(this);
        (btnLayoutUserAuth = (RelativeLayout) findViewById(R.id.btnLayoutUserAuth)).setOnClickListener(this);
        (btnNotifications = (ImageButton) findViewById(R.id.btnNotifications)).setOnClickListener(this);
        lvFeed = (RecyclerView) findViewById(R.id.lvFeed);
        ivProfileImageToolbar = (ImageView) findViewById(R.id.ivProfileImageToolbar);
        tvNameToolbar = (TextView) findViewById(R.id.tvNameToolbar);

        messages = new ArrayList<>();
        loadFeed();
        adapter = new FeedAdapter(this,messages);
        lvFeed.setAdapter(adapter);

        loadUserInfo();

        //Setando o layout do RecyclerView para LinearLayout
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        lvFeed.setLayoutManager(layout);

        //Setando o Divider
        Drawable dividerDrawable = ContextCompat.getDrawable(this,R.drawable.divider);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getBaseContext(),LinearLayout.VERTICAL);
        dividerItemDecoration.setDrawable(dividerDrawable);
        lvFeed.addItemDecoration(dividerItemDecoration);

        //Start NotificationService
        Intent intent = new Intent(this,NotificationService.class);
        startService(intent);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                Intent it = new Intent(MainActivity.this, NewMessageActivity.class);
                startActivityForResult(it,REQUEST_MESSAGE);
                break;

            case R.id.btnLayoutUserAuth:
                goToProfile(GlobalSettings.getInstance().getAuthenticatedUser());
                break;

            case R.id.btnNotifications:

                break;

        }
    }



    private void loadFeed(){
        Log.i(LTAG,"LoadFeed");
        Database.getInstance().loadFeed(new DatabaseCallback() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot) {
                Message m = dataSnapshot.getValue(Message.class);
                m.setMessageId(dataSnapshot.getRef().getKey());
                messages.add(m);
                Collections.sort(messages);
                Collections.reverse(messages);
                Log.i(LTAG,"Retrieve one message");
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot) {
                adapter.notifyDataSetChanged();
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



    private void goToProfile(User user){
        Intent it = new Intent(MainActivity.this, ProfileActivity.class);
        it.putExtra(USER_EXTRA,user);
        startActivity(it);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MESSAGE){
            if (resultCode == RESULT_MESSAGE){
                messages.clear();
                loadFeed();
                Snackbar.make(findViewById(R.id.fab),getString(R.string.message_sent), Snackbar.LENGTH_LONG).show();
            }
        }
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
            //Remove user from local sqlite
            UserLocalSave localSave = new UserLocalSave(this);
            localSave.removeUser(user);

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
