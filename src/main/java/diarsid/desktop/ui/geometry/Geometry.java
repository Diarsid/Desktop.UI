package diarsid.desktop.ui.geometry;

import static diarsid.desktop.ui.geometry.Anchor.anchor;
import static diarsid.desktop.ui.geometry.Screen.screen;
import static diarsid.desktop.ui.geometry.Size.size;

public class Geometry {
    
    public static void main(String[] args) {
        Screen screen = screen(10, 100, 100);
        
        MutableRectangle rectangle = new RealMutableNamedRectangle("a", anchor(110, 110), size(120, 60), size(100, 40));
        screen.fit(rectangle);
        
        System.out.println(rectangle.toString());
    }
}
