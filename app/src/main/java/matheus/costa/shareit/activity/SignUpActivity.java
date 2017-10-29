package matheus.costa.shareit.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.InputStream;

import matheus.costa.shareit.R;
import matheus.costa.shareit.firebase.Authentication;
import matheus.costa.shareit.firebase.AuthenticationCallBack;
import matheus.costa.shareit.firebase.Database;
import matheus.costa.shareit.firebase.Storage;
import matheus.costa.shareit.firebase.StorageCallback;
import matheus.costa.shareit.objects.User;
import matheus.costa.shareit.settings.GlobalSettings;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    private final String LTAG = getClass().getSimpleName();

    private TextInputLayout inputLayoutName;
    private TextInputLayout inputLayoutEmail;
    private TextInputLayout inputLayoutPassword;
    private TextInputLayout inputLayoutConfirmPassword;
    private EditText etEmail;
    private EditText etName;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button btnProfileImage;
    private ImageView ivProfileImage;

    private boolean profileImageChoosed = false; //user has picked a image
    private Bitmap profileImageBMP;
    private static final int CHOOSE_IMAGE = 1;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        (etEmail = (EditText) findViewById(R.id.etEmail)).addTextChangedListener(new FormTextWatcher(etEmail));
        (etName = (EditText) findViewById(R.id.etName)).addTextChangedListener(new FormTextWatcher(etName));
        (etPassword = (EditText) findViewById(R.id.etPassword)).addTextChangedListener(new FormTextWatcher(etPassword));
        (etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword)).addTextChangedListener(new FormTextWatcher(etConfirmPassword));
        inputLayoutName = (TextInputLayout) findViewById(R.id.inputLayoutName);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.inputLayoutEmail);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.inputLayoutPassword);
        inputLayoutConfirmPassword = (TextInputLayout) findViewById(R.id.inputLayoutConfirmPassword);
        (btnProfileImage = (Button) findViewById(R.id.btnProfileImage)).setOnClickListener(this);
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnProfileImage:
                chooseImage();
                break;
        }
    }



    private void registerUser(){
        user = new User();
        if (validateName() && validateEmail() && validateConfirmPassword()){

            user.setUserName(etName.getText().toString());
            user.setUserEmail(etEmail.getText().toString());

            Authentication.getInstance().register(user.getUserEmail(), etPassword.getText().toString(),
                    new AuthenticationCallBack() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            user.setUserUid(authResult.getUser().getUid());
                            uploadProfileImage(user.getUserUid());
                            Log.i(LTAG,"Register Success!");

                            GlobalSettings.getInstance().saveAuthenticatedUser(user);
                            Intent it = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(it);
                            finish();
                        }

                        @Override
                        public void onFailure() {
                            AlertDialog.Builder msg = new AlertDialog.Builder(SignUpActivity.this);
                            msg.setTitle(getString(R.string.error));
                            msg.setMessage(getString(R.string.error_register));
                            msg.setNeutralButton("OK",null);
                            msg.show();
                            Log.w(LTAG,"Register Failure!");
                        }

                        @Override
                        public void onComplete() {
                            Log.i(LTAG,"Register Complete!");
                        }
            });
        }
    }



    private void uploadProfileImage(String userUid){
        Storage.getInstance().uploadImage(profileImageBMP, userUid, new StorageCallback() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                user.setUserProfileImage(taskSnapshot.getDownloadUrl().toString());
            }

            @Override
            public void onComplete() {
                Database.getInstance().saveUser(user);

                //TODO MANDAR PARA TELA DE FEED
                AlertDialog.Builder msg = new AlertDialog.Builder(SignUpActivity.this);
                msg.setTitle(getString(R.string.welcome));
                msg.setMessage(getString(R.string.register_successfully));
                msg.setNeutralButton("OK",null);
                msg.show();
            }

            @Override
            public void onFailure() {
                AlertDialog.Builder msg = new AlertDialog.Builder(SignUpActivity.this);
                msg.setTitle(getString(R.string.error));
                msg.setMessage(getString(R.string.error_register));
                msg.setNeutralButton("OK",null);
                msg.show();
            }
        });
    }



    private void chooseImage(){
        Intent it = new Intent();
        it.setType("image/*");
        it.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(it,getString(R.string.choose_image)),CHOOSE_IMAGE);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE){
            if (resultCode == Activity.RESULT_OK){
                try{
                    InputStream is = this.getContentResolver().openInputStream(data.getData());
                    BufferedInputStream bfis = new BufferedInputStream(is);
                    profileImageBMP = BitmapFactory.decodeStream(bfis);

                    ivProfileImage.setImageBitmap(profileImageBMP);
                    profileImageChoosed = true;
                }catch (Throwable t){
                    //TODO Tratar erro
                    t.printStackTrace();
                }
            }else {
                AlertDialog.Builder msg = new AlertDialog.Builder(SignUpActivity.this);
                msg.setTitle(getString(R.string.warning));
                msg.setMessage(getString(R.string.you_need_image));
                msg.setNeutralButton("OK",null);
                msg.show();
            }
        }
    }



    private boolean validateName(){
        String name = etName.getText().toString();

        if (name.isEmpty()){
            inputLayoutName.setError(getString(R.string.invalid_name));
            return false;
        }else{
            inputLayoutName.setErrorEnabled(false);
            return true;
        }
    }



    private boolean validateEmail(){
        String email = etEmail.getText().toString();

        if (email.isEmpty()){
            inputLayoutEmail.setError(getString(R.string.invalid_email));
            return false;
        }else{
            inputLayoutEmail.setErrorEnabled(false);
            return true;
        }
    }



    private String validatePassword(){
        String pass = etPassword.getText().toString();

        if (pass.isEmpty() || pass.length() < 8){
            inputLayoutPassword.setError(getString(R.string.invalid_password));
        }else{
            inputLayoutPassword.setErrorEnabled(false);
        }
        return pass;
    }



    private boolean validateConfirmPassword(){
        String confirmPass = etConfirmPassword.getText().toString();

        if (confirmPass.isEmpty() || confirmPass.length() < 8){
            inputLayoutConfirmPassword.setError(getString(R.string.invalid_password));
            return false;
        }else{
            if (confirmPass.equals(validatePassword())){
                inputLayoutConfirmPassword.setErrorEnabled(false);
                return true;
            }else{
                inputLayoutConfirmPassword.setError(getString(R.string.diff_password));
                return false;
            }
        }
    }


    ///////TextWatcher for EditTexts validate

    private class FormTextWatcher implements TextWatcher{
        private View view;
        public FormTextWatcher(View view) {this.view = view;}
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override
        public void afterTextChanged(Editable s) {
            switch (view.getId()){
                case R.id.etName: validateName(); break;
                case R.id.etEmail: validateEmail(); break;
                case R.id.etPassword: validatePassword(); break;
                case R.id.etConfirmPassword: validateConfirmPassword(); break;
            }
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_signup, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_register) {
            registerUser();
        }

        return super.onOptionsItemSelected(item);
    }
}
