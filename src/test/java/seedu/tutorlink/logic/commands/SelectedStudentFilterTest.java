package seedu.tutorlink.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.tutorlink.testutil.TypicalStudents.BENSON;
import static seedu.tutorlink.testutil.TypicalStudents.getTypicalTutorLink;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.model.Model;
import seedu.tutorlink.model.ModelManager;
import seedu.tutorlink.model.UserPrefs;
import seedu.tutorlink.model.student.NameContainsKeywordsPredicate;

/**
 * Regression tests for issue #57: the selected student must stay consistent with the shown list
 * across {@code student view}, {@code find} and {@code list}.
 */
public class SelectedStudentFilterTest {

    private final Model model = new ModelManager(getTypicalTutorLink(), new UserPrefs());

    @Test
    public void viewThenFindOtherStudentThenList_selectionClearedAndStaysCleared() throws Exception {
        new ViewStudentCommand(BENSON.getName()).execute(model);
        assertEquals(BENSON, model.getSelectedStudent().get());

        new FindCommand(new NameContainsKeywordsPredicate(List.of("Alice"))).execute(model);
        assertEquals(null, model.getSelectedStudent().get());

        new ListCommand().execute(model);
        assertEquals(null, model.getSelectedStudent().get());
    }

    @Test
    public void viewThenFindSameStudentThenList_selectionKept() throws Exception {
        new ViewStudentCommand(BENSON.getName()).execute(model);

        new FindCommand(new NameContainsKeywordsPredicate(List.of("Benson"))).execute(model);
        assertEquals(BENSON, model.getSelectedStudent().get());

        new ListCommand().execute(model);
        assertEquals(BENSON, model.getSelectedStudent().get());
    }
}
