package ru.spbau.mit.telsc.controller.command.userCommands;

import ru.spbau.mit.telsc.controller.command.Command;
import ru.spbau.mit.telsc.controller.command.StickerCommand;
import ru.spbau.mit.telsc.model.Sticker;

/**
 * Created by mikhail on 18.11.17.
 */

public final class SelectElement extends StickerCommand {
    private int elementId;

    public SelectElement(Sticker sticker, int elementId) {
        super(sticker);
        this.elementId = elementId;
    }

    @Override
    public void apply() {
        // TODO: change mode on this elemnt in sticker
    }
}
