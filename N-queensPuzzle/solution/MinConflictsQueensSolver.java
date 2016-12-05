/**
 * AI - Homework 03
 * Solution to the N-queens puzzle with MinConflict algorithm.
 * The program works for puzzles that are significantly large as requested in
 * the task. I have tried to implement quite a lot optimization, so that we can
 * have good execution times for big input. For example, if we want to solve a 
 * board with size 5000 x 5000 it will take between 190 and 280 seconds. I've tested
 * it for 10k x 10k board and it takes about half an hour. The key in my optimization
 * is using only one dimensional arrays - not even ArrayLists so that we can skip the
 * unwanted AutoBoxing and AutoUnboxing.
 * 
 * @author Dimitar Cenkov, FN: 81000
 * @version 1.0
 */
package solution;

import java.util.Random;
import java.util.Scanner;

public class MinConflictsQueensSolver {

	private static class Board {
		/**
		 * The representation of a board will be with a simple array, containing
		 * the the numbers of the rows where we place a queen on each column.
		 * For example [3, 7, 0, 4, 6, 1, 5, 2] represents:
		 * 
		 * <pre>
		 *0    _ _ * _ _ _ _ _
		 *1    _ _ _ _ _ * _ _ 
		 *2    _ _ _ _ _ _ _ *
		 *3    * _ _ _ _ _ _ _
		 *4    _ _ _ * _ _ _ _
		 *5    _ _ _ _ _ _ * _
		 *6    _ _ _ _ * _ _ _
		 *7    _ * _ _ _ _ _ _
		 * </pre>
		 */
		int[] rows;

		// Take an instance of Random, to work with.
		Random random = new Random();

		// Create a new board NxN and randomly place the queens.
		Board(int n) {
			this.rows = new int[n];
			scramble();
		}

		// Place a queen on a random place on each column.
		public void scramble() {
			int n = rows.length;
			for (int i = 0; i < n; i++) {
				rows[i] = i;
			}
			for (int i = 0; i < n; i++) {
				int j = random.nextInt(n);
				int rowToSwap = rows[i];
				rows[i] = rows[j];
				rows[j] = rowToSwap;
			}
		}

		// Return the number of queens that are in conflict with (row, col), not
		// counting the queen on column col.
		public int conflicts(int row, int col) {
			int count = 0;
			for (int i = 0; i < rows.length; i++) {
				if (i == col) {
					continue;
				}
				int currentRow = rows[i];
				if (currentRow == row || Math.abs(currentRow - row) == Math.abs(i - col)) {
					count++;
				}
			}
			return count;
		}

		// Create a new array from a given one, that "has no gaps" and a smaller
		// size.
		public int[] reArray(int[] arr) {
			int count = 0;
			for (int i = 0; i < arr.length; i++) {
				if (arr[i] != 0)
					count++;
			}
			int[] newArr = new int[count];
			int index = 0;
			for (int i = 0; i < arr.length; i++) {
				if (arr[i] != 0) {
					if (arr[i] == -1)
						newArr[index++] = 0;
					else
						newArr[index++] = arr[i];
				}
			}
			return newArr;
		}

		// Solve a board using a little bit changed MinConflict algorithm.
		public void solve(int maxIter) {
			int iterations = 0;

			/**
			 * This is the array for storing the positions that have the same
			 * amount of conflicts and picking a random one from them. Because
			 * arrays in Java have fixed size there will be a lot of "gaps" in
			 * those arrays. Gaps have 0 as value by default, to be able to trim
			 * them from gaps, I swap real 0 whit -1. If this was ArrayList
			 * <Integer> it would look a lot simpler with less checks, but it
			 * would be slower.
			 */
			int[] candidatesSameConflicts;

			while (true) {

				// Find the queen with the most conflicts on the board.
				int maxConflicts = 0;
				candidatesSameConflicts = new int[rows.length];
				for (int i = 0; i < rows.length; i++) {
					int conflicts = conflicts(rows[i], i);
					if (conflicts == maxConflicts) {
						if (i == 0) {
							candidatesSameConflicts[i] = -1;
						} else {
							candidatesSameConflicts[i] = i;
						}
					} else if (conflicts > maxConflicts) {
						maxConflicts = conflicts;
						candidatesSameConflicts = new int[rows.length];
						if (i == 0) {
							candidatesSameConflicts[i] = -1;
						} else {
							candidatesSameConflicts[i] = i;
						}
					}
				}

				// If there are no conflicts at all, we are ready.
				if (maxConflicts == 0) {
					return;
				}

				// Pick a random queen from those that had the most conflicts.
				int[] reArrMax = reArray(candidatesSameConflicts);
				int worstQueenPosition = reArrMax[random.nextInt(reArrMax.length)];

				// Move her to the place with the least conflicts on her row.
				candidatesSameConflicts = new int[rows.length];
				int minConflicts = rows.length;
				for (int i = 0; i < rows.length; i++) {
					int conflicts = conflicts(i, worstQueenPosition);
					if (conflicts == minConflicts) {
						if (i == 0) {
							candidatesSameConflicts[i] = -1;
						} else {
							candidatesSameConflicts[i] = i;
						}
					} else if (conflicts < minConflicts) {
						minConflicts = conflicts;
						candidatesSameConflicts = new int[rows.length];
						if (i == 0) {
							candidatesSameConflicts[i] = -1;
						} else {
							candidatesSameConflicts[i] = i;
						}
					}
				}
				if (candidatesSameConflicts.length != 0) {
					int[] reArrMin = reArray(candidatesSameConflicts);
					int newBestPosition = reArrMin[random.nextInt(reArrMin.length)];
					rows[worstQueenPosition] = newBestPosition;
				}

				iterations++;
				if (iterations == maxIter) {
					// Trying too long... start over.
					iterations = 0;
					scramble();
				}
			}
		}

		// Print the solved board.
		public void print() {
			for (int i = 0; i < rows.length; i++) {
				for (int j = 0; j < rows.length; j++) {
					// Easier to check if we use "." instead of "_", but thats
					// in the task.
					System.out.print(rows[j] == i ? '*' + " " : '_' + " ");
				}
				System.out.println();
			}
		}
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter the size of the square board you want to be solved:");
		int size = sc.nextInt();
		while (size < 4) {
			System.out.println("Please enter correct size for the square board:");
			size = sc.nextInt();
		}
		sc.close();
		// maxIter can be entered in the console, but this makes the input
		// simpler.
		int maxIter = 2 * size;
		Board board = new Board(size);
		long start = System.currentTimeMillis();
		board.solve(maxIter);
		long stop = System.currentTimeMillis();
		// For very large boards print() also takes a lot of time to finish.
		board.print();
		System.out.println("Solution found in " + ((double) (stop - start)) / 1000 + "sec.");
	}
}