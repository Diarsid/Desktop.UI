package diarsid.desktop.ui.geometry;

import static java.lang.String.format;

public class RealMutableNamedRectangle 
        extends RealMutableRectangle 
        implements MutableNamedRectangle {
        
    final String name;

    public RealMutableNamedRectangle(String name) {
        super();
        this.name = name;
    }

    public RealMutableNamedRectangle(String name, Anchor anchor, Size size) {
        super(anchor, size);
        this.name = name;
    }

    public RealMutableNamedRectangle(String name, double x, double y, double width, double height) {
        super(x, y, width, height);
        this.name = name;
    }

    public RealMutableNamedRectangle(
            String name, Anchor anchor, Size size, Size minSize) {
        super(anchor, size, minSize);
        this.name = name;
    }

    public RealMutableNamedRectangle(
            String name, 
            double x, double y, 
            double width, double height, 
            double minWidth, double minHeight) {
        super(x, y, width, height, minWidth, minHeight);
        this.name = name;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String toString() {
        return format("%s[name:%s, x:%s, y:%s, width:%s, height:%s]", 
                      this.getClass().getSimpleName(), 
                      this.name, 
                      super.anchor.x, 
                      super.anchor.y,
                      super.size.width,
                      super.size.height);
    }

    @Override
    public NamedRectangle asImmutable() {
        return this;
    }

    @Override
    protected Object[] params() {
        return new Object[] {
            this.name, super.anchor.x, super.anchor.y, super.size.width, super.size.height};
    }
}
