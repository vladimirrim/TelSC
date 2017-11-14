package ru.spbau.mit.telsc.model;

import android.graphics.Bitmap;

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
}
