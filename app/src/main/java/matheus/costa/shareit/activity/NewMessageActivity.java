package matheus.costa.shareit.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import matheus.costa.shareit.R;
import matheus.costa.shareit.firebase.Database;
import matheus.costa.shareit.objects.Message;
import matheus.costa.shareit.objects.User;
import matheus.costa.shareit.settings.GlobalSettings;

public class NewMessageActivity extends AppCompatActivity implements View.OnClickListener {

    private final String LTAG = getClass().getSimpleName();
    private ImageView ivProfileNewMessage;
    private TextView tvNameNewMessage;
    private EditText etMessageNewMessage;
    private FloatingActionButton fab;
    private User user;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        (fab = (FloatingActionButton) findViewById(R.id.fab)).setOnClickListener(this);
        ivProfileNewMessage = (ImageView) findViewById(R.id.ivProfileNewMessage);
        tvNameNewMessage = (TextView) findViewById(R.id.tvNameNewMessage);
        etMessageNewMessage = (EditText) findViewById(R.id.etMessageNewText);

        loadUserInfo();
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.fab:
                String text = etMessageNewMessage.getText().toString();
                if (!text.isEmpty()){
                    Message message = new Message();
                    message.setMessageContent(text);
                    message.setMessageRate(0);
                    message.setMessageUserId(user.getUserUid());

                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
                    String date = sdf.format(Calendar.getInstance().getTime());
                    message.setMessageTimeStamp(date);
                    Log.i(LTAG,"Message time stamp: " + Calendar.getInstance().getTime());

                    Database.getInstance().saveMessage(message,user.getUserUid());

                    setResult(MainActivity.RESULT_MESSAGE);
                    finish();
                }else{
                    AlertDialog.Builder msg = new AlertDialog.Builder(NewMessageActivity.this);
                    msg.setTitle(getString(R.string.error));
                    msg.setMessage(getString(R.string.message_invalid));
                    msg.setNeutralButton("OK",null);
                    msg.show();
                }
                break;
        }
    }



    private void loadUserInfo(){
        user = GlobalSettings.getInstance().getAuthenticatedUser();

        if (user != null){
            if (user.getUserProfileImage() != null){
                //Criando referencia de um arquivo a partir de uma url HTTPS do firebase storage
                StorageReference imagemPerfilReference = FirebaseStorage.getInstance().getReferenceFromUrl(user.getUserProfileImage());
                Glide.with(this).using(new FirebaseImageLoader()).load(imagemPerfilReference).bitmapTransform(new CenterCrop(this)).into(ivProfileNewMessage);
            }

            tvNameNewMessage.setText(user.getUserName());
        }
    }
}
