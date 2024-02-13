package diarsid.desktop.ui.geometry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class RealMutableAnchor implements MutableAnchor {

    private final List<MutablePoint.Listener> listeners;
    private final RealPoint prevPoint;
    protected boolean present;
    protected double x;
    protected double y;

    RealMutableAnchor() {
        this.present = false;
        this.listeners = new ArrayList<>();
        this.prevPoint = new RealPoint();
    }

    RealMutableAnchor(Anchor anchor) {
        this.present = true;
        this.listeners = new ArrayList<>();
        this.prevPoint = new RealPoint();
        this.x = anchor.x();
        this.y = anchor.y();
    }

    RealMutableAnchor(double x, double y) {
        this.present = true;
        this.listeners = new ArrayList<>();
        this.prevPoint = new RealPoint();
        this.x = x;
        this.y = y;
    }

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

        this.prevPoint.x = this.x;
        this.prevPoint.y = this.y;

        this.x = x;
        this.y = y;

        this.afterChange();
    }

    @Override
    public Anchor asImmutable() {
        return this;
    }

    @Override
    public void setX(double x) {
        this.present = true;
        this.prevPoint.x = this.x;
        this.x = x;
        this.afterChange();
    }

    @Override
    public void setY(double y) {
        this.present = true;
        this.prevPoint.y = this.y;
        this.y = y;
        this.afterChange();
    }

    private void afterChange() {
        this.listeners.forEach(listener -> listener.onChange(this.prevPoint, this));
    }

    @Override
    public void addListener(Listener listener) {
        this.listeners.add(listener);
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
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RealMutableAnchor other = (RealMutableAnchor) obj;
        if (!Objects.equals(this.x, other.x)) {
            return false;
        }
        if (!Objects.equals(this.y, other.y)) {
            return false;
        }
        return true;
    }

}
