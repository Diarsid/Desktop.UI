package diarsid.desktop.ui.geometry;

public interface MutablePoint extends Point, Mutable {

    void set(double x, double y);

    void set(Point point);

    void setX(double x);

    void setY(double y);
}
