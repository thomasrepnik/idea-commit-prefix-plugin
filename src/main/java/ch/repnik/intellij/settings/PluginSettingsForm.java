package ch.repnik.intellij.settings;

import javax.swing.*;

public class PluginSettingsForm {
    private JPanel mainPanel;
    private JTextField txtDelimiter;

    public JPanel getPanel(){
        return mainPanel;
    }

    public String getDelimiter(){
        return txtDelimiter.getText();
    }

    public void resetEditorFrom(PluginSettings settings){
        this.txtDelimiter.setText(settings.getCommitMessageDelimiter());
    }
}
