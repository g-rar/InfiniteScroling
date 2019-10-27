package com.example.infinitescroling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.infinitescroling.adapters.EditAcademicAdapter;
import com.example.infinitescroling.models.AcademicInfo;
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
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity implements EditAcademicAdapter.AcademicEditable {

    public static final String TAG = "Edit profile Activity: ";
    public static final String ACADEMICS_KEY = "academicInfo";
    private int ACCOUNT_DELETED = 6;
    private int academicEdited = -1;
    private int RC_SIGN_IN = 7;

    private ConstraintLayout loadingLayout;
    private ListView academicListView;
    private ArrayList<AcademicInfo> academics;
    private ArrayList<String> academicIds;
    private CollectionReference academicsReference;
    private EditAcademicAdapter adapter;
    private FirebaseAuth firebaseAuth;
    private Button submitAcademic;
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
    private EditText institutionInput;
    private EditText titleInput;
    private EditText beginDateInput;
    private EditText endDateInput;
    private EditText passwordInput;
    private EditText repeatPasswordInput;
    private EditText passwordForDeleteInput;

    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        academicListView = findViewById(R.id.ListView_academics);
        submitAcademic = findViewById(R.id.button_submitAcademic);
        academics = new ArrayList<>();
        academicIds = new ArrayList<>();
        adapter = new EditAcademicAdapter(this, this, academics);
        academicListView.setAdapter(adapter);
        loadingLayout = findViewById(R.id.ConstraintLayout_loading);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

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
        institutionInput = findViewById(R.id.editText_institution);
        titleInput = findViewById(R.id.editText_title);
        beginDateInput = findViewById(R.id.editText_dateStart);
        endDateInput = findViewById(R.id.editText_dateEnd);
        passwordInput = findViewById(R.id.editText_passwordInput);
        repeatPasswordInput = findViewById(R.id.editText_repeatPasswordInput);
        passwordForDeleteInput = findViewById(R.id.editText_deleteAccountPassword);

        //User info
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userDoc = db.collection("users").document(firebaseAuth.getUid());
        loadingLayout.setVisibility(View.VISIBLE);
        userDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
                fillWithUserInfo(documentSnapshot);
                loadingLayout.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, R.string.str_somethingWentWrong, Toast.LENGTH_SHORT).show();
                loadingLayout.setVisibility(View.GONE);
            }
        });
        //TODO submit editions for user data
    }

    private void fillWithUserInfo(DocumentSnapshot documentSnapshot) {
        nameInput.setText(user.getFirstName());
        lastNameInput.setText(user.getLastName());
        String usrGender = user.getGender();
        genderSpinner.setSelection(usrGender.equals("Masculino") ? 0 : (usrGender.equals("Femenino") ? 1 : 2));
        Date birthDate = user.getBirthDate();
        birthDateInput.setText( birthDate!=null ? simpleDateFormat.format(birthDate) : "");
        cityInput.setText(user.getCity());
        phoneNumberInput.setText(user.getPhoneNumber());
        emailInput.setText(user.getEmail());
        academicsReference = documentSnapshot.getReference().collection(ACADEMICS_KEY);
        refreshAcademics();

    }

    private void refreshAcademics(){
        academicsReference.orderBy("endDate", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                academics.clear();
                List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot doc : docs){
                    AcademicInfo academicInfo = doc.toObject(AcademicInfo.class);
                    academics.add(academicInfo);
                    academicIds.add(doc.getId());
                }
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, R.string.str_fetchAcademicFail, Toast.LENGTH_SHORT).show();
                Log.w(TAG, "refreshAcademics/onFailure: ", e);
            }
        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                loadingLayout.setVisibility(View.GONE);
            }
        });
    }

    public void submitAcademic(View veiw){
        String institution = institutionInput.getText().toString();
        final String title = titleInput.getText().toString();
        String begindDateStr = beginDateInput.getText().toString();
        String endDateStr = endDateInput.getText().toString();
        if(institution.equals("") | title.equals("") |begindDateStr.equals("")
                | endDateStr.equals("")){
            Toast.makeText(this, R.string.str_regAcademicIncomplete, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            //TODO validate bDate < eDate
            Date bDate = simpleDateFormat.parse(begindDateStr);
            Date eDate = simpleDateFormat.parse(endDateStr);
            loadingLayout.setVisibility(View.VISIBLE);
            AcademicInfo newAcademic = new AcademicInfo(title, institution, bDate, eDate);
            //if this is new academic
            if(academicEdited == -1){
                academicsReference.add(newAcademic).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(EditProfileActivity.this, R.string.str_addAcademicSucces, Toast.LENGTH_SHORT).show();
                        resetEditAcademic(null);
                        refreshAcademics();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "onFailure: Add academic: ",e);
                        Toast.makeText(EditProfileActivity.this, R.string.str_addAcademicFail, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                academicsReference.document(academicIds.get(academicEdited)).set(newAcademic)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditProfileActivity.this, R.string.str_editAcademicSuccess, Toast.LENGTH_SHORT).show();
                                resetEditAcademic(null);
                                refreshAcademics();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "onFailure: editAcademic: ", e);
                        Toast.makeText(EditProfileActivity.this, R.string.str_editAcademicFail, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (ParseException e) {
            Toast.makeText(this, R.string.str_unvalidDate, Toast.LENGTH_SHORT).show();
            Log.w(TAG, e);
        }
    }

    public void deleteAccount(View view){
        loadingLayout.setVisibility(View.VISIBLE);
        String passwordConfirm = passwordForDeleteInput.getText().toString();
        Log.d(TAG, "Inicio del for");
        for(UserInfo userInfo : firebaseAuth.getCurrentUser().getProviderData()){
            if(userInfo.getProviderId().equals("password")){
                deletePasswordAccount(passwordConfirm);
            }
            if(userInfo.getProviderId().equals("google.com")){
                deleteGoogleAccount();
            }
        }
    }

    private void deleteGoogleAccount() {
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
                        confirmDeleteAccount();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingLayout.setVisibility(View.GONE);
                        Toast.makeText(EditProfileActivity.this, R.string.str_loginFailed, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                Log.w(TAG, "Sign in with credential: Fail", e);
                Toast.makeText(this, R.string.str_loginFailed, Toast.LENGTH_LONG).show();
                loadingLayout.setVisibility(View.GONE);
            }
        }
    }

    private void deletePasswordAccount(String passwordConfirm) {
        if(passwordConfirm.equals("")){
            Toast.makeText(this, R.string.str_passwordIncomplete, Toast.LENGTH_SHORT).show();
            loadingLayout.setVisibility(View.GONE);
            return;
        }
        firebaseAuth.signInWithEmailAndPassword(firebaseAuth.getCurrentUser().getEmail(), passwordConfirm)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                confirmDeleteAccount();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, R.string.str_loginFailed, Toast.LENGTH_SHORT).show();
                loadingLayout.setVisibility(View.GONE);
            }
        });
    }

    public void confirmDeleteAccount(){
        new AlertDialog.Builder(EditProfileActivity.this)
            .setTitle(R.string.alert_deleteAccount)
            .setMessage(R.string.alert_deleteAccountDes)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(R.string.str_deleteAccountConfirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //TODO also delete posts
                    userDoc.collection(ACADEMICS_KEY).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()){
                                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                    document.getReference().delete();
                                }
                            }
                        }
                    });
                    userDoc.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            firebaseAuth.getCurrentUser().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent intent = getIntent();
                                    setResult(ACCOUNT_DELETED, intent);
                                    loadingLayout.setVisibility(View.GONE);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EditProfileActivity.this, "Algo falló borrando cuenta", Toast.LENGTH_SHORT).show();
                                    Log.w(TAG, "onFailure: Fail deleting account", e);
                                    loadingLayout.setVisibility(View.GONE);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditProfileActivity.this, "Algo falló borrando de la bd", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "onFailure: Failed to delete user from db", e);
                            loadingLayout.setVisibility(View.GONE);
                        }
                    });
                }

            }).setNegativeButton(R.string.str_deleteAccountCancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadingLayout.setVisibility(View.GONE);
            }
        }).show();
    }

    public void resetEditAcademic(View view){
        submitAcademic.setText(R.string.str_addAcademic);
        academicEdited = -1;
        institutionInput.setText("");
        titleInput.setText("");
        beginDateInput.setText("");
        endDateInput.setText("");
    }

    @Override
    public void editAcademicOnClick(int position) {
        submitAcademic.setText(R.string.str_editAcademic);
        academicEdited = position;
        AcademicInfo academicInfo = academics.get(position);
        institutionInput.setText(academicInfo.getInstitution());
        titleInput.setText(academicInfo.getTitle());
        beginDateInput.setText(simpleDateFormat.format(academicInfo.getBeginDate()));
        endDateInput.setText(simpleDateFormat.format(academicInfo.getEndDate()));
    }

    @Override
    public void deleteAcademicOnClick(int position) {
        loadingLayout.setVisibility(View.VISIBLE);
        academicsReference.document(academicIds.get(position)).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(EditProfileActivity.this, R.string.str_deleteAcademicSucces, Toast.LENGTH_SHORT).show();
                refreshAcademics();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, R.string.str_deleteAcademicFail, Toast.LENGTH_SHORT).show();
                Log.w(TAG, "onFailure: academic delete", e);
                loadingLayout.setVisibility(View.GONE);
            }
        });
    }
}
