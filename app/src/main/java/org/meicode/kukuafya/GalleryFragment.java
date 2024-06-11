package org.meicode.kukuafya;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class GalleryFragment extends Fragment {

    private static final int REQUEST_MEDIA_IMAGES_PERMISSION = 101;
    private static final int REQUEST_PICK_IMAGE = 1;

    private Button btnOpenGallery;
    private ImageView imageView;

    private ActivityResultLauncher<String> galleryLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        btnOpenGallery = view.findViewById(R.id.btnOpenGallery);
        imageView = view.findViewById(R.id.imageView);

        btnOpenGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        // Initialize the ActivityResultLauncher
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        if (uri != null) {
                            imageView.setImageURI(uri);
                        } else {
                            Toast.makeText(getActivity(), "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        return view;
    }

    private void openGallery() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_MEDIA_IMAGES_PERMISSION);
        } else {
            galleryLauncher.launch("image/*");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_MEDIA_IMAGES_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(getActivity(), "Media permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
