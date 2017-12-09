package ru.spbau.mit.telsc.controller.command.userCommands;

import ru.spbau.mit.telsc.controller.command.Command;
import ru.spbau.mit.telsc.controller.command.StickerCommand;
import ru.spbau.mit.telsc.model.Sticker;

/**
 * Created by mikhail on 18.11.17.
 */

public class Filter extends StickerCommand {
    public enum Filters {
        GRAY_SCALING, BLUR, GAUSS;
    }

    private Filters filter;

    public Filter(Filters filter, Sticker sticker) {
        super(sticker);
        this.sticker = sticker;
    }

    @Override
    public void apply() {
        /*
        switch (filter){
            case Filters.GRAY_SCALING:
                sticker.grayScale();
        }
        */

        // TODO: think about static methods in Filter +
        // TODO: saving sticker to database
    }

}
