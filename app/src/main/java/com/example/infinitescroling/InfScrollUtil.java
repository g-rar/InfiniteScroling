package com.example.infinitescroling;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Calendar;
import java.util.Date;
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
}
