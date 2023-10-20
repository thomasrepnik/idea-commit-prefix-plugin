package ch.repnik.intellij.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@State(
        name = "ch.repnik.intellij.settings.PluginConfigService",
        storages = {
                @Storage(value = "git-auto-prefix.xml")
        }
)
public class PluginConfigService implements PersistentStateComponent<PluginConfigService.Configuration> {

    private Configuration configuration = new Configuration();

    @Override
    public Configuration getState() {
        System.out.println("getState");
        System.out.println(configuration.getWrapLeft());
        System.out.println(configuration.getWrapRight());
        return configuration;
    }

    @Override
    public void loadState(@NotNull Configuration configuration) {
        System.out.println("loadState");
        System.out.println(configuration.getWrapLeft());
        System.out.println(configuration.getWrapRight());
        this.configuration = configuration;
    }

    public static class Configuration implements Serializable {

        private String wrapLeft = "";
        private String wrapRight = ": ";

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


}
