package com.example.infinitescroling;

import androidx.annotation.NonNull;

import com.example.infinitescroling.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ISFirebaseManager {
    private static final ISFirebaseManager ourInstance = new ISFirebaseManager();
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;

    public static ISFirebaseManager getInstance() {
        return ourInstance;
    }

    private ISFirebaseManager() {
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public /*User*/ void getUserWithId(String postedBy, OnSuccessListener<DocumentSnapshot> listener){
        DocumentReference ref = db.collection("users").document(postedBy);
        ref.get().addOnSuccessListener(listener
//                new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                    user[0] = task.getResult().toObject(User.class);
//                }
//            }
//        }
        );
//        return user[0];
    }
}
