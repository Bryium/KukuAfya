package org.meicode.kukuafya;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showImageInDialog(image);
                }
            });
            linearLayout.addView(imageView);
        }
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int width, int height) {
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
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
