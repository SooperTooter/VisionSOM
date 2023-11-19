package cst3170;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateSOM { // This class uses the SOM class to generate 10 SOMs, combine them into one map, and save it to a file
	public static double[][] readFile(String fileName) { // Read the file and create an array of input nodes
		double[][] nodeArray = null;
		try {
			String dataFile = System.getProperty("user.dir") + File.separator + fileName;
			String text = "";
	        File myObj = new File(dataFile);
	        Scanner myReader = new Scanner(myObj);
	        while (myReader.hasNextLine()) { // Read all lines and add them to the string
	            String data = myReader.nextLine();
	            text += data.trim() + "\n";
	        }
	        String[] textArray = text.split("\n"); // Split the string by lines
	        nodeArray = new double[textArray.length][65];;
	        for (int lineIndex = 0; lineIndex < textArray.length; lineIndex++) { 
	        	String[] digitsArray = textArray[lineIndex].split(",");
	        	for (int coordIndex = 0; coordIndex < 65; coordIndex++) {
	        		nodeArray[lineIndex][coordIndex] = Integer.parseInt(digitsArray[coordIndex]);
	        	}
	        }
	        myReader.close();
		}
		catch (FileNotFoundException e) {
            System.out.println("File " + fileName + " not found.");
            e.printStackTrace();
        }
        return nodeArray;
	}
	
	public static double[][] generateSom(int category, double[][] inputArray) { // Create a SOM for a given category
		int counter = 0;
		for (int nodeIndex = 0; nodeIndex < inputArray.length; nodeIndex++) { // Count how many inputs with given category there are
			if (inputArray[nodeIndex][64] == category) {
				counter++;
			}
		}
		double[][] inputArrayCategorised = new double[counter][65];
		for (int nodeIndex = 0; nodeIndex < inputArray.length; nodeIndex++) { // Add all inputs with the given category to an array
			if (inputArray[nodeIndex][64] == category) {
				for (int coordIndex = 0; coordIndex < inputArray[nodeIndex].length; coordIndex++) {
					inputArrayCategorised[counter - 1][coordIndex] = inputArray[nodeIndex][coordIndex];
				}
				counter--;
			}
		}
		SOM som = new SOM(inputArrayCategorised, inputArrayCategorised.length*10, 0.1);
		System.out.println("Training SOM for category " + category + "...");
		som.train(2);
		System.out.println("Training complete!");
		return som.getNodes();
	}
	
	public static void writeFile(double[][] somArray, String filename) { // Write the SOM to a file 
		System.out.println("Writing to file " + filename + "...");
		try {
			FileWriter arrayWriter = new FileWriter(filename);
		    String arrayString = "";
		    for (int nodeIndex = 0; nodeIndex < somArray.length; nodeIndex++) {
		    	for (int coordIndex = 0; coordIndex < somArray[nodeIndex].length; coordIndex++) {
		    		int somCoord = (int) Math.round(somArray[nodeIndex][coordIndex]);
		    		arrayString += somCoord;
		    		if (coordIndex < 64) {
		    			arrayString += ",";
		    		}
		    		else {
		    			arrayString += "\n";
		    		}
		    	}
		    }
		    arrayWriter.write(arrayString);
		    arrayWriter.close();
		    System.out.println("Successfully wrote to file " + filename);
		} 
		catch (IOException e) {
		    System.out.println("An error occurred.");
		    e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		double[][] trainingArray = readFile("cw2DataSet2.csv");
		double[][] somArray = generateSom(0, trainingArray); // Create SOM for category 0
		for (int category = 1; category < 10; category++) { // Create SOMs for other categories and add them to somArray
			double[][] catArray = generateSom(category, trainingArray);
			double[][] newSomArray = new double[somArray.length + catArray.length][65];
			System.arraycopy(somArray, 0, newSomArray, 0, somArray.length);
			System.arraycopy(catArray, 0, newSomArray, somArray.length, catArray.length);
			somArray = newSomArray;
		}
		writeFile(somArray, "som2.txt");
	}
}