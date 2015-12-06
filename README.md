# MScProjectPrototype03
Prototype 03 for MSc Project (2nd Android Version)

##Introduction
This is the third prototype of a tile matching game that I wil be working on until September 2016 for my 
MSc Computer Science final project. It is designed to help children with autism to recognize people's 
facial expressions.

The game is in the popular genre of the matching-tile game. The idea is to make a connection of 
3 or more emoticons of the same type by swapping adjacent pairs on the board. When a match is made, the 
matching emoticons are removed and those above drop down accordingly, then new randomly generated emoticons
fill the vacant spaces on the board

It is hoped that by playing the game and focusing on the different expressions, and hearing that emotion names
when a match is made, that children will be able to practice recognizing and differentiating between the
emoticons. To aid this, each emotion is emphasized through a combination of graphics, sounds, and points.

##Improvements made since Prototype02
* Class structure changed to form a pattern more similar to that of MVC
* Threads now efficiently synchronized by calling wait()/notifyAll() on a lock object, rather than wasting
CPU cycles through polling
* Manipulation of the board now handled within each method rather than calling update(), which loops through
the whole grid, updataing the x,y values of each emoticon
* Implementation of a ScoreBoard object which displays the score and updates it as matches are found
* New layout to accomodate the scoreboard
* New colours selected which are predominantly tones of blue, which evidence suggests can be caliming. 




