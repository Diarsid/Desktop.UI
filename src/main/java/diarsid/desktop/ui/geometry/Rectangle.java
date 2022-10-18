package diarsid.desktop.ui.geometry;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import diarsid.support.objects.CommonEnum;

import static java.util.Collections.emptyList;

import static diarsid.desktop.ui.geometry.Rectangle.Side.BOTTOM;
import static diarsid.desktop.ui.geometry.Rectangle.Side.LEFT;
import static diarsid.desktop.ui.geometry.Rectangle.Side.Orientation.HORIZONTAL;
import static diarsid.desktop.ui.geometry.Rectangle.Side.Orientation.VERTICAL;
import static diarsid.desktop.ui.geometry.Rectangle.Side.RIGHT;
import static diarsid.desktop.ui.geometry.Rectangle.Side.TOP;
import static diarsid.desktop.ui.geometry.RectangleSidesAndCorners.NEIGHBOURS_SIDES_BY_SIDE;

public interface Rectangle {

    public interface Area {

        public static Area CENTRAL = new Area() {

            @Override
            public String name() {
                return "CENTRAL";
            }

            @Override
            public List<Area> neighbours() {
                return emptyList();
            }
        };

        String name();

        default List<Area> neighbours() {
            return NEIGHBOURS_SIDES_BY_SIDE.get(this);
        }

        default Area neighbourBefore() {
            return NEIGHBOURS_SIDES_BY_SIDE.get(this).get(0);
        }

        default Area neighbourAfter() {
            return NEIGHBOURS_SIDES_BY_SIDE.get(this).get(1);
        }
    }

    public static enum Side implements Area, CommonEnum<Side> {

        TOP(HORIZONTAL),
        LEFT(VERTICAL),
        RIGHT(VERTICAL),
        BOTTOM(HORIZONTAL);

        public final Orientation orientation;

        private Side(Orientation orientation) {
            this.orientation = orientation;
        }

        public enum Orientation implements CommonEnum<Orientation> {
            VERTICAL,
            HORIZONTAL
        }
    }

    public static enum Corner implements Area, CommonEnum<Corner> {

        TOP_LEFT(TOP, LEFT),        TOP_RIGHT(TOP, RIGHT),

        BOTTOM_LEFT(BOTTOM, LEFT),  BOTTOM_RIGHT(BOTTOM, RIGHT);

        public final Side sideBefore;
        public final Side sideAfter;
        public final List<Side> sides;

        private Corner(Side sideBefore, Side sideAfter) {
            this.sideBefore = sideBefore;
            this.sideAfter = sideAfter;
            this.sides = List.of(this.sideBefore, this.sideAfter);
        }
    }

    Anchor anchor();

    Size size();

    default double height() {
        return this.size().height();
    }

    default double width() {
        return this.size().width();
    }
    
    default double oppositeAnchorX() {
        return this.anchor().x() + this.size().width();
    }
    
    default double oppositeAnchorY() {
        return this.anchor().y() + this.size().height();
    }
    
    default boolean contains(Rectangle other) {
        return this.anchorLesserThanIn(other) && this.oppositeAnchorBiggerThanIn(other);
    }

    default boolean contains(double x, double y) {
        return
                this.anchor().lesserThan(x, y) &&
                this.oppositeAnchorX() >= x &&
                this.oppositeAnchorY() >= y;
    }
    
    default boolean contains(Point point) {
        return 
                this.anchor().lesserThan(point) &&
                this.oppositeAnchorX() >= point.x() &&
                this.oppositeAnchorY() >= point.y();
    }

    default boolean contains(double x, double y, double width, double height) {
        return
                this.anchor().lesserThan(x, y) &&
                this.oppositeAnchorX() >= x + width &&
                this.oppositeAnchorY() >= y + height;
    }

    default EnumSet<Rectangle.Side> findCollisions(double x, double y, double width, double height) {
        List<Rectangle.Side> collidedSides = new ArrayList<>();

        double thisX = this.anchor().x();
        double thisY = this.anchor().y();
        double thisOppX = this.oppositeAnchorX();
        double thisOppY = this.oppositeAnchorY();

        if ( x < thisX ) {
            collidedSides.add(LEFT);
        }

        if ( x + width > thisOppX ) {
            collidedSides.add(RIGHT);
        }

        if ( y < thisY ) {
            collidedSides.add(TOP);
        }

        if ( y + height > thisOppY ) {
            collidedSides.add(BOTTOM);
        }

        if ( collidedSides.isEmpty()) {
            return EnumSet.noneOf(Rectangle.Side.class);
        }

        return EnumSet.copyOf(collidedSides);
    }

    default PointToCorner pointToCorner(Corner corner, Point point) {
        return this.pointToCorner(corner, point.x(), point.y());
    }

    default PointToCorner pointToCorner(Corner corner, double x, double y) {
        double dX;
        double dY;
        Size size = this.size();

        switch ( corner ) {
            case TOP_LEFT:
                dX = x;
                dY = y;
                break;
            case BOTTOM_LEFT:
                dX = x;
                dY = size.height() - y;
                break;
            case TOP_RIGHT:
                dX = size.width() - x;
                dY = y;
                break;
            case BOTTOM_RIGHT:
                dX = size.width() - x;
                dY = size.height() - y;
                break;
            default:
                throw corner.unsupported();
        }

        return new PointToCorner(corner, x, y, dX, dY);
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
