package diarsid.desktop.ui.geometry;

public interface Anchor extends Point {
    
    static Anchor anchor(double x, double y) {
        return new RealMutableRectangle.RealMutableAnchor(x, y);
    }
    
    default boolean lesserThan(Anchor other) {
        return this.x() <= other.x() && this.y() <= other.y();
    }
    
}
