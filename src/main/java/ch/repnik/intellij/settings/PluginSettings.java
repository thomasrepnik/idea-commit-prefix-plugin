package ch.repnik.intellij.settings;

import com.intellij.ide.util.PropertiesComponent;

public class PluginSettings {

    private static PluginSettings instance;
    private static final String SETTING_COMMIT_MESSAGE_DELIMITER = "git.auto.prefix.delimiter";
    private static final String SETTING_WRAP_LEFT = "git.auto.prefix.wrap.left";
    private static final String SETTING_WRAP_RIGHT = "git.auto.prefix.wrap.right";


    private String commitMessageDelimiter;
    private String wrapLeft;
    private String wrapRight;

    private PluginSettings(){}

    public static PluginSettings getInstance(){
        if (instance == null){
            instance = new PluginSettings();

            //Set unset Properties
            PropertiesComponent properties = PropertiesComponent.getInstance();

            if (properties.isValueSet(SETTING_COMMIT_MESSAGE_DELIMITER)){
                properties.setValue(SETTING_WRAP_RIGHT, properties.getValue(SETTING_COMMIT_MESSAGE_DELIMITER));
                properties.unsetValue(SETTING_COMMIT_MESSAGE_DELIMITER);
            }

            PluginSettings.instance.savePropertyIfUnset(SETTING_WRAP_RIGHT, ": ");
            PluginSettings.instance.savePropertyIfUnset(SETTING_WRAP_LEFT, "");

            //Load all properties
            PluginSettings.instance.load();
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
        this.wrapLeft = PropertiesComponent.getInstance().getValue(SETTING_WRAP_LEFT);
        this.wrapRight = PropertiesComponent.getInstance().getValue(SETTING_WRAP_RIGHT);
    }

    public void save(){
        PropertiesComponent.getInstance().setValue(SETTING_COMMIT_MESSAGE_DELIMITER, this.commitMessageDelimiter);
        PropertiesComponent.getInstance().setValue(SETTING_WRAP_LEFT, this.wrapLeft);
        PropertiesComponent.getInstance().setValue(SETTING_WRAP_RIGHT, this.wrapRight);
    }

    public String getCommitMessageDelimiter() {
        return commitMessageDelimiter;
    }

    public void setCommitMessageDelimiter(String commitMessageDelimiter) {
        this.commitMessageDelimiter = commitMessageDelimiter;
    }

    public String getWrapLeft() {
        return wrapLeft;
    }

    public void setWrapLeft(String wrapLeft) {
        this.wrapLeft = wrapLeft;
    }

    public String getWrapRight() {
        return wrapRight;
    }

    public void setWrapRight(String wrapRight) {
        this.wrapRight = wrapRight;
    }

}
