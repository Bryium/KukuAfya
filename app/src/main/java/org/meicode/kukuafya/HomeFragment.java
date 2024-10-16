package org.meicode.kukuafya;

import static android.app.PendingIntent.getActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        WebView webView = view.findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        NestedScrollView nestedScrollView = view.findViewById(R.id.nestedScrollView);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    // Scroll down, hide toolbar, bottomAppBar, and FAB
                    ((MainActivity) getActivity()).hideToolbar();
                    ((MainActivity) getActivity()).hideBottomAppBar();
                    ((MainActivity) getActivity()).hideFloatingActionButton();

                } else if (scrollY < oldScrollY) {
                    // Scroll up, show toolbar, bottom navigation, and FAB
                    ((MainActivity) getActivity()).showToolbar();
                    ((MainActivity) getActivity()).showBottomAppBar();
                    ((MainActivity) getActivity()).showFloatingActionButton();

                }
            }
        });

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://www.bentoli.com/chicken-problems-common/");

        return view;
    }
}
