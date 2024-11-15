module org.example.cw {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.cw to javafx.fxml;
    exports org.example.cw;
}