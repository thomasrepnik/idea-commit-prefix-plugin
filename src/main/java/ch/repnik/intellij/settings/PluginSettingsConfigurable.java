package ch.repnik.intellij.settings;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PluginSettingsConfigurable implements SearchableConfigurable {

    private Pattern allowedCharsPattern = Pattern.compile("[ :\\_\\-/\\|,\\.]+");
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
        String oldValue = settings.getCommitMessageDelimiter();
        String newValue = settingsForm.getDelimiter();
        return !Objects.equals(oldValue, newValue);
    }

    @Override
    public void apply() throws ConfigurationException {
        if (settingsForm != null) {
            if (isModified()) {
                validate();
                PluginSettings.getInstance().setCommitMessageDelimiter(settingsForm.getDelimiter());
                PluginSettings.getInstance().save();
            }
        }
    }

    private void validate() throws ConfigurationException {

        if (settingsForm.getDelimiter().isEmpty()){
            throw new ConfigurationException("Delimiter must not be empty", "Validation failed");
        }

        if (!allowedCharsPattern.matcher(settingsForm.getDelimiter()).matches()){
            throw new ConfigurationException("Delimiter can only contain following chars: \" :_-/|,.\"", "Validation failed");
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
