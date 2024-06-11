package org.meicode.kukuafya;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ViewImagesActivity extends AppCompatActivity {

    private LinearLayout linearLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_images);

        linearLayout = findViewById(R.id.linearLayout);

        loadImagesFromDatabase();
    }

    private void loadImagesFromDatabase() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        List<Bitmap> images = databaseHelper.getAllImages();

        for (Bitmap image : images) {
            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(image);
            linearLayout.addView(imageView);
        }
    }
}
