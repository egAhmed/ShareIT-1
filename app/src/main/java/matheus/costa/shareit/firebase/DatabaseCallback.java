package matheus.costa.shareit.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * Created by Matheus on 28/10/2017.
 */

public interface DatabaseCallback {
    void onChildAdded(DataSnapshot dataSnapshot);
    void onChildChanged(DataSnapshot dataSnapshot);
    void onChildRemoved(DataSnapshot dataSnapshot);
    void onChildMoved(DataSnapshot dataSnapshot);
    void onCanceled(DatabaseError databaseError);
}
