package matheus.costa.shareit.firebase;

import com.google.firebase.storage.UploadTask;

/**
 * Created by Matheus on 28/10/2017.
 */

public interface StorageCallback {
    void onSuccess(UploadTask.TaskSnapshot taskSnapshot);
    void onComplete();
    void onFailure();
}
