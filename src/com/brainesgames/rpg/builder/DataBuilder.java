package com.brainesgames.rpg.builder;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Created by obrai on 2016-12-22.
 */
public class DataBuilder extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        VBox root = new VBox();
        ToggleGroup categoryGroup = new ToggleGroup();
        HBox categoryBox = new HBox();
        RadioButton items = new RadioButton("items");
        categoryGroup.selectToggle(items);
        categoryGroup.getToggles().addAll(items);
        categoryBox.getChildren().addAll(items);
        ToggleGroup subcategoryGroup = new ToggleGroup();
        HBox subcategoryBox = new HBox();
        subcategoryBox.getChildren().addAll(new Label("BLANK"));
        ToggleGroup sub2categoryGroup = new ToggleGroup();
        HBox sub2categoryBox = new HBox();
        sub2categoryBox.getChildren().addAll(new Label("BLANK"));

        root.getChildren().addAll(categoryBox,subcategoryBox,sub2categoryBox);

        Scene scene = new Scene(root,400,400);

        stage.setTitle("DataBuilder");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}
