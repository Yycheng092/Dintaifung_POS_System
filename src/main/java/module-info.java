module Dintaifung {
    requires javafx.controls;
    requires javafx.fxml;

    // 这一行允许 javafx.fxml 通过反射访问 dintaifung 包
    opens dintaifung to javafx.fxml;
    // 加這一行，才能在程式裡 import java.sql.*
    requires java.sql;
      // 这一行：把 SQLite 驱动模块读进来
    requires org.xerial.sqlitejdbc;
    requires java.base;
    // 如果你要在别的地方 import AppController，还可以 exports
    exports dintaifung;
}
