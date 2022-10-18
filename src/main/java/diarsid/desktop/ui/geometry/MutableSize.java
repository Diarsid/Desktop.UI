package diarsid.desktop.ui.geometry;

public interface MutableSize extends Size, Mutable {

    static interface Listener {

        void onChange(Size oldSize, Size newSize);
    }

    void set(double width, double height);

    void set(Size otherSize);
    
    void setWidth(double width);
    
    void setHeight(double height);

    Size asImmutable();

    void addListener(Listener listener);

    static MutableSize mutableSize(double width, double height) {
        return new RealMutableSize(width, height);
    }
    
}
