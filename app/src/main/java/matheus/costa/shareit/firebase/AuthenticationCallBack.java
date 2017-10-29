package matheus.costa.shareit.firebase;

import com.google.firebase.auth.AuthResult;

/**
 * Created by Matheus on 28/10/2017.
 */

public interface AuthenticationCallBack {
    void onSuccess(AuthResult authResult);
    void onFailure();
    void onComplete();
}
