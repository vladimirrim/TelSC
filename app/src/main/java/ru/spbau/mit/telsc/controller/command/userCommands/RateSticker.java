package ru.spbau.mit.telsc.controller.command.userCommands;

import ru.spbau.mit.telsc.controller.command.StickerCommand;
import ru.spbau.mit.telsc.model.Sticker;

/**
 * Created by mikhail on 18.11.17.
 */

public class RateSticker extends StickerCommand {
    private float rate;

    public RateSticker(float rate, Sticker sticker) {
        super(sticker);
        this.rate = rate;
    }

    @Override
    public void apply() {
        // TODO: rating sticker
    }
}
