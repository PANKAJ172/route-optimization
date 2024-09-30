package com.route.utils;

import com.route.model.Stop;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class Calculations {
    public List<Stop> optimizeRoute(List<Stop> stops) {
        List<Stop> route = new ArrayList<>();
        Stop depot = new Stop("0", 0, 0);
        route.add(depot);

        while (!stops.isEmpty()) {
            Stop lastStop = route.get(route.size() - 1);
            Stop nearestStop = findNearestStop(lastStop, stops);
            System.out.println("nearestStop :: " + nearestStop);
            route.add(nearestStop);
            stops.remove(nearestStop);
        }
        route.add(depot);
        route = perform2Opt(route);
        return route;
    }

    public double calculateDistance(Stop a, Stop b) {
        double dd = Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
        return dd;
    }


    private Stop findNearestStop(Stop current, List<Stop> stops) {
        return stops.stream()
                .min(Comparator.comparing(stop -> calculateDistance(current, stop)))
                .orElse(null);
    }

    private List<Stop> perform2Opt(List<Stop> route) {
        boolean improvement = true;
        while (improvement) {
            improvement = false;
            for (int i = 1; i < route.size() - 2; i++) {
                for (int j = i + 1; j < route.size() - 1; j++) {
                    if (calculateDistance(route.get(i - 1), route.get(i)) +
                            calculateDistance(route.get(j), route.get(j + 1)) >
                            calculateDistance(route.get(i - 1), route.get(j)) +
                                    calculateDistance(route.get(i), route.get(j + 1))) {
                        // Reverse the segment from i to j
                        reverseSegment(route, i, j);
                        improvement = true;
                    }
                }
            }
        }
        return route;
    }

    // Helper method to reverse a segment in the route
    private void reverseSegment(List<Stop> route, int start, int end) {
        while (start < end) {
            Stop temp = route.get(start);
            route.set(start, route.get(end));
            route.set(end, temp);
            start++;
            end--;
        }
    }

    public double calculateTotalDistance(List<Stop> stops) {
        double totalDistance = 0.0;
        // Depot coordinates (starting point)
        int depotX = 0;
        int depotY = 0;
        // Calculate distance from depot to the first stop
        Stop previousStop = new Stop("Depot", depotX, depotY); // Starting at the depot
        for (Stop currentStop : stops) {
            // Calculate distance between consecutive stops
            double distance = calculateDistance(previousStop, currentStop);
            totalDistance += distance;
            // Move to the next stop
            previousStop = currentStop;
        }
        // After the last stop, calculate distance back to the depot
        totalDistance += calculateDistance(previousStop, new Stop("Depot", depotX, depotY));

        return totalDistance;
    }

}
