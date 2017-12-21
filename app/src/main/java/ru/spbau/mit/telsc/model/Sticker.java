package ru.spbau.mit.telsc.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ru.spbau.mit.telsc.model.stickerManager.StickerManager;

/**
 * Created by mikhail on 14.11.17.
 */

public class Sticker {
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

    public static String saveStickerInFile(Bitmap bitmap, Context context) {
        String fileName = "sticker";
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }

    public static byte[] getRawData(ImageView imageView) {
        Bitmap image = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        //image = Bitmap.createScaledBitmap(image, 512, 512, false);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap getStickerBitmap(ImageView imageView) {
        Bitmap image = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        return Bitmap.createScaledBitmap(image, 512, 512, false);
    }

    public static Bitmap getStickerBitmap(String imagePath) {
        Bitmap image = BitmapFactory.decodeFile(imagePath);
        return Bitmap.createScaledBitmap(image, 512, 512, false);
    }
}
