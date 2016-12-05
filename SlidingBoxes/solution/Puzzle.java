/**
 * AI - Homework 02
 * Solution to the Sliding Blocks puzzle with A* and Manhattan distances heuristic.
 * The program works for puzzles that are of type: 3, 8, 15 and 24. Not for larger.
 * All puzzles of type 3 and 8 that are solvable will get their optimal solution in less than a second.
 * Same implies for some easy 15 and 24 puzzles. For those of them that need more steps for the solution,
 * the program might take quite a long time or even the VM could run out of memory. As this problem was
 * discussed I decided to leave the implementation on this state.
 * 
 * @author Dimitar Cenkov, FN: 81000
 * @version 1.0
 */
package solution;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Puzzle {

	public static final int ONE = 1;

	// Generate the goal state according to the given puzzle size.
	public static byte[] generateGoal(int sizeofPuzzle) {
		byte[] goalTiles = new byte[sizeofPuzzle + ONE];
		for (int i = 0; i < goalTiles.length - ONE; i++) {
			goalTiles[i] = (byte) (i + ONE);
		}
		goalTiles[goalTiles.length - ONE] = 0;
		return goalTiles;
	}
	
	// Find the number of inversions in the given initial state.
	public static int inversions(byte[] initial) {
		int inversion = 0;
		for (int i = 0; i < initial.length; i++) {
			for (int j = 0; j < i; j++) {
				if (initial[i] != 0 && initial[j] != 0) {
					if (initial[i] < initial[j]) {
						inversion++;
					}
				}
			}
		}
		return inversion;
	}

	// Add a valid (non-null and not closed) successor to the A* queue.
	public static void addSuccessor(State successor, PriorityQueue<State> queue, HashSet<State> closed) {
		if (successor != null && !closed.contains(successor)) {
			queue.add(successor);
		}
	}
	
	// Main method - solving the puzzle with A* algorithm.
	public static void main(String[] args) {
		System.out.println("Please enter the type of puzzle you want to solve (example: 3; 8; 15):");
		Scanner sc1 = new Scanner(System.in);
		int sizeOfPuzzle = sc1.nextInt();
		while(sizeOfPuzzle != 3 && sizeOfPuzzle != 8 && sizeOfPuzzle != 15 && sizeOfPuzzle != 24) {
			System.out.println("Please enter correct puzzle type:");
			sizeOfPuzzle = sc1.nextInt();
		}
		byte[] goal = generateGoal(sizeOfPuzzle);
		System.out.println("Please enter the puzzle you want to solve:");
		byte[] initial = new byte[goal.length];
		Scanner sc2 = new Scanner(System.in);
		for(int i = 0; i < initial.length; i++) {
			String token = sc2.next();
			initial[i] = Byte.parseByte(token);
		}
		sc1.close();
		sc2.close();
		
		// A* priority queue.
		PriorityQueue<State> queue = new PriorityQueue<State>(new Comparator<State>() {
			
			@Override
			public int compare(State s1, State s2) {
				return s1.priority() - s2.priority();
			}
		});
		
		// The closed state set.
		HashSet<State> closed = new HashSet<State>();
		
		// Transform sizeOfPuzzle for the move-methods.
		sizeOfPuzzle = (int) Math.sqrt(sizeOfPuzzle + ONE);
		
		int inversions = inversions(initial);
		boolean evenInversions = inversions % 2 == 0;
		boolean sizeOfPuzzleEven = sizeOfPuzzle % 2 == 0;
		
		// Find if zero tile is on odd "row" for the puzzle solvability check.
		boolean blankOnOddRowThirdLast = State.getIndexOfValue(initial, 0) / sizeOfPuzzle == ONE;
		boolean blankOnOddRowLast = State.getIndexOfValue(initial, 0) / sizeOfPuzzle == 3;
		boolean blankOnOddRowFromBottom = blankOnOddRowThirdLast || blankOnOddRowLast;

		// Check if the puzzle is solvable.
		if((!sizeOfPuzzleEven && !evenInversions) || (sizeOfPuzzleEven && (evenInversions != blankOnOddRowFromBottom))) {
			System.out.println("Unsolvable puzzle. Please try again!");
		} else {

			// Add initial state to queue.
			queue.add(new State(initial));

			while(!queue.isEmpty()) {

				// Get the lowest priority state.
				State state = queue.poll();

				// If it's the goal, we're done.
				if(state.isGoal(goal)) {
					System.out.println("Minimal path length to goal: " + state.getG());
					state.printAll();
					return;
				}

				// Make sure we don't revisit this state.
				closed.add(state);
				
				// Add successors to the queue.
				addSuccessor(state.moveUp(sizeOfPuzzle), queue, closed);
				addSuccessor(state.moveDown(sizeOfPuzzle), queue, closed);
				addSuccessor(state.moveRight(sizeOfPuzzle), queue, closed);
				addSuccessor(state.moveLeft(sizeOfPuzzle), queue, closed);
			}
		}
	}
}
