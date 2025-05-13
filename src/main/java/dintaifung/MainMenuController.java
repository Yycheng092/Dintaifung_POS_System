package dintaifung;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainMenuController {

    // 菜單按鈕點擊
    @FXML
    private Button noodleSoupButton, dryNoodleButton, friedRiceButton, soupButton;
    @FXML
    private ImageView menuItem1, menuItem2, menuItem3, menuItem4;
    @FXML
    private Label productName;

    @FXML
    private ComboBox<Integer> quantityComboBox;

    @FXML
    private Button addToCartButton;

    @FXML
    private ListView<Product> cartListView;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private Label totalAmount;

    private ObservableList<Product> cartItems = FXCollections.observableArrayList();
    private Product selectedProduct;

    @FXML
    private ImageView productImage;
    @FXML
    private Button checkoutButton;
    @FXML
    private Button cancelOrderButton;
    @FXML
    private Button cancelSingleButton;
    @FXML
    private Button memberLoginButton;
    @FXML
    private Button backendManagementButton;

    @FXML
    private GridPane menuGrid;

    @FXML
    private TableView<Product> cartTable;
    @FXML
    private TableColumn<Product, String> nameColumn;
    @FXML
    private TableColumn<Product, Integer> priceColumn;
    @FXML
    private TableColumn<Product, Integer> quantityColumn;

    @FXML
    private Label memberPhoneLabel;

    // 已登入的會員電話
    private String loggedInPhone = null;

    @FXML
    public void initialize() {
        // 預設顯示 "湯麵" 圖片
        showProducts("noodles");
        noodleSoupButton.setOnAction(e -> showProducts("noodles"));
        dryNoodleButton.setOnAction(e -> showProducts("dry_noodles"));
        friedRiceButton.setOnAction(e -> showProducts("fried_rice"));
        soupButton.setOnAction(e -> showProducts("soup"));

        // 初始化數量選擇
        quantityComboBox.getItems().addAll(1, 2, 3, 4, 5, 10, 20);
        quantityComboBox.setValue(1);

        // 設置表格列
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        cartTable.setItems(cartItems);

        // 設置加入購物車按鈕
        addToCartButton.setOnAction(e -> addToCart());

        // 設置取消單筆訂單按鈕
        cancelSingleButton.setOnAction(e -> cancelSingleItem());

        // 設置取消整筆訂單按鈕
        cancelOrderButton.setOnAction(e -> cancelOrder());

        // 設置結帳按鈕
        checkoutButton.setOnAction(e -> generateOrderSummary());

        // 初始化會員電話顯示
        memberPhoneLabel.setText("未登入");

        // 設置會員登入按鈕
        memberLoginButton.setOnAction(e -> openLoginDialog());

        // 設置會員登入按鈕
        memberLoginButton.setOnAction(e -> openLoginDialog());

        if (loggedInPhone != null) {
            memberPhoneLabel.setText("會員電話：" + loggedInPhone);
        } else {
            memberPhoneLabel.setText("未登入");
        }
    }

    // 設置會員電話
    public void setLoggedInPhone(String phone) {
        this.loggedInPhone = phone;
        memberPhoneLabel.setText("會員電話：" + phone);
    }

    @FXML
    private void cancelSingleItem() {
        // 取得選中的商品
        Product selectedItem = cartTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            // 從購物車移除
            cartItems.remove(selectedItem);
            cartTable.refresh();

            // 更新總金額
            updateTotalAmount();
        }
    }

    @FXML
    private void cancelOrder() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("取消訂單");
        alert.setHeaderText("確定要取消整筆訂單嗎？");
        alert.setContentText("此操作將會清空購物車。");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            cartItems.clear();
            cartTable.refresh();
            updateTotalAmount();
        }
    }

    @FXML
    private void generateOrderSummary() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dintaifung/Invoice.fxml"));
            Stage invoiceStage = new Stage();
            invoiceStage.setScene(new Scene(loader.load()));

            // 傳遞購物車數據
            InvoiceController controller = loader.getController();
            controller.setInvoiceDetails(cartItems);

            // 顯示帳單視窗
            invoiceStage.setTitle("帳單明細");
            invoiceStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProducts(String category) {
        switch (category) {
            case "noodles":
                setupHoverEffect(menuItem1, "紅燒牛肉麵", 300, "noodle1.png", "精心熬製的牛骨湯頭，馨香濃郁，加入以獨門秘方滷製而成的軟滑牛筋及鮮嫰牛肉，搭配鼎泰豐自製雞蛋麵，絕頂美味令人食指大動。");
                setupHoverEffect(menuItem2, "元盅雞麵", 300, "noodle2.png", "採用十六周以上的黑羽土雞，加入費時細熬八小時的精華湯底，再以慢火蒸煮。清澈溫潤的雞湯，搭配鼎泰豐自製雞蛋麵，風味更顯獨特。");
                setupHoverEffect(menuItem3, "元盅牛肉麵", 290, "noodle3.png", "嚴選上等牛肉，經師傅精巧刀工，依肉質紋理切片，加入精心熬製八小時的湯底後，再以慢火蒸煮，完整保留肉質的細緻鮮嫩，清甜的牛肉湯頭伴隨著微微的蔥香，搭配鼎泰豐自製雞蛋麵，絕佳風味入口難忘。");
                setupHoverEffect(menuItem4, "紅燒牛肉湯麵", 160, "noodle4.png", "精心熬製的牛骨湯頭，馨香濃郁，搭配鼎泰豐自製雞蛋麵，鮮甜甘美。");
                // 預設顯示第一個產品
                updateProductDetails("紅燒牛肉麵", 300, "noodle1.png", "精心熬製的牛骨湯頭，馨香濃郁，加入以獨門秘方滷製而成的軟滑牛筋及鮮嫰牛肉，搭配鼎泰豐自製雞蛋麵，絕頂美味令人食指大動。");
                break;
            case "dry_noodles":
                setupHoverEffect(menuItem1, "炸醬麵", 170, "dry_noodle1.png", "以豆干、毛豆、絞肉、番茄等多種食材，調配出鹹香適中的獨家炸醬，搭配鼎泰豐自製雞蛋麵，滋味樸實且富含層次，建議搭配桌上米醋一起享用，口味更提升。");
                setupHoverEffect(menuItem2, "雪菜肉絲乾拌麵", 190, "dry_noodle2.png", "將新鮮油菜製成的雪菜、鮮甜筍絲與肉絲拌炒，口感清脆爽口，佐以特製醬料乾拌入味的鼎泰豐自製雞蛋麵，每一口都是難忘的好滋味。");
                setupHoverEffect(menuItem3, "擔擔麵", 140, "dry_noodle3.png", "特調麻醬散發濃郁花生與芝麻香氣，加入特製辣油，搭配鼎泰豐自製雞蛋麵，拌勻後品嚐，蒜香恰到好處。");
                setupHoverEffect(menuItem4, "麻醬麵", 140, "dry_noodle4.png", "特調麻醬散發微微的花生味與芝麻香，搭配鼎泰豐自製雞蛋麵享用，香氣十足。");
                updateProductDetails("炸醬麵", 170, "dry_noodle1.png", "以豆干、毛豆、絞肉、番茄等多種食材，調配出鹹香適中的獨家炸醬，搭配鼎泰豐自製雞蛋麵，滋味樸實且富含層次，建議搭配桌上米醋一起享用，口味更提升。");
                break;
            case "fried_rice":
                setupHoverEffect(menuItem1, "蝦仁蛋炒飯", 300, "fried_rice1.png", "美味蝦仁加上特選青蔥，並使用優質台梗九號米，在火候油溫精控下，炒出粒粒分明、蛋香四溢的黃金蛋炒飯，是鼎泰豐的人氣料理。");
                setupHoverEffect(menuItem2, "肉絲蛋炒飯", 250, "fried_rice2.png", "採用優質台梗九號米，在火候油溫精控下炒出的黃金蛋炒飯，粒粒分明、蛋香四溢，再加上肉絲軟嫩的口感，每一口都是驚喜。");
                setupHoverEffect(menuItem3, "蝦仁肉絲蛋炒飯", 360, "fried_rice3.png", "「飯香、蛋嫩、火候足」是炒好飯的三大要訣。將優質台梗九號米煮成的熱飯炒至蛋花均勻密布，加上美味蝦仁與肉絲，更是令人吮指回味。");
                setupHoverEffect(menuItem4, "排骨蛋炒飯", 310, "fried_rice4.png", "採用優質台梗九號米的金黃色澤炒飯，蛋花均勻密布，每口咀嚼皆富含蛋香與蔥香，再搭配肉香十足的鼎泰豐炸排骨，令人食慾大開。");
                updateProductDetails("蝦仁蛋炒飯", 300, "fried_rice1.png", "美味蝦仁加上特選青蔥，並使用優質台梗九號米，在火候油溫精控下，炒出粒粒分明、蛋香四溢的黃金蛋炒飯，是鼎泰豐的人氣料理。");
                break;
            case "soup":
                setupHoverEffect(menuItem1, "元盅雞湯", 250, "soup1.png", "採用十六周以上的黑羽土雞，加入費時細熬八小時的精華湯底，再以慢火蒸煮。清澈溫潤的雞湯，營養滋補、甘醇鮮甜，是本店的招牌餐點之一。");
                setupHoverEffect(menuItem2, "元盅牛肉湯", 240, "soup2.png", "嚴選上等牛肉，經師傅精巧刀工，依肉質紋理切片，加入精心熬製八小時的湯底後，再以慢火蒸煮，完整保留肉質的細緻鮮嫩，將牛肉的清甜鮮美，釋放於湯中，是碗清燉風味的上等湯品。");
                setupHoverEffect(menuItem3, "紅燒牛肉湯", 270, "soup3.png", "精心熬製的牛骨湯頭，馨香濃郁，再加入以獨門秘方滷製的軟滑牛筋及鮮嫰牛肉，鮮甜甘美。");
                setupHoverEffect(menuItem4, "菜肉餛飩湯", 190, "soup4.png", "薄透麵皮包覆翠綠飽滿的小白菜及青江菜、絞肉肉餡，搭配特製高湯，佐上些許蔥花和榨菜，清爽的口感，推薦給喜歡青蔬的您。");
                updateProductDetails("元盅雞湯", 250, "soup1.png", "採用十六周以上的黑羽土雞，加入費時細熬八小時的精華湯底，再以慢火蒸煮。清澈溫潤的雞湯，營養滋補、甘醇鮮甜，是本店的招牌餐點之一。");
                break;
        }
    }

    private void setupHoverEffect(ImageView imageView, String name, int price, String imageName, String description) {
        // 設置點擊效果
        imageView.setOnMouseClicked(e -> updateProductDetails(name, price, imageName, description));

        // 設置初始圖片
        String imagePath = getClass().getResource("/imgs/" + imageName).toExternalForm();
        imageView.setImage(new Image(imagePath));
    }

    private void updateProductDetails(String name, int price, String imageName, String description) {
        productName.setText(name + " - $" + price);
        descriptionArea.setText(description);

        // 設置左側的大圖片
        String imagePath = getClass().getResource("/imgs/" + imageName).toExternalForm();
        productImage.setImage(new Image(imagePath));

        // 更新選中的產品
        selectedProduct = new Product(name, price, description, imageName);
    }

    private void selectProduct(Product product) {
        this.selectedProduct = product;
        productName.setText(product.getName() + " - $" + product.getPrice());
        productImage.setImage(new Image(getClass().getResource(product.getImagePath()).toExternalForm()));
    }

    private void updateTotalAmount() {
        int total = 0;
        for (Product item : cartItems) {
            total += item.getTotalPrice();
        }
        totalAmount.setText("總金額: " + total);
    }

    private void addToCart() {
        if (selectedProduct != null) {
            int quantity = quantityComboBox.getValue();

            // 檢查是否已經存在相同品項
            boolean itemExists = false;
            for (Product item : cartItems) {
                if (item.getName().equals(selectedProduct.getName())) {
                    // 更新已存在商品的數量
                    item.setQuantity(item.getQuantity() + quantity);
                    itemExists = true;
                    break;
                }
            }

            // 如果是新商品，加入購物車
            if (!itemExists) {
                Product cartItem = new Product(
                        selectedProduct.getName(),
                        selectedProduct.getPrice(),
                        selectedProduct.getDescription(),
                        selectedProduct.getImagePath().replace("/imgs/", "")
                );
                cartItem.setQuantity(quantity);
                cartItems.add(cartItem);
            }

            // 刷新購物車列表
            cartTable.refresh();

            // 計算總金額
            updateTotalAmount();
        }
    }

    private void openLoginDialog() {
        try {
            // 加載 Login 畫面
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dintaifung/Login.fxml"));
            Parent root = loader.load();
            LoginController loginController = loader.getController();

            // 建立新的登入視窗
            Stage loginStage = new Stage();
            loginStage.initModality(Modality.APPLICATION_MODAL);
            loginStage.setTitle("會員登入");
            loginStage.setScene(new Scene(root));

            // 顯示登入視窗並等待使用者輸入
            loginStage.showAndWait();

            // 取得登入後的電話號碼
            String loggedInPhone = loginController.getLoggedInPhone();
            if (loggedInPhone != null) {

                // 如果是新會員，清空購物車
                if (!loggedInPhone.equals(this.loggedInPhone)) {
                    cartItems.clear();      // 清空購物車
                    cartTable.refresh();    // 刷新表格
                    updateTotalAmount();    // 更新總金額
                }

                // 更新會員電話標籤
                memberPhoneLabel.setText("會員電話：" + loggedInPhone);
                this.loggedInPhone = loggedInPhone;

            } else {
                // 如果登入失敗，顯示警告
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("登入失敗");
                alert.setHeaderText(null);
                alert.setContentText("登入失敗，請重新嘗試！");
                alert.showAndWait();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            // 發生錯誤，顯示錯誤訊息
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("系統錯誤");
            alert.setHeaderText(null);
            alert.setContentText("無法載入登入畫面，請稍後再試！");
            alert.showAndWait();
        }
    }

}
