module com.project1.slangdictionary {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.project1.slangdictionary to javafx.fxml;
    exports com.project1.slangdictionary;
}