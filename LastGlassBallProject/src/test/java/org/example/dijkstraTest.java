package org.example;

import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class dijkstraTest {

    @Test
    void discoverOptimalRoute() {
        DijkstraShortestPath dijkstra = new DijkstraShortestPath();

        // Example graph
        Map<String, Map<String, Double>> graph = new java.util.HashMap<>();
        graph.put("A", Map.of("C", 10.0, "B", 60.0));
        graph.put("B", Map.of("D", 20.0));
        graph.put("D", Map.of("C", 15.0));
        graph.put("C", Map.of("B", 30.0));

        // Output file path
        String filePath = "src/main/output/dijkstrasTestReport.txt";

        // Create a FileWriter for output
        try (FileWriter writer = new FileWriter(filePath)) {

            // Test case 1: Optimal route from A to C
            Map<String, Double> result1 = dijkstra.discoverOptimalRoute(graph, "A", "C");
            assertEquals(10.0, result1.get("C"));
            writer.write("Test case 1: " + result1 + "\n");

            // Test case 2: Optimal route from A to B
            Map<String, Double> result2 = dijkstra.discoverOptimalRoute(graph, "A", "B");
            assertEquals(40.0, result2.get("B"));
            writer.write("Test case 2: " + result2 + "\n");

            // Test case 3: Optimal route from A to D
            Map<String, Double> result3 = dijkstra.discoverOptimalRoute(graph, "A", "D");
            assertEquals(60.0, result3.get("D"));
            writer.write("Test case 3: " + result3 + "\n");

            // Test case 4: Optimal route from B to D
            Map<String, Double> result4 = dijkstra.discoverOptimalRoute(graph, "B", "D");
            assertEquals(20.0, result4.get("D"));
            writer.write("Test case 4: " + result4 + "\n");

            // Test case 5: Optimal route from B to C
            Map<String, Double> result5 = dijkstra.discoverOptimalRoute(graph, "B", "C");
            assertEquals(35.0, result5.get("C"));
            writer.write("Test case 5: " + result5 + "\n");

            // Test case 6: Optimal route from C to B
            Map<String, Double> result6 = dijkstra.discoverOptimalRoute(graph, "C", "B");
            assertEquals(30.0, result6.get("B"));
            writer.write("Test case 6: " + result6 + "\n");

            // Test case 7: Optimal route from C to D
            Map<String, Double> result7 = dijkstra.discoverOptimalRoute(graph, "C", "D");
            assertEquals(50.0, result7.get("D"));
            writer.write("Test case 7: " + result7 + "\n");

            // Test case 8: Optimal route from D to C
            Map<String, Double> result8 = dijkstra.discoverOptimalRoute(graph, "D", "C");
            assertEquals(15.0, result8.get("C"));
            writer.write("Test case 8: " + result8 + "\n");

            // Test case 9: Optimal route from D to B
            Map<String, Double> result9 = dijkstra.discoverOptimalRoute(graph, "D", "B");
            assertEquals(45.0, result9.get("B"));
            writer.write("Test case 9: " + result9 + "\n");

            // Test case 10: No path from C to A
            Map<String, Double> result10 = dijkstra.discoverOptimalRoute(graph, "C", "A");
            assertEquals(0, result10.size());
            writer.write("Test case 10: " + result10 + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
