package karnaugh;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import java.util.HashSet;
import java.util.Arrays;

import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;


public class Game{
    KarnaughTable karnaugh; // represents logic beneath the game
    public static Map<Integer, Color> colorDict = App.colorDict;    

    final int START_TABLE_SIZE_X_BITS = 3; // How to split bits between x and y axis in table
    final int START_TABLE_SIZE_Y_BITS = 3;
    final int START_TABLE_VALUE_COUNT = 4; // How many logic values (tile colors) should be there
    final int MIN_PATTERN_SIZE = 4; // Pattern has to be at least this size to be scored
    final Set<ReplacementSource> replacementSourcesSet = new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top, ReplacementSource.Bottom }));
    
    // Initializes as a 4:3 aspect ratio 640x480 window
    final int SCENE_HEIGHT = 480;
    final int SCENE_WIDTH = 640;
    
    final int SIDEBAR_WIDTH = 160; // Width of the sidebar containing main menu button, score, etc.
    final int GAME_WIDTH = SCENE_HEIGHT - SIDEBAR_WIDTH; // Defaults game pane to cover the rest of the scene at initialization (root change)

    final int WIDTH = 1 << START_TABLE_SIZE_X_BITS; // = 2^startTableSizeXBits
    final int HEIGHT = 1 << START_TABLE_SIZE_Y_BITS;

    public void startGame() throws IOException{
        System.out.println("Starting game.");
        karnaugh = new KarnaughTable(START_TABLE_SIZE_X_BITS, START_TABLE_SIZE_Y_BITS, START_TABLE_VALUE_COUNT, MIN_PATTERN_SIZE, replacementSourcesSet);
        

        // array containing all buttons in the playable field
        Button[] rectangles = new Button[WIDTH * HEIGHT];
        

        HBox wholeLayout = new HBox(); // "topmost" layout; later used as the new root

        // Put there to make padding easier; a more clever solution almost certainly exists, but searching the net for answers started giving me flashbacks after doing it thousands of times
        Pane leftPad = new Pane();
        Pane rightPad = new Pane();
        
        GridPane gameLayout = new GridPane(); // rectangle containing more rectangles (buttons actually), game happens there
        VBox sidebarLayout = new VBox(); // main menu button, score, etc.

        wholeLayout.getChildren().addAll(leftPad, gameLayout, sidebarLayout, rightPad); // Adds all "sublayouts" to the "main layout" in order

        wholeLayout.setPrefWidth(SCENE_HEIGHT);

        // thanks to these scaling works. I think
        gameLayout.minWidthProperty().bind(App.scene.heightProperty());
        leftPad.prefWidthProperty().bind(rightPad.prefWidthProperty());
        
        wholeLayout.setHgrow(rightPad, Priority.ALWAYS);
        wholeLayout.setHgrow(leftPad, Priority.ALWAYS);

        sidebarLayout.setPrefWidth(SIDEBAR_WIDTH);


        sidebarLayout.setAlignment(Pos.BASELINE_CENTER);


        // used for testing resizing
        gameLayout.setStyle("-fx-background-color: red;"); 
        sidebarLayout.setStyle("-fx-background-color: blue;");
        



        // Filling the layout with buttons
        
        String id; // Later used to apply fx:id to buttons
        for (int x = 0; x < WIDTH; x++) {

            
            for (int y = 0; y < HEIGHT; y++) {
                Button btn = new Button();
                id = "rectangle";

                // the 4 following if statements are used to implement "mirrors" by coloring borders
                if((x+1)%4 == 0 && x!=WIDTH - 1){
                    id += "_right";
                }

                else if((x+1)%4 == 1 && x!= 0){
                    id += "_left";
                }

                
                if((y+1)%4 == 0 && y!=HEIGHT - 1){
                    id += "_bottom";
                }

                else if((y+1)%4 == 1 && y!=0){
                    id += "_top";
                }
                

                btn.setId(id);

                // sets base size of the buttons, actually useless
                btn.setPrefHeight(480/HEIGHT);
                btn.setPrefWidth(480/WIDTH);

                // makes buttons resize with window, and thus gameLayout, resizing
                btn.prefHeightProperty().bind(gameLayout.heightProperty().divide(WIDTH));
                btn.prefWidthProperty().bind(gameLayout.heightProperty().divide(HEIGHT));

                // changes the cursor when hovering over the button
                btn.setCursor(Cursor.HAND);
                
                // adds reference to the button to the array; and then adds it to the layout at correct positions
                rectangles[y * WIDTH + x] = btn;
                gameLayout.add(btn, x+1, y +1); // "+1" as I'll soon add labels at x = 0 and y = 0;

                // Rectangle input handling
                btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {

                    }
                });

                
            }
        }
        

        Button mainMenuButton = new Button("Main Menu");
        mainMenuButton.setId("main_menu_button");
        
        mainMenuButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try{
                App.setRoot("menu");
                return;
                }   catch (Exception e){;};
            }
        });
        
        sidebarLayout.getChildren().add(mainMenuButton);


        // applies .css styling to the scene
        App.scene.getStylesheets().addAll(this.getClass().getResource("game.css").toExternalForm());

        // changes root of the scene to wholeLayout - the "topmost" parent layer of the game "scene"
        App.setLayoutAsScene(wholeLayout);
    }

};
