package karnaugh;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

public class KarnaughTableTest
{
    /*
    1000    0000
    0101 -> 0000
    0000    0100
    0110    1111
    */
    @Test
    public void testCollapseTopDownSquare()
    {
        KarnaughTable test = new KarnaughTable(2, 2, 2, 2, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field empty = new Field(), one = new Field(1, ReplacementSource.Top);
        Field newBoard[][] = {  {one,   empty, empty, empty},//leftmost vertical row
                                {empty, one,   empty, one},
                                {empty, empty, empty, one},
                                {empty, one,   empty, empty}};
        test.set(newBoard);
        test.collapse(ReplacementSource.Top);
        Field afterCollapse[][] = test.get();
        Field correct[][] = {   {empty, empty, empty, one},//leftmost vertical row
                                {empty, empty, one,   one},
                                {empty, empty, empty, one},
                                {empty, empty, empty, one}};
        assertArrayEquals(correct, afterCollapse);
    }

    /*
    0010    0000
    0100    0000
    0000    0000
    1001    0000
    1000    0000
    0101 -> 0100
    0000    1111
    0110    1111
    */
    @Test
    public void testCollapseTopDownVerticalRectangle()
    {
        KarnaughTable test = new KarnaughTable(2, 3, 2, 2, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field empty = new Field(), one = new Field(1, ReplacementSource.Top);
        Field newBoard[][] = {  {empty, empty, empty, one,   one,   empty, empty, empty},//leftmost vertical row
                                {empty, one,   empty, empty, empty, one,   empty, one},
                                {one,   empty, empty, empty, empty, empty, empty, one},
                                {empty, empty, empty, one,   empty, one,   empty, empty}};
        test.set(newBoard);
        test.collapse(ReplacementSource.Top);
        Field afterCollapse[][] = test.get();
        Field correct[][] = {   {empty, empty, empty, empty, empty, empty, one,   one},//leftmost vertical row
                                {empty, empty, empty, empty, empty, one,   one,   one},
                                {empty, empty, empty, empty, empty, empty, one,   one},
                                {empty, empty, empty, empty, empty, empty, one,   one}};
        assertArrayEquals(correct, afterCollapse);
    }
    
    /*
    10001100    00000100
    01011110 -> 00001100
    00000100    01001100
    01101100    11111110
    */
    @Test
    public void testCollapseTopDownHorizontalRectangle()
    {
        KarnaughTable test = new KarnaughTable(3, 2, 2, 2, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field empty = new Field(), one = new Field(1, ReplacementSource.Top);
        Field newBoard[][] = {  {one,   empty, empty, empty},//leftmost vertical row
                                {empty, one,   empty, one},
                                {empty, empty, empty, one},
                                {empty, one,   empty, empty},
                                {one,   one,   empty, one},
                                {one,   one,   one,   one},
                                {empty, one,   empty, empty},
                                {empty, empty, empty, empty}};
        test.set(newBoard);
        test.collapse(ReplacementSource.Top);
        Field afterCollapse[][] = test.get();
        Field correct[][] = {   {empty, empty, empty, one},//leftmost vertical row
                                {empty, empty, one,   one},
                                {empty, empty, empty, one},
                                {empty, empty, empty, one},
                                {empty, one,   one,   one},
                                {one,   one,   one,   one},
                                {empty, empty, empty, one},
                                {empty, empty, empty, empty}};
        assertArrayEquals(correct, afterCollapse);
    }
    
    /*
    1000    1111
    0101 -> 0100
    0000    0000
    0110    0000
    */
    @Test
    public void testCollapseDownTopSquare()
    {
        KarnaughTable test = new KarnaughTable(2, 2, 2, 2, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field empty = new Field(), one = new Field(1, ReplacementSource.Top);
        Field newBoard[][] = {  {one,   empty, empty, empty},//leftmost vertical row
                                {empty, one,   empty, one},
                                {empty, empty, empty, one},
                                {empty, one,   empty, empty}};
        test.set(newBoard);
        test.collapse(ReplacementSource.Bottom);
        Field afterCollapse[][] = test.get();
        Field correct[][] = {   {one, empty, empty, empty},//leftmost vertical row
                                {one, one,   empty, empty},
                                {one, empty, empty, empty},
                                {one, empty, empty, empty}};
        assertArrayEquals(correct, afterCollapse);
    }
    
    /*
    0010    1111
    0100    1111
    0000    0100
    1001    0000
    1000    0000
    0101 -> 0000
    0000    0000
    0110    0000
    */
    @Test
    public void testCollapseDownTopVerticalRectangle()
    {
        KarnaughTable test = new KarnaughTable(2, 3, 2, 2, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field empty = new Field(), one = new Field(1, ReplacementSource.Top);
        Field newBoard[][] = {  {empty, empty, empty, one,   one,   empty, empty, empty},//leftmost vertical row
                                {empty, one,   empty, empty, empty, one,   empty, one},
                                {one,   empty, empty, empty, empty, empty, empty, one},
                                {empty, empty, empty, one,   empty, one,   empty, empty}};
        test.set(newBoard);
        test.collapse(ReplacementSource.Bottom);
        Field afterCollapse[][] = test.get();
        Field correct[][] = {   {one, one, empty, empty, empty, empty, empty, empty},//leftmost vertical row
                                {one, one, one,   empty, empty, empty, empty, empty},
                                {one, one, empty, empty, empty, empty, empty, empty},
                                {one, one, empty, empty, empty, empty, empty, empty}};
        assertArrayEquals(correct, afterCollapse);
    }
    
    /*
    10001100    11111110
    01011110 -> 01001100
    00000100    00001100
    01101100    00000100
    */
    @Test
    public void testCollapseDownTopHorizontalRectangle()
    {
        KarnaughTable test = new KarnaughTable(3, 2, 2, 2, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field empty = new Field(), one = new Field(1, ReplacementSource.Top);
        Field newBoard[][] = {  {one,   empty, empty, empty},//leftmost vertical row
                                {empty, one,   empty, one},
                                {empty, empty, empty, one},
                                {empty, one,   empty, empty},
                                {one,   one,   empty, one},
                                {one,   one,   one,   one},
                                {empty, one,   empty, empty},
                                {empty, empty, empty, empty}};
        test.set(newBoard);
        test.collapse(ReplacementSource.Bottom);
        Field afterCollapse[][] = test.get();
        Field correct[][] = {   {one,   empty, empty, empty},//leftmost vertical row
                                {one,   one,   empty, empty},
                                {one,   empty, empty, empty},
                                {one,   empty, empty, empty},
                                {one,   one,   one,   empty},
                                {one,   one,   one,   one},
                                {one,   empty, empty, empty},
                                {empty, empty, empty, empty}};
        assertArrayEquals(correct, afterCollapse);
    }
    
    /*
    1000    0001
    0101 -> 0011
    0000    0000
    0110    0011
    */
    @Test
    public void testCollapseLeftRightSquare()
    {
        KarnaughTable test = new KarnaughTable(2, 2, 2, 2, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field empty = new Field(), one = new Field(1, ReplacementSource.Top);
        Field newBoard[][] = {  {one,   empty, empty, empty},//leftmost vertical row
                                {empty, one,   empty, one},
                                {empty, empty, empty, one},
                                {empty, one,   empty, empty}};
        test.set(newBoard);
        test.collapse(ReplacementSource.Left);
        Field afterCollapse[][] = test.get();
        Field correct[][] = {   {empty, empty, empty, empty},//leftmost vertical row
                                {empty, empty, empty, empty},
                                {empty, one,   empty, one},
                                {one,   one,   empty, one}};
        assertArrayEquals(correct, afterCollapse);
    }
    
    /*
    0010    0001
    0100    0001
    0000    0000
    1001    0011
    1000    0001
    0101 -> 0011
    0000    0000
    0110    0011
    */
    @Test
    public void testCollapseLeftRightVerticalRectangle()
    {
        KarnaughTable test = new KarnaughTable(2, 3, 2, 2, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field empty = new Field(), one = new Field(1, ReplacementSource.Top);
        Field newBoard[][] = {  {empty, empty, empty, one,   one,   empty, empty, empty},//leftmost vertical row
                                {empty, one,   empty, empty, empty, one,   empty, one},
                                {one,   empty, empty, empty, empty, empty, empty, one},
                                {empty, empty, empty, one,   empty, one,   empty, empty}};
        test.set(newBoard);
        test.collapse(ReplacementSource.Left);
        Field afterCollapse[][] = test.get();
        Field correct[][] = {   {empty, empty, empty, empty, empty, empty, empty, empty},//leftmost vertical row
                                {empty, empty, empty, empty, empty, empty, empty, empty},
                                {empty, empty, empty, one,   empty, one,   empty, one},
                                {one,   one,   empty, one,   one,   one,   empty, one}};
        assertArrayEquals(correct, afterCollapse);
    }
    
    /*
    10001100    00000111
    01011110 -> 00011111
    00000100    00000001
    01101100    00001111
    */
    @Test
    public void testCollapseLeftRightHorizontalRectangle()
    {
        KarnaughTable test = new KarnaughTable(3, 2, 2, 2, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field empty = new Field(), one = new Field(1, ReplacementSource.Top);
        Field newBoard[][] = {  {one,   empty, empty, empty},//leftmost vertical row
                                {empty, one,   empty, one},
                                {empty, empty, empty, one},
                                {empty, one,   empty, empty},
                                {one,   one,   empty, one},
                                {one,   one,   one,   one},
                                {empty, one,   empty, empty},
                                {empty, empty, empty, empty}};
        test.set(newBoard);
        test.collapse(ReplacementSource.Left);
        Field afterCollapse[][] = test.get();
        Field correct[][] = {   {empty, empty, empty, empty},//leftmost vertical row
                                {empty, empty, empty, empty},
                                {empty, empty, empty, empty},
                                {empty, one,   empty, empty},
                                {empty, one,   empty, one},
                                {one,   one,   empty, one},
                                {one,   one,   empty, one},
                                {one,   one,   one,   one}};
        assertArrayEquals(correct, afterCollapse);
    }
    
    /*
    1000    1000
    0101 -> 1100
    0000    0000
    0110    1100
    */
    @Test
    public void testCollapseRightLeftSquare()
    {
        KarnaughTable test = new KarnaughTable(2, 2, 2, 2, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field empty = new Field(), one = new Field(1, ReplacementSource.Top);
        Field newBoard[][] = {  {one,   empty, empty, empty},//leftmost vertical row
                                {empty, one,   empty, one},
                                {empty, empty, empty, one},
                                {empty, one,   empty, empty}};
        test.set(newBoard);
        test.collapse(ReplacementSource.Right);
        Field afterCollapse[][] = test.get();
        Field correct[][] = {   {one,   one,   empty, one},//leftmost vertical row
                                {empty, one,   empty, one},
                                {empty, empty, empty, empty},
                                {empty, empty, empty, empty}};
        assertArrayEquals(correct, afterCollapse);
    }
    
    /*
    0010    1000
    0100    1000
    0000    0000
    1001    1100
    1000    1000
    0101 -> 1100
    0000    0000
    0110    1100
    */
    @Test
    public void testCollapseRightLeftVerticalRectangle()
    {
        KarnaughTable test = new KarnaughTable(2, 3, 2, 2, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field empty = new Field(), one = new Field(1, ReplacementSource.Top);
        Field newBoard[][] = {  {empty, empty, empty, one,   one,   empty, empty, empty},//leftmost vertical row
                                {empty, one,   empty, empty, empty, one,   empty, one},
                                {one,   empty, empty, empty, empty, empty, empty, one},
                                {empty, empty, empty, one,   empty, one,   empty, empty}};
        test.set(newBoard);
        test.collapse(ReplacementSource.Right);
        Field afterCollapse[][] = test.get();
        Field correct[][] = {   {one,   one,   empty, one,   one,   one,   empty, one},//leftmost vertical row
                                {empty, empty, empty, one,   empty, one,   empty, one},
                                {empty, empty, empty, empty, empty, empty, empty, empty},
                                {empty, empty, empty, empty, empty, empty, empty, empty}};
        assertArrayEquals(correct, afterCollapse);
    }
    
    /*
    10001100    11100000
    01011110 -> 11111000
    00000100    10000000
    01101100    11110000
    */
    @Test
    public void testCollapseRightLeftHorizontalRectangle()
    {
        KarnaughTable test = new KarnaughTable(3, 2, 2, 2, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field empty = new Field(), one = new Field(1, ReplacementSource.Top);
        Field newBoard[][] = {  {one,   empty, empty, empty},//leftmost vertical row
                                {empty, one,   empty, one},
                                {empty, empty, empty, one},
                                {empty, one,   empty, empty},
                                {one,   one,   empty, one},
                                {one,   one,   one,   one},
                                {empty, one,   empty, empty},
                                {empty, empty, empty, empty}};
        test.set(newBoard);
        test.collapse(ReplacementSource.Right);
        Field afterCollapse[][] = test.get();
        Field correct[][] = {   {one,   one,   one,   one},//leftmost vertical row
                                {one,   one,   empty, one},
                                {one,   one,   empty, one},
                                {empty, one,   empty, one},
                                {empty, one,   empty, empty},
                                {empty, empty, empty, empty},
                                {empty, empty, empty, empty},
                                {empty, empty, empty, empty}};
        assertArrayEquals(correct, afterCollapse);
    }

    /*
        0 1 2 3 4 5 6 7
    0   1 1 1 0 0 0 0 1 G:000
    1   1 1 1 1 2 2 1 0 G:001
    2   1 1 1 1 2 1 1 0 G:011
    3   0 2 2 0 0 1 0 0 G:010
    4   0 0 0 2 0 0 0 0 G:110
    5   0 0 1 2 0 0 0 0 G:111
    6   0 2 2 2 2 1 0 0 G:101
    7   1 1 2 0 0 1 1 1 G:100
    */
    @Test
    public void testGetPatternsContainingSingular()
    {
        KarnaughTable test = new KarnaughTable(3, 3, 2, 2, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field zero = new Field(0, ReplacementSource.Top), one = new Field(1, ReplacementSource.Top), two = new Field(2, ReplacementSource.Top);
        test.clear();
        Field newBoard[][] = {  {one,  one,  one,  zero, zero, zero, zero, one},//leftmost vertical row
                                {one,  one,  one,  two,  zero, zero, two,  one},
                                {one,  one,  one,  two,  zero, one,  two,  two},
                                {zero, one,  one,  zero, two,  two,  two,  zero},
                                {zero, two,  two,  zero, zero, zero, two,  zero},
                                {zero, two,  one,  one,  zero, zero, one,  one},
                                {zero, one,  one,  zero, zero, zero, zero, one},
                                {one,  zero, zero, zero, zero, zero, zero, one}};
        test.set(new Coord(0, 0), newBoard);
        ArrayList<ArrayList<Coord>> patterns = test.getPatternsContaining(new Coord(1, 3));
        ArrayList<ArrayList<Coord>> correct = new ArrayList<ArrayList<Coord>>();
        correct.add(new ArrayList<Coord>(Arrays.asList(new Coord[] {new Coord(1, 3), new Coord(2, 3)})));
        assertEquals(correct, patterns);
    }
    @Test
    public void testGetPatternsContainingSingularTooSmall()
    {
        KarnaughTable test = new KarnaughTable(4, 4, 4, 4, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field zero = new Field(0, ReplacementSource.Top), one = new Field(1, ReplacementSource.Top), two = new Field(2, ReplacementSource.Top);
        test.clear();
        Field newBoard[][] = {  {one,  one,  one,  zero, zero, zero, zero, one},//leftmost vertical row
                                {one,  one,  one,  two,  zero, zero, two,  one},
                                {one,  one,  one,  two,  zero, one,  two,  two},
                                {zero, one,  one,  zero, two,  two,  two,  zero},
                                {zero, two,  two,  zero, zero, zero, two,  zero},
                                {zero, two,  one,  one,  zero, zero, one,  one},
                                {zero, one,  one,  zero, zero, zero, zero, one},
                                {one,  zero, zero, zero, zero, zero, zero, one}};
        test.set(new Coord(0, 0), newBoard);
        ArrayList<ArrayList<Coord>> patterns = test.getPatternsContaining(new Coord(1, 3));
        ArrayList<ArrayList<Coord>> correct = new ArrayList<ArrayList<Coord>>();
        assertEquals(correct, patterns);
    }
    @Test
    public void testGetPatternsContainingMultiple()
    {
        KarnaughTable test = new KarnaughTable(4, 4, 4, 4, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field zero = new Field(0, ReplacementSource.Top), one = new Field(1, ReplacementSource.Top), two = new Field(2, ReplacementSource.Top);
        test.clear();
        Field newBoard[][] = {  {one,  one,  one,  zero, zero, zero, zero, one},//leftmost vertical row
                                {one,  one,  one,  two,  zero, zero, two,  one},
                                {one,  one,  one,  two,  zero, one,  two,  two},
                                {zero, one,  one,  zero, two,  two,  two,  zero},
                                {zero, two,  two,  zero, zero, zero, two,  zero},
                                {zero, two,  one,  one,  zero, zero, one,  one},
                                {zero, one,  one,  zero, zero, zero, zero, one},
                                {one,  zero, zero, zero, zero, zero, zero, one}};
        test.set(new Coord(0, 0), newBoard);
        ArrayList<ArrayList<Coord>> patterns = test.getPatternsContaining(new Coord(6, 4));
        ArrayList<ArrayList<Coord>> correct = new ArrayList<ArrayList<Coord>>();
        correct.add(new ArrayList<Coord>(Arrays.asList(
            new Coord[] {new Coord(4, 4), new Coord(5, 4), new Coord(6, 4), new Coord(7, 4),
                         new Coord(4, 5), new Coord(5, 5), new Coord(6, 5), new Coord(7, 5)})));
        correct.add(new ArrayList<Coord>(Arrays.asList(
            new Coord[] {new Coord(6, 3), new Coord(6, 4), new Coord(7, 3), new Coord(7, 4)})));
        correct.add(new ArrayList<Coord>(Arrays.asList(
            new Coord[] {new Coord(0, 4), new Coord(0, 5), new Coord(1, 4), new Coord(1, 5),
                         new Coord(6, 4), new Coord(6, 5), new Coord(7, 4), new Coord(7, 5)})));
        correct.add(new ArrayList<Coord>(Arrays.asList(
            new Coord[] {new Coord(1, 4), new Coord(2, 4), new Coord(5, 4), new Coord(6, 4)})));
        assertTrue(coordArrayListsArrayListEquals(correct, patterns));
    }
    @Test
    public void testGetPatternsContainingMultipleWithTooSmall()
    {
        KarnaughTable test = new KarnaughTable(4, 4, 4, 8, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field zero = new Field(0, ReplacementSource.Top), one = new Field(1, ReplacementSource.Top), two = new Field(2, ReplacementSource.Top);
        test.clear();
        Field newBoard[][] = {  {one,  one,  one,  zero, zero, zero, zero, one},//leftmost vertical row
                                {one,  one,  one,  two,  zero, zero, two,  one},
                                {one,  one,  one,  two,  zero, one,  two,  two},
                                {zero, one,  one,  zero, two,  two,  two,  zero},
                                {zero, two,  two,  zero, zero, zero, two,  zero},
                                {zero, two,  one,  one,  zero, zero, one,  one},
                                {zero, one,  one,  zero, zero, zero, zero, one},
                                {one,  zero, zero, zero, zero, zero, zero, one}};
        test.set(new Coord(0, 0), newBoard);
        ArrayList<ArrayList<Coord>> patterns = test.getPatternsContaining(new Coord(6, 4));
        ArrayList<ArrayList<Coord>> correct = new ArrayList<ArrayList<Coord>>();
        correct.add(new ArrayList<Coord>(Arrays.asList(
            new Coord[] {new Coord(4, 4), new Coord(5, 4), new Coord(6, 4), new Coord(7, 4),
                         new Coord(4, 5), new Coord(5, 5), new Coord(6, 5), new Coord(7, 5)})));
        correct.add(new ArrayList<Coord>(Arrays.asList(
            new Coord[] {new Coord(0, 4), new Coord(0, 5), new Coord(1, 4), new Coord(1, 5),
                         new Coord(6, 4), new Coord(6, 5), new Coord(7, 4), new Coord(7, 5)})));
        assertTrue(coordArrayListsArrayListEquals(correct, patterns));  
    }

    private boolean coordArrayListsArrayListEquals(ArrayList<ArrayList<Coord>> expected, ArrayList<ArrayList<Coord>> actual)
    {
        if(expected.size() != actual.size())
        {
            return false;
        }
        boolean available[] = new boolean[expected.size()];
        for(int i=0; i<available.length; i++){available[i] = true;}
        for(int i=0; i<expected.size(); i++)
        {
            boolean found = false;
            int j=0;
            while(!found && j < actual.size())
            {
                if(available[i] && coordArrayListEquals(expected.get(i), actual.get(j)))
                {
                    found = true;
                    available[i] = false;
                }
                j++;
            }
            if(!found)
            {
                return false;
            }
        }
        return true;
    }

    private boolean coordArrayListEquals(ArrayList<Coord> expected, ArrayList<Coord> actual)
    {
        if(expected.size() != actual.size() || !(expected.containsAll(actual)))
        {
            return false;
        }
        return true;
    }
}