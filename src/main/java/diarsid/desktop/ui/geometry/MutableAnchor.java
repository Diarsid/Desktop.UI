package diarsid.desktop.ui.geometry;

public interface MutableAnchor extends Anchor, MutablePoint {

    static MutableAnchor mutableAnchor(double x, double y) {
        return new RealMutableAnchor(x, y);
    }

    Anchor asImmutable();
    
}
