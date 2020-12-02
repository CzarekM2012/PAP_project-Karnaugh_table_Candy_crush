import java.util.Map;
import javafx.scene.control.Button; 
import java.util.HashMap;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class App extends Application{
    static final int WIDTH = 16;
    static final int HEIGHT = 8;
    static final int SQUARE_SIZE = 100;
    static final int SCOREBAR_WIDTH = 150;

    private static Map<Integer, Color> colorDict = new HashMap<>(); // can be later used for coloring squares 

    public static void main(String[] args){
        colorDict.put(1, Color.GREEN);
        colorDict.put(2, Color.BLUE);
        colorDict.put(3, Color.YELLOW);
        colorDict.put(4, Color.RED);
        launch(args);
    }

    public Rectangle[] rectangles = new Rectangle[WIDTH*HEIGHT];
    public Rectangle getRectangleAt(int xRctg, int yRctg){
        //returns a reference to a rectangle on the board
        return rectangles[yRctg*WIDTH + xRctg];
    }

    @Override
    public void start(Stage primaryStage){

        primaryStage.setTitle("Title");

        Pane gameLayout = new Pane();
        gameLayout.setPrefSize(WIDTH*SQUARE_SIZE, HEIGHT*SQUARE_SIZE);
        HBox wholeLayout = new HBox();
        wholeLayout.setPrefSize(WIDTH*SQUARE_SIZE + SCOREBAR_WIDTH, HEIGHT*SQUARE_SIZE);
        VBox scorebarLayout = new VBox();
        scorebarLayout.setPrefSize(SCOREBAR_WIDTH, HEIGHT*SQUARE_SIZE);

        Text scoreLabel = new Text("Score");
        scoreLabel.setFont(new Font("Arial", 35));

        Text score = new Text("00000000");
        score.setFont(new Font("Comic Sans", 24));

        scorebarLayout.getChildren().add(scoreLabel);
        scorebarLayout.getChildren().add(score);    

        wholeLayout.getChildren().add(gameLayout);
        wholeLayout.getChildren().add(scorebarLayout);
        Scene scene = new Scene(wholeLayout);
        
        primaryStage.setScene(scene);
       
        for(int x = 0; x < WIDTH; x++){
            for(int y = 0; y < HEIGHT; y++){
                Rectangle rctg = new Rectangle(SQUARE_SIZE, SQUARE_SIZE, Color.GREEN);
                rctg.setTranslateX(SQUARE_SIZE*x);
                rctg.setTranslateY(SQUARE_SIZE*y);
                rctg.setCursor(Cursor.HAND);
                rctg.setStroke(Color.BLACK);

                rectangles[y*WIDTH + x] = rctg;
                gameLayout.getChildren().add(rctg);

                rctg.setOnMouseClicked(new EventHandler<MouseEvent>(){
                    @Override
                    public void handle(MouseEvent event){
                        int xOfRctg = (int)(event.getSceneX())/SQUARE_SIZE; //x coordinate of the rectangle 
                        int yOfRctg = (int)(event.getSceneY())/SQUARE_SIZE; // y -|| -
                        // ^those tell you which rectangle was pressed, useful for implementing game mechanics
                        rctg.setFill(Color.RED);
                    }
                });
            }
        }

        getRectangleAt(1, 2).setFill(Color.BEIGE); //example use of getRectangleAt()
        primaryStage.show();
    }

}