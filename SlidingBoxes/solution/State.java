/**
 * AI - Homework 02
 * State class, representing a single state for the Sliding Blocks puzzle task.
 * 
 * @author Dimitar Cenkov, FN: 81000
 * @version 1.0
 */
package solution;

import java.util.Arrays;

public class State {
	
	public byte[] tiles; 			// Tiles left to right, top to bottom.
	private int zeroIndex; 			// Index of zero in tiles(the "blank" space).
	private int g; 					// Number of moves from start.
	private int h; 					// Heuristic value (difference from goal).
	private State previousState; 	// Previous state in solution chain.
	private String direction;		// Direction from which the state was formed.

	// Build a start state, using the given initial.
	State(byte[] initial) {
		this.tiles = initial;
		this.zeroIndex = getIndexOfValue(tiles, 0);
		this.g = 0;
		this.h = heuristic(tiles);
		this.previousState = null;
		this.direction = null;
	}

	// Build a successor to previous by sliding tile from given index.
	State(State prev, int slideFromIndex, String direction) {
		tiles = Arrays.copyOf(prev.tiles, prev.tiles.length);
		tiles[prev.zeroIndex] = tiles[slideFromIndex];
		tiles[slideFromIndex] = 0;
		this.zeroIndex = slideFromIndex;
		this.g = prev.g + 1;
		this.h = heuristic(tiles);
		this.previousState = prev;
		this.direction = direction;
	}
	
	// Get index of a given value.
	public static int getIndexOfValue(byte[] tiles, int val) {
		for(int i = 0; i < tiles.length; i++)
			if(tiles[i] == val) {
				return i;
			}
		return -1;
	}
	
	// A* priority function (the f-function = g + h).
	public int priority() {
		return g + h;
	}
	
	// Return true if this is the goal state.
	boolean isGoal(byte[] goal) {
		return Arrays.equals(tiles, goal);
	}

	// Successor states representing a move of a tile: up, down, right or left.
	public State moveDown(int n) {
		return zeroIndex > n - 1 ? new State(this, zeroIndex - n, "down") : null;
	}

	public State moveUp(int n) {
		return zeroIndex < n * n - n ? new State(this, zeroIndex + n, "up") : null;
	}

	public State moveRight(int n) {
		return zeroIndex % n > 0 ? new State(this, zeroIndex - Puzzle.ONE, "right") : null;
	}

	public State moveLeft(int n) {
		return zeroIndex % n < n - 1 ? new State(this, zeroIndex + Puzzle.ONE, "left") : null;
	}

	// For our A* heuristic, we just use max of Manhattan distances of all tiles.
	public static int heuristic(byte[] tiles) {
		int h = 0;
		for(int i = 0; i < tiles.length; i++) {
			if(tiles[i] != 0) {
				h = Math.max(h, manhattanDistance(i+1, tiles[i], tiles.length));
			}
		}
		return h;
	}

	// Return the Manhattan distance between tiles with indices i and j.
	public static int manhattanDistance(int i, int j, int dim) {
		dim = (int) Math.sqrt(dim + Puzzle.ONE);
		return Math.abs(i / dim - j / dim) + Math.abs(i % dim - j % dim);
	}
	
	// Return number of moves from start.
	public int getG(){
		return this.g;
	}
	
	// Print direction of this state.
	public void print() {
		if (direction != null) {
			System.out.println(direction);
		}
	}

	// Print the solution chain.
	public void printAll() {
		if(previousState != null) {
			previousState.printAll();
		}
		print();
		System.out.println();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof State) {
			State other = (State) obj;
			return Arrays.equals(tiles, other.tiles);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(tiles);
	}
}
