import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Student {

  
    private String regNo;
    private String name;
    private String email;
    private String phoneNo;
    private String course;
    private String idNo;

    @FXML private TextField nameField, password, id, emailField, phoneNumber, address, regNoField, courseField, courseId,county;
    @FXML private Label registerLabel;

    @FXML private ComboBox<String> courseCombo;
    @FXML private ComboBox<String> yearCombo;
    @FXML private TableView<Student> learnerTable;
    @FXML private TableColumn<Student, String> colNo;
    @FXML private TableColumn<Student, String> colName;
    @FXML private TableColumn<Student, String> colEmail;
    @FXML private TableColumn<Student, String> colPhone;
     @FXML private TableColumn<Student, String>colCourse;
    @FXML private TableColumn <Student,String>colID;
    public Student() {
        
    }

    public Student(String regNo, String name, String email, String phoneNo, String course,String idNo) {
        this.regNo = regNo;
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
        this.course = course;
        this.idNo=idNo;
    }

    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNo() { return phoneNo; }
    public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getIdNo(){
        return idNo;

    }
    public void setIdNo(String idNo){
        this.idNo=idNo;
    }

    


    public void registerLearner(ActionEvent event) {
    String username = nameField.getText();
    String password1 = password.getText();
    String idNo = id.getText();
    String Email = emailField.getText();
    String phone = phoneNumber.getText();
    String addres = address.getText();
    String regN = regNoField.getText();
    String Course = courseField.getText();
    String CourseId = courseId.getText();
    String County=county.getText();

    if (username == null || username.isBlank() ||
        password1 == null || password1.isBlank() ||
        Course == null || Course.isBlank() ||
        regN == null || regN.isBlank()) {
        showAlert(Alert.AlertType.WARNING, "Missing Details", "Fill the missing Details");
        return;
    }

    try (Connection conn = DbConnection.getConnection()) {
        // Calculate year of study based on regNo suffix
        int yearOfStudy = calculateYearOfStudy(regN);

        String sql = "INSERT INTO user(name,password,email,idNo,address,regNo,phoneNo,courseId,course,county,year_of_study) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, username);
        stmt.setString(2, password1);
        stmt.setString(3, Email);
        stmt.setString(4, idNo);
        stmt.setString(5, addres);
        stmt.setString(6, regN);
        stmt.setString(7, phone);
        stmt.setString(8, CourseId);
        stmt.setString(9, Course);
        stmt.setString(10, County);
        stmt.setInt(11, yearOfStudy);

        int rowsInserted = stmt.executeUpdate();
        if (rowsInserted > 0) {
            showAlert(Alert.AlertType.INFORMATION, "Registered Successfully", "Registered successfully");
        } else {
            showAlert(Alert.AlertType.ERROR, "Failure", "Account Not created");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
private int calculateYearOfStudy(String regNo) {
    // Extract suffix after "/"
    String suffix = regNo.substring(regNo.lastIndexOf("/") + 1);
    int admissionYear = 2000 + Integer.parseInt(suffix); // e.g. "23" -> 2023

    LocalDate now = LocalDate.now();
    int currentYear = now.getYear();

    int diff = currentYear - admissionYear;

    // If we are in August or later, increment year of study
    if (now.getMonthValue() >= 9) {
        diff += 1;
    }

    // Minimum year should be 1
    return Math.max(diff, 1);
}


    @FXML
    private void handleImportCSV(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file));
                 Connection conn = DbConnection.getConnection()) {

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length >= 11) {
                        String sql = "INSERT INTO user(name,password,email,idNo,address,regNo,phoneNo,courseId,course,county,year_of_study) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setString(1, data[0]);
                        stmt.setString(2, data[1]);
                        stmt.setString(3, data[2]);
                        stmt.setString(4, data[3]);
                        stmt.setString(5, data[4]);
                        stmt.setString(6, data[5]);
                        stmt.setString(7, data[6]);
                        stmt.setString(8, data[7]);
                        stmt.setString(9, data[8]);
                        stmt.setString(10,data[9]);
                        stmt.setString(11,data[10]);
                        stmt.executeUpdate();
                    }
                }
                showAlert(Alert.AlertType.INFORMATION, "Import Success", "CSV data imported successfully!");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Import Failed", e.getMessage());
            }
        }
    }

    // ------------------- CSV Export -------------------
    @FXML
    private void handleExportCSV(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                 Connection conn = DbConnection.getConnection()) {

                String sql = "SELECT name,password,email,idNo,address,regNo,phoneNo,course,courseId FROM user";
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                            rs.getString("name"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("idNo"),
                            rs.getString("address"),
                            rs.getString("regNo"),
                            rs.getString("phoneNo"),
                            rs.getString("courseId"),
                            rs.getString("course")));
                }
                showAlert(Alert.AlertType.INFORMATION, "Export Success", "CSV file saved successfully!");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Export Failed", e.getMessage());
            }
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
        Parent root = FXMLLoader.load(getClass().getResource("exam.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("Application.css").toExternalForm());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void staff() {
        
    }
    public void handleCourse(ActionEvent event){
    FileChooser filechooser=new FileChooser();
    filechooser.setTitle("Choose a CSV File");
    filechooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files","*.csv"));
    File file= filechooser.showOpenDialog(null);

    if(file!=null){
        try(BufferedReader reader= new BufferedReader(new FileReader(file));
        Connection conn= DbConnection.getConnection()){
            String line;

            while((line=reader.readLine())!=null){
                String[] data=line.split(",");
                if(data.length>=2){
                      String sql = "INSERT INTO course(courseID,name)VALUES(?,?)";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setString(1, data[0]); 
                        stmt.setString(2, data[1]); 
                      
                      
                    
                }
                showAlert(Alert.AlertType.INFORMATION, "Export Success", "CSV  course file saved successfully!");
            } 

        }catch(Exception e){
            showAlert(Alert.AlertType.INFORMATION, "Export Filde", "CSV  course file Not uploaded");
        }
    }
}
public void viewLearner(ActionEvent event) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("viewLearner.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("Application.css").toExternalForm());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
}

private void filterLearners() {
    String selectedCourse = courseCombo.getSelectionModel().getSelectedItem();
    String selectedYear = yearCombo.getSelectionModel().getSelectedItem();

    if (selectedCourse != null && selectedYear != null) {
        ObservableList<Student> learners = FXCollections.observableArrayList();
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT * FROM user WHERE course = ? AND year_of_study = ?")) {
            stmt.setString(1, selectedCourse); // matches course name in user table
            stmt.setString(2, selectedYear);   // matches year_of_study column

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                learners.add(new Student(
                    rs.getString("regNo"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phoneNo"),
                    rs.getString("course"),
                    rs.getString("idNo")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        learnerTable.setItems(learners);
    }
}


@FXML
public void initialize() {
    // Populate course combo from DB
    if (courseCombo != null) {
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT name FROM course");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                courseCombo.getItems().add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Populate year combo with fixed values 1–4
    if (yearCombo != null) {
        yearCombo.getItems().addAll("1", "2", "3", "4");
    }

    // Table column bindings
    if (colNo != null) colNo.setCellValueFactory(new PropertyValueFactory<>("regNo"));
    if (colName != null) colName.setCellValueFactory(new PropertyValueFactory<>("name"));
    if (colEmail != null) colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    if (colPhone != null) colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNo"));
    if (colCourse != null) colCourse.setCellValueFactory(new PropertyValueFactory<>("course"));
    if (colID != null) colID.setCellValueFactory(new PropertyValueFactory<>("idNo"));

    // Add listeners to filter learners by both course and year
    if (courseCombo != null) {
        courseCombo.setOnAction(event -> filterLearners());
    }
    if (yearCombo != null) {
        yearCombo.setOnAction(event -> filterLearners());
    }
}




@FXML
public void loadLearnersByCourse(ActionEvent event) {
    String selectedCourse = courseCombo.getValue();
    if (selectedCourse == null) return;

    ObservableList<Student> learners = FXCollections.observableArrayList();

    try (Connection conn = DbConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user WHERE course = ?")) {
        stmt.setString(1, selectedCourse);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            learners.add(new Student(
                rs.getString("regNo"),  
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phoneNo"),
                rs.getString("course"),
                rs.getString("idNo")
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    learnerTable.setItems(learners);
}






}
