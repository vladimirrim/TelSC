package ru.spbau.mit.telsc;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;

import org.telegram.telegrambots.bots.DefaultBotOptions;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import ru.spbau.mit.telsc.model.Sticker;
import ru.spbau.mit.telsc.telegramManager.TelegramManager;

public class MainActivity extends AppCompatActivity {
    public static final int PICK_IMAGE = 1;

    private Sticker sticker;
    private ArrayList<Sticker.Actions> savedTemplate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button chooseImageButton = (Button) findViewById(R.id.selectImage);
        chooseImageButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        });

        final Button uploadStickerButton = (Button) findViewById(R.id.uploadSticker);
        uploadStickerButton.setOnClickListener(v -> {
            byte[] byteArray = sticker.getRawData();
            TelegramManager manager = new TelegramManager(new DefaultBotOptions());
            SharedPreferences sp = getSharedPreferences("numberStorage", Activity.MODE_PRIVATE);
            int currentStickerNumber = sp.getInt("number", 6);
            try {
                manager.createSticker(new ByteArrayInputStream(byteArray), currentStickerNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("number", currentStickerNumber + 1);
            editor.apply();
        });

        final Button grayScalingFilter = (Button) findViewById(R.id.edit);
        grayScalingFilter.setOnClickListener(this::showPopupMenu);

        final Button templatesDatabase = (Button) findViewById(R.id.templatesDatabase);
        templatesDatabase.setOnClickListener(v -> savedTemplate = sticker.getTemplate());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE) {
            Uri pickedImage = data.getData();
            if (pickedImage != null) {
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
                cursor.moveToFirst();
                String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
                bitmap = Bitmap.createScaledBitmap(bitmap, 512, 512, false);
                sticker = new Sticker(bitmap);

                updateImageView();
                cursor.close();
            }
        }
    }

    private void updateImageView() {
        final ImageView stickerImageView = (ImageView) findViewById(R.id.stickerImageView);
        stickerImageView.setImageBitmap(sticker.getStickerImage());
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.popupmenu);

        popupMenu.setOnMenuItemClickListener(item -> {
            Sticker.Actions action = null;

            switch (item.getItemId()) {
                case R.id.loadTemplate:
                    ArrayList<Sticker.Actions> loadedTemplate = savedTemplate;
                    sticker.applyActions(loadedTemplate);
                    break;
                case R.id.line:
                    //Drawing Line Mode
                    break;
                case R.id.crop:
                    //Crop Mode
                    break;
                case R.id.eraser:
                    //Eraser Mode
                    break;
                case R.id.rotate:
                    action = Sticker.Actions.ROTATION_90_DEGREES_CLOCKWISE;
                    break;
                // Filters part
                case R.id.grayScaling:
                    action = Sticker.Actions.GRAY_SCALING;
                    break;
                case R.id.blur:
                    //Blur filter
                    break;
                case R.id.gauss:
                    //Gauss filter
                    break;
                default:
                    return false;
            }
            if (action != null)
                sticker.applyAction(action);
            updateImageView();
            return true;
        });

        popupMenu.show();
    }
}
