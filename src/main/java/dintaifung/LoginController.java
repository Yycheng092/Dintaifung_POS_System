package dintaifung;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class LoginController implements Initializable {

    // ====== FXML 欄位注入 ======
    @FXML
    private TextField phoneField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button loginButton;
    @FXML
    private Button cancelButton;

    // 會員電話號碼
    private String loggedInPhone = null;

    public String getLoggedInPhone() {
        return loggedInPhone;
    }

    // ====== 初始化元件狀態 ======
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 隱藏錯誤訊息、禁用登入按鈕
        errorLabel.setVisible(false);
        loginButton.setDisable(true);

        // 監聽電話欄位：有輸入才啟用登入
        phoneField.textProperty().addListener((obs, oldText, newText) -> {
            loginButton.setDisable(newText.trim().isEmpty());
            errorLabel.setVisible(false);
        });

        // 取消按鈕：直接關閉對話框
        cancelButton.setOnAction(this::handleCancel);
    }

    // ====== 處理登入邏輯 ======
    @FXML
    private void handleLogin(ActionEvent event) {
        // 每次進來先隱藏舊的錯誤訊息
        errorLabel.setVisible(false);

        String phone = phoneField.getText().trim();

        // 1. 格式檢查：09開頭、共10碼
        if (!Pattern.matches("^09\\d{8}$", phone)) {
            showError("電話需以09開頭，共10位數");
            return;
        }

        // 2. 使用 JDBC 進行 SELECT 查詢
        String url = "jdbc:sqlite:visitors.db?busy_timeout=5000";
        String sql = "SELECT name FROM visitor WHERE phone = ?";
        try (Connection conn = DriverManager.getConnection(url); PreparedStatement ps = conn.prepareStatement(sql)) {

            // 2a. 綁定參數並執行查詢
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // 找到對應資料：登入成功
                    loggedInPhone = phone;
                    showSuccessMessage("歡迎回來，" + rs.getString("name") + "！");
                    ((Stage) phoneField.getScene().getWindow()).close();
                } else {
                    // 查無此會員
                    showError("查無此會員，請先註冊");
                }
            }

        } catch (SQLException ex) {
            // 資料庫例外處理
            ex.printStackTrace();
            showError("資料庫錯誤，稍後再試");
        }
    }

    // ====== 處理取消邏輯 ======
    @FXML
    private void handleCancel(ActionEvent event) {
        // 關閉視窗
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    // ====== 顯示錯誤訊息 ======
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setVisible(true);
    }

    // ====== 顯示成功訊息 ======
    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("登入成功");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // 可以帶入預設電話
    public void setInitialPhone(String phone) {
        phoneField.setText(phone);
        loginButton.setDisable(phone.trim().isEmpty());
    }

    // ====== 靜態啟動流程 ======
    public static void start(Stage owner, String initialPhone) {
        try {
            // 1. 載入 Login.fxml 並取得 controller 實例
            FXMLLoader loader = new FXMLLoader(LoginController.class.getResource("/dintaifung/Login.fxml"));
            Parent root = loader.load();
            LoginController ctrl = loader.getController();

            // 這邊帶入預設電話
            if (initialPhone != null && !initialPhone.trim().isEmpty()) {
                ctrl.setInitialPhone(initialPhone);
            }

            // 2. 建立模態對話框，鎖定 owner
            Stage dialog = new Stage();
            dialog.initOwner(owner);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("會員登入");
            dialog.setScene(new Scene(root));
            dialog.showAndWait();  // 顯示並等待使用者操作

            // 3. 對話框關閉後，切換到主選單
            if (ctrl.getLoggedInPhone() != null) {
                // 3a. 切換到 MainMenu.fxml
                FXMLLoader mainLoader = new FXMLLoader(LoginController.class.getResource("/dintaifung/MainMenu.fxml"));
                Parent mainRoot = mainLoader.load();
                Scene mainScene = new Scene(mainRoot);

                // 設置會員電話
                MainMenuController mainMenuController = mainLoader.getController();
                mainMenuController.setLoggedInPhone(ctrl.getLoggedInPhone());

                // 3b. 設置新的場景
                owner.setScene(mainScene);
                owner.setTitle("主選菜單");
                owner.show();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
