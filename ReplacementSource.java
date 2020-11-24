public enum ReplacementSource
{
    Improper(-1), Top(1), Bottom(2), Left(3), Right(4);
    int id;
    private ReplacementSource(int id)
    {
        this.id = id;
    }
}