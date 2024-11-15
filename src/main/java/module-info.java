module org.example.cw {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.cw to javafx.fxml;
    exports org.example.cw;
}