package dintaifung;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class VisitorRegistration implements Initializable {

               @FXML
               private TextField nameField;    // 姓名輸入框
               @FXML
               private TextField phoneField;   // 電話輸入框
               @FXML
               private Button submitButton;
               @FXML
               private Button backButton;
               @FXML
               private Label errorLabel;

               // 僅允許 09 開頭 10 碼
               private static final Pattern PHONE_PATTERN = Pattern.compile("^09\\d{8}$");

               //顯示註冊對話框 
               public static void show(Stage owner) {
                              try {
                                             FXMLLoader loader = new FXMLLoader(
                                                       VisitorRegistration.class.getResource("/dintaifung/VisitorRegistration.fxml")
                                             );
                                             Parent root = loader.load();
                                             Stage dialog = new Stage();
                                             dialog.setTitle("新增會員");
                                             dialog.initOwner(owner);
                                             dialog.initModality(Modality.APPLICATION_MODAL);
                                             dialog.setScene(new Scene(root));
                                             dialog.showAndWait();
                              } catch (IOException e) {
                                             e.printStackTrace();
                              }
               }

               @Override
               public void initialize(URL url, ResourceBundle rb) {
                              // 初始狀態
                              submitButton.setDisable(true);
                              errorLabel.setVisible(false);

                              // 同步監聽兩個欄位
                              nameField.textProperty().addListener((obs, o, n) -> validateForm());
                              phoneField.textProperty().addListener((obs, o, n) -> validateForm());

                              backButton.setOnAction(e
                                        -> ((Stage) backButton.getScene().getWindow()).close()
                              );
               }

               //驗證姓名 & 電話格式
               private void validateForm() {
                              String name = nameField.getText().trim();
                              String phone = phoneField.getText().trim();
                              boolean validName = !name.isEmpty();
                              boolean validPhone = PHONE_PATTERN.matcher(phone).matches();

                              submitButton.setDisable(!(validName && validPhone));

                              if (!phone.isEmpty() && !validPhone) {
                                             errorLabel.setText("電話需以09開頭，共10位數");
                                             errorLabel.setStyle("-fx-text-fill: red;");
                                             errorLabel.setVisible(true);
                              } else {
                                             errorLabel.setVisible(false);
                              }
               }

               // 擷取使用者的名稱與電話輸入，並將其存入 SQLite
               @FXML
               private void handleSubmit(ActionEvent event) {
                              String name = nameField.getText().trim();
                              String phone = phoneField.getText().trim();
                              Stage dialog = (Stage) submitButton.getScene().getWindow();

                              // 1) 先檢查是否已註冊
                              if (visitorExists(phone)) {
                                             // 跳出確認，詢問要不要直接登入
                                             Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                                             confirm.initOwner(dialog);
                                             confirm.setTitle("已註冊");
                                             confirm.setHeaderText(null);
                                             confirm.setContentText("此電話已註冊過，是否前往會員登入？");
                                             Optional<ButtonType> res = confirm.showAndWait();
                                             if (res.isPresent() && res.get() == ButtonType.OK) {
                                                            dialog.close();
                                                            // 呼叫 LoginController.start 進入登入流程
                                                            LoginController.start((Stage) dialog.getOwner(), phone);

                                             }
                                             return;  // 無論 OK 或 Cancel，都不繼續 insert
                              }

                              // 2) 若未註冊，正常插入資料並關閉
                              insertVisitor(name, phone);
                              dialog.close();
               }

               // 若按下返回則回到原始 PosSystem 頁面
               @FXML
               private void handleBack(ActionEvent event) {
                              ((Stage) backButton.getScene().getWindow()).close();
               }

               // 查詢同電話是否已存在
               private boolean visitorExists(String phone) {
                              String url = "jdbc:sqlite:visitors.db?busy_timeout=5000";
                              String querySql = "SELECT COUNT(*) FROM visitor WHERE phone = ?";
                              try (Connection conn = DriverManager.getConnection(url); PreparedStatement ps = conn.prepareStatement(querySql)) {
                                             // 建表確保表存在
                                             try (Statement stmt = conn.createStatement()) {
                                                            stmt.execute("PRAGMA journal_mode = WAL");
                                                            stmt.execute("CREATE TABLE IF NOT EXISTS visitor ("
                                                                      + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                                                                      + "name TEXT, phone TEXT"
                                                                      + ")");
                                             }
                                             // 查詢
                                             ps.setString(1, phone);
                                             try (ResultSet rs = ps.executeQuery()) {
                                                            if (rs.next()) {
                                                                           return rs.getInt(1) > 0;
                                                            }
                                             }
                              } catch (SQLException ex) {
                                             ex.printStackTrace();
                              }
                              return false;
               }

               private void insertVisitor(String name, String phone) {
                              String url = "jdbc:sqlite:visitors.db?busy_timeout=5000";

                              // 建立 vistors 表
                              String createSql = ""
                                        + "CREATE TABLE IF NOT EXISTS visitor ("
                                        + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + "name TEXT, "
                                        + "phone TEXT"
                                        + ")";
                              // 插入資料的參數化 SQL，使用 PreparedStatement 防注入
                              String insertSql = "INSERT INTO visitor (name, phone) VALUES (?, ?)";
                              try (Connection conn = DriverManager.getConnection(url); Statement stmt = conn.createStatement()) {
                                             // 提升並發：WAL
                                             stmt.execute("PRAGMA journal_mode = WAL");
                                             stmt.execute(createSql);

                                             // 插入 name 與 phone 
                                             try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                                                            ps.setString(1, name);
                                                            ps.setString(2, phone);
                                                            ps.executeUpdate();
                                             }
                              } catch (SQLException ex) {
                                             ex.printStackTrace();
                              }
               }
}
