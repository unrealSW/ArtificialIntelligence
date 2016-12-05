/**
 * AI - Homework 05
 * This class represents a position on the board for the TicTacToe game.
 * Each position has 2 coordinates - X and Y.
 * 
 * @author Dimitar Cenkov, FN: 81000
 * @version 1.0
 */
package solution;

public class Position {

	private int coordX;
	private int coordY;

	public Position() {
	}

	public Position(int coordX, int coordY) {
		this.coordX = coordX;
		this.coordY = coordY;
	}

	public int getCoordX() {
		return coordX;
	}

	public void setCoordX(int coordX) {
		this.coordX = coordX;
	}

	public int getCoordY() {
		return coordY;
	}

	public void setCoordY(int coordY) {
		this.coordY = coordY;
	}
}
