package com.example.infinitescroling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.infinitescroling.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "Login activity: ";
    private int CREAR_CUENTA = 1;
    private int RC_SIGN_IN = 7;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private CollectionReference dbUsers = FirebaseFirestore.getInstance().collection("users");
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;

    private EditText emailInput;
    private EditText passwordInput;
    private ConstraintLayout loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(firebaseAuth.getCurrentUser() != null){
            gotoMainActivity();
        }

        emailInput = findViewById(R.id.editText_emailInput);
        passwordInput = findViewById(R.id.editText_passwordInput);
        loadingLayout = findViewById(R.id.ConstraintLayout_loading);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut();
    }


    public void registerAccount(View view) {
        //TODO crear metodo para recibir el resultado del activity
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivityForResult(registerIntent, CREAR_CUENTA);
    }

    public void loginOnClick(View view){
        loadingLayout.setVisibility(View.VISIBLE);

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
                loadingLayout.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, R.string.str_loginFailed, Toast.LENGTH_LONG).show();
                loadingLayout.setVisibility(View.GONE);
            }
        });
    }

    public void signInWithGoogle(View vIew){
        loadingLayout.setVisibility(View.VISIBLE);
        Intent gSignInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(gSignInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                final GoogleSignInAccount account = task.getResult();
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                firebaseAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        registerManageGoogleSignIn(account);
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
            loadingLayout.setVisibility(View.GONE);
        }
    }

    private void registerManageGoogleSignIn(final GoogleSignInAccount account) {
        final String uid = firebaseAuth.getCurrentUser().getUid();

        DocumentReference userRef = dbUsers.document(uid);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        gotoMainActivity();
                    } else {
                        dbUsers.document(uid).set(new User(
                                account.getGivenName(), account.getFamilyName(), "", "", account.getEmail(), null, "", account.getPhotoUrl().toString()
                        )).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LoginActivity.this, R.string.str_registrationSuccess, Toast.LENGTH_SHORT).show();
                                gotoMainActivity();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                firebaseAuth.getCurrentUser().delete();
                                Toast.makeText(LoginActivity.this,R.string.str_registrationFailed, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }

    private void gotoMainActivity(){
        Intent intent =  new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
