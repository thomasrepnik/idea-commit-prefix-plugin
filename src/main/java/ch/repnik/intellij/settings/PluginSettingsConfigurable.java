package ch.repnik.intellij.settings;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;
import java.util.regex.Pattern;

public class PluginSettingsConfigurable implements SearchableConfigurable {

    private Pattern allowedCharsPattern = Pattern.compile("[ \\[\\]\\(\\)\\{\\}:\\_\\-/\\|,\\.]+");
    private PluginSettingsForm settingsForm;


    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Git Auto Prefix";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (settingsForm == null) {
            settingsForm = new PluginSettingsForm();
        }
        reset();
        return settingsForm.getPanel();
    }

    @Override
    public boolean isModified() {
        PluginSettings settings = PluginSettings.getInstance();
        String oldValueRight = settings.getWrapRight();
        String newValueRight = settingsForm.getWrapRight();
        String oldValueLeft = settings.getWrapLeft();
        String newValueLeft = settingsForm.getWrapRight();
        return !Objects.equals(oldValueRight, newValueRight) || !Objects.equals(oldValueLeft, newValueLeft);
    }

    @Override
    public void apply() throws ConfigurationException {
        if (settingsForm != null) {
            if (isModified()) {
                validate();
                PluginSettings.getInstance().setWrapRight(settingsForm.getWrapRight());
                PluginSettings.getInstance().setWrapLeft(settingsForm.getWrapLeft());
                PluginSettings.getInstance().save();
            }
        }
    }

    private void validate() throws ConfigurationException {

        if (settingsForm.getWrapRight().isEmpty()){
            throw new ConfigurationException("Wrap Right/Delimiter must not be empty", "Validation failed");
        }

        if (!allowedCharsPattern.matcher(settingsForm.getWrapRight()).matches()){
            throw new ConfigurationException("Wrap Right/Delimiter can only contain following chars: \" [](){}:_-/|,.\"", "Validation failed");
        }

        if (!allowedCharsPattern.matcher(settingsForm.getWrapLeft()).matches()){
            throw new ConfigurationException("Wrap Left can only contain following chars: \" [](){}:_-/|,.\"", "Validation failed");
        }
    }

    @Override
    public void reset() {
        if (settingsForm != null) {
            settingsForm.resetEditorFrom(PluginSettings.getInstance());
        }
    }

    @NotNull
    @Override
    public String getId() {
        return "git.auto.prefix";
    }


    @Nullable
    @Override
    public Runnable enableSearch(String option) {
        return null;
    }
}
