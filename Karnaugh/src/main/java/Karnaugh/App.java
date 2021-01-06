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

public class App extends Application {

    // Karnaugh table setup values
    static final int START_TABLE_SIZE_X_BITS = 3; // How to split bits between x and y axis in table
    static final int START_TABLE_SIZE_Y_BITS = 3;
    static final int START_TABLE_VALUE_COUNT = 4; // How many logic values (tile colors) should be there
    static final int MIN_PATTERN_SIZE = 4; // Pattern has to be at least this size to be scored
    static final Set<ReplacementSource> replacementSourcesSet = new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top, ReplacementSource.Bottom }));//TODO:miało być na zewnątrz, więc jest tutaj

    // Default UI element sizes and offsets
    static int WIDTH = 1 << START_TABLE_SIZE_X_BITS; // = 2^startTableSizeXBits
    static int HEIGHT = 1 << START_TABLE_SIZE_Y_BITS;
    static int SQUARE_SIZE = 100;
    static int MIRROR_SIZE = 5;
    static int MIRROR_FREQUENCY = 4; // mirrors appear every 4 squares, but I've put it as a variable in case I'm wrong
    static int SCOREBAR_WIDTH = 150;
    static int BASE_X_OFFSET = 75;
    static int BASE_Y_OFFSET = 75;
    static int BOTTOM_PAD = 75;
    final static int ANIMATION_DELAY = 50;

    // Input handling
    static Coord lastSelectedTile = null;
    static boolean lockClicking = false;

    // Main data structures
    KarnaughTable karnaugh;                                             // Main karnaugh table, the core element of the game
    public static Map<Integer, String> colorDict = new HashMap<>();     // Tile value-to-color map
    ArrayList<Coord> highlightedTiles = new ArrayList<>();

    // Score
    Text scoreTextField;
    int score = 0;

    // .fxml scene
    public static Scene scene;


    static void setLayoutAsScene(Pane layout){ // Every pane/layout derives from Pane
        scene.setRoot(layout);
    }

    static void setRoot(String fxml) throws IOException {
         scene.setRoot(loadFXML(fxml));
    }


    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        colorDict.put(0, "577590");
        colorDict.put(1, "90be6d");
        colorDict.put(2, "f8961e");
        colorDict.put(3, "f94144");
        colorDict.put(4, "48cae4");
        colorDict.put(5, "f9c74f");
        colorDict.put(6, "f3722c");
        launch(args);
    }

    // Table fields' graphical representation
    public Rectangle[] rectangles = new Rectangle[WIDTH * HEIGHT];

    // Returns a reference to a rectangle on the board
    public Rectangle getRectangleAt(int xRctg, int yRctg) {return rectangles[yRctg * WIDTH + xRctg];}


    @Override
    public void start(Stage primaryStage) throws IOException {
        // Preparing window
        primaryStage.setTitle("Karnaugh");
        primaryStage.setMinWidth(640);
        primaryStage.setMinHeight(480);

        primaryStage.minWidthProperty().bind(primaryStage.heightProperty().multiply(4/3));

        scene = new Scene(loadFXML("menu"), 640, 480);
        primaryStage.setScene(scene);  
        
    /*  Reference on how to add properties to nodes by fxid
        scene.lookup("#startButton").setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.print("AAAAAAAAAAAAAAAAAAA");
            }
        });
    */
        primaryStage.show();
    }
     
    static public void showHighscores(){

    } 

    static public void quitGame(){
        Platform.exit();
    }

}