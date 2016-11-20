/*
 * Author: Chaitanya Malladi
 * Project: DV2542 Machine Learning Assginment 2 - Decision Tree
 * Based on: Algorithm 5.1 and 5.2 from "Machine Learning - The Art and Science of Algorithms that Make Sense of Data" by Peter Flach
 * Uses Weka 3.6 java library 
 */

package assignment2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.Random;

import weka.core.*;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;

public class DecisionTree extends Classifier {
	static Tree finalTree;

	public static void main(String[] args) throws Exception {
		Instances data = new Instances(new BufferedReader(new FileReader(
				"weather.nominal.arff")));// Input data file (nominal
											// attributes)
		data.setClassIndex(data.numAttributes() - 1);// Set class attribute

		for (int i = 0; i < data.numAttributes(); i++)// Delete instances with
														// missing attributes
		{
			data.deleteWithMissing(data.attribute(i));
		}

		DecisionTree tree = new DecisionTree();
		tree.buildClassifier(data);

		System.out.println("Decision Tree\n=============");
		printTree(finalTree, 0);
		System.out.println("\n\nEvaluation\n==========");

		Evaluation test = new Evaluation(data);
		test.crossValidateModel(tree, data, 10, new Random());
		System.out.println(test.toSummaryString());

	}

	private static Tree growTree(Instances data) {

		if (homo(data))
			return label(data);

		Attribute a = bestSplit(data);

		Enumeration attValues = a.enumerateValues();
		int count = countValues(attValues);
		attValues = a.enumerateValues();
		int index = 0;
		Instances splitData[] = new Instances[count];
		String attList[] = new String[count];

		// Splitting data based on best attribute
		while (attValues.hasMoreElements()) {
			String tempAtt = (String) attValues.nextElement();
			Instances tempInstance = new Instances(data, 0);
			for (int j = 0; j < data.numInstances(); j++) {
				if (data.instance(j).stringValue(a).equals(tempAtt)) {

					tempInstance.add(data.instance(j));
				}
			}
			int attToDel = 0;
			for (; attToDel < tempInstance.numAttributes(); attToDel++) {
				if (a.name().equals(tempInstance.attribute(attToDel).name()))
					break;
			}
			tempInstance.deleteAttributeAt(attToDel);
			splitData[index] = tempInstance;
			attList[index] = tempAtt;
			index++;
		}

		// Building tree
		Tree dtree = new Tree(a.name());
		for (int i = 0; i < splitData.length; i++) {
			if (splitData[i].numInstances() == 0) {
				Tree tempTree = new Tree(":" + attList[i]);
				tempTree.addChild(":" + attList[i], label(data));
				dtree.addChild(a.name(), tempTree);
			} else {
				Tree tempTree = new Tree(":" + attList[i]);
				tempTree.addChild(":" + attList[i], growTree(splitData[i]));
				dtree.addChild(a.name(), tempTree);

			}
		}
		return dtree;
	}

	// Function to identify is data is homogeneous
	private static boolean homo(Instances data) {
		double first = data.instance(0).classValue();
		for (int i = 1; i < data.numInstances(); i++) {
			if (data.instance(i).classValue() != first) {
				return false;
			}
		}
		return true;
	}

	// Function to identify the attribute best to split on
	private static Attribute bestSplit(Instances data) {
		double iMin = 1.0;
		Attribute bestAtt = data.attribute(0);
		for (int i = 0; i < data.numAttributes() - 1; i++) {
			Attribute att = data.attribute(i);
			Enumeration attValues = att.enumerateValues();
			int count = countValues(attValues);
			attValues = att.enumerateValues();

			int index = 0;
			Instances splitData[] = new Instances[count];
			while (attValues.hasMoreElements()) {
				String tempAtt = (String) attValues.nextElement();
				Instances tempInstance = new Instances(data, 0);
				for (int j = 0; j < data.numInstances(); j++) {
					if (data.instance(j).stringValue(i).equals(tempAtt)) {
						tempInstance.add(data.instance(j));
					}
				}
				splitData[index] = tempInstance;
				index++;
			}

			double impurity = gini(splitData, data);
			if (impurity < iMin) {
				iMin = impurity;
				bestAtt = att;
			}
		}
		return bestAtt;

	}

	// functions entropy(), imp() are used to find impurity based on Gini Index
	// for bestsplit
	private static double gini(Instances[] splitData, Instances data) {
		double entropy = 0.0;
		for (int i = 0; i < splitData.length; i++) {
			Enumeration<Instance> instances = splitData[i].enumerateInstances();
			entropy += splitData[i].numInstances() * imp(splitData[i]);
		}
		entropy /= data.numInstances();
		return entropy;
	}

	private static double imp(Instances splitData) {
		AttributeStats stats = splitData.attributeStats(splitData.classIndex());
		int numPos = stats.nominalCounts[0];
		int numInst = splitData.numInstances();
		Double p = (double) numPos / (double) numInst;

		Double result = 2 * p * (1 - p); // Gini Index

		return result;
	}

	// Function to count number of values in an enumeration
	private static int countValues(Enumeration attValues) {
		int count = 0;
		while (attValues.hasMoreElements()) {
			attValues.nextElement();
			count++;
		}
		return count;
	}

	// Function to return tree with class label for building tree
	private static Tree label(Instances data) {
		return new Tree(Double.toString(data.lastInstance().classValue()));
	}

	// Function that traverses and prints tree
	public static void printTree(Tree t, int level) {
		for (int i = 0; i < level; i++)
			System.out.print("\t");
		System.out.println(t.node);
		if (!t.isLeaf()) {
			for (int i = 0; i < t.children.size(); i++)
				printTree(t.children.get(i), level + 1);
		}
	}

	// Extended from Classifier
	// Builds this decision tree as weka classifier
	@Override
	public void buildClassifier(Instances arg0) throws Exception {

		finalTree = this.growTree(arg0);

	}

	// Extended from Classifier
	// Classifies given Instance arg0 to a class based on built decision tree
	@Override
	public double classifyInstance(Instance arg0) {

		double classvalue = traverse(arg0, finalTree);
		return classvalue;
	}

	// Function to traverse tree for classification of instance
	private double traverse(Instance arg0, Tree tree) {
		String att = tree.node;

		if (tree.isLeaf())
			return Double.parseDouble(att);

		for (int i = 0; i < arg0.numAttributes(); i++) {
			if (arg0.attribute(i).name().equals(att)) {
				String val = arg0.stringValue(i);

				for (int j = 0; j < tree.children.size(); j++) {
					if (tree.children.get(j).node.equals(":" + val)) {

						return traverse(arg0,
								tree.children.get(j).children.get(0));
					}
				}
			}
		}
		return -1;
	}
}
