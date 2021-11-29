package basicbackbonegame2d;

import java.awt.geom.Point2D.Double;
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

public class Pathing {

    public List<Double> path;

    Pathing() {
        path = new ArrayList<Double>();
    }

    void clear() {
        path.clear();
    }

    static double distanceSquared(double x1, double y1, double x2, double y2) {
        return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
    }

    static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(distanceSquared(x1, y1, x2, y2));
    }

    // Step towards the target using the provided maximum step size.
    static Double stepToward(double currentX, double currentY, double targetX, double targetY, double stepSize) {
        // Head straight towards the target.
        double xStep = targetX - currentX;
        double yStep = targetY - currentY;
        double normalizationFactor = Math.sqrt(xStep * xStep + yStep * yStep);

        if (normalizationFactor == 0) {
            return new Double(targetX, targetY);
        }

        double xDir = xStep * stepSize / normalizationFactor;
        double yDir = yStep * stepSize / normalizationFactor;

        if (distanceSquared(targetX, targetY, currentX, currentY) > stepSize * stepSize) {
            return new Double(currentX + xDir, currentY + yDir);
        }

        // Target is within reach, step there immediately.
        return new Double(targetX, targetY);
    }

    // Generate a path from start to end locations, with normalized steps of size
    // [0.0, 1.0]. The first entry in the path list will be the starting location
    // and the last entry is the destination.
    void generatePath(Obstacle obstacle, double startX, double startY, double endX, double endY,
            double stepSizeNormalized) {
        clear();

        Double current = new Double(startX, startY);
        path.add(current);

        while ((current.x != endX) || (current.y != endY)) {
            current = stepToward(current.x, current.y, endX, endY, stepSizeNormalized);
            path.add(current);
        }

        return;
    }

    boolean hasNext() {
        return path.size() > 0;
    }

    // Get the next location.
    // This based on moving towards points in the movement list until the movement
    // range is used up.
    Double getNext(double currentX, double currentY, double range) {
        double curX = currentX;
        double curY = currentY;
        double distanceTraveled = 0;

        Double nextLocation = new Double(currentX, currentY);

        // Track which path locations were reached so we can remove them.
        int idxReached = -1;
        for (int idx = 0; (idx < path.size()) && (distanceTraveled < range); idx++) {
            Double next = path.get(idx);
            nextLocation = stepToward(curX, curY, next.x, next.y, range - distanceTraveled);
            if ((nextLocation.x == next.x) && (nextLocation.y == next.y)) {
                idxReached = idx;
            }
            distanceTraveled += distance(curX, curY, nextLocation.x, nextLocation.y);
            curX = nextLocation.x;
            curY = nextLocation.y;
        }

        // Clear locations that were reached.
        while (idxReached >= 0) {
            path.remove(0);
            idxReached -= 1;
        }

        return nextLocation;
    }
}
