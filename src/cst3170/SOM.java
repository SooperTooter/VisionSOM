package cst3170;
import java.util.Random;

public class SOM { // This class describes and creates a self-organising map
    private double[][] inputs;
    private double[][] nodes;
    private double learningRate;
    private static double MEX_HAT_DISTANCE = 10.0;
    
    public SOM(double[][] inputs, int numNodes, double learningRate) { // Constructor
        this.inputs = inputs;
        this.nodes = new double[numNodes][inputs[0].length];
        this.learningRate = learningRate;
        Random rand = new Random();
        for (int nodeIndex = 0; nodeIndex < nodes.length; nodeIndex++) {
            for (int coordIndex = 0; coordIndex < inputs[0].length - 1; coordIndex++) {
                nodes[nodeIndex][coordIndex] = Math.round(rand.nextDouble() * 16);
            }
            nodes[nodeIndex][inputs[0].length -1] = inputs[0][64];
        }
    }
    
    private int getNearestNode(double[] input) { // Find the nearest node to a given input
        int nearestNode = 0;
        double minDistance = euclideanDistance(input, nodes[0]);;
        for (int nodeIndex = 0; nodeIndex < nodes.length; nodeIndex++) {
            double distance = euclideanDistance(input, nodes[nodeIndex]);
            if (distance < minDistance) {
                nearestNode = nodeIndex;
                minDistance = distance;
            }
        }
        return nearestNode;
    }
    
    private double euclideanDistance(double[] a, double[] b) { // Return the euclidean distance between two n-dimensional points
        double distance = 0;
        for (int coordIndex = 0; coordIndex < a.length; coordIndex++) {
            distance += (a[coordIndex] - b[coordIndex]) * (a[coordIndex] - b[coordIndex]);
        }
        return Math.sqrt(distance);
    }
    
    public void train(int numIterations) { // Iterate over all inputs and move nodes, then repeat a set amount of times 
        for (int i = 0; i < numIterations; i++) {
            for (int inputIndex = 0; inputIndex < inputs.length; inputIndex++) {
                int nearestNodeIndex = getNearestNode(inputs[inputIndex]);
                for (int coordIndex = 0; coordIndex < nodes[nearestNodeIndex].length; coordIndex++) {
                    nodes[nearestNodeIndex][coordIndex] += learningRate * (inputs[inputIndex][coordIndex] - nodes[nearestNodeIndex][coordIndex]);
                }
                mexicanHat(nodes[nearestNodeIndex], inputs[inputIndex]);
            }
            learningRate *= 0.999; // Decrease the learning rate
        }
    }
    
    private void mexicanHat(double[] node, double[] input) { // Move all points within a certain distance of a node towards or away from the input point
    	for (int nodeIndex = 0; nodeIndex < nodes.length; nodeIndex++) {
    		if(euclideanDistance(node, nodes[nodeIndex]) * learningRate <= MEX_HAT_DISTANCE && euclideanDistance(node, nodes[nodeIndex]) != 0) {
    			for (int coordIndex = 0; coordIndex < nodes[nodeIndex].length; coordIndex++) {
    				nodes[nodeIndex][coordIndex] += learningRate * (input[coordIndex] - node[coordIndex]);
    				if (nodes[nodeIndex][coordIndex] < 0) { // Prevent nodes from having too large or too small values
    					nodes[nodeIndex][coordIndex] = 0;
    				}
    				else if (nodes[nodeIndex][coordIndex] > 16.0) {
    					nodes[nodeIndex][coordIndex] = 16.0;
    				}
    			}
    		}
    		else if(euclideanDistance(node, nodes[nodeIndex]) * learningRate <= (MEX_HAT_DISTANCE + 2.0) && euclideanDistance(node, nodes[nodeIndex]) != 0) {
    			for (int coordIndex = 0; coordIndex < nodes[nodeIndex].length; coordIndex++) {
    				nodes[nodeIndex][coordIndex] -= learningRate * (input[coordIndex] - node[coordIndex]);
    				if (nodes[nodeIndex][coordIndex] < 0) {
    					nodes[nodeIndex][coordIndex] = 0;
    				}
    				else if (nodes[nodeIndex][coordIndex] > 16.0) {
    					nodes[nodeIndex][coordIndex] = 16.0;
    				}
    			}
    		}
    	}
    }
    
    public double[][] getNodes() {
    	return nodes;
    }
}