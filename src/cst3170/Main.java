package cst3170;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
	public static int[][] INPUT_ARRAY = readFile("som1.txt"); // AKA input set
	public static int[][] NODES_ARRAY = readFile("cw2DataSet1.csv"); // AKA test set
	public static double NEIGHBOR_DISTANCE = 25.0; // Set to 0 to use euclidean distance only
	public static int[][] readFile(String fileName) { // Read the file and create an array of input nodes
		int[][] nodeArray = null;
		try {
			System.out.println("Reading file " + fileName + "...");
			String dataFile = System.getProperty("user.dir") + File.separator + fileName;
			String text = "";
	        File myObj = new File(dataFile);
	        Scanner myReader = new Scanner(myObj);
	        while (myReader.hasNextLine()) { // Read all lines and add them to the string
	            String data = myReader.nextLine();
	            text += data.trim() + "\n";
	        }
	        String[] textArray = text.split("\n"); // Split the string by lines
	        nodeArray = new int[textArray.length][65];;
	        for (int lineIndex = 0; lineIndex < textArray.length; lineIndex++) { 
	        	String[] digitsArray = textArray[lineIndex].split(",");
	        	for (int coordIndex = 0; coordIndex < 65; coordIndex++) {
	        		nodeArray[lineIndex][coordIndex] = Integer.parseInt(digitsArray[coordIndex]);
	        	}
	        }
	        myReader.close();
	        System.out.println("File read succesfully!");
		}
		catch (FileNotFoundException e) {
            System.out.println("File " + fileName + " not found.");
            e.printStackTrace();
        }
        return nodeArray;
	}
	
	private static double euclideanDistance(int[] a, int[] b) { // Return the euclidean distance between two n-dimensional points
        double distance = 0;
        for (int coordIndex = 0; coordIndex < a.length; coordIndex++) {
            distance += (a[coordIndex] - b[coordIndex]) * (a[coordIndex] - b[coordIndex]);
        }
        return Math.sqrt(distance);
    }
	
	private static int getNearestInput(int[] node) { // Find the nearest input to a given node and return its index
        int nearestNode = 0;
        double minDistance = euclideanDistance(node, INPUT_ARRAY[0]);
        for (int nodeIndex = 0; nodeIndex < INPUT_ARRAY.length; nodeIndex++) {
            double distance = euclideanDistance(node, INPUT_ARRAY[nodeIndex]);
            if (distance < minDistance) {
                nearestNode = nodeIndex;
                minDistance = distance;
            }
        }
        return nearestNode;
    }
	
	public static int categorise(int[] node) { // Return the expected category of the node
		int counter = 0;
		for (int nodeIndex = 0; nodeIndex < INPUT_ARRAY.length; nodeIndex++) { // Count how many inputs around the node there are
			if (euclideanDistance(node, INPUT_ARRAY[nodeIndex]) <= NEIGHBOR_DISTANCE) {
				counter++;
			}
		}
		if (counter > 0) { // If there are inputs around the node, return the most common category among them
			if (counter % 2 == 0) {
				counter--;
			}
			int[][] neighborArray = new int[counter][65];
			for (int nodeIndex = 0; nodeIndex < INPUT_ARRAY.length; nodeIndex++) { // Write the closest inputs into neighborArray
				if (euclideanDistance(node, INPUT_ARRAY[nodeIndex]) <= NEIGHBOR_DISTANCE && counter > 0) {
					for (int coordIndex = 0; coordIndex < 65; coordIndex++) {
						neighborArray[counter - 1][coordIndex] = INPUT_ARRAY[nodeIndex][coordIndex];
					}
					counter--;
				}
			}
			int[] categoryArray = new int[10];
			for (int nodeIndex = 0; nodeIndex < neighborArray.length; nodeIndex++) { // Count all categories found in neighborArray
				int nodeCategory = neighborArray[nodeIndex][64];
				categoryArray[nodeCategory]++;
			}
			int max = categoryArray[0];
			int category = 0;
			for (int categoryIndex = 0; categoryIndex < categoryArray.length; categoryIndex++) { // Find the most commonly found category
				if (categoryArray[categoryIndex] > max) {
					max = categoryArray[categoryIndex];
					category = categoryIndex;
				}
			}
			return category;
		}
		else { // If there are no inputs around the node, return the category of the nearest input
			int category = INPUT_ARRAY[getNearestInput(node)][64];
			return category;
		}
	}

	public static void main(String[] args) { 
		System.out.println("Starting categorisation...");
		int correctCounter = 0;
		for (int nodeIndex = 0; nodeIndex < NODES_ARRAY.length; nodeIndex++) { // Categorise all digits in the test set, and check if the category assigned is correct
			if (categorise(NODES_ARRAY[nodeIndex]) == NODES_ARRAY[nodeIndex][64]) {
				correctCounter++;
			}
		}
		System.out.println("Categorised " + correctCounter + " out of " + NODES_ARRAY.length + " correctly.");
	}
}