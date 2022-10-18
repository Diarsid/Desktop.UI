package diarsid.desktop.ui.geometry;

public interface Point {

    double x();

    double y();

    default boolean lesserThan(Point other) {
        return this.x() <= other.x() && this.y() <= other.y();
    }

    default boolean lesserThan(double x, double y) {
        return this.x() <= x && this.y() <= y;
    }
    
}
