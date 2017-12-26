package ru.spbau.mit.telsc.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import ly.img.android.PESDK;
import ly.img.android.sdk.models.constant.Directory;
import ly.img.android.sdk.models.state.CameraSettings;
import ly.img.android.sdk.models.state.EditorLoadSettings;
import ly.img.android.sdk.models.state.EditorSaveSettings;
import ly.img.android.sdk.models.state.manager.SettingsList;
import ly.img.android.serializer._3._0._0.PESDKFileReader;
import ly.img.android.serializer._3._0._0.PESDKFileWriter;
import ly.img.android.ui.activities.CameraPreviewBuilder;
import ly.img.android.ui.activities.ImgLyIntent;
import ly.img.android.ui.activities.PhotoEditorBuilder;
import ly.img.android.ui.utilities.PermissionRequest;
import ru.spbau.mit.telsc.R;
import ru.spbau.mit.telsc.model.Sticker;

// TODO: fix bug: after long usage editor loads only part of image, but in fact it isn't broken.

public class ImageEditorActivity extends AppCompatActivity implements PermissionRequest.Response {

    /*
     * Delete this function, just for testing, while Vova's db isn't working.
     */
    private void uploadTemplate(byte[] bytes, String templateName) throws IOException {
        FileUtils.writeByteArrayToFile(new File(Environment.getExternalStorageDirectory(), templateName), bytes);
    }

    /*
     * Delete this function, just for testing, while Vova's db isn't working.
     */
    private byte[] downloadTemplate(String templateName) throws IOException {
        return FileUtils.readFileToByteArray(new File(Environment.getExternalStorageDirectory(), templateName));
    }

    private static final String FOLDER = "TelSC";
    private String currentImagePath;
    public static int EDITOR_RESULT = 1;
    public enum ButtonType {
        SAVE_STICKER_TO_PHONE, UPLOAD_STICKER_TO_TELEGRAM, UPLOAD_STICKER_TO_DB,
        UPLOAD_TEMPLATE_TO_DB, DOWNLOAD_TEMPLATE_FROM_DB
    }
    public static ButtonType buttonType = ButtonType.SAVE_STICKER_TO_PHONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_editor);

        Intent intent = getIntent();

        if (intent.hasExtra("pathToImage")) {
            currentImagePath = intent.getStringExtra("pathToImage");
        }

        SettingsList settingsList = getInitializeSettingsList(currentImagePath == null);

        if (currentImagePath == null) {
            new CameraPreviewBuilder(this)
                    .setSettingsList(settingsList)
                    .startActivityForResult(this, EDITOR_RESULT);
        }
        else {
            PhotoEditorBuilder photoEditorBuilder = new PhotoEditorBuilder(this);
            photoEditorBuilder
                    .setSettingsList(settingsList)
                    .startActivityForResult(this, EDITOR_RESULT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == EDITOR_RESULT) {
            String resultPath = data.getStringExtra(ImgLyIntent.RESULT_IMAGE_PATH);

            byte[] currentEditorSettings = null;
            try {
                currentEditorSettings = getCurrentEditorSettings(data);
            } catch (IOException e) {
                Toast.makeText(PESDK.getAppContext(), "Editor won't be restored because of error during creating template to restore editor:\n" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

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
                case UPLOAD_TEMPLATE_TO_DB:
                    String uploadTemplateName = "template.pesdk";
                    // TODO: Vova call your activity for result or call popup window where you enter template name. I'd prefer popup menu.

                    if (currentEditorSettings != null) {
                        try {
                            uploadTemplate(currentEditorSettings, uploadTemplateName);
                        } catch (IOException e) {
                            Toast.makeText(PESDK.getAppContext(), "Error during uploading template:\n" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                    else {
                        Toast.makeText(PESDK.getAppContext(), "Error during creating template", Toast.LENGTH_LONG).show();
                    }
                    break;
                case DOWNLOAD_TEMPLATE_FROM_DB:
                    String downloadTemplateName = "template.pesdk";

                    try {
                        // TODO: Vova call your activity for result or call popup window where you choose template to download. I'd prefer popup menu.
                        // TODO: check if we can apply currentTemplate and downloaded template.
                        if (currentEditorSettings != null) {
                            applyDownloadedSettingsToSourceImage(currentEditorSettings, downloadTemplate(downloadTemplateName));
                        }
                        else {
                            applyDownloadedSettingsToResultImage(downloadTemplate(downloadTemplateName), resultPath);
                        }
                    } catch (IOException e) {
                        Toast.makeText(PESDK.getAppContext(), "Error during downloading template: \n" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    break;
                default:
                    throw new IllegalArgumentException();
            }

            if (buttonType == ButtonType.UPLOAD_STICKER_TO_TELEGRAM) {
                finish();
            }
            else if (buttonType != ButtonType.DOWNLOAD_TEMPLATE_FROM_DB) {
                if (currentEditorSettings != null) {
                    restoreEditor(currentEditorSettings);
                }
                else {
                    finish();
                }
            }
        } else if (resultCode == RESULT_CANCELED && requestCode == EDITOR_RESULT && data != null) {
            String sourcePath = data.getStringExtra(ImgLyIntent.SOURCE_IMAGE_PATH);
            Toast.makeText(PESDK.getAppContext(), "Editor canceled, sourceType image is:\n" + sourcePath, Toast.LENGTH_LONG).show();
            finish();
        }

        buttonType = ButtonType.SAVE_STICKER_TO_PHONE;
    }

    private byte[] getCurrentEditorSettings(android.content.Intent data) throws IOException {
        SettingsList settingsList = data.getParcelableExtra(ImgLyIntent.SETTINGS_LIST);
        PESDKFileWriter writer = new PESDKFileWriter(settingsList);

        return writer.writeJsonAsBytes();
    }

    private void restoreEditor(byte[] settingsToRestore) {
        applyDownloadedSettingsToResultImage(settingsToRestore, currentImagePath);
    }

    private void applyDownloadedSettingsToSourceImage(byte[] currentSettings, byte[] settings) {
        SettingsList settingsList = getInitializeSettingsList(false);

        PESDKFileReader reader = new PESDKFileReader(settingsList);
        try {
            reader.readJson(currentSettings);
            reader.readJson(settings);
        } catch (IOException e) {
            Toast.makeText(this, "Error while opening json:" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            finish();
            return;
        }

        new PhotoEditorBuilder(this)
                .setSettingsList(settingsList)
                .startActivityForResult(this, EDITOR_RESULT);
    }

    private void applyDownloadedSettingsToResultImage(byte[] settings, String resultPath) {
        currentImagePath = resultPath;

        SettingsList settingsList = getInitializeSettingsList(false);

        PESDKFileReader reader = new PESDKFileReader(settingsList);
        try {
            reader.readJson(settings);
        } catch (IOException e) {
            Toast.makeText(this, "Error while opening json:" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            finish();
            return;
        }

        new PhotoEditorBuilder(this)
                .setSettingsList(settingsList)
                .startActivityForResult(this, EDITOR_RESULT);
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

    private void galleryAddPic(String resultPhotoPath, String name, String description) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(resultPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

        try {
            MediaStore.Images.Media.insertImage(getContentResolver(), resultPhotoPath, name, description);
        } catch (FileNotFoundException e) {
            Toast.makeText(PESDK.getAppContext(), "File not found, image wasn't saved, please try again. Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private SettingsList getInitializeSettingsList(boolean takePictureFromCamera) {
        SettingsList settingsList = new SettingsList();

        if (takePictureFromCamera) {
            settingsList
                    .getSettingsModel(CameraSettings.class)
                    .setExportDir(Directory.DCIM, FOLDER)
                    .setExportPrefix("camera_");
        }
        else {
            settingsList
                    .getSettingsModel(EditorLoadSettings.class)
                    .setImageSourcePath(currentImagePath, true);
        }

        settingsList
                .getSettingsModel(EditorSaveSettings.class)
                .setExportDir(Directory.DCIM, FOLDER)
                .setExportPrefix("result_")
                .setJpegQuality(80, true)
                .setSavePolicy(EditorSaveSettings.SavePolicy.KEEP_SOURCE_AND_CREATE_ALWAYS_OUTPUT);

        return settingsList;
    }
}
