package org.meicode.kukuafya;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TreatmentFragment extends Fragment {

    private static final String YOUTUBE_VIDEO_URL = "https://www.youtube.com/watch?v=nV7lk3lht60";

    private WebView webView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_treatment, container, false);

        // Initialize WebView
        webView = view.findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();


        webSettings.setJavaScriptEnabled(true); // Enable JavaScript (required for YouTube embeds)
        webView.setWebViewClient(new WebViewClient()); // Open links in the WebView itself

        // Load YouTube video
        webView.loadUrl(YOUTUBE_VIDEO_URL);

        return view;
    }
}
