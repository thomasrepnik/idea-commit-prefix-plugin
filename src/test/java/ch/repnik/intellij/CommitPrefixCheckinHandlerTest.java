package ch.repnik.intellij;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class CommitPrefixCheckinHandlerTest {

    @Test
    public void updatePrefix_existingMessage_updatedCorrectly() {
        String result = CommitPrefixCheckinHandler.updatePrefix("ABC-1234", "XYXY-837292: This is my text", "",  ": ");
        assertThat(result, is("ABC-1234: This is my text"));
    }

    @Test
    public void updatePrefix_delimiterWithoutMessage_updatedCorrectly() {
        String result = CommitPrefixCheckinHandler.updatePrefix("ABC-1234", "XYXY-837292:","", ": ");
        assertThat(result, is("ABC-1234: "));
    }

    @Test
    public void updatePrefix_wrongDelimiter_issueNotRecognized() {
        String result = CommitPrefixCheckinHandler.updatePrefix("ABC-1234", "XYXY-837292: This is my text","", " | ");
        assertThat(result, is("ABC-1234 | XYXY-837292: This is my text"));
    }

    @Test
    public void updatePrefix_specialDelimiter_updatedCorrectly() {
        String result = CommitPrefixCheckinHandler.updatePrefix("ABC-1234", null," [](){}:_-/|,.", " [](){}:_-/|,.");
        assertThat(result, is(" [](){}:_-/|,.ABC-1234 [](){}:_-/|,."));
    }

    @Test
    public void updatePrefix_doubledPattern_updatedCorrectly() {
        String result = CommitPrefixCheckinHandler.updatePrefix("ABC-1234", "XYXY-837292: XYZ-11 This is my text","", ": ");
        assertThat(result, is("ABC-1234: XYZ-11 This is my text"));
    }

    @Test
    public void updatePrefix_null_updatedCorrectly() {
        String result = CommitPrefixCheckinHandler.updatePrefix("ABC-1234", null,"", ": ");
        assertThat(result, is("ABC-1234: "));
    }

    @Test
    public void updatePrefix_emptyMessage_updatedCorrectly() {
        String result = CommitPrefixCheckinHandler.updatePrefix("ABC-1234", "","", ": ");
        assertThat(result, is("ABC-1234: "));
    }

    @Test
    public void updatePrefix_blankMessage_updatedCorrectly() {
        String result = CommitPrefixCheckinHandler.updatePrefix("ABC-1234", "       ","", ": ");
        assertThat(result, is("ABC-1234: "));
    }

    @Test
    public void updatePrefix_existingMessageWithBlanks_updatedCorrectly() {
        String result = CommitPrefixCheckinHandler.updatePrefix("ABC-1234", "   XYXY-837292:  This is a Test     ","", ": ");
        assertThat(result, is("   ABC-1234:  This is a Test     "));
    }

    @Test
    public void updatePrefix_existingMessageWithPrefixInText_updatedCorrectly() {
        String result = CommitPrefixCheckinHandler.updatePrefix("ABC-1234", "   According to issue XYXY-837292: this fix...     ","", ": ");
        assertThat(result, is("ABC-1234:    According to issue XYXY-837292: this fix...     "));
    }



    //TESTS WITH WRAP LEFT
    @Test
    public void updatePrefix_existingMessageWrapLeft_updatedCorrectly() {
        String result = CommitPrefixCheckinHandler.updatePrefix("ABC-1234", "[XYXY-837292]: This is my text", "[",  "]: ");
        assertThat(result, is("[ABC-1234]: This is my text"));
    }

    @Test
    public void updatePrefix_delimiterWithoutMessageWrapLeft_updatedCorrectly() {
        String result = CommitPrefixCheckinHandler.updatePrefix("ABC-1234", "[XYXY-837292]:","[", "]: ");
        assertThat(result, is("[ABC-1234]: "));
    }

    @Test
    public void updatePrefix_wrongDelimiterWrapLeft_issueNotRecognized() {
        String result = CommitPrefixCheckinHandler.updatePrefix("ABC-1234", "[XYXY-837292]: This is my text","[", " | ");
        assertThat(result, is("[ABC-1234 | [XYXY-837292]: This is my text"));
    }

    @Test
    public void updatePrefix_specialDelimiterWrapLeft_updatedCorrectly() {
        String result = CommitPrefixCheckinHandler.updatePrefix("ABC-1234", null," [](){}:_-/|,.", " [](){}:_-/|,.");
        assertThat(result, is(" [](){}:_-/|,.ABC-1234 [](){}:_-/|,."));
    }

    @Test
    public void updatePrefix_doubledPatternWrapLeft_updatedCorrectly() {
        String result = CommitPrefixCheckinHandler.updatePrefix("ABC-1234", "[XYXY-837292]: XYZ-11 This is my text","[", "]: ");
        assertThat(result, is("[ABC-1234]: XYZ-11 This is my text"));
    }

    @Test
    public void updatePrefix_nullWrapLeft_updatedCorrectly() {
        String result = CommitPrefixCheckinHandler.updatePrefix("ABC-1234", null,"[", "]: ");
        assertThat(result, is("[ABC-1234]: "));
    }

    @Test
    public void updatePrefix_emptyMessageWrapLeft_updatedCorrectly() {
        String result = CommitPrefixCheckinHandler.updatePrefix("ABC-1234", "","[", "]: ");
        assertThat(result, is("[ABC-1234]: "));
    }

    @Test
    public void updatePrefix_blankMessageWrapLeft_updatedCorrectly() {
        String result = CommitPrefixCheckinHandler.updatePrefix("ABC-1234", "       ","[", "]: ");
        assertThat(result, is("[ABC-1234]: "));
    }

    @Test
    public void updatePrefix_existingMessageWithBlanksWrapLeft_updatedCorrectly() {
        String result = CommitPrefixCheckinHandler.updatePrefix("ABC-1234", "   [XYXY-837292]:  This is a Test     ","[", "]: ");
        assertThat(result, is("   [ABC-1234]:  This is a Test     "));
    }

    @Test
    public void updatePrefix_existingMessageWithPrefixInTextWrapLeft_updatedCorrectly() {
        String result = CommitPrefixCheckinHandler.updatePrefix("ABC-1234", "   According to issue XYXY-837292: this fix...     ","[", "]: ");
        assertThat(result, is("[ABC-1234]:    According to issue XYXY-837292: this fix...     "));
    }

    @Test
    public void updatePrefix_noMessageButWrappedInBlanks_updatedCorrectly() {
        String result = CommitPrefixCheckinHandler.updatePrefix("ABC-1234", null,"     ", "     ");
        assertThat(result, is("     ABC-1234     "));
    }

    @Test
    public void getJiraTicketName_withoutBranchType_retunsJiraTicket() {
        Optional<String> result = CommitPrefixCheckinHandler.getJiraTicketName("ABC-1234-app-not-working");

        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), is("ABC-1234"));
    }

    @Test
    public void getJiraTicketName_reproduce() {
        Optional<String> result = CommitPrefixCheckinHandler.getJiraTicketName("feature/DATA-4214-ab-CEP3.0-Transition-polling");

        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), is("DATA-4214"));
    }

    @Test
    public void getJiraTicketName_featureBranchType_retunsJiraTicket() {
        Optional<String> result = CommitPrefixCheckinHandler.getJiraTicketName("feature/ABC-1234-app-not-working");
        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), is("ABC-1234"));
    }

    @Test
    public void getJiraTicketName_releaseBranchType_retunsJiraTicket() {
        Optional<String> result = CommitPrefixCheckinHandler.getJiraTicketName("release/ABC-1234-app-not-working");
        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), is("ABC-1234"));
    }

    @Test
    public void getJiraTicketName_bugfixBranchType_retunsJiraTicket() {
        Optional<String> result = CommitPrefixCheckinHandler.getJiraTicketName("bugfix/ABC-1234-app-not-working");
        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), is("ABC-1234"));
    }

    @Test
    public void getJiraTicketName_someOtherType_retunsJiraTicket() {
        Optional<String> result = CommitPrefixCheckinHandler.getJiraTicketName("someOtherType/ABC-1234-app-not-working");
        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), is("ABC-1234"));
    }

    @Test
    public void getJiraTicketName_emptyType_retunsJiraTicket() {
        Optional<String> result = CommitPrefixCheckinHandler.getJiraTicketName("/ABC-1234-app-not-working");
        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), is("ABC-1234"));
    }

    @Test
    public void getJiraTicketName_emptySuffix_retunsJiraTicket() {
        Optional<String> result = CommitPrefixCheckinHandler.getJiraTicketName("feature/ABC-1234");
        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), is("ABC-1234"));
    }

}
