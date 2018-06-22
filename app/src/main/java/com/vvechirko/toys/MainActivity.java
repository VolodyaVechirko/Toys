package com.vvechirko.toys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

//    private void showAddImageDialog() {
//        new BottomActionDialog(this)
//                .titleText(R.string.actions)
//                .action1(R.drawable.ic_upload_black_24dp, R.string.upload_image, v -> {
//                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                    intent.setType("image/*");
//                    startActivityForResult(Intent.createChooser(intent, null), 1);
//                })
//                .action2(R.drawable.ic_camera_black_24dp, R.string.use_camera, v -> {
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    if (intent.resolveActivity(getPackageManager()) != null) {
//                        try {
//                            userPhoto = ResourcesUtils.createImageFile();
//                            intent.putExtra(MediaStore.EXTRA_OUTPUT, userPhoto);
//                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                            startActivityForResult(intent, 1);
//                        } catch (IOException ex) {
//                            showError(ex.getMessage());
//                        }
//                    }
//                })
//                .show();
//    }
}
