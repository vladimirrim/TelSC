package ru.spbau.mit.telsc.controller.command.userCommands.drawActions;

import android.graphics.Point;

import ru.spbau.mit.telsc.controller.command.StickerCommand;
import ru.spbau.mit.telsc.model.Sticker;

/**
 * Created by mikhail on 18.11.17.
 */

public class DrawLine extends StickerCommand {
    private class Line {
        Point point1, point2;

        Line(Point point1, Point point2) {
            this.point1 = point1;
            this.point2 = point2;
        }
    }

    private Line line;

    public DrawLine(Point point1, Point point2, Sticker sticker) {
        super(sticker);
        this.line = new Line(point1, point2);
    }

    @Override
    public void apply() {
        // TODO: draw line, static method call ?
    }
}
