package Karnaugh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class KarnaughTable {
    private int xSize, ySize, fieldValuesNumber;
    private int indexToGrey[], greyToIndex[];
    private Set<ReplacementSource> replacementSourcesSet;
    private Field board[][]; //

    public KarnaughTable(int bitsNumberX, int bitsNumberY, int fieldValuesNumber, int minPatternTileCount) throws IllegalArgumentException
    {
        // Temporarily here, should be moved outside later
        Set<ReplacementSource> replacementSourcesSet = new HashSet<ReplacementSource>();
        replacementSourcesSet.addAll(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top, ReplacementSource.Bottom }));

        // Check if bit numbers are correct
        if (bitsNumberX + bitsNumberY <= countBits(Integer.MAX_VALUE) * 2 && fieldValuesNumber > 0 && !replacementSourcesSet.isEmpty()
            && !replacementSourcesSet.contains(ReplacementSource.Improper))
        {
            this.xSize = 1 << bitsNumberX; // left shift - equivalent to (int)math.pow(2, bitsNumberX);
            this.ySize = 1 << bitsNumberY;
            this.indexToGrey = new int[xSize];
            this.greyToIndex = new int[xSize];
            setupArrayIndexToGreyCode(); // Grey code caching
            for (int i = 0; i < xSize; i++)
            {
                greyToIndex[indexToGrey[i]] = i;
            }

            // Initialising fields
            this.fieldValuesNumber = fieldValuesNumber;
            this.replacementSourcesSet = replacementSourcesSet;
            this.board = new Field[xSize][ySize];
            for (int i = 0; i < xSize; i++)
            {
                for (int j = 0; j < ySize; j++)
                {
                    board[i][j] = new Field();
                }
            }

            // Setting field values
            prepareBoardWithNoPatterns(minPatternTileCount);
        }
        else
        {
            // Throw errors
            if (bitsNumberX + bitsNumberY > countBits(Integer.MAX_VALUE) * 2)
            {
                throw new IllegalArgumentException("BitsNumber cannot be higher than 2*number_of_bits_in_Integer.MAX_VALUE");
            }
            if (fieldValuesNumber <= 0)
            {
                throw new IllegalArgumentException("fieldValuesNumber needs to be higher than 0");
            }
            if (replacementSourcesSet.isEmpty())
            {
                throw new IllegalArgumentException("replacementSources cannot be empty");
            }
            else if (replacementSourcesSet.contains(ReplacementSource.Improper))
            {
                throw new IllegalArgumentException("replacementSourcesSet cannot contain ReplacementSource.Improper");
            }
        }
    }

    // Counts the 1ns in a binary form of given number
    private int countBits(int number)
    {
        int bits = 0;
        while (number != 0)
        {
            bits += (number & 1);
            number >>>= 1; // new Field(0, ReplacementSource.Top)-fill right shift
        }
        return bits;
    }

    // Gray code caching
    private void setupArrayIndexToGreyCode()
    {
        indexToGrey[0] = 0;
        indexToGrey[1] = 1;
        int leftMostBit = 2, j = 1;
        for (int i = 2; i < xSize; i++)
        {
            if(countBits(i) == 1)
            {
                leftMostBit = i;
                j = i - 1;
            }
            indexToGrey[i] = leftMostBit + indexToGrey[j];
            j--;
        }
    }

    // Randomise a board in such way that tiles don't form any pattern at least of given size
    private boolean prepareBoardWithNoPatterns(int minPatternTileCount) throws IllegalArgumentException
    {
        // Checks if minPatternTileCount is correct 
        if(countBits(minPatternTileCount) == 1 && minPatternTileCount < (this.xSize * this.ySize))
        {
            Random generator = new Random();
            int[] valuesAppearanceCounts = new int[this.fieldValuesNumber];
            int minNeighboursPerPattern = 0;

            // Calculates log2(minPatternTileCount)
            while(minPatternTileCount > 1)
            {
                minNeighboursPerPattern++;
                minPatternTileCount>>>=1;
            }
            
            int size = replacementSourcesSet.size();
            ReplacementSource array[] = new ReplacementSource[size];
            replacementSourcesSet.toArray(array);
            for(int i=0; i<xSize; i++)
            {
                for(int j=0; j<ySize; j++)
                {
                    // Counts neighbour tile values
                    int value;
                    List<Coord> neighourCoords = adjacentFields(new Coord(i, j));
                    for(Coord neighbourCoord : neighourCoords)
                    {
                        value = board[neighbourCoord.x][neighbourCoord.y].value;
                        if(value >= 0)
                        {
                            valuesAppearanceCounts[value]++;
                        }
                    }
                    value = minNeighboursPerPattern;
                    for(int k=0; k<valuesAppearanceCounts.length; k++)
                    {
                        if(valuesAppearanceCounts[k]<value)
                        {
                            value = valuesAppearanceCounts[k];
                        }
                    }
                    if(value == minNeighboursPerPattern)
                    {
                        return false;
                    }

                    // Set a random value that doesn't create a possibility of pattern creation 
                    value = generator.nextInt(this.fieldValuesNumber);
                    while(valuesAppearanceCounts[value] >= minNeighboursPerPattern) //TODO: Make sure that this while doesn't create an endless loop
                    {
                        value = generator.nextInt(this.fieldValuesNumber);
                    }
                    board[i][j].set(value, array[generator.nextInt(size)]);
                    for(int k=0; k<valuesAppearanceCounts.length; k++)
                    {
                        valuesAppearanceCounts[k]=0;
                    }
                }
            }
            return true;
        }
        else
        {
            throw new IllegalArgumentException("minPatternTileCount needs to be a power of 2 lower than 2^(bitsNumberX + bitsNumberY)");
        }
    }

    // Fill empty tiles with random values
    public ArrayList<Coord> fillWithRandoms()
    {
        ArrayList<Coord> filledTiles = new ArrayList<Coord>();
        Random generator = new Random();
        int size = replacementSourcesSet.size();
        ReplacementSource array[] = new ReplacementSource[size];
        replacementSourcesSet.toArray(array);
        Field emptyField = new Field();
        for (int i = 0; i < xSize; i++)
        {
            for (int j = 0; j < ySize; j++)
            {
                if (board[i][j].equals(emptyField))
                {
                    board[i][j].set(generator.nextInt(fieldValuesNumber), array[generator.nextInt(size)]);
                    filledTiles.add(new Coord(i, j));
                }
            }
        }
        return filledTiles;
    }

    // Runs collapse() and transforms result into an Array of tiles which position changed during collapse
    public ArrayList<Coord> collapseGetTiles(ReplacementSource replacementSource) {
        ArrayList<Coord> movedTiles = new ArrayList<Coord>();
        Coord[][] result = collapse(replacementSource);
        Coord empty = new Coord();

        for(int x = 0; x < xSize; ++x)
            for(int y = 0; y < ySize; ++y)
                if(!result[x][y].equals(empty))
                    movedTiles.add(new Coord(result[x][y]));

        return movedTiles;
    }

    // Gravity - moves fields in the opposite of specified direction
    // (if replacementSource equals ReplacementSource.Top, fields will be moving
    // down, etc.), if there are empty fields in that direction
    // TODO: change collapser position checking for it to not get blocked on edges when movig alongside them
    public Coord[][] collapse(ReplacementSource replacementSource) throws IllegalArgumentException
    {
        // Initialise result tab[][]
        Coord fieldsMovementBoard[][] = new Coord[xSize][ySize];
        for (int i = 0; i < xSize; i++)
        {
            for (int j = 0; j < ySize; j++)
            {
                fieldsMovementBoard[i][j] = new Coord();
            }
        }

        Coord currentPosition, collapser, move, nextLine, fall, fallLimit;
        Field clearField = new Field();
        switch (replacementSource)
        {
            case Top:// spawn at top -> collapse down
                currentPosition = new Coord(0, ySize - 2);
                move = new Coord(1, 0);
                nextLine = new Coord(-xSize, -1);
                fall = new Coord(0, 1);
                fallLimit = new Coord(-1, ySize);
                break;
            case Bottom:// spawn at bottom -> collapse up
                currentPosition = new Coord(0, 1);
                move = new Coord(1, 0);
                nextLine = new Coord(-xSize, 1);
                fall = new Coord(0, -1);
                fallLimit = new Coord(-1, -1);
                break;
            case Left:// spawn at left -> collapse right
                currentPosition = new Coord(xSize - 2, 0);
                move = new Coord(0, 1);
                nextLine = new Coord(-1, -ySize);
                fall = new Coord(1, 0);
                fallLimit = new Coord(xSize, -1);
                break;
            case Right:// spawn at right -> collapse left
                currentPosition = new Coord(1, 0);
                move = new Coord(0, 1);
                nextLine = new Coord(1, -ySize);
                fall = new Coord(-1, 0);
                fallLimit = new Coord(-1, -1);
                break;
            default:
                throw new IllegalArgumentException("Argument has to be one of proper replacement sources");
        }

        // Checks whole board (from 'currentPosition' moving by 'move') for tiles that can fall
        while (currentPosition.x >= 0 && currentPosition.x < xSize && currentPosition.y >= 0
                && currentPosition.y < ySize)
        {
            collapser = currentPosition.add(fall);
            
            // If collapsable
            if (!board[currentPosition.x][currentPosition.y].equals(clearField) && board[collapser.x][collapser.y].equals(clearField))
            {
                // Moves tiles in 'fall' diretion until another non-empty tile or border is found
                while (!(collapser.x == fallLimit.x || collapser.y == fallLimit.y)
                       && board[collapser.x + fall.x][collapser.y + fall.y].equals(clearField))
                {
                    collapser.addTo(fall);
                }
                swapTiles(collapser, currentPosition);

                // Add to result
                fieldsMovementBoard[currentPosition.x][currentPosition.y].set(collapser);
            }
            currentPosition.addTo(move);
            // start checking next line
            if (currentPosition.x == xSize || currentPosition.y == ySize)
            {
                currentPosition.addTo(nextLine);
            }
        }
        return fieldsMovementBoard;
    }


    // Clears fields at Coords in list and returns value of ReplacementSource that described most of them
    public ReplacementSource destroyFields(List<Coord> list)
    {
        int replacementSourcesCounts[] = { 0, 0, 0, 0, 0 };
        for (int i = 0; i < list.size(); i++)
        {
            Coord destroyed = list.get(i);
            //System.out.print(board[destroyed.x][destroyed.y].replacementSource.id);
            if(board[destroyed.x][destroyed.y].replacementSource.id != -1) // Added a safeguard
            {
                replacementSourcesCounts[board[destroyed.x][destroyed.y].replacementSource.id]++;
            }
            board[destroyed.x][destroyed.y].clear();
        }

        // Returns replacement source that was most present in destroyed tiles
        int max = 0, index = 0;
        for (int i = 1; i < 5; i++)
        {
            if (max < replacementSourcesCounts[i])
            {
                max = replacementSourcesCounts[i];
                index = i;
            }
        }
        switch (index)
        {
            case 1:
                return ReplacementSource.Top;
            case 2:
                return ReplacementSource.Bottom;
            case 3:
                return ReplacementSource.Left;
            case 4:
                return ReplacementSource.Right;
            default:
                return ReplacementSource.Improper;
        }
    }

    // Returns list of Coords of fields adjacent to field at startCoord according to Karnaugh table rules
    public ArrayList<Coord> adjacentFields(Coord startCoord)
    {
        ArrayList<Coord> destinatitionList = new ArrayList<Coord>();
        Coord greyStartCoord = new Coord(indexToGrey[startCoord.x], indexToGrey[startCoord.y]);
        int modifier = indexToGrey[xSize - 1]; // singular leftmost '1' bit for index representations in Grey code for
                                               // X-axis
        /*
         * ^ - XOR operator:
         * True ^ True -> False
         * True ^ False -> True
         * False ^ True -> True
         * False ^ False -> False
         */
        while (modifier != 0)
        {
            destinatitionList.add(new Coord(greyToIndex[greyStartCoord.x ^ modifier], startCoord.y));
            modifier >>>= 1;
        }
        modifier = indexToGrey[ySize - 1]; // singular leftmost '1' bit for index representations in Grey code for
                                           // Y-axis
        while (modifier != 0)
        {
            destinatitionList.add(new Coord(startCoord.x, greyToIndex[greyStartCoord.y ^ modifier]));
            modifier >>>= 1;
        }
        return destinatitionList;
    }

    // Finds all tile patterns that contain selected tile, skips ones that are fully included in other patterns
    // All patterns have to be at least 'minPatternTileCount' long to be considered
    // TODO: make it so that subpatterns of already existing bigger patterns are checked when inspecting moveCoord neighbour
    // TODO: Document it better
    public ArrayList<ArrayList<Coord>> getPatternsContaining(Coord moveCoord, int minPatternTileCount)
    {
        int moveFieldValue = board[moveCoord.x][moveCoord.y].value;
        Coord greyMoveCoord = new Coord(indexToGrey[moveCoord.x], indexToGrey[moveCoord.y]);
        ArrayList<ArrayList<Coord>> fieldsCollections = new ArrayList<ArrayList<Coord>>();

        ArrayList<Coord> collectionPotentialNewFields = new ArrayList<Coord>();
        Coord greyDifference = new Coord();
        ArrayList<Coord> adjacentsToMoveCoord = adjacentFields(moveCoord);
        
        for (int i = 0; i < adjacentsToMoveCoord.size(); i++)
        {
            Coord neighour = adjacentsToMoveCoord.get(i);
            if (moveFieldValue == board[neighour.x][neighour.y].value)
            {
                greyDifference.set(greyMoveCoord.x ^ indexToGrey[neighour.x],
                        greyMoveCoord.y ^ indexToGrey[neighour.y]); // get bit that differs between field an moveCoord from its neighbour 
                boolean belongsToAnyExistingCollection = false; // we will need to create new collection
                for (int j = 0; j < fieldsCollections.size(); j++)
                {
                    boolean belongsToThisCollection = true;
                    int k = 0, elements = fieldsCollections.get(j).size();
                    while(belongsToThisCollection && k < elements)
                    {
                        Coord inspected = new Coord(fieldsCollections.get(j).get(k));
                        // transform inspected to Grey code, change bit pointed by greyDifference and
                        // change to index
                        inspected.set(greyToIndex[indexToGrey[inspected.x] ^ greyDifference.x],
                                      greyToIndex[indexToGrey[inspected.y] ^ greyDifference.y]);
                        if (board[inspected.x][inspected.y].value != moveFieldValue)
                        {
                            belongsToThisCollection = false;
                        }
                        collectionPotentialNewFields.add(inspected);
                        k++;
                    }
                    if (belongsToThisCollection)
                    {
                        belongsToAnyExistingCollection = true; // no need to create new collection anymore
                        fieldsCollections.get(j).addAll(collectionPotentialNewFields);
                    }
                    collectionPotentialNewFields.clear();
                }
                if (!belongsToAnyExistingCollection)
                {
                    fieldsCollections.add(new ArrayList<Coord>(Arrays.asList(moveCoord, neighour)));
                }
            }
        }
        return fieldsCollections;
    }

    // Returns a list containing coords of every field to be destroyed together with given tile
    // only includes a single instance of each tile
    public ArrayList<Coord> fieldsToDestroy(Coord tile, int minPatternTileCount)
    {
        ArrayList<ArrayList<Coord>> fieldsCollections = getPatternsContaining(tile, minPatternTileCount);
        ArrayList<Coord> fieldsToDestroy = new ArrayList<>();

        for (int i = 0; i < fieldsCollections.size(); i++) {
            
            // Take only patterns that have at least 'minPatternTileCount' tiles
            if(fieldsCollections.get(i).size() < minPatternTileCount)
                continue;
            
            for (int j = 0; j < fieldsCollections.get(i).size(); j++) {
                Coord inspected = fieldsCollections.get(i).get(j);
                if (!fieldsToDestroy.contains(inspected)) {
                    fieldsToDestroy.add(inspected);
                }
            }
        }
        return fieldsToDestroy;
    }

    // Debug printouts
    public void printTile(Coord tile) {
        System.out.println("Tile: (" + tile.x + ", " + tile.y + "), value = " + board[tile.x][tile.y].value);
    }

    public static void printTiles(ArrayList<Coord> tiles) {
        System.out.println("Tile list:");
        for(Coord tile : tiles) {
            System.out.print("(" + tile.x + ", " + tile.y + ")");
        }
        System.out.println();
    }

    public void print() {
        for (int i = 0; i < ySize; i++) {
            for (int j = 0; j < xSize; j++) {
                System.out.print(board[j][i].value);
                switch (board[j][i].replacementSource.id) {
                    case 1:
                        System.out.print("top ");
                        break;
                    case 2:
                        System.out.print("bot ");
                        break;
                    case 3:
                        System.out.print("lef ");
                        break;
                    case 4:
                        System.out.print("rig ");
                        break;
                    default:
                        System.out.print("imp ");
                        break;
                }
            }
            System.out.println();
        }
    }

    // Setters and getters
    public void set(Coord coord, Field field) throws IndexOutOfBoundsException {board[coord.x][coord.y].set(field);}
    public void set(Field[][] newBoard) throws IndexOutOfBoundsException {
        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                board[i][j].set(newBoard[i][j]);
            }
        }
    }

    public Field[][] get() {
        Field[][] copy = new Field[xSize][ySize];
        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                copy[i][j] = new Field(board[i][j]);
            }
        }
        return copy;
    }

    int getSizeX() {return xSize;}
    int getSizeY() {return ySize;}
    int getBitSizeX()
    {
        int counter = 0, temp = xSize;
        while(temp!=1)
        {
            counter++;
            temp>>=1;
        }
        return counter;
    }
    int getBitSizeY()
    {
        int counter = 0, temp = ySize;
        while(temp!=1)
        {
            counter++;
            temp>>=1;
        }
        return counter;
    }

    int getTileValue(int x, int y) {return board[x][y].value;}
    int getTileValue(Coord coord) {return board[coord.x][coord.y].value;}

    int translateIndexToGrey(int index){return indexToGrey[index];}

    public void swapTiles(Coord t1, Coord t2) {
        Field tmp = board[t1.x][t1.y];
        board[t1.x][t1.y] = board[t2.x][t2.y];
        board[t2.x][t2.y] = tmp;
    }
    public static void main(String[] args)
    {
        KarnaughTable test = new KarnaughTable(3, 3, 4, 4);
        test.print();
    }
}