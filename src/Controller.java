import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;


public class Controller  implements Initializable {

    // Buttons
    @FXML private Button signUp;
    @FXML private Button login;
    @FXML private Button mgCourse;
    @FXML private Button uploadUnits;

    @FXML private TableView<Exam> ExamTable;
    @FXML private TableColumn<Exam, String> colregNumber1;
   @FXML private TableColumn<Exam, String> colname;
   @FXML private TableColumn<Exam, Integer> colCat1;
    @FXML private TableColumn<Exam, Integer> colCat2;
   @FXML private TableColumn<Exam, Integer> colMain;



    @FXML private TextField regNo;
    @FXML private TextField password;

    @FXML private Label home1, registerLabel;

    @FXML private Label regNumber, name, courseLabel, name2, phone, idNo, email;

    @FXML private Label courseCount, learnerCount, courseNo;

    
    @FXML private TextField title, id;


    @FXML private TextField UnitName, UnitCode;
    @FXML private ComboBox<Integer> yearCombo;
    @FXML private ComboBox<Integer> semCombo;
    @FXML private ComboBox<String> unitCode; 

    
    public void signUp() {
        String reg = regNo.getText();
        String pass = password.getText();

        if (reg == null || reg.isBlank() || pass == null || pass.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in both fields.");
            return;
        }

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "INSERT INTO user (regNo, password, role) VALUES (?, ?, 'USER')";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, reg);
            stmt.setString(2, pass);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "User registration successful");
                regNo.clear();
                password.clear();

                Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("Application.css").toExternalForm());

                Stage stage = (Stage) signUp.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failure", "Registration failed.");
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            showAlert(Alert.AlertType.ERROR, "Duplicate User", "User already exists.");
        } catch (SQLException | IOException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
            e.printStackTrace();
        }
    }

    // ------------------- Registration (ADMIN) -------------------
    public void insertAdmin(String reg, String pass) {
        if (reg == null || reg.isBlank() || pass == null || pass.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Admin regNo and password required.");
            return;
        }

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "INSERT INTO user (regNo, password, role) VALUES (?, ?, 'ADMIN')";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, reg);
            stmt.setString(2, pass);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Admin account created successfully");
            } else {
                showAlert(Alert.AlertType.ERROR, "Failure", "Admin creation failed.");
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            showAlert(Alert.AlertType.ERROR, "Duplicate Admin", "Admin with this regNo already exists.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
            e.printStackTrace();
        }
    }

   
    @FXML
    public void login() {
        String reg = regNo.getText();
        String pass = password.getText();

        if (reg == null || reg.isBlank() || pass == null || pass.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in both fields.");
            return;
        }

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT * FROM user WHERE regNo = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, reg);
            stmt.setString(2, pass);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome (" + role + ")");

                Parent root;
                Scene scene;

                if ("ADMIN".equalsIgnoreCase(role)) {
                    root = FXMLLoader.load(getClass().getResource("Admin.fxml"));
                    scene = new Scene(root);
                } else {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("UserDashboard.fxml"));
                    root = loader.load();
                    Controller dashboardController = loader.getController();
                    dashboardController.loadLearnerDetails(reg);
                    scene = new Scene(root);
                }

                scene.getStylesheets().add(getClass().getResource("Application.css").toExternalForm());
                Stage stage = (Stage) login.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid credentials. Please try again.");
            }

        } catch (SQLException | IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
            e.printStackTrace();
        }
    }

    public void register(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("register2.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("Application.css").toExternalForm());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void manageCourse(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Course.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("Application.css").toExternalForm());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void fee(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("fee.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("Application.css").toExternalForm());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void exam(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("exam.fxml")); // If this is intentional; else change to exam.fxml
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("Application.css").toExternalForm());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void staff() {
 
    }

    public void viewLearner(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("viewLearner.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("Application.css").toExternalForm());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

   
    public void loadLearnerDetails(String regNo) {
        try (Connection conn = DbConnection.getConnection()) {
            String sql = "SELECT name, regNo, course, email, phoneNo, idNo FROM user WHERE regNo = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, regNo);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                name.setText(rs.getString("name"));
                regNumber.setText(rs.getString("regNo"));
                courseLabel.setText(rs.getString("course"));
                name2.setText(rs.getString("name"));
                phone.setText(rs.getString("phoneNo"));
                email.setText(rs.getString("email"));
                idNo.setText(rs.getString("idNo"));
            } else {
                showAlert(Alert.AlertType.ERROR, "Not Found", "No learner found with regNo: " + regNo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    private int getLearnerCount() {
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM user");
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getCourseCount() {
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM course");
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void handleCourse(ActionEvent event) {
        FileChooser filechooser = new FileChooser();
        filechooser.setTitle("Choose a CSV File");
        filechooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        File file = filechooser.showOpenDialog(null);

        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file));
                 Connection conn = DbConnection.getConnection()) {

                String line;
                boolean firstLine = true; 
                int rowsInserted = 0;

                while ((line = reader.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false;
                        continue;
                    }

                    String[] data = line.split(",");
                    if (data.length >= 2) {
                        String sql = "INSERT INTO course(courseID, name) VALUES(?, ?)";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setString(1, data[0].trim()); 
                        stmt.setString(2, data[1].trim()); 
                        rowsInserted += stmt.executeUpdate();
                    } else {
                        System.out.println("Skipped invalid row: " + line);
                    }
                }

                if (rowsInserted > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Import Success", rowsInserted + " courses imported successfully!");
                } else {
                    showAlert(Alert.AlertType.WARNING, "No Data", "No valid rows were imported.");
                }

            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Import Failed", "CSV course file not uploaded: " + e.getMessage());
            }
        }
    }

    @FXML
    public void registerCourse(ActionEvent event) {
        String courseTitle = title.getText();
        String courseID = id.getText();

        if (courseTitle == null || courseTitle.isBlank() || courseID == null || courseID.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Course ID and Title are required.");
            return;
        }

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "INSERT INTO course(CourseID, name) VALUES(?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, courseID);
            stmt.setString(2, courseTitle);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                showAlert(Alert.AlertType.CONFIRMATION, "Course Added Successfully", "Course Added Successfully");
                id.clear();
                title.clear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failure", "Course not added");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    public void csvUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file));
                 Connection conn = DbConnection.getConnection()) {

                String line;
                boolean firstLine = true; 
                int rowsInserted = 0;

                while ((line = reader.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false;
                        continue;
                    }

                    String[] data = line.split(",");
                    if (data.length >= 5) {
                        // Expected order: unit_name, unit_code, courseId, year, semester
                        String sql = "INSERT INTO course_units(unit_name, unit_code, courseID, year, semester) VALUES (?, ?, ?, ?, ?)";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setString(1, data[0].trim());                     
                        stmt.setString(2, data[1].trim());                      
                        stmt.setString(3, data[2].trim());                   
                        stmt.setInt(4, Integer.parseInt(data[3].trim()));       
                        stmt.setInt(5, Integer.parseInt(data[4].trim()));     
                        rowsInserted += stmt.executeUpdate();
                    } else {
                        System.out.println("Skipped invalid row: " + line);
                    }
                }

                showAlert(Alert.AlertType.INFORMATION, "Import Success", rowsInserted + " unit rows imported successfully!");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Import Failed", "Error: " + e.getMessage());
            }
        }
    }

   
    @FXML
    public void addUnits(ActionEvent event) {
        String unitName1 = UnitName.getText();
        String unitCode1 = UnitCode.getText();
        Integer year = yearCombo != null ? yearCombo.getValue() : null;
        Integer semester = semCombo != null ? semCombo.getValue() : null;   
        String courseId = unitCode != null ? unitCode.getValue() : null;    

        if (unitName1 == null || unitName1.isBlank()
                || unitCode1 == null || unitCode1.isBlank()
                || year == null || semester == null
                || courseId == null || courseId.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "All fields must be filled.");
            return;
        }

        try (Connection conn = DbConnection.getConnection()) {
            String sql = "INSERT INTO course_units(unit_name, unit_code, courseID, year, semester) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, unitName1);
            stmt.setString(2, unitCode1);
            stmt.setString(3, courseId); 
            stmt.setInt(4, year);
            stmt.setInt(5, semester);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                showAlert(Alert.AlertType.CONFIRMATION, "Course Registration Success", "Registered successfully");
                UnitName.clear();
                UnitCode.clear();
                if (yearCombo != null) yearCombo.getSelectionModel().clearSelection();
                if (semCombo != null) semCombo.getSelectionModel().clearSelection();
                if (unitCode != null) unitCode.getSelectionModel().clearSelection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

 
   

    private void loadCourseIds() {
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT courseId FROM course");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                unitCode.getItems().add(rs.getString("courseId")); // assumes VARCHAR courseId in course table
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

  
    @FXML
    public void mgCourse(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Course.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("Application.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("Application.css").toExternalForm());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void uploadUnits() {
      
        csvUpload();
    }





public void loadCourses() throws SQLException {
    try (Connection conn = DbConnection.getConnection()) {
        PreparedStatement stmt = conn.prepareStatement("SELECT courseID FROM course");
        ResultSet rs = stmt.executeQuery();

        uploadRe.getItems().clear();
        while (rs.next()) {
            uploadRe.getItems().add(rs.getString("courseID")); 
        }
    }
}

   

@FXML private ComboBox<String> uploadRe;     
@FXML private ComboBox<String> unitRe;       
@FXML private ComboBox<String> semesterRe;  
@FXML private ComboBox<String> yearRe;       

private void loadCourses(Connection conn) throws SQLException {
    if (uploadRe == null) {
        System.out.println("uploadRe ComboBox not injected");
        return;
    }
    PreparedStatement stmt = conn.prepareStatement(
        "SELECT DISTINCT courseID, course_name FROM course_with_units"
    );
    ResultSet rs = stmt.executeQuery();

    uploadRe.getItems().clear();
    while (rs.next()) {
    String courseId = rs.getString("courseID");
    String courseName = rs.getString("course_name");
    uploadRe.getItems().add(courseId + " - " + courseName);
}

if (!uploadRe.getItems().isEmpty()) {
    uploadRe.getSelectionModel().select(0);
}

}

@FXML
private void loadUnitsForCourse(Connection conn, String courseId) throws SQLException {
    PreparedStatement stmt2 = conn.prepareStatement(
        "SELECT unit_name, semester, year FROM course_with_units WHERE courseID = ?"
    );
    stmt2.setString(1, courseId);

    ResultSet rs2 = stmt2.executeQuery();

    unitRe.getItems().clear();
    semesterRe.getItems().clear();
    yearRe.getItems().clear();

    while (rs2.next()) {
        unitRe.getItems().add(rs2.getString("unit_name"));
        semesterRe.getItems().add(rs2.getString("semester"));
        yearRe.getItems().add(rs2.getString("year"));
    }
}
  @FXML
public void fetchData() {
    try (Connection conn = DbConnection.getConnection()) {
        // Populate course/year/semester combos only once
        if (uploadRe != null && uploadRe.getItems().isEmpty()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT courseID, course_name, year, semester FROM course_with_units"
            );
            ResultSet rs = stmt.executeQuery();

            Set<String> courses = new HashSet<>();
            Set<String> semesters = new HashSet<>();
            Set<String> years = new HashSet<>();

            uploadRe.getItems().clear();
            semesterRe.getItems().clear();
            yearRe.getItems().clear();

            while (rs.next()) {
                String course = rs.getString("courseID") + " - " + rs.getString("course_name");
                String sem = rs.getString("semester");
                String yr = rs.getString("year");

                if (courses.add(course)) uploadRe.getItems().add(course);
                if (semesters.add(sem)) semesterRe.getItems().add(sem);
                if (years.add(yr)) yearRe.getItems().add(yr);
            }

            uploadRe.setPromptText("Select a course");
            semesterRe.setPromptText("Select semester");
            yearRe.setPromptText("Select year");
        }

        // Populate units and learners
        if (uploadRe != null && semesterRe != null && yearRe != null) {
            String selectedCourse = uploadRe.getSelectionModel().getSelectedItem();
            String selectedSemester = semesterRe.getSelectionModel().getSelectedItem();
            String selectedYear = yearRe.getSelectionModel().getSelectedItem();

            if (selectedCourse != null && selectedSemester != null && selectedYear != null) {
                String selectedCourseId = selectedCourse.split(" - ")[0];

                PreparedStatement stmt2 = conn.prepareStatement(
                    "SELECT unit_name FROM course_with_units WHERE courseID = ? AND semester = ? AND year = ?"
                );
                stmt2.setString(1, selectedCourseId);
                stmt2.setString(2, selectedSemester);
                stmt2.setString(3, selectedYear);

                ResultSet rs2 = stmt2.executeQuery();
                unitRe.getItems().clear();
                while (rs2.next()) {
                    unitRe.getItems().add(rs2.getString("unit_name"));
                }

PreparedStatement stmt3 = conn.prepareStatement(
    "SELECT DISTINCT u.regNo, u.name, e.cat1, e.cat2, e.main " +
    "FROM user u " +
    "JOIN course_with_units cu ON u.courseId = cu.courseID AND u.year_of_study = cu.year " +
    "LEFT JOIN exam_results e ON u.regNo = e.regNo " +
    "WHERE cu.courseID = ? AND cu.year = ? AND cu.semester = ?"
);
stmt3.setString(1, selectedCourseId);
stmt3.setString(2, selectedYear);
stmt3.setString(3, selectedSemester);

ResultSet rs3 = stmt3.executeQuery();
ObservableList<Exam> exams = FXCollections.observableArrayList();

while (rs3.next()) {
    String regNo = rs3.getString("regNo");
    String name = rs3.getString("name");

    // Use getObject so null stays null
    Integer cat1 = (Integer) rs3.getObject("cat1");
    Integer cat2 = (Integer) rs3.getObject("cat2");
    Integer main = (Integer) rs3.getObject("main");

    // If null, leave blank until user types marks
    exams.add(new Exam(
        regNo,
        name,
        cat1 != null ? cat1 : null,
        cat2 != null ? cat2 : null,
        main != null ? main : null
    ));
}

ExamTable.setItems(exams);
ExamTable.setEditable(true);
            }}
    } catch (SQLException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
    }
}

    public void uploadResults() {
    try (Connection conn = DbConnection.getConnection()) {
        for (Exam exam : ExamTable.getItems()) {
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO exam_results (regNo, cat1, cat2, main) VALUES (?, ?, ?, ?)"
            );
            stmt.setString(1, exam.getRegNo());
            stmt.setInt(2, exam.getCat1());
            stmt.setInt(3, exam.getCat2());
            stmt.setInt(4, exam.getMain());
            stmt.executeUpdate();
        }
        showAlert(Alert.AlertType.INFORMATION, "Success", "Marks inserted successfully!");
    } catch (SQLException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
    }
}

    public void home(MouseEvent event) throws IOException{
        Parent root=FXMLLoader.load(getClass().getResource("Admin.fxml"));
        Scene scene= new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene.getStylesheets().add(getClass().getResource("Application.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

   
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

@Override
public void initialize(URL location, ResourceBundle resources) {
    // --- Populate year combo with fixed values 1–4 ---
    if (yearCombo != null) {
        yearCombo.getItems().addAll(1, 2, 3, 4);
        yearCombo.setPromptText("Select Year");
    }

    // --- Populate semester combo with fixed values 1–2 ---
    if (semCombo != null) {
        semCombo.getItems().addAll(1, 2);
        semCombo.setPromptText("Select Semester");
    }

    // --- Populate unitCode combo with course IDs from DB ---
    if (unitCode != null) {
        loadCourseIds();
        unitCode.setPromptText("Select Course ID");
    }

    // --- Set learner and course counts ---
    if (learnerCount != null) {
        learnerCount.setText(String.valueOf(getLearnerCount()));
    }
    if (courseCount != null) {
        courseCount.setText(String.valueOf(getCourseCount()));
    }

    // --- Bind ExamTable columns ---
    if (colregNumber1 != null) {
        colregNumber1.setCellValueFactory(new PropertyValueFactory<>("regNo"));
    }
    if (colname != null) {
        colname.setCellValueFactory(new PropertyValueFactory<>("name"));
    }

    // --- Bind CAT1 column (int) and make it editable ---
    if (colCat1 != null) {
        colCat1.setCellValueFactory(new PropertyValueFactory<>("cat1"));
        colCat1.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colCat1.setOnEditCommit(event -> {
            Exam exam = event.getRowValue();
            exam.setCat1(event.getNewValue());   // ✅ stays in memory
        });
    }

    // --- Bind CAT2 column (int) and make it editable ---
    if (colCat2 != null) {
        colCat2.setCellValueFactory(new PropertyValueFactory<>("cat2"));
        colCat2.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colCat2.setOnEditCommit(event -> {
            Exam exam = event.getRowValue();
            exam.setCat2(event.getNewValue());   // ✅ stays in memory
        });
    }

    // --- Bind Main (marks) column (int) and make it editable ---
    if (colMain != null) {
        colMain.setCellValueFactory(new PropertyValueFactory<>("main"));
        colMain.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colMain.setOnEditCommit(event -> {
            Exam exam = event.getRowValue();
            exam.setMain(event.getNewValue());   // ✅ stays in memory
        });
    }

    // --- Make the table editable ---
    if (ExamTable != null) {
        ExamTable.setEditable(true);
    }

    // --- Initial load ---
    fetchDataSafe();

    // --- Only reload when course/year/semester selection changes ---
    if (uploadRe != null) {
        uploadRe.setOnAction(event -> {
            if (uploadRe.getSelectionModel().getSelectedItem() != null) {
                fetchDataSafe();
            }
        });
    }
    if (yearRe != null) {
        yearRe.setOnAction(event -> {
            if (yearRe.getSelectionModel().getSelectedItem() != null) {
                fetchDataSafe();
            }
        });
    }
    if (semesterRe != null) {
        semesterRe.setOnAction(event -> {
            if (semesterRe.getSelectionModel().getSelectedItem() != null) {
                fetchDataSafe();
            }
        });
    }
    

    // ⚠️ No reloads tied to table editing — marks now remain until Upload
}
private void fetchDataSafe() {
    try {
        fetchData();   // <-- this is your real DB-loading method
    } catch (Exception e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Error loading data", e.getMessage());
    }
}


// --- Safe wrapper
}
