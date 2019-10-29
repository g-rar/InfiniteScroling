package com.example.infinitescroling;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.infinitescroling.models.Posts;
import com.example.infinitescroling.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class CreatePostActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private StorageReference storageReference;
    private DocumentReference userDoc;
    private FirebaseAuth firebaseAuth;
    private ImageView img_post;
    private VideoView video_post;
    private ImageButton btn_deleteImg;
    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE = 300;
    private Uri path;
    private User user;
    private Posts newPost;
    private Uri pathVideo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        img_post = findViewById(R.id.imgPhoto);
        video_post = findViewById(R.id.videoView_videoPost);
        btn_deleteImg = findViewById(R.id.btnDeletePhoto);
        video_post.setMediaController(new MediaController(this));

        userDoc = db.collection("users").document(firebaseAuth.getUid());

        myRequestStoragePermission();
        path = null;
        pathVideo = null;

        userDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
                newPost = new Posts(user.getFirstName(),user.getLastName(),firebaseAuth.getUid(),user.getProfilePicture(),user.getFriendIds());
                for(String friend : user.getFriendIds()){
                    newPost.addFriend(friend);
                }
                newPost.addFriend(newPost.getPostedBy());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreatePostActivity.this, R.string.str_somethingWentWrong, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    public void createPost(View view){
        EditText txt = findViewById(R.id.txtPost);
        String text = txt.getText().toString();
        newPost.setDescription(text);
        newPost.setDatePublication(new Date());
        if(path != null){
            StorageReference file = storageReference.child(firebaseAuth.getUid()).child(path.getLastPathSegment());
            file.putFile(path).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                    while(!uri.isComplete());
                    path = uri.getResult();
                    newPost.setImage(path.toString());
                    uploadPost(newPost);
                }
            });
        }
        else{
            uploadPost(newPost);
        }
    }

    public void removeImg(View view){
        btn_deleteImg.setVisibility(View.GONE);
        img_post.setVisibility(View.GONE);
        path = null;
    }

    private void uploadPost(Posts newPost){
        DocumentReference ref = db.collection("posts").document();
        newPost.setId(ref.getId());
        ref.set(newPost).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
        finish();
    }

    private void showExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreatePostActivity.this);
        builder.setTitle("Permisos denegados");
        builder.setMessage("Para usar las funciones de la app necesitas aceptar los permisos");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
    }

    public void addPicture(View view){
        final CharSequence[] options = {"Tomar foto", "Elegir de galeria", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(CreatePostActivity.this);
        builder.setTitle("Elige una opción");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(options[i].equals("Tomar foto"))
                    openCamera();
                else if(options[i].equals("Elegir de galeria")){
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent,"Selecciona la app:" ), SELECT_PICTURE);
                }
                else if (options[i].equals("Cancelar"))
                    dialogInterface.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void openCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, PHOTO_CODE);
    }

    private boolean myRequestStoragePermission(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        if((checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED))
            return true;

        if((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) || (shouldShowRequestPermissionRationale(CAMERA)))
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
        else
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == MY_PERMISSIONS){
            if(grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(CreatePostActivity.this, "Permisos aceptados", Toast.LENGTH_SHORT).show();
        }
        else
            showExplanation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case PHOTO_CODE:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                    // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                    path = getImageUri(getApplicationContext(), bitmap);
                    break;
                case SELECT_PICTURE:
                    path = data.getData();
                    break;
            }
            img_post.setImageURI(path);
            img_post.setVisibility(View.VISIBLE);
            btn_deleteImg.setVisibility(View.VISIBLE);
        }
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
