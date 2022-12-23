package diarsid.desktop.ui.mouse.watching;

import java.awt.Point;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class WatchThread extends Thread {

    private static final Logger log = LoggerFactory.getLogger(WatchThread.class);

    public static final String THREAD_NAME_PREFIX =
            MouseWatcher.class.getCanonicalName() + "." +
            Watch.class.getSimpleName() + ".";

    private final AtomicBoolean isWatcherWorking;
    final Watch watch;
    final Lock watching;
    final Condition watchingCondition;
    volatile int watchingConditionCount;
    volatile boolean isWorking;
    volatile Point point;
    volatile boolean predicateValue;

    WatchThread(Watch watch, AtomicBoolean isWatcherWorking) {
        super(THREAD_NAME_PREFIX + watch.name);
        this.watch = watch;
        this.watching = new ReentrantLock(true);
        this.watchingCondition = this.watching.newCondition();
        this.isWorking = false;
        this.isWatcherWorking = isWatcherWorking;
        this.watchingConditionCount = 0;
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
                this.watching.lock();
                try {
                    do {
                        this.watchingCondition.await();
                    }
                    while ( this.watchingConditionCount == 0 );
                    this.watchingConditionCount = 0;
                    currentPoint = this.point;
                    currentPredicateValue = this.predicateValue;
                }
                finally {
                    this.watching.unlock();
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

        this.watching.lock();
        try {
            this.point = null;
            this.watchingConditionCount++;
            this.watchingCondition.signal();
        }
        finally {
            this.watching.unlock();
        }
    }
}
