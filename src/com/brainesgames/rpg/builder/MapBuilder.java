package com.brainesgames.rpg.builder;

import com.brainesgames.rpg.MapUnit;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by obrai on 2016-12-22.
 */
public class MapBuilder extends Application {
    private GraphicsContext g;
    private File[] textureFiles;
    private MapUnit[][] grid;
    private int activeX, activeY;
    private int s;
    private double offset,unit;

    static Color colourActive = Color.rgb(0xff, 0x00, 0x00, 0.5);
    @Override
    public void start(Stage stage) throws Exception {
        VBox root = new VBox();

        grid = new MapUnit[10][10];
        activeX = 0;
        activeY = 0;
        s = 500;
        offset = s * 0.10;
        unit = s * 0.08;
        Canvas canvas = new Canvas(s,s);
        g = canvas.getGraphicsContext2D();
        paint();

        canvas.setOnMousePressed(e->{
            int x = (int)Math.floor((e.getX() - offset)/ unit);
            int y = (int)Math.floor((e.getY() - offset)/ unit);
            if(x >= 0 && x < grid.length && y >= 0 && y < grid.length){
                activeX = x;
                activeY = y;
            }
            paint();
        });

        ToggleGroup categoryGroup = new ToggleGroup();
        HBox categoryBox = new HBox();
        RadioButton textureButton = new RadioButton("textures");
        RadioButton npcButton = new RadioButton("NPCs");
        RadioButton resourceButton = new RadioButton("resourceButton");
        categoryGroup.selectToggle(textureButton);
        categoryGroup.getToggles().addAll(textureButton, npcButton, resourceButton);
        categoryBox.getChildren().addAll(textureButton, npcButton, resourceButton);

        File textureDirectory = new File("res/images/map");
        textureFiles = textureDirectory.listFiles();
//        textureFileNames = new String[textureFiles.length];
//        for(int i=0; i<textureFileNames.length; i++) textureFileNames[i] = textureFiles[i].getAbsolutePath();
//        System.out.println(textureDirectory.getAbsolutePath());
//        System.out.println(textureDirectory.isDirectory());
//        System.out.println(textureFileNames);
        ToggleButton[] textureToggles = new ToggleButton[textureFiles.length];
        for(int i=0; i<textureToggles.length; i++){
            ImageView iv = new ImageView(new Image(new FileInputStream(textureFiles[i])));
            iv.setFitHeight(64);
            iv.setFitWidth(64);
            textureToggles[i] = new ToggleButton(textureFiles[i].getName(), iv);
        }
        ToggleGroup textureTG = new ToggleGroup();
        textureTG.getToggles().addAll(textureToggles);
        TilePane texturePane = new TilePane();
        texturePane.getChildren().addAll(textureToggles);
        TilePane npcPane = new TilePane();
        TilePane resourcePane = new TilePane();
        ScrollPane scrollPane = new ScrollPane(texturePane);
        scrollPane.setPrefSize(500,100);

        root.getChildren().addAll(canvas,categoryBox, scrollPane);

        Scene scene = new Scene(root);

        stage.setTitle("MapBuilder");
        stage.setScene(scene);
        stage.show();
    }

    void paint(){
        g.setFill(Color.WHITE);
        g.fillRect(0,0,500,500);
        g.setStroke(Color.BLACK);
        g.setLineWidth(3);

        for(int i=0; i<=10;i++){
            g.strokeLine(offset + i*unit, offset, offset + i*unit, offset + 10*unit);
            g.strokeLine(offset, offset + i*unit, offset + 10*unit, offset + i*unit);
        }

        for(int x = 0; x < grid.length; x++){
            for(int y = 0; y < grid.length; y++){
                if(grid[x][y] != null){
                    g.drawImage(grid[x][y].getTexture(), offset + x * unit, offset+y*unit, unit, unit);
                }
                if(x == activeX && y == activeY){
                    g.setFill(colourActive);
                    g.fillRect(offset + x * unit, offset+y*unit, unit, unit);
                }
            }
        }
    }

    public static void main(String[] args){
        launch(args);
    }
}
