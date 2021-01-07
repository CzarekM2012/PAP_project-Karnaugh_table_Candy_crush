package karnaugh;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;

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

    // Main data structures
    public static Map<Integer, String> colorDict = new HashMap<>();     // Tile value-to-color map

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
        colorDict.put(-3, "000000");
        colorDict.put(-2, "404040");
        colorDict.put(-1, "ffffff");
        colorDict.put(0, "577590");
        colorDict.put(1, "90be6d");
        colorDict.put(2, "f8961e");
        colorDict.put(3, "f94144");
        colorDict.put(4, "48cae4");
        colorDict.put(5, "f9c74f");
        colorDict.put(6, "f3722c");
        launch(args);

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
    }

    static public double aspectRatio = 5/3;
    static public Stage stage;

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Preparing window
        primaryStage.setTitle("Bekarnaughed");
        primaryStage.setMinWidth(640);
        primaryStage.setMinHeight(480);

        stage = primaryStage;
        primaryStage.minWidthProperty().bind(primaryStage.heightProperty().multiply(aspectRatio));
        
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