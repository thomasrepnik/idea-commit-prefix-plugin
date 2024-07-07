package ch.repnik.intellij;

import static ch.repnik.intellij.settings.TicketSystem.JIRA;

import ch.repnik.intellij.settings.PluginConfigService;
import ch.repnik.intellij.settings.Position;
import ch.repnik.intellij.settings.TicketSystem;
import com.intellij.dvcs.DvcsUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
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
import git4idea.repo.GitRepositoryChangeListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommitPrefixCheckinHandler extends CheckinHandler implements GitRepositoryChangeListener {

  private final Logger log = Logger.getInstance(getClass());
  private final CheckinProjectPanel panel;
  private static final Pattern jiraBranchNamePattern =
      Pattern.compile("(?<=\\/)*([A-Z0-9]+-[0-9]+)");
  private static final Pattern otherBranchNamePattern = Pattern.compile("(?<=\\/)*(\\d+)");
  private static final Pattern jiraPrefixPattern = Pattern.compile("[A-Z0-9]+-[0-9]+");
  private static final Pattern otherPrefixPattern = Pattern.compile("\\d+");

  public CommitPrefixCheckinHandler(CheckinProjectPanel panel) {
    this.panel = panel;

    MessageBusConnection connect = panel.getProject().getMessageBus().connect();
    connect.subscribe(GitRepository.GIT_REPO_CHANGE, this);

    // Sets the new message on the new commit UI
    updateCommitMessage();
  }

  private void updateCommitMessage() {
        ApplicationManager.getApplication().invokeLater(() -> {
            PsiDocumentManager psiInstance = PsiDocumentManager.getInstance(this.panel.getProject());
              if (psiInstance instanceof PsiDocumentManagerImpl) {
                if (!((PsiDocumentManagerImpl) psiInstance).isCommitInProgress()) {
                  panel.setCommitMessage(getNewCommitMessage());
                } else {
                    log.info("PsiDocumentManager reported commit in progress. Skipping Git Auto Prefix");
                }
              } else {
                log.info("PsiDocumentManager is not an instance of PsiDocumentManagerImpl. Skipping Git Auto Prefix");
              }
            });
  }

  @Nullable
  @Override
  public RefreshableOnComponent getBeforeCheckinConfigurationPanel() {
    // Sets the new message on the old commit UI
    updateCommitMessage();
    return super.getBeforeCheckinConfigurationPanel();
  }

  private String getNewCommitMessage() {
    String branchName = extractBranchName();
    // log.warn("BranchName: " + branchName);

    Optional<String> ticketName = getTicket(getTicketSystem(), branchName);

    if (ticketName.isPresent()) {
      // Sets the value for the new Panel UI
      return updatePrefix(ticketName.get(), panel.getCommitMessage(), getTicketSystem(), getWrapLeft(), getWrapRight(), getIssueKeyPosition());
    }

    return panel.getCommitMessage();
  }

  static Optional<String> getTicket(TicketSystem ticketSystem, String branchName) {
    Pattern branchNamePattern =
        ticketSystem == JIRA ? jiraBranchNamePattern : otherBranchNamePattern;
    Matcher matcher = branchNamePattern.matcher(branchName);
    if (matcher.find()){
      return Optional.ofNullable(matcher.group(1));
    }else{
      return Optional.empty();
    }
  }

  static String rTrim(String input) {
    int i = input.length() - 1;
    while (i >= 0 && Character.isWhitespace(input.charAt(i))) {
      i--;
    }
    return input.substring(0, i + 1);
  }

  static String lTrim(String input) {
    int i = 0;
    while (i <= input.length() - 1 && Character.isWhitespace(input.charAt(i))) {
      i++;
    }
    return input.substring(i, input.length());
  }

  static String updatePrefix(String newPrefix, String currentMessage, TicketSystem ticketSystem, String wrapLeft, String wrapRight, Position issueKeyPosition) {
    if (currentMessage == null || currentMessage.trim().isEmpty()) {
      return wrapLeft + newPrefix + wrapRight;
    }

    Pattern prefixPattern = ticketSystem == JIRA ? jiraPrefixPattern : otherPrefixPattern;

    return updatePrefix(
        newPrefix, currentMessage, wrapLeft, wrapRight, issueKeyPosition, prefixPattern);
  }

    private static String updatePrefix(String newPrefix, String currentMessage, String wrapLeft, String wrapRight,
        Position issueKeyPosition, Pattern prefixPattern) {
    // If there is already a commit message with a matching prefix only replace the prefix
    Matcher matcher = prefixPattern.matcher(currentMessage);
    Matcher foundLastMatch = null;

    if (issueKeyPosition == Position.END) {
      foundLastMatch = selectLastMatch(matcher, prefixPattern, currentMessage);
    }

        if (issueKeyPosition == Position.START && matcher.find() &&
            subString(currentMessage,0, matcher.start()).trim().equals(wrapLeft) &&
            (subString(currentMessage, matcher.end(), matcher.end() + wrapRight.length()).equals(
                wrapRight) ||
                subString(currentMessage, matcher.end(), matcher.end() + wrapRight.length()).equals(rTrim(
                    wrapRight)))
        ){
      String start = subString(currentMessage, 0, matcher.start());
      String end = subString(currentMessage, matcher.end() + wrapRight.length());

      return start + newPrefix + wrapRight + end;
        }else if (issueKeyPosition == Position.END && foundLastMatch.find() &&
            subString(currentMessage,foundLastMatch.end(), currentMessage.length()).trim().equals(
                wrapRight) &&
            (subString(currentMessage, foundLastMatch.start()- wrapLeft.length(), foundLastMatch.start()).equals(
                wrapLeft) ||
                subString(currentMessage, foundLastMatch.start()- wrapLeft.length(), foundLastMatch.start()).equals(lTrim(
                    wrapLeft)))){

      String start = subString(currentMessage, 0, foundLastMatch.start());
      String end = subString(currentMessage, foundLastMatch.end() + wrapRight.length());

      return start + newPrefix + wrapRight + end;

    }

    if (issueKeyPosition == Position.START) {
      return wrapLeft + newPrefix + wrapRight + currentMessage;
        }
        else{
      return currentMessage + wrapLeft + newPrefix + wrapRight;
    }
  }

  private static Matcher selectLastMatch(Matcher matcher, Pattern pattern, String input) {
    int foundMatches = 0;
    while (matcher.find()) {
      foundMatches++;
    }

    Matcher newMatcher = pattern.matcher(input);
    if (foundMatches > 1) {
      for (int i = 1; i < foundMatches; i++) {
        newMatcher.find();
      }

      return newMatcher;
    }

    return pattern.matcher(input);
  }

  static String subString(String string, int start) {
    if (string.length() < start) {
      return "";
    } else {
      return string.substring(start);
    }
  }

  static String subString(String string, int start, int end) {
    if (end < start) {
      throw new IllegalArgumentException("start must be smaller than end");
    } else if (string.length() < start || start == end) {
      return "";
    } else if (string.length() < end) {
      return string.substring(start);
    } else {
      return string.substring(start, end);
    }
  }

  TicketSystem getTicketSystem() {
    return this.panel.getProject().getService(PluginConfigService.class).getState().getTicketSystem();
  }

  Position getIssueKeyPosition() {
        return this.panel.getProject().getService(PluginConfigService.class).getState().getIssueKeyPosition();
  }

  String getWrapRight() {
    return this.panel.getProject().getService(PluginConfigService.class).getState().getWrapRight();
  }

  String getWrapLeft() {
    return this.panel.getProject().getService(PluginConfigService.class).getState().getWrapLeft();
  }

  private String extractBranchName() {
    Project project = panel.getProject();

    String branch = "";
    ProjectLevelVcsManager instance = ProjectLevelVcsManagerImpl.getInstance(project);
    if (instance.checkVcsIsActive("Git")) {
            GitRepository currentRepository = GitBranchUtil.guessWidgetRepository(project, DvcsUtil.getSelectedFile(project));
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
  public void repositoryChanged(@NotNull GitRepository repository) {
    updateCommitMessage();
  }
}
