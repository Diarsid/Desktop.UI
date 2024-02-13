package diarsid.desktop.ui.geometry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;

import diarsid.support.exceptions.UnsupportedLogicException;
import diarsid.support.objects.CommonEnum;

import static java.util.Collections.emptyList;

import static diarsid.desktop.ui.geometry.Rectangle.Corner.BOTTOM_LEFT;
import static diarsid.desktop.ui.geometry.Rectangle.Corner.BOTTOM_RIGHT;
import static diarsid.desktop.ui.geometry.Rectangle.Corner.TOP_LEFT;
import static diarsid.desktop.ui.geometry.Rectangle.Corner.TOP_RIGHT;
import static diarsid.desktop.ui.geometry.Rectangle.OutsideToSide.FROM_OUTSIDE_TO_BOTTOM;
import static diarsid.desktop.ui.geometry.Rectangle.OutsideToSide.FROM_OUTSIDE_TO_LEFT;
import static diarsid.desktop.ui.geometry.Rectangle.OutsideToSide.FROM_OUTSIDE_TO_RIGHT;
import static diarsid.desktop.ui.geometry.Rectangle.OutsideToSide.FROM_OUTSIDE_TO_TOP;
import static diarsid.desktop.ui.geometry.Rectangle.Side.BOTTOM;
import static diarsid.desktop.ui.geometry.Rectangle.Side.LEFT;
import static diarsid.desktop.ui.geometry.Rectangle.Side.Orientation.HORIZONTAL;
import static diarsid.desktop.ui.geometry.Rectangle.Side.Orientation.VERTICAL;
import static diarsid.desktop.ui.geometry.Rectangle.Side.RIGHT;
import static diarsid.desktop.ui.geometry.Rectangle.Side.TOP;
import static diarsid.desktop.ui.geometry.RectangleSidesAndCorners.NEIGHBOURS_SIDES_BY_SIDE;

public interface Rectangle extends Serializable {

    public interface Area extends Serializable {

        public interface Inside extends Area {

        }

        public interface Outside extends Area {

        }

        public static Area.Inside CENTRAL = new Area.Inside() {

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

    public static enum Side implements Area.Inside, CommonEnum<Side>, Serializable {

        TOP(HORIZONTAL),
        LEFT(VERTICAL),
        RIGHT(VERTICAL),
        BOTTOM(HORIZONTAL);

        private static final EnumMap<Side, Side> NEXT_SIDE_BY_SIDE = new EnumMap<Side, Side>(Side.class);

        static {
            NEXT_SIDE_BY_SIDE.put(TOP, RIGHT);
            NEXT_SIDE_BY_SIDE.put(RIGHT, BOTTOM);
            NEXT_SIDE_BY_SIDE.put(BOTTOM, LEFT);
            NEXT_SIDE_BY_SIDE.put(LEFT, TOP);
        }

        public final Orientation orientation;


        private Side(Orientation orientation) {
            this.orientation = orientation;
        }

        public enum Orientation implements CommonEnum<Orientation>, Serializable {
            VERTICAL,
            HORIZONTAL
        }

        public Side nextByClock() {
            return NEXT_SIDE_BY_SIDE.get(this);
        }
    }

    public static enum Corner implements Area.Inside, CommonEnum<Corner>, Serializable {

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

    public static enum OutsideToSide implements Area.Outside, CommonEnum<OutsideToSide>, Serializable {

        FROM_OUTSIDE_TO_TOP(TOP),
        FROM_OUTSIDE_TO_LEFT(LEFT),
        FROM_OUTSIDE_TO_RIGHT(RIGHT),
        FROM_OUTSIDE_TO_BOTTOM(BOTTOM);

        public final Side side;

        OutsideToSide(Side side) {
            this.side = side;
        }
    }

    public static enum OutsideToCorner implements Area.Outside, CommonEnum<OutsideToCorner>, Serializable {

        FROM_OUTSIDE_TO_TOP_LEFT(TOP_LEFT, FROM_OUTSIDE_TO_TOP, FROM_OUTSIDE_TO_LEFT),
        FROM_OUTSIDE_TO_TOP_RIGHT(TOP_RIGHT, FROM_OUTSIDE_TO_TOP, FROM_OUTSIDE_TO_RIGHT),
        FROM_OUTSIDE_TO_BOTTOM_LEFT(BOTTOM_LEFT, FROM_OUTSIDE_TO_BOTTOM, FROM_OUTSIDE_TO_LEFT),
        FROM_OUTSIDE_TO_BOTTOM_RIGHT(BOTTOM_RIGHT, FROM_OUTSIDE_TO_BOTTOM, FROM_OUTSIDE_TO_RIGHT);

        public final Corner corner;
        public final OutsideToSide outsideToSideBefore;
        public final OutsideToSide outsideToSideAfter;

        OutsideToCorner(Corner corner, OutsideToSide outsideToSideBefore, OutsideToSide outsideToSideAfter) {
            this.corner = corner;
            this.outsideToSideBefore = outsideToSideBefore;
            this.outsideToSideAfter = outsideToSideAfter;
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

    default Side closerSideToOuterPointOf(OutsideToCorner outsideToCorner, Point point) {
        return this.closerSideToOuterPointOf(outsideToCorner, point.x(), point.y());
    }

    default Side closerSideToOuterPointOf(OutsideToCorner outsideToCorner, double x, double y) {
        double width = this.width();
        double height = this.height();

        double dX;
        double dY;
        Side closerSide;

        switch ( outsideToCorner ) {
            case FROM_OUTSIDE_TO_TOP_LEFT:
                if ( x > 0 && y > 0) {
                    throw new UnsupportedLogicException();
                }

                dX = 0 - x;
                dY = 0 - y;

                if ( dX > dY ) {
                    closerSide = LEFT;
                }
                else {
                    closerSide = TOP;
                }

                break;
            case FROM_OUTSIDE_TO_TOP_RIGHT:
                if ( x < width && y > 0 ) {
                    throw new UnsupportedLogicException();
                }

                dX = x - width;
                dY = 0 - y;

                if ( dX > dY ) {
                    closerSide = RIGHT;
                }
                else {
                    closerSide = TOP;
                }

                break;
            case FROM_OUTSIDE_TO_BOTTOM_RIGHT:
                if ( x < width && y < height ) {
                    throw new UnsupportedLogicException();
                }

                dX = x - width;
                dY = y - height;

                if ( dX > dY ) {
                    closerSide = RIGHT;
                }
                else {
                    closerSide = BOTTOM;
                }

                break;
            case FROM_OUTSIDE_TO_BOTTOM_LEFT:
                if ( x > 0 && y < this.height() ) {
                    throw new UnsupportedLogicException();
                }

                dX = 0 - x;
                dY = y - height;

                if ( dX > dY ) {
                    closerSide = LEFT;
                }
                else {
                    closerSide = BOTTOM;
                }

                break;
            default:
                throw outsideToCorner.unsupported();
        }

        return closerSide;
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

    default PointToSide pointToSideInside(Point point) {
        if ( ! this.contains(point) ) {
            throw new IllegalArgumentException();
        }

        double x = this.anchor().x();
        double y = this.anchor().y();

        double pX = point.x();
        double pY = point.y();

        double width = this.size().width();
        double height = this.size().height();

        double distanceToTop = pY - y;
        double distanceToRight = (x + width) - pX;
        double distanceToBottom = (y + height) - pY;
        double distanceToLeft = pX - x;

        Side side;
        double distance;

        if ( distanceToTop <= distanceToBottom ) {
            if ( distanceToLeft <= distanceToRight ) {
                if ( distanceToLeft <= distanceToTop ) {
                    distance = distanceToLeft;
                    side = LEFT;
                }
                else {
                    distance = distanceToTop;
                    side = TOP;
                }
            }
            else {
                if ( distanceToTop <= distanceToRight ) {
                    distance = distanceToTop;
                    side = TOP;
                }
                else {
                    distance = distanceToRight;
                    side = RIGHT;
                }
            }
        }
        else {
            if ( distanceToLeft <= distanceToRight ) {
                if ( distanceToLeft <= distanceToBottom ) {
                    distance = distanceToLeft;
                    side = LEFT;
                }
                else {
                    distance = distanceToBottom;
                    side = BOTTOM;
                }
            }
            else {
                if ( distanceToRight <= distanceToBottom ) {
                    distance = distanceToRight;
                    side = RIGHT;
                }
                else {
                    distance = distanceToBottom;
                    side = BOTTOM;
                }
            }
        }

        return new PointToSide(point, distance, side);
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
