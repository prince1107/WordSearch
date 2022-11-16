module com.example.wordsearch {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.wordsearch to javafx.fxml;
    exports com.example.wordsearch;
}