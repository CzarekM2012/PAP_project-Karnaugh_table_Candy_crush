package karnaugh;

import java.util.ArrayList;
import java.util.Map;

import java.util.HashMap;

import javafx.application.Application;
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
    private static Map<Integer, Color> colorDict = new HashMap<>();     // Tile value-to-color map
    ArrayList<Coord> highlightedTiles = new ArrayList<>();

    // Score
    Text scoreTextField;
    int score = 0;

    // .fxml scene
    //private static Scene scene;

    static void setRoot(String fxml) throws IOException {
         //scene.setRoot(loadFXML(fxml));
    }

    // private static Parent loadFXML(String fxml) throws IOException {
    //     FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
    //     return fxmlLoader.load();
    // }

    public static void main(String[] args) {
        colorDict.put(0, Color.web("577590"));
        colorDict.put(1, Color.web("90be6d"));
        colorDict.put(2, Color.web("f8961e"));
        colorDict.put(3, Color.web("f94144"));
        colorDict.put(4, Color.web("48cae4"));
        colorDict.put(5, Color.web("f9c74f"));
        colorDict.put(6, Color.web("f3722c"));
        launch(args);
    }

    // Table fields' graphical representation
    public Rectangle[] rectangles = new Rectangle[WIDTH * HEIGHT];

    // Returns a reference to a rectangle on the board
    public Rectangle getRectangleAt(int xRctg, int yRctg) {return rectangles[yRctg * WIDTH + xRctg];}

    @Override
    public void start(Stage primaryStage) throws IOException {

        // .fxml setup
        
        //scene = new Scene(loadFXML("primary"), 640, 480);
        //stage.setScene(scene);
        //stage.show();

        // Initialising mechanics
        karnaugh = new KarnaughTable(START_TABLE_SIZE_X_BITS, START_TABLE_SIZE_Y_BITS, START_TABLE_VALUE_COUNT, MIN_PATTERN_SIZE);
        //karnaugh.print();

        // Preparing window
        primaryStage.setTitle("Karnaugh");

        Pane gameLayout = new Pane();
        gameLayout.setPrefSize(BASE_X_OFFSET + WIDTH * SQUARE_SIZE + (WIDTH/MIRROR_FREQUENCY - 1)* MIRROR_SIZE,
                               BASE_Y_OFFSET + HEIGHT * SQUARE_SIZE + (HEIGHT/MIRROR_FREQUENCY - 1)*MIRROR_SIZE + BOTTOM_PAD);

        VBox scorebarLayout = new VBox();
        scorebarLayout.setPrefSize(SCOREBAR_WIDTH, HEIGHT * SQUARE_SIZE);

        HBox wholeLayout = new HBox();
        wholeLayout.setPrefSize(gameLayout.getPrefWidth() + scorebarLayout.getPrefWidth(), Math.max(gameLayout.getPrefHeight(), scorebarLayout.getPrefHeight()));
        
        // Creating layout elements
        Button menuButton = new Button("Main menu");
        menuButton.setFont(new Font("Arial", 15));
        menuButton.setPrefWidth(0.8 * SCOREBAR_WIDTH);
        menuButton.setTextFill(Color.WHITE);
        menuButton.setCursor(Cursor.HAND);
        menuButton.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(0), Insets.EMPTY))); // makes the button red, could probably make it easier with css 

        menuButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("Main menu button clicked.");
                // currently unused, as there's no main menu yet
            }
        });

        Text scoreLabel = new Text("Score:");
        scoreLabel.setFont(new Font("Arial", 35));

        scoreTextField = new Text("");
        scoreTextField.setFont(new Font("Comic Sans", 24));
        updateScore();

        scorebarLayout.setAlignment(Pos.BASELINE_CENTER);
        scorebarLayout.getChildren().add(menuButton);
        scorebarLayout.getChildren().add(scoreLabel);
        scorebarLayout.getChildren().add(scoreTextField);

        wholeLayout.getChildren().add(gameLayout);
        wholeLayout.getChildren().add(scorebarLayout);
        Scene scene = new Scene(wholeLayout);

        primaryStage.setScene(scene);


        // Filling the layout with squares
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                Rectangle rctg = new Rectangle(SQUARE_SIZE, SQUARE_SIZE, Color.GREEN);
                rctg.setTranslateX(BASE_X_OFFSET + SQUARE_SIZE * x + x/MIRROR_FREQUENCY * MIRROR_SIZE);
                rctg.setTranslateY(BASE_Y_OFFSET + SQUARE_SIZE * y + y/MIRROR_FREQUENCY * MIRROR_SIZE);
                rctg.setCursor(Cursor.HAND);
                rctg.setStroke(Color.BLACK);

                rectangles[y * WIDTH + x] = rctg;
                gameLayout.getChildren().add(rctg);

                // Rectangle input handling
                rctg.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if(lockClicking)
                            return;
                        lockClicking = true;
                        
                        // These coords tell which rectangle was pressed
                        final int xOfRctg = (int) ((event.getSceneX() - BASE_X_OFFSET)  / SQUARE_SIZE); // x coordinate of the rectangle
                        final int yOfRctg = (int) ((event.getSceneY() - BASE_Y_OFFSET)  / SQUARE_SIZE); // y ...
                        //System.out.println("Clicked (" + xOfRctg + ", " + yOfRctg + ")");

                        // Game functions are started in a separate thread to keep the UI responsive
                        Task<Void> task = new Task<Void>() {
                            @Override
                            protected Void call() throws Exception {
                                onTileSelected(xOfRctg, yOfRctg);
                                return null;
                            }
                        };
                        
                        new Thread(task).start();
                    }
                });
            }
        }


        // Adding row and column labels
        
        // Column labels
        for(int x = 0; x < WIDTH; x++){
            TextField txt = new TextField((String.format("%" + START_TABLE_SIZE_X_BITS + "s", Integer.toBinaryString(karnaugh.translateIndexToGrey(x)))).replace(" ", "0"));
            txt.setEditable(false);
            
            txt.setPrefSize(SQUARE_SIZE, BASE_Y_OFFSET - 1);

            txt.setFont(new Font("Arial", 15));
            txt.setAlignment(Pos.CENTER);

            txt.setTranslateX(BASE_X_OFFSET + x*SQUARE_SIZE + x/MIRROR_FREQUENCY * MIRROR_SIZE);

            gameLayout.getChildren().add(txt);
        }

        // Row labels
        for(int y = 0; y < WIDTH; y++){
            TextField txt = new TextField((String.format("%" + START_TABLE_SIZE_Y_BITS + "s", Integer.toBinaryString(karnaugh.translateIndexToGrey(y)))).replace(" ", "0"));
            txt.setEditable(false);

            txt.setPrefSize(BASE_X_OFFSET - 1, SQUARE_SIZE);
            
            txt.setFont(new Font("Arial", 15));
            txt.setAlignment(Pos.CENTER);

            txt.setTranslateY(BASE_Y_OFFSET + y*SQUARE_SIZE + y/MIRROR_FREQUENCY * MIRROR_SIZE);

            gameLayout.getChildren().add(txt);
        }

        updateTable();
        primaryStage.show();
    }


    void setTileColor(int x, int y, Color color) {
        getRectangleAt(x, y).setFill(color);
    }

    // Highlights a tile by desaturating it. Highlighted tiles can be cleared using removeHighlights()
    void highlightTile(Coord tile) {highlightTile(tile.x, tile.y);}
    void highlightTile(int x, int y) {
        //System.out.println("Highlighing " + x + ", " + y);
        highlightedTiles.add(new Coord(x, y));

        Rectangle tile = getRectangleAt(x, y);
        Color color = (Color)tile.getFill();
        color = color.desaturate();
        color = color.desaturate();
        tile.setFill(color);
    }

    void highlightTiles(ArrayList<Coord> coords) {
        for(Coord tile : coords)
            highlightTile(tile.x, tile.y);
    }

    void highlightNeighbours(int x, int y) {
        highlightTiles(karnaugh.adjacentFields(new Coord(x, y)));
    }

    void removeHighlights() {
        for(Coord tile : highlightedTiles)
            updateTile(tile); // reset color
        highlightedTiles.clear();
    }

    // Redraws a tile with given coordinates
    void updateTile(Coord coord) {updateTile(coord.x, coord.y);}
    void updateTile(int x, int y) {
        Color color = colorDict.get(karnaugh.getTileValue(x, y));
        setTileColor(x, y, color);
    }

    void updateTiles(ArrayList<Coord> coords) {
        for(Coord coord : coords)
            updateTile(coord);
    }

    // Redraws whole table
    void updateTable() {
        for(int x = 0; x < karnaugh.getSizeX(); ++x)
            for(int y = 0; y < karnaugh.getSizeY(); ++y)
                updateTile(x, y);
    }

    // Input processing function
    // decides when to swap tiles
    void onTileSelected(int x, int y) {

        if(lastSelectedTile == null) {
            lastSelectedTile = new Coord(x, y);
            highlightNeighbours(x, y);
            lockClicking = false;
            return;
        }

        trySwapTiles(new Coord(lastSelectedTile), new Coord(x, y));
        lastSelectedTile = null;
    }


    void trySwapTiles(Coord firstTile, Coord secondTile) {
        removeHighlights();
        
        // Makes sure that tiles are swapped only "1 bit away"
        if(!karnaugh.adjacentFields(firstTile).contains(secondTile)){
            lockClicking = false;
            return;
        }

        ArrayList<Coord> tilesToDestroy;
        ArrayList<Coord> movedTiles = new ArrayList<Coord>();
        movedTiles.add(new Coord(firstTile.x, firstTile.y));
        movedTiles.add(new Coord(secondTile.x, secondTile.y));

        // we have to make sure that after the move, DESTRUCTION occurs

        karnaugh.swapTiles(firstTile, secondTile); // firstly: we swap the tiles in the table's logical model but don't update the gui yet
       

        // Check whether, after the swap that occured in the logical model, there are any DESTRUCTION possibilities
        if(karnaugh.fieldsToDestroy(firstTile).size() == 0 && karnaugh.fieldsToDestroy(secondTile).size() == 0){
            // if not - reverse the swap as if nothing happened
            karnaugh.swapTiles(firstTile, secondTile);
            sleep(ANIMATION_DELAY);
            lockClicking = false;
            return;
        }

        // else acknowledge the swap in the logical model, update the gui and go on to DESTROY

        // Destroy patterns until none remain
        do {
            // Seek
            tilesToDestroy = new ArrayList<Coord>();
            for(Coord tile : movedTiles) {
                int lastSize = tilesToDestroy.size();
                
                ArrayList<Coord> toAdd = karnaugh.fieldsToDestroy(tile);
                toAdd.removeAll(tilesToDestroy); // Removing duplicates
                tilesToDestroy.addAll(toAdd);

                // Only show updates if something actually changed
                if(lastSize != tilesToDestroy.size()) {
                    removeHighlights();
                    //KarnaughTable.printTiles(tilesToDestroy);
                    highlightTiles(tilesToDestroy);
                    sleep(ANIMATION_DELAY);
                }
            }

            
            updateTable();

            addScore(tilesToDestroy.size());
            removeHighlights();

            // Destroy
            //KarnaughTable.printTiles(tilesToDestroy);
            karnaugh.destroyFields(tilesToDestroy);
            updateTable();
            sleep(ANIMATION_DELAY);
            
            // Collapse
            movedTiles = karnaugh.collapseGetTiles(ReplacementSource.Top);
            //KarnaughTable.printTiles(movedTiles);
            updateTable();
            sleep(ANIMATION_DELAY);

            highlightTiles(movedTiles);
            sleep(ANIMATION_DELAY);
            removeHighlights();

            // Refill
            movedTiles.addAll(karnaugh.fillWithRandoms());
            updateTable();
            sleep(ANIMATION_DELAY);
            removeHighlights();

            System.out.println("TEST");

        } while(!tilesToDestroy.isEmpty());

        lockClicking = false;

    }

    // Adds a value to score, more of a placeholder for now
    void addScore(int value) {
        score += value;
        updateScore();
    }

    void updateScore() {
        scoreTextField.setText(Integer.toString(score));
    }

    // Just a tiny function to make code cleaner
    void sleep(int timeMs) {
        try {
            Thread.sleep(timeMs);
        } catch(Exception e) {};
    }

}