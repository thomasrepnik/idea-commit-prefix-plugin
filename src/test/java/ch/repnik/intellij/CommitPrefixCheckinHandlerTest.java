package ch.repnik.intellij;

import ch.repnik.intellij.settings.Position;
import ch.repnik.intellij.settings.TicketSystem;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.With;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static ch.repnik.intellij.settings.TicketSystem.JIRA;
import static ch.repnik.intellij.settings.TicketSystem.OTHER;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class CommitPrefixCheckinHandlerTest {

  static Stream<Arguments> updatePrefix_noMatch_updatedCorrectly() {
    UpdatePrefixTester startTemplate = new UpdatePrefixTester().withWrapLeft("").withWrapRight(": ").withIssueKeyPosition(Position.START);
    UpdatePrefixTester endTemplate = new UpdatePrefixTester().withWrapLeft(": ").withWrapRight("").withIssueKeyPosition(Position.END);
    return Stream.of(
            Arguments.of(endTemplate.withTicketSystem(JIRA).withCurrentMessage("Testli").withNewPrefix("ABC-1234"), "Testli: ABC-1234"),
            Arguments.of(endTemplate.withTicketSystem(JIRA).withCurrentMessage("Testli").withNewPrefix("123-4567"), "Testli: 123-4567"),
            Arguments.of(endTemplate.withTicketSystem(OTHER).withCurrentMessage("Testli").withNewPrefix("5678"), "Testli: 5678"),
            Arguments.of(startTemplate.withTicketSystem(JIRA).withCurrentMessage("Testli").withNewPrefix("ABC-1234"), "ABC-1234: Testli"),
            Arguments.of(startTemplate.withTicketSystem(JIRA).withCurrentMessage("Testli").withNewPrefix("123-4567"), "123-4567: Testli"),
            Arguments.of(startTemplate.withTicketSystem(OTHER).withCurrentMessage("Testli").withNewPrefix("5678"), "5678: Testli")
    );
  }

  @ParameterizedTest
  @MethodSource
  public void updatePrefix_noMatch_updatedCorrectly(UpdatePrefixTester tester, String expectedMessage) {
    tester.updatePrefix().doAssertion(expectedMessage);
  }

  static Stream<Arguments> updatePrefix_existingMessage_updatedCorrectly() {
    UpdatePrefixTester startTemplate = new UpdatePrefixTester().withWrapLeft("").withWrapRight(": ").withIssueKeyPosition(Position.START);
    UpdatePrefixTester endTemplate = new UpdatePrefixTester().withWrapLeft(": ").withWrapRight("").withIssueKeyPosition(Position.END);
    return Stream.of(
            Arguments.of(startTemplate.withTicketSystem(JIRA).withCurrentMessage("XYXY-837292: This is my text").withNewPrefix("ABC-1234"), "ABC-1234: This is my text"),
            Arguments.of(endTemplate.withTicketSystem(JIRA).withCurrentMessage("This is my text: XYXY-837292").withNewPrefix("ABC-1234"), "This is my text: ABC-1234"),
            Arguments.of(startTemplate.withTicketSystem(OTHER).withCurrentMessage("837292: This is my text").withNewPrefix("5678"), "5678: This is my text"),
            Arguments.of(endTemplate.withTicketSystem(OTHER).withCurrentMessage("This is my text: 837292").withNewPrefix("5678"), "This is my text: 5678")
    );
  }

  @ParameterizedTest
  @MethodSource
  public void updatePrefix_existingMessage_updatedCorrectly(UpdatePrefixTester tester, String expectedMessage) {
    tester.updatePrefix().doAssertion(expectedMessage);
  }

  static Stream<Arguments> updatePrefix_delimiterWithoutMessage_updatedCorrectly() {
    UpdatePrefixTester startTemplate = new UpdatePrefixTester().withWrapLeft("").withWrapRight(": ").withIssueKeyPosition(Position.START);
    UpdatePrefixTester endTemplate = new UpdatePrefixTester().withWrapLeft(": ").withWrapRight("").withIssueKeyPosition(Position.END);
    return Stream.of(
            Arguments.of(startTemplate.withTicketSystem(JIRA).withCurrentMessage("XYXY-837292:").withNewPrefix("ABC-1234"), "ABC-1234: "),
            Arguments.of(endTemplate.withTicketSystem(JIRA).withCurrentMessage(": XYXY-837292").withNewPrefix("ABC-1234"), ": ABC-1234"),
            Arguments.of(startTemplate.withTicketSystem(OTHER).withCurrentMessage("837292: ").withNewPrefix("5678"), "5678: "),
            Arguments.of(endTemplate.withTicketSystem(OTHER).withCurrentMessage(": 837292").withNewPrefix("5678"), ": 5678")
    );
  }

  @ParameterizedTest
  @MethodSource
  public void updatePrefix_delimiterWithoutMessage_updatedCorrectly(UpdatePrefixTester tester, String expectedMessage) {
    tester.updatePrefix().doAssertion(expectedMessage);
  }

  static Stream<Arguments> updatePrefix_wrongDelimiter_issueNotRecognized() {
    UpdatePrefixTester startTemplate = new UpdatePrefixTester().withWrapLeft("").withWrapRight(" | ").withIssueKeyPosition(Position.START);
    UpdatePrefixTester endTemplate = new UpdatePrefixTester().withWrapLeft(" | ").withWrapRight("").withIssueKeyPosition(Position.END);

    return Stream.of(
            Arguments.of(startTemplate.withTicketSystem(JIRA).withCurrentMessage("XYXY-837292: This is my text").withNewPrefix("ABC-1234"), "ABC-1234 | XYXY-837292: This is my text"),
            Arguments.of(endTemplate.withTicketSystem(JIRA).withCurrentMessage("This is my text: XYXY-837292").withNewPrefix("ABC-1234"), "This is my text: XYXY-837292 | ABC-1234"),
            Arguments.of(startTemplate.withTicketSystem(OTHER).withCurrentMessage("837292: This is my text").withNewPrefix("5678"), "5678 | 837292: This is my text"),
            Arguments.of(endTemplate.withTicketSystem(OTHER).withCurrentMessage("This is my text: 837292").withNewPrefix("5678"), "This is my text: 837292 | 5678")
    );
  }

  @ParameterizedTest
  @MethodSource
  public void updatePrefix_wrongDelimiter_issueNotRecognized(UpdatePrefixTester tester, String expectedMessage) {
    tester.updatePrefix().doAssertion(expectedMessage);
  }

  static Stream<Arguments> updatePrefix_specialDelimiter_updatedCorrectly() {
    UpdatePrefixTester startTemplate = new UpdatePrefixTester().withWrapLeft(" [](){}:_-/|,.").withWrapRight(" [](){}:_-/|,.").withIssueKeyPosition(Position.START);
    UpdatePrefixTester endTemplate = new UpdatePrefixTester().withWrapLeft(" [](){}:_-/|,.").withWrapRight(" [](){}:_-/|,.").withIssueKeyPosition(Position.END);

    return Stream.of(
            Arguments.of(startTemplate.withTicketSystem(JIRA).withCurrentMessage(null).withNewPrefix("ABC-1234"), " [](){}:_-/|,.ABC-1234 [](){}:_-/|,."),
            Arguments.of(endTemplate.withTicketSystem(JIRA).withCurrentMessage(null).withNewPrefix("ABC-1234"), " [](){}:_-/|,.ABC-1234 [](){}:_-/|,."),
            Arguments.of(startTemplate.withTicketSystem(OTHER).withCurrentMessage(null).withNewPrefix("5678"), " [](){}:_-/|,.5678 [](){}:_-/|,."),
            Arguments.of(endTemplate.withTicketSystem(OTHER).withCurrentMessage(null).withNewPrefix("5678"), " [](){}:_-/|,.5678 [](){}:_-/|,.")
    );
  }

  @ParameterizedTest
  @MethodSource
  public void updatePrefix_specialDelimiter_updatedCorrectly(UpdatePrefixTester tester, String expectedMessage) {
    tester.updatePrefix().doAssertion(expectedMessage);
  }

  static Stream<Arguments> updatePrefix_doubledPattern_updatedCorrectly() {
    UpdatePrefixTester startTemplate = new UpdatePrefixTester().withWrapLeft("").withWrapRight(": ").withIssueKeyPosition(Position.START);
    UpdatePrefixTester endTemplate = new UpdatePrefixTester().withWrapLeft(": ").withWrapRight("").withIssueKeyPosition(Position.END);

    return Stream.of(
            Arguments.of(startTemplate.withTicketSystem(JIRA).withCurrentMessage("XYXY-837292: XYZ-11 This is my text").withNewPrefix("ABC-1234"), "ABC-1234: XYZ-11 This is my text"),
            Arguments.of(endTemplate.withTicketSystem(JIRA).withCurrentMessage("XYZ-11 This is my text: XYXY-837292").withNewPrefix("ABC-1234"), "XYZ-11 This is my text: ABC-1234"),
            Arguments.of(startTemplate.withTicketSystem(OTHER).withCurrentMessage("1234: 9999 This is my text").withNewPrefix("5678"), "5678: 9999 This is my text"),
            Arguments.of(endTemplate.withTicketSystem(OTHER).withCurrentMessage("9999 This is my text: 1234").withNewPrefix("5678"), "9999 This is my text: 5678")

    );
  }


  @ParameterizedTest
  @MethodSource
  public void updatePrefix_doubledPattern_updatedCorrectly(UpdatePrefixTester tester, String expectedMessage) {
    tester.updatePrefix().doAssertion(expectedMessage);
  }


  static Stream<Arguments> updatePrefix_null_updatedCorrectly() {
    UpdatePrefixTester startTemplate = new UpdatePrefixTester().withWrapLeft("").withWrapRight(": ").withIssueKeyPosition(Position.START);
    UpdatePrefixTester endTemplate = new UpdatePrefixTester().withWrapLeft(": ").withWrapRight("").withIssueKeyPosition(Position.END);

    return Stream.of(
            Arguments.of(startTemplate.withTicketSystem(JIRA).withCurrentMessage(null).withNewPrefix("ABC-1234"), "ABC-1234: "),
            Arguments.of(endTemplate.withTicketSystem(JIRA).withCurrentMessage(null).withNewPrefix("ABC-1234"), ": ABC-1234"),
            Arguments.of(startTemplate.withTicketSystem(OTHER).withCurrentMessage(null).withNewPrefix("5678"), "5678: "),
            Arguments.of(endTemplate.withTicketSystem(OTHER).withCurrentMessage(null).withNewPrefix("5678"), ": 5678")

    );
  }

  @ParameterizedTest
  @MethodSource
  public void updatePrefix_null_updatedCorrectly(UpdatePrefixTester tester, String expectedMessage) {
    tester.updatePrefix().doAssertion(expectedMessage);
  }

  static Stream<Arguments> updatePrefix_emptyMessage_updatedCorrectly() {
    UpdatePrefixTester startTemplate = new UpdatePrefixTester().withWrapLeft("").withWrapRight(": ").withIssueKeyPosition(Position.START);
    UpdatePrefixTester endTemplate = new UpdatePrefixTester().withWrapLeft(": ").withWrapRight("").withIssueKeyPosition(Position.END);

    return Stream.of(
            Arguments.of(startTemplate.withTicketSystem(JIRA).withCurrentMessage("").withNewPrefix("ABC-1234"), "ABC-1234: "),
            Arguments.of(endTemplate.withTicketSystem(JIRA).withCurrentMessage("").withNewPrefix("ABC-1234"), ": ABC-1234"),
            Arguments.of(startTemplate.withTicketSystem(OTHER).withCurrentMessage("").withNewPrefix("5678"), "5678: "),
            Arguments.of(endTemplate.withTicketSystem(OTHER).withCurrentMessage("").withNewPrefix("5678"), ": 5678")

    );
  }

  @ParameterizedTest
  @MethodSource
  public void updatePrefix_emptyMessage_updatedCorrectly(UpdatePrefixTester tester, String expectedMessage) {
    tester.updatePrefix().doAssertion(expectedMessage);
  }

  static Stream<Arguments> updatePrefix_blankMessage_updatedCorrectly() {
    UpdatePrefixTester startTemplate = new UpdatePrefixTester().withWrapLeft("").withWrapRight(": ").withIssueKeyPosition(Position.START);
    UpdatePrefixTester endTemplate = new UpdatePrefixTester().withWrapLeft(": ").withWrapRight("").withIssueKeyPosition(Position.END);

    return Stream.of(
            Arguments.of(startTemplate.withTicketSystem(JIRA).withCurrentMessage("       ").withNewPrefix("ABC-1234"), "ABC-1234: "),
            Arguments.of(endTemplate.withTicketSystem(JIRA).withCurrentMessage("       ").withNewPrefix("ABC-1234"), ": ABC-1234"),
            Arguments.of(startTemplate.withTicketSystem(OTHER).withCurrentMessage("       ").withNewPrefix("5678"), "5678: "),
            Arguments.of(endTemplate.withTicketSystem(OTHER).withCurrentMessage("       ").withNewPrefix("5678"), ": 5678")

    );
  }

  @ParameterizedTest
  @MethodSource
  public void updatePrefix_blankMessage_updatedCorrectly(UpdatePrefixTester tester, String expectedMessage) {
    tester.updatePrefix().doAssertion(expectedMessage);
  }


  static Stream<Arguments> updatePrefix_existingMessageWithBlanks_updatedCorrectly() {
    UpdatePrefixTester startTemplate = new UpdatePrefixTester().withWrapLeft("").withWrapRight(": ").withIssueKeyPosition(Position.START);
    UpdatePrefixTester endTemplate = new UpdatePrefixTester().withWrapLeft(": ").withWrapRight("").withIssueKeyPosition(Position.END);

    return Stream.of(
            Arguments.of(startTemplate.withTicketSystem(JIRA).withCurrentMessage("   XYXY-837292:  This is a Test     ").withNewPrefix("ABC-1234"), "   ABC-1234:  This is a Test     "),
            Arguments.of(endTemplate.withTicketSystem(JIRA).withCurrentMessage("   This is a Test  : XYXY-837292     ").withNewPrefix("ABC-1234"), "   This is a Test  : ABC-1234     "),
            Arguments.of(startTemplate.withTicketSystem(OTHER).withCurrentMessage("   1111:  This is a Test     ").withNewPrefix("5678"), "   5678:  This is a Test     "),
            Arguments.of(endTemplate.withTicketSystem(OTHER).withCurrentMessage("   This is a Test  : 1111     ").withNewPrefix("5678"), "   This is a Test  : 5678     ")

    );
  }

  @ParameterizedTest
  @MethodSource
  public void updatePrefix_existingMessageWithBlanks_updatedCorrectly(UpdatePrefixTester tester, String expectedMessage) {
    tester.updatePrefix().doAssertion(expectedMessage);
  }


  static Stream<Arguments> updatePrefix_existingMessageWithPrefixInText_updatedCorrectly() {
    UpdatePrefixTester startTemplate = new UpdatePrefixTester().withWrapLeft("").withWrapRight(": ").withIssueKeyPosition(Position.START);
    UpdatePrefixTester endTemplate = new UpdatePrefixTester().withWrapLeft(": ").withWrapRight("").withIssueKeyPosition(Position.END);

    return Stream.of(
            Arguments.of(startTemplate.withTicketSystem(JIRA).withCurrentMessage("   According to issue XYXY-837292: this fix...     ").withNewPrefix("ABC-1234"), "ABC-1234:    According to issue XYXY-837292: this fix...     "),
            Arguments.of(endTemplate.withTicketSystem(JIRA).withCurrentMessage("   According to issue : XYXY-837292 this fix...     ").withNewPrefix("ABC-1234"), "   According to issue : XYXY-837292 this fix...     : ABC-1234"),
            Arguments.of(startTemplate.withTicketSystem(OTHER).withCurrentMessage("   According to issue 1111: this fix...     ").withNewPrefix("5678"), "5678:    According to issue 1111: this fix...     "),
            Arguments.of(endTemplate.withTicketSystem(OTHER).withCurrentMessage("   According to issue : 1111 this fix...     ").withNewPrefix("5678"), "   According to issue : 1111 this fix...     : 5678")

    );
  }

  @ParameterizedTest
  @MethodSource
  public void updatePrefix_existingMessageWithPrefixInText_updatedCorrectly(UpdatePrefixTester tester, String expectedMessage) {
    tester.updatePrefix().doAssertion(expectedMessage);
  }



  static Stream<Arguments> updatePrefix_noMessageButWrappedInBlanks_updatedCorrectly() {
    UpdatePrefixTester startTemplate = new UpdatePrefixTester().withWrapLeft("     ").withWrapRight("     ").withIssueKeyPosition(Position.START);
    UpdatePrefixTester endTemplate = new UpdatePrefixTester().withWrapLeft("     ").withWrapRight("     ").withIssueKeyPosition(Position.END);

    return Stream.of(
            Arguments.of(startTemplate.withTicketSystem(JIRA).withCurrentMessage(null).withNewPrefix("ABC-1234"), "     ABC-1234     "),
            Arguments.of(endTemplate.withTicketSystem(JIRA).withCurrentMessage(null).withNewPrefix("ABC-1234"), "     ABC-1234     "),
            Arguments.of(startTemplate.withTicketSystem(OTHER).withCurrentMessage(null).withNewPrefix("5678"), "     5678     "),
            Arguments.of(endTemplate.withTicketSystem(OTHER).withCurrentMessage(null).withNewPrefix("5678"), "     5678     ")

    );
  }


  @ParameterizedTest
  @MethodSource
  public void updatePrefix_noMessageButWrappedInBlanks_updatedCorrectly(UpdatePrefixTester tester, String expectedMessage) {
    tester.updatePrefix().doAssertion(expectedMessage);
  }


  static Stream<Arguments> updateAzureBoardsPrefix_digitsInCommitMessage_onlyReplacedIfMatchingPrefixSettings() {
    UpdatePrefixTester startTemplate = new UpdatePrefixTester().withTicketSystem(OTHER).withWrapLeft("").withWrapRight(": ").withIssueKeyPosition(Position.START);
    UpdatePrefixTester endTemplate = new UpdatePrefixTester().withTicketSystem(OTHER).withWrapLeft(": ").withWrapRight("").withIssueKeyPosition(Position.END);

    return Stream.of(
            Arguments.of(startTemplate.withCurrentMessage("Fixed 4 bugs in 3 classes").withNewPrefix("5678"), "5678: Fixed 4 bugs in 3 classes"),
            Arguments.of(startTemplate.withCurrentMessage("4 bugs in 3 classes fixed").withNewPrefix("5678"), "5678: 4 bugs in 3 classes fixed"),
            Arguments.of(endTemplate.withCurrentMessage("Fixed 4 bugs in 3 classes").withNewPrefix("5678"), "Fixed 4 bugs in 3 classes: 5678"),
            Arguments.of(endTemplate.withCurrentMessage("4 bugs in 3 classes fixed").withNewPrefix("5678"), "4 bugs in 3 classes fixed: 5678")
    );
  }

  @ParameterizedTest
  @MethodSource
  void updateAzureBoardsPrefix_digitsInCommitMessage_onlyReplacedIfMatchingPrefixSettings(UpdatePrefixTester tester, String expectedMessage) {
    tester.updatePrefix().doAssertion(expectedMessage);
  }


  static Stream<Arguments> updatePrefix_existingPrefixFromDifferentTicketSystem_newPrefixAdded() {
    UpdatePrefixTester startJiraTemplate = new UpdatePrefixTester().withTicketSystem(JIRA).withWrapLeft("").withWrapRight(": ").withIssueKeyPosition(Position.START);
    UpdatePrefixTester endJiraTemplate = new UpdatePrefixTester().withTicketSystem(JIRA).withWrapLeft(": ").withWrapRight("").withIssueKeyPosition(Position.END);
    UpdatePrefixTester startOtherTemplate = new UpdatePrefixTester().withTicketSystem(OTHER).withWrapLeft("").withWrapRight(": ").withIssueKeyPosition(Position.START);
    UpdatePrefixTester endOtherTemplate = new UpdatePrefixTester().withTicketSystem(OTHER).withWrapLeft(": ").withWrapRight("").withIssueKeyPosition(Position.END);

    return Stream.of(
            Arguments.of(startJiraTemplate.withCurrentMessage("5678: Some fix").withNewPrefix("ABC-1234"), "ABC-1234: 5678: Some fix"),
            Arguments.of(startOtherTemplate.withCurrentMessage("ABC-1234: Some fix").withNewPrefix("5678"), "5678: ABC-1234: Some fix"),
            Arguments.of(endJiraTemplate.withCurrentMessage("Some fix: 5678").withNewPrefix("ABC-1234"), "Some fix: 5678: ABC-1234"),
            Arguments.of(endOtherTemplate.withCurrentMessage("Some fix: ABC-1234").withNewPrefix("5678"), "Some fix: ABC-1234: 5678")
    );
  }

  @ParameterizedTest
  @MethodSource
  void updatePrefix_existingPrefixFromDifferentTicketSystem_newPrefixAdded(UpdatePrefixTester tester, String expectedMessage) {
    tester.updatePrefix().doAssertion(expectedMessage);
  }




  @Test
  public void getTicketName_withoutBranchType_retunsJiraTicket() {
    new TicketNameTester()
        .withTicketSystem(JIRA)
        .getTicketFromBranch("ABC-1234-app-not-working")
        .shouldHaveTicketName("ABC-1234");
  }

  @Test
  public void getTicketName_reproduce() {
    new TicketNameTester()
        .withTicketSystem(JIRA)
        .getTicketFromBranch("feature/DATA-4214-ab-CEP3.0-Transition-polling")
        .shouldHaveTicketName("DATA-4214");
  }

  @Test
  public void getTicketName_featureBranchType_retunsJiraTicket() {
    new TicketNameTester()
        .withTicketSystem(JIRA)
        .getTicketFromBranch("feature/ABC-1234-app-not-working")
        .shouldHaveTicketName("ABC-1234");
  }

  @Test
  public void getTicketName_releaseBranchType_retunsJiraTicket() {
    new TicketNameTester()
        .withTicketSystem(JIRA)
        .getTicketFromBranch("release/ABC-1234-app-not-working")
        .shouldHaveTicketName("ABC-1234");
  }

  @Test
  public void getTicketName_bugfixBranchType_retunsJiraTicket() {
    new TicketNameTester()
        .withTicketSystem(JIRA)
        .getTicketFromBranch("bugfix/ABC-1234-app-not-working")
        .shouldHaveTicketName("ABC-1234");
  }

  @Test
  public void getTicketName_someOtherType_retunsJiraTicket() {
    new TicketNameTester()
        .withTicketSystem(JIRA)
        .getTicketFromBranch("someOtherType/ABC-1234-app-not-working")
        .shouldHaveTicketName("ABC-1234");
  }

  @Test
  public void getTicketName_emptyType_retunsJiraTicket() {
    new TicketNameTester()
        .withTicketSystem(JIRA)
        .getTicketFromBranch("/ABC-1234-app-not-working")
        .shouldHaveTicketName("ABC-1234");
  }

  @Test
  public void getTicketName_emptySuffix_retunsJiraTicket() {
    new TicketNameTester()
        .withTicketSystem(JIRA)
        .getTicketFromBranch("feature/ABC-1234")
        .shouldHaveTicketName("ABC-1234");
  }

  @Test
  void getTicketName_branchNameStartingWithDigit_retunsJiraTicket() {
    new TicketNameTester()
        .withTicketSystem(JIRA)
        .getTicketFromBranch("ABC-1234-3-small-fixes")
        .shouldHaveTicketName("ABC-1234");
  }

  @Test
  void getTicketName_allDigitProjectKeyAndBranchNameStartingWithDigit_retunsJiraTicket() {
    new TicketNameTester()
        .withTicketSystem(JIRA)
        .getTicketFromBranch("123-1234-3-small-fixes")
        .shouldHaveTicketName("123-1234");
  }

  @Test
  void getTicketName_withoutBranchType_returnsAzureBoardsWorkItem() {
    new TicketNameTester()
        .withTicketSystem(OTHER)
        .getTicketFromBranch("1234-app-not-working")
        .shouldHaveTicketName("1234");
  }

  @Test
  void getTicketName_featureBranchType_returnsAzureBoardsWorkItem() {
    new TicketNameTester()
        .withTicketSystem(OTHER)
        .getTicketFromBranch("feature/1234-app-not-working")
        .shouldHaveTicketName("1234");
  }

  @Test
  void getTicketName_emptyType_returnsAzureBoardsWorkItem() {
    new TicketNameTester()
        .withTicketSystem(OTHER)
        .getTicketFromBranch("/1234-app-not-working")
        .shouldHaveTicketName("1234");
  }

  @Test
  void getTicketName_emptySuffix_returnsAzureBoardsWorkItem() {
    new TicketNameTester()
        .withTicketSystem(OTHER)
        .getTicketFromBranch("feature/1234")
        .shouldHaveTicketName("1234");
  }

  @Test
  void getTicketName_branchNameStartingWithDigit_doesNotReturnAzureBoardsWorkItem() {
    new TicketNameTester()
        .withTicketSystem(JIRA)
        .getTicketFromBranch("1234-3-small-fixes")
        .shouldHaveTicketName("1234-3");
    new TicketNameTester()
        .withTicketSystem(OTHER)
        .getTicketFromBranch("1234-3-small-fixes")
        .shouldHaveTicketName("1234");
  }

  @With
  @AllArgsConstructor
  @NoArgsConstructor
  static class UpdatePrefixTester {

    private String currentMessage;
    private String newPrefix;
    private TicketSystem ticketSystem;
    private String wrapLeft;
    private String wrapRight;
    private Position issueKeyPosition;


    UpdatePrefixAsserter updatePrefix() {
      String newMessage =
              CommitPrefixCheckinHandler.updatePrefix(
                      newPrefix, currentMessage, ticketSystem, wrapLeft, wrapRight, issueKeyPosition);
      return new UpdatePrefixAsserter(newMessage);
    }

    @Override
    public String toString() {
      return String.format("%s, %s", ticketSystem.toString(), issueKeyPosition.toString());
    }
  }

  @RequiredArgsConstructor
  static class UpdatePrefixAsserter {

    private final String actualMessage;

    void doAssertion(String expectedValue) {
      assertThat(actualMessage, is(expectedValue));
    }
  }


  static class TicketNameTester {

    private TicketSystem ticketSystem;

    TicketNameTester withTicketSystem(TicketSystem ticketSystem) {
      this.ticketSystem = ticketSystem;
      return this;
    }

    TicketNameAsserter getTicketFromBranch(String branchName) {
      Optional<String> ticket = CommitPrefixCheckinHandler.getTicket(ticketSystem, branchName);
      return new TicketNameAsserter(ticket);
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
