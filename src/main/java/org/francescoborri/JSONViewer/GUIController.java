package org.francescoborri.JSONViewer;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.*;

import java.io.IOException;
import java.net.*;
import java.util.Iterator;

public class GUIController {
    @FXML
    private TextField urlField;

    @FXML
    private TreeView<String> jsonTree;

    @FXML
    private void initialize() {
        jsonTree.setFocusTraversable(false);
    }

    public void ready() {
        App.getScene().getRoot().requestFocus();
        App.getScene().setOnKeyPressed(keyEvent -> {
            try {
                if (keyEvent.getCode() == KeyCode.ENTER) get();
            } catch (IOException | URISyntaxException ignored) {
            }
        });
    }

    @FXML
    void get() throws IOException, URISyntaxException {
        String stringURL = urlField.getText();
        if (stringURL == null || stringURL.isBlank())
            return;

        URL url = new URL(stringURL.trim());
        URI resource = new URI(
                url.getProtocol(),
                url.getUserInfo(),
                url.getHost(),
                url.getPort(),
                url.getPath(),
                url.getQuery(),
                url.getRef()
        );

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(resource);
        HttpResponse response = client.execute(request);

        JSONTokener jsonTokener = new JSONTokener(response.getEntity().getContent());
        jsonTree.setRoot(new TreeItem<>(resource.toASCIIString()));

        if (isJSONObject(jsonTokener)) {
            JSONObject json = new JSONObject(jsonTokener);
            json.keys().forEachRemaining((String jsonKey) -> load(jsonTree.getRoot(), jsonKey, json.get(jsonKey)));
        } else if (isJSONArray(jsonTokener)) {
            Iterator<Object> iterator = new JSONArray(jsonTokener).iterator();
            for (int i = 0; iterator.hasNext(); i++)
                load(jsonTree.getRoot(), String.valueOf(i), iterator.next());
        } else
            throw new JSONException(String.format("Invalid JSON response from URL %s", resource.toASCIIString()));
    }

    private boolean isJSONObject(JSONTokener jsonTokener) {
        boolean result = jsonTokener.next() == '{';
        jsonTokener.back();
        return result;
    }

    private boolean isJSONArray(JSONTokener jsonTokener) {
        boolean result = jsonTokener.next() == '[';
        jsonTokener.back();
        return result;
    }

    private void load(TreeItem<String> parent, String key, Object value) {
        TreeItem<String> node = new TreeItem<>(key);

        if (value instanceof String || value instanceof Number) {
            node.getChildren().add(new TreeItem<>(String.valueOf(value)));
        } else if (value instanceof JSONObject) {
            JSONObject json = (JSONObject) value;
            json.keys().forEachRemaining((String jsonKey) -> load(node, jsonKey, json.get(jsonKey)));
        } else if (value instanceof JSONArray) {
            Iterator<Object> iterator = ((JSONArray) value).iterator();
            for (int i = 0; iterator.hasNext(); i++)
                load(node, String.valueOf(i), iterator.next());
        }

        parent.getChildren().add(node);
    }
}