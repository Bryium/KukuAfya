package org.meicode.kukuafya;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

public class AboutFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        // Find the TextView by ID
        TextView appDescription = view.findViewById(R.id.textview);

        // Set the text for the TextView
        String descriptionText = "Embark on an extraordinary journey with KukuAfya, a state-of-the-art Android application meticulously crafted with Java to revolutionize global chicken health management. Engineered for optimal efficiency and user-friendliness, KukuAfya seamlessly integrates cutting-edge Machine Learning components developed in Python to provide precise and timely disease diagnostics for livestock worldwide. With its intuitive interface, robust performance, and scalable architecture, KukuAfya empowers farmers, veterinarians, and agricultural enterprises with invaluable insights, ensuring proactive and sustainable animal healthcare management. Join us in harnessing the transformative power of technology with KukuAfya, setting new standards in livestock health and agriculture innovation on a global scale.";

        appDescription.setText(descriptionText);

        // Find the NestedScrollView by ID and set the scroll change listener
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

        return view;
    }
}
