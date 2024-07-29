package org.meicode.kukuafya;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DetectFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_SELECT_IMAGE = 10;

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
                ex.printStackTrace();
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
        return image;
    }

    private void predictDisease() {
        if (bitmap == null) {
            Toast.makeText(getActivity(), "No image to predict", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Load the TFLite model
            ChickenDisease9852Quantized model = ChickenDisease9852Quantized.newInstance(requireContext());

            // Resize the bitmap to 224x224
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);

            // Convert Bitmap to float array
            float[] floatArray = new float[224 * 224 * 3];
            int[] intArray = new int[224 * 224];
            resizedBitmap.getPixels(intArray, 0, 224, 0, 0, 224, 224);

            for (int i = 0; i < intArray.length; i++) {
                floatArray[i * 3] = ((intArray[i] >> 16) & 0xFF) / 255.0f; // Red
                floatArray[i * 3 + 1] = ((intArray[i] >> 8) & 0xFF) / 255.0f; // Green
                floatArray[i * 3 + 2] = (intArray[i] & 0xFF) / 255.0f; // Blue
            }

            // Create TensorBuffer with the correct shape
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            inputFeature0.loadArray(floatArray);

            // Run inference
            ChickenDisease9852Quantized.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            // Find the index of the maximum value in the output
            int resultIndex = getMax(outputFeature0.getFloatArray());

            // Load class labels
            List<String> labels = loadClassLabels();

            // Ensure labels list is not null and has enough elements
            if (labels != null && resultIndex < labels.size()) {
                // Map the result index to the disease label
                String predictedLabel = labels.get(resultIndex);
                // Display the prediction result
                results.setText("Prediction: " + predictedLabel);
            } else {
                results.setText("Prediction: Unknown");
            }

            // Close the model
            model.close();
        } catch (IOException e) {
            logToFile("Error loading model: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error loading model", Toast.LENGTH_SHORT).show();
        }
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

    private List<String> loadClassLabels() {
        // Ensure this list matches the model's output indices
        return Arrays.asList("Coccidiosis", "Healthy", "Newcastle Disease", "Salmonella");
    }

    private void logToFile(String s) {
        // Implement logging if necessary
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    Toast.makeText(getActivity(), "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == REQUEST_SELECT_IMAGE && data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                        imageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}
