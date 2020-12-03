package Karnaugh.src;

import java.util.Map;

import javax.swing.text.Highlighter.Highlight;

import javafx.scene.control.Button;
import java.util.HashMap;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class App extends Application {

    // Karnaug table setup values
    static final int startTableSizeXBits = 3;
    static final int startTableSizeYBits = 3;
    static final int startTableValueCount = 3;

    static int WIDTH = 1 << startTableSizeXBits; // = 2^startTableSizeXBits
    static int HEIGHT = 1 << startTableSizeYBits;
    static int SQUARE_SIZE = 100;
    static int SCOREBAR_WIDTH = 150;

    static Coord lastSelectedTile = null;

    private static Map<Integer, Color> colorDict = new HashMap<>(); // can be later used for coloring squares
    KarnaughTable karnaugh; // Main karnaugh table, this is the core element of the game
    ArrayList<Coord> highlightedTiles = new ArrayList<>();


    public static void main(String[] args) {
        colorDict.put(0, Color.GREEN);
        colorDict.put(1, Color.BLUE);
        colorDict.put(2, Color.YELLOW);
        colorDict.put(3, Color.RED);
        launch(args);
    }

    public Rectangle[] rectangles = new Rectangle[WIDTH * HEIGHT];

    public Rectangle getRectangleAt(int xRctg, int yRctg) {
        // returns a reference to a rectangle on the board
        return rectangles[yRctg * WIDTH + xRctg];
    }

    @Override
    public void start(Stage primaryStage) {

        // Preparing window
        primaryStage.setTitle("Karnaugh");

        Pane gameLayout = new Pane();
        gameLayout.setPrefSize(WIDTH * SQUARE_SIZE, HEIGHT * SQUARE_SIZE);
        HBox wholeLayout = new HBox();
        wholeLayout.setPrefSize(WIDTH * SQUARE_SIZE + SCOREBAR_WIDTH, HEIGHT * SQUARE_SIZE);
        VBox scorebarLayout = new VBox();
        scorebarLayout.setPrefSize(SCOREBAR_WIDTH, HEIGHT * SQUARE_SIZE);

        
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

        Text score = new Text("00000000");
        score.setFont(new Font("Comic Sans", 24));

        scorebarLayout.setAlignment(Pos.BASELINE_CENTER);
        scorebarLayout.getChildren().add(menuButton);
        scorebarLayout.getChildren().add(scoreLabel);
        scorebarLayout.getChildren().add(score);

        wholeLayout.getChildren().add(gameLayout);
        wholeLayout.getChildren().add(scorebarLayout);
        Scene scene = new Scene(wholeLayout);

        primaryStage.setScene(scene);

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                Rectangle rctg = new Rectangle(SQUARE_SIZE, SQUARE_SIZE, Color.GREEN);
                rctg.setTranslateX(SQUARE_SIZE * x);
                rctg.setTranslateY(SQUARE_SIZE * y);
                rctg.setCursor(Cursor.HAND);
                rctg.setStroke(Color.BLACK);

                rectangles[y * WIDTH + x] = rctg;
                gameLayout.getChildren().add(rctg);

                rctg.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        int xOfRctg = (int) (event.getSceneX()) / SQUARE_SIZE; // x coordinate of the rectangle
                        int yOfRctg = (int) (event.getSceneY()) / SQUARE_SIZE; // y -|| -
                        // ^those tell you which rectangle was pressed, useful for implementing game
                        // mechanics
                        System.out.println("Clicked (" + xOfRctg + ", " + yOfRctg + ")");
                        onTileSelected(xOfRctg, yOfRctg);
                    }
                });
            }
        }

        //getRectangleAt(1, 1).setFill(Color.BEIGE); // example use of getRectangleAt()
        //getRectangleAt(WIDTH - 2, HEIGHT - 2).setFill(Color.BEIGE); // example use of getRectangleAt()

        // Initialising mechanics
        karnaugh = new KarnaughTable(startTableSizeXBits, startTableSizeYBits, startTableValueCount);
        updateTable();

        primaryStage.show();
    }

    void setTileColor(int x, int y, Color color) {
        getRectangleAt(x, y).setFill(color);
    }

    void highlightTile(int x, int y) {
        System.out.println("Highlighing " + x + ", " + y);
        highlightedTiles.add(new Coord(x, y));

        Rectangle tile = getRectangleAt(x, y);
        Color color = (Color)tile.getFill();
        color = color.desaturate();
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
        
        if(lastSelectedTile == null) {
            lastSelectedTile = new Coord(x, y);
            //highlightTile(x, y);
            highlightNeighbours(x, y);
            return;
        }

        //highlightTile(x, y);
        trySwapTiles(new Coord(lastSelectedTile), new Coord(x, y));
        lastSelectedTile = null;
    }

    void trySwapTiles(Coord firstTile, Coord secondTile) {
        //highlightNeighbours(firstTile.x, firstTile.y);

        removeHighlights();
    }

}