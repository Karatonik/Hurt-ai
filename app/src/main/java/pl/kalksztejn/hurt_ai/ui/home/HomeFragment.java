package pl.kalksztejn.hurt_ai.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.io.FileNotFoundException;
import java.io.InputStream;

import pl.kalksztejn.hurt_ai.databinding.FragmentHomeBinding;
import pl.kalksztejn.hurt_ai.service.AuthenticationService;

public class HomeFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;

    private static final int REQUEST_CAMERA_PERMISSION = 100;

    private HomeViewModel homeViewModel;

    private ImageView imageDrawable;
    private Button uploadButton;
    private FragmentHomeBinding binding;
    private AuthenticationService authenticationService;

    private String email ="";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
         homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        this.imageDrawable = binding.imageDrawable;
        this.uploadButton = binding.uploadButton;
        authenticationService = new AuthenticationService(this.getContext());

        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        homeViewModel.getImageDrawable().observe(getViewLifecycleOwner(), new Observer<Drawable>() {
            @Override
            public void onChanged(Drawable drawable) {
                imageDrawable.setImageDrawable(drawable);
                System.out.println("drawable");
            }
        });
        uploadButton.setOnClickListener(view -> {
            Toast.makeText(getContext(), "Upload button clicked", Toast.LENGTH_SHORT).show();
            if (homeViewModel.getUploadClickListener() != null) {
                email = authenticationService.getEmail();
                homeViewModel.getUploadClickListener().onClick(view);
            }
        });
        imageDrawable.setOnClickListener(view -> {
            openImagePickerDialog();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void openImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Image");
        CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        builder.setItems(options, (dialog, item) -> {
            if (item == 0) {
                takePhoto();
            } else if (item == 1) {
                chooseFromGallery();
            } else if (item == 2) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void takePhoto() {
        // Sprawdzenie uprawnienia do aparatu
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Jeśli brakuje uprawnienia, wysłanie żądania uprawnienia
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            // Jeśli uprawnienie jest dostępne, wykonanie zdjęcia
            dispatchTakePictureIntent();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Uprawnienie do aparatu zostało udzielone, wykonanie zdjęcia
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(requireContext(), "Brak uprawnień do aparatu", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(requireContext(), "Aplikacja aparatu niedostępna", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("IntentReset")
    private void chooseFromGallery() {
        @SuppressLint("IntentReset") Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                if (imageBitmap != null) {
                    Drawable drawable = new BitmapDrawable(getResources(), imageBitmap);
                    if(homeViewModel != null){
                        homeViewModel.setImageDrawable(drawable);
                    }
                }
            } else if (requestCode == REQUEST_IMAGE_GALLERY) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        InputStream inputStream = requireContext().getContentResolver().openInputStream(selectedImageUri);
                        Bitmap imageBitmap = BitmapFactory.decodeStream(inputStream);
                        if (imageBitmap != null) {
                            Drawable drawable = new BitmapDrawable(getResources(), imageBitmap);
                            if(homeViewModel != null) {
                                homeViewModel.setImageDrawable(drawable);
                            }else{
                                Toast.makeText(getContext(), "homeViewModel null", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}