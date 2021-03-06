module org.francescoborri.JSONViewer {
    requires org.json;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;

    opens org.francescoborri.JSONViewer to javafx.controls, javafx.fxml, javafx.graphics;
    exports org.francescoborri.JSONViewer;
}