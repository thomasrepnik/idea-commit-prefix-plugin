package ch.repnik.intellij;

import ch.repnik.intellij.settings.Position;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class CommitPrefixCheckinHandlerTest {

  @Test
  public void updatePrefix_noMatchPositionEnd_updatedCorrectly() {
    updateABC1234JiraPrefixTester()
        .withCurrentMessage("Testli")
        .withPluginSettings(": ", "", Position.END)
        .updatePrefix()
        .shouldHaveNewMessage("Testli: ABC-1234");
  }

  @Test
  public void updatePrefix_existingMessage_updatedCorrectly() {
    updateABC1234JiraPrefixTester()
        .withCurrentMessage("XYXY-837292: This is my text")
        .withPluginSettings("", ": ", Position.START)
        .updatePrefix()
        .shouldHaveNewMessage("ABC-1234: This is my text");
  }

  @Test
  public void updatePrefix_existingMessagePositionEnd_updatedCorrectly() {
    updateABC1234JiraPrefixTester()
        .withCurrentMessage("This is my text: XYXY-837292")
        .withPluginSettings(": ", "", Position.END)
        .updatePrefix()
        .shouldHaveNewMessage("This is my text: ABC-1234");
  }

  @Test
  public void updatePrefix_delimiterWithoutMessage_updatedCorrectly() {
    updateABC1234JiraPrefixTester()
        .withCurrentMessage("XYXY-837292:")
        .withPluginSettings("", ": ", Position.START)
        .updatePrefix()
        .shouldHaveNewMessage("ABC-1234: ");
  }

  @Test
  public void updatePrefix_delimiterWithoutMessagePositionEnd_updatedCorrectly() {
    updateABC1234JiraPrefixTester()
        .withCurrentMessage(": XYXY-837292")
        .withPluginSettings(": ", "", Position.END)
        .updatePrefix()
        .shouldHaveNewMessage(": ABC-1234");
  }

  @Test
  public void updatePrefix_wrongDelimiter_issueNotRecognized() {
    updateABC1234JiraPrefixTester()
        .withCurrentMessage("XYXY-837292: This is my text")
        .withPluginSettings("", " | ", Position.START)
        .updatePrefix()
        .shouldHaveNewMessage("ABC-1234 | XYXY-837292: This is my text");
  }

  @Test
  public void updatePrefix_wrongDelimiterPositionEnd_issueNotRecognized() {
    updateABC1234JiraPrefixTester()
        .withCurrentMessage("This is my text: XYXY-837292")
        .withPluginSettings(" | ", "", Position.END)
        .updatePrefix()
        .shouldHaveNewMessage("This is my text: XYXY-837292 | ABC-1234");
  }

  @Test
  public void updatePrefix_specialDelimiter_updatedCorrectly() {
    updateABC1234JiraPrefixTester()
        .withCurrentMessage(null)
        .withPluginSettings(" [](){}:_-/|,.", " [](){}:_-/|,.", Position.START)
        .updatePrefix()
        .shouldHaveNewMessage(" [](){}:_-/|,.ABC-1234 [](){}:_-/|,.");
  }

  @Test
  public void updatePrefix_specialDelimiterPositionEnd_updatedCorrectly() {
    updateABC1234JiraPrefixTester()
        .withCurrentMessage(null)
        .withPluginSettings(" [](){}:_-/|,.", " [](){}:_-/|,.", Position.END)
        .updatePrefix()
        .shouldHaveNewMessage(" [](){}:_-/|,.ABC-1234 [](){}:_-/|,.");
  }

  @Test
  public void updatePrefix_doubledPattern_updatedCorrectly() {
    updateABC1234JiraPrefixTester()
        .withCurrentMessage("XYXY-837292: XYZ-11 This is my text")
        .withPluginSettings("", ": ", Position.START)
        .updatePrefix()
        .shouldHaveNewMessage("ABC-1234: XYZ-11 This is my text");
  }

  @Test
  public void updatePrefix_doubledPatternPositionEnd_updatedCorrectly() {
    updateABC1234JiraPrefixTester()
        .withCurrentMessage("XYZ-11 This is my text: XYXY-837292")
        .withPluginSettings(": ", "", Position.END)
        .updatePrefix()
        .shouldHaveNewMessage("XYZ-11 This is my text: ABC-1234");
  }

  @Test
  public void updatePrefix_null_updatedCorrectly() {
    updateABC1234JiraPrefixTester()
        .withCurrentMessage(null)
        .withPluginSettings("", ": ", Position.START)
        .updatePrefix()
        .shouldHaveNewMessage("ABC-1234: ");
  }

  @Test
  public void updatePrefix_nullPositionEnd_updatedCorrectly() {
    updateABC1234JiraPrefixTester()
        .withCurrentMessage(null)
        .withPluginSettings(": ", "", Position.END)
        .updatePrefix()
        .shouldHaveNewMessage(": ABC-1234");
  }

  @Test
  public void updatePrefix_emptyMessage_updatedCorrectly() {
    updateABC1234JiraPrefixTester()
        .withCurrentMessage("")
        .withPluginSettings("", ": ", Position.START)
        .updatePrefix()
        .shouldHaveNewMessage("ABC-1234: ");
  }

  @Test
  public void updatePrefix_emptyMessagePositionEnd_updatedCorrectly() {
    updateABC1234JiraPrefixTester()
        .withCurrentMessage("")
        .withPluginSettings(": ", "", Position.END)
        .updatePrefix()
        .shouldHaveNewMessage(": ABC-1234");
  }

  @Test
  public void updatePrefix_blankMessage_updatedCorrectly() {
    updateABC1234JiraPrefixTester()
        .withCurrentMessage("       ")
        .withPluginSettings("", ": ", Position.START)
        .updatePrefix()
        .shouldHaveNewMessage("ABC-1234: ");
  }

  @Test
  public void updatePrefix_blankMessagePositionEnd_updatedCorrectly() {
    updateABC1234JiraPrefixTester()
        .withCurrentMessage("       ")
        .withPluginSettings(": ", "", Position.END)
        .updatePrefix()
        .shouldHaveNewMessage(": ABC-1234");
  }

  @Test
  public void updatePrefix_existingMessageWithBlanks_updatedCorrectly() {
    updateABC1234JiraPrefixTester()
        .withCurrentMessage("   XYXY-837292:  This is a Test     ")
        .withPluginSettings("", ": ", Position.START)
        .updatePrefix()
        .shouldHaveNewMessage("   ABC-1234:  This is a Test     ");
  }

  @Test
  public void updatePrefix_existingMessageWithBlanksPositionEnd_updatedCorrectly() {
    updateABC1234JiraPrefixTester()
        .withCurrentMessage("   This is a Test  : XYXY-837292     ")
        .withPluginSettings(": ", "", Position.END)
        .updatePrefix()
        .shouldHaveNewMessage("   This is a Test  : ABC-1234     ");
  }

  @Test
  public void updatePrefix_existingMessageWithPrefixInText_updatedCorrectly() {
    updateABC1234JiraPrefixTester()
        .withCurrentMessage("   According to issue XYXY-837292: this fix...     ")
        .withPluginSettings("", ": ", Position.START)
        .updatePrefix()
        .shouldHaveNewMessage("ABC-1234:    According to issue XYXY-837292: this fix...     ");
  }

  // TESTS WITH WRAP LEFT

  @Test
  public void updatePrefix_existingMessageWrapLeft_updatedCorrectly() {
    updateABC1234JiraPrefixTester()
        .withCurrentMessage("[XYXY-837292]: This is my text")
        .withPluginSettings("[", "]: ", Position.START)
        .updatePrefix()
        .shouldHaveNewMessage("[ABC-1234]: This is my text");
  }

  @Test
  public void updatePrefix_delimiterWithoutMessageWrapLeft_updatedCorrectly() {
    updateABC1234JiraPrefixTester()
        .withCurrentMessage("[XYXY-837292]:")
        .withPluginSettings("[", "]: ", Position.START)
        .updatePrefix()
        .shouldHaveNewMessage("[ABC-1234]: ");
  }

  @Test
  public void updatePrefix_wrongDelimiterWrapLeft_issueNotRecognized() {
    updateABC1234JiraPrefixTester()
        .withCurrentMessage("[XYXY-837292]: This is my text")
        .withPluginSettings("[", " | ", Position.START)
        .updatePrefix()
        .shouldHaveNewMessage("[ABC-1234 | [XYXY-837292]: This is my text");
  }

  @Test
  public void updatePrefix_specialDelimiterWrapLeft_updatedCorrectly() {
    updateABC1234JiraPrefixTester()
        .withCurrentMessage(null)
        .withPluginSettings(" [](){}:_-/|,.", " [](){}:_-/|,.", Position.START)
        .updatePrefix()
        .shouldHaveNewMessage(" [](){}:_-/|,.ABC-1234 [](){}:_-/|,.");
  }

  @Test
  public void updatePrefix_doubledPatternWrapLeft_updatedCorrectly() {
    updateABC1234JiraPrefixTester()
        .withCurrentMessage("[XYXY-837292]: XYZ-11 This is my text")
        .withPluginSettings("[", "]: ", Position.START)
        .updatePrefix()
        .shouldHaveNewMessage("[ABC-1234]: XYZ-11 This is my text");
  }

  @Test
  public void updatePrefix_nullWrapLeft_updatedCorrectly() {
    updateABC1234JiraPrefixTester()
        .withCurrentMessage(null)
        .withPluginSettings("[", "]: ", Position.START)
        .updatePrefix()
        .shouldHaveNewMessage("[ABC-1234]: ");
  }

  @Test
  public void updatePrefix_emptyMessageWrapLeft_updatedCorrectly() {
    updateABC1234JiraPrefixTester()
        .withCurrentMessage("")
        .withPluginSettings("[", "]: ", Position.START)
        .updatePrefix()
        .shouldHaveNewMessage("[ABC-1234]: ");
  }

  @Test
  public void updatePrefix_blankMessageWrapLeft_updatedCorrectly() {
    updateABC1234JiraPrefixTester()
        .withCurrentMessage("       ")
        .withPluginSettings("[", "]: ", Position.START)
        .updatePrefix()
        .shouldHaveNewMessage("[ABC-1234]: ");
  }

  @Test
  public void updatePrefix_existingMessageWithBlanksWrapLeft_updatedCorrectly() {
    updateABC1234JiraPrefixTester()
        .withCurrentMessage("   [XYXY-837292]:  This is a Test     ")
        .withPluginSettings("[", "]: ", Position.START)
        .updatePrefix()
        .shouldHaveNewMessage("   [ABC-1234]:  This is a Test     ");
  }

  @Test
  public void updatePrefix_existingMessageWithPrefixInTextWrapLeft_updatedCorrectly() {
    updateABC1234JiraPrefixTester()
        .withCurrentMessage("   According to issue XYXY-837292: this fix...     ")
        .withPluginSettings("[", "]: ", Position.START)
        .updatePrefix()
        .shouldHaveNewMessage("[ABC-1234]:    According to issue XYXY-837292: this fix...     ");
  }

  @Test
  public void updatePrefix_noMessageButWrappedInBlanks_updatedCorrectly() {
    updateABC1234JiraPrefixTester()
        .withCurrentMessage(null)
        .withPluginSettings("     ", "     ", Position.START)
        .updatePrefix()
        .shouldHaveNewMessage("     ABC-1234     ");
  }

  private static UpdatePrefixTester updateABC1234JiraPrefixTester() {
    return new UpdatePrefixTester().withNewPrefix("ABC-1234");
  }

  static class UpdatePrefixTester {

    private String currentMessage;
    private String newPrefix;
    private String wrapLeft;
    private String wrapRight;
    private Position issueKeyPosition;

    UpdatePrefixTester() {}

    UpdatePrefixTester withCurrentMessage(String currentMessage) {
      this.currentMessage = currentMessage;
      return this;
    }

    UpdatePrefixTester withNewPrefix(String newPrefix) {
      this.newPrefix = newPrefix;
      return this;
    }

    UpdatePrefixTester withPluginSettings(
        String wrapLeft, String wrapRight, Position issueKeyPosition) {
      this.wrapLeft = wrapLeft;
      this.wrapRight = wrapRight;
      this.issueKeyPosition = issueKeyPosition;
      return this;
    }

    UpdatePrefixAsserter updatePrefix() {
      var newMessage =
          CommitPrefixCheckinHandler.updatePrefix(
              newPrefix, currentMessage, wrapLeft, wrapRight, issueKeyPosition);
      return new UpdatePrefixAsserter(newMessage);
    }
  }

  static class UpdatePrefixAsserter {

    private final String actualMessage;

    UpdatePrefixAsserter(String actualMessage) {
      this.actualMessage = actualMessage;
    }

    void shouldHaveNewMessage(String expectedMessage) {
      assertThat(actualMessage, is(expectedMessage));
    }
  }

  @Test
  public void getJiraTicketName_withoutBranchType_retunsJiraTicket() {
    TicketNameTester.getTicketNameFromBranch("ABC-1234-app-not-working")
        .shouldHaveTicketName("ABC-1234");
  }

  @Test
  public void getJiraTicketName_reproduce() {
    TicketNameTester.getTicketNameFromBranch("feature/DATA-4214-ab-CEP3.0-Transition-polling")
        .shouldHaveTicketName("DATA-4214");
  }

  @Test
  public void getJiraTicketName_featureBranchType_retunsJiraTicket() {
    TicketNameTester.getTicketNameFromBranch("feature/ABC-1234-app-not-working")
        .shouldHaveTicketName("ABC-1234");
  }

  @Test
  public void getJiraTicketName_releaseBranchType_retunsJiraTicket() {
    TicketNameTester.getTicketNameFromBranch("release/ABC-1234-app-not-working")
        .shouldHaveTicketName("ABC-1234");
  }

  @Test
  public void getJiraTicketName_bugfixBranchType_retunsJiraTicket() {
    TicketNameTester.getTicketNameFromBranch("bugfix/ABC-1234-app-not-working")
        .shouldHaveTicketName("ABC-1234");
  }

  @Test
  public void getJiraTicketName_someOtherType_retunsJiraTicket() {
    TicketNameTester.getTicketNameFromBranch("someOtherType/ABC-1234-app-not-working")
        .shouldHaveTicketName("ABC-1234");
  }

  @Test
  public void getJiraTicketName_emptyType_retunsJiraTicket() {
    TicketNameTester.getTicketNameFromBranch("/ABC-1234-app-not-working")
        .shouldHaveTicketName("ABC-1234");
  }

  @Test
  public void getJiraTicketName_emptySuffix_retunsJiraTicket() {
    TicketNameTester.getTicketNameFromBranch("feature/ABC-1234").shouldHaveTicketName("ABC-1234");
  }

  static class TicketNameTester {

    static TicketNameAsserter getTicketNameFromBranch(String branchName) {
      var ticketName = CommitPrefixCheckinHandler.getJiraTicketName(branchName);
      return new TicketNameAsserter(ticketName);
    }
  }

  static class TicketNameAsserter {

    private final Optional<String> actualTicketName;

    TicketNameAsserter(Optional<String> actualTicketName) {
      this.actualTicketName = actualTicketName;
    }

    void shouldHaveTicketName(String expectedTicketName) {
      assertThat(actualTicketName.isPresent(), is(true));
      assertThat(actualTicketName.get(), is(expectedTicketName));
    }
  }
}
