package ru.spbau.mit.telsc.controller.command.userCommands.drawActions;

import ru.spbau.mit.telsc.controller.command.Command;
import ru.spbau.mit.telsc.controller.command.StickerCommand;
import ru.spbau.mit.telsc.model.Sticker;

/**
 * Created by mikhail on 18.11.17.
 */

public class Rotate extends StickerCommand {
    private float degree;

    public Rotate(float degree, Sticker sticker) {
        super(sticker);
        this.degree = degree;
    }

    @Override
    public void apply() {
        // TODO
    }
}
