package ch.repnik.intellij.settings;

import com.intellij.ide.util.PropertiesComponent;

public class PluginSettings {

    private static PluginSettings instance;
    private static final String SETTING_COMMIT_MESSAGE_DELIMITER = "git.auto.prefix.delimiter";

    private String commitMessageDelimiter;

    private PluginSettings(){}

    public static PluginSettings getInstance(){
        if (instance == null){
            instance = new PluginSettings();

            //Set unset Properties
            instance.savePropertyIfUnset(SETTING_COMMIT_MESSAGE_DELIMITER, ": ");

            //Load all properties
            instance.load();
        }

        return instance;
    }

    private void savePropertyIfUnset(String propertyName, String defaultValue){
        boolean valueSet = PropertiesComponent.getInstance().isValueSet(propertyName);
        if (!valueSet){
            PropertiesComponent.getInstance().setValue(propertyName, defaultValue);
        }
    }

    private void load(){
        this.commitMessageDelimiter = PropertiesComponent.getInstance().getValue(SETTING_COMMIT_MESSAGE_DELIMITER);
    }

    public void save(){
        PropertiesComponent.getInstance().setValue(SETTING_COMMIT_MESSAGE_DELIMITER, this.commitMessageDelimiter);
    }

    public String getCommitMessageDelimiter() {
        return commitMessageDelimiter;
    }

    public void setCommitMessageDelimiter(String commitMessageDelimiter) {
        this.commitMessageDelimiter = commitMessageDelimiter;
    }

}
