
package com.example.infinitescroling.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.infinitescroling.CreatePostActivity;
import com.example.infinitescroling.EditProfileActivity;
import com.example.infinitescroling.LoginActivity;
import com.example.infinitescroling.MainActivity;
import com.example.infinitescroling.R;
import com.example.infinitescroling.InfScrollUtil;
import com.example.infinitescroling.adapters.FeedAdapter;
import com.example.infinitescroling.models.Posts;
import com.example.infinitescroling.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

    private int MODIFY_ACCOUNT = 1;
    private int ACCOUNT_DELETED = 6;
    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE = 300;

    private SimpleDateFormat simpleDateFormat;
    private User loggedUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private View layout;
    private ArrayList<CharSequence> infos;
    private ArrayAdapter<CharSequence> infoAdapter;
    private ListView infoListView;
    private ImageView profile;

    private FeedAdapter adapterList;
    private RecyclerView recyclerViewProfile;
    private ArrayList<Posts> listProfile;
    private Uri path;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        view.findViewById(R.id.button_editProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfileOnClick(v);
            }
        });
        layout = view;
        infos = new ArrayList<>();
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        infoAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, infos );
        infoListView = view.findViewById(R.id.listView_information_profile);
        infoListView.setAdapter(infoAdapter);

        recyclerViewProfile = view.findViewById(R.id.RecyclerView_posts);
        recyclerViewProfile.setHasFixedSize(true);
        recyclerViewProfile.setLayoutManager(new LinearLayoutManager(getContext()));

        listProfile = new ArrayList<Posts>();
        adapterList = new FeedAdapter(view.getContext(), listProfile, new FeedAdapter.OnItemClickListener() {
            @Override public void onItemClick(Posts item) {

            }
        });
        recyclerViewProfile .setAdapter(adapterList);
        profile = view.findViewById(R.id.imageView_profile);
        Button btn = view.findViewById(R.id.button_updateProfilePicture);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPicture();
            }
        });
        searchPosts();
        loadUser();
        myRequestStoragePermission();
        return view;
    }

    private void loadUser(){
        db.collection("users").document(firebaseAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                loggedUser = documentSnapshot.toObject(User.class);
                loadUserInfo();
                loadImages();
            }
        });
    }

    private void loadUserInfo() {
        ((TextView) layout.findViewById(R.id.textView_name_profile)).setText(
                (loggedUser.getFirstName() + " " + loggedUser.getLastName()));
        if(!loggedUser.getCity().equals(""))
            infos.add("Ciudad: " + loggedUser.getCity());
        if(loggedUser.getBirthDate() != null)
            infos.add("Nació el: " + simpleDateFormat.format(loggedUser.getBirthDate()));
        if(!loggedUser.getGender().equals(""))
            infos.add("Género: " + loggedUser.getGender());
        if(!loggedUser.getPhoneNumber().equals(""))
            infos.add("Número de teléfono: " + loggedUser.getPhoneNumber());
        infoAdapter.notifyDataSetChanged();
        InfScrollUtil.setListViewHeightBasedOnChildren(infoListView);
    }

    private void searchPosts(){
        listProfile.clear();
        Query documentPosts = db.collection("posts").whereEqualTo("postedBy",firebaseAuth.getUid());
        documentPosts.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot taskPost : queryDocumentSnapshots) {
                    Posts post = taskPost.toObject(Posts.class);
                    listProfile.add(post);
                }
                Collections.sort(listProfile, new Comparator<Posts>() {
                    public int compare(Posts o1, Posts o2) {
                        return o2.getDatePublication().compareTo(o1.getDatePublication());
                    }
                });
                adapterList.notifyDataSetChanged();
            }
        });
    }

    private void loadImages() {
        if(loggedUser.getProfilePicture() != null && !loggedUser.getProfilePicture().equals("")){
            try {
                ImageView iv = layout.findViewById(R.id.imageView_profile);
                Glide.with(getContext())
                        .load(loggedUser.getProfilePicture())
                        .centerCrop().fitCenter().into(iv);
                //TODO fill the rest of images
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Permisos denegados");
        builder.setMessage("Para usar las funciones de la app necesitas aceptar los permisos");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public void addPicture(){
        final CharSequence[] options = {"Tomar foto", "Elegir de galeria", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

        if((getContext().checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (getContext().checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED))
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
                Toast.makeText(getContext(), "Permisos aceptados", Toast.LENGTH_SHORT).show();
        }
        else
            showExplanation();
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void editProfileOnClick(View view){
        Intent intent = new Intent(getContext(), EditProfileActivity.class);
        startActivityForResult(intent, MODIFY_ACCOUNT);
    }

    private void uploadUser(){
        db.collection("users").document(firebaseAuth.getUid()).set(loggedUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MODIFY_ACCOUNT){
            if(resultCode == ACCOUNT_DELETED){
                Intent loginIntent = new Intent(this.getContext(), LoginActivity.class);
                startActivity(loginIntent);
                getActivity().finish();
            }
        }
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case PHOTO_CODE:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                    // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                    path = getImageUri(getContext().getApplicationContext(), bitmap);
                    break;
                case SELECT_PICTURE:
                    path = data.getData();
                    break;
            }
            if(path != null){
                Glide
                        .with(getContext())
                        .load(path)
                        .into(profile);
                StorageReference file = storageReference.child(firebaseAuth.getUid()).child(path.getLastPathSegment());
                file.putFile(path).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uri.isComplete());
                        path = uri.getResult();
                        loggedUser.setProfilePicture(path.toString());
                        uploadUser();
                    }
                });

            }
        }
        searchPosts();
    }


}
