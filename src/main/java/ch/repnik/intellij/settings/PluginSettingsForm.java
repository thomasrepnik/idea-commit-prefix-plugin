package ch.repnik.intellij.settings;

import com.intellij.openapi.options.BaseConfigurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Objects;
import java.util.regex.Pattern;

public class PluginSettingsForm extends BaseConfigurable implements SearchableConfigurable {
    private JPanel mainPanel;
    private JTextField txtWrapLeft;
    private JTextField txtWrapRight;
    private JComboBox cboPosition;

    private Project project;

    private Pattern allowedCharsPattern = Pattern.compile("[ \\[\\]\\(\\)\\{\\}:\\_\\-/\\|,\\.]+");


    public PluginSettingsForm(Project project){
        txtWrapLeft.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (txtWrapLeft.getText().length() >= 5 ) // limit textfield to 5 characters
                    e.consume();
            }
        });

        txtWrapRight.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (txtWrapRight.getText().length() >= 5 ) // limit textfield to 5 characters
                    e.consume();
            }
        });

        this.project = project;
    }

    public JPanel getPanel(){
        return mainPanel;
    }

    public String getWrapRight(){
        return txtWrapRight.getText();
    }

    public String getWrapLeft() {return txtWrapLeft.getText();}

    public Position getIssueKeyPosition() {
        return Position.parse(cboPosition.getSelectedItem().toString());
    }

    public void resetEditorFrom(PluginConfigService settings){
        this.txtWrapLeft.setText(settings.getState().getWrapLeft());
        this.txtWrapRight.setText(settings.getState().getWrapRight());
        this.cboPosition.setSelectedItem(settings.getState().getIssueKeyPosition().getStringValue());
    }

    @Override
    public @NotNull @NonNls String getId() {
        return "Git Auto Prefix";
    }

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Git Auto Prefix";
    }

    @Override
    public @Nullable JComponent createComponent() {
        return mainPanel;
    }

    @Override
    public boolean isModified() {
        PluginConfigService.Configuration state = project.getService(PluginConfigService.class).getState();
        String oldValueRight = state.getWrapRight();
        String newValueRight = getWrapRight();
        String oldValueLeft = state.getWrapLeft();
        String newValueLeft = getWrapRight();
        Position oldIssueKeyPosition = state.getIssueKeyPosition();
        Position newIssueKeyPosition = getIssueKeyPosition();
        return !Objects.equals(oldValueRight, newValueRight) || !Objects.equals(oldValueLeft, newValueLeft) || oldIssueKeyPosition != newIssueKeyPosition;
    }

    @Override
    public void apply() throws ConfigurationException {
        if (mainPanel != null) {
            if (isModified()) {
                validate();
                System.out.println("Config applied");
                PluginConfigService.Configuration state = project.getService(PluginConfigService.class).getState();
                state.setWrapRight(getWrapRight());
                state.setWrapLeft(getWrapLeft());
                state.setIssueKeyPosition(getIssueKeyPosition());
            }
        }
    }

    private void validate() throws ConfigurationException {

        if (getWrapRight().isEmpty()){
            throw new ConfigurationException("Wrap Right/Delimiter must not be empty", "Validation failed");
        }

        if (!allowedCharsPattern.matcher(getWrapRight()).matches()){
            throw new ConfigurationException("Wrap Right/Delimiter can only contain following chars: \" [](){}:_-/|,.\"", "Validation failed");
        }

        if (!getWrapLeft().isEmpty() && !allowedCharsPattern.matcher(getWrapLeft()).matches()){
            throw new ConfigurationException("Wrap Left can only contain following chars: \" [](){}:_-/|,.\"", "Validation failed");
        }
    }

    @Override
    public void reset() {
        if (mainPanel != null) {
            resetEditorFrom(project.getService(PluginConfigService.class));
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
