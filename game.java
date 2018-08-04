/*
 * game.java
 *
 * Version:
 *     $Id: game.java,v 1.3 2013/10/20 07:59:54 sdh9795 Exp $
 *
 * Revisions:
 *     $Log: game.java,v $
 *     Revision 1.3  2013/10/20 07:59:54  sdh9795
 *     Added comments
 *
 */

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Creates the wrapped 2-d int array that represents the board and lets players
 * 'drop' in chars and check if they won
 *
 * @author      Sunny Harjani
 */

public class game {
	private char[][] board; //Holds the chars in the board
	private char lastPlayer;//The last player who dropped a piece
	private int lastCol;    //The last column used
	private int rows, cols; //The number of rows & cols in the board
	private int gameResult = 0; // =0 if game in progress
								// =-1 if full full board
								// Between 10-19 if Horizontal Win
								// Between 20-29 if Vertical Win
								// =30 if Diagonal Win
	public boolean test = false;//Disables win messages if true
	
	/*
	 * No-args constructor
	 */
	public game() {
		this( 4, 4 );
	}
	
	/*
	 * Constructor
	 * 
	 * @param 	r	# of rows
	 * @param	c 	# of cols
	 */
	public game( int r, int c ) {
		if ( r < 1 || r > 6 || c < 1 || c > 7 ) {
			usageError();
		} else {
			board = new char[r][c];
			for ( char[] row : board ) {
				Arrays.fill( row, ' ');
			}
			rows = r;
			cols = c;
		}
	}
	
	/*
	 * Makes a clone of a game, for testing purposes
	 * 
	 * @param	Clone	The game to be cloned
	 */
	public game ( game Clone) {
		this( Clone.getRows(), Clone.getCols() );
		test = true;
		for ( int r = 0; r < rows; r++ ) {
			for ( int c = 0; c < cols; c++ ) {
				board[r][c] = Clone.getBoard( r, c );
			}
		}
	}
	
	/*
	 * @return The a location on the board
	 */
	public char getBoard( int r, int c ) {
		return board[r][c];
	}
	
	/*
	 * @return Number of rows
	 */
	public int getRows() {
		return rows;
	}
	
	/*
	 * @return Number of cols
	 */
	public int getCols() {
		return cols;
	}
	
	/*
	 * Resets the game's status if someone won
	 */
	public void resetGameResult() {
		gameResult = 0;
	}
	
	/*
	 * @return An ArrayList containing the columns that aren't full
	 */
	public ArrayList<Integer> possibleMoves () {
		ArrayList<Integer> possible = new ArrayList<Integer>();
		for ( int i = 0; i < cols; i++ ) {
			if ( board[0][i] == ' ' ) {
				possible.add( i );
			}
		}
		return possible;
	}
	
	/*
	 * Drops a piece in the board
	 * 
	 * @param	col		the column to drop into
	 * @param	player	the char to drop in
	 * @param	print	whether or not the result should be printed out
	 * 
	 * @return	false if the column is full
	 */
	public boolean drop ( int col, char player, boolean print ) {
		if ( board[0][col] == ' ') {
			lastPlayer = player;
			lastCol = col;
			for ( int i = rows - 1; i >= 0; i-- ) {
				if ( board[i][col] == ' ' ) {
					board[i][col] = player;
					if ( print ) {
						System.out.println( "Player drops an " + player + " piece into column: " + col );
					}
					checkWin();
					return true;
				}
			}
		}
		return false;
	}
	
	/*
	 * Removes the last piece dropped in
	 */
	public void removeLast() {
		removeLast( lastCol );
	}
	
	/*
	 * Removes the last piece dropped in a specific column
	 * 
	 * @param	col		The column to remove from
	 */
	private void removeLast ( int col ) {
		for ( int i = 0; i < rows; i++ ) {
			if ( board[i][col] != ' ' ) {
				board[i][col] = ' ';
				return;
			}
		}
		return;
	}
	
	/*
	 * Checks if the last player to drop a piece won
	 */
	private void checkWin() {
		checkWin( lastPlayer );
	}
	
	/*
	 * Check if a specified player won
	 * 
	 * @param	player	The player to check for
	 */
	private void checkWin ( char player ) {
		//Check Horizontal
		for ( int i = 0; i < rows; i++ ) {
			for ( int j = 0; j < cols - 3; j++ ) {
				if ( board[i][j] == player && board[i][j + 1] == player &&
				board[i][j + 2] == player && board[i][j + 3] == player ) {
					gameResult = i + 10;
					gameOver();
					return;
				}
			}
		}
		
		//Check Vertical
		for ( int i = 0; i < cols; i++ ) {
			for ( int j = 0; j < rows - 3; j++) {
				if ( board[j][i] == player && board[j + 1][i] == player &&
				board[j + 2][i] == player && board[j + 3][i] == player ) {
					gameResult = i + 20;
					gameOver();
					return;
				}
			}
		}
		
		/*
		 * Citation-
		 * Diagonal check based off:
		 * http://answers.yahoo.com/question/index?qid=20120310103051AAJV5xG
		 */
		//Check Diagonal- Bottom Left to Top Right
		for ( int i = 0; i < rows - 3; i++) {
			for ( int j = 0; j < cols - 3; j++) {
				if ( board[i][j] == player && board[i + 1][j + 1] == player &&
				board[i + 2][j + 2] == player && board[i + 3][j + 3] == player ) {
					gameResult = 30;
					gameOver();
					return;
				}
			}
		}
		
		//Check Diagonal- Top Left to Bottom Right
		for ( int i = rows - 1; i > 2; i-- ) {
			for ( int j = 0; j < cols - 3; j++ ) {
				if ( board[i][j] == player && board[i - 1][j + 1] == player &&
				board[i - 2][j + 2] == player && board[i - 3][j + 3] == player ) {
					gameResult = 30;
					gameOver();
					return;
				}
			}
		}
		
		//Check Tie
		for ( int i = 0; i < cols; i++ ) {
			if ( board[0][i] == ' ') {
				return;
			}
		}
		gameResult = -1;
		gameOver();
		return;
	}
	
	/*
	 * @return True if the game is tied or someone won
	 */
	public boolean isGameOver() {
		return ( gameResult != 0 );
	}
	
	/*
	 * Prints the player that won and how it won
	 */
	public void gameOver() {
		if ( !test ) {
			if ( gameResult == -1 ) {
				System.out.println( "Its a tie, no one wins" );
			} else if ( gameResult == 30 ) {
				System.out.println( lastPlayer + " won on a diagonal" );
				return;
			} else if ( gameResult > 19 ) {
				System.out.println( lastPlayer + " won in column " + gameResult % 10 );
			} else if ( gameResult > 9 ) {
				System.out.println( lastPlayer + " won in row " + gameResult % 10 );
			}
		}
		return;
	}
	
	/*
	 * Called if human inputs -1 to exit the program early
	 */
	public void gameOver( char player ) {
		System.out.println( player + " quits the game" );
		System.out.println( toString() );
		System.exit(0);
	}

	/*
	 * Called if wrong number of args, out of range board size,
	 * or invalid player type
	 */
	public static void usageError() {
		System.err.println(
			"Usage: java Connect4 player-X player-O [#rows #cols] \n"
			+ "where player-X and player-O are one of: human bad good random \n"
			+ "[#rows #cols] are optional, if provided their values must be \n"
			+ "in the ranges: #rows from 1 to 6 and #cols from 1 to 7");
		System.exit(0);
	}
	
	/*
	 * @return the ASCII art version of the board
	 */
	public String toString() {
		String picture = "\n";
		for ( int i = 0; i < rows; i++ ) {
			for ( int j = 0; j < cols; j++ ) {
				picture += "|" + board[i][j];
			}
			picture += "| \n";
		}
		for ( int i = 0; i < cols; i++ ) {
			picture += "+-";
		}
		picture += "+\n";
		return picture;
	}
}
