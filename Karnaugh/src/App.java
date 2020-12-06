package Karnaugh.src;

import java.util.ArrayList;
import java.util.Map;

import java.util.HashMap;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.concurrent.WorkerStateEvent;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class App extends Application {

    // Karnaugh table setup values
    static final int startTableSizeXBits = 3;
    static final int startTableSizeYBits = 3;
    static final int startTableValueCount = 4;

    static final int MIN_PATTERN_SIZE = 4;

    static int WIDTH = 1 << startTableSizeXBits; // = 2^startTableSizeXBits
    static int HEIGHT = 1 << startTableSizeYBits;
    static int SQUARE_SIZE = 100;
    static int MIRROR_SIZE = 5;
    static int MIRROR_FREQUENCY = 4; // mirrors appear every 4 squares, but I've put it as a variable in case I'm wrong
    static int SCOREBAR_WIDTH = 150;
    static int BASE_X_OFFSET = 50;
    static int BASE_Y_OFFSET = 50;
    static int BOTTOM_PAD = 50;
    final static int ANIMATION_DELAY = 50;

    static Coord lastSelectedTile = null;
    static boolean lockClicking = false;

    private static Map<Integer, Color> colorDict = new HashMap<>(); // can be later used for coloring squares
    KarnaughTable karnaugh; // Main karnaugh table, this is the core element of the game
    ArrayList<Coord> highlightedTiles = new ArrayList<>();

    Text scoreTextField;
    int score = 0;

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

    public Rectangle[] rectangles = new Rectangle[WIDTH * HEIGHT];

    public Rectangle getRectangleAt(int xRctg, int yRctg) {
        // returns a reference to a rectangle on the board
        return rectangles[yRctg * WIDTH + xRctg];
    }

    @Override
    public void start(Stage primaryStage) {

        // Initialising mechanics
        karnaugh = new KarnaughTable(startTableSizeXBits, startTableSizeYBits, startTableValueCount);

        // Preparing window
        primaryStage.setTitle("Karnaugh");

        Pane gameLayout = new Pane();
        gameLayout.setPrefSize(BASE_X_OFFSET + WIDTH * SQUARE_SIZE + (WIDTH/MIRROR_FREQUENCY - 1)* MIRROR_SIZE, BASE_Y_OFFSET + HEIGHT * SQUARE_SIZE + (HEIGHT/MIRROR_FREQUENCY - 1)*MIRROR_SIZE + BOTTOM_PAD);

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
        // makes the button red, could probably make it easier with css
        menuButton.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(0), Insets.EMPTY))); 

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


        /****************************************************
        *       FILLING THE LAYOUT WITH SQUARES
        ****************************************************/
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                Rectangle rctg = new Rectangle(SQUARE_SIZE, SQUARE_SIZE, Color.GREEN);
                rctg.setTranslateX(BASE_X_OFFSET + SQUARE_SIZE * x + x/MIRROR_FREQUENCY * MIRROR_SIZE);
                rctg.setTranslateY(BASE_Y_OFFSET + SQUARE_SIZE * y + y/MIRROR_FREQUENCY * MIRROR_SIZE);
                rctg.setCursor(Cursor.HAND);
                rctg.setStroke(Color.BLACK);

                rectangles[y * WIDTH + x] = rctg;
                gameLayout.getChildren().add(rctg);

                rctg.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if(lockClicking)
                            return;
                        lockClicking = true;
                        
                        int xOfRctg = (int) ((event.getSceneX() - BASE_X_OFFSET)  / SQUARE_SIZE); // x coordinate of the rectangle
                        int yOfRctg = (int) ((event.getSceneY() - BASE_Y_OFFSET)  / SQUARE_SIZE); // y -|| -
                        // ^those tell you which rectangle was pressed, useful for implementing game
                        // mechanics
                        System.out.println("Clicked (" + xOfRctg + ", " + yOfRctg + ")");
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

        /***************************************************
        *       ADDING ROW/COLUMN LABELS 
        ***************************************************/
        //horizontal
        for(int x = 0; x < WIDTH; x++){
            TextField txt = new TextField((String.format("%" + startTableSizeXBits + "s", Integer.toBinaryString(karnaugh.translateIndexToGrey(x)))).replace(" ", "0"));
            txt.setEditable(false);
            txt.setMaxWidth(SQUARE_SIZE);
            txt.setFont(new Font("Arial", 15));
            txt.setAlignment(Pos.CENTER);

            txt.setTranslateX(BASE_X_OFFSET + x*SQUARE_SIZE + x/MIRROR_FREQUENCY * MIRROR_SIZE);

            gameLayout.getChildren().add(txt);
        }


        updateTable();
        primaryStage.show();
    }


    void setTileColor(int x, int y, Color color) {
        getRectangleAt(x, y).setFill(color);
    }

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
        highlightTiles(karnaugh.AdjacentFields(new Coord(x, y)));
    }

    void removeHighlights() {
        for(Coord tile : highlightedTiles)
            updateTile(tile); // reset color
        highlightedTiles.clear();
    }

    void removeHighlightsDelay(int ms) {
        Task<Void> rmHighlightDelay = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Thread.sleep(ms);
                } catch (InterruptedException e) {
                }
                return null;
            }
        };
        rmHighlightDelay.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                removeHighlights();
            }
        });
        new Thread(rmHighlightDelay).start();
    }

    void updateTile(int x, int y) {
        Color color = colorDict.get(karnaugh.getTileValue(x, y));
        setTileColor(x, y, color);
    }

    void updateTile(Coord coord) {
        updateTile(coord.x, coord.y);
    }

    void updateTiles(ArrayList<Coord> coords) {
        for(Coord coord : coords) {
            updateTile(coord);
        }
    }

    void updateTable() {
        for(int x = 0; x < karnaugh.getSizeX(); ++x)
            for(int y = 0; y < karnaugh.getBitSizeY(); ++y)
                updateTile(x, y);
    }

    void onTileSelected(int x, int y) {

        // for(ArrayList<Coord> pattern : karnaugh.getPatternsContaining(new Coord(x, y), 2)) {
        //     highlightTiles(pattern);
        //     karnaugh.printTiles(pattern);
        //     removeHighlightsDelay(500);
        //     try {
        //         Thread.sleep(600);
        //     } catch(Exception e) {};
        // }
        



        if(lastSelectedTile == null) {
            lastSelectedTile = new Coord(x, y);
            //highlightTile(x, y);
            //highlightTiles(karnaugh.FieldsToDestroy(new Coord(x, y), 2));
            highlightNeighbours(x, y);
            lockClicking = false;
            return;
        }

        //highlightTile(x, y);
        trySwapTiles(new Coord(lastSelectedTile), new Coord(x, y));
        lastSelectedTile = null;
    }

    static boolean firstIteration = true;
    void trySwapTiles(Coord firstTile, Coord secondTile) {
        removeHighlights();
        
        //Makes sure you can only move to the positions "1 bit away"
        if(!karnaugh.AdjacentFields(firstTile).contains(secondTile)){
            lockClicking = false;
            return;
        }

        ArrayList<Coord> tilesToDestroy;
        ArrayList<Coord> movedTiles = new ArrayList<Coord>();
        movedTiles.add(new Coord(firstTile.x, firstTile.y));
        movedTiles.add(new Coord(secondTile.x, secondTile.y));

        // we have to make sure that after the move, DESTRUCTION occurs

        karnaugh.swapTiles(firstTile, secondTile); // firstly: we swap the tiles in the table's logical model but don't update the gui yet
       
        // Destroy patterns until none remain
        do {
            // Seek
            tilesToDestroy = new ArrayList<Coord>();
            for(Coord tile : movedTiles) {
                int lastSize = tilesToDestroy.size();
                
                ArrayList<Coord> toAdd = karnaugh.FieldsToDestroy(tile, MIN_PATTERN_SIZE);
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

            // then we check whether, after the swap that occured in the logical model, there are any DESTRUCTION possibilities
            if(tilesToDestroy.size() == 0 && firstIteration){
                // if not - we reverse the swap as if nothing happened
                karnaugh.swapTiles(firstTile, secondTile);
                sleep(ANIMATION_DELAY);
            }

            // else we acknowledge the swap in the logical model, update the gui and go on to DESTROY
            updateTable();

            addScore(tilesToDestroy.size());
            removeHighlights();

            // Destroy
            //KarnaughTable.printTiles(tilesToDestroy);
            karnaugh.DestroyFields(tilesToDestroy);
            updateTable();
            sleep(ANIMATION_DELAY);
            
            // Collapse
            movedTiles = karnaugh.collapseGetTiles(1);
            //KarnaughTable.printTiles(movedTiles);
            updateTable();
            sleep(ANIMATION_DELAY);

            highlightTiles(movedTiles);
            sleep(ANIMATION_DELAY);
            removeHighlights();

            // Refill
            karnaugh.FillWithRandoms();
            updateTable();
            sleep(ANIMATION_DELAY);
            removeHighlights();

            firstIteration = false;
        } while(!tilesToDestroy.isEmpty());

        firstIteration = true;
        lockClicking = false;

    }

    void addScore(int value) {
        score += value;
        updateScore();
    }

    void updateScore() {
        scoreTextField.setText(Integer.toString(score));
    }

    void sleep(int timeMs) {
        try {
            Thread.sleep(timeMs);
        } catch(Exception e) {};
    }

}