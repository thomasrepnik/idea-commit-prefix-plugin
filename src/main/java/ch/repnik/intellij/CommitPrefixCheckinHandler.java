package ch.repnik.intellij;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.BranchChangeListener;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.impl.ProjectLevelVcsManagerImpl;
import com.intellij.util.messages.MessageBusConnection;
import git4idea.GitLocalBranch;
import git4idea.branch.GitBranchUtil;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommitPrefixCheckinHandler extends CheckinHandler implements BranchChangeListener {

    private final Logger log = Logger.getInstance(getClass());
    private CheckinProjectPanel panel;
    private static final Pattern branchNamePattern = Pattern.compile("(?<=\\/)([A-Z0-9]+-[0-9]+)");
    private static final Pattern prefixPattern = Pattern.compile("[A-Z0-9]+-[0-9]+:");


    public CommitPrefixCheckinHandler(CheckinProjectPanel panel) {
        this.panel = panel;

        MessageBusConnection connect = panel.getProject().getMessageBus().connect();
        connect.subscribe(BranchChangeListener.VCS_BRANCH_CHANGED, this);

        updateCommitMessage();
    }

    private void updateCommitMessage() {
        String branchName = extractBranchName();
        log.warn("BranchName: " + branchName);

        Matcher matcher = branchNamePattern.matcher(branchName);
        if (matcher.find()){
            String newMessage = updatePrefix(matcher.group(1), panel.getCommitMessage());
            panel.setCommitMessage(newMessage);
        }

    }

    static String updatePrefix(String newPrefix, String currentMessage){
        if (currentMessage == null || currentMessage.trim().isEmpty()){
            return newPrefix + ": ";
        }

        //If there is already a commit message only replace the prefix at the beginning
        Matcher matcher = prefixPattern.matcher(currentMessage);
        if (matcher.find() && currentMessage.substring(0, matcher.start()).trim().isEmpty()){
            return currentMessage.replaceFirst(prefixPattern.pattern(), newPrefix + ":");
        }

        return currentMessage;
    }




    private String extractBranchName() {
        Project project = panel.getProject();

        String branch = "";
        ProjectLevelVcsManager instance = ProjectLevelVcsManagerImpl.getInstance(project);
        if (instance.checkVcsIsActive("Git")) {
            GitLocalBranch currentBranch = GitBranchUtil.getCurrentRepository(project).getCurrentBranch();

            if (currentBranch != null) {
                // Branch name  matches Ticket Name
                branch = currentBranch.getName().trim();
            }
        }

        return branch;
    }


    @Override
    public void branchWillChange(@NotNull String branchName) {
        updateCommitMessage();
    }

    @Override
    public void branchHasChanged(@NotNull String branchName) {
        updateCommitMessage();
    }
}
