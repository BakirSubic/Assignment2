package org.example;

import java.util.*;

public class DijkstraShortestPath {

    public Map<String, Double> discoverOptimalRoute(Map<String, Map<String, Double>> interconnectedGraph, String departure, String destination) {
        Map<String, Double> travelDistances = new HashMap<>();
        Map<String, String> priorLocations = new HashMap<>();
        Set<String> exploredLocations = new HashSet<>();

        travelDistances.put(departure, 0.0);

        while (!exploredLocations.contains(destination)) {
            String currentLocation = findClosestUnexploredLocation(travelDistances, exploredLocations);
            if (currentLocation == null) {
                break;
            }

            exploredLocations.add(currentLocation);

            for (Map.Entry<String, Double> neighboringLocationEntry : interconnectedGraph.getOrDefault(currentLocation, Collections.emptyMap()).entrySet()) {
                String neighboringLocation = neighboringLocationEntry.getKey();
                double newTravelDistance = travelDistances.get(currentLocation) + neighboringLocationEntry.getValue();

                if (!exploredLocations.contains(neighboringLocation) && newTravelDistance < travelDistances.getOrDefault(neighboringLocation, Double.POSITIVE_INFINITY)) {
                    travelDistances.put(neighboringLocation, newTravelDistance);
                    priorLocations.put(neighboringLocation, currentLocation);
                }
            }
        }

        return constructOptimalRoute(departure, destination, travelDistances, priorLocations);
    }

    private String findClosestUnexploredLocation(Map<String, Double> travelDistances, Set<String> exploredLocations) {
        return travelDistances.entrySet()
                .stream()
                .filter(entry -> !exploredLocations.contains(entry.getKey()))
                .min(Comparator.comparingDouble(entry -> travelDistances.getOrDefault(entry.getKey(), Double.POSITIVE_INFINITY)))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private Map<String, Double> constructOptimalRoute(String departure, String destination, Map<String, Double> travelDistances, Map<String, String> priorLocations) {
        Map<String, Double> result = new HashMap<>();
        LinkedList<String> path = new LinkedList<>();

        String currentLocation = destination;
        while (currentLocation != null) {
            path.addFirst(currentLocation);
            currentLocation = priorLocations.get(currentLocation);
        }

        if (!path.getFirst().equals(departure)) {
            return Collections.emptyMap();
        }

        for (String location : travelDistances.keySet()) {
            double distance = travelDistances.get(location);
            result.put(location, distance < Double.POSITIVE_INFINITY ? distance : null);
        }
        return result;
    }

    public void imposeConnectivityConstraint(Map<String, Map<String, Double>> interconnectedGraph, String source, String target, double constraintValue) {
        interconnectedGraph.computeIfAbsent(source, k -> new HashMap<>()).put(target, constraintValue);
    }

    public void imposeProbabilityConstraint(Map<String, Map<String, Double>> interconnectedGraph, String source, String target, double probability) {
        interconnectedGraph.computeIfAbsent(source, k -> new HashMap<>()).put(target, probability);
    }
}
