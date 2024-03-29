package diarsid.desktop.ui.geometry;

import diarsid.support.objects.references.Possible;

public interface MutableRectangle extends Rectangle, Mutable {

    static interface Listener {

        void onChange(
                Point oldPint, Point newPoint,
                Size oldSize, Size newSize);
    }

    @Override
    MutableAnchor anchor();

    @Override
    MutableSize size();
    
    Possible<MutableSize> minSize();
    
    Rectangle asImmutable();

    boolean fitIn(Rectangle rectangle);
    
    default boolean toMinSizeAbsolute() {
        if ( this.minSize().isPresent() ) {
            this.size().set(this.minSize().orThrow());
            return true;
        } else {
            return false;
        } 
    }
    
    default boolean toMinSizeIfSmaller() {        
        if ( this.minSize().isPresent() ) {
            MutableSize thisSize = this.size();
            Size thisMinSize = this.minSize().orThrow();
            if ( thisSize.width() < thisMinSize.width() ) {
                thisSize.setWidth(thisMinSize.width());
            }
            if ( thisSize.height() < thisMinSize.height() ) {
                thisSize.setHeight(thisMinSize.height());
            }
            return true;
        } else {
            return false;
        }       
    }
    
    default boolean isSmallerThanMinSize() {
        if ( this.minSize().isPresent() ) {
            Size minSize = this.minSize().orThrow();
            Size size = this.size();
            return 
                    size.width() < minSize.width() || 
                    size.height() < minSize.height();
        } else {
            return false;
        }
    }
    
    default boolean isBiggerThanMinSize() {
        if ( this.minSize().isPresent() ) {
            Size minSize = this.minSize().orThrow();
            Size size = this.size();
            return 
                    size.width() > minSize.width() || 
                    size.height() > minSize.height();
        } else {
            return false;
        }
    }

    default void addListener(Listener listener) {
        this.anchor().addListener((oldPoint, newPoint) -> {
            Size size = this.size();
            listener.onChange(oldPoint, newPoint, size, size);
        });
        this.size().addListener((oldSize, newSize) -> {
            Point point = this.anchor();
            listener.onChange(point, point, oldSize, newSize);
        });
    }
    
}
