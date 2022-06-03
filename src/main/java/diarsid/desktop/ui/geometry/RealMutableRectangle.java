package diarsid.desktop.ui.geometry;

import java.util.Objects;

import diarsid.support.objects.references.Possible;

import static java.lang.String.format;

import static diarsid.support.objects.references.References.simplePossibleButEmpty;
import static diarsid.support.objects.references.References.simplePossibleWith;

public class RealMutableRectangle implements MutableRectangle {

    static class RealMutableAnchor implements MutableAnchor {

        RealMutableAnchor() {
            this.present = false;
        }
        
        RealMutableAnchor(Anchor anchor) {
            this.present = true;
            this.x = anchor.x();
            this.y = anchor.y();
        }
        
        RealMutableAnchor(double x, double y) {
            this.present = true;
            this.x = x;
            this.y = y;
        }

        boolean present;
        protected double x;
        protected double y;

        @Override
        public double x() {
            return this.x;
        }

        @Override
        public double y() {
            return this.y;
        }

        @Override
        public boolean isValueAbsent() {
            return ! this.present;
        }

        @Override
        public boolean isValuePresent() {
            return this.present;
        }

        @Override
        public void set(double x, double y) {
            this.present = true;
            this.x = x;
            this.y = y;
        }

        @Override
        public Anchor asImmutable() {
            return this;
        }

        @Override
        public void setX(double x) {
            this.present = true;
            this.x = x;
        }

        @Override
        public void setY(double y) {
            this.present = true;
            this.y = y;
        }

        @Override
        public void set(Point point) {
            this.set(point.x(), point.y());
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 41 * hash + Objects.hashCode(this.x);
            hash = 41 * hash + Objects.hashCode(this.y);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if ( this == obj ) {
                return true;
            }
            if ( obj == null ) {
                return false;
            }
            if ( getClass() != obj.getClass() ) {
                return false;
            }
            final RealMutableAnchor other = (RealMutableAnchor) obj;
            if ( !Objects.equals(this.x, other.x) ) {
                return false;
            }
            if ( !Objects.equals(this.y, other.y) ) {
                return false;
            }
            return true;
        }

    }

    static class RealMutableSize implements MutableSize {

        RealMutableSize() {
            this.present = false;
        }
        
        RealMutableSize(Size size) {
            this.present = true;
            this.width = size.width();
            this.height = size.height();
        }
        
        RealMutableSize(double width, double height) {
            this.present = true;
            this.width = width;
            this.height = height;
        }

        boolean present;
        protected double width;
        protected double height;

        @Override
        public double width() {
            return this.width;
        }

        @Override
        public double height() {
            return this.height;
        }

        @Override
        public boolean isValuePresent() {
            return ! this.present;
        }

        @Override
        public boolean isValueAbsent() {
            return this.present;
        }

        @Override
        public void set(double width, double height) {
            this.present = true;
            this.width = width;
            this.height = height;
        }

        @Override
        public void set(Size otherSize) {
            this.present = true;
            this.width = otherSize.width();
            this.height = otherSize.height();
        }

        @Override
        public Size asImmutable() {
            return this;
        }

        @Override
        public void setWidth(double width) {
            this.present = true;
            this.width = width;
        }

        @Override
        public void setHeight(double height) {
            this.present = true;
            this.height = height;
        }

    }

    final RealMutableAnchor anchor;
    final RealMutableSize size;
    final Possible<Size> minSize;

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
        this.minSize = simplePossibleWith(minSize);
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
    public Possible<Size> minSize() {
        return this.minSize;
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
