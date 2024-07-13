package org.meicode.kukuafya;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
        String descriptionText = "Embark on an innovative journey with KukuAfya, a cutting-edge "
                + "Android application designed to revolutionize chicken health management. "
                + "Utilizing the agile methodology, our development process ensures flexibility and adaptability, allowing "
                + "for swift iterations and seamless updates. Powered by the robust Java language, KukuAfya "
                + "delivers a seamless user experience with its intuitive interface and efficient performance. "
                + "Integrating Machine Learning components developed in Python, the app boasts "
                + "advanced diagnostic capabilities, enabling accurate and timely disease detection in "
                + "livestock. Agile methodology was chosen for its inherent advantages, including "
                + "rapid development cycles, continuous stakeholder feedback, and enhanced collaboration "
                + "among team members. Join us in harnessing the power of technology to transform "
                + "animal healthcare and promote the well-being of livestock worldwide with KukuAfya.";

        appDescription.setText(descriptionText);


        return view;
    }
}
