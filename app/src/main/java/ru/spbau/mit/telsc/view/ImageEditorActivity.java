package ru.spbau.mit.telsc.view;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.UUID;

import ly.img.android.PESDK;
import ly.img.android.sdk.models.constant.Directory;
import ly.img.android.sdk.models.state.CameraSettings;
import ly.img.android.sdk.models.state.EditorLoadSettings;
import ly.img.android.sdk.models.state.EditorSaveSettings;
import ly.img.android.sdk.models.state.manager.SettingsList;
import ly.img.android.ui.activities.CameraPreviewBuilder;
import ly.img.android.ui.activities.ImgLyIntent;
import ly.img.android.ui.activities.PhotoEditorBuilder;
import ly.img.android.ui.utilities.PermissionRequest;
import ru.spbau.mit.telsc.R;
import ru.spbau.mit.telsc.model.Sticker;

public class ImageEditorActivity extends AppCompatActivity implements PermissionRequest.Response {

    private static final String FOLDER = "TelSC";
    public static int CAMERA_PREVIEW_RESULT = 1;
    public enum ButtonType {
        SAVE_STICKER_TO_PHONE, UPLOAD_STICKER_TO_TELEGRAM, UPLOAD_STICKER_TO_DB
    }
    public static ButtonType buttonType = ButtonType.SAVE_STICKER_TO_PHONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_editor);

        Intent intent = getIntent();

        SettingsList settingsList = new SettingsList();
        if (intent.hasExtra("pathToImage")) {
            String myPicture = intent.getStringExtra("pathToImage");

            settingsList
                    .getSettingsModel(EditorLoadSettings.class)
                    .setImageSourcePath(myPicture, true); // Load with delete protection true!
        }
        else {
            settingsList
                    .getSettingsModel(CameraSettings.class)
                    .setExportDir(Directory.DCIM, FOLDER)
                    .setExportPrefix("camera_");
        }
        settingsList
                .getSettingsModel(EditorSaveSettings.class)
                .setExportDir(Directory.DCIM, FOLDER)
                .setExportPrefix("result_")
                .setJpegQuality(80, false)
                .setSavePolicy(
                        EditorSaveSettings.SavePolicy.KEEP_SOURCE_AND_CREATE_ALWAYS_OUTPUT
                );
        if (intent.hasExtra("pathToImage")) {
            new PhotoEditorBuilder(this)
                    .setSettingsList(settingsList)
                    .startActivityForResult(this, CAMERA_PREVIEW_RESULT);
        }
        else {
            new CameraPreviewBuilder(this)
                    .setSettingsList(settingsList)
                    .startActivityForResult(this, CAMERA_PREVIEW_RESULT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == CAMERA_PREVIEW_RESULT) {
            String resultPath = data.getStringExtra(ImgLyIntent.RESULT_IMAGE_PATH);
            String sourcePath = data.getStringExtra(ImgLyIntent.SOURCE_IMAGE_PATH);

            switch (buttonType) {
                case UPLOAD_STICKER_TO_TELEGRAM:
                    Intent intent = new Intent(this, PhoneActivity.class);
                    intent.putExtra("stickerName",
                            Sticker.saveStickerInFile(Sticker.getStickerBitmap(resultPath), this));
                    startActivity(intent);
                    break;
                case UPLOAD_STICKER_TO_DB:
                    Toast.makeText(PESDK.getAppContext(), "UPLOAD TO DB clicked", Toast.LENGTH_LONG).show();
                    /*
                     TODO: Vova, paste here your code to start uploading to database.
                     Use Sticker.getStickerBitmap(resultPath) to get sticker bitmap.
                     */
                    break;
                case SAVE_STICKER_TO_PHONE:
                    if (resultPath != null) {
                        // Add result file to Gallery
                        galleryAddPic(resultPath, UUID.randomUUID().toString(), "saved image from editor");
                    }
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            buttonType = ButtonType.SAVE_STICKER_TO_PHONE;
        } else if (resultCode == RESULT_CANCELED && requestCode == CAMERA_PREVIEW_RESULT && data != null) {
            String sourcePath = data.getStringExtra(ImgLyIntent.SOURCE_IMAGE_PATH);
            Toast.makeText(PESDK.getAppContext(), "Editor canceled, sourceType image is:\n" + sourcePath, Toast.LENGTH_LONG).show();
        }
        finish();
    }

    // Important permission request for Android 6.0 and above, don't forget this!
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionRequest.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void permissionGranted() {

    }

    @Override
    public void permissionDenied() {
        // The Permission was rejected by the user. The Editor was not opened, as it could not save the result image.
        // TODO for you: Show a Hint to the User
    }

    private void galleryAddPic(String currentPhotoPath, String name, String description) {
        /*
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        */
        try {
            MediaStore.Images.Media.insertImage(getContentResolver(), currentPhotoPath, name, description);
        } catch (FileNotFoundException e) {
            Toast.makeText(PESDK.getAppContext(), "File not found, image wasn't saved, please try again. Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
