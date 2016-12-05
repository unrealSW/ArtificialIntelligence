/**
 * AI - Homework 04
 * Solution to the Max-cost-Backpack problem with Genetic algorithm.
 * This task was very difficult for me. I tried to implement the pseudo code given
 * on the exercise as close as possible. A population is represented with ArrayList
 * of individuals, which are stored as arrays of integers. This is not optimal but
 * is easy to work with. Because there are so many random driven changes, the solution
 * is not always the best, but in more iterations the program should find the best
 * solution a couple of times.
 * 
 * @author Dimitar Cenkov, FN: 81000
 * @version 1.0
 */
package solution;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

public class BackpackGASolver {

	public static final int ONE = 1;
	public static final int TWO = 2;

	public static class BackPack {
		private int maxWeight;
		private int itemsCount;
		private Item[] items;
		private int individualsCount;
		private int individualsForChange;
		private int individualsForMutation;

		// Take an instance of Random, to work with.
		private Random random = new Random();

		public BackPack(int maxWeight, int itemsCount, Item[] items, int individualsCount, int individualsForChange,
				int individualsForMutation) {
			this.maxWeight = maxWeight;
			this.itemsCount = itemsCount;
			this.items = items;
			this.individualsCount = individualsCount;
			this.individualsForChange = individualsForChange;
			this.individualsForMutation = individualsForMutation;
		}

		// Generate individual with random values.
		public int[] generateRandomInd(int n) {
			int[] individual = new int[n];
			for (int i = 0; i < individual.length; i++) {
				individual[i] = random.nextInt(TWO);
			}
			return individual;
		}

		// Fill an array with all fitness values for every individual.
		public int[] allFitness(ArrayList<int[]> population) {
			int[] fitnessArr = new int[population.size()];
			int[] weightArr = new int[population.size()];
			int index = 0;
			while (index < population.size()) {
				int[] currentIndividual = new int[population.size()];
				currentIndividual = population.get(index).clone();
				for (int i = 0; i < currentIndividual.length; i++) {
					if (currentIndividual[i] == ONE) {
						fitnessArr[index] += items[i].getCost();
						weightArr[index] += items[i].getWeight();
					}
				}
				index++;
			}
			for (int i = 0; i < population.size(); i++) {
				if (weightArr[i] > maxWeight) {
					fitnessArr[i] = 0;
				}
			}
			return fitnessArr;
		}

		// Find maximum fitness value.
		public int maxFitness(int[] fitness) {
			int max = 0;
			for (int i = 0; i < fitness.length; i++) {
				if (max < fitness[i]) {
					max = fitness[i];
				}
			}
			return max;
		}

		// Find the index of the individual with the worst fitness value.
		public int worstFitnessIndex(int[] fitness) {
			int indexOfWorst = -1;
			int minFitness = Integer.MAX_VALUE;
			for (int i = 0; i < fitness.length; i++) {
				if (minFitness > fitness[i] && fitness[i] >= 0) {
					minFitness = fitness[i];
					indexOfWorst = i;
				}
			}
			return indexOfWorst;
		}

		// Execute single-point crossover for the individuals of the population.
		public void crossover(ArrayList<int[]> population) {
			TreeSet<Integer> isParent = new TreeSet<Integer>();
			// p - r best individuals.
			int change = individualsCount - individualsForChange;
			if (change > ONE) {
				int randomIndex = random.nextInt(itemsCount);
				int parent1 = random.nextInt(change);
				int parent2 = random.nextInt(change);
				isParent.add(parent1);
				isParent.add(parent2);
				int endCond = 0;
				if (individualsForChange % TWO == 0) {
					endCond = individualsForChange / TWO;
				} else {
					endCond = individualsForChange / TWO + ONE;
				}
				for (int i = 0; i < endCond; i++) {
					while (!isParent.contains(parent1)) {
						parent1 = random.nextInt(change);
						isParent.add(parent1);
					}
					while (!isParent.contains(parent2)) {
						parent2 = random.nextInt(change);
						isParent.add(parent2);
					}
					int[] successor1 = population.get(parent1).clone();
					int[] successor2 = population.get(parent2).clone();
					for (int j = itemsCount - ONE; j > randomIndex; j--) {
						int temp = successor1[j];
						successor1[j] = successor2[j];
						successor2[j] = temp;
					}
					population.add(successor1);
					population.add(successor2);
				}
			}
		}

		// Mutate given individual.
		public int[] mutate(int[] individual) {
			int indexOfMutation = random.nextInt(itemsCount);
			int[] newIndividual = new int[itemsCount];
			for (int i = 0; i < newIndividual.length; i++) {
				if (i == indexOfMutation) {
					newIndividual[i] = newIndividual[i] == ONE ? 0 : ONE;
				} else {
					newIndividual[i] = individual[i];
				}
			}
			return newIndividual;
		}

		// Execute the genetic algorithm.
		public int solve() {

			// Generate population.
			ArrayList<int[]> population = new ArrayList<int[]>();
			for (int i = 0; i < individualsCount; i++) {
				population.add(generateRandomInd(itemsCount));
			}

			int maxCost = 0;

			// Store the best max cost in a Map (maxCost, occurrenceOfMaxCost).
			TreeMap<Integer, Integer> bestResult = new TreeMap<Integer, Integer>();
			Entry<Integer, Integer> lastEntry = null;

			// Terminate when maxCost is found at least $individualsCount times.
			while (lastEntry == null || lastEntry.getValue() < individualsCount) {

				// Fitness calculations.
				int[] fitnessOfAll = allFitness(population);
				int currentMaxCost = maxFitness(fitnessOfAll);
				if (maxCost < currentMaxCost) {
					maxCost = currentMaxCost;
				}

				// Prepare worst individuals for removal.
				int index[] = new int[individualsForChange];
				for (int i = 0; i < index.length; i++) {
					index[i] = worstFitnessIndex(fitnessOfAll);
					if (index[i] == -1) {
						break;
					}
					fitnessOfAll[index[i]] = -1;
				}

				// Elitism.
				ArrayList<int[]> newPopulation = new ArrayList<int[]>();
				for (int i = 0; i < fitnessOfAll.length; i++) {
					if (fitnessOfAll[i] >= 0) {
						newPopulation.add(population.get(i));
					}
				}

				// Crossover.
				crossover(newPopulation);
				
				// Mutations.
				for (int i = 0; i < individualsForMutation; i++) {
					if (newPopulation.size() <= 0) {
						break;
					}
					int mutateInd = random.nextInt(newPopulation.size());
					int[] mutated = mutate(newPopulation.get(mutateInd));
					newPopulation.set(mutateInd, mutated);
				}

				// Update population.
				population.clear();
				population = newPopulation;

				// Add potential result in the Map of results.
				int countMaxValue = bestResult.get(maxCost) != null ? bestResult.get(maxCost) : 0;
				countMaxValue++;
				bestResult.put(maxCost, countMaxValue);
				lastEntry = bestResult.lastEntry();
			}
			return maxCost;
		}
	};

	public static void main(String[] args) {
		/**
		 * Note: It is very... slow to enter large amount of data in the
		 * suggested way with the validation, so there is a commented lines than
		 * can be used for static testing. Just uncomment them, and comment
		 * everything in the main function before them.
		 */
		Scanner sc = new Scanner(System.in);
		int cost;
		int weight;
		System.out.println("Please enter backpack's maximum weight capacity: ");
		int maxWeight = sc.nextInt();
		while (maxWeight <= 0) {
			System.out.println("Please enter correct maximum wight: ");
			maxWeight = sc.nextInt();
		}
		System.out.println("Please enter backpack's items count: ");
		int itemsCount = sc.nextInt();
		while (itemsCount <= 0) {
			System.out.println("Please enter correct items count: ");
			itemsCount = sc.nextInt();
		}
		Item[] items = new Item[itemsCount];
		for (int i = 0; i < items.length; i++) {
			System.out.println("Item " + (i + ONE) + " cost: ");
			cost = sc.nextInt();
			System.out.println("Item " + (i + ONE) + " weight: ");
			weight = sc.nextInt();
			items[i] = new Item(cost, weight);
		}
		System.out.println("Enter how many individuals you want to begin with(the bigger, the better): ");
		int individualsCount = sc.nextInt();
		while (individualsCount <= 0) {
			System.out.println("Please enter correct individuals count: ");
			individualsCount = sc.nextInt();
		}
		System.out.println("Enter how many individuals you want to be changed each iteration: ");
		int individualsForChange = sc.nextInt();
		while (individualsForChange <= 0 || individualsForChange >= individualsCount) {
			System.out.println("Please enter correct individuals count to be changed: ");
			individualsForChange = sc.nextInt();
		}
		System.out.println("Enter how many individuals you want to be mutated each iteration: ");
		int individualsForMutation = sc.nextInt();
		while (individualsForMutation <= 0 || individualsForMutation > individualsCount) {
			System.out.println("Please enter correct individuals count to be mutated: ");
			individualsForMutation = sc.nextInt();
		}
		sc.close();

		BackPack backPack = new BackPack(maxWeight, itemsCount, items, individualsCount, individualsForChange,
				individualsForMutation);

		// Static testing:
//		 Item[] items = { new Item(7, 8), new Item(8, 5), new Item(6, 4), new
//		 Item(3, 1), new Item(5, 6) };
//		 BackPack backPack = new BackPack(15, 5, items, 100, 4, 5);

		int result = backPack.solve();
		System.out.println("Result: " + result);
	}
}
