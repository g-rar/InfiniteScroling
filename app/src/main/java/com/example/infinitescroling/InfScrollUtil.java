package com.example.infinitescroling;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infinitescroling.adapters.FeedAdapter;
import com.example.infinitescroling.models.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InfScrollUtil {

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        Log.e("Listview Size ", "" + listView.getCount());
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    public static String makeDateReadable(Date date){
        return (new PrettyTime(new Locale("es"))).format(date);
    }


    public static String getYTVideoId(String inputURL) {
        String result = "";
        if(inputURL.contains(".be")){
            result = inputURL.substring(inputURL.lastIndexOf("/")+1);
        } else if(inputURL.contains("v=")){
            result = inputURL.substring(inputURL.lastIndexOf("v=")+2);
            if(inputURL.contains("&"))
                result = result.substring(0,result.indexOf("&"));
        }
        return result;
    }

    public static void loadVideoIntoWebView(String videoId, WebView webView){
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        String videoHTML = "<iframe width=\"340\" height=\"191\" src=\"https://www.youtube.com/embed/"+videoId+"\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>";
        webView.loadData(videoHTML, "text/html", "utf-8");
    }

    public static void setInfiniteScrolling(RecyclerView recyclerView, final ContentPaginable contentPaginable){
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(!contentPaginable.isFinished() & !contentPaginable.isLoading()){
                    if(dy > 0){
                        int visibleItems = recyclerView.getChildCount();
                        int pastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                        int total = recyclerView.getLayoutManager().getItemCount();
                        if(visibleItems+pastVisibleItem>=total){
                            loadNextPage(contentPaginable);
                        }
                    }
                    super.onScrolled(recyclerView, dx, dy);
                }
            }
        });
    }

    public static void loadNextPage(final ContentPaginable cP){
        cP.setLoading(true);
        String endMessage = "Se ha acabado el feed";
        Query fillQuery;
        if(!cP.getFetchedPosts().isEmpty()){
            fillQuery = cP.getQuery().startAfter(cP.getLastDocLoaded());
        }else{
            fillQuery = cP.getQuery();
            endMessage = "No hay posts que mostrar";
        }
        final String finalEndMessage = endMessage;
        fillQuery.limit(10).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                if(docs.isEmpty()){
                    Toast.makeText(cP.getContext(), finalEndMessage, Toast.LENGTH_SHORT).show();
                    cP.setLoading(false);
                    cP.setFinished(true);
                    return;
                }
                for (DocumentSnapshot doc : docs) {
                    cP.getFetchedPosts().add(doc.toObject(Post.class));
                }
                cP.setLastDocLoaded(docs.get(docs.size()-1));
                cP.getFeedAdapter().notifyDataSetChanged();
                cP.setLoading(false);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                cP.setLoading(false);
                Toast.makeText(cP.getContext(), "Algo salio mal", Toast.LENGTH_SHORT).show();
                Log.w("LoadNextPage in Feed Fragment: ", "onFailure: Cargando pagina", e);
            }
        });

    }

    public interface ContentPaginable {
        DocumentSnapshot getLastDocLoaded();
        void setLastDocLoaded(DocumentSnapshot doc);
        ArrayList<Post> getFetchedPosts();
        FeedAdapter getFeedAdapter();
        Context getContext();
        Query getQuery();
        boolean isLoading();
        void setLoading(boolean loading);
        boolean isFinished();
        void setFinished(boolean finished);
    }
}
