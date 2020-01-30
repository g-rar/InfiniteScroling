package com.example.infinitescroling;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.infinitescroling.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class ISFirebaseManager {
    private static final ISFirebaseManager ourInstance = new ISFirebaseManager();
    private static final String TAG = "ISFirebaseManager";
    private String device_token = "device_token";
    private FirebaseFirestore db;
    private SQLiteDatabase sqLiteDatabase;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private User loggedUser;

    public static ISFirebaseManager getInstance() {
        return ourInstance;
    }

    private ISFirebaseManager() {
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void getUserWithId(String postedBy, OnSuccessListener<DocumentSnapshot> listener){
        DocumentReference ref = db.collection("users").document(postedBy);
        ref.get().addOnSuccessListener(listener);
    }

    public void setLoggedUser() {
        db.collection("users").document(firebaseAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
            loggedUser = documentSnapshot.toObject(User.class);
            updateDeviceTokenInDB(getDeviceToken());
            }
        });
    }

    public boolean isLoggedUser(String userId){
        return userId.equals(firebaseAuth.getUid());
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public String getLoggedUserId() {
        return firebaseAuth.getUid();
    }

    public void updateDeviceTokenInDB(final String s) {
        Log.d(TAG, "updateDeviceTokenInDB: updating token to: " + s);
        String previusToken = getDeviceToken();
        updateDeviceToken(s);
        if(loggedUser == null){
            Log.d(TAG, "updateDeviceTokenInDB: No user logged... do nothing in server");
            return;
        }
        ArrayList<String> userDevices = loggedUser.getDevices();
        userDevices.remove(previusToken);
        userDevices.add(s);
        loggedUser.setDevices(userDevices);
        db.document("users/" + firebaseAuth.getUid()).set(loggedUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: logged user token updated");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: couldn't update logged user's token", e);
                    }
        });
    }

    public void removeDeviceTokenInDB() {
        ArrayList<String> userDevices = loggedUser.getDevices();
        userDevices.remove(getDeviceToken());
        loggedUser.setDevices(userDevices);
        db.document("users/" + firebaseAuth.getUid()).set(loggedUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: removed token from user");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: couldn't remove logged user's token", e);
            }
        });
    }

    private void updateDeviceToken(String s) {
        if(getDeviceToken().equals(s)) return;
        sqLiteDatabase.execSQL("Delete from deviceInfo where info = '" + device_token + "'");
        sqLiteDatabase.execSQL("Insert into deviceInfo values('"+ device_token +"', '"+s+"')");
        Log.d(TAG, "updateDeviceToken: token updated locally");
    }

    private String getDeviceToken(){
        String token = "";
        Cursor cursor = sqLiteDatabase.rawQuery("Select * from deviceInfo where info = 'device_token'", null);
        int deviceTokenIndex = cursor.getColumnIndex("value");
        cursor.moveToFirst();
        if(!cursor.isAfterLast()) {
            token = cursor.getString(deviceTokenIndex);
        }
        return token;
    }

    public void setSQLiteDB(SQLiteDatabase sqLiteDB) {
        //the method to create sqlite db is inherited from Activity
        if(sqLiteDatabase == null){
            Log.d(TAG, "setSQLiteDB: setting local db");
            sqLiteDatabase = sqLiteDB;
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS deviceInfo(" +
                    "info VARCHAR(5) primary key, value TEXT)");
        } else {
            Log.d(TAG, "setSQLiteDB: local db already set");
        }
    }
}
