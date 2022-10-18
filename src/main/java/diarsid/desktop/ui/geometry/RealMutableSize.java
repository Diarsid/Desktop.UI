package diarsid.desktop.ui.geometry;

import java.util.ArrayList;
import java.util.List;

class RealMutableSize implements MutableSize {

    static class SizeImpl implements Size {

        double width;
        double height;

        @Override
        public double width() {
            return this.width;
        }

        @Override
        public double height() {
            return this.height;
        }
    };

    boolean present;
    private final SizeImpl prevSize;
    private final List<MutableSize.Listener> listeners;
    protected double width;
    protected double height;

    RealMutableSize() {
        this.present = false;
        this.prevSize = new SizeImpl();
        this.listeners = new ArrayList<>();
    }

    RealMutableSize(Size size) {
        this.present = true;
        this.prevSize = new SizeImpl();
        this.listeners = new ArrayList<>();
        this.width = size.width();
        this.height = size.height();
    }

    RealMutableSize(double width, double height) {
        this.present = true;
        this.prevSize = new SizeImpl();
        this.listeners = new ArrayList<>();
        this.width = width;
        this.height = height;
    }

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
        return !this.present;
    }

    @Override
    public boolean isValueAbsent() {
        return this.present;
    }

    @Override
    public void set(double width, double height) {
        this.present = true;

        this.prevSize.width = this.width;
        this.prevSize.height = this.height;

        this.width = width;
        this.height = height;

        this.afterChange();
    }

    private void afterChange() {
        this.listeners.forEach(listener -> listener.onChange(this.prevSize, this));
    }

    @Override
    public void set(Size otherSize) {
        this.present = true;

        this.prevSize.width = this.width;
        this.prevSize.height = this.height;

        this.width = otherSize.width();
        this.height = otherSize.height();

        this.afterChange();
    }

    @Override
    public void setWidth(double width) {
        this.present = true;
        this.prevSize.width = this.width;
        this.width = width;
        this.afterChange();
    }

    @Override
    public void setHeight(double height) {
        this.present = true;
        this.prevSize.height = this.height;
        this.height = height;
        this.afterChange();
    }

    @Override
    public Size asImmutable() {
        return this;
    }

    @Override
    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

}
