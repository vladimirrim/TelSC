package ru.spbau.mit.telsc.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

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

import static ru.spbau.mit.telsc.view.ImageEditorActivity.ButtonType.DOWNLOAD_TEMPLATE_FROM_DB;
import static ru.spbau.mit.telsc.view.ImageEditorActivity.ButtonType.SAVE_STICKER_TO_PHONE;
import static ru.spbau.mit.telsc.view.ImageEditorActivity.ButtonType.UPLOAD_STICKER_TO_DB;
import static ru.spbau.mit.telsc.view.ImageEditorActivity.ButtonType.UPLOAD_STICKER_TO_TELEGRAM;
import static ru.spbau.mit.telsc.view.ImageEditorActivity.ButtonType.UPLOAD_TEMPLATE_TO_DB;

// TODO: fix bug: after long usage editor loads only part of image, but in fact it isn't broken.
// TODO: check on real devices. In emulator sometimes loads not all settings in template.

public class ImageEditorActivity extends AppCompatActivity implements PermissionRequest.Response {
    private static final String FOLDER = "TelSC";
    /**
     * Used to indicate that specific button was pressed and exactly this button caused editor finishing.
     * Incorrect behaviour can happen if more than one activity run simultaneously.
     */
    private static final int EDITOR_RESULT = 1;
    private static final int UPLOAD_TO_TELEGRAM = 2;
    private static final int UPLOAD_TEMPLATE_TO_DATABASE = 3;
    private static final int DOWNLOAD_TEMPLATE_FROM_DATABASE = 4;
    private static final int UPLOAD_STICKER_TO_DATABASE = 5;
    public static ButtonType buttonType = SAVE_STICKER_TO_PHONE;
    private String sourcePath = "";
    private byte[] currentEditorSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_editor);

        Intent intent = getIntent();

        String sourcePath = null;
        if (intent.hasExtra("pathToImage")) {
            sourcePath = intent.getStringExtra("pathToImage");
        }

        SettingsList settingsList = getInitializeSettingsList(sourcePath);

        if (sourcePath == null) {
            startCameraPreview(settingsList);
        } else {
            startEditor(settingsList);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == EDITOR_RESULT) {
            String resultPath = data.getStringExtra(ImgLyIntent.RESULT_IMAGE_PATH);
            sourcePath = data.getStringExtra(ImgLyIntent.SOURCE_IMAGE_PATH);

            try {
                currentEditorSettings = getCurrentEditorSettings(data);
            } catch (IOException e) {
                Toast.makeText(PESDK.getAppContext(), "Editor won't be restored because of error during creating template to restore editor:\n" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            if (buttonType == UPLOAD_STICKER_TO_DB) {

                Intent intent = new Intent(this, StickerNameUploadActivity.class);
                intent.putExtra("stickerName", Sticker.saveStickerInFile(Sticker.getStickerBitmap(resultPath),
                        this));
                startActivityForResult(intent, UPLOAD_STICKER_TO_DATABASE);
            }

            if (buttonType == UPLOAD_STICKER_TO_TELEGRAM) {
                Intent intent = new Intent(this, PhoneActivity.class);
                intent.putExtra("stickerName", Sticker.saveStickerInFile(Sticker.getStickerBitmap(resultPath), this));
                startActivityForResult(intent, UPLOAD_TO_TELEGRAM);
            }


            if (buttonType == SAVE_STICKER_TO_PHONE) {

                if (resultPath != null) {
                    galleryAddPic(resultPath, UUID.randomUUID().toString(), "saved image from editor");

                }

                if (currentEditorSettings != null) {
                    startEditor(applySettingsToImage(sourcePath, currentEditorSettings));
                } else {
                    finish();
                }
            }

            if (buttonType == UPLOAD_TEMPLATE_TO_DB) {

                if (currentEditorSettings != null) {
                    String fileName = "template";
                    try {

                        IOUtils.write(currentEditorSettings, openFileOutput(fileName, Context.MODE_PRIVATE));
                        Intent intent = new Intent(this, TemplateNameUploadActivity.class);
                        intent.putExtra("templateName", "template");
                        startActivityForResult(intent, UPLOAD_TEMPLATE_TO_DATABASE);

                    } catch (IOException e) {
                        Toast.makeText(PESDK.getAppContext(), "Error during creating template. Reason: " + e.getLocalizedMessage(),
                                Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(PESDK.getAppContext(), "Error during creating template", Toast.LENGTH_LONG).show();
                }

            }

            if (buttonType == DOWNLOAD_TEMPLATE_FROM_DB) {
                Intent intent = new Intent(this, TemplateNameDownloadActivity.class);
                startActivityForResult(intent, DOWNLOAD_TEMPLATE_FROM_DATABASE);
                return;
            }


        } else if (resultCode == RESULT_CANCELED && requestCode == EDITOR_RESULT && data != null) {
            sourcePath = data.getStringExtra(ImgLyIntent.SOURCE_IMAGE_PATH);
            Toast.makeText(PESDK.getAppContext(), "Editor canceled, sourceType image is:\n" + sourcePath, Toast.LENGTH_LONG).show();
            finish();
        }

        if (requestCode == UPLOAD_TO_TELEGRAM) {
            finish();
        }

        if (requestCode == DOWNLOAD_TEMPLATE_FROM_DATABASE) {
            byte[] downloadedSettings = null;
            try {
                downloadedSettings = IOUtils.toByteArray(openFileInput("template"));
            } catch (IOException e) {
                Toast.makeText(PESDK.getAppContext(), "No template will be applied. Error during downloading template: \n" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            if (currentEditorSettings != null && downloadedSettings != null) {

                startEditor(applySettingsToImage(sourcePath, currentEditorSettings, downloadedSettings));

            } else if (currentEditorSettings == null && downloadedSettings != null) {

                String resultPath = "";
                startEditor(applySettingsToImage(resultPath, downloadedSettings));

            } else if (currentEditorSettings != null) {

                startEditor(applySettingsToImage(sourcePath, currentEditorSettings));

            } else {
                Toast.makeText(PESDK.getAppContext(), "Editor cannot be restored", Toast.LENGTH_LONG).show();
                finish();
            }

        }

        if (requestCode == UPLOAD_STICKER_TO_DATABASE || requestCode == UPLOAD_TEMPLATE_TO_DATABASE) {
            if (currentEditorSettings != null) {
                startEditor(applySettingsToImage(sourcePath, currentEditorSettings));
            } else {
                finish();
            }

        }

        buttonType = SAVE_STICKER_TO_PHONE;
    }

    /**
     * Returns settings of current editor that were passed by intent after finishing this current editor activity.
     * Method could be used to relaunch (restore) closed editor activity.
     *
     * @param data intent where you can get settings list.
     * @return settings in byte array
     * @throws IOException because of use PESDKFileWrite to get bytes of settings list object.
     */
    private byte[] getCurrentEditorSettings(android.content.Intent data) throws IOException {
        SettingsList settingsList = data.getParcelableExtra(ImgLyIntent.SETTINGS_LIST);
        PESDKFileWriter writer = new PESDKFileWriter(settingsList);

        return writer.writeJsonAsBytes();
    }

    /**
     * Creates default settings list to given image and loads other settings given.
     *
     * @param imagePath      path to image that will be loaded to editor and settings will be applied to it.
     * @param ListOfSettings extra settings to apply, they describe templates.
     * @return result settings list: combination of all given settings in the order as they were passed to this method.
     */
    @NonNull
    private SettingsList applySettingsToImage(@NonNull String imagePath, byte[]... ListOfSettings) {
        SettingsList settingsList = getInitializeSettingsList(imagePath);

        PESDKFileReader reader = new PESDKFileReader(settingsList);
        try {
            for (byte[] settings : ListOfSettings) {
                reader.readJson(settings);
            }
        } catch (IOException e) {
            Toast.makeText(this, "Only default settings is loaded, could be problems with downloading template and restoring editor. Error while opening json:" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        return settingsList;
    }

    /**
     * Starts editor activity and then loads photo that is specified by given settings list.
     *
     * @param settingsList settings of editor where is also specified which image to load.
     */
    private void startEditor(@NonNull SettingsList settingsList) {
        new PhotoEditorBuilder(this)
                .setSettingsList(settingsList)
                .startActivityForResult(this, EDITOR_RESULT);
    }

    /**
     * Starts camera activity and then loads photo that was taken by user into editor.
     *
     * @param settingsList settings of camera and editor.
     */
    private void startCameraPreview(@NonNull SettingsList settingsList) {
        new CameraPreviewBuilder(this)
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

    /**
     * Copies result image to gallery.
     *
     * @param resultPath  path to result image.
     * @param name        of image in gallery.
     * @param description of image in gallery.
     */
    private void galleryAddPic(String resultPath, String name, String description) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(resultPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

        try {
            MediaStore.Images.Media.insertImage(getContentResolver(), resultPath, name, description);
        } catch (FileNotFoundException e) {
            Toast.makeText(PESDK.getAppContext(), "File not found, image wasn't saved, please try again. Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Method creates default settings list.
     *
     * @param imageToLoadPath path to image which will be loaded and edited. If null then method sets up sets up camera run settings.
     * @return created settings list instance.
     */
    @NonNull
    private SettingsList getInitializeSettingsList(@Nullable String imageToLoadPath) {
        SettingsList settingsList = new SettingsList();

        if (imageToLoadPath == null) {
            settingsList
                    .getSettingsModel(CameraSettings.class)
                    .setExportDir(Directory.DCIM, FOLDER)
                    .setExportPrefix("camera_");
        } else {
            settingsList
                    .getSettingsModel(EditorLoadSettings.class)
                    .setImageSourcePath(imageToLoadPath, true);
        }

        settingsList
                .getSettingsModel(EditorSaveSettings.class)
                .setExportDir(Directory.DCIM, FOLDER)
                .setExportPrefix("result_")
                .setJpegQuality(80, true)
                .setSavePolicy(EditorSaveSettings.SavePolicy.KEEP_SOURCE_AND_CREATE_ALWAYS_OUTPUT);

        return settingsList;
    }

    /**
     * Describes which button was pressed.
     */
    public enum ButtonType {
        SAVE_STICKER_TO_PHONE, UPLOAD_STICKER_TO_TELEGRAM, UPLOAD_STICKER_TO_DB,
        UPLOAD_TEMPLATE_TO_DB, DOWNLOAD_TEMPLATE_FROM_DB
    }
}
