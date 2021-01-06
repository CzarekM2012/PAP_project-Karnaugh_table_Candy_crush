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
    KarnaughTable karnaugh;

    final int START_TABLE_SIZE_X_BITS = 3; // How to split bits between x and y axis in table
    final int START_TABLE_SIZE_Y_BITS = 3;
    final int START_TABLE_VALUE_COUNT = 4; // How many logic values (tile colors) should be there
    final int MIN_PATTERN_SIZE = 4; // Pattern has to be at least this size to be scored
    final Set<ReplacementSource> replacementSourcesSet = new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top, ReplacementSource.Bottom }));
    
    final int SCENE_HEIGHT = 480;
    final int SCENE_WIDTH = 640;
    
    final int SIDEBAR_WIDTH = 160;
    final int GAME_WIDTH = SCENE_HEIGHT - SIDEBAR_WIDTH;

    final int WIDTH = 1 << START_TABLE_SIZE_X_BITS; // = 2^startTableSizeXBits
    final int HEIGHT = 1 << START_TABLE_SIZE_Y_BITS;

    final int SQUARE_SIZE = SCENE_HEIGHT/HEIGHT;

    public void startGame() throws IOException{
        System.out.println("Starting game.");
        karnaugh = new KarnaughTable(START_TABLE_SIZE_X_BITS, START_TABLE_SIZE_Y_BITS, START_TABLE_VALUE_COUNT, MIN_PATTERN_SIZE, replacementSourcesSet);
        // TOD O: rectangleList
        

        HBox wholeLayout = new HBox();
        GridPane gameLayout = new GridPane();
        VBox sidebarLayout = new VBox();
        wholeLayout.getChildren().addAll(gameLayout, sidebarLayout);




        wholeLayout.setMinWidth(SCENE_WIDTH);
        wholeLayout.setMaxWidth(SCENE_HEIGHT);

        gameLayout.setMinWidth(GAME_WIDTH);
        gameLayout.minWidthProperty().bind(wholeLayout.heightProperty());


        sidebarLayout.setPrefWidth(SIDEBAR_WIDTH);

        gameLayout.setStyle("-fx-background-color: red;");
        sidebarLayout.setStyle("-fx-background-color: blue;");


        //wholeLayout.setHgrow(gameLayout, Priority.ALWAYS);

        App.setLayoutAsScene(wholeLayout);
    }

};
