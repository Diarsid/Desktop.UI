package diarsid.desktop.ui.geometry;

import diarsid.support.configuration.Configuration;

class RealScreen implements Screen {
    
    private final Rectangle full;
    private final Rectangle inset;

    RealScreen(Configuration configuration, double width, double height) {
        double insetValue = configuration.asInt("ui.screen.insets");
        this.full = new RealMutableRectangle(0, 0, width, height);
        this.inset = new RealMutableRectangle(
                0 + insetValue, 
                0 + insetValue, 
                width - (insetValue * 2), 
                height - (insetValue * 2));
    }

    RealScreen(int insetValue, double width, double height) {
        this.full = new RealMutableRectangle(0, 0, width, height);
        this.inset = new RealMutableRectangle(
                0 + insetValue, 
                0 + insetValue, 
                width - (insetValue * 2), 
                height - (insetValue * 2));
    }

    RealScreen(
            int insetTop, int insetRight, int insetBottom, int insetLeft, 
            double width, double height) {
        this.full = new RealMutableRectangle(0, 0, width, height);
        this.inset = new RealMutableRectangle(
                insetLeft, 
                insetTop, 
                width - insetLeft - insetRight, 
                height - insetTop - insetBottom);
    }

    @Override
    public boolean fit(MutableRectangle fitted) {
        if ( this.inset.contains(fitted) ) {
            return false;
        }
        
        if ( fitted.minSize().isPresent() ) {
            this.fitRespectingMinSize(fitted);
        } else {
            this.fitAnyway(fitted);
        }
        return true;
    }
    
    private void fitRespectingMinSize(MutableRectangle fitted) {
        fitted.toMinSizeAbsolute();
        Size fittedSize = fitted.size();
        Size thisSize = this.size();
        Anchor insetAnchor = this.inset.anchor();
        
        double anchorX = (thisSize.width() - fittedSize.width()) / 2;
        double anchorY = (thisSize.height() - fittedSize.height()) / 2;
        
        anchorX = anchorX < insetAnchor.x() ? insetAnchor.x() : anchorX;
        anchorY = anchorY < insetAnchor.y() ? insetAnchor.y() : anchorY;
        
        fitted.anchor().set(anchorX, anchorY);
    }

    private void notFinished(MutableRectangle fitted) {
        if ( this.inset.contains(fitted.anchor()) ) {
            
        } else {
            
        }
        
        
        
        MutableSize fittedSize = fitted.size();
        Size insetSize = this.inset.size();
        
        if ( fitted.isSmallerThanMinSize() ) {
            fitted.toMinSizeAbsolute();
        } else if ( fitted.isBiggerThanMinSize() ) {
            
        } else {
            
        }
        
        if ( fittedSize.isOverallBiggerThan(insetSize) ) {
            fitted.toMinSizeAbsolute();
            fitted.anchor().set(this.inset.anchor());
        } else if ( fittedSize.isOverallSmallerThan(insetSize) ) {
            
        } 
    }

    private void fitAnyway(MutableRectangle fitted) {
        MutableSize fittedSize = fitted.size();
        Size insetSize = this.inset.size();

        if( fittedSize.width() > insetSize.width() ) {
            fittedSize.setWidth(insetSize.width());
        }
        if ( fittedSize.height() > insetSize.height() ) {
            fittedSize.setHeight(insetSize.height());
        }
        
        double xDiff = insetSize.width() - fittedSize.width();
        double yDiff = insetSize.height()- fittedSize.height();
        
        double fitterAnchorX = this.inset.anchor().x() + (xDiff / 2);
        double fitterAnchorY = this.inset.anchor().y() + (yDiff / 2);
        
        fitted.anchor().set(fitterAnchorX, fitterAnchorY);
    }

    @Override
    public Anchor anchor() {
        return this.full.anchor();
    }

    @Override
    public Size size() {
        return this.full.size();
    }
    
}
