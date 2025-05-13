package dintaifung;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AppController implements Initializable {

               @FXML
               private AnchorPane rootPane;
               @FXML
               private Button Visitor_Registration;
               @FXML
               private Button Member_Login;
               @FXML
               private Button Backstage_Management;

               @Override
               public void initialize(URL url, ResourceBundle rb) {
                              // 訪客註冊按鈕
                              Visitor_Registration.setOnAction(e
                                        -> VisitorRegistration.show((Stage) rootPane.getScene().getWindow())
                              );

                              // 會員登入按鈕
                              Member_Login.setOnAction(e -> {
                                             Stage owner = (Stage) ((Node) e.getSource()).getScene().getWindow();
                                             LoginController.start(owner, null);  // 沒帶預設電話就傳 null
                              });

                              // 後臺管理按鈕（保持原有實作）
                              Backstage_Management.setOnAction(e
                                        -> openMenu("後臺管理畫面")
                              );
               }

               private void openMenu(String title) {
                              Stage menuStage = new Stage();
                              menuStage.setTitle(title);
                              // 這裡可以先放空白內容，或使用 Canvas/其他 UI
                              AnchorPane menuRoot = new AnchorPane();
                              menuStage.setScene(new Scene(menuRoot, 600, 400));
                              menuStage.show();
                              // 關閉原主畫面
                              Stage primary = (Stage) rootPane.getScene().getWindow();
                              primary.close();
               }
}
