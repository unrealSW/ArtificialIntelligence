/**
 * AI - Homework 05
 * This class represents a state of the TicTacToe game. Each position
 * is represented by a board, the next player that will play, heuristic
 * value and depth(the level of the tree, where this state is).
 * 
 * @author Dimitar Cenkov, FN: 81000
 * @version 1.0
 */
package solution;

public class State {

	private String[][] board;
	private String nextPlayer;
	private int heuristicValue = 0;
	private int atDepth = 0;

	public State() {
		this.board = new String[TicTacToe.BOARD_SIZE][TicTacToe.BOARD_SIZE];
	}

	public State(String[][] board, int heuristicValue, int atDepth, String nextPlayer) {
		this.board = board;
		this.heuristicValue = heuristicValue;
		this.atDepth = atDepth;
		this.nextPlayer = nextPlayer;
	}

	public String[][] getBoard() {
		return board;
	}

	public void setBoard(String[][] board) {
		this.board = board;
	}

	public String getNextPlayer() {
		return nextPlayer;
	}

	public void setNextPlayer(String nextPlayer) {
		this.nextPlayer = nextPlayer;
	}

	public int getHeuristicValue() {
		return heuristicValue;
	}

	public void setHeuristicValue(int heuristicValue) {
		this.heuristicValue = heuristicValue;
	}

	public int getAtDepth() {
		return atDepth;
	}

	public void setAtDepth(int atDepth) {
		this.atDepth = atDepth;
	}
}
