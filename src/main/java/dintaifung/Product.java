package dintaifung;

public class Product {

    private String name;
    private int price;
    private int quantity;
    private String description;
    private String imagePath;

    // 正確的建構函數
    public Product(String name, int price, String description, String imagePath) {
        this.name = name;
        this.price = price;
        this.quantity = 1;  // 預設數量為 1
        this.description = description;
        this.imagePath = "/imgs/" + imagePath;  // 修正路徑
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public int getTotalPrice() {
        return price * quantity;
    }

    public String getImagePath() {
        return imagePath;
    }


    @Override
    public String toString() {
        return name + " - $" + price + " x" + quantity + " = $" + getTotalPrice();
    }

}
