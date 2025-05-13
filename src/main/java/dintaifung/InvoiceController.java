package dintaifung;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InvoiceController {

    @FXML
    private TextArea invoiceTextArea;

    @FXML
    private Button closeButton;

    @FXML
    public void initialize() {
        closeButton.setOnAction(e -> ((Stage) closeButton.getScene().getWindow()).close());
    }

    public void setInvoiceDetails(List<Product> cartItems) {
        StringBuilder invoice = new StringBuilder();

        // 訂單標題
        invoice.append("購物清單明細\n");
        invoice.append("訂單成立時間：")
                .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a")))
                .append("\n\n");

        // 表頭
        invoice.append(String.format("%-12s%-7s%-6s%-10s\n", "品名", "單價", "數量", "金額"));
        invoice.append("------------------------------------\n");

        // 商品明細
        int totalAmount = 0;
        for (Product item : cartItems) {
            String productName = item.getName();
            if (productName.length() < 8) {
                // 自動補全產品名稱至8個字（全形）
                productName += "　".repeat(8 - productName.length());
            }
            String priceWithPadding = String.format("$%d", item.getPrice()) + "　　　";  // 單價 + 5個全型空白
            String quantityWithPadding = "x" + item.getQuantity() + "　　";  // 數量 + 3個全型空白
            String totalWithPadding = String.format("%d", item.getTotalPrice());

            invoice.append(productName)
                    .append(priceWithPadding)
                    .append(quantityWithPadding)
                    .append(totalWithPadding)
                    .append("\n");

            totalAmount += item.getTotalPrice();
        }

        // 總金額
        invoice.append("\n總金額：$").append(totalAmount).append("\n");

        // 設置到 TextArea
        invoiceTextArea.setText(invoice.toString());
    }
}
