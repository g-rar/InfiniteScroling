package com.example.infinitescroling;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.infinitescroling.adapters.EditAcademicAdapter;
import com.example.infinitescroling.adapters.FeedAdapter;
import com.example.infinitescroling.models.AcademicInfo;
import com.example.infinitescroling.models.Post;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AnotherProfileActivity extends AppCompatActivity implements InfScrollUtil.ContentPaginable, EditAcademicAdapter.AcademicEditable {

    private static final int NOT_FRIEND = 11;
    private static final int FRIEND = 328;
    private static final int REQ_SENDER = 214;
    private static final int REQUESTED = 110;

    private User profileUser;
    private User loggedUser;
    private int profileIs;
    private String profileUserId;
    private String loggedUserId;
    private ArrayList<CharSequence> infos;
    private ArrayAdapter<CharSequence> infoAdapter;
    private FirebaseFirestore db;
    private ISFirebaseManager firebaseManager;
    private SimpleDateFormat simpleDateFormat;
    public static final String TAG = "Profile Fragment: ";
    public static final String ACADEMICS_KEY = "academicInfo";

    private DocumentSnapshot lastDocLoaded;
    private boolean loading = false;
    private boolean finished = false;
    private ArrayList<Post> fetchedPosts;
    private FeedAdapter feedAdapter;
    private Query query;

    private ImageView profilePicture;
    private TextView profileName;
    private ListView infoListView;
    private Button addFriendBtn;
    private Button seePicturesBtn;
    private Button seeFriendsBtn;
    private RecyclerView recyclerViewPosts;
    private LinearLayout gallery;
    private ArrayList<String> listIdPost;

    private ListView academicListView;
    private ArrayList<AcademicInfo> academics;
    private ArrayList<String> academicIds;
    private CollectionReference academicsReference;
    private EditAcademicAdapter adapter;
    private boolean friend;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another_profile);

        profilePicture = findViewById(R.id.imageView_profile);
        profileName = findViewById(R.id.textView_name_profile);
        infoListView = findViewById(R.id.listView_information_profile);
        addFriendBtn = findViewById(R.id.button_addFriend);
        seePicturesBtn = findViewById(R.id.button_anotherSeePhotos);
        seeFriendsBtn = findViewById(R.id.button_anotherSeeFriends);
        recyclerViewPosts = findViewById(R.id.recyclerView_posts);

        academicListView = findViewById(R.id.ListView_academics);
        academics = new ArrayList<>();
        academicIds = new ArrayList<>();
        adapter = new EditAcademicAdapter(this, this, academics, false);
        academicListView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        firebaseManager = ISFirebaseManager.getInstance();
        infos = new ArrayList<>();
        infoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, infos );
        infoListView.setAdapter(infoAdapter);
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        profileUserId = getIntent().getStringExtra("userId");
        loggedUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        loggedUser = firebaseManager.getLoggedUser();
        db.collection("users").document(profileUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                profileUser = documentSnapshot.toObject(User.class);
                academicsReference = documentSnapshot.getReference().collection(ACADEMICS_KEY);
                refreshAcademics();
                fillProfile();
            }
        });

        infoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, infos );
        infoListView = findViewById(R.id.listView_information_profile);
        infoListView.setAdapter(infoAdapter);

        recyclerViewPosts.setHasFixedSize(true);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));

        fetchedPosts = new ArrayList<Post>();
        feedAdapter = new FeedAdapter(this, fetchedPosts, new FeedAdapter.OnItemClickListener() {
            @Override public void onItemClick(Post item) {
                Intent intent = new Intent(AnotherProfileActivity.this, PostDetailsActivity.class);
                intent.putExtra("idPost",item.getId());
                startActivity(intent);
            }
        });
        recyclerViewPosts.setAdapter(feedAdapter);
        listIdPost = new ArrayList<String>();
        query = db.collection("posts").whereEqualTo("postedBy", profileUserId)
            .orderBy("datePublication", Query.Direction.DESCENDING);
        InfScrollUtil.setInfiniteScrolling(recyclerViewPosts, this);
        InfScrollUtil.loadNextPage(this);
        createGallery();
    }

    private void fillProfile() {
        profileName.setText((profileUser.getFirstName() + " " + profileUser.getLastName()));
        if(profileUser.getProfilePicture() != null && !profileUser.getProfilePicture().equals(""))
            Glide.with(this).load(profileUser.getProfilePicture()).fitCenter()
            .into(profilePicture);
        profileIs = NOT_FRIEND;
        if(profileUser.getFriendIds().contains(loggedUserId)) {
            addFriendBtn.setText(R.string.str_unFriend);
            addFriendBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(android.R.drawable.ic_delete, 0,0,0);
            profileIs = FRIEND;
        } else if (profileUser.getFriendRequests().contains(loggedUserId)){
            addFriendBtn.setText(R.string.str_cancelFriendRequest);
            addFriendBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(android.R.drawable.ic_delete, 0,0,0);
            profileIs = REQUESTED;
        } else if (profileUser.getRequestsSent().contains(loggedUserId)){
            addFriendBtn.setText(R.string.str_respondFriendRequest);
            addFriendBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(android.R.drawable.ic_menu_send,0,0,0);
            profileIs = REQ_SENDER;
            friend = true;
        }
        else{
            friend = false;
        }
        if(!profileUser.getCity().equals(""))
            infos.add("Ciudad: " + profileUser.getCity());
        if(profileUser.getBirthDate() != null)
            infos.add("Nació el: " + simpleDateFormat.format(profileUser.getBirthDate()));
        if(!profileUser.getGender().equals(""))
            infos.add("Género: " + profileUser.getGender());
        if(!profileUser.getPhoneNumber().equals(""))
            infos.add("Número de teléfono: " + profileUser.getPhoneNumber());
        infoAdapter.notifyDataSetChanged();
        InfScrollUtil.setListViewHeightBasedOnChildren(infoListView);
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
                InfScrollUtil.setListViewHeightBasedOnChildren(academicListView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), R.string.str_fetchAcademicFail, Toast.LENGTH_SHORT).show();
                Log.w(TAG, "refreshAcademics/onFailure: ", e);
            }
        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

            }
        });
    }

    private void createGallery(){
        gallery = findViewById(R.id.gallery);
        final LayoutInflater inflater = LayoutInflater.from(this);
        db.collection("posts").whereEqualTo("postedBy", profileUserId)
                .whereGreaterThan("image", "").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int posTag = 0;
                List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot doc : docs) {
                    Post post = doc.toObject(Post.class);
                    View view = inflater.inflate(R.layout.image_item, gallery, false);
                    ImageView imageView = view.findViewById(R.id.imageView_carousel);
                    Uri pathImage = Uri.parse(post.getImage());
                    Glide
                            .with(view)
                            .load(pathImage)
                            .into(imageView);
                    imageView.setTag(posTag);
                    listIdPost.add(doc.getId());
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int pos = (int) v.getTag();
                            Intent intent = new Intent(AnotherProfileActivity.this, CommentActivity.class);
                            intent.putExtra("posPost", pos);
                            intent.putExtra("idUser", profileUserId);
                            intent.putExtra("listIdsImages", listIdPost);
                            startActivity(intent);
                        }
                    });
                    gallery.addView(view);
                    posTag++;
                }
            }
        });
    }

    public void friendBtnClick(View view) {
        Log.d("Another profile activity: ", "friendBtnClick: ");
        switch (profileIs){
            case FRIEND: {
                deleteFriend();
                break;
            }
            case NOT_FRIEND: {
                sendRequest();
                break;
            }
            case REQ_SENDER: {
                answerRequest();
                break;
            }
//            case REQUESTED: {
//                cancelRequest();
//                break;
//            }
        }
    }

    public void deleteFriend(){
        new AlertDialog.Builder(AnotherProfileActivity.this)
                .setIcon(android.R.drawable.ic_delete)
                .setTitle(R.string.alert_sure)
                .setPositiveButton(R.string.alert_imSureConfirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loggedUser.getFriendIds().remove(profileUserId);
                        profileUser.getFriendIds().remove(loggedUserId);
                        db.collection("users").document(loggedUserId).set(firebaseManager.getLoggedUser());
                        db.collection("users").document(profileUserId).set(profileUser);
                        finish();
                    }
                }).setNegativeButton(R.string.alert_backCancel, null).show();
    }

    private void sendRequest() {
        new AlertDialog.Builder(AnotherProfileActivity.this)
                .setIcon(android.R.drawable.ic_input_add)
                .setTitle(R.string.alert_sendReqTitle)
                .setMessage(R.string.alert_sendReqMessage)
                .setPositiveButton(R.string.alert_letsGoConfirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loggedUser.getRequestsSent().add(profileUserId);
                        profileUser.getFriendRequests().add(loggedUserId);
                        db.collection("users").document(loggedUserId).set(firebaseManager.getLoggedUser());
                        db.collection("users").document(profileUserId).set(profileUser);
                        finish();
                    }
                }).setNegativeButton(R.string.alert_backCancel, null).show();
    }

    private void answerRequest() {
        new AlertDialog.Builder(AnotherProfileActivity.this)
                .setIcon(android.R.drawable.ic_menu_send)
                .setTitle(R.string.alert_answerReqTitle)
                .setMessage(R.string.alert_answerReqMessage)
                .setPositiveButton(R.string.alert_letsGoConfirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loggedUser.getFriendRequests().remove(profileUserId);
                        loggedUser.getFriendIds().add(loggedUserId);
                        profileUser.getRequestsSent().remove(loggedUserId);
                        profileUser.getFriendIds().add(loggedUserId);
                        db.collection("users").document(profileUserId).set(profileUser);
                        db.collection("users").document(loggedUserId).set(loggedUser);
                        finish();
                    }
                }).setNegativeButton(R.string.alert_rejectFriendRequest, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loggedUser.getFriendRequests().remove(profileUserId);
                        profileUser.getRequestsSent().remove(loggedUserId);
                        db.collection("users").document(profileUserId).set(profileUser);
                        db.collection("users").document(loggedUserId).set(loggedUser);
                        finish();
                    }
        }).setNeutralButton(R.string.alert_waitCancel, null).show();
    }

    @Override
    public DocumentSnapshot getLastDocLoaded() {
        return lastDocLoaded;
    }

    @Override
    public void setLastDocLoaded(DocumentSnapshot doc) {
        lastDocLoaded = doc;
    }

    @Override
    public ArrayList<Post> getFetchedPosts() {
        return fetchedPosts;
    }

    @Override
    public FeedAdapter getFeedAdapter() {
        return feedAdapter;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public Query getQuery() {
        return query;
    }

    @Override
    public boolean isLoading() {
        return loading;
    }

    @Override
    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    @Override
    public void editAcademicOnClick(int position) {

    }

    @Override
    public void deleteAcademicOnClick(int position) {

    }
}
