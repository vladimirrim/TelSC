package ru.spbau.mit.telsc.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Sticker {
    private static final String STICKER_FILENAME_TO_SAVE_IN_CACHE = "sticker";

    private static Bitmap getTransparentBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);

        for (int i = 0; i < bitmap.getHeight(); i++) {
            for (int j = 0; j < bitmap.getWidth(); j++) {
                setTransparent(bitmap, j, i);
            }
        }

        return bitmap;
    }

    public static String createEmptySticker(Context context) throws IOException {
        Bitmap bitmap = getTransparentBitmap();
        return saveStickerInCache(bitmap, context);
    }

    public static String saveStickerInFile(Bitmap bitmap, Context context) throws IOException {
        try (FileOutputStream fileOutputStream = context.openFileOutput(STICKER_FILENAME_TO_SAVE_IN_CACHE, Context.MODE_PRIVATE)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        }

        return STICKER_FILENAME_TO_SAVE_IN_CACHE;
    }

    public static String saveStickerInCache(Bitmap bitmap, Context context) throws IOException {
        File savedSticker = File.createTempFile(STICKER_FILENAME_TO_SAVE_IN_CACHE, null, context.getCacheDir());
        try (FileOutputStream fileOutputStream = new FileOutputStream(savedSticker)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        }

        return savedSticker.getAbsolutePath();
    }

    public static Bitmap getStickerBitmap(String imagePath) {
        Bitmap image = BitmapFactory.decodeFile(imagePath);
        return Bitmap.createScaledBitmap(image, 512, 512, false);
    }

    private static void setTransparent(Bitmap image, int x, int y) {
        int color = image.getPixel(x, y);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        image.setPixel(x, y, Color.argb(0, red, blue, green));
    }
}
