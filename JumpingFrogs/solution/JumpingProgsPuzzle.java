/*
 * Author: 	Dimitar Cenkov
 * FN:		81000
 * Task:	AI-Homework01
 * Subject:	Solution to the jumping frog puzzle with DFS
 * Other:	This is the variant which works for significant 
 * 			large n, but at the cost of a lot of memory
 */
package solution;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Stack;

public class JumpingProgsPuzzle {
	public static final int ONE = 1;
	public static final int TWO = 2;

	public static final char gap = '_';
	public static final char left = '<';
	public static final char right = '>';

	public static final String GAP = "_";
	public static final String LEFT = "<";
	public static final String RIGHT = ">";

	public static String swapDynamic(StringBuilder sb, int index) {
		int indexOfGap = sb.indexOf(GAP);
		char charToSwap = sb.charAt(index);
		StringBuilder newSB = new StringBuilder(sb);
		newSB.setCharAt(indexOfGap, charToSwap);
		newSB.setCharAt(index, sb.charAt(indexOfGap));
		return newSB.toString();
	}

	public static StringBuilder swapStatic(StringBuilder sb, int index) {
		int indexOfGap = sb.indexOf(GAP);
		char charToSwap = sb.charAt(index);
		sb.setCharAt(indexOfGap, charToSwap);
		sb.setCharAt(index, gap);
		return sb;
	}

	public static void main(String[] args) {
		System.out.println("Please enter how many frogs you want on each side:");
		Scanner sc = new Scanner(System.in);
		int frogOnSide = sc.nextInt();
		long timeOfStart = Calendar.getInstance().getTimeInMillis();
		StringBuilder initialState = new StringBuilder();
		StringBuilder solution = new StringBuilder();
		for (int i = 0; i < frogOnSide; i++) {
			initialState.append(RIGHT);
			solution.append(LEFT);
		}
		initialState.append(GAP);
		solution.append(GAP);
		for (int i = frogOnSide + 1; i <= 2 * frogOnSide; i++) {
			initialState.append(LEFT);
			solution.append(RIGHT);
		}

		Stack<StringBuilder> stack = new Stack<StringBuilder>();
		HashSet<String> visited = new HashSet<String>();
		stack.push(new StringBuilder(initialState));

		do {
			int indexOfGap = initialState.indexOf(GAP);
			if ((indexOfGap >= TWO) && (initialState.charAt(indexOfGap - TWO) == right)
					&& (!visited.contains(swapDynamic(initialState, indexOfGap - TWO)))) {
				initialState = swapStatic(initialState, indexOfGap - TWO);
				stack.push(new StringBuilder(initialState));
				visited.add(new StringBuilder(initialState).toString());
				continue;
			}
			if ((indexOfGap >= ONE) && (initialState.charAt(indexOfGap - ONE) == right)
					&& (!visited.contains(swapDynamic(initialState, indexOfGap - ONE)))) {
				initialState = swapStatic(initialState, indexOfGap - ONE);
				stack.push(new StringBuilder(initialState));
				visited.add(new StringBuilder(initialState).toString());
				continue;
			}
			if ((indexOfGap < initialState.length() - ONE) && (initialState.charAt(indexOfGap + ONE) == left)
					&& (!visited.contains(swapDynamic(initialState, indexOfGap + ONE)))) {
				initialState = swapStatic(initialState, indexOfGap + ONE);
				stack.push(new StringBuilder(initialState));
				visited.add(new StringBuilder(initialState).toString());
				continue;
			}
			if ((indexOfGap < initialState.length() - TWO) && (initialState.charAt(indexOfGap + TWO) == left)
					&& (!visited.contains(swapDynamic(initialState, indexOfGap + TWO)))) {
				initialState = swapStatic(initialState, indexOfGap + TWO);
				stack.push(new StringBuilder(initialState));
				visited.add(new StringBuilder(initialState).toString());
				continue;
			}

			stack.pop();
			initialState = new StringBuilder(stack.peek());
			
		} while (!initialState.toString().equals(solution.toString()));

		Stack<StringBuilder> solutionSteps = new Stack<StringBuilder>();
		while (!stack.empty()) {
			solutionSteps.push(stack.pop());
		}
		while (!solutionSteps.empty()) {
			System.out.println(solutionSteps.pop());
		}
		long timeOfEnd = Calendar.getInstance().getTimeInMillis();
		System.out.println("Execution time: " + (timeOfEnd - timeOfStart) + " millis");
		sc.close();
	}
}