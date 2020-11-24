import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

class KarnaughTable
{
    int xDimension, yDimension, FieldValuesNumber;
    int toGrey[];
    Set<ReplacementSource> replacementSourcessSet;
    Field board[][];
    public KarnaughTable(int BitsNumber, int FieldValuesNumber, Set<ReplacementSource> replacementSourcessSet)
    {
        if(BitsNumber<=CountBits(Integer.MAX_VALUE)*2);
        {
            int xPow=BitsNumber/2, yPow=xPow;
            if((BitsNumber&1)==1)
            {
                xPow++;
            }
            this.xDimension = (int)Math.pow(2, xPow);
            this.yDimension = (int)Math.pow(2, yPow);
            this.toGrey = new int[xDimension];
            SetupArrayIndexToGreyCode();
            this.FieldValuesNumber = FieldValuesNumber;
            this.replacementSourcessSet = replacementSourcessSet;
            this.board = new Field[xDimension][yDimension];
            for(int i=0; i<xDimension; i++)
            {
                for(int j=0; j<yDimension; j++)
                {
                    board[i][j] = new Field();
                }
            }
            FillWithRandoms();
        }
    }
    private int CountBits(int number)
    {
        int bits=0;
        while(number!=0)
        {
            bits+=(number&1);
            number>>>=1; //zero-fill right shift
        }
        return bits;
    }
    private void SetupArrayIndexToGreyCode()
    {
        toGrey[0]=0;
        toGrey[1]=1;
        int leftMostBit = 2, j=1;;
        for(int i=2; i<xDimension; i++)
        {
            if(CountBits(i)==1)
            {
                leftMostBit=i;
                j=i-1;
            }
            toGrey[i] = leftMostBit + toGrey[j];
            j--;
        }
    }
    private void FillWithRandoms()
    {
        Random generator = new Random();
        int size = replacementSourcessSet.size();
        ReplacementSource array[] = new ReplacementSource[size];
        replacementSourcessSet.toArray(array);
        Field emptyField = new Field();
        for(int i=0; i<xDimension; i++)
        {
            for(int j=0; j<yDimension; j++)
            {
                if(board[i][j].equals(emptyField))
                {
                    board[i][j].set(generator.nextInt(FieldValuesNumber), array[generator.nextInt(size)]);
                }
            }
        }
    }
    public Coord[][] Collapse(ReplacementSource replacementSource)
    {
        Coord fieldsMovementBoard[][] = new Coord[xDimension][yDimension];
        for(int i=0; i<xDimension; i++)
        {
            for(int j=0; j<yDimension; j++)
            {
                fieldsMovementBoard[i][j] = new Coord();
            }
        }
        Coord currentPosition, collapser=new Coord(), move, nextLine, fall;
        Field clearField = new Field();
        switch(replacementSource.id)
        {
            case 1://Spawn at top -> collapse down
                currentPosition=new Coord(0, yDimension-2);
                move=new Coord(1,0);
                nextLine=new Coord(-xDimension, -1);
                fall=new Coord(0, 1);
                break;
            case 2://spawn at bottom -> collapse up
                currentPosition=new Coord(0, 1);
                move=new Coord(1,0);
                nextLine=new Coord(-xDimension, 1);
                fall=new Coord(0, -1);
                break;
            case 3://spawn at left -> collapse right
                currentPosition=new Coord(xDimension-2, 0);
                move=new Coord(0, 1);
                nextLine=new Coord(-1, -yDimension);
                fall=new Coord(1, 0);
                break;
            default://4-spawn at right -> collapse left
                currentPosition=new Coord(1, 0);
                move=new Coord(0, 1);
                nextLine=new Coord(1, -yDimension);
                fall=new Coord(-1, 0);
                break;
        }
        while(currentPosition.x>=0 && currentPosition.x<xDimension && currentPosition.y>=0 && currentPosition.y<yDimension)
        {
            collapser.set(currentPosition.x + fall.x, currentPosition.y + fall.y);
            if(!board[currentPosition.x][currentPosition.y].equals(clearField) && board[collapser.x][collapser.y].equals(clearField))
            {
                while(collapser.x>0 && collapser.x<xDimension-1 && collapser.y>0 && collapser.y<yDimension-1 && board[collapser.x + fall.x][collapser.y + fall.y].equals(clearField))
                {
                    collapser.addTo(fall);
                }
                Field temp = board[collapser.x][collapser.y];
                board[collapser.x][collapser.y] = board[currentPosition.x][currentPosition.y];
                board[currentPosition.x][currentPosition.y] = temp;
                fieldsMovementBoard[currentPosition.x][currentPosition.y].set(collapser.x, collapser.y);
            }
            currentPosition.addTo(move);
            if(currentPosition.x==xDimension || currentPosition.y==yDimension)
            {
                currentPosition.addTo(nextLine);
            }
        }
        return fieldsMovementBoard;
    }
    /*private boolean IsMovePossible()
    {

    }*/
    public ReplacementSource DestroyFields(Coord array[])
    {
        int replacementSourcesCounts[] = {0,0,0,0,0};
        for(int i=0; i<array.length; i++)
        {
            replacementSourcesCounts[board[array[i].x][array[i].y].replacementSource.id]++;
            board[array[i].x][array[i].y].clear();
        }
        int max=0, index=0;
        for(int i=1; i<5; i++)
        {
            if(max<replacementSourcesCounts[i])
            {
                max = replacementSourcesCounts[i];
                index = i;
            }
        }
        switch(index)
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
    /*public Coord[] PossibleMoves(Coord move)
    {

    }*/
    public void Print()
    {
        for(int i=0; i<yDimension; i++)
        {
            for(int j=0; j<xDimension; j++)
            {
                System.out.print(board[j][i].value);
                switch(board[j][i].replacementSource.id)
                {
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
    public static void main(String args[])
    {
        Set<ReplacementSource> temp = new HashSet<ReplacementSource>();
        temp.addAll(Arrays.asList(new ReplacementSource[] {ReplacementSource.Top, ReplacementSource.Bottom}));
        temp.addAll(Arrays.asList(new ReplacementSource[] {}));
        KarnaughTable test = new KarnaughTable(6, 3, temp);
        test.Print();
        System.out.println();
        Coord array[] = new Coord[4];
        array[0] = new Coord(1, 1);
        array[1] = new Coord(1, 2);
        array[2] = new Coord(2, 1);
        array[3] = new Coord(2, 2);
        ReplacementSource spawnDirection=test.DestroyFields(array);
        test.Print();
        System.out.println();
        test.Collapse(spawnDirection);
        test.Print();
        System.out.println();
        test.FillWithRandoms();
        test.Print();
    }
}