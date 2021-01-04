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
        KarnaughTable test = new KarnaughTable(2, 2, 10, 2, 0f, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field empty = KarnaughTable.emptyField, one = new Field(1, ReplacementSource.Top);
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
    1000    1000
    b1b1 -> b0b0
    0000    0101
    011b    011b
    */
    @Test
    public void testCollapseTopDownSquareWithBlocades()
    {
        KarnaughTable test = new KarnaughTable(2, 2, 10, 2, 0f, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field empty = KarnaughTable.emptyField, one = new Field(1, ReplacementSource.Top), block = KarnaughTable.blockadeField;
        Field newBoard[][] = {  {one,   block, empty, empty},//leftmost vertical row
                                {empty, one,   empty, one},
                                {empty, block, empty, one},
                                {empty, one,   empty, block}};
        test.set(newBoard);
        test.collapse(ReplacementSource.Top);
        Field afterCollapse[][] = test.get();
        Field correct[][] = {   {one,   block, empty, empty},//leftmost vertical row
                                {empty, empty, one,   one},
                                {empty, block, empty, one},
                                {empty, empty, one,   block}};
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
        KarnaughTable test = new KarnaughTable(2, 3, 10, 2, 0f, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field empty = KarnaughTable.emptyField, one = new Field(1, ReplacementSource.Top);
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
    0010    0000
    0100    0010
    00b0    00b0
    1001    1000
    100b    1001
    b101 -> b101
    000b    010b
    0110    0110
    */
    @Test
    public void testCollapseTopDownVerticalRectangleWithBlockades()
    {
        KarnaughTable test = new KarnaughTable(2, 3, 10, 2, 0f, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field empty = KarnaughTable.emptyField, one = new Field(1, ReplacementSource.Top), block = KarnaughTable.blockadeField;
        Field newBoard[][] = {  {empty, empty, empty, one,   one,   block, empty, empty},//leftmost vertical row
                                {empty, one,   empty, empty, empty, one,   empty, one},
                                {one,   empty, block, empty, empty, empty, empty, one},
                                {empty, empty, empty, one,   empty, one,   block, empty}};
        test.set(newBoard);
        test.collapse(ReplacementSource.Top);
        Field afterCollapse[][] = test.get();
        Field correct[][] = {   {empty, empty, empty, one,   one,   block, empty, empty},//leftmost vertical row
                                {empty, empty, empty, empty, empty, one,   one,   one},
                                {empty, one,   block, empty, empty, empty, empty, one},
                                {empty, empty, empty, empty, one,   one, block,   empty}};
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
        KarnaughTable test = new KarnaughTable(3, 2, 10, 2, 0f, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field empty = KarnaughTable.emptyField, one = new Field(1, ReplacementSource.Top);
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
    10001100    00000100
    01011110 -> 11001110
    bb0001b0    bb0011b0
    01101100    01111100
    */
    @Test
    public void testCollapseTopDownHorizontalRectangleWithBlockades()
    {
        KarnaughTable test = new KarnaughTable(3, 2, 10, 2, 0f, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field empty = KarnaughTable.emptyField, one = new Field(1, ReplacementSource.Top), block = KarnaughTable.blockadeField;
        Field newBoard[][] = {  {one,   empty, block, empty},//leftmost vertical row
                                {empty, one,   block, one},
                                {empty, empty, empty, one},
                                {empty, one,   empty, empty},
                                {one,   one,   empty, one},
                                {one,   one,   one,   one},
                                {empty, one,   block, empty},
                                {empty, empty, empty, empty}};
        test.set(newBoard);
        test.collapse(ReplacementSource.Top);
        Field afterCollapse[][] = test.get();
        Field correct[][] = {   {empty, one,   block, empty},//leftmost vertical row
                                {empty, one,   block, one},
                                {empty, empty, empty, one},
                                {empty, empty, empty, one},
                                {empty, one,   one,   one},
                                {one,   one,   one,   one},
                                {empty, one, block, empty},
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
        KarnaughTable test = new KarnaughTable(2, 2, 10, 2, 0f, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field empty = KarnaughTable.emptyField, one = new Field(1, ReplacementSource.Top);
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
        KarnaughTable test = new KarnaughTable(2, 3, 10, 2, 0f, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field empty = KarnaughTable.emptyField, one = new Field(1, ReplacementSource.Top);
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
        KarnaughTable test = new KarnaughTable(3, 2, 10, 2, 0f, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field empty = KarnaughTable.emptyField, one = new Field(1, ReplacementSource.Top);
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
        KarnaughTable test = new KarnaughTable(2, 2, 10, 2, 0f, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field empty = KarnaughTable.emptyField, one = new Field(1, ReplacementSource.Top);
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
        KarnaughTable test = new KarnaughTable(2, 3, 10, 2, 0f, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field empty = KarnaughTable.emptyField, one = new Field(1, ReplacementSource.Top);
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
        KarnaughTable test = new KarnaughTable(3, 2, 10, 2, 0f, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field empty = KarnaughTable.emptyField, one = new Field(1, ReplacementSource.Top);
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
        KarnaughTable test = new KarnaughTable(2, 2, 10, 2, 0f, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field empty = KarnaughTable.emptyField, one = new Field(1, ReplacementSource.Top);
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
        KarnaughTable test = new KarnaughTable(2, 3, 10, 2, 0f, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field empty = KarnaughTable.emptyField, one = new Field(1, ReplacementSource.Top);
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
        KarnaughTable test = new KarnaughTable(3, 2, 10, 2, 0f, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field empty = KarnaughTable.emptyField, one = new Field(1, ReplacementSource.Top);
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
        KarnaughTable test = new KarnaughTable(3, 3, 10, 2, 0f, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
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
        KarnaughTable test = new KarnaughTable(4, 4, 10, 4, 0f, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
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
        KarnaughTable test = new KarnaughTable(4, 4, 10, 4, 0f, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
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
        KarnaughTable test = new KarnaughTable(4, 4, 10, 8, 0f, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
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

    /*
        0 1 2 3 4 5 6 7
    0   1 1 1 0 0 0 0 1 G:000
    1   1 1 1 1 2 2 1 0 G:001
    2   1 1 1 1 2 1 1 0 G:011
    3   0 2 2 0 0 1 0 0 G:010
    4   0 w 0 2 0 0 0 0 G:110
    5   0 0 1 2 0 0 0 0 G:111
    6   0 2 2 2 2 1 0 0 G:101
    7   1 1 2 0 0 1 1 1 G:100
    */
    @Test
    public void testGetPatternsContainingMultipleWithWildTileAsCore()
    {
        KarnaughTable test = new KarnaughTable(3, 3, 10, 2, 0f, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field zero = new Field(0, ReplacementSource.Top), one = new Field(1, ReplacementSource.Top), two = new Field(2, ReplacementSource.Top), wild = KarnaughTable.wildField;
        test.clear();
        Field newBoard[][] = {  {one,  one,  one,  zero, zero, zero, zero, one},//leftmost vertical row
                                {one,  one,  one,  two,  wild, zero, two,  one},
                                {one,  one,  one,  two,  zero, one,  two,  two},
                                {zero, one,  one,  zero, two,  two,  two,  zero},
                                {zero, two,  two,  zero, zero, zero, two,  zero},
                                {zero, two,  one,  one,  zero, zero, one,  one},
                                {zero, one,  one,  zero, zero, zero, zero, one},
                                {one,  zero, zero, zero, zero, zero, zero, one}};
        test.set(new Coord(0, 0), newBoard);
        ArrayList<ArrayList<Coord>> patterns = test.getPatternsContaining(new Coord(1, 4));
        ArrayList<ArrayList<Coord>> correct = new ArrayList<ArrayList<Coord>>();
        correct.add(new ArrayList<Coord>(Arrays.asList(
            new Coord[] {new Coord(0, 4), new Coord(0, 5), new Coord(1, 4), new Coord(1, 5),
                         new Coord(6, 4), new Coord(6, 5), new Coord(7, 4), new Coord(7, 5)})));
        correct.add(new ArrayList<Coord>(Arrays.asList(
            new Coord[] {new Coord(1, 3), new Coord(1, 4)})));
        correct.add(new ArrayList<Coord>(Arrays.asList(
            new Coord[] {new Coord(1, 4), new Coord(1, 7)})));
        correct.add(new ArrayList<Coord>(Arrays.asList(
            new Coord[] {new Coord(1, 4), new Coord(2, 4), new Coord(5, 4), new Coord(6, 4)})));
        assertTrue(coordArrayListsArrayListEquals(correct, patterns));
    }

    @Test
    public void testGetPatternsContainingMultipleWithWildTileAsPart()
    {
        KarnaughTable test = new KarnaughTable(3, 3, 10, 2, 0f, new HashSet<ReplacementSource>(Arrays.asList(new ReplacementSource[] { ReplacementSource.Top})));
        Field zero = new Field(0, ReplacementSource.Top), one = new Field(1, ReplacementSource.Top), two = new Field(2, ReplacementSource.Top), wild = KarnaughTable.wildField;
        test.clear();
        Field newBoard[][] = {  {one,  one,  one,  zero, zero, zero, zero, one},//leftmost vertical row
                                {one,  one,  one,  two,  wild, zero, two,  one},
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
