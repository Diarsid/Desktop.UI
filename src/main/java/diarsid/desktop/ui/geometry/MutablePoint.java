package diarsid.desktop.ui.geometry;

public interface MutablePoint extends Point, Mutable {

    static interface Listener {

        void onChange(Point oldPint, Point newPoint);
    }

    void set(double x, double y);

    void set(Point point);

    void setX(double x);

    void setY(double y);

    void addListener(Listener listener);
}
