package ch.repnik.intellij;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class CommitPrefixCheckinHandlerTest {

    @Test
    public void updatePrefix_existingMessage_updatedCorrectly() {
        String result = CommitPrefixCheckinHandler.updatePrefix("ABC-1234", "XYXY-837292:This is my text");
        assertThat(result, is("ABC-1234:This is my text"));
    }

    @Test
    public void updatePrefix_doubledPattern_updatedCorrectly() {
        String result = CommitPrefixCheckinHandler.updatePrefix("ABC-1234", "XYXY-837292: XYZ-11 This is my text");
        assertThat(result, is("ABC-1234: XYZ-11 This is my text"));
    }

    @Test
    public void updatePrefix_null_updatedCorrectly() {
        String result = CommitPrefixCheckinHandler.updatePrefix("ABC-1234", null);
        assertThat(result, is("ABC-1234: "));
    }

    @Test
    public void updatePrefix_emptyMessage_updatedCorrectly() {
        String result = CommitPrefixCheckinHandler.updatePrefix("ABC-1234", "");
        assertThat(result, is("ABC-1234: "));
    }

    @Test
    public void updatePrefix_blankMessage_updatedCorrectly() {
        String result = CommitPrefixCheckinHandler.updatePrefix("ABC-1234", "       ");
        assertThat(result, is("ABC-1234: "));
    }

    @Test
    public void updatePrefix_existingMessageWithBlanks_updatedCorrectly() {
        String result = CommitPrefixCheckinHandler.updatePrefix("ABC-1234", "   XYXY-837292:  This is a Test     ");
        assertThat(result, is("   ABC-1234:  This is a Test     "));
    }

    @Test
    public void updatePrefix_existingMessageWithPrefixInText_updatedCorrectly() {
        String result = CommitPrefixCheckinHandler.updatePrefix("ABC-1234", "   According to issue XYXY-837292: this fix...     ");
        assertThat(result, is("ABC-1234:    According to issue XYXY-837292: this fix...     "));
    }

}