package diarsid.desktop.ui.geometry;

import java.util.List;

import static java.lang.String.format;

public interface Screen extends Rectangle {    
    
    public static Screen screen(int insetValue, double width, double height) {
        return new RealScreen(insetValue, width, height);
    }
    
    public static Screen screen(List<Integer> insets, double width, double height) {
        switch ( insets.size() ) {
            case 0 : {
                return new RealScreen(0, width, height);
            }
            case 1 : {
                return new RealScreen(
                        insets.get(0), 
                        width, height);
            }
            case 4 : {
                return new RealScreen(
                        insets.get(0), insets.get(1), insets.get(2), insets.get(3), 
                        width, height);
            }
            default : { 
                String message = format(
                        "Insets cannot have '%s' parameters!", insets.size());
                throw new IllegalArgumentException(message);
            }
        }        
    }
    
    boolean fit(MutableRectangle mutableRectangle);
    
}
