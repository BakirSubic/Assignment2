package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Time;
import java.util.*;

public class Main {
    private static Map<String, Map<String, Map<String, Double>>> graphs = new HashMap<>();

    public static void main(String[] args) {
        String[] task1 = {
                "src/main/resources/all_places_a.txt",
                "src/main/resources/all_places_b.txt",
                "src/main/resources/complex.txt",
                "src/main/resources/simple.txt",
                "src/main/resources/ten_places.txt",
                "src/main/resources/five_places.txt"
        };

        long seed = System.currentTimeMillis();
        Random random = new Random(seed);

        for (int i = 0; i < task1.length; i++) {
            Map<String, Map<String, Double>> graph = loadGraphFromFile(task1[i]);

            graphs.clear();
            task3Solution(graph, task1[i]);
            task4Solution(graph, task1[i], random);
        }
    }

    private static Map<String, Map<String, Double>> loadGraphFromFile(String inputFile) {
        Map<String, Map<String, Double>> graph = new HashMap<>();

        try {
            Scanner s = new Scanner(new File(inputFile));
            int lineNumber = 0;

            while (s.hasNextLine()) {
                String line = s.nextLine();
                try {
                    lineNumber++;
                    String[] lineParts = line.split("[,\\s]+");
                    String starting = lineParts[0];
                    String destination = lineParts[1];
                    double time = Double.parseDouble(lineParts[2]);

                    graph.computeIfAbsent(starting, k -> new HashMap<>()).put(destination, time);

                } catch (Exception e) {
                    System.out.println("Bad line: " + lineNumber);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return graph;
    }

    private static void task3Solution(Map<String, Map<String, Double>> graph, String inputFile) {
        System.out.println("Optimal Travel Times for " + inputFile + ":");

        String outputFileName = "task3_" + new File(inputFile).getName();

        try (FileWriter writer = new FileWriter("src/main/output/" + outputFileName)) {
            for (String source : graph.keySet()) {
                for (String destination : graph.keySet()) {
                    if (!source.equals(destination)) {
                        Map<String, Double> optimalRoute = discoverOptimalRoute(graph, source, destination);
                        writeOptimalRoute(writer, source, destination, optimalRoute);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeOptimalRoute(FileWriter writer, String startNode, String endNode, Map<String, Double> optimalRoute) throws IOException {
        if (optimalRoute.containsKey(endNode)) {
            Double distance = optimalRoute.get(endNode);
            writer.write(startNode + " to " + endNode + " = " + (distance != null ? distance : 0) + "\n");
        } else {
            writer.write("No path from " + startNode + " to " + endNode + "\n");
        }
    }

    private static Map<String, Double> discoverOptimalRoute(Map<String, Map<String, Double>> graph, String startNode, String endNode) {
        DijkstraShortestPath pathSolver = new DijkstraShortestPath();
        return pathSolver.discoverOptimalRoute(graph, startNode, endNode);
    }

    private static void task4Solution(Map<String, Map<String, Double>> graph, String inputFile, Random random) {
        System.out.println("Task 4 Solution for " + inputFile + ":");

        String constraintsFile = "src/main/resources/constraints.txt";
        Map<String, Map<String, Double>> constraints = loadConstraintsFromFile(constraintsFile);

        String outputFileName = "task4_" + new File(inputFile).getName();

        try (FileWriter writer = new FileWriter("src/main/output/" + outputFileName);
             FileWriter graphWriter = new FileWriter("src/main/output/" + outputFileName + "_graph.txt")) {

            for (Map.Entry<String, Map<String, Double>> entry : constraints.entrySet()) {
                String source = entry.getKey();
                for (Map.Entry<String, Double> constraint : entry.getValue().entrySet()) {
                    String target = constraint.getKey();
                    double chanceOfClosure = constraint.getValue();
                    String constraintType = loadConstraintType(constraintsFile, source, target); // Get constraint type from CSV file
                    imposeRandomProbabilityConstraint(graph, source, target, chanceOfClosure, random, writer, constraintType);
                }
            }

            printGraphToFile(graph, graphWriter);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String loadConstraintType(String constraintsFile, String source, String target) {
        try (Scanner s = new Scanner(new File(constraintsFile))) {
            while (s.hasNextLine()) {
                String line = s.nextLine();
                String[] lineParts = line.split(",");
                String fileSource = lineParts[0].trim();
                String fileTarget = lineParts[1].trim();
                if (source.equals(fileSource) && target.equals(fileTarget)) {
                    return lineParts[2].trim();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unknown Constraint";
    }


    private static void imposeRandomProbabilityConstraint(Map<String, Map<String, Double>> graph, String source, String target, double chanceOfClosure, Random random, FileWriter writer, String constraint) throws IOException {
        double randomProbability = random.nextDouble();

        if (randomProbability <= chanceOfClosure) {
            graph.computeIfAbsent(source, k -> new HashMap<>()).put(target, Double.POSITIVE_INFINITY);
            writer.write("Path " + source + " to " + target + " is closed due to " + constraint + ".\n");
        } else {
            double weight = random.nextDouble();
            graph.computeIfAbsent(source, k -> new HashMap<>()).put(target, weight);
            writer.write("Path " + source + " to " + target + " is open.\n");
        }
    }


    private static void printGraphToFile(Map<String, Map<String, Double>> graph, FileWriter writer) throws IOException {
        for (String source : graph.keySet()) {
            for (Map.Entry<String, Double> entry : graph.get(source).entrySet()) {
                String target = entry.getKey();
                Double weight = entry.getValue();
                writer.write(source + " to " + target + " = " + (weight != null ? weight : 0) + "\n");
            }
        }
    }

    private static Map<String, Map<String, Double>> loadConstraintsFromFile(String constraintsFile) {
        Map<String, Map<String, Double>> constraints = new HashMap<>();

        try {
            Scanner s = new Scanner(new File(constraintsFile));

            while (s.hasNextLine()) {
                String line = s.nextLine();
                try {
                    String[] lineParts = line.split(",");
                    String source = lineParts[0].trim();
                    String target = lineParts[1].trim();
                    double probability = Double.parseDouble(lineParts[3].trim());

                    constraints.computeIfAbsent(source, k -> new HashMap<>()).put(target, probability);
                } catch (Exception e) {
                    System.out.println("Bad line in constraints file");
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return constraints;
    }
}

