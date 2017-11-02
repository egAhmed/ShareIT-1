package matheus.costa.shareit.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import matheus.costa.shareit.R;
import matheus.costa.shareit.firebase.Database;
import matheus.costa.shareit.firebase.DatabaseCallback;
import matheus.costa.shareit.objects.Message;
import matheus.costa.shareit.objects.User;
import matheus.costa.shareit.settings.GlobalSettings;

/**
 * Created by Matheus on 28/10/2017.
 */

public class FeedAdapter extends RecyclerView.Adapter {

    private final String LTAG = getClass().getSimpleName();

    private List<Message> messages;
    private Context context;
    private User user;

    public FeedAdapter(Context context, List<Message> messages) {
        this.messages = messages;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.feed_row,parent,false);
        FeedListViewHolder viewHolder = new FeedListViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final FeedListViewHolder viewHolder = (FeedListViewHolder) holder;

        final Message message = messages.get(position);
        user = new User();

        if (message.getMessageUserId() != null){
            Log.i(LTAG,"onBindViewHolder() Loading info...");
            Log.i(LTAG,"onBindViewHolder() Message Author: " + message.getMessageUserId());
            Log.i(LTAG,"onBindViewHolder() Message ID: " + message.getMessageUserId());

            Database.getInstance().retrieveUser(message.getMessageUserId(), new DatabaseCallback() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);

                    StorageReference imagemPerfilReference = FirebaseStorage.getInstance().getReferenceFromUrl(user.getUserProfileImage());
                    Glide.with(context).using(new FirebaseImageLoader()).load(imagemPerfilReference).bitmapTransform(new CenterCrop(context)).into(viewHolder.getIvProfileFeed());
                    viewHolder.getTvNameFeed().setText(user.getUserName());
                    Log.i(LTAG,"onBindViewHolder() User: " + user.toString());
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot) {}
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot) {}
                @Override
                public void onCanceled(DatabaseError databaseError) {}
            });

            viewHolder.getTvMessageFeed().setText(message.getMessageContent());
            viewHolder.getTvRateFeed().setText(String.valueOf(message.getMessageRate()));
            viewHolder.getTvTimeStampFeed().setText(message.getMessageTimeStamp());

            if (message.getMessageRateUsersId().contains(GlobalSettings.getInstance().getAuthenticatedUser().getUserUid())){
                //If the current authenticated user rated the specific message
                viewHolder.getBtnLike().setBackground(context.getResources().getDrawable(R.drawable.ic_thumb_up_green_24dp));
            }else{
                viewHolder.getBtnLike().setBackground(context.getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp));
            }

            //Button click event
            viewHolder.getBtnLike().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    message.userRate(GlobalSettings.getInstance().getAuthenticatedUser().getUserUid());
                    Database.getInstance().updateMessageRate(message);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
