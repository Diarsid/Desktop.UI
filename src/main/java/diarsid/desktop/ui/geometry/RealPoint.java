package diarsid.desktop.ui.geometry;

class RealPoint implements Point {

    double x;
    double y;

    RealPoint() {
    }

    RealPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    RealPoint(Point point) {
        this.x = point.x();
        this.y = point.y();
    }

    @Override
    public double x() {
        return this.x;
    }

    @Override
    public double y() {
        return this.y;
    }
}
