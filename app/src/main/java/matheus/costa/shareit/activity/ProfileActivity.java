package matheus.costa.shareit.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import matheus.costa.shareit.R;
import matheus.costa.shareit.firebase.Database;
import matheus.costa.shareit.firebase.DatabaseCallback;
import matheus.costa.shareit.objects.AppNotification;
import matheus.costa.shareit.objects.Message;
import matheus.costa.shareit.objects.User;
import matheus.costa.shareit.settings.ApplicationConstants;
import matheus.costa.shareit.settings.GlobalSettings;
import matheus.costa.shareit.utils.InterfaceUtils;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private final String LTAG = getClass().getSimpleName();

    private TextView tvNameProfile;
    private TextView tvEmailProfile;
    private TextView tvFriendsCounter;
    private Button btnAddFriend;
    private ImageView ivProfileImageProfile;
    private RelativeLayout header;
    private TextView tvRecentActivityLabel;
    private RecyclerView lvUserMessages;
    private ScrollView scrollViewProfile;

    private User user;

    private FeedAdapter adapter;
    private List<Message> messages;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvNameProfile = (TextView) findViewById(R.id.tvNameProfile);
        tvEmailProfile = (TextView) findViewById(R.id.tvEmailProfile);
        tvFriendsCounter = (TextView) findViewById(R.id.tvFriendsCounter);
        ivProfileImageProfile = (ImageView) findViewById(R.id.ivProfileImageProfile);
        lvUserMessages = (RecyclerView) findViewById(R.id.lvUserMessages);
        scrollViewProfile = (ScrollView) findViewById(R.id.scrollViewProfile);
        (btnAddFriend = (Button) findViewById(R.id.btnAddFriend)).setOnClickListener(this);
        (header = (RelativeLayout) findViewById(R.id.header)).setOnClickListener(this);
        (tvRecentActivityLabel = (TextView) findViewById(R.id.tvRecentActivityLabel)).setOnClickListener(this);

        //Receives the user object
        user = (User) getIntent().getSerializableExtra(MainActivity.USER_EXTRA);

        //Toolbar title
        getSupportActionBar().setTitle(user.getUserName());

        messages = new ArrayList<>();
        showUserInfo();
        loadUserMessages();
        adapter = new FeedAdapter(this,messages);
        lvUserMessages.setAdapter(adapter);

        //Setando o layout do RecyclerView para LinearLayout
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        lvUserMessages.setLayoutManager(layout);

        //Setando o Divider
        Drawable dividerDrawable = ContextCompat.getDrawable(this,R.drawable.divider);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getBaseContext(), LinearLayout.VERTICAL);
        dividerItemDecoration.setDrawable(dividerDrawable);
        lvUserMessages.addItemDecoration(dividerItemDecoration);

        //Removing the button if the user equals authenticate user
        if (user.getUserUid().equals(GlobalSettings.getInstance().getAuthenticatedUser().getUserUid())){
            btnAddFriend.setVisibility(View.GONE);
        }
    }



    private void loadUserMessages(){
        Database.getInstance().retrieveMessagesByUser(user.getUserUid(), new DatabaseCallback() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot) {
                Message m = dataSnapshot.getValue(Message.class);
                m.setMessageId(dataSnapshot.getRef().getKey());
                messages.add(m);
                Collections.sort(messages);
                Collections.reverse(messages);
                Log.i(LTAG,"Retrieve one message");
                adapter.notifyDataSetChanged();
                scrollViewProfile.scrollTo(0,0);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot) {
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onCanceled(DatabaseError databaseError) {}
        });
    }



    private void showUserInfo(){
        tvNameProfile.setText(user.getUserName());
        tvEmailProfile.setText(user.getUserEmail());
        StorageReference imagemPerfilReference = FirebaseStorage.getInstance().getReferenceFromUrl(user.getUserProfileImage());
        Glide.with(this).using(new FirebaseImageLoader()).load(imagemPerfilReference).bitmapTransform(new CenterCrop(this)).into(ivProfileImageProfile);
        tvFriendsCounter.setText("0");
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAddFriend:
                SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
                String date = sdf.format(Calendar.getInstance().getTime());

                Database.getInstance().friendNotification(
                        new AppNotification()
                                .setNotificationUserOwner(user.getUserUid())
                                .setNotificationType(ApplicationConstants.NOTIF_TYPE_FRIEND_REQ)
                                .setNotificationChecked(false)
                                .setNotificationContent(GlobalSettings.getInstance().getAuthenticatedUser().getUserName() +
                                        "," + GlobalSettings.getInstance().getAuthenticatedUser().getUserUid())
                                .setNotificationTimeStamp(date));

                break;

            case R.id.header:
                scrollViewProfile.smoothScrollTo(0,InterfaceUtils.getScreenHeight(this));
                break;

            case R.id.tvRecentActivityLabel:
                scrollViewProfile.smoothScrollTo(0,InterfaceUtils.getScreenHeight(this));
                break;
        }
    }
}
