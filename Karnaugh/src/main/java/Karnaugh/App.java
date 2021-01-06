package karnaugh;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;

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

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.Properties;
import java.io.IOException;
import java.util.Scanner;

public class App extends Application {

    // Level-related values
    String levelName;
    int tableBitSizeX;                      // How to split bits between x and y axis in table
    int tableBitSizeY;
    int tableWidth = 1 << tableBitSizeX;    // = 2^startTableSizeXBits
    int tableHeight = 1 << tableBitSizeY;
    int tableValueCount;                    // How many logic values (tile colors) should be there
    int gravityType;                        // How Gravity should be handled
    int minPatternSize;                     // Pattern has to be at least this size to be scored
    float wildFieldChance;
    Set<ReplacementSource> replacementSourcesSet;

    // Default UI element sizes and offsets
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

    // File I/O
    String levelFolderPath = "config/levels";

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

        // Load config

        // Properties prop = new Properties();
        // InputStream input = null;

        // try {

        //     input = new FileInputStream("config.properties");

        //     // load a properties file
        //     prop.load(input);

        //     // get the property value and print it out
        //     System.out.println(prop.setProperty("conf_1", "1"));

        // } catch (IOException ex) {
        //     ex.printStackTrace();
        // } finally {
        //     if (input != null) {
        //         try {
        //             input.close();
        //         } catch (IOException e) {
        //             e.printStackTrace();
        //         }
        //     }
        // }

        launch(args);
    }

    // Loads level from file, resets game, creates and redraws the table
    void loadLevel(int fileId) {
        final File levelFolder = new File(levelFolderPath);
        System.out.println(levelFolder.getAbsolutePath());

        try {
            loadLevelDataFromFile(listFilesForFolder(levelFolder).get(fileId));
        }
        catch (IOException e) {
            System.out.println("ERROR - No levels found. Exiting");
            return;
        }
    }

    // Loads all level data into local variables
    void loadLevelDataFromFile(final File file) throws IOException {

        HashMap<String, String> values = new HashMap<String, String>();
        HashMap<Coord, String> specialTiles = new HashMap<Coord, String>();
        ArrayList<Integer> tileRarityWeights = new ArrayList<Integer>();
        
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            String[] line = scanner.nextLine().split(" = ");
            switch(line[0]) {
                case "special tiles:":
                    for(int i = 0; i < Integer.parseInt(values.get("special tile count")); ++i) {
                        line = scanner.nextLine().split(" ");
                        Coord coord = new Coord(Integer.parseInt(line[0]), Integer.parseInt(line[1]));
                        specialTiles.put(coord, line[2]);
                    }
                    break;
                case "tile probabilities:":
                    for(int i = 0; i < Integer.parseInt(values.get("tile type count")); ++i)
                        tileRarityWeights.add(Integer.parseInt(scanner.nextLine()));
                    break;
                default:
                    if(line.length >= 2)
                        values.put(line[0], line[1]);
            }
        }

        scanner.close();
        
        levelName = values.get("name");
        tableBitSizeX = Integer.parseInt(values.get("bitSizeX")); 
        tableBitSizeY = Integer.parseInt(values.get("bitSizeY"));
        tableValueCount = Integer.parseInt(values.get("tile type count"));
        minPatternSize = Integer.parseInt(values.get("min pattern size"));
        gravityType = Integer.parseInt(values.get("gravity type"));
        wildFieldChance = Float.parseFloat(values.get("wild tile frequency"));

        System.out.println(levelName);
        
        tableWidth = 1 << tableBitSizeX;
        tableHeight = 1 << tableBitSizeY;

        rectangles = new Rectangle[tableWidth * tableHeight];

        // Unused for now
        replacementSourcesSet = new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top, ReplacementSource.Bottom }));
        
    }

    ArrayList<File> listFilesForFolder(final File folder) throws IOException {
        ArrayList<File> result = new ArrayList<File>();
        for (final File file : folder.listFiles()) {
            if (file.isDirectory()) {
                listFilesForFolder(file);
            } else {
                result.add(file);
            }
        }
        return result;
    }

    // Table fields' graphical representation
    public Rectangle[] rectangles;

    // Returns a reference to a rectangle on the board
    public Rectangle getRectangleAt(int xRctg, int yRctg) {return rectangles[yRctg * tableWidth + xRctg];}

    @Override
    public void start(Stage primaryStage) throws IOException {

        // .fxml setup
        
        //scene = new Scene(loadFXML("primary"), 640, 480);
        //stage.setScene(scene);
        //stage.show();

        // Initialising mechanics
        loadLevel(0);
        karnaugh = new KarnaughTable(tableBitSizeX, tableBitSizeY, tableValueCount, minPatternSize, wildFieldChance, replacementSourcesSet);//TODO:podaje set
        //karnaugh.print();

        // Preparing window
        primaryStage.setTitle("Karnaugh");

        Pane gameLayout = new Pane();
        gameLayout.setPrefSize(BASE_X_OFFSET + tableWidth * SQUARE_SIZE + (tableWidth/MIRROR_FREQUENCY - 1)* MIRROR_SIZE,
                               BASE_Y_OFFSET + tableHeight * SQUARE_SIZE + (tableHeight/MIRROR_FREQUENCY - 1)*MIRROR_SIZE + BOTTOM_PAD);

        VBox scorebarLayout = new VBox();
        scorebarLayout.setPrefSize(SCOREBAR_WIDTH, tableHeight * SQUARE_SIZE);

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
        for (int x = 0; x < tableWidth; x++) {
            for (int y = 0; y < tableHeight; y++) {
                Rectangle rctg = new Rectangle(SQUARE_SIZE, SQUARE_SIZE, Color.GREEN);
                rctg.setTranslateX(BASE_X_OFFSET + SQUARE_SIZE * x + x/MIRROR_FREQUENCY * MIRROR_SIZE);
                rctg.setTranslateY(BASE_Y_OFFSET + SQUARE_SIZE * y + y/MIRROR_FREQUENCY * MIRROR_SIZE);
                rctg.setCursor(Cursor.HAND);
                rctg.setStroke(Color.BLACK);

                rectangles[y * tableWidth + x] = rctg;
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
        for(int x = 0; x < tableWidth; x++){
            TextField txt = new TextField((String.format("%" + tableBitSizeX + "s", Integer.toBinaryString(karnaugh.translateIndexToGrey(x)))).replace(" ", "0"));
            txt.setEditable(false);
            
            txt.setPrefSize(SQUARE_SIZE, BASE_Y_OFFSET - 1);

            txt.setFont(new Font("Arial", 15));
            txt.setAlignment(Pos.CENTER);

            txt.setTranslateX(BASE_X_OFFSET + x*SQUARE_SIZE + x/MIRROR_FREQUENCY * MIRROR_SIZE);

            gameLayout.getChildren().add(txt);
        }

        // Row labels
        for(int y = 0; y < tableWidth; y++){
            TextField txt = new TextField((String.format("%" + tableBitSizeY + "s", Integer.toBinaryString(karnaugh.translateIndexToGrey(y)))).replace(" ", "0"));
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