package matheus.costa.shareit.firebase;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import matheus.costa.shareit.objects.Message;
import matheus.costa.shareit.objects.User;
import matheus.costa.shareit.settings.ApplicationConstants;
import matheus.costa.shareit.settings.GlobalSettings;

/**
 * Created by Matheus on 23/10/2017.
 */

public class Database {
    private final String LTAG = getClass().getSimpleName();

    private static final Database ourInstance = new Database();

    public static Database getInstance() {
        return ourInstance;
    }

    private Database() {
    }


    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    private DatabaseReference appReference = firebaseDatabase.getReference();
    private DatabaseReference userReference = appReference.child(ApplicationConstants.USERS);
    private DatabaseReference messageReference = appReference.child(ApplicationConstants.MESSAGES);


    //Only in register time
    public void saveUser(User user){
        DatabaseReference specificUserRef = userReference.child(user.getUserUid());

        specificUserRef.child(ApplicationConstants.USER_NAME).setValue(user.getUserName());
        specificUserRef.child(ApplicationConstants.USER_EMAIL).setValue(user.getUserEmail());
        specificUserRef.child(ApplicationConstants.USER_PROFILE_IMAGE).setValue(user.getUserProfileImage());
    }


    //https://www.firebase.com/docs/android/guide/saving-data.html
    public void saveMessage(Message message, String userUid){
        DatabaseReference specificMessageRef = messageReference.push();

        specificMessageRef.child(ApplicationConstants.MESSAGE_USER_UID).setValue(userUid);
        specificMessageRef.child(ApplicationConstants.MESSAGE_CONTENT).setValue(message.getMessageContent());
        specificMessageRef.child(ApplicationConstants.MESSAGE_RATE).setValue(message.getMessageRate());
        specificMessageRef.child(ApplicationConstants.MESSAGE_TIMESTAMP).setValue(message.getMessageTimeStamp());
    }



    public void loadFeed(final DatabaseCallback callback){
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i(LTAG,"onChildAdded() ");
                callback.onChildAdded(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.i(LTAG,"onChildChanged() ");
                callback.onChildChanged(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.i(LTAG,"onChildRemoved() ");
                callback.onChildRemoved(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.i(LTAG,"onChildMoved() ");
                callback.onChildMoved(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(LTAG,"onCancelled() ");
                callback.onCanceled(databaseError);
            }
        };
        messageReference.addChildEventListener(childEventListener);
    }



    public void retrieveAuthUser(final String userUid, final DatabaseCallback callback){
        DatabaseReference specificUserRef = userReference.child(userUid);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(LTAG,"UserUID: " + userUid);
                User user = dataSnapshot.getValue(User.class);
                user.setUserUid(userUid);
                GlobalSettings.getInstance().saveAuthenticatedUser(user);
                callback.onChildChanged(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        specificUserRef.addListenerForSingleValueEvent(valueEventListener);
    }



    public void retrieveUser(String userUid, final DatabaseCallback callback){
        DatabaseReference specificUserRef = userReference.child(userUid);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.onChildAdded(dataSnapshot);
                Log.i(LTAG,"retrieveUser() -> onDataChange()");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCanceled(databaseError);
            }
        };
        specificUserRef.addValueEventListener(valueEventListener);
    }

}
