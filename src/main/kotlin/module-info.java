module com.example.tankgame {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;


    opens mqcreaple.tankgame to javafx.fxml;
    exports mqcreaple.tankgame;
}