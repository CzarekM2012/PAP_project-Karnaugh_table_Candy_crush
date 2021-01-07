package karnaugh;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;


public class LevelDesc {
    private final SimpleStringProperty name;
    private final SimpleStringProperty size;
    private final SimpleStringProperty colors;
    private final SimpleStringProperty difficulty;

    public LevelDesc(String name, String size, String colors, String difficulty) {
        this.name = new SimpleStringProperty(name);
        this.size = new SimpleStringProperty(size);
        this.colors = new SimpleStringProperty(colors);
        this.difficulty = new SimpleStringProperty(difficulty);
    }

    public String getName() {
        return name.get();
    }
    public void setName(String name) {
        this.name.set(name);
    }
   
    public String getSize() {
        return size.get();
    }
    public void setSize(String size) {
        this.size.set(size);
    }
   
    public String getColors() {
        return colors.get();
    }
    public void setColors(String colors) {
        this.colors.set(colors);
    }

    public String getDifficulty() {
        return difficulty.get();
    }
    public void setDifficulty(String difficulty) {
        this.difficulty.set(difficulty);
    }
}