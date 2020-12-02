class Coord
{
    public int x, y;
    public Coord()
    {
        set(-1, -1);
    }
    public Coord(int x, int y)
    {
        set(x, y);
    }
    public Coord(Coord coord)
    {
        set(coord);
    }
    public void addTo(Coord addendCoord)
    {
        x+=addendCoord.x;
        y+=addendCoord.y;
    }
    public Coord add(Coord addendCoord)
    {
        return new Coord(x+addendCoord.x, y+addendCoord.y);
    }
    public void set(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    public void set(Coord coord)
    {
        this.x = coord.x;
        this.y = coord.y;
    }
    public boolean equals(Coord coord)
    {
        return(this.x == coord.x && this.y == coord.y);
    }
}