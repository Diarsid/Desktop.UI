package diarsid.desktop.ui.geometry;

public interface Anchor extends Point {
    
    static Anchor anchor(double x, double y) {
        return new RealMutableAnchor(x, y);
    }
    
}
