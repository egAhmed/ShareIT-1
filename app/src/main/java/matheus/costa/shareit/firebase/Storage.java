package matheus.costa.shareit.firebase;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;


import matheus.costa.shareit.settings.ApplicationConstants;

/**
 * Created by Matheus on 26/10/2017.
 */

public class Storage {

    private static final Storage ourInstance = new Storage();
    public static Storage getInstance() {
        return ourInstance;
    }
    private final String LTAG = getClass().getSimpleName();


    //App Reference
    private StorageReference appReference = FirebaseStorage.getInstance().getReference();
    //Reference to "ProfileImages" directory
    private StorageReference profileImages = appReference.child(ApplicationConstants.PROGILE_IMAGES);


    private Storage() {
    }


    public void uploadImage(Bitmap bmp, String userUid, final StorageCallback callback){

        //Reference to the image
        StorageReference imageRef = profileImages.child(userUid + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);

        //Failure
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure();
                Log.e(LTAG,"Profile image upload error!",e);
            }
        });

        //Success (Image can be uploaded and the url has generated)
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                callback.onSuccess(taskSnapshot);
                Log.i(LTAG,"Profile image upload success!");
            }
        });

        //Complete (Image upload has complete)
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                callback.onComplete();
                Log.i(LTAG,"Profile image upload completed!");
            }
        });
    }

}
