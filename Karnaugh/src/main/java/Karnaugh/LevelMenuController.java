package karnaugh;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.nio.channels.SelectableChannel;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.Properties;
import java.io.IOException;
import java.util.Scanner;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class LevelMenuController {

    String levelFolderPath = "config/levels";

    private final ObservableList<LevelDesc> data =
        FXCollections.observableArrayList();   

    @FXML
    private TableView levelTable;
    @FXML
    private TableColumn nameColumn;
    @FXML
    private TableColumn sizeColumn;
    @FXML
    private TableColumn colorsColumn;
    @FXML
    private TableColumn difficultyColumn;
    @FXML
    private Label selectTextLabel;

    @FXML
    public void initialize() {
        System.out.println("Initializing table");
        nameColumn.setCellValueFactory(new PropertyValueFactory<LevelDesc, String>("name"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<LevelDesc, String>("size"));
        colorsColumn.setCellValueFactory(new PropertyValueFactory<LevelDesc, String>("colors"));
        difficultyColumn.setCellValueFactory(new PropertyValueFactory<LevelDesc, String>("difficulty"));

        //System.out.println(levelTable.getId());
        
        levelTable.setRowFactory(tv -> {
            TableRow<LevelDesc> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (! row.isEmpty() && event.getButton()==MouseButton.PRIMARY 
                     && event.getClickCount() == 2) {
        
                    LevelDesc clickedRow = row.getItem();

                    try {
                        launchGame(clickedRow.getName());
                    }
                    catch(IOException e) {
                        System.out.println("ERROR - Level file could not be loaded!");
                        clickedRow.setName("ERROR - File could not be loaded!");
                    }
                }
            });
            return row ;
        });

        levelTable.setItems(data);
        try {
            refreshClicked();
        }
        catch(IOException e) {
            selectTextLabel.setText("No levels found!");
        }
    }
    
    @FXML
    private void backClicked() throws IOException {
        System.out.print("Back to menu clicked.\n");    
        App.setRoot("menu");
    }

    @FXML
    private void refreshClicked() throws IOException {
        data.clear();
        
        System.out.print("Refreshing level list\n");
        final File levelFolder = new File(levelFolderPath);
        //System.out.println(levelFolder.getAbsolutePath());
        
        // Load new data
        for(File levelFile : listFilesForFolder(levelFolder))
            data.add(getLevelDescFromFile(levelFile));
    }

    LevelDesc getLevelDescFromFile(final File file) throws IOException {

        HashMap<String, String> values = new HashMap<String, String>();

        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            String[] line = scanner.nextLine().split(" = ");
            switch(line[0]) {
                case "special tiles:":
                    for(int i = 0; i < Integer.parseInt(values.get("special tile count")); ++i) {}
                    break;
                case "tile probabilities:":
                    for(int i = 0; i < Integer.parseInt(values.get("tile type count")); ++i) {}
                    break;
                default:
                    if(line.length >= 2)
                        values.put(line[0], line[1]);
            }
        }

        scanner.close();
        
        return new LevelDesc(values.get("name"),
                             values.get("bitSizeX") + "x" + values.get("bitSizeY"),
                             values.get("tile type count"),
                             values.get("difficulty") );
        
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

    // Creates a game and loads specified level
    void launchGame(String levelName) throws IOException {
        final File levelFolder = new File(levelFolderPath);
        System.out.println(levelFolder.getAbsolutePath());

        // Find first file that has that name
        for(File levelFile : listFilesForFolder(levelFolder)) {
            if(!getLevelDescFromFile(levelFile).getName().equals(levelName))
                continue;
            
            // Then launch a game with its data
            startGameFromLevelFile(levelFile);

            break;
        }
    }

    // Loads all level data into local variables
    void startGameFromLevelFile(final File file) throws IOException {

        HashMap<String, String> values = new HashMap<String, String>();
        HashSet<Coord> specialTiles = new HashSet<Coord>();
        //ArrayList<Integer> tileRarityWeights = new ArrayList<Integer>();
        
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            String[] line = scanner.nextLine().split(" = ");
            switch(line[0]) {
                case "special tiles:":
                    for(int i = 0; i < Integer.parseInt(values.get("special tile count")); ++i) {
                        line = scanner.nextLine().split(" ");
                        Coord coord = new Coord(Integer.parseInt(line[0]), Integer.parseInt(line[1]));
                        if(line[2].equals("block"))
                        {
                            specialTiles.add(coord);   
                        }
                    }
                    break;
                /*case "tile probabilities:":
                    for(int i = 0; i < Integer.parseInt(values.get("tile type count")); ++i)
                        tileRarityWeights.add(Integer.parseInt(scanner.nextLine()));
                    break;*/
                default:
                    if(line.length >= 2)
                        values.put(line[0], line[1]);
            }
        }

        scanner.close();
        
        Game game = new Game();
        
        //TODO: Put all this mess inside the constructor 
        game.levelName = values.get("name");
        game.tableBitSizeX = Integer.parseInt(values.get("bitSizeX")); 
        game.tableBitSizeY = Integer.parseInt(values.get("bitSizeY"));
        game.tableValueCount = Integer.parseInt(values.get("tile type count"));
        game.minPatternSize = Integer.parseInt(values.get("min pattern size"));
        game.gravityType = Integer.parseInt(values.get("gravity type"));
        game.wildFieldChance = Float.parseFloat(values.get("wild tile frequency"));
        game.timeLimitMax = Float.parseFloat(values.get("time limit max"));
        game.timeGainPerTile = Float.parseFloat(values.get("time gain per tile"));
        game.timeGainMin = Float.parseFloat(values.get("time gain min"));
        game.timeGainDecrease = Float.parseFloat(values.get("time gain decrease"));
        game.tableWidth = 1 << game.tableBitSizeX;
        game.tableHeight = 1 << game.tableBitSizeY;
        game.rectangles = new Button[game.tableWidth * game.tableHeight];
        game.replacementSourcesSet = new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top, ReplacementSource.Bottom }));


        KarnaughTable karnaugh = new KarnaughTable(game.tableBitSizeX, game.tableBitSizeY, game.tableValueCount, game.minPatternSize, game.wildFieldChance, game.replacementSourcesSet);
        for(Coord coord : specialTiles)
            karnaugh.set(coord, KarnaughTable.blockadeField);
        
        karnaugh.reroll();

        game.startGame(karnaugh);
        
    }
}