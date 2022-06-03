package diarsid.desktop.ui.geometry;

public interface MutableNamedRectangle extends NamedRectangle, MutableRectangle {

    @Override
    NamedRectangle asImmutable();
    
}
