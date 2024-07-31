package org.meicode.kukuafya;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import org.meicode.kukuafya.ml.ChickenDisease9852Quantized;
import org.meicode.kukuafya.ml.ChickenDisease9852;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DetectFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_SELECT_IMAGE = 10;
    private static final String TAG = "DetectFragment";

    private Button selectBtn, predictBtn, captureBtn;
    private TextView results;
    private ImageView imageView;
    private Uri photoUri;
    private String currentPhotoPath;
    private Bitmap bitmap;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detect, container, false);

        // Initialize views using the inflated view
        selectBtn = view.findViewById(R.id.selectBtn);
        predictBtn = view.findViewById(R.id.predictBtn);
        captureBtn = view.findViewById(R.id.captureBtn);
        results = view.findViewById(R.id.result);
        imageView = view.findViewById(R.id.imageView);

        // Set up button click listeners
        captureBtn.setOnClickListener(v -> dispatchTakePictureIntent());
        selectBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ViewImagesActivity.class);
            startActivityForResult(intent, REQUEST_SELECT_IMAGE);
        });
        predictBtn.setOnClickListener(v -> predictDisease());

        return view;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, "Error creating image file", ex);
            }
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(requireContext(),
                        "org.meicode.kukuafya.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(null);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        Log.d(TAG, "Image file created: " + currentPhotoPath);
        return image;
    }

    private void predictDisease() {
        if (bitmap == null) {
            Toast.makeText(getActivity(), "No image to predict", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "No image to predict");
            return;
        }

        // Resize the bitmap to 224x224
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
        Log.d(TAG, "Bitmap resized to 224x224");

        // Convert Bitmap to float array
        float[] floatArray = new float[224 * 224 * 3];
        int[] intArray = new int[224 * 224];
        resizedBitmap.getPixels(intArray, 0, 224, 0, 0, 224, 224);

        for (int i = 0; i < intArray.length; i++) {
            floatArray[i * 3] = ((intArray[i] >> 16) & 0xFF) / 255.0f; // Red
            floatArray[i * 3 + 1] = ((intArray[i] >> 8) & 0xFF) / 255.0f; // Green
            floatArray[i * 3 + 2] = (intArray[i] & 0xFF) / 255.0f; // Blue
        }
        Log.d(TAG, "Bitmap converted to float array");

        if (!isValidImage(bitmap)) {
            Toast.makeText(getActivity(), "Provide a valid image", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Invalid image dimensions: " + bitmap.getWidth() + "x" + bitmap.getHeight());
            return;
        }


        try {
            // Load the TFLite model
            ChickenDisease9852 model = ChickenDisease9852.newInstance(requireContext());

            // Create TensorBuffer with the correct shape
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            inputFeature0.loadArray(floatArray);

            // Run inference
            ChickenDisease9852.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
            float[] outputArray = outputFeature0.getFloatArray();

            // Log the entire output array
            StringBuilder outputBuilder = new StringBuilder("Model output: ");
            for (float value : outputArray) {
                outputBuilder.append(value).append(" ");
            }
            Log.d(TAG, outputBuilder.toString());

            // Find the index of the maximum value in the output
            int resultIndex = getMax(outputArray);
            Log.d(TAG, "Prediction index: " + resultIndex);

            // Load class labels from assets
            List<String> labels = loadClassLabelsFromAssets();

            // Ensure labels list is not null and has enough elements
            if (labels != null && resultIndex < labels.size()) {
                // Map the result index to the disease label
                String predictedLabel = labels.get(resultIndex);
                // Display the prediction result
                results.setText("Prediction: " + predictedLabel);
                Log.d(TAG, "Prediction result: " + predictedLabel);
            } else {
                results.setText("Prediction: Unknown");
                Log.w(TAG, "Labels list is null or index out of bounds");
            }

            // Close the model
            model.close();
        } catch (IOException e) {
            Log.e(TAG, "Error loading model", e);
            Toast.makeText(getActivity(), "Error loading model", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidImage(Bitmap bitmap) {
        // Check if the image dimensions are correct
        return bitmap.getWidth() == 224 && bitmap.getHeight() == 224;
    }

    private int getMax(float[] arr) {
        int maxIndex = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > arr[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    private List<String> loadClassLabelsFromAssets() {
        List<String> labels = new ArrayList<>();
        try {
            InputStream inputStream = requireContext().getAssets().open("Chicken_Disease-class_dict.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                // Assuming the CSV format is simple with labels on each line
                labels.add(line.trim());
            }
            reader.close();
            Log.d(TAG, "Class labels loaded successfully");
        } catch (IOException e) {
            Log.e(TAG, "Error loading labels", e);
            Toast.makeText(getActivity(), "Error loading labels", Toast.LENGTH_SHORT).show();
        }
        return labels;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                    Log.d(TAG, "Image captured and displayed");
                } else {
                    Toast.makeText(getActivity(), "Failed to load image", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to decode image file");
                }
            } else if (requestCode == REQUEST_SELECT_IMAGE && data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                        imageView.setImageBitmap(bitmap);
                        Log.d(TAG, "Image selected and displayed");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Failed to load image", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error loading selected image", e);
                    }
                }
            }
        }
    }
}
