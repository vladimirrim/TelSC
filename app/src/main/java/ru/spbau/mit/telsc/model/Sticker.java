package ru.spbau.mit.telsc.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ru.spbau.mit.telsc.model.core.pixels.Pixels;
import ru.spbau.mit.telsc.model.stickerManager.StickerManager;

/**
 * Created by mikhail on 14.11.17.
 */

public class Sticker {
    private static String STICKER_FILENAME_TO_SAVE_IN_CACHE = "sticker";

    public enum Actions {
        GRAY_SCALING, ROTATION_90_DEGREES_CLOCKWISE
    }

    private Map<Actions, Runnable> methodsByStringAction;

    private StickerManager stickerManager;

    private ArrayList<Actions> actions = new ArrayList<>();

    public Bitmap getStickerImage() {
        return stickerManager.getStickerImage();
    }

    public Sticker(Bitmap stickerImage) {
        stickerManager = new StickerManager(stickerImage);

        methodsByStringAction = new HashMap<>();

        methodsByStringAction.put(Actions.GRAY_SCALING,
                () -> stickerManager.applyGrayScaleFilter());
        methodsByStringAction.put(Actions.ROTATION_90_DEGREES_CLOCKWISE,
                () -> stickerManager.rotate90DegreesClockwise());

        methodsByStringAction = Collections.unmodifiableMap(methodsByStringAction);
    }

    public void applyActions(ArrayList<Actions> actionsToApply) {
        for (Actions actionToApply : actionsToApply) {
            methodsByStringAction.get(actionToApply).run();
            actions.add(actionToApply);
        }
    }

    public void applyAction(Actions action) {
        methodsByStringAction.get(action).run();
        actions.add(action);
    }

    public ArrayList<Actions> getTemplate() {
        return new ArrayList<>(actions);
    }

    public byte[] getRawData() {
        return stickerManager.getRawData();
    }

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

    public static byte[] getRawData(ImageView imageView) {
        Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        //image = Bitmap.createScaledBitmap(image, 512, 512, false);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap getStickerBitmap(ImageView imageView) {
        Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        return Bitmap.createScaledBitmap(image, 512, 512, false);
    }

    public static Bitmap getStickerBitmap(String imagePath) {
        Bitmap image = BitmapFactory.decodeFile(imagePath);
        return Bitmap.createScaledBitmap(image, 512, 512, false);
    }
}
