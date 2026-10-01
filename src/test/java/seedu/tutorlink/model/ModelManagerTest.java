package seedu.tutorlink.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.tutorlink.model.Model.PREDICATE_SHOW_ALL_STUDENTS;
import static seedu.tutorlink.testutil.Assert.assertThrows;
import static seedu.tutorlink.testutil.TypicalStudents.ALICE;
import static seedu.tutorlink.testutil.TypicalStudents.BENSON;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.tutorlink.commons.core.GuiSettings;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.NameContainsKeywordsPredicate;
import seedu.tutorlink.model.student.Student;
import seedu.tutorlink.testutil.StudentBuilder;
import seedu.tutorlink.testutil.TutorLinkBuilder;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new TutorLink(), new TutorLink(modelManager.getTutorLink()));
    }

    @Test
    public void constructor_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager = new ModelManager(new TutorLink(), userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setGuiSettings(new GuiSettings(5, 6, 7, 8));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void hasStudent_nullStudent_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasStudent(null));
    }

    @Test
    public void hasStudent_studentNotInTutorLink_returnsFalse() {
        assertFalse(modelManager.hasStudent(ALICE));
    }

    @Test
    public void hasStudent_studentInTutorLink_returnsTrue() {
        modelManager.addStudent(ALICE);
        assertTrue(modelManager.hasStudent(ALICE));
    }

    @Test
    public void findStudentByName_nullName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.findStudentByName(null));
    }

    @Test
    public void findStudentByName_studentNotInTutorLink_returnsEmpty() {
        modelManager.addStudent(ALICE);
        assertEquals(Optional.empty(), modelManager.findStudentByName(BENSON.getName()));
    }

    @Test
    public void findStudentByName_nameDiffersInCaseAndSpacing_returnsStudent() {
        modelManager.addStudent(ALICE);
        Name differentlyTypedName = new Name(ALICE.getName().fullName.toUpperCase().replace(" ", "  ") + " ");
        assertEquals(Optional.of(ALICE), modelManager.findStudentByName(differentlyTypedName));
    }

    @Test
    public void getFilteredStudentList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredStudentList().remove(0));
    }

    @Test
    public void equals() {
        TutorLink tutorLink = new TutorLinkBuilder().withStudent(ALICE).withStudent(BENSON).build();
        TutorLink differentTutorLink = new TutorLink();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(tutorLink, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(tutorLink, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different tutorLink -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentTutorLink, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredStudentList(new NameContainsKeywordsPredicate(List.of(keywords)));
        assertFalse(modelManager.equals(new ModelManager(tutorLink, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredStudentList(PREDICATE_SHOW_ALL_STUDENTS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        assertFalse(modelManager.equals(new ModelManager(tutorLink, differentUserPrefs)));
    }

    @Test
    public void getSelectedStudent_nothingSelected_holdsNull() {
        assertEquals(null, modelManager.getSelectedStudent().get());
    }

    @Test
    public void setStudent_selectedStudentEdited_selectionFollowsEdit() {
        modelManager.addStudent(ALICE);
        modelManager.setSelectedStudent(ALICE);
        Student editedAlice = new StudentBuilder(ALICE).withSubjects("Physics").build();

        modelManager.setStudent(ALICE, editedAlice);

        assertEquals(editedAlice, modelManager.getSelectedStudent().get());
    }

    @Test
    public void deleteStudent_selectedStudentDeleted_selectionCleared() {
        modelManager.addStudent(ALICE);
        modelManager.addStudent(BENSON);
        modelManager.setSelectedStudent(ALICE);

        modelManager.deleteStudent(BENSON);
        assertEquals(ALICE, modelManager.getSelectedStudent().get());

        modelManager.deleteStudent(ALICE);
        assertEquals(null, modelManager.getSelectedStudent().get());
    }

    @Test
    public void setTutorLink_selectionCleared() {
        modelManager.addStudent(ALICE);
        modelManager.setSelectedStudent(ALICE);

        modelManager.setTutorLink(new TutorLink());

        assertEquals(null, modelManager.getSelectedStudent().get());
    }

    @Test
    public void updateFilteredStudentList_selectedStudentHidden_selectionCleared() {
        modelManager.addStudent(ALICE);
        modelManager.addStudent(BENSON);
        modelManager.setSelectedStudent(ALICE);

        modelManager.updateFilteredStudentList(new NameContainsKeywordsPredicate(List.of("Benson")));

        assertEquals(null, modelManager.getSelectedStudent().get());
    }

    @Test
    public void updateFilteredStudentList_selectedStudentStillShown_selectionKept() {
        modelManager.addStudent(ALICE);
        modelManager.addStudent(BENSON);
        modelManager.setSelectedStudent(ALICE);

        modelManager.updateFilteredStudentList(new NameContainsKeywordsPredicate(List.of("Alice")));
        assertEquals(ALICE, modelManager.getSelectedStudent().get());

        modelManager.updateFilteredStudentList(PREDICATE_SHOW_ALL_STUDENTS);
        assertEquals(ALICE, modelManager.getSelectedStudent().get());
    }
}
