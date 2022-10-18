package diarsid.desktop.ui.geometry;

import diarsid.support.objects.references.Possible;

import static java.lang.String.format;

import static diarsid.support.objects.references.References.simplePossibleButEmpty;
import static diarsid.support.objects.references.References.simplePossibleWith;

public class RealMutableRectangle implements MutableRectangle {

    final RealMutableAnchor anchor;
    final RealMutableSize size;
    final Possible<MutableSize> minSize;

    RealMutableRectangle() {
        this.anchor = new RealMutableAnchor();
        this.size = new RealMutableSize();
        this.minSize = simplePossibleButEmpty();
    }
    
    RealMutableRectangle(Anchor givenAnchor, Size givenSize) {
        this.anchor = new RealMutableAnchor(givenAnchor);
        this.size = new RealMutableSize(givenSize);
        this.minSize = simplePossibleButEmpty();
    }
    
    RealMutableRectangle(double x, double y, double width, double height) {
        this.anchor = new RealMutableAnchor(x, y);
        this.size = new RealMutableSize(width, height);
        this.minSize = simplePossibleButEmpty();
    }
    
    RealMutableRectangle(
            Anchor givenAnchor, Size givenSize, Size minSize) {
        this.anchor = new RealMutableAnchor(givenAnchor);
        this.size = new RealMutableSize(givenSize);
        this.minSize = simplePossibleWith(new RealMutableSize(minSize));
    }
    
    RealMutableRectangle(
            double x, double y, double width, double height, double minWidth, double minHeight) {
        this.anchor = new RealMutableAnchor(x, y);
        this.size = new RealMutableSize(width, height);
        this.minSize = simplePossibleWith(new RealMutableSize(minWidth, minHeight));
    }

    @Override
    public String toString() {
        return format("%s[x:%s, y:%s, width:%s, height:%s]", 
                      this.getClass().getSimpleName(), 
                      this.anchor.x, 
                      this.anchor.y,
                      this.size.width,
                      this.size.height);
    }

    @Override
    public Rectangle asImmutable() {
        return this;
    }

    @Override
    public MutableAnchor anchor() {
        return this.anchor;
    }

    @Override
    public MutableSize size() {
        return this.size;
    }

    @Override
    public Possible<MutableSize> minSize() {
        return this.minSize;
    }

    @Override
    public boolean fitIn(Rectangle rectangle) {
        if ( rectangle.contains(this) ) {
            return false;
        }

        if ( this.minSize().isPresent() ) {
            this.fitRespectingMinSizeIn(rectangle);
        } else {
            this.fitAnywayIn(rectangle);
        }
        return true;
    }

//    private boolean fit(MutableRectangle fitted) {
//        if ( this.contains(fitted) ) {
//            return false;
//        }
//
//        if ( fitted.minSize().isPresent() ) {
//            this.fitRespectingMinSize(fitted);
//        } else {
//            this.fitAnyway(fitted);
//        }
//        return true;
//    }

    private void fitRespectingMinSizeIn(Rectangle rectangle) {
        this.toMinSizeAbsolute();
        Size fittedSize = this.size();

        double anchorX = (rectangle.size().width() - fittedSize.width()) / 2;
        double anchorY = (rectangle.size().height() - fittedSize.height()) / 2;

        anchorX = Math.max(anchorX, this.anchor.x);
        anchorY = Math.max(anchorY, this.anchor.y);

        this.anchor.set(anchorX, anchorY);
    }

    private void notFinished(MutableRectangle fitted) {
        if ( this.contains(fitted.anchor()) ) {

        } else {

        }



        MutableSize fittedSize = fitted.size();
        Size insetSize = this.size();

        if ( fitted.isSmallerThanMinSize() ) {
            fitted.toMinSizeAbsolute();
        } else if ( fitted.isBiggerThanMinSize() ) {

        } else {

        }

        if ( fittedSize.isOverallBiggerThan(insetSize) ) {
            fitted.toMinSizeAbsolute();
            fitted.anchor().set(this.anchor);
        } else if ( fittedSize.isOverallSmallerThan(insetSize) ) {

        }
    }

    private void fitAnywayIn(Rectangle rectangle) {
        MutableSize fittedSize = this.size;

        if( fittedSize.width() > rectangle.size().width() ) {
            fittedSize.setWidth(rectangle.size().width());
        }
        if ( fittedSize.height() > rectangle.size().height() ) {
            fittedSize.setHeight(rectangle.size().height());
        }

        double xDiff = rectangle.size().width() - fittedSize.width();
        double yDiff = rectangle.size().height()- fittedSize.height();

        double fitterAnchorX = rectangle.anchor().x() + (xDiff / 2);
        double fitterAnchorY = rectangle.anchor().y() + (yDiff / 2);

        this.anchor.set(fitterAnchorX, fitterAnchorY);
    }

    @Override
    public boolean isValuePresent() {
        return this.anchor.isValuePresent() && this.size.isValuePresent();
    }

    @Override
    public boolean isValueAbsent() {
        return this.anchor.isValueAbsent() && this.size.isValueAbsent();
    }

    protected Object[] params() {
        return new Object[] {
            this.anchor.x, this.anchor.y, this.size.width, this.size.height};
    }
}
