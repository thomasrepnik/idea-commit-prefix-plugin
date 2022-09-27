package ch.repnik.intellij;

import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.changes.CommitContext;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.checkin.VcsCheckinHandlerFactory;
import git4idea.GitVcs;
import org.jetbrains.annotations.NotNull;

public class GitBaseCheckinHandlerFactory extends VcsCheckinHandlerFactory {

    public GitBaseCheckinHandlerFactory(){
        super(GitVcs.getKey());
    }

    @NotNull
    @Override
    protected CheckinHandler createVcsHandler(@NotNull CheckinProjectPanel panel, @NotNull CommitContext commitContext) {
        return new CommitPrefixCheckinHandler(panel);
    }
}
