package diarsid.desktop.ui.geometry;

public class RealRectangle implements Rectangle {

    final Anchor anchor;
    final Size size;

    public RealRectangle(Anchor anchor, Size size) {
        this.anchor = anchor;
        this.size = size;
    }

    public RealRectangle(double x, double y, double width, double height) {
        this(Anchor.anchor(x, y), Size.size(width, height));
    }

    @Override
    public Anchor anchor() {
        return this.anchor;
    }

    @Override
    public Size size() {
        return this.size;
    }
}
