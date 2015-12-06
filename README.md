# MScProjectPrototype03
Prototype 03 for MSc Project (2nd Android Version)

##Introduction
This is the third prototype of a tile matching game designed with the idea of helping children with autism  
to recognize people's facial expressions.

##Improvements made since Prototype02
* Class structure changed to form a pattern more similar to the of MVC
* Threads now efficiently synchronized by calling wait()/notifyAll() on a lock object, rather than wasting
CPU cycles through polling
* Manipulation of the board now handled within each method rather than calling update(), which loops through
the whole grid, updataing the x,y values of each emoticon
* Implementation of a ScoreBoard object which displays the score and updates it as matches are found
* New layout to accomodate the scoreboard
* New colours selected which are predominantly tones of blue, which evidence suggests can be caliming. 




