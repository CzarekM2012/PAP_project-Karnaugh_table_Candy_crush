import java.util.Map;
import java.util.HashMap;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;



public class App extends Application{
    static final int WIDTH = 4;
    static final int HEIGHT = 4;
    static final int SQUARE_SIZE = 100;
    static final int SCOREBAR_WIDTH = 120;

    private static Map<Integer, Color> colorDict = new HashMap<>(); // can be later used for coloring squares 

    public static void main(String[] args){
        colorDict.put(0, Color.GREEN);
        colorDict.put(1, Color.BLUE);
        colorDict.put(2, Color.YELLOW);
        colorDict.put(3, Color.RED);
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
        
        Pane layout = new Pane();
        layout.setPrefSize(WIDTH*SQUARE_SIZE, HEIGHT*SQUARE_SIZE);
   
        Scene scene = new Scene(layout);
        
        primaryStage.setScene(scene);
       
        for(int x = 0; x < WIDTH; x++){
            for(int y = 0; y < HEIGHT; y++){
                //Square sqr = new Square(x,y,1);
                Rectangle rctg = new Rectangle(SQUARE_SIZE, SQUARE_SIZE, Color.GREEN);
                rctg.setTranslateX(SQUARE_SIZE*x);
                rctg.setTranslateY(SQUARE_SIZE*y);
                rctg.setCursor(Cursor.HAND);
                rctg.setStroke(Color.BLACK);

                rectangles[y*WIDTH + x] = rctg;
                layout.getChildren().add(rctg);

                rctg.setOnMouseClicked(new EventHandler<MouseEvent>(){
                    @Override
                    public void handle(MouseEvent event){
                        int xOfRctg = (int)(event.getSceneX())/SQUARE_SIZE; //x coordinate of the rectangle 
                        int yOfRctg = (int)(event.getSceneY())/SQUARE_SIZE; // y -|| -
                        // ^those tell you which rectangle was pressed, useful for implementing game mechanics 
                        System.out.println(getRectangleAt(xOfRctg, yOfRctg).getFill());
                        rctg.setFill(Color.RED);
                    }
                });
            }
        }

        getRectangleAt(1, 2).setFill(Color.BEIGE);
        primaryStage.show();
    }



}