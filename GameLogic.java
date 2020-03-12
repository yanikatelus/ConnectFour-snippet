public class GameLogic {
/**************************************************
      Yanika Telus 
      Shaneil Webley-Roberts
***************************************************/

	private GameStatus gameStatus;//obj game status 
	private static final int P1 = 1;
	private static final int P2 = 2;

	private int currentPlayer;
   
   //2d array
	private int[][] board;
	private int[] fullColumns;
   
   //constructor
	public GameLogic() {
		gameStatus = GameStatus.WAIT;
		newGame();
	}
   
   	// paly 
      //makes piece move for each part
	public boolean play(int numMoving, int player) {
		if (numMoving < 0 || numMoving > 6)
			return false;
         //checks if numMoving is a proper numMoving
		boolean valid = validateMove(numMoving);
		if (valid) {
			int boardRow = makeMove(numMoving);
			checkIfGameIsOver(numMoving, boardRow, currentPlayer);
		}
		return valid;
	}
   

	/**************************
         Checks
	 */
// 	private int bestMove(int id) {
// 		int numMoving = -1;
// 		for (int currentColumn = 0; currentColumn < 7; currentColumn++) {
// 			if (fullColumns[currentColumn] > 0) {
// 				int currentRow = fullColumns[currentColumn] + 2;
// 				if (checkPosWin(currentRow, currentColumn + 3, id)) {
// 					numMoving = currentColumn;
// 					break;
// 				}
// 			}
// 		}
// 		return numMoving;
// 	}

	/**
	 * Resets the values for a new game.
	 */
	public void newGame() {
		board = new int[12][13];
		fullColumns = new int[7];
		for (int i = 0; i < fullColumns.length; i++)
			fullColumns[i] = 6;

		currentPlayer = P1;

		gameStatus = GameStatus.PLAY_P1;
	}

	/*moves move takes an interger*/
	private int makeMove(int numMoving) {
		// fullColumns values go from 1-6, therefore not zero based.
		int row = fullColumns[numMoving] + 2;
		board[row][numMoving + 3] = currentPlayer;
		fullColumns[numMoving]--;
		return row;
	}

	/* CHecks if checker Can it be placed there */
	private boolean validateMove(int numMoving) {
		return (fullColumns[numMoving] > 0 && fullColumns[numMoving] < 7);
	}

	/* Checks if the game is over */
	private void checkIfGameIsOver(int col, int row, int player) {
		col += 3;
		boolean isDraw = true;
		for (int c : fullColumns)
			if (c != 0) {
				isDraw = false;
				break;
			}
		if (isDraw) {
			gameStatus = GameStatus.OVER_DRAW;
		}
		// Check if someone won.
		if (checkPosWin(row, col, currentPlayer)) {
			if (currentPlayer == P1) {
				gameStatus = GameStatus.OVER_P1;
			} else {
				gameStatus = GameStatus.OVER_P2;
			}
		} else {
			if (!isDraw) {
				if (currentPlayer == P1) {
					System.out.println("Now P2");
					gameStatus = GameStatus.PLAY_P2;
					currentPlayer = P2;
				} else {
					gameStatus = GameStatus.PLAY_P1;
					System.out.println("Now P1");
					currentPlayer = P1;
				}
			}
		}
	}

	/* Checks to see if the numMoving sent in is a winning numMoving */
	public boolean checkPosWin(int row, int col, int player) {
      // right to bottom left diagonal check 
      
		int checkChecker = 1;
		for (int d = 1; d < 4; d++)
			if (board[row - d][col - d] == player)
				checkChecker++;
			else
				break;

		if (checkChecker >= 4) {
			return true;
		}

		for (int d = 1; d < 4; d++)
			if (board[row + d][col + d] == player)
				checkChecker++;
			else
				break;

		if (checkChecker >= 4) {
			return true;
		}
      
		checkChecker = 1;

		// Checking VERTICAL
		for (int c = 1; c < 4; c++)
			if (board[row + c][col] == player)
				checkChecker++;
			else
				break;

		if (checkChecker >= 4) {
			return true;
		}

		// Checking HORIZONTAL
		checkChecker = 1;
		for (int a = 1; a < 4; a++)
			if (board[row][col - a] == player)
				checkChecker++;
			else
				break;

		if (checkChecker >= 4) {
			return true;
		}

		for (int i = 1; i < 4; i++)
			if (board[row][col + i] == player)
				checkChecker++;
			else
				break;
		if (checkChecker >= 4) {
			return true;
		}

		// Checking DIAGONAL: top left to bottom right
		checkChecker = 1;
		for (int d = 1; d < 4; d++)
			if (board[row - d][col + d] == player)
				checkChecker++;
			else
				break;

		if (checkChecker >= 4) {
			return true;
		}

		for (int d = 1; d < 4; d++)
			if (board[row + d][col - d] == player)
				checkChecker++;
			else
				break;

		if (checkChecker >= 4) {
			return true;
		}

		return false;
	}
	public void displayBoard() {
	}

	/*Get the status of the game*/
	public GameStatus getGameStatus() {
		return gameStatus;
	}
}

enum GameStatus {
	WAIT, PLAY_P1, PLAY_P2, OVER_P1, OVER_P2, OVER_DRAW
}

class PlayException extends RuntimeException {
	private static final long serialVersionUID = 8887181243412679206L;
	public PlayException(String message) {
		super(message);
	}
}