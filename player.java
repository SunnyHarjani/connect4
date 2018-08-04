/*
 * player.java
 *
 * Version:
 *     $Id: player.java,v 1.2 2013/10/20 07:59:54 sdh9795 Exp $
 *
 * Revisions:
 *     $Log: player.java,v $
 *     Revision 1.2  2013/10/20 07:59:54  sdh9795
 *     Added comments
 *
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Creates the player and associates one of the 4 types of playing strategies
 * with the player
 *
 * @author      Sunny Harjani
 */

public class player {
	private char player;	//The char this object represents
	private String type;	//The player's playing style
	private char opponent;	//Who the player is playing against
	
	/*
	 * The constructor
	 * 
	 * @param	player	Sets this object's player as either X or O
	 * @param	type	Set this object's playing style
	 */
	public player ( char player, String type ) {
		this.player = player;
		if ( type.equals("bad") || type.equals("human") ||
		type.equals("good") || type.equals("random") ) {
			this.type = type;
		} else {
			game.usageError();	//The user entered an undefined player type
		}
		if ( player == 'X' ) {
			opponent = 'O';
		} else {
			opponent = 'X';
		}
	}
	
	/*
	 * @return	True if this player is a human
	 */
	public boolean isHuman() {
		return type.equals( "human" );
	}
	
	/*
	 * Calls this player's playing algorithm according to its type
	 * 
	 * @param	Game	The game board before this player has played
	 * 
	 * @return	The game board after this player has played
	 */
	public game play ( game Game ) {
		System.out.println( type + " player " + player + " moving...");
		if ( type.equals( "bad" ) ) {
			return playBad( Game );
		} else if ( type.equals( "random" ) ) {
			return playRandom( Game );
		} else {
			return playGood( Game );
		}
	}
	
	/*
	 * Called by the main method if the player is human controlled.
	 * It needed a different constructor from the others because only
	 * the human player needs the scanner to function.
	 * 
	 * @param	kboard	The user's input
	 * @param	Game	The game board before this player has played
	 * 
	 * @return	The game board after this player has played
	 */
	public game playHuman ( Scanner kboard, game Game ) {
		System.out.print( "Player " + player + 
		": Enter the column to drop your piece (-1 to quit): " );
		int col = kboard.nextInt();
		//Quits the game if the user types -1
		if ( col == -1 ) {
			Game.gameOver( player );
		} else if ( col < 0 || col > Game.getCols() - 1 ||
		!Game.drop( col, player, true ) ) {
		//Drops the player's piece in. If Game.drop says the entered column is
		//full,it recursively calls itself asking for a new column until 
		//Game.drop says the column isn't full
			System.out.println( "invalid column: " + col );
			Game = playHuman( kboard, Game );
		}
		return Game;
	}
	
	/*
	 * Drops a piece into the closes column it can
	 * 
	 * @param	Game	The game board before this player has played
	 * 
	 * @return	The game board after this player has played
	 */
	private game playBad ( game Game ) {
		int col = 0;
		while ( !Game.drop( col, player, true ) ) {
			col++;
		}
		System.out.println( Game );
		return Game;
	}
	
	/*
	 * Drops a piece into a random column
	 * 
	 * @param	Game	The game board before this player has played
	 * 
	 * @return	The game board after this player has played
	 */
	private game playRandom ( game Game ) {
		int col = ( int ) ( Math.random() * Game.getCols() );
		while ( !Game.drop( col, player, true ) ) {
			col = ( int ) ( Math.random() * Game.getCols() );
		}
		System.out.println( Game );
		return Game;
	}
	
	/*
	 * Strategically places a piece to try to win
	 * 
	 * @param	Game	The game board before this player has played
	 * 
	 * @return	The game board after this player has played
	 */
	private game playGood ( game Game ) {
		//Disables System.out.print's while testing spots
		Game.test = true;
		
		//If this player can end the game in one move, it'll do it
		for ( int i = 0; i < Game.getCols(); i++ ) {
			if ( Game.drop( i, player, false ) ) {
				if ( Game.isGameOver() ) {
					Game.removeLast();
					Game.test = false;
					Game.drop( i, player, true );
					System.out.println( Game );
					return Game;
				} else {
					Game.removeLast();
				}
			}
		}
		
		//If this player's enemy can end the game in one move, it'll steal it
		for ( int i = 0; i < Game.getCols(); i++ ) {
			if ( Game.drop( i, opponent, false ) ) {
				if ( Game.isGameOver() ) {
					Game.removeLast();
					Game.resetGameResult();
					Game.test = false;
					Game.drop( i, player, true );
					System.out.println( Game );
					return Game;
				} else {
					Game.removeLast();
				}
			}
		}
		
		/* My algorithm creates a map with Values being possible moves it can
		 * take this turn and their keys being the amount of moves it will
		 * take to end the game if this player takes that move. Then it loops
		 * through the map trying to find the smallest key, which is the
		 * fewest amount of moves it can take to end the game. Then the player
		 * plays the move(the value) associated with that number(the key).
		 */
		
		//Creates a clone of the current game to test on
		game clone = new game( Game );
		//Generate the map
		HashMap<Integer, Integer> scenarios = findBestMove( clone ); 
		//Starting with 0, keep incrementing the number by 1 until that key
		//exists in the map
		int smallestKey;
		for ( smallestKey = 0; !scenarios.containsKey( smallestKey ); 
		smallestKey++ ) {}
		Game.test = false;
		//Play the turn using the value associated with the smallest key
		Game.drop( scenarios.get( smallestKey ), player, true );
		System.out.println( Game );
		return Game;
	
	}
	
	/*
	 * The helper method to generate the map used by good player
	 * 
	 * @param	Game	The game board before this player has played
	 * 
	 * @return	The completed map
	 */
	private HashMap<Integer, Integer> findBestMove ( game Game ) {
		ArrayList<Integer> possibleMoves = Game.possibleMoves();
		HashMap<Integer, Integer> scenarios = new HashMap<Integer, Integer>();
		for ( int startingMove : possibleMoves ) {
			/*
			 * The values in scenarios will only be the moves the player can
			 *
			 * make right now. There will be several keys linking to these
			 * values representing the amount of moves it'll take to end the
			 * game for each possible move in the entire game.
			 */
			scenarios = bestMove( 0, Game.possibleMoves(), new game(Game),
			startingMove, scenarios);
		}
		return scenarios;
	}
	
	/*
	 * This recursive method populates the map for good player
	 * 
	 * @param	movesTaken	The amount of times this method has called itself
	 * 						aka how many moves its taken to get to this one
	 * @param 	possibleMoves	The moves the player can make if it did the move
	 * 							tested in the last time this method called itself
	 * @param	Game			The hypothetical game if the player took the move
	 * 							the method tested for before it called itself
	 * @param 	startingMove	The original move the method took to get here
	 * @param	map				The map in progress of being populated
	 * 
	 * @return	The completed map
	 */
	private HashMap<Integer, Integer> bestMove ( int movesTaken, 
	ArrayList<Integer> possibleMoves, game Game, int startingMove,
	HashMap<Integer, Integer> map ) {
		Game.resetGameResult();
		//Base Case: the hypothetical board is full and there are no more
		//possible moves it can test for
		if ( possibleMoves.isEmpty() ) {
			return map;
		} else {
			//The method drops a piece anywhere its possible then calls itself
			//but with an updates Game
			for ( int i : possibleMoves ) {
				Game.drop( i, player, false );
				//If the move it took just won/tied the game, it'll add the
				//amount of moves it took to reach that point and the move it
				//originally took in the beginning as a key, value in the map
				if ( Game.isGameOver() ) {
					map.put( movesTaken, startingMove);
				}
				map = bestMove( movesTaken + 1, Game.possibleMoves(), Game,
				startingMove, map );
				Game.removeLast();
			}
		return map;
		}
	}
}
