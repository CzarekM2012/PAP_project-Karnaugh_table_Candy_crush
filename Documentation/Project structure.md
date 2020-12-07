# Structure of the Karnaugh Table game:
## Classes:
- App.java - all the UI and some gameplay elements are coded here
- KarnaughTable.java - the underlying structure - Karnaugh Table - with associated methods
- Field.java - a small class to store all data of one Karnaug Table tile
- Coord.java - a simple implementation of Point
- ReplacementSource.java - an enum for gravity calculation (not much used as of yet)
- ScoreBuffer.java - a FIFO Buffer to keep last-destroyed tile patterns and their respective score (also not-yet-used)

## Installation:
We have yet to find an optimal installation method or package. For now, game data is compiled into a .jar file, that unfortunately doesn't include JavaFX, which has to be added manually (I didn't want to store it in the repository).
After adding the JavaFX the game can be run using a script.