package diarsid.desktop.ui.geometry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static diarsid.desktop.ui.geometry.Rectangle.Side.LEFT;
import static diarsid.desktop.ui.geometry.Rectangle.Side.RIGHT;
import static diarsid.desktop.ui.geometry.Rectangle.Side.TOP;
import static diarsid.desktop.ui.geometry.Rectangle.Side.BOTTOM;
import static diarsid.desktop.ui.geometry.Rectangle.Corner.TOP_LEFT;
import static diarsid.desktop.ui.geometry.Rectangle.Corner.TOP_RIGHT;
import static diarsid.desktop.ui.geometry.Rectangle.Corner.BOTTOM_LEFT;
import static diarsid.desktop.ui.geometry.Rectangle.Corner.BOTTOM_RIGHT;

final class RectangleSidesAndCorners {

    public static final Map<Rectangle.Area, List<Rectangle.Area>> NEIGHBOURS_SIDES_BY_SIDE = new HashMap<>();

    static {
        NEIGHBOURS_SIDES_BY_SIDE.put(LEFT, List.of(BOTTOM_LEFT, TOP_LEFT));
        NEIGHBOURS_SIDES_BY_SIDE.put(TOP, List.of(TOP_LEFT, TOP_RIGHT));
        NEIGHBOURS_SIDES_BY_SIDE.put(RIGHT, List.of(TOP_RIGHT, BOTTOM_RIGHT));
        NEIGHBOURS_SIDES_BY_SIDE.put(BOTTOM, List.of(BOTTOM_RIGHT, BOTTOM_LEFT));

        NEIGHBOURS_SIDES_BY_SIDE.put(TOP_LEFT, List.of(LEFT, TOP));
        NEIGHBOURS_SIDES_BY_SIDE.put(TOP_RIGHT, List.of(TOP, RIGHT));
        NEIGHBOURS_SIDES_BY_SIDE.put(BOTTOM_RIGHT, List.of(RIGHT, BOTTOM));
        NEIGHBOURS_SIDES_BY_SIDE.put(BOTTOM_LEFT, List.of(BOTTOM, LEFT));
    }

    private RectangleSidesAndCorners() {};
}
