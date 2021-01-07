package karnaugh;

import java.io.IOException;
import javafx.fxml.FXML;
// import javafx.scene.Parent;
// import javafx.scene.Scene;
// import javafx.stage.Stage;
// import javafx.fxml.FXMLLoader;

public class MenuController {

    // private static Parent loadFXML(String fxml) throws IOException {
    //     FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
    //     return fxmlLoader.load();
    // }

    @FXML
    private void startClicked() throws IOException {
        // Scene scene = new Scene(loadFXML("levelMenu"), 640, 480);

        // App.stage.setScene(scene);
        App.setRoot("levelMenu");
    }
    
    @FXML
    private void highscoreClicked() throws IOException {
        System.out.print("Highscore clicked.\n");
        App.scene.getStylesheets().addAll(this.getClass().getResource("game.css").toExternalForm());
        App.showHighscores();
    }
    
    @FXML
    private void quitClicked() throws IOException {
        System.out.print("Quit clicked.\n");    
        App.quitGame();
    }
}
