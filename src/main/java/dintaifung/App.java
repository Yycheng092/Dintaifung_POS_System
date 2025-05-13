package dintaifung;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 載入鼎泰豐的會員登入頁面
        Parent root = FXMLLoader.load(
                getClass().getResource("/dintaifung/App.fxml")
        );

        // 設定背景圖
        Image bgImage = new Image(
                getClass().getResource("/imgs/background.jpg").toExternalForm()
        );
        
        BackgroundSize bgSize = new BackgroundSize(1.0, 1.0, true, true, false, false);
        BackgroundImage bg = new BackgroundImage(bgImage,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, bgSize);
        ((AnchorPane) root).setBackground(new Background(bg));

        // 顯示舞台
        primaryStage.setTitle("Dintaifung POS System");
        primaryStage.setScene(new Scene(root, 340, 250));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
