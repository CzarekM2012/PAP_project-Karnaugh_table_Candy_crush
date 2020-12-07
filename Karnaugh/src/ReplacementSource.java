package Karnaugh.src;

// Just an enum to store gravity direction
// Accesed as "ReplacementSource.TOP"
public enum ReplacementSource {
    Improper(-1), Top(1), Bottom(2), Left(3), Right(4);

    int id;

    private ReplacementSource(int id) {
        this.id = id;
    }
}