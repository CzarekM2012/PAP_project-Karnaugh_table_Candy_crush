package Karnaugh.src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

class KarnaughTable {
    private int xSize, ySize, FieldValuesNumber;
    private int indexToGrey[], greyToIndex[];
    private Set<ReplacementSource> replacementSourcesSet;
    private Field board[][];

    public KarnaughTable(int BitsNumberX, int BitsNumberY, int FieldValuesNumber) throws IllegalArgumentException {

        // a quick hack
        int BitsNumber = BitsNumberX + BitsNumberY;

        // I moved it here because I still don't know how it actually works, and can probably be initialised here
        Set<ReplacementSource> replacementSourcesSet = new HashSet<ReplacementSource>();
        replacementSourcesSet.addAll(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top, ReplacementSource.Bottom }));
        replacementSourcesSet.addAll(Arrays.asList(new ReplacementSource[] {}));

        if (BitsNumber <= CountBits(Integer.MAX_VALUE) * 2 && FieldValuesNumber > 0 && !replacementSourcesSet.isEmpty()
                && !replacementSourcesSet.contains(ReplacementSource.Improper)) {
            int xPow = BitsNumber / 2, yPow = xPow;
            xPow += BitsNumber & 1;
            this.xSize = 1 << xPow; // left shift - equivalent to (int)math.pow(2, xPow);
            this.ySize = 1 << yPow;
            this.indexToGrey = new int[xSize];
            this.greyToIndex = new int[xSize];
            SetupArrayIndexToGreyCode();
            for (int i = 0; i < xSize; i++) {
                greyToIndex[indexToGrey[i]] = i;
            }
            this.FieldValuesNumber = FieldValuesNumber;
            this.replacementSourcesSet = replacementSourcesSet;
            this.board = new Field[xSize][ySize];
            for (int i = 0; i < xSize; i++) {
                for (int j = 0; j < ySize; j++) {
                    board[i][j] = new Field();
                }
            }
            FillWithRandoms();
        } else {
            if (BitsNumber > CountBits(Integer.MAX_VALUE) * 2) {
                throw new IllegalArgumentException(
                        "BitsNumber cannot be higher than 2*number_of_bits_in_Integer.MAX_VALUE");
            }
            if (FieldValuesNumber <= 0) {
                throw new IllegalArgumentException("FieldValuesNumber needs to be higher than 0");
            }
            if (replacementSourcesSet.isEmpty()) {
                throw new IllegalArgumentException("replacementSources cannot be empty");
            } else if (replacementSourcesSet.contains(ReplacementSource.Improper)) {
                throw new IllegalArgumentException("replacementSourcesSet cannot contain ReplacementSource.Improper");
            }
        }
    }

    private int CountBits(int number) {
        int bits = 0;
        while (number != 0) {
            bits += (number & 1);
            number >>>= 1; // new Field(0, ReplacementSource.Top)-fill right shift
        }
        return bits;
    }

    private void SetupArrayIndexToGreyCode() {
        indexToGrey[0] = 0;
        indexToGrey[1] = 1;
        int leftMostBit = 2, j = 1;
        ;
        for (int i = 2; i < xSize; i++) {
            if (CountBits(i) == 1) {
                leftMostBit = i;
                j = i - 1;
            }
            indexToGrey[i] = leftMostBit + indexToGrey[j];
            j--;
        }
    }

    private void FillWithRandoms() {
        Random generator = new Random();
        int size = replacementSourcesSet.size();
        ReplacementSource array[] = new ReplacementSource[size];
        replacementSourcesSet.toArray(array);
        Field emptyField = new Field();
        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                if (board[i][j].equals(emptyField)) {
                    board[i][j].set(generator.nextInt(FieldValuesNumber), array[generator.nextInt(size)]);
                }
            }
        }
    }

    public Coord[][] Collapse(ReplacementSource replacementSource)
    // moves fields in the direction opposite to the direction indicated by the
    // replacementSource
    // (if replacementSource equals ReplacementSource.Top, fields will be moving
    // down, etc.), if there are empty fields in that direction
    {
        Coord fieldsMovementBoard[][] = new Coord[xSize][ySize];
        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                fieldsMovementBoard[i][j] = new Coord();
            }
        }
        Coord currentPosition, collapser = new Coord(), move, nextLine, fall;
        Field clearField = new Field();
        switch (replacementSource.id) {
            case 1:// Spawn at top -> collapse down
                currentPosition = new Coord(0, ySize - 2);
                move = new Coord(1, 0);
                nextLine = new Coord(-xSize, -1);
                fall = new Coord(0, 1);
                break;
            case 2:// spawn at bottom -> collapse up
                currentPosition = new Coord(0, 1);
                move = new Coord(1, 0);
                nextLine = new Coord(-xSize, 1);
                fall = new Coord(0, -1);
                break;
            case 3:// spawn at left -> collapse right
                currentPosition = new Coord(xSize - 2, 0);
                move = new Coord(0, 1);
                nextLine = new Coord(-1, -ySize);
                fall = new Coord(1, 0);
                break;
            default:// 4-spawn at right -> collapse left
                currentPosition = new Coord(1, 0);
                move = new Coord(0, 1);
                nextLine = new Coord(1, -ySize);
                fall = new Coord(-1, 0);
                break;
        }
        while (currentPosition.x >= 0 && currentPosition.x < xSize && currentPosition.y >= 0
                && currentPosition.y < ySize) {
            collapser.set(currentPosition.x + fall.x, currentPosition.y + fall.y);
            if (!board[currentPosition.x][currentPosition.y].equals(clearField)
                    && board[collapser.x][collapser.y].equals(clearField)) {
                while (collapser.x > 0 && collapser.x < xSize - 1 && collapser.y > 0 && collapser.y < ySize - 1
                        && board[collapser.x + fall.x][collapser.y + fall.y].equals(clearField)) {
                    collapser.addTo(fall);
                }
                Field temp = board[collapser.x][collapser.y];
                board[collapser.x][collapser.y] = board[currentPosition.x][currentPosition.y];
                board[currentPosition.x][currentPosition.y] = temp;
                fieldsMovementBoard[currentPosition.x][currentPosition.y].set(collapser.x, collapser.y);
            }
            currentPosition.addTo(move);
            if (currentPosition.x == xSize || currentPosition.y == ySize) {
                currentPosition.addTo(nextLine);
            }
        }
        return fieldsMovementBoard;
    }

    public boolean IsMovePossible()
    // returns true if, there is field that has 2 adjacent fields with the same
    // value, false otherwise
    {
        int valuesCountArray[] = new int[this.FieldValuesNumber];
        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                List<Coord> neighbours = AdjacentFields(new Coord(i, j));
                for (int k = 0; k < neighbours.size(); k++) {
                    Coord neighbour = neighbours.get(k);
                    valuesCountArray[board[neighbour.x][neighbour.y].value]++;
                }
                for (int k = 0; k < this.FieldValuesNumber; k++) {
                    if (valuesCountArray[k] >= 2) {
                        return true;
                    }
                    valuesCountArray[k] = 0;
                }
            }
        }
        return false;
    }

    public ReplacementSource DestroyFields(List<Coord> list)
    // clears fields at Coords in list and returns value of ReplacementSource that
    // described most of them
    {
        int replacementSourcesCounts[] = { 0, 0, 0, 0, 0 };
        for (int i = 0; i < list.size(); i++) {
            Coord destroyed = list.get(i);
            replacementSourcesCounts[board[destroyed.x][destroyed.y].replacementSource.id]++;
            board[destroyed.x][destroyed.y].clear();
        }
        int max = 0, index = 0;
        for (int i = 1; i < 5; i++) {
            if (max < replacementSourcesCounts[i]) {
                max = replacementSourcesCounts[i];
                index = i;
            }
        }
        switch (index) {
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

    public ArrayList<Coord> AdjacentFields(Coord startCoord)
    // returns list of Coords of fields adjacent to field at startCoord according to
    // Karnaugh table rules
    {
        ArrayList<Coord> destinatitionList = new ArrayList<Coord>();
        Coord greyStartCoord = new Coord(indexToGrey[startCoord.x], indexToGrey[startCoord.y]);
        int modifier = indexToGrey[xSize - 1]; // singular leftmost '1' bit for index representations in Grey code for
                                               // X-axis
        /*
         * ^ - XOR operator: True ^ True -> False True ^ False -> True False ^ True ->
         * True False ^ False -> False
         */
        while (modifier != 0) {
            destinatitionList.add(new Coord(greyToIndex[greyStartCoord.x ^ modifier], startCoord.y));
            modifier >>>= 1;
        }
        modifier = indexToGrey[ySize - 1]; // singular leftmost '1' bit for index representations in Grey code for
                                           // Y-axis
        while (modifier != 0) {
            destinatitionList.add(new Coord(startCoord.x, greyToIndex[greyStartCoord.y ^ modifier]));
            modifier >>>= 1;
        }
        return destinatitionList;
    }

    // Finds all tile patterns that contain selected tile, skips ones that are fully included in other patterns
    // All patterns have to be at least 'minPatternTileCount' long to be considered
    // TODO: Fix this so that it ALWAYS works
    public ArrayList<ArrayList<Coord>> getPatternsContaining(Coord moveCoord, int minPatternTileCount) {
        int moveFieldValue = board[moveCoord.x][moveCoord.y].value;
        Coord greyMoveCoord = new Coord(indexToGrey[moveCoord.x], indexToGrey[moveCoord.y]);
        ArrayList<ArrayList<Coord>> fieldsCollections = new ArrayList<ArrayList<Coord>>();

        ArrayList<Coord> collectionPotentialNewFields = new ArrayList<Coord>();
        Coord greyDifference = new Coord();
        ArrayList<Coord> adjacentsToMoveCoord = AdjacentFields(moveCoord);
        for (int i = 0; i < adjacentsToMoveCoord.size(); i++) {
            Coord neighour = adjacentsToMoveCoord.get(i);
            if (moveFieldValue == board[neighour.x][neighour.y].value) {
                greyDifference.set(greyMoveCoord.x ^ indexToGrey[neighour.x],
                        greyMoveCoord.y ^ indexToGrey[neighour.y]); // get bit that differs between field an moveCoord
                                                                    // from its neighbour
                boolean belongsToAnyExistingCollection = false; // we will need to create new collection
                for (int j = 0; j < fieldsCollections.size(); j++) {
                    boolean belongsToThisCollection = true;
                    int k = 0, elements = fieldsCollections.get(j).size();
                    while (belongsToThisCollection && k < elements) {
                        Coord inspected = new Coord(fieldsCollections.get(j).get(k));
                        // transform inspected to Grey code, change bit pointed by greyDifference and
                        // change to index
                        inspected.set(greyToIndex[indexToGrey[inspected.x] ^ greyDifference.x],
                                greyToIndex[indexToGrey[inspected.y] ^ greyDifference.y]);
                        if (board[inspected.x][inspected.y].value != moveFieldValue) {
                            belongsToThisCollection = false;
                        }
                        collectionPotentialNewFields.add(inspected);
                        k++;
                    }
                    if (belongsToThisCollection) {
                        belongsToAnyExistingCollection = true; // no need to create new collection anymore
                        fieldsCollections.get(j).addAll(collectionPotentialNewFields);
                    }
                    collectionPotentialNewFields.clear();
                }
                if (!belongsToAnyExistingCollection) {
                    fieldsCollections.add(new ArrayList<Coord>(Arrays.asList(moveCoord, neighour)));
                }
            }
        }

        return fieldsCollections;
    }

    // Returns a list containing coords of every field to be destroyed together with given tile
    // only includes a single instance of each tile
    // TODO: add pattern lenght checking
    public ArrayList<Coord> FieldsToDestroy(Coord tile, int minPatternTileCount) {

        ArrayList<ArrayList<Coord>> fieldsCollections = getPatternsContaining(tile, minPatternTileCount);
        ArrayList<Coord> fieldsToDestroy = new ArrayList<>();

        for (int i = 0; i < fieldsCollections.size(); i++) {
            //printTiles(fieldsCollections.get(i));
            for (int j = 0; j < fieldsCollections.get(i).size(); j++) {
                Coord inspected = fieldsCollections.get(i).get(j);
                if (!fieldsToDestroy.contains(inspected)) {
                    fieldsToDestroy.add(inspected);
                }
            }
        }
        return fieldsToDestroy;
    }

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

    public void Print() {
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

    public void set(Coord coord, Field field) throws IndexOutOfBoundsException {
        board[coord.x][coord.y].set(field);
    }

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


    // Added some getters
    int getSizeX() {return xSize;}
    int getSizeY() {return ySize;}
    int getBitSizeX() {return xSize;} // Replace with correct later
    int getBitSizeY() {return ySize;}

    int getTileValue(int x, int y) {return board[x][y].value;}
    int getTileValue(Coord coord) {return board[coord.x][coord.y].value;}

    public static void main(String args[]) {
        // testing area, erase later
        KarnaughTable test = new KarnaughTable(3, 3, 3);
        test.Print();
        System.out.println();
        Coord array[] = new Coord[4];
        array[0] = new Coord(1, 1);
        array[1] = new Coord(1, 2);
        array[2] = new Coord(2, 1);
        array[3] = new Coord(2, 2);
        ReplacementSource spawnDirection = test.DestroyFields(Arrays.asList(array));
        test.Print();
        System.out.println();
        test.Collapse(spawnDirection);
        test.Print();
        System.out.println();
        test.FillWithRandoms();
        List<Coord> possibleMoves = test.AdjacentFields(new Coord(1, 1));
        Field[][] newBoard = new Field[][]
        /*
         * 00000000 01100000 01110000 00110000 00000000 00000000 00000000 00000000
         */
        { { new Field(0, ReplacementSource.Top), new Field(0, ReplacementSource.Top),
                new Field(0, ReplacementSource.Top), new Field(0, ReplacementSource.Top),
                new Field(0, ReplacementSource.Top), new Field(0, ReplacementSource.Top),
                new Field(0, ReplacementSource.Top), new Field(0, ReplacementSource.Top) },
                { new Field(0, ReplacementSource.Top), new Field(1, ReplacementSource.Top),
                        new Field(1, ReplacementSource.Top), new Field(0, ReplacementSource.Top),
                        new Field(0, ReplacementSource.Top), new Field(0, ReplacementSource.Top),
                        new Field(0, ReplacementSource.Top), new Field(0, ReplacementSource.Top) },
                { new Field(0, ReplacementSource.Top), new Field(1, ReplacementSource.Top),
                        new Field(1, ReplacementSource.Top), new Field(1, ReplacementSource.Top),
                        new Field(0, ReplacementSource.Top), new Field(0, ReplacementSource.Top),
                        new Field(0, ReplacementSource.Top), new Field(0, ReplacementSource.Top) },
                { new Field(0, ReplacementSource.Top), new Field(0, ReplacementSource.Top),
                        new Field(1, ReplacementSource.Top), new Field(1, ReplacementSource.Top),
                        new Field(0, ReplacementSource.Top), new Field(0, ReplacementSource.Top),
                        new Field(0, ReplacementSource.Top), new Field(0, ReplacementSource.Top) },
                { new Field(0, ReplacementSource.Top), new Field(0, ReplacementSource.Top),
                        new Field(0, ReplacementSource.Top), new Field(0, ReplacementSource.Top),
                        new Field(0, ReplacementSource.Top), new Field(0, ReplacementSource.Top),
                        new Field(0, ReplacementSource.Top), new Field(0, ReplacementSource.Top) },
                { new Field(0, ReplacementSource.Top), new Field(0, ReplacementSource.Top),
                        new Field(0, ReplacementSource.Top), new Field(0, ReplacementSource.Top),
                        new Field(0, ReplacementSource.Top), new Field(0, ReplacementSource.Top),
                        new Field(0, ReplacementSource.Top), new Field(0, ReplacementSource.Top) },
                { new Field(0, ReplacementSource.Top), new Field(0, ReplacementSource.Top),
                        new Field(0, ReplacementSource.Top), new Field(0, ReplacementSource.Top),
                        new Field(0, ReplacementSource.Top), new Field(0, ReplacementSource.Top),
                        new Field(0, ReplacementSource.Top), new Field(0, ReplacementSource.Top) },
                { new Field(0, ReplacementSource.Top), new Field(0, ReplacementSource.Top),
                        new Field(0, ReplacementSource.Top), new Field(0, ReplacementSource.Top),
                        new Field(0, ReplacementSource.Top), new Field(0, ReplacementSource.Top),
                        new Field(0, ReplacementSource.Top), new Field(0, ReplacementSource.Top) } };
        test.set(newBoard);
        List<Coord> list = test.FieldsToDestroy(new Coord(2, 2), 2);
        test.Print();
        System.out.println();
        test.DestroyFields(list);
        test.Print();
    }
}