package ch.repnik.intellij;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.BranchChangeListener;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.impl.ProjectLevelVcsManagerImpl;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import com.intellij.util.messages.MessageBusConnection;
import git4idea.GitLocalBranch;
import git4idea.branch.GitBranchUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommitPrefixCheckinHandler extends CheckinHandler implements BranchChangeListener {

    private final Logger log = Logger.getInstance(getClass());
    private CheckinProjectPanel panel;
    private static final Pattern branchNamePattern = Pattern.compile("(?<=\\/)*([A-Z0-9]+-[0-9]+)");
    private static final Pattern prefixPattern = Pattern.compile("[A-Z0-9]+-[0-9]+:");

    private String newCommitMessage;


    public CommitPrefixCheckinHandler(CheckinProjectPanel panel) {
        this.panel = panel;

        MessageBusConnection connect = panel.getProject().getMessageBus().connect();
        connect.subscribe(BranchChangeListener.VCS_BRANCH_CHANGED, this);

        //Sets the new message on the new commit UI
        updateCommitMessage();
    }

    private void updateCommitMessage(){
        panel.setCommitMessage(getNewCommitMessage());
    }

    @Nullable
    @Override
    public RefreshableOnComponent getBeforeCheckinConfigurationPanel() {
        //Sets the new message on the old commit UI
        updateCommitMessage();
        return super.getBeforeCheckinConfigurationPanel();
    }

    private String getNewCommitMessage(){
        String branchName = extractBranchName();
        //log.warn("BranchName: " + branchName);

        Optional<String> jiraTicketName = getJiraTicketName(branchName);

        if (jiraTicketName.isPresent()){
            String newMessage = updatePrefix(jiraTicketName.get(), panel.getCommitMessage());
            //Sets the value for the new Panel UI
            return newMessage;
        }

        return  panel.getCommitMessage();
    }

    static Optional<String> getJiraTicketName(String branchName){
        Matcher matcher = branchNamePattern.matcher(branchName);
        if (matcher.find()){
            return Optional.ofNullable(matcher.group(1));
        }else{
            return Optional.empty();
        }
    }


    static String updatePrefix(String newPrefix, String currentMessage){
        if (currentMessage == null || currentMessage.trim().isEmpty()){
            return newPrefix + ": ";
        }

        //If there is already a commit message with a matching prefix only replace the prefix
        Matcher matcher = prefixPattern.matcher(currentMessage);
        if (matcher.find() && currentMessage.substring(0, matcher.start()).trim().isEmpty()){
            return currentMessage.replaceFirst(prefixPattern.pattern(), newPrefix + ":");
        }

        return newPrefix + ": " + currentMessage;
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
