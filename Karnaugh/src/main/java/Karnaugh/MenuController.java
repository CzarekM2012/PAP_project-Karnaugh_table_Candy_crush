package karnaugh;

import java.io.IOException;
import javafx.fxml.FXML;

public class MenuController {

    @FXML
    private void startClicked() throws IOException {
        System.out.print("Start clicked.\n");
        App.setRoot("game");
    }
    
    @FXML
    private void highscoreClicked() throws IOException {
        System.out.print("Highscore clicked.\n");
        App.showHighscores();
    }
    
    @FXML
    private void quitClicked() throws IOException {
        System.out.print("Quit clicked.\n");    
        App.quitGame();
    }
}
