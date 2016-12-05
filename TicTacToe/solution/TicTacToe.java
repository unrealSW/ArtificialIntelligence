/**
 * AI - Homework 05
 * Solution to the TicTacToe game with MiniMax algorithm with Alpha–beta pruning.
 * The task was not as easy as I thought. I did some research and then implemented 
 * this rather "heavy" solution. I tried to keep it as much OOP-oriented, as possible.
 * There are quite a lot "magic" number, but this time I decided not to  handle them
 * as constants to keep the code simpler for me. There is an option if the player wants
 * to be first or second. As far as I tested, the bot cannot be defeated and the program
 * works fast enough.
 * 
 * @author Dimitar Cenkov, FN: 81000
 * @version 1.0
 */
package solution;

import java.util.ArrayList;
import java.util.Scanner;

public class TicTacToe {

	public static final int BOARD_SIZE = 3;

	// Take an instance of Scanner, to work with for the whole class.
	private Scanner scanner = new Scanner(System.in);

	// Store potential moves for the current state of game.
	private ArrayList<State> possibleNextMoves = new ArrayList<State>();

	/**
	 * TicTacToe - win condition checks:
	 */
	// Check if the given state is a winning one.
	public boolean checkWin(State state) {
		return checkWinRow(state) || checkWinColumn(state) || checkWinDiagonal(state);
	}

	// Check if all elements on the same row are equal.
	public boolean checkWinRow(State state) {
		String[][] board = state.getBoard();
		for (int i = 0; i < board.length; i++) {
			int j = 0;
			if (board[i][j] != null && board[i][j] == board[i][j + 1] && board[i][j] == board[i][j + 2]) {
				return true;
			}
		}
		return false;
	}

	// Check if all elements on the same column are equal.
	public boolean checkWinColumn(State state) {
		String[][] board = state.getBoard();
		for (int j = 0; j < board.length; j++) {
			int i = 0;
			if (board[i][j] != null && board[i][j] == board[i + 1][j] && board[i][j] == board[i + 2][j]) {
				return true;
			}
		}
		return false;
	}

	// Check if all elements on each diagonal are equal.
	public boolean checkWinDiagonal(State state) {
		String[][] board = state.getBoard();
		int i = 0;
		int j = 0;
		if (board[i][j] != null && board[i][j] == board[i + 1][j + 1] && board[i][j] == board[i + 2][j + 2]) {
			return true;
		}
		int k = 0;
		int l = board.length - 1;
		if (board[k][l] != null && board[k][l] == board[k + 1][l - 1] && board[k + 1][l - 1] == board[k + 2][l - 2]) {
			return true;
		}
		return false;
	}

	public boolean isLeaf(State state) {
		return checkWin(state) || getAvailablePositions(state).size() == 0;
	}

	// Initialize all available positions.
	public ArrayList<Position> getAvailablePositions(State state) {
		ArrayList<Position> availableMoves = new ArrayList<Position>();
		String[][] board = state.getBoard();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				if (board[i][j] == null) {
					availableMoves.add(new Position(j, i)); // strange
				}
			}
		}
		return availableMoves;
	}

	/**
	 * A simple heuristic function that evaluates states that are at the lowest
	 * depth(that are leaves). If the state has next player market as O(player),
	 * it means that the current player in this leaf is the bot, so he "wins" 1
	 * point. If the nextPlayer is X, then this leaf was played by the human
	 * player and the bot "loses" 1 point. If its a draw, 0 is assigned.
	 * 
	 * @param state
	 * @return heuristicValue
	 */
	public int evaluateHeuristicValue(State state) {
		if (state.getNextPlayer() == "X" && this.checkWin(state)) {
			return -1;
		}
		if (state.getNextPlayer() == "O" && this.checkWin(state)) {
			return 1;
		}
		return 0;
	}

	// Getter for Alpha-value for the Alpha–beta pruning.
	public int getAlpha(State state) {
		if (isLeaf(state)) {
			return evaluateHeuristicValue(state);
		}
		return Integer.MIN_VALUE;
	}

	// Getter for Beta-value for the Alpha–beta pruning.
	public int getBeta(State state) {
		if (isLeaf(state)) {
			return evaluateHeuristicValue(state);
		}
		return Integer.MAX_VALUE;
	}

	/**
	 * MiniMax algorithm with Alpha–beta pruning implemented in the next four
	 * methods.
	 */
	public int getMiniMaxAlphaBeta(State state, int alpha, int beta) {
		if (isLeaf(state)) {
			return miniMaxLeaf(state);
		} else if (state.getNextPlayer().equals("O")) {
			return minimazingValue(state, alpha, beta);
		} else {
			return maximazingValue(state, alpha, beta);
		}
	}

	public int minimazingValue(State state, int alpha, int beta) {
		ArrayList<State> allSuccessors = getAllSuccessors(state);
		for (int i = 0; i < allSuccessors.size(); i++) {
			State s = allSuccessors.get(i);
			int currentMin = getMiniMaxAlphaBeta(s, alpha, beta);
			beta = Math.min(beta, currentMin);
			state.setHeuristicValue(Math.min(state.getHeuristicValue(), beta));
			if (alpha >= beta) {
				break;
			}
		}
		if (isDirectSuccessor(state)) {
			possibleNextMoves.add(state);
		}
		return beta;
	}

	public int maximazingValue(State state, int alpha, int beta) {
		ArrayList<State> allSuccessors = getAllSuccessors(state);
		for (int i = 0; i < allSuccessors.size(); i++) {
			State s = allSuccessors.get(i);
			int currentMax = getMiniMaxAlphaBeta(s, alpha, beta);
			alpha = Math.max(alpha, currentMax);
			state.setHeuristicValue(Math.max(state.getHeuristicValue(), alpha));
			if (alpha >= beta) {
				break;
			}
		}
		if (isDirectSuccessor(state)) {
			possibleNextMoves.add(state);
		}
		return alpha;
	}

	public int miniMaxLeaf(State state) {
		if (isDirectSuccessor(state)) {
			possibleNextMoves.add(state);
		}
		return evaluateHeuristicValue(state);
	}

	/**
	 * Check if the state is a direct successor of the previous one. If we look
	 * at the previous state as a root, than its direct successors are all
	 * states at level 1 in the current tree. They and only they are possible
	 * next states.
	 * 
	 * @param state
	 * @return true if state is direct successor
	 */
	public boolean isDirectSuccessor(State state) {
		return state.getAtDepth() == 1;
	}

	// Create successor of a given state for a new given position.
	public State getSuccessor(State state, Position pos) {
		if (isLeaf(state)) {
			return null;
		}
		State successor = new State(updateBoard(state, pos), evaluateHeuristicValue(state), state.getAtDepth() + 1,
				state.getNextPlayer().equals("X") ? "O" : "X");
		return successor;
	}

	// Initialize all available successor for all available positions.
	public ArrayList<State> getAllSuccessors(State state) {
		ArrayList<State> successors = new ArrayList<State>();
		ArrayList<Position> availableMoves = getAvailablePositions(state);

		for (int i = 0; i < availableMoves.size(); i++) {
			successors.add(getSuccessor(state, availableMoves.get(i)));
		}
		return successors;
	}

	// Fill the board of the given state on the given position.
	public String[][] updateBoard(State state, Position pos) {
		String[][] currentBoard = state.getBoard();
		int boardSize = currentBoard.length;
		String[][] newBoard = new String[boardSize][boardSize];
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++)
				newBoard[i][j] = currentBoard[i][j];
		}
		newBoard[pos.getCoordY()][pos.getCoordX()] = state.getNextPlayer();
		return newBoard;
	}

	/**
	 * After the player has chosen either to be first or not, this method starts
	 * the game.
	 * 
	 * @param first
	 */
	public void startGame(boolean first) {
		State root = new State();
		if (first) {
			root.setNextPlayer("X");
			moveBot(root);
		} else {
			root.setNextPlayer("O");
			movePlayer(root);
		}
	}

	// A move of the player.
	public void movePlayer(State state) {
		State newState = new State();
		ArrayList<Position> availableMoves = getAvailablePositions(state);
		Position newPosition = createPositionFromConsole(state, availableMoves);
		newState = getSuccessor(state, newPosition);

		printBoard(newState);

		if (checkWin(newState)) {
			System.out.println("Congratulations! You Won!");
		} else if (isLeaf(newState)) {
			System.out.println("-> Draw! <-");
		} else {
			System.out.println();
			System.out.println("Bot's turn:");
			moveBot(newState);
		}
	}

	// Create and validate new position by getting the coordinates from the
	// console as input data.
	public Position createPositionFromConsole(State state, ArrayList<Position> availableMoves) {
		Position newPosition = new Position();
		do {
			int coordX = -1;
			int coordY = -1;
			while (coordX < 0 || coordX > 2) {
				System.out.print("Enter X-coordinate[0-2]: ");
				coordX = scanner.nextInt();
			}
			while (coordY < 0 || coordY > 2) {
				System.out.print("Enter Y-coordinate[0-2]: ");
				coordY = scanner.nextInt();
			}
			newPosition.setCoordX(coordX);
			newPosition.setCoordY(coordY);
			if (!isValidMove(newPosition, availableMoves)) {
				System.out.println("Your move is invalid! Try again!");
			}
		} while (!isValidMove(newPosition, availableMoves));
		return newPosition;
	}

	// Check if the given position is a valid move.
	public boolean isValidMove(Position pos, ArrayList<Position> availableMoves) {
		for (int i = 0; i < availableMoves.size(); i++) {
			Position currentPos = availableMoves.get(i);
			if (pos.getCoordX() == currentPos.getCoordX() && pos.getCoordY() == currentPos.getCoordY())
				return true;
		}
		return false;
	}

	// A move of the bot.
	public void moveBot(State state) {
		State newState = new State();
		newState.setBoard(state.getBoard());
		newState.setNextPlayer("X");
		newState = nextMove(newState);

		printBoard(newState);

		if (checkWin(newState)) {
			System.out.println("You lost! This AI is the best!");
		} else if (isLeaf(newState)) {
			System.out.println("-> Draw! <-");
		} else {
			System.out.println("It's your turn! Please enter position coordinates:");
			movePlayer(newState);
		}
	}

	// Call the algorithm for the new state, get the best one and clear other
	// possible moves.
	public State nextMove(State state) {
		getMiniMaxAlphaBeta(state, getAlpha(state), getBeta(state));
		State newState = getBestState();
		possibleNextMoves.clear();
		return newState;
	}

	// Get the state with the best heuristic value from the possible
	// states(moves).
	public State getBestState() {
		State maxState = possibleNextMoves.get(0);
		for (int i = 0; i < possibleNextMoves.size(); i++) {
			if (maxState.getHeuristicValue() < possibleNextMoves.get(i).getHeuristicValue()) {
				maxState = possibleNextMoves.get(i);
			}
		}
		return maxState;
	}

	// Print the TicTacToe board in a fancy way.
	public void printBoard(State state) {
		String board[][] = state.getBoard();
		for (int i = 0; i < board.length; i++) {
			System.out.println("-------------");
			for (int j = 0; j < board.length; j++) {
				System.out.print("| ");
				if (board[i][j] != null) {
					System.out.print(board[i][j]);
				} else {
					System.out.print(" ");
				}
				System.out.print(" ");
			}
			System.out.println("|");
		}
		System.out.println("-------------");
	}

	// Main function - run the game.
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		boolean botFirst = true;
		String input = "";
		while (true) {
			while (!input.equalsIgnoreCase("y") && !input.equalsIgnoreCase("n")) {
				System.out.println("Do you want to play first? [y/n]: ");
				input = sc.nextLine();
			}
			if (input.equals("y")) {
				botFirst = false;
			} else {
				botFirst = true;
			}
			TicTacToe game = new TicTacToe();
			game.startGame(botFirst);
			input = "";
			while (!input.equalsIgnoreCase("y") && !input.equalsIgnoreCase("n")) {
				System.out.println("Do you want to play again? [y/n]: ");
				input = sc.nextLine();
			}
			if (input.equalsIgnoreCase("n")) {
				break;
			}
			input = "";
		}
		System.out.println("Thank you for playing with me!");
		sc.close();
	}
}
