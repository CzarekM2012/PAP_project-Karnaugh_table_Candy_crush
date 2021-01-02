package karnaugh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class KarnaughTable {
    private int xSize, ySize, fieldValuesNumber, minPatternTileCount;
    private int indexToGrey[], greyToIndex[];
    private Set<ReplacementSource> replacementSourcesSet;
    private Field board[][]; //

    public KarnaughTable(int bitsNumberX, int bitsNumberY, int fieldValuesNumber, int minPatternTileCount) throws IllegalArgumentException, AssertionError
    {
        // Temporarily here, should be moved outside later
        Set<ReplacementSource> replacementSourcesSet = new HashSet<ReplacementSource>();
        replacementSourcesSet.addAll(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top, ReplacementSource.Bottom }));

        if (bitsNumberX <= countBits(Integer.MAX_VALUE) && bitsNumberY <= countBits(Integer.MAX_VALUE) /*physical limitation for number of tiles*/
            && !replacementSourcesSet.isEmpty() && !replacementSourcesSet.contains(ReplacementSource.Improper) /*assigning proper values to tiles*/
            && countBits(minPatternTileCount) == 1 /*patterns correct according to Karnaugh Table rules*/
            && fieldValuesNumber > 1 && minPatternTileCount < (1 << (bitsNumberX + bitsNumberY)) && minPatternTileCount > 1 /*conditions for gameplay*/)
        {
            this.xSize = 1 << bitsNumberX; // left shift - equivalent to (int)math.pow(2, bitsNumberX);
            this.ySize = 1 << bitsNumberY;
            int longerEdge = xSize>ySize?xSize:ySize;
            this.indexToGrey = new int[longerEdge];
            this.greyToIndex = new int[longerEdge];
            setupArrayIndexToGreyCode(longerEdge); // Grey code caching
            for (int i = 0; i < longerEdge; i++)
            {
                greyToIndex[indexToGrey[i]] = i;
            }

            // Initialising fields
            this.minPatternTileCount = minPatternTileCount;
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
            try
            {
                prepareBoardWithNoPatterns();
            }
            catch(AssertionError e)
            {
                throw e;
            }
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
            if(countBits(minPatternTileCount) != 1 || minPatternTileCount < 1 || minPatternTileCount > 1 << (bitsNumberX + bitsNumberY))
            {
                throw new IllegalArgumentException("minPatternTileCount needs to be a power of 2 higher than 1 and lower than 2^(bitsNumberX + bitsNumberY)");
            }
        }
    }

    /** @return number of "1" bits in binary represantation of number */
    private int countBits(int number)
    {
        int bits = 0;
        while (number != 0)
        {
            bits += (number & 1);
            number >>>= 1; // 0 fill right shift
        }
        return bits;
    }

    // Gray code caching
    private void setupArrayIndexToGreyCode(int longerEdge)
    {
        indexToGrey[0] = 0;
        indexToGrey[1] = 1;
        int leftMostBit = 2, j = 1;
        for (int i = 2; i < longerEdge; i++)
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
    private void prepareBoardWithNoPatterns() throws AssertionError
    {
        Random generator = new Random();
        int[] valuesAppearanceCounts = new int[this.fieldValuesNumber];
        int minTiles = this.minPatternTileCount, minNeighboursPerPattern = 0;
        // calculate how many neighbours of the choosen tile needs to have the same value
        // in order to make it possible for them to be a part of a complete minimal pattern
        while(minTiles > 1)
        {
            minNeighboursPerPattern++;
            minTiles>>>=1;
        }

        int numberOfReplacementSources = replacementSourcesSet.size();
        ReplacementSource replacementSourcesArray[] = new ReplacementSource[numberOfReplacementSources];
        replacementSourcesSet.toArray(replacementSourcesArray);
        for(int i=0; i<xSize; i++)
        {
            for(int j=0; j<ySize; j++)
            {
                // Counts neighbour tiles values
                int tileValue;
                List<Coord> neighourCoords = adjacentFields(new Coord(i, j));
                for(Coord neighbourCoord : neighourCoords)
                {
                    tileValue = board[neighbourCoord.x][neighbourCoord.y].value;
                    if(tileValue >= 0)
                    {
                        valuesAppearanceCounts[tileValue]++;
                    }
                }
                List<Integer> possibleValues = new ArrayList<Integer>();
                for(int k=0; k<valuesAppearanceCounts.length; k++)
                {
                    if(valuesAppearanceCounts[k] < minNeighboursPerPattern)
                    {
                        possibleValues.add(k);
                    }
                }
                assert possibleValues.size()>0;
                // Set a random value that doesn't create a possibility of pattern creation
                tileValue = possibleValues.get(generator.nextInt(possibleValues.size()));
                board[i][j].set(tileValue, replacementSourcesArray[generator.nextInt(numberOfReplacementSources)]);
                for(int k=0; k<valuesAppearanceCounts.length; k++)
                {
                    valuesAppearanceCounts[k]=0;
                }
            }
        }
    }

    // Fill empty tiles with random values
    public ArrayList<Coord> fillWithRandoms()
    {
        ArrayList<Coord> filledTiles = new ArrayList<Coord>();
        Random generator = new Random();
        int numberOfReplacementSources = replacementSourcesSet.size();
        ReplacementSource replacementSourcesArray[] = new ReplacementSource[numberOfReplacementSources];
        replacementSourcesSet.toArray(replacementSourcesArray);
        Field emptyField = new Field();
        for (int i = 0; i < xSize; i++)
        {
            for (int j = 0; j < ySize; j++)
            {
                if (board[i][j].equals(emptyField))
                {
                    board[i][j].set(generator.nextInt(fieldValuesNumber), replacementSourcesArray[generator.nextInt(numberOfReplacementSources)]);
                    filledTiles.add(new Coord(i, j));
                }
            }
        }
        return filledTiles;
    }

    /** Runs collapse() and transforms result into an Array of tiles which position changed during collapse */
    public ArrayList<Coord> collapseGetTiles(ReplacementSource replacementSource) {
        ArrayList<Coord> movedTiles = new ArrayList<Coord>();
        Coord[][] result;
        try
        {
            result = collapse(replacementSource);
        }
        catch(IllegalArgumentException e)
        {
            throw e;
        }
        Coord empty = new Coord();
        for(int x = 0; x < xSize; ++x)
            for(int y = 0; y < ySize; ++y)
                if(!result[x][y].equals(empty))
                    movedTiles.add(new Coord(result[x][y]));
        return movedTiles;
    }

    /** move fields in the opposite of specified direction
      * (if replacementSource equals ReplacementSource.Top, fields will be moving
      * down, etc.), if there are empty fields in that direction.
      * @return array[xSize][ySize] containing Coord() if specific tile was not moved
      * or target coord of tile movement*/
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

        Coord currentPosition, collapser = new Coord(), move, nextLine, fall, fallLimit;
        Field clearField = new Field();
        switch (replacementSource)
        {
            case Top:// spawn at top -> X down
                currentPosition = new Coord(0, ySize - 2);
                move = new Coord(1, 0);
                nextLine = new Coord(-xSize, -1);
                fall = new Coord(0, 1);
                fallLimit = new Coord(-1, ySize - 1);
                break;
            case Bottom:// spawn at bottom -> collapse up
                currentPosition = new Coord(0, 1);
                move = new Coord(1, 0);
                nextLine = new Coord(-xSize, 1);
                fall = new Coord(0, -1);
                fallLimit = new Coord(-1, 0);
                break;
            case Left:// spawn at left -> collapse right
                currentPosition = new Coord(xSize - 2, 0);
                move = new Coord(0, 1);
                nextLine = new Coord(-1, -ySize);
                fall = new Coord(1, 0);
                fallLimit = new Coord(xSize - 1, -1);
                break;
            case Right:// spawn at right -> collapse left
                currentPosition = new Coord(1, 0);
                move = new Coord(0, 1);
                nextLine = new Coord(1, -ySize);
                fall = new Coord(-1, 0);
                fallLimit = new Coord(0, -1);
                break;
            default:
                throw new IllegalArgumentException("Argument has to be one of proper replacement sources");
        }

        // Checks whole board (from 'currentPosition' moving by 'move') for tiles that can fall
        while (currentPosition.x >= 0 && currentPosition.x < xSize && currentPosition.y >= 0
                && currentPosition.y < ySize)
        {
            collapser.set(currentPosition.x + fall.x, currentPosition.y + fall.y);;
            
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


    /** Clears fields at Coords in list and returns value of ReplacementSource that described most of them */
    public ReplacementSource destroyFields(List<Coord> list)
    {
        int replacementSourcesCounts[] = {0, 0, 0, 0, 0 };
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

    /** Returns list of Coords of fields adjacent to field at startCoord according to Karnaugh table rules */
    public ArrayList<Coord> adjacentFields(Coord startCoord)
    {
        ArrayList<Coord> destinatitionList = new ArrayList<Coord>();
        Coord greyStartCoord = new Coord(indexToGrey[startCoord.x], indexToGrey[startCoord.y]);
        int modifier = indexToGrey[xSize - 1]; // singular leftmost '1' bit for index representations in Grey code for X-axis
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
        modifier = indexToGrey[ySize - 1]; // singular leftmost '1' bit for index representations in Grey code for Y-axis
        while (modifier != 0)
        {
            destinatitionList.add(new Coord(startCoord.x, greyToIndex[greyStartCoord.y ^ modifier]));
            modifier >>>= 1;
        }
        return destinatitionList;
    }

    // TODO: make it so that subpatterns of already existing bigger patterns are checked when inspecting moveCoord neighbour
    // TODO: Document it better
    /**Finds all tile patterns that contain tile at coord.
     * All patterns have to be at least 'minPatternTileCount' long to be considered */
    public ArrayList<ArrayList<Coord>> getPatternsContaining(Coord choosenFieldCoord)
    {
        int choosenFieldValue = board[choosenFieldCoord.x][choosenFieldCoord.y].value;
        Coord greyChoosenFieldCoord = new Coord(indexToGrey[choosenFieldCoord.x], indexToGrey[choosenFieldCoord.y]);
        ArrayList<ArrayList<Coord>> fieldsCollections = new ArrayList<ArrayList<Coord>>();
        ArrayList<Coord> potentialNewFields = new ArrayList<Coord>();
        ArrayList<Coord> adjacentsToMoveCoord = adjacentFields(choosenFieldCoord);
        Coord greyDifference = new Coord();
        
        for (Coord neighbour : adjacentsToMoveCoord)
        {
            if (choosenFieldValue == board[neighbour.x][neighbour.y].value)
            {
                greyDifference.set(greyChoosenFieldCoord.x ^ indexToGrey[neighbour.x], 
                                   greyChoosenFieldCoord.y ^ indexToGrey[neighbour.y]); // get bit in Grey Code that differs between field at choosenCoord from its neighbour 
                int limit = fieldsCollections.size();
                for (int i = 0; i < limit; i++)
                {
                    boolean belongsToThisCollection = true;
                    int j = 0, collectionSize = fieldsCollections.get(i).size();
                    while(belongsToThisCollection && j < collectionSize)
                    {
                        Coord inspected = new Coord(fieldsCollections.get(i).get(j));// element of pattern
                        // transform inspected to Grey code, change bit pointed by greyDifference and transform to index
                        inspected.set(greyToIndex[indexToGrey[inspected.x] ^ greyDifference.x],
                                      greyToIndex[indexToGrey[inspected.y] ^ greyDifference.y]);
                        if (board[inspected.x][inspected.y].value != choosenFieldValue)
                        {
                            belongsToThisCollection = false;
                        }
                        potentialNewFields.add(inspected);
                        j++;
                    }
                    if (belongsToThisCollection)
                    {
                        ArrayList<Coord> newCollection = new ArrayList<Coord>();
                        newCollection.addAll(fieldsCollections.get(i));
                        newCollection.addAll(potentialNewFields);
                        fieldsCollections.add(newCollection);
                    }
                    potentialNewFields.clear();
                }
                fieldsCollections.add(new ArrayList<Coord>(Arrays.asList(choosenFieldCoord, neighbour)));
            }
        }
        //filter out subpatterns and too small patterns
        ArrayList<ArrayList<Coord>> unnecesaryCollections = new ArrayList<ArrayList<Coord>>();
        for(int i=0; i<fieldsCollections.size(); i++)
        {
            ArrayList<Coord> inspected = fieldsCollections.get(i);
            boolean necessary = inspected.size()>=this.minPatternTileCount?true:false;
            int j=0;
            while(necessary && j < fieldsCollections.size())
            {
                if(fieldsCollections.get(j).size() > inspected.size() && fieldsCollections.get(j).containsAll(inspected))
                {
                    necessary = false;
                }
                j++;
            }
            if(!necessary)
            {
                unnecesaryCollections.add(inspected);
            }
        }
        for(ArrayList<Coord> unnecesary : unnecesaryCollections)
        {
            fieldsCollections.remove(unnecesary);
        }
        return fieldsCollections;
    }

    /**Returns a list containing coords of every field to be destroyed
     * together with given tile only includes a single instance of each tile*/
    public ArrayList<Coord> fieldsToDestroy(Coord tile)
    {
        ArrayList<ArrayList<Coord>> fieldsCollections = getPatternsContaining(tile);
        ArrayList<Coord> fieldsToDestroy = new ArrayList<>();

        for (int i = 0; i < fieldsCollections.size(); i++) {
            
            // Take only patterns that have at least 'minPatternTileCount' tiles
            if(fieldsCollections.get(i).size() < this.minPatternTileCount)
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

    /** Swaps referrences in board between tiles at t1 and t2 */
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
