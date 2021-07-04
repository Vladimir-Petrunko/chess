# chess
This project offers an object-oriented API for handling chess games. This API supports:
* Classes for all components of a chess game:
  * Pieces (descendants of class `Piece`),
  * `Position` with all kinds of useful functions. Encapsulation is supported and inner data cannot be accessed and altered from client's code,
  * Players (descendants of class `Player`),
  * Limited (so far) support of non-standard chess variants &ndash; Fischer random chess and chess on board sizes other than the default.

This project was made for analytical purposes, to solve problems of the following sort:
* What is the expected outcome of a random chess game?
* What is the probability that a randomly chosen chess position is legal? check? checkmate?
* Generate mate-in-K chess problems, etc.

TODO list:
* Optimization of several functions and tools within the API, so that algorithms that rely on depth-first search of the game tree would work faster,
* Enable full support of non-standard chess variants,
* Create several chess engines with various styles of play and track their performance against each other, etc.
