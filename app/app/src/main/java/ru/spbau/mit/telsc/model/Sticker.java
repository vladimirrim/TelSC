package ru.spbau.mit.telsc.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import ru.spbau.mit.telsc.model.core.pixels.Pixels;

public class Sticker {
    private static final String STICKER_FILENAME_TO_SAVE_IN_CACHE = "sticker";

    private static Bitmap getTransparentBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);

        for (int i = 0; i < bitmap.getHeight(); i++)
            for (int j = 0; j < bitmap.getWidth(); j++) {
                Pixels.setTransparent(bitmap, j, i);
            }

        return bitmap;
    }

    public static String getEmptySticker(Context context) {
        Bitmap bitmap = getTransparentBitmap();
        return saveStickerInCache(bitmap, context);
    }

    public static String saveStickerInFile(Bitmap bitmap, Context context) {
        String fileName = STICKER_FILENAME_TO_SAVE_IN_CACHE;
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
            FileOutputStream fo = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (Exception e) {
            Toast.makeText(context, "Error occurred during saving sticker to file. Reason: "
                    + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            fileName = null;
        }

        return fileName;
    }

    public static String saveStickerInCache(Bitmap bitmap, Context context) {
        File savedSticker = null;

        try {
            savedSticker = File.createTempFile(STICKER_FILENAME_TO_SAVE_IN_CACHE, null, context.getCacheDir());
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
            FileOutputStream fo = new FileOutputStream(savedSticker);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            Toast.makeText(context, "Error occurred during saving sticker to temporary file in cache. Reason: "
                    + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }

        if (savedSticker == null)
            return null;

        return savedSticker.getAbsolutePath();
    }

    public static Bitmap getStickerBitmap(String imagePath) {
        Bitmap image = BitmapFactory.decodeFile(imagePath);
        return Bitmap.createScaledBitmap(image, 512, 512, false);
    }
}
