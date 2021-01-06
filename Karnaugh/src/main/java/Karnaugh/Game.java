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
    final int START_TABLE_SIZE_X_BITS = 3; // How to split bits between x and y axis in table
    final int START_TABLE_SIZE_Y_BITS = 3;
    final int START_TABLE_VALUE_COUNT = 4; // How many logic values (tile colors) should be there
    final int MIN_PATTERN_SIZE = 4; // Pattern has to be at least this size to be scored
    final int ANIMATION_DELAY = 50;
    
    final Set<ReplacementSource> replacementSourcesSet = new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top, ReplacementSource.Bottom }));

    
    final int SIDEBAR_WIDTH = 160; // Maximal width of the sidebar containing main menu button, score, etc.

    final int WIDTH = 1 << START_TABLE_SIZE_X_BITS; // = 2^startTableSizeXBits
    final int HEIGHT = 1 << START_TABLE_SIZE_Y_BITS;

    int score;


    // Input handling
    static Coord lastSelectedTile = null;
    static boolean lockClicking = false;


    KarnaughTable karnaugh; // represents logic beneath the game
    public static Map<Integer, String> colorDict = App.colorDict;    

    // array containing all buttons in the playable field
    Button[] rectangles = new Button[WIDTH * HEIGHT];

    ArrayList<Coord> highlightedTiles = new ArrayList<>();
    // Returns a reference to a rectangle on the board
    public Button getRectangleAt(int xRctg, int yRctg) {return rectangles[yRctg * WIDTH + xRctg];}



    public void startGame() throws IOException{
        score = 0;
        System.out.println("Starting game.");
        karnaugh = new KarnaughTable(START_TABLE_SIZE_X_BITS, START_TABLE_SIZE_Y_BITS, START_TABLE_VALUE_COUNT, MIN_PATTERN_SIZE, replacementSourcesSet);
        

        

        HBox wholeLayout = new HBox(); // "topmost" layout; later used as the new root

        // Put there to make padding easier; a more clever solution almost certainly exists, but searching the net for answers started giving me flashbacks after doing it thousands of times
        Pane leftPad = new Pane();
        Pane rightPad = new Pane();
        
        GridPane gameLayout = new GridPane(); // rectangle containing more rectangles (buttons actually), game happens there
        VBox sidebarLayout = new VBox(); // main menu button, score, etc.

        wholeLayout.getChildren().addAll(leftPad, gameLayout, sidebarLayout, rightPad); // Adds all "sublayouts" to the "main layout" in order


        gameLayout.setId("game");
        sidebarLayout.setId("sidebar");
        
        // thanks to these scaling works. I think
        gameLayout.minWidthProperty().bind(App.scene.heightProperty());
        leftPad.prefWidthProperty().bind(rightPad.prefWidthProperty());
        
        wholeLayout.setHgrow(rightPad, Priority.ALWAYS);
        wholeLayout.setHgrow(leftPad, Priority.ALWAYS);

        sidebarLayout.setMinWidth(SIDEBAR_WIDTH);
        sidebarLayout.setPrefWidth(SIDEBAR_WIDTH);


        sidebarLayout.setAlignment(Pos.BASELINE_CENTER);


        // used for testing resizing
        // gameLayout.setStyle("-fx-background-color: red;"); 
        // sidebarLayout.setStyle("-fx-background-color: blue;");
        



        // Filling the layout with buttons
        
        String id; // Later used to apply fx:id to buttons
        for (int x = 0; x < WIDTH; x++){

            
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
                        if(lockClicking)
                            return;
                        lockClicking = true;
                        
                        // These coords tell which rectangle was pressed
                        final int xOfRctg = (int) (((event.getSceneX()) - leftPad.getWidth())/ btn.getWidth()); // x coordinate of the rectangle
                        final int yOfRctg = (int) ((event.getSceneY())/ btn.getHeight()); 
            
                        System.out.println("Button at (" + xOfRctg +", " + yOfRctg + ") clicked.");
						
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

        updateTable();

    }


    public String getColorFromStyle(String style){
        return style.substring(style.length() - 7,style.length() - 1);
    }

    public String getTileColor(int x, int y){
        return getColorFromStyle(getRectangleAt(x, y).getStyle());
    }

    void setTileColor(int x, int y, String color) {
        getRectangleAt(x, y).setStyle("-fx-background-color: #" + color + ";");
    }

    // Highlights a tile by desaturating it. Highlighted tiles can be cleared using removeHighlights()
    void highlightTile(Coord tile) {highlightTile(tile.x, tile.y);}

    String getHighligthedColor(String hexCode){
        Color color = Color.web("0x" + hexCode);
        color = color.desaturate();
        color = color.desaturate();
        //TODO: check if works
        return color.toString().substring(2, 8);
    }

    void highlightTile(int x, int y) {
        //System.out.println("Highlighing " + x + ", " + y);
        highlightedTiles.add(new Coord(x, y));
        Button tile = getRectangleAt(x, y);

        setTileColor(x, y, getHighligthedColor(getTileColor(x, y)));
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
            updateTile(tile);
        highlightedTiles.clear();
    }

    // Redraws a tile with given coordinates
    void updateTile(Coord coord) {updateTile(coord.x, coord.y);}
    void updateTile(int x, int y) {
        String color = colorDict.get(karnaugh.getTileValue(x, y));
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
        //scoreTextField.setText(Integer.toString(score));
    }

    // Just a tiny function to make code cleaner
    void sleep(int timeMs) {
        try {
            Thread.sleep(timeMs);
        } catch(Exception e) {};
    }


};
