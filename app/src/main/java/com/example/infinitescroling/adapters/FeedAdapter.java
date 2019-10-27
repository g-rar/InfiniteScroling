package com.example.infinitescroling.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infinitescroling.R;
import com.example.infinitescroling.models.Posts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.PostsHolder> {

    private Context context;
    private ArrayList<Posts> posts;

    public FeedAdapter(Context context, ArrayList<Posts> posts) {
        this.context = context;
        this.posts = posts;
    }


    @NonNull
    @Override
    public FeedAdapter.PostsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.post_row, null);
        return new PostsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedAdapter.PostsHolder holder, int position) {
        Posts post = posts.get(position);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        holder.textView_nameUser.setText(post.getFirstNameUser()+" "+ post.getLastNameUser());
        holder.textView_datePost.setText(formatter.format(post.getDatePublication()));
        holder.textView_description.setText(post.getDescription());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class PostsHolder extends RecyclerView.ViewHolder{

        private TextView textView_nameUser;
        private TextView textView_datePost;
        private TextView textView_description;

        public PostsHolder(View view) {
            super(view);

            textView_nameUser = view.findViewById(R.id.textView_nameUserRow);
            textView_datePost = view.findViewById(R.id.textView_datePost);
            textView_description = view.findViewById(R.id.textView_descriptionPost);
        }
    }
}
