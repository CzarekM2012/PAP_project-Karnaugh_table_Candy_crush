package Karnaugh;

import Karnaugh.KarnaughTable;

class KarnaughTableTest
{
    /*
    1000    0000
    0101 -> 0000
    0000    0100
    0110    1111
    */
    public void testCollapseTopDownSquare()
    {}

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
    
    public void testCollapseTopDownVerticalRectangle()
    {}
    
    /*
    10001100    00000100
    01011110 -> 00001100
    00000100    01001100
    01101100    11111110
    */
    
    public void testCollapseTopDownHorizontalRectangle()
    {}
    
    /*
    1000    1111
    0101 -> 0100
    0000    0000
    0110    0000
    */
    
    public void testCollapseDownTopSquare()
    {}
    
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
    
    public void testCollapseDownTopVerticalRectangle()
    {}
    
    /*
    10001100    11111110
    01011110 -> 01001100
    00000100    00001100
    01101100    00000100
    */
    
    public void testCollapseDownTopHorizontalRectangle()
    {}
    
    /*
    1000    0001
    0101 -> 0011
    0000    0000
    0110    0011
    */
    
    public void testCollapseLeftRightSquare()
    {}
    
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
    
    public void testCollapseLeftRightVerticalRectangle()
    {}
    
    /*
    10001100    00000111
    01011110 -> 00011111
    00000100    00000001
    01101100    00001111
    */
    
    public void testCollapseLeftRightHorizontalRectangle()
    {}
    
    /*
    1000    1000
    0101 -> 1100
    0000    0000
    0110    1100
    */
    
    public void testCollapseRightLeftSquare()
    {}
    
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
    
    public void testCollapseRightLeftVerticalRectangle()
    {}
    
    /*
    10001100    11100000
    01011110 -> 11111000
    00000100    10000000
    01101100    11110000
    */
    
    public void testCollapseRightLeftHorizontalRectangle()
    {}
}