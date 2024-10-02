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
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ViewImagesActivity extends AppCompatActivity {

    private GridLayout gridLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_images);

        gridLayout = findViewById(R.id.gridLayout);
        gridLayout.setColumnCount(3); // Set number of columns

        loadImagesFromDatabase();
    }

    private void loadImagesFromDatabase() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        List<Bitmap> images = databaseHelper.getAllImages();

        for (Bitmap image : images) {
            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(resizeBitmap(image, 200, 200)); // Adjust size as needed
            imageView.setLayoutParams(new GridLayout.LayoutParams());
            imageView.setPadding(4, 4, 4, 4);
            imageView.setOnClickListener(v -> {
                // Convert Bitmap to Uri and send it back to DetectFragment
                Uri imageUri = getImageUri(image);
                if (imageUri != null) {
                    Intent resultIntent = new Intent();
                    resultIntent.setData(imageUri);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(this, "Failed to get image URI", Toast.LENGTH_SHORT).show();
                }
            });

            // Set layout parameters for each image view
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = GridLayout.LayoutParams.WRAP_CONTENT;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.setMargins(4, 4, 4, 4);
            imageView.setLayoutParams(params);

            gridLayout.addView(imageView);
        }
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int width, int height) {
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

    private Uri getImageUri(Bitmap bitmap) {
        OutputStream outputStream = null;
        try {
            // Create a temporary file to save the image
            File imageFile = new File(getExternalFilesDir(null), "Title_" + System.currentTimeMillis() + ".jpg");
            outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();

            // Get the URI from the file
            return Uri.fromFile(imageFile);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showImageInDialog(Bitmap bitmap) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_image);
        dialog.getWindow().setLayout(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setGravity(Gravity.CENTER);

        ImageView imageView = dialog.findViewById(R.id.dialogImageView);
        imageView.setImageBitmap(bitmap);

        dialog.show();
    }
}
