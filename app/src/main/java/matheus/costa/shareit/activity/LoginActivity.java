package matheus.costa.shareit.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import org.w3c.dom.Text;

import matheus.costa.shareit.R;
import matheus.costa.shareit.firebase.Authentication;
import matheus.costa.shareit.firebase.AuthenticationCallBack;
import matheus.costa.shareit.firebase.Database;
import matheus.costa.shareit.firebase.DatabaseCallback;
import matheus.costa.shareit.service.NotificationService;
import matheus.costa.shareit.settings.GlobalSettings;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private final String LTAG = getClass().getSimpleName();

    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextView tvSignup;
    private Button btnLogin;
    private LoginActivity activityContext = this;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etEmail = (TextInputEditText) findViewById(R.id.etEmail);
        etPassword = (TextInputEditText) findViewById(R.id.etPassword);
        (btnLogin = (Button) findViewById(R.id.btnLogin)).setOnClickListener(this);
        (tvSignup = (TextView) findViewById(R.id.tvAccountText)).setOnClickListener(this);

        verifyAuthenticatedUser();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btnLogin:
                if (validateFields()){
                    Authentication.getInstance().login(etEmail.getText().toString(), etPassword.getText().toString(),
                            new AuthenticationCallBack() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Log.i(LTAG,"Login success!");
                                }

                                @Override
                                public void onFailure() {
                                    AlertDialog.Builder msg = new AlertDialog.Builder(LoginActivity.this);
                                    msg.setTitle(getString(R.string.error));
                                    msg.setMessage(getString(R.string.invalid_login));
                                    msg.setNeutralButton("OK",null);
                                    msg.show();
                                }

                                @Override
                                public void onComplete() {
                                    Log.i(LTAG,"Login complete!");
                                    verifyAuthenticatedUser();
                                }
                            });
                }
                break;

            case R.id.tvAccountText:
                Intent it = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(it);
                break;
        }
    }



    private void verifyAuthenticatedUser(){
        FirebaseUser firebaseUser = Authentication.getInstance().retrieveAuthUser();

        if (firebaseUser != null){
            Log.i(LTAG,"User actually authenticated!");
            Log.i(LTAG,"User email: " + firebaseUser.getEmail());

            Database.getInstance().retrieveAuthUser(this,firebaseUser.getUid(), new DatabaseCallback() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot) {}
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot) {
                    Intent it = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(it);
                    finish();
                }
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot) {}
                @Override
                public void onCanceled(DatabaseError databaseError) {}
            });
        }
    }



    private boolean validateFields(){
        if (etEmail.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty()){
            AlertDialog.Builder msg = new AlertDialog.Builder(LoginActivity.this);
            msg.setTitle(getString(R.string.error));
            msg.setMessage(getString(R.string.invalid_login));
            msg.setNeutralButton("OK",null);
            msg.show();
            return false;
        }else{
            return true;
        }
    }
}
