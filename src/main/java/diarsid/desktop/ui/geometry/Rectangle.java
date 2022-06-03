package diarsid.desktop.ui.geometry;

public interface Rectangle {

    Anchor anchor();

    Size size();
    
    default double oppositeAnchorX() {
        return this.anchor().x() + this.size().width();
    }
    
    default double oppositeAnchorY() {
        return this.anchor().y() + this.size().height();
    }
    
    default boolean contains(Rectangle other) {
        return this.anchorLesserThanIn(other) && this.oppositeAnchorBiggerThanIn(other);
    }
    
    default boolean contains(Anchor other) {
        return 
                this.anchor().lesserThan(other) &&
                this.oppositeAnchorX() >= other.x() &&
                this.oppositeAnchorY() >= other.y();
    }
    
    default boolean anchorBiggerThanIn(Rectangle other) {
        return ! this.anchorLesserThanIn(other);
    }
    
    default boolean anchorLesserThanIn(Rectangle other) {
        return 
                this.anchor().x() <= other.anchor().x() &&
                this.anchor().y() <= other.anchor().y();
    }
    
    default boolean oppositeAnchorLesserThanIn(Rectangle other) {
        return ! this.oppositeAnchorBiggerThanIn(other);
    }
    
    default boolean oppositeAnchorBiggerThanIn(Rectangle other) {
        return 
                this.oppositeAnchorX() >= other.oppositeAnchorX() &&
                this.oppositeAnchorY() >= other.oppositeAnchorY();
    }
    
}
