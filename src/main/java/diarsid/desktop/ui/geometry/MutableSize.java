package diarsid.desktop.ui.geometry;

public interface MutableSize extends Size, Mutable {

    void set(double width, double height);

    void set(Size otherSize);
    
    void setWidth(double width);
    
    void setHeight(double height);

    Size asImmutable();

    static MutableSize mutableSize(double width, double height) {
        return new RealMutableRectangle.RealMutableSize(width, height);
    }
    
}
