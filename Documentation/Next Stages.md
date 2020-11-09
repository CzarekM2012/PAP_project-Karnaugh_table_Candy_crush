Next Stages
===========
Stage 2. - A prototype of bejeweled-like game on a Karnaugh map.
---------
- played like bejeweled, swapping nearby tiles on a grid
- when at least 4 tiles are placed into a correct pattern, all tiles that form a pattern disappear
- they are replaced by new ones falling from top or from a side
- could be played indefinitely, needs a loss and a win condition:
  - score and move thresholds
  - when no pattern-completing move can be made
  - something else
- can have varying difficulty:
  - 0 and 1 can be replaced with more values (or colored tiles)
  - Karnaugh map dimension can be increased (~ form 3 to 6)
  - move limit can get lower (if there is any)
- prototype needs to be playable
- doesn't have to have sophisticated graphics
- some mechanics may be subject to change

Stage 3. - Further development of the 'Karnaugh' game
-----------------------------------------------------
- increase graphical fidelity
- maybe implement new mechanics
- general polish
- maybe even release it somewhere if it turns out good enough


It can happen that the game doesn't turn out as good as we've anticipated. In such case in Stage 3. we would rather turn our focus to the Web app or maybe try to make a decent Web-based port or a landing page for the game.

Stage 4. - A SmartHome-type front for a Raspberry Pi
----------------------------------------------------
- a webpage that can be used to communicate with a Raspberry Pi
- preferably NOT hosted on Raspberry Pi (external IP adresses do cost something)
- a SmartHome-style website
- the website should allow for some basic remote control:
  - light control
  - viewing temperature readings
  - checking if power is on
  - changing time setting for a water filter flush
  - remote heat pump control (may prove unfeasible, as the manufacturer doesn't provide any easy way to interface with it)
  - other ideas will surely surface while working on the project