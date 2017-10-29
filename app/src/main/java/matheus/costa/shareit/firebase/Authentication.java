package matheus.costa.shareit.firebase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import matheus.costa.shareit.settings.GlobalSettings;

/**
 * Created by Matheus on 23/10/2017.
 */

public class Authentication {
    private final String LTAG = getClass().getSimpleName();

    private static final Authentication ourInstance = new Authentication();

    public static Authentication getInstance() {
        return ourInstance;
    }

    private Authentication() {
    }

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    public void register(String email, String password, final AuthenticationCallBack callBack){
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    callBack.onComplete();
                }else{
                    callBack.onFailure();
                    Log.w(LTAG,"Register Failure! -> " + task.getException());
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                callBack.onSuccess(authResult); //can get a FirebaseUser
            }
        });
    }



    public void login(String email, String password, final AuthenticationCallBack callBack){
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    callBack.onComplete();
                }else{
                    callBack.onFailure();
                }
            }
        });
    }



    public FirebaseUser retrieveAuthUser(){
        return firebaseAuth.getCurrentUser();
    }



    public void logout(){
        FirebaseAuth.getInstance().signOut();
        GlobalSettings.getInstance().saveAuthenticatedUser(null);
    }

}
