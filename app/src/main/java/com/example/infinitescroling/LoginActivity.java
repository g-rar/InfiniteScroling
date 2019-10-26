package com.example.infinitescroling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "Login activity: ";
    private int CREAR_CUENTA = 1;
    private int RC_SIGN_IN = 7;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;

    private EditText emailInput;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(firebaseAuth.getCurrentUser() != null){
            //TODO go to main activity
        }

        emailInput = findViewById(R.id.editText_emailInput);
        passwordInput = findViewById(R.id.editText_passwordInput);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }


    public void registerAccount(View view) {
        //TODO crear metodo para recibir el resultado del activity
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivityForResult(registerIntent, CREAR_CUENTA);
    }

    public void loginOnClick(View view){
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        if(email.equals("") || password.equals("")){
            Toast.makeText(this, R.string.str_loginIncomplete,Toast.LENGTH_LONG).show();
            return;
        }
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                gotoMainActivity();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, R.string.str_loginFailed, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void signInWithGoogle(View vIew){
        Intent gSignInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(gSignInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult();
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                firebaseAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        gotoMainActivity();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, R.string.str_loginFailed, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                Log.w(TAG, "Sign in with credential: Fail", e);
                Toast.makeText(this, R.string.str_loginFailed, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void gotoMainActivity(){
        Intent intent =  new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
