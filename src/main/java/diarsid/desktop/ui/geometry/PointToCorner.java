package diarsid.desktop.ui.geometry;

public class PointToCorner implements Point {

    public final Rectangle.Corner corner;
    public final double x;
    public final double y;
    public final double dX;
    public final double dY;

    PointToCorner(Rectangle.Corner corner,
                  double x, double y,
                  double dX, double dY) {
        this.corner = corner;
        this.x = x;
        this.y = y;
        this.dX = dX;
        this.dY = dY;
    }

    public Rectangle.Side closerSide() {
        switch (this.corner) {
            case TOP_LEFT:
                if (this.dX > this.dY) {
                    return Rectangle.Side.TOP;
                } else {
                    return Rectangle.Side.LEFT;
                }
            case TOP_RIGHT:
                if (this.dX > this.dY) {
                    return Rectangle.Side.TOP;
                } else {
                    return Rectangle.Side.RIGHT;
                }
            case BOTTOM_LEFT:
                if (this.dX > this.dY) {
                    return Rectangle.Side.BOTTOM;
                } else {
                    return Rectangle.Side.LEFT;
                }
            case BOTTOM_RIGHT:
                if (this.dX > this.dY) {
                    return Rectangle.Side.BOTTOM;
                } else {
                    return Rectangle.Side.RIGHT;
                }
            default:
                throw this.corner.unsupported();
        }
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
