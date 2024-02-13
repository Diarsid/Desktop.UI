package diarsid.desktop.ui.geometry;

public class PointToSide extends RealPoint {

    double distance;
    Rectangle.Side side;

    public PointToSide(double x, double y, double distance, Rectangle.Side side) {
        super(x, y);
        this.distance = distance;
        this.side = side;
    }

    public PointToSide(Point point, double distance, Rectangle.Side side) {
        super(point);
        this.distance = distance;
        this.side = side;
    }

    public double distance() {
        return this.distance;
    }

    public Rectangle.Side side() {
        return this.side;
    }
}
