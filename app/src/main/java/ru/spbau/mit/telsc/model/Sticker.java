package ru.spbau.mit.telsc.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.widget.Toast;

import java.io.File;
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

    public static String createEmptySticker(Context context) {
        Bitmap bitmap = getTransparentBitmap();
        return saveStickerInCache(bitmap, context);
    }

    public static String saveStickerInFile(Bitmap bitmap, Context context) {
        String fileName = STICKER_FILENAME_TO_SAVE_IN_CACHE;

        try (FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (Exception e) {
            Toast.makeText(context, "Error occurred during saving sticker to file. Reason: "
                    + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            fileName = null;
        }

        return fileName;
    }

    public static String saveStickerInCache(Bitmap bitmap, Context context) {
        File savedSticker;

        try {
            savedSticker = File.createTempFile(STICKER_FILENAME_TO_SAVE_IN_CACHE, null, context.getCacheDir());
        } catch (IOException e) {
            Toast.makeText(context, "Error occurred during saving sticker to temporary file in cache. Reason: "
                    + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

            return null;
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(savedSticker)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (IOException e) {
            Toast.makeText(context, "Error occurred during saving sticker to temporary file in cache. Reason: "
                    + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
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
