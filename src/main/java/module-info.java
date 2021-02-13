module org.francescoborri.JSONViewer {
    requires org.json;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;

    opens org.francescoborri.JSONViewer to javafx.controls, javafx.fxml, javafx.graphics;
}