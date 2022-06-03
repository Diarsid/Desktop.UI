package diarsid.desktop.ui.geometry;

public interface MutableAnchor extends Anchor, MutablePoint {

    Anchor asImmutable();
    
}
