package seedu.tutorlink.model;

import java.util.Optional;
import java.util.function.Predicate;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import seedu.tutorlink.commons.core.GuiSettings;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Student;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluates to true */
    Predicate<Student> PREDICATE_SHOW_ALL_STUDENTS = unused -> true;

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Replaces TutorLink data with the data in {@code tutorLink}.
     */
    void setTutorLink(ReadOnlyTutorLink tutorLink);

    /** Returns the TutorLink */
    ReadOnlyTutorLink getTutorLink();

    /**
     * Returns true if a student with the same identity as {@code student} exists in TutorLink.
     */
    boolean hasStudent(Student student);

    /**
     * Returns the student whose name matches {@code name}, ignoring case and extra whitespace,
     * or an empty {@code Optional} if there is no such student.
     */
    Optional<Student> findStudentByName(Name name);

    /**
     * Deletes the given student.
     * The student must exist in TutorLink.
     */
    void deleteStudent(Student target);

    /**
     * Adds the given student.
     * {@code student} must not already exist in TutorLink.
     */
    void addStudent(Student student);

    /**
     * Replaces the given student {@code target} with {@code editedStudent}.
     * {@code target} must exist in TutorLink.
     * The student identity of {@code editedStudent} must not be the same as another existing student in TutorLink.
     */
    void setStudent(Student target, Student editedStudent);

    /** Returns an unmodifiable view of the filtered student list */
    ObservableList<Student> getFilteredStudentList();

    /**
     * Returns the student currently shown in detail, or a property holding {@code null} if no student is selected.
     * The selection is cleared when the student is deleted or hidden by a filter, and follows the student when it
     * is edited.
     */
    ReadOnlyObjectProperty<Student> getSelectedStudent();

    /**
     * Shows {@code student} in detail. {@code student} may be {@code null} to clear the selection.
     */
    void setSelectedStudent(Student student);

    /**
     * Updates the filter of the filtered student list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredStudentList(Predicate<Student> predicate);
}
