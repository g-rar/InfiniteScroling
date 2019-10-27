package com.example.infinitescroling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.infinitescroling.adapters.EditAcademicAdapter;
import com.example.infinitescroling.models.AcademicInfo;
import com.example.infinitescroling.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EditProfileActivity extends AppCompatActivity {

    private int ACCOUNT_DELETED = 6;

    private ListView academicListView;
    private ArrayList<AcademicInfo> academics;
    private EditAcademicAdapter adapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private DocumentReference userDoc;
    private User user;
    private Spinner editGenderSpinner;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
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
        setContentView(R.layout.activity_edit_profile);

        academicListView = findViewById(R.id.ListView_academics);
        academics = new ArrayList<>();
        //TODO populate with firestorm info instead
        //<<--- delete this
        try {
            academics.add(new AcademicInfo("Bachillerato", "Lic. Alfaro ruiz", simpleDateFormat.parse("15/02/2013"), new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //--->>
        adapter = new EditAcademicAdapter(this, academics);
        academicListView.setAdapter(adapter);

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

        //User info
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userDoc = db.collection("users").document(firebaseAuth.getUid());
        userDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, R.string.str_somethingWentWrong, Toast.LENGTH_SHORT).show();
            }
        });


        //TODO fill form spaces with the actual information of the user
        //TODO listing academic information and editing it
    }

    public void deleteAccount(View view){
        new AlertDialog.Builder(this)
                .setTitle(R.string.alert_deleteAccount)
                .setMessage(R.string.alert_deleteAccountDes)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.str_deleteAccountConfirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userDoc.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                firebaseAuth.getCurrentUser().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent intent = getIntent();
                                        setResult(ACCOUNT_DELETED, intent);
                                        finish();
                                    }
                                });
                            }
                        });
                    }
                }).setNegativeButton(R.string.str_deleteAccountCancel, null).show();
    }

}
