package seedu.tutorlink.model;

import static java.util.Objects.requireNonNull;
import static seedu.tutorlink.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.tutorlink.commons.core.GuiSettings;
import seedu.tutorlink.commons.core.LogsCenter;
import seedu.tutorlink.model.student.Name;
import seedu.tutorlink.model.student.Student;

/**
 * Represents the in-memory model of TutorLink data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final TutorLink tutorLink;
    private final UserPrefs userPrefs;
    private final FilteredList<Student> filteredStudents;
    private final SimpleObjectProperty<Student> selectedStudent = new SimpleObjectProperty<>();

    /**
     * Initializes a ModelManager with the given tutorLink and userPrefs.
     */
    public ModelManager(ReadOnlyTutorLink tutorLink, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(tutorLink, userPrefs);

        logger.fine("Initializing with TutorLink: " + tutorLink + " and user prefs " + userPrefs);

        this.tutorLink = new TutorLink(tutorLink);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredStudents = new FilteredList<>(this.tutorLink.getStudentList());
    }

    public ModelManager() {
        this(new TutorLink(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    //=========== TutorLink ================================================================================

    @Override
    public void setTutorLink(ReadOnlyTutorLink tutorLink) {
        this.tutorLink.resetData(tutorLink);
        selectedStudent.set(null);
    }

    @Override
    public ReadOnlyTutorLink getTutorLink() {
        return tutorLink;
    }

    @Override
    public boolean hasStudent(Student student) {
        requireNonNull(student);
        return tutorLink.hasStudent(student);
    }

    @Override
    public Optional<Student> findStudentByName(Name name) {
        requireNonNull(name);
        return tutorLink.getStudentList().stream()
                .filter(student -> student.getName().isSameName(name))
                .findFirst();
    }

    @Override
    public void deleteStudent(Student target) {
        tutorLink.removeStudent(target);
        if (target.equals(selectedStudent.get())) {
            selectedStudent.set(null);
        }
    }

    @Override
    public void addStudent(Student student) {
        tutorLink.addStudent(student);
        updateFilteredStudentList(PREDICATE_SHOW_ALL_STUDENTS);
    }

    @Override
    public void setStudent(Student target, Student editedStudent) {
        requireAllNonNull(target, editedStudent);

        tutorLink.setStudent(target, editedStudent);
        if (target.equals(selectedStudent.get())) {
            selectedStudent.set(editedStudent);
        }
    }

    //=========== Selected Student ===========================================================================

    @Override
    public ReadOnlyObjectProperty<Student> getSelectedStudent() {
        return selectedStudent;
    }

    @Override
    public void setSelectedStudent(Student student) {
        selectedStudent.set(student);
    }

    //=========== Filtered Student List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Student} backed by the internal list of
     * {@code tutorLink}
     */
    @Override
    public ObservableList<Student> getFilteredStudentList() {
        return filteredStudents;
    }

    @Override
    public void updateFilteredStudentList(Predicate<Student> predicate) {
        requireNonNull(predicate);
        filteredStudents.setPredicate(predicate);
        // Do not keep showing a student the filter has hidden from the list
        if (selectedStudent.get() != null && !filteredStudents.contains(selectedStudent.get())) {
            selectedStudent.set(null);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager otherModelManager)) {
            return false;
        }

        return tutorLink.equals(otherModelManager.tutorLink)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredStudents.equals(otherModelManager.filteredStudents);
    }

}
