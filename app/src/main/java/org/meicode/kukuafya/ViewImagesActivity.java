package org.meicode.kukuafya;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
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
            imageView.setImageBitmap(resizeBitmap(image, 70, 70));
            imageView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            imageView.setPadding(4, 4, 4, 4);
            imageView.setOnClickListener(v -> {
                // Convert Bitmap to Uri and send it back to DetectFragment
                Uri imageUri = getImageUri(image);
                Intent resultIntent = new Intent();
                resultIntent.setData(imageUri);
                setResult(RESULT_OK, resultIntent);
                finish();
            });
            linearLayout.addView(imageView);
        }
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int width, int height) {
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

    private Uri getImageUri(Bitmap bitmap) {
        // Convert Bitmap to Uri and return it
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);  // Corrected from Url.parse to Uri.parse
    }

    private void showImageInDialog(Bitmap bitmap) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_image);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setGravity(Gravity.CENTER);

        ImageView imageView = dialog.findViewById(R.id.dialogImageView);
        imageView.setImageBitmap(bitmap);

        dialog.show();
    }
}
