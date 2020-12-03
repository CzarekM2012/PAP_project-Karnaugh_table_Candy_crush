import java.util.Map;
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


public class App extends Application{
    static int WIDTH = 8;
    static int HEIGHT = 8;
    static int SQUARE_SIZE = 100;
    static int SCOREBAR_WIDTH = 150;

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

        primaryStage.setTitle("Karnaugh");

        Pane gameLayout = new Pane();
        gameLayout.setPrefSize(WIDTH*SQUARE_SIZE, HEIGHT*SQUARE_SIZE);
        HBox wholeLayout = new HBox();
        wholeLayout.setPrefSize(WIDTH*SQUARE_SIZE + SCOREBAR_WIDTH, HEIGHT*SQUARE_SIZE);
        VBox scorebarLayout = new VBox();
        scorebarLayout.setPrefSize(SCOREBAR_WIDTH, HEIGHT*SQUARE_SIZE);

        Button menuButton = new Button("Main menu");
        menuButton.setFont(new Font("Arial", 15));
        menuButton.setPrefWidth(0.8*SCOREBAR_WIDTH);
        menuButton.setTextFill(Color.WHITE);
        menuButton.setCursor(Cursor.HAND);
        menuButton.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(0), Insets.EMPTY))); //makes the button red, could probably make it easier with css

        menuButton.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event){
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

                        score.setText(String.valueOf(Integer.parseInt(score.getText()) + 1)); //example of changing the score
                        rctg.setFill(Color.RED); //example color change
                    }
                });
            }
        }

        getRectangleAt(1, 1).setFill(Color.BEIGE); //example use of getRectangleAt()
        getRectangleAt(WIDTH - 2, HEIGHT - 2).setFill(Color.BEIGE); //example use of getRectangleAt()
        
        primaryStage.show();
    }

}