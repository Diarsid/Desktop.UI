package diarsid.desktop.ui.mouse.watching;

import java.awt.Point;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WatchThread extends Thread {

    private static final Logger log = LoggerFactory.getLogger(WatchThread.class);

    public static final String THREAD_NAME_PREFIX =
            MouseWatcher.class.getCanonicalName() + "." +
            Watch.class.getSimpleName() + ".";

    private final AtomicBoolean isWatcherWorking;
    final Watch watch;
    final Object monitor;
    boolean isWorking;
    Point point;
    boolean predicateValue;

    public WatchThread(Watch watch, AtomicBoolean isWatcherWorking) {
        super(THREAD_NAME_PREFIX + watch.name);
        this.watch = watch;
        this.monitor = new Object();
        this.isWorking = false;
        this.isWatcherWorking = isWatcherWorking;
    }

    @Override
    public void run() {
        log.info("{} '{}' starting", WatchThread.class.getSimpleName(), this.watch.name);
        this.isWorking = true;
        boolean work = this.isWorking && this.isWatcherWorking.get();

        Point currentPoint;
        boolean currentPredicateValue;
        while ( work ) {
            try {
                synchronized ( this.monitor ) {
                    this.monitor.wait();
                    currentPoint = this.point;
                    currentPredicateValue = this.predicateValue;
                }
                work = this.isWorking && this.isWatcherWorking.get();
                if ( ! work ) {
                    break;
                }

                if ( currentPoint != null ) {
                    this.watch.actionOnPredicateChange.accept(currentPoint, currentPredicateValue);
                }

            }
            catch (InterruptedException e) {
                log.warn(e.getMessage(), e);
            }
            catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            finally {
                work = this.isWorking && this.isWatcherWorking.get();
                currentPoint = null;
            }
        }

        log.info("{} '{}' stopped", WatchThread.class.getSimpleName(), this.watch.name);
    }

    void signalToStop() {
        log.info("{} '{}' stopping", WatchThread.class.getSimpleName(), this.watch.name);
        this.isWorking = false;
        synchronized ( this.monitor ) {
            this.point = null;
            this.monitor.notify();
        }
    }
}
