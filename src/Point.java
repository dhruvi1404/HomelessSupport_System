/**
 * Capture a two-dimensional (x, y) point, with integer coordinates.
 */
public class Point {
    private int x, y;

    /**
     * Create a point at a given location.
     * @param x -- x coordinate
     * @param y -- y coordinate
     */
    public Point( int x, int y ) {
        this.x = x;
        this.y = y;
    }

    /**
     * Return the x coordinate of this point.
     * @return -- the integer x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Return the y coordinate of this point.
     * @return -- the integer y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Report the straight-line distance from this point to another given point
     * @param to -- the target destination point to which we want to know the distance
     * @return -- the distance
     */
    public Double distanceTo( Point to ) throws IllegalArgumentException {
        if (to == null) {
            throw new IllegalArgumentException( "No second pont" );
        }

        Double distance = Math.sqrt( (to.x - this.x)*(to.x - this.x) + (to.y - this.y)*(to.y - this.y));
        return distance;
    }
    public static boolean isValidCoordinate(int coordinate) {

        return coordinate >= 0; // coordinate must be non-negative
    }

    public static boolean isValidPoint(Point point) {
        return point != null && isValidCoordinate(point.getX()) && isValidCoordinate(point.getY());
    }
}
