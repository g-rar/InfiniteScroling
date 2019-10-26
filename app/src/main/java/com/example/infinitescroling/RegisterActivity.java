package com.example.infinitescroling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.infinitescroling.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "Login Activity: ";
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference usersDoc = FirebaseFirestore.getInstance().collection("users");
    private Button googleRegisterBtn;
    private EditText nameInput;
    private EditText lastNameInput;
    private Spinner genderSpinner;
    private EditText birthDateInput;
    private EditText cityInput;
    private EditText phoneNumberInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText repeatPasswordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameInput = findViewById(R.id.editText_nameInput);
        lastNameInput = findViewById(R.id.editText_lastNamesInput);
        genderSpinner = findViewById(R.id.spinner_genderSelection);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.strArr_genders, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);
        birthDateInput = findViewById(R.id.editText_birthDateInput);
        cityInput = findViewById(R.id.editText_cityInput);
        phoneNumberInput = findViewById(R.id.editText_phoneNumberInput);
        emailInput = findViewById(R.id.editText_emailInput);
        passwordInput = findViewById(R.id.editText_passwordInput);
        repeatPasswordInput = findViewById(R.id.editText_repeatPasswordInput);
    }

    public void registerBtnOnClick(View view){
        final String name = nameInput.getText().toString();
        final String lastName = lastNameInput.getText().toString();
        final String gender = ((TextView) genderSpinner.getSelectedView()).getText().toString();
        final String birthDateStr = birthDateInput.getText().toString();
        final String city = cityInput.getText().toString();
        final String phoneNumberStr = phoneNumberInput.getText().toString();
        final String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String repeatPassword  = repeatPasswordInput.getText().toString();

        if(name.equals("") | lastName.equals("") | gender.equals("") | birthDateStr.equals("")
                | city.equals("") | phoneNumberStr.equals("") | email.equals("")
                | password.equals("") | repeatPassword.equals("")){
            Toast.makeText(this, R.string.str_registerIncomplete, Toast.LENGTH_LONG).show();
            return;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date birthDate = null;
        try {
            birthDate = simpleDateFormat.parse(birthDateStr);
        } catch (ParseException e) {
            Log.d(TAG , "Ha ocurrido un error con el parseo de la fecha: \n" + e.getStackTrace().toString());
            Toast.makeText(this, R.string.str_unvalidDate, Toast.LENGTH_LONG).show();
            return;
        }

        if(!password.equals(repeatPassword)){
            Toast.makeText(this, R.string.str_passwordConfirmFailed, Toast.LENGTH_LONG).show();
            return;
        }

        final Date finalBirthDate = birthDate;
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                firestore.collection("users").document(authResult.getUser().getUid()).set(new User(
                        name, lastName, city, gender, email, finalBirthDate, phoneNumberStr
                )).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //TODO goto  login activity
                        Toast.makeText(RegisterActivity.this, "Registro completo", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        firebaseAuth.getCurrentUser().delete();
                        Log.d(TAG, "onFailure: " + e.getStackTrace().toString());
                        Toast.makeText(RegisterActivity.this, "No se pudo meter al usuario a la bd", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, R.string.str_registrationFailed, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
