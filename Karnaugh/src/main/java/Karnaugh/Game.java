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
    KarnaughTable karnaugh;

    static final int START_TABLE_SIZE_X_BITS = 3; // How to split bits between x and y axis in table
    static final int START_TABLE_SIZE_Y_BITS = 3;
    static final int START_TABLE_VALUE_COUNT = 4; // How many logic values (tile colors) should be there
    static final int MIN_PATTERN_SIZE = 4; // Pattern has to be at least this size to be scored
    static final Set<ReplacementSource> replacementSourcesSet = new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top, ReplacementSource.Bottom }));

    static int WIDTH = 1 << START_TABLE_SIZE_X_BITS; // = 2^startTableSizeXBits
    static int HEIGHT = 1 << START_TABLE_SIZE_Y_BITS;

    public void startGame(){
        System.out.println("Starting game.");
        karnaugh = new KarnaughTable(START_TABLE_SIZE_X_BITS, START_TABLE_SIZE_Y_BITS, START_TABLE_VALUE_COUNT, MIN_PATTERN_SIZE, replacementSourcesSet);
    }

};
