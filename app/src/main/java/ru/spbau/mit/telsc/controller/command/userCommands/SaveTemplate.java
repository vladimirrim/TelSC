package ru.spbau.mit.telsc.controller.command.userCommands;

import ru.spbau.mit.telsc.controller.command.Command;
import ru.spbau.mit.telsc.template.Template;

/**
 * Created by mikhail on 18.11.17.
 */

public final class SaveTemplate implements Command {
    Template template;

    public SaveTemplate(Template template) {
        this.template = template;
    }

    @Override
    public void apply() {
    // TODO: saving template to database
    }
}
