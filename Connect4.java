/*
 * Connect4.java
 *
 * Version:
 *     $Id: Connect4.java,v 1.4 2013/10/20 07:59:53 sdh9795 Exp $
 *
 * Revisions:
 *     $Log: Connect4.java,v $
 *     Revision 1.4  2013/10/20 07:59:53  sdh9795
 *     Added comments
 *
 */

import java.util.Scanner;
 
/**
 * This program produces creates a Connect 4 game along with players X and O.
 * Players X and O play one at a time until the game is over.
 *
 * @author      Sunny Harjani
 */

public class Connect4 {
	//The keyboard input for human players
	public static Scanner kboard = new Scanner( System.in ); 
	
	/**
	 * The main program
	 * 
	 * @param args[0] Player X's type
	 * @param args[1] Player O's type
	 * @param args[2] (Optional) # of rows in board
	 * @param args[3] (Optional) # of cols in board
	 */
	public static void main(String[] args) { 
		if ( args.length == 2 || args.length == 4 ) {
			player X = new player( 'X', args[0].toLowerCase() );
			player O = new player( 'O', args[1].toLowerCase() );
			game Game = new game();
			if ( args.length == 4 ) {
				Game = new game( Integer.parseInt( args[2] ), Integer.parseInt( args[3] ) );
			}
			System.out.println( Game );

			while ( !Game.isGameOver() ) {
				if ( X.isHuman() ) {
					System.out.println( "human player X moving...");
					Game = X.playHuman( kboard, Game );
					System.out.println( Game );
				} else {
					Game = X.play( Game );
				}
				
				if ( !Game.isGameOver() ) {
					if ( O.isHuman() ) {
						System.out.println( "human player O moving...");
						Game = O.playHuman( kboard, Game );
						System.out.println( Game );
					} else {
						Game = O.play( Game );
					}
				}
			}
		} else {
			game.usageError(); //Illegal number of parameters
		}
	}
}
