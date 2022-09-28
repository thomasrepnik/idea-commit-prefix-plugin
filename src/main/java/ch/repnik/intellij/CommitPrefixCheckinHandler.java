package ch.repnik.intellij;

import ch.repnik.intellij.settings.PluginSettings;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.BranchChangeListener;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.impl.ProjectLevelVcsManagerImpl;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.impl.PsiDocumentManagerImpl;
import com.intellij.util.messages.MessageBusConnection;
import git4idea.GitLocalBranch;
import git4idea.branch.GitBranchUtil;
import git4idea.repo.GitRepository;
import git4idea.status.GitRefreshListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommitPrefixCheckinHandler extends CheckinHandler implements BranchChangeListener, GitRefreshListener {

    private final Logger log = Logger.getInstance(getClass());
    private final CheckinProjectPanel panel;
    private static final Pattern branchNamePattern = Pattern.compile("(?<=\\/)*([A-Z0-9]+-[0-9]+)");
    private static final Pattern prefixPattern = Pattern.compile("[A-Z0-9]+-[0-9]+");

    public CommitPrefixCheckinHandler(CheckinProjectPanel panel) {
        this.panel = panel;

        MessageBusConnection connect = panel.getProject().getMessageBus().connect();
        connect.subscribe(BranchChangeListener.VCS_BRANCH_CHANGED, this);
        connect.subscribe(GitRefreshListener.TOPIC, this);

        //Sets the new message on the new commit UI
        updateCommitMessage();
    }

    private void updateCommitMessage(){
        ApplicationManager.getApplication().invokeLater(() -> {
            PsiDocumentManager psiInstance = PsiDocumentManager.getInstance(this.panel.getProject());
            if (psiInstance instanceof PsiDocumentManagerImpl){
                if (!((PsiDocumentManagerImpl) psiInstance).isCommitInProgress()){
                    panel.setCommitMessage(getNewCommitMessage());
                }else{
                    log.info("PsiDocumentManager reported commit in progress. Skipping Git Auto Prefix");
                }
            }else{
                log.info("PsiDocumentManager is not an instance of PsiDocumentManagerImpl. Skipping Git Auto Prefix");
            }
        });
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
            //Sets the value for the new Panel UI
            return updatePrefix(jiraTicketName.get(), panel.getCommitMessage(), getWrapLeft(), getWrapRight());
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

    static String rTrim(String input){
        int i = input.length()-1;
        while (i >= 0 && Character.isWhitespace(input.charAt(i))) {
            i--;
        }
        return input.substring(0,i+1);
    }

    static String updatePrefix(String newPrefix, String currentMessage, String wrapLeft, String wrapRight){
        if (currentMessage == null || currentMessage.trim().isEmpty()){
            return wrapLeft + newPrefix + wrapRight;
        }

        //If there is already a commit message with a matching prefix only replace the prefix
        Matcher matcher = prefixPattern.matcher(currentMessage);
        if (matcher.find() &&
                subString(currentMessage,0, matcher.start()).trim().equals(wrapLeft) &&
                (subString(currentMessage, matcher.end(), matcher.end() + wrapRight.length()).equals(wrapRight) ||
                        subString(currentMessage, matcher.end(), matcher.end() + wrapRight.length()).equals(rTrim(wrapRight)))
        ){
            String start = subString(currentMessage, 0, matcher.start());
            String end = subString(currentMessage, matcher.end() + wrapRight.length());

            return start + newPrefix + wrapRight + end;
        }

        return wrapLeft + newPrefix + wrapRight + currentMessage;
    }

    static String subString(String string, int start){
        if (string.length() < start){
            return "";
        }else{
            return string.substring(start);
        }
    }

    static String subString(String string, int start, int end){
        if (end < start){
            throw new IllegalArgumentException("start must be smaller than end");
        } else if (string.length() < start || start == end){
            return "";
        } else if (string.length() < end){
            return string.substring(start);
        } else {
            return string.substring(start, end);
        }
    }



    String getWrapRight() {
        return PluginSettings.getInstance().getWrapRight();
    }

    String getWrapLeft() {
        return PluginSettings.getInstance().getWrapLeft();
    }

    private String extractBranchName() {
        Project project = panel.getProject();

        String branch = "";
        ProjectLevelVcsManager instance = ProjectLevelVcsManagerImpl.getInstance(project);
        if (instance.checkVcsIsActive("Git")) {
            GitRepository currentRepository = GitBranchUtil.getCurrentRepository(project);
            if (currentRepository != null) {
                GitLocalBranch currentBranch = currentRepository.getCurrentBranch();

                if (currentBranch != null) {
                    // Branch name  matches Ticket Name
                    branch = currentBranch.getName().trim();
                }
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

    //Detects repository updates made in the terminal
    @Override
    public void repositoryUpdated(@NotNull GitRepository repository) {
        updateCommitMessage();
    }
}
