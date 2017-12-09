package ru.spbau.mit.telsc.controller.command.userCommands.downloadTemplate;

import ru.spbau.mit.telsc.controller.command.Command;
import ru.spbau.mit.telsc.template.Template;

/**
 * Created by mikhail on 18.11.17.
 */

class ApplyTemplate implements Command {
    Template template;

    public ApplyTemplate(Template template) {
        this.template = template;
    }

    @Override
    public void apply() {
        // TODO
    }
}
