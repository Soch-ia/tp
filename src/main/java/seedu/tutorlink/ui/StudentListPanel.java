package seedu.tutorlink.ui;

import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import seedu.tutorlink.commons.core.LogsCenter;
import seedu.tutorlink.model.student.Student;

/**
 * Panel containing the list of students, which highlights the student currently shown in detail.
 */
public class StudentListPanel extends UiPart<Region> {
    public static final String MESSAGE_FILTERED_COUNT = "Showing %1$d of %2$d \u00b7 type list to show all";

    private static final String FXML = "StudentListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(StudentListPanel.class);

    @FXML
    private ListView<Student> studentListView;
    @FXML
    private Label studentCount;

    /**
     * Creates a {@code StudentListPanel} showing {@code shownStudents}, which may be filtered from
     * {@code allStudents}, and highlighting the student held by {@code selectedStudent}.
     * The list is controlled only through commands, so it ignores mouse clicks and keyboard selection.
     */
    public StudentListPanel(ObservableList<Student> shownStudents, ObservableList<Student> allStudents,
            ObservableValue<Student> selectedStudent) {
        super(FXML);
        studentListView.setItems(shownStudents);
        studentListView.setCellFactory(listView -> new StudentListViewCell());
        studentListView.setFocusTraversable(false);
        // Rows are selected by commands only, so mouse clicks on the list are ignored; scrolling still works
        studentListView.addEventFilter(MouseEvent.ANY, event -> {
            if (!isOnScrollBar(event)) {
                event.consume();
            }
        });
        studentCount.textProperty().bind(Bindings.createStringBinding(() ->
                describeCount(shownStudents.size(), allStudents.size()), shownStudents, allStudents));
        selectedStudent.addListener((observable, oldStudent, newStudent) -> highlight(newStudent));
        // Changing the filter rebuilds the rows and drops the highlight, so restore it
        shownStudents.addListener((ListChangeListener<Student>) change -> highlight(selectedStudent.getValue()));
    }

    /**
     * Returns the count shown under the title, which says how to show all students again when filtered.
     */
    static String describeCount(int shownCount, int totalCount) {
        if (shownCount == totalCount) {
            return totalCount + (totalCount == 1 ? " student" : " students");
        }
        return String.format(MESSAGE_FILTERED_COUNT, shownCount, totalCount);
    }

    private static boolean isOnScrollBar(MouseEvent event) {
        for (Node node = event.getPickResult().getIntersectedNode(); node != null; node = node.getParent()) {
            if (node instanceof ScrollBar) {
                return true;
            }
        }
        return false;
    }

    private void highlight(Student student) {
        if (student == null || !studentListView.getItems().contains(student)) {
            studentListView.getSelectionModel().clearSelection();
            return;
        }
        studentListView.getSelectionModel().select(student);
        studentListView.scrollTo(student);
        // Adjust again after the list has laid out any newly added row, so the selected row is shown in full
        Platform.runLater(() -> scrollIntoView(studentListView.getItems().indexOf(student)));
    }

    /**
     * Scrolls the list by the smallest amount that shows the whole row at {@code index}.
     */
    private void scrollIntoView(int index) {
        if (index < 0) {
            return;
        }
        if (studentListView.lookup(".virtual-flow") instanceof VirtualFlow<?> flow) {
            flow.scrollTo(index);
        } else {
            studentListView.scrollTo(index);
        }
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Student} using a {@code StudentCard}.
     */
    class StudentListViewCell extends ListCell<Student> {
        @Override
        protected void updateItem(Student student, boolean empty) {
            super.updateItem(student, empty);

            if (empty || student == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new StudentCard(student, getIndex() + 1).getRoot());
            }
            // Keep each row within the list's width, so long names wrap instead of adding a horizontal scroll bar
            setPrefWidth(0);
        }
    }

}
