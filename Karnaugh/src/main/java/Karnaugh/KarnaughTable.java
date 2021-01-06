package karnaugh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class KarnaughTable {
    private int xSize, ySize, fieldValuesNumber, minPatternTileCount, wildTileChance/** wildTileChance/1000 probability*/;
    private int indexToGrey[], greyToIndex[];
    private Set<ReplacementSource> replacementSourcesSet;
    private Field board[][];
    public static final Field emptyField = new Field(), wildField = new Field(-2, ReplacementSource.None), blockadeField = new Field(-3, ReplacementSource.None);

    public KarnaughTable(int bitsNumberX, int bitsNumberY, int fieldValuesNumber, int minPatternTileCount, float wildTileChance, Set<ReplacementSource> replacementSourcesSet) throws IllegalArgumentException, AssertionError
    {
        if (bitsNumberX <= countBits(Integer.MAX_VALUE) && bitsNumberY <= countBits(Integer.MAX_VALUE) /*physical limitation for number of tiles*/
            && !replacementSourcesSet.isEmpty() && !replacementSourcesSet.contains(ReplacementSource.Improper) && !replacementSourcesSet.contains(ReplacementSource.None) /*assigning replacement sources to tiles*/
            && countBits(minPatternTileCount) == 1 /*patterns correct according to Karnaugh Table rules*/
            && wildTileChance >= 0 && wildTileChance <= 1 /*mathematically proper probability of wildTile spawning, for the sake of gameplay do not assign values that are higher than 5%*/
            && fieldValuesNumber > 1 && minPatternTileCount < (1 << (bitsNumberX + bitsNumberY)) && minPatternTileCount > 1 /*conditions for gameplay*/)
        {
            this.xSize = 1 << bitsNumberX; // left shift - equivalent to (int)math.pow(2, bitsNumberX);
            this.ySize = 1 << bitsNumberY;
            
            this.fieldValuesNumber = fieldValuesNumber;
            this.minPatternTileCount = minPatternTileCount;
            this.wildTileChance = (int)wildTileChance * 1000;
            this.replacementSourcesSet = replacementSourcesSet;
            
            int longerEdge = xSize>ySize?xSize:ySize;
            this.indexToGrey = new int[longerEdge];
            this.greyToIndex = new int[longerEdge];
            setupArrayIndexToGreyCode(longerEdge); // Grey code caching
            for (int i = 0; i < longerEdge; i++)
            {
                greyToIndex[indexToGrey[i]] = i;
            }

            // Initialising fields
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
            if(wildTileChance < 0 || wildTileChance > 1)
            {
                throw new IllegalArgumentException("wildTileChance needs to be between 0 and 1(inclusive)");
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

    /** Randomise a board in such way that tiles don't form any pattern of size this.minPatternTileCount or smaller */
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
        List<Integer> possibleValues = new ArrayList<Integer>();
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
                
                for(int k=0; k<valuesAppearanceCounts.length; k++)
                {
                    if(valuesAppearanceCounts[k] < minNeighboursPerPattern)
                    {
                        possibleValues.add(k);
                    }
                }
                if(!(possibleValues.size()>0))
                {
                    print();
                    throw new AssertionError();
                }
                // Set a random value that doesn't create a possibility of pattern creation
                tileValue = possibleValues.get(generator.nextInt(possibleValues.size()));
                board[i][j].set(tileValue, replacementSourcesArray[generator.nextInt(numberOfReplacementSources)]);
                possibleValues.clear();
                for(int k=0; k<valuesAppearanceCounts.length; k++)
                {
                    valuesAppearanceCounts[k]=0;
                }
            }
        }
    }

    /** Fill empty tiles with random values */
    public ArrayList<Coord> fillWithRandoms()
    {
        ArrayList<Coord> filledTiles = new ArrayList<Coord>();
        Random generator = new Random();
        int numberOfReplacementSources = replacementSourcesSet.size();
        ReplacementSource replacementSourcesArray[] = new ReplacementSource[numberOfReplacementSources];
        replacementSourcesSet.toArray(replacementSourcesArray);
        for (int i = 0; i < xSize; i++)
        {
            for (int j = 0; j < ySize; j++)
            {
                if (board[i][j].equals(emptyField))
                {
                    board[i][j].set(generator.nextInt(fieldValuesNumber), replacementSourcesArray[generator.nextInt(numberOfReplacementSources)]);
                    if(generator.nextInt(1000) < wildTileChance)
                    {
                        board[i][j].set(wildField);
                    }
                    filledTiles.add(new Coord(i, j));
                }
            }
        }
        return filledTiles;
    }

    /** Runs collapse(replacementSource) 
     * @return result collapse(replacementSource) transformed into a list of coords of tiles that moved during collapse */
    public ArrayList<Coord> collapseGetTiles(ReplacementSource replacementSource) throws IllegalArgumentException
    {
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

    /** move fields in the direction from specified side of board, 
      * if there are empty fields in that direction.
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
        switch (replacementSource)
        {
            case Top:// spawn at top -> collapse down
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
            if (!board[currentPosition.x][currentPosition.y].equals(emptyField) && !board[currentPosition.x][currentPosition.y].equals(blockadeField)
                && board[collapser.x][collapser.y].equals(emptyField))
            {
                // Moves tiles in 'fall' diretion until another non-empty tile or border is found
                while (!(collapser.x == fallLimit.x || collapser.y == fallLimit.y) //one will trigger at edge, but to know which one, if with "fall" would be required, this is faster
                       && board[collapser.x + fall.x][collapser.y + fall.y].equals(emptyField))
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
        int replacementSourcesCounts[] = {0, 0, 0, 0, 0};
        for (Coord destroyed : list)
        {
            /*if(board[destroyed.x][destroyed.y].replacementSource.id != -1)
            {*/
            replacementSourcesCounts[board[destroyed.x][destroyed.y].replacementSource.id]++;
            /*}*/
            board[destroyed.x][destroyed.y].clear();
        }

        // Returns replacement source that was most numerous in destroyed tiles
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

    /**Finds all tile patterns that contain tile at coord.
     * All patterns have to be at least 'minPatternTileCount' long to be considered.
     * Requires removing redundant coords before clearing tiles. */
    public ArrayList<ArrayList<Coord>> getPatternsContaining(Coord choosenFieldCoord)
    {
        Coord greyChoosenFieldCoord = new Coord(indexToGrey[choosenFieldCoord.x], indexToGrey[choosenFieldCoord.y]);
        ArrayList<Coord> patternExpansion = new ArrayList<Coord>();
        Coord greyCodeDifference = new Coord();
        
        ArrayList<ArrayList<Coord>> patterns = getCompatiblePairsTileNeighbour(choosenFieldCoord);
        //merge pairs
        int compatibleNeighboursNumber = patterns.size();
        for(int i=0; i < compatibleNeighboursNumber; i++)
        {
            ArrayList<Coord> expander = patterns.get(i);
            for(int j=i+1; j < patterns.size(); j++)
            {
                ArrayList<Coord> expanded = patterns.get(j);
                if(!expanded.contains(expander.get(1)))
                {
                    boolean compatiblePatterns = true;
                    Coord expandingTileCoord = expander.get(1);
                    int expandedPatternValue = board[expanded.get(1).x][expanded.get(1).y].value;
                    /*
                     * ^ - XOR operator:
                     * True ^ True -> False
                     * True ^ False -> True
                     * False ^ True -> True
                     * False ^ False -> False
                     */
                    greyCodeDifference.set(greyChoosenFieldCoord.x ^ indexToGrey[expandingTileCoord.x], 
                                           greyChoosenFieldCoord.y ^ indexToGrey[expandingTileCoord.y]);
                    int k=0;
                    while(compatiblePatterns && k < expanded.size())
                    {
                        Coord inspected = new Coord(expanded.get(k));
                        /* transform "inspected" to Grey code, change bit pointed by "greyDifference" and transform to index.
                           "inspected" becomes it's own neighbour with the same bit difference in Grey Code as the one between
                           "choosenFieldCoord" and "expandingTile"*/
                        inspected.set(greyToIndex[indexToGrey[inspected.x] ^ greyCodeDifference.x],
                                      greyToIndex[indexToGrey[inspected.y] ^ greyCodeDifference.y]);
                        if(board[inspected.x][inspected.y].value != expandedPatternValue && !board[inspected.x][inspected.y].equals(wildField))
                        {
                            compatiblePatterns = false;
                        }
                        patternExpansion.add(inspected);
                        k++;
                    }
                    if(compatiblePatterns)
                    {
                        ArrayList<Coord> newCollection = new ArrayList<Coord>();
                        newCollection.addAll(expanded);
                        newCollection.addAll(patternExpansion);
                        patterns.add(newCollection);
                    }
                    patternExpansion.clear();
                }
            }
        }
        filterOutUnnecesaryPatterns(patterns);
        return patterns;
    }

    /** @return list of pairs of Coords of "tileCoord" and its neighbour that can be in the same pattern */
    private ArrayList<ArrayList<Coord>> getCompatiblePairsTileNeighbour(Coord tileCoord)
    {
        ArrayList<ArrayList<Coord>> pairs = new ArrayList<ArrayList<Coord>>();
        ArrayList<Coord> neighbouringTilesCoords = adjacentFields(tileCoord);
        Field tile = board[tileCoord.x][tileCoord.y];
        for(Coord neighbour : neighbouringTilesCoords)
        {
            Field nTile = board[neighbour.x][neighbour.y];
            if((tile.equals(wildField) || tile.value == nTile.value || nTile.equals(wildField)) && !nTile.equals(emptyField) && !nTile.equals(blockadeField))
            pairs.add(new ArrayList<Coord>(Arrays.asList(tileCoord, neighbour)));
        }
        return pairs;
    }
    
    /** Filter out subpatterns and too small patterns */
    private void filterOutUnnecesaryPatterns(ArrayList<ArrayList<Coord>> patterns)
    {
        HashSet<ArrayList<Coord>> unnecesaryPatterns = new HashSet<ArrayList<Coord>>();
        /*Since we are discarding patterns based on their size and relationship between them,
          discarding those that are too small first should speed up the whole process*/
        for(ArrayList<Coord> pattern : patterns)
        {
            if(pattern.size() < this.minPatternTileCount)
            {
                unnecesaryPatterns.add(pattern);
            }
        }
        patterns.removeAll(unnecesaryPatterns);
        unnecesaryPatterns.clear();
        for(ArrayList<Coord> containing : patterns)
        {
            if(!unnecesaryPatterns.contains(containing))
            {
                for(ArrayList<Coord> contained : patterns)
                {
                    if(containing != contained && containing.size() >= contained.size() && containing.containsAll(contained))
                    {
                        unnecesaryPatterns.add(contained);
                    }
                }
            }
        }
        patterns.removeAll(unnecesaryPatterns);
    }

    /**Returns a list containing coords of every field to be destroyed
     * together with given tile, includes a single instance of each tile*/
    public ArrayList<Coord> fieldsToDestroy(Coord tile)
    {
        ArrayList<ArrayList<Coord>> fieldsCollections = getPatternsContaining(tile);
        ArrayList<Coord> fieldsToDestroy = new ArrayList<>();

        for (ArrayList<Coord> pattern : fieldsCollections)
        {           
            for (Coord tileCoord : pattern)
            {
                if (!fieldsToDestroy.contains(tileCoord)) {
                    fieldsToDestroy.add(tileCoord);
                }
            }
        }
        return fieldsToDestroy;
    }

    public boolean isMovePossible()
    {
        boolean moveFound = false;
        int i=0, inspectedValue;
        Coord inspected = new Coord();
        while(!moveFound && i<this.xSize)
        {
            int j=0;
            while(!moveFound && j<this.ySize)
            {
                inspected.set(i, j);
                if(!board[inspected.x][inspected.y].equals(blockadeField))
                {
                    inspectedValue = board[inspected.x][inspected.y].value;
                    ArrayList<Coord> neighboursCoords = adjacentFields(inspected);
                    int neighboursNumber = neighboursCoords.size(), k=0;
                    while(!moveFound && k<neighboursNumber)
                    {
                        Coord neighbour = neighboursCoords.get(k);
                        if(inspectedValue != board[neighbour.x][neighbour.y].value
                           && !board[neighbour.x][neighbour.y].equals(blockadeField))
                        {
                            swapTiles(inspected, neighbour);
                            if(!getPatternsContaining(inspected).isEmpty())
                            {
                                moveFound = true;
                            }
                            swapTiles(inspected, neighbour);
                        }
                        k++;
                    }
                    j++;
                }
            }
            i++;
        }
        return moveFound;
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
            for (int j = 0; j < xSize; j++){
                switch(board[j][i].value)
                {
                    case -3:
                        System.out.print("b");
                        break;
                    case -2:
                        System.out.print("w");
                        break;
                    case -1:
                        System.out.print("e");
                        break; 
                    default:
                        System.out.print(board[j][i].value);
                        break;
                }
                switch(board[j][i].replacementSource.id)
                {
                    case 0:
                        System.out.print("non ");
                        break;
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
    /** set tile at coord */
    public void set(int x, int y, Field field) throws IndexOutOfBoundsException {board[x][y].set(field);}
    /** set tile at coord */
    public void set(Coord coord, Field field) throws IndexOutOfBoundsException {set(coord.x, coord.y, field);}
    /** set whole board */
    public void set(Field[][] newBoard) throws IndexOutOfBoundsException, IllegalArgumentException {
        if(newBoard.length == this.xSize && newBoard[0].length == this.ySize)
        {
            set(new Coord(0, 0), newBoard);
        }
        else
        {
            throw new IllegalArgumentException("newBoard of incorrect size");
        }
    }
    /** set part of the board */
    public void set(Coord start, Field[][] newPartOfBoard) throws IndexOutOfBoundsException{
        for (int i = 0; i < newPartOfBoard.length; i++) {
            for (int j = 0; j < newPartOfBoard[i].length; j++) {
                board[start.x + i][start.y + j].set(newPartOfBoard[i][j]);
            }
        }
    }
    public void clear() {
        for(int i=0; i<xSize; i++)
        {
            for(int j=0; j<ySize; j++)
            {
                board[i][j].clear();
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

    public int getSizeX() {return xSize;}
    public int getSizeY() {return ySize;}
    public int getBitSizeX()
    {
        int counter = 0, temp = xSize;
        while(temp!=1)
        {
            counter++;
            temp>>=1;
        }
        return counter;
    }
    public int getBitSizeY()
    {
        int counter = 0, temp = ySize;
        while(temp!=1)
        {
            counter++;
            temp>>=1;
        }
        return counter;
    }
    

    public int getTileValue(int x, int y) {return board[x][y].value;}
    public int getTileValue(Coord coord) {return getTileValue(coord.x, coord.y);}

    public int translateIndexToGrey(int index){return indexToGrey[index];}

    /** Swaps referrences in board between tiles at t1 and t2 */
    public void swapTiles(Coord t1, Coord t2) {
        Field tmp = board[t1.x][t1.y];
        board[t1.x][t1.y] = board[t2.x][t2.y];
        board[t2.x][t2.y] = tmp;
    }
}