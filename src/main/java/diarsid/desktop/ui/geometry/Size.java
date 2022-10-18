package diarsid.desktop.ui.geometry;

public interface Size {

    double width();

    double height();

    default boolean isOverallBiggerThan(Size other) {
        return this.width() > other.width() &&
                this.height() > other.height();
    }

    default boolean isOverallSmallerThan(Size other) {
        return this.width() < other.width() &&
                this.height() < other.height();
    }

    static Size size(double width, double height) {
        return new RealMutableSize(width, height);
    }
    
}
