# connect4
Command Line Interface version of the game Connect 4

Usage: java Connect4 player-X player-O [#rows #cols]
where player-X and player-O are one of: human bad good random
[#rows #cols] are optional, if provided their values must be
in the ranges: #rows from 1 to 6 and #cols from 1 to 7

Human: The user decides the next move
Bad: The bot tries to lose
Good: The bot tries to win, using a minimum spanning tree
Random: The bot places pieces at random
