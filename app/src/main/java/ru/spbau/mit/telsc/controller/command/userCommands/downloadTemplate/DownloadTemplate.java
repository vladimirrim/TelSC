package ru.spbau.mit.telsc.controller.command.userCommands.downloadTemplate;

import ru.spbau.mit.telsc.controller.command.Command;
import ru.spbau.mit.telsc.template.Template;

/**
 * Created by mikhail on 18.11.17.
 */

public class DownloadTemplate implements Command {
    String templateName;

    public DownloadTemplate(String templateName) {
        this.templateName = templateName;
    }

    @Override
    public void apply() {
        // TODO
        Template downloadedTemplate = null;
        ApplyTemplate commandApplyTemplate = new ApplyTemplate(downloadedTemplate);
        commandApplyTemplate.apply();
    }
}
