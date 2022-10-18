package diarsid.desktop.ui.geometry;

import java.util.HashMap;
import java.util.Map;

import static diarsid.desktop.ui.geometry.Rectangle.Area.CENTRAL;

public class RealRectangleAreas extends RealRectangle {

    private final Map<Area, Rectangle> areasRectangles;

    public RealRectangleAreas(double x, double y, double width, double height, double sideAreaSize) {
        super(x, y, width, height);

        this.areasRectangles = new HashMap<>();

        double x2sideAreaSize = sideAreaSize * 2;

        this.areasRectangles.put(
                Side.LEFT,
                new RealRectangle(
                        0, sideAreaSize,
                        sideAreaSize, this.height() - x2sideAreaSize));

        this.areasRectangles.put(
                Corner.TOP_LEFT,
                new RealRectangle(
                        0, 0,
                        sideAreaSize, sideAreaSize));

        this.areasRectangles.put(
                Side.TOP,
                new RealRectangle(
                        sideAreaSize, 0,
                        this.width() - x2sideAreaSize, sideAreaSize));

        this.areasRectangles.put(
                Corner.TOP_RIGHT,
                new RealRectangle(
                        this.width() - sideAreaSize, 0,
                        sideAreaSize, sideAreaSize));

        this.areasRectangles.put(
                Side.RIGHT,
                new RealRectangle(
                        this.width() - sideAreaSize, sideAreaSize,
                        sideAreaSize, this.height() - x2sideAreaSize));

        this.areasRectangles.put(
                Corner.BOTTOM_RIGHT,
                new RealRectangle(
                        this.width() - sideAreaSize, this.height() - sideAreaSize,
                        sideAreaSize, sideAreaSize));

        this.areasRectangles.put(
                Side.BOTTOM,
                new RealRectangle(
                        sideAreaSize, this.height() - sideAreaSize,
                        this.width() - x2sideAreaSize, sideAreaSize));

        this.areasRectangles.put(
                Corner.BOTTOM_LEFT,
                new RealRectangle(
                        0, this.height() - sideAreaSize,
                        sideAreaSize, this.height() - x2sideAreaSize));

        this.areasRectangles.put(
                CENTRAL,
                new RealRectangle(
                        sideAreaSize, sideAreaSize,
                        this.width() - x2sideAreaSize, this.height() - x2sideAreaSize));
    }

    public boolean isInCentralArea(Point point) {
        return this.isIntersecting(CENTRAL, point);
    }

    public Area areaOf(double position, Side side) {
        switch ( side ) {
            case TOP:
                if ( isIntersecting(side, position, 0) ) {
                    return side;
                }

                for ( Area area : side.neighbours() ) {
                    if ( isIntersecting(area, position, 0) ) {
                        return area;
                    }
                }

                throw new IllegalStateException();
            case LEFT:
                if ( isIntersecting(side, 0, position) ) {
                    return side;
                }

                for ( Area area : side.neighbours() ) {
                    if ( isIntersecting(area, 0, position) ) {
                        return area;
                    }
                }

                throw new IllegalStateException();
            case RIGHT:
                if ( isIntersecting(side, this.width(), position) ) {
                    return side;
                }

                for ( Area area : side.neighbours() ) {
                    if ( isIntersecting(area, this.width(), position) ) {
                        return area;
                    }
                }

                throw new IllegalStateException();
            case BOTTOM:
                if ( isIntersecting(side, position, this.height()) ) {
                    return side;
                }

                for ( Area area : side.neighbours() ) {
                    if ( isIntersecting(area, position, this.height()) ) {
                        return area;
                    }
                }

                throw new IllegalStateException();
            default:
                throw side.unsupported();
        }
    }

    public Area areaOf(Point point, Area areaInitial) {
        if ( this.isIntersecting(areaInitial, point) ) {
            return areaInitial;
        }

        for ( Area area : areaInitial.neighbours() ) {
            if ( this.isIntersecting(area, point) ) {
                return area;
            }
        }

        if ( this.isIntersecting(CENTRAL, point) ) {
            return CENTRAL;
        }

        for ( Area area : this.areasRectangles.keySet() ) {
            if ( this.isIntersecting(area, point) ) {
                return area;
            }
        }

        throw new IllegalStateException();
    }

    private boolean isIntersecting(Area area, Point point) {
        return this.areasRectangles
                .get(area)
                .contains(point);
    }

    private boolean isIntersecting(Area area, double x, double y) {
        return this.areasRectangles
                .get(area)
                .contains(x, y);
    }
}
