package diarsid.desktop.ui.mouse.watching;

import java.awt.MouseInfo;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import diarsid.support.concurrency.stateful.workers.AbstractStatefulPausableDestroyableWorker;
import diarsid.support.concurrency.threads.ConstantThreadsNaming;
import diarsid.support.concurrency.threads.NamedThreadFactory;
import diarsid.support.concurrency.threads.ThreadsNaming;

import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import static diarsid.support.concurrency.threads.ThreadsUtil.shutdownAndWait;

public class MouseWatcher extends AbstractStatefulPausableDestroyableWorker {

    private static final Logger log = LoggerFactory.getLogger(MouseWatcher.class);

    public static final String THREAD_NAME_PREFIX = MouseWatcher.class.getCanonicalName() + ".";

    private final Runnable watcherProbe;
    private final Runnable probeReceiving;
    private final long tick;
    private final Lock probe;
    private final Lock allWatches;
    private final Condition probeCondition;
    private final List<Watch> watches;
    private final List<WatchThread> watchThreads;
    private final List<WatchThread> watchThreadsToAwake;
    private final List<WatchThread> watchThreadsAwakened;
    private final AtomicBoolean isWorking;
    private final NamedThreadFactory watcherThreadFactory;

    private ScheduledExecutorService watcherThread;
    private Thread receiverThread;
    private ScheduledFuture<?> watcherThreadSchedule;
    private volatile Point pointReceived;
    private volatile boolean probeConditionSignalled;

    public MouseWatcher(long tick, Watch... watches) {
        this(tick, asList(watches));
    }

    public MouseWatcher(long tick, List<Watch> watches) {
        super();
        this.tick = tick;
        this.probe = new ReentrantLock(true);
        this.allWatches = new ReentrantLock();
        this.probeCondition = this.probe.newCondition();
        this.isWorking = new AtomicBoolean(false);
        this.watches = new ArrayList<>(watches);
        this.watchThreads = new ArrayList<>();
        this.watchThreadsToAwake = new ArrayList<>();
        this.watchThreadsAwakened = new ArrayList<>();

        this.probeConditionSignalled = false;

        Set<String> names = new HashSet<>();
        List<String> duplicates = new ArrayList<>();
        String name;

        for ( var reaction : watches) {
            name = reaction.name;
            if ( names.contains(name) ) {
                duplicates.add(name);
            }
            else {
                names.add(name);
            }
        }

        if ( ! duplicates.isEmpty()) {
            throw new IllegalArgumentException("Reactions have duplicated elements: " + duplicates);
        }


        ThreadsNaming naming = new ConstantThreadsNaming(THREAD_NAME_PREFIX + "watcher");
        this.watcherThreadFactory = new NamedThreadFactory(naming);

        this.watcherProbe = () -> {
            Point point = MouseInfo.getPointerInfo().getLocation();

            this.probe.lock();
            try {
                if ( ! point.equals(this.pointReceived) ) {
                    this.pointReceived = point;
                    this.probeConditionSignalled = true;
                    this.probeCondition.signal();
                }
            }
            finally {
                this.probe.unlock();
            }
        };

        this.probeReceiving = () -> {
            while ( this.isWorking.get() ) {
                try {
                    Point point;

                    this.probe.lock();
                    try {
                        do {
                            this.probeCondition.await();
                        }
                        while ( ! this.probeConditionSignalled );
                        this.probeConditionSignalled = false;
                        point = this.pointReceived;
                    }
                    finally {
                        this.probe.unlock();
                    }

                    if ( ! this.isWorking.get() ) {
                        break;
                    }

                    if ( point == null ) {
                        continue;
                    }

                    WatchThread watchThread;

                    this.allWatches.lock();
                    try {
                        for (int i = 0; i < this.watchThreads.size(); i++ ) {
                            watchThread = this.watchThreads.get(i);
                            if ( watchThread.watch.predicate.test(point) ) {
                                this.watchThreadsToAwake.add(watchThread);
                            }
                        }
                    }
                    finally {
                        this.allWatches.unlock();
                    }

                    for (int i = 0; i < this.watchThreadsToAwake.size(); i++ ) {
                        watchThread = this.watchThreadsToAwake.get(i);
                        if ( this.watchThreadsAwakened.contains(watchThread) ) {
                            continue;
                        }

                        watchThread.awakeWhenPredicateIsTrueFor(point);
                    }

                    for (int i = 0; i < this.watchThreadsAwakened.size(); i++ ) {
                        watchThread = this.watchThreadsAwakened.get(i);
                        if ( ! this.watchThreadsToAwake.contains(watchThread) ) {
                            watchThread.awakeWhenPredicateIsFalseFor(point);
                        }
                    }
                }
                catch (InterruptedException e) {
                    log.warn(e.getMessage(), e);
                }
                finally {
                    this.watchThreadsAwakened.clear();
                    this.watchThreadsAwakened.addAll(this.watchThreadsToAwake);
                    this.watchThreadsToAwake.clear();
                }
            }

            log.info("stopped");
        };
    }

    @Override
    protected boolean doSynchronizedStartWork() {
        this.isWorking.set(true);

        this.allWatches.lock();
        try {
            for ( var watch : this.watches ) {
                this.createAndStartThreadFor(watch);
            }
        }
        finally {
            this.allWatches.unlock();
        }

        this.watcherThread = Executors.newSingleThreadScheduledExecutor(this.watcherThreadFactory);
        this.watcherThreadSchedule = this.watcherThread.scheduleAtFixedRate(this.watcherProbe, tick, tick, MILLISECONDS);

        this.receiverThread = new Thread(this.probeReceiving, THREAD_NAME_PREFIX + "receiver");
        this.receiverThread.start();

        return true;
    }

    private void createAndStartThreadFor(Watch watch) {
        WatchThread watchThread = new WatchThread(watch, this.isWorking);
        this.watchThreads.add(watchThread);
        watchThread.start();
    }

    @Override
    protected boolean doSynchronizedPauseWork() {
        this.isWorking.set(false);

        this.probe.lock();
        try {
            this.probeConditionSignalled = true;
            this.probeCondition.signal();
        }
        finally {
            this.probe.unlock();
        }

        this.watchThreadsAwakened.clear();

        this.allWatches.lock();
        try {
            for ( WatchThread watchThread : this.watchThreads) {
                watchThread.signalToStop();
            }
            this.watchThreads.clear();
        }
        finally {
            this.allWatches.unlock();
        }

        this.watcherThreadSchedule.cancel(false);
        shutdownAndWait(this.watcherThread);

        return true;
    }

    @Override
    protected boolean doSynchronizedDestroy() {
        this.doSynchronizedPauseWork();
        return true;
    }

    public boolean add(WatchBearer watchBearer) {
        return this.add(watchBearer.watch());
    }

    public boolean add(Watch watch) {
        return super.doSynchronizedReturnChange(() -> {this.allWatches.lock();
            try {
                if ( this.watches.contains(watch) ) {
                    return false;
                }

                this.watches.add(watch);
                if ( this.isWorking() ) {
                    this.createAndStartThreadFor(watch);
                }
            }
            finally {
                this.allWatches.unlock();
            }

            return true;
        });
    }

    public boolean remove(String name) {
        return super.doSynchronizedReturnChange(() -> {
            WatchThread watchThreadToRemove = null;

            this.allWatches.lock();
            try {
                for ( var watchThread : this.watchThreads) {
                    if ( watchThread.watch.name.equals(name) ) {
                        watchThreadToRemove = watchThread;
                        break;
                    }
                }

                if ( watchThreadToRemove != null ) {
                    watchThreadToRemove.signalToStop();
                    this.watches.remove(watchThreadToRemove.watch);
                    this.watchThreads.remove(watchThreadToRemove);
                    return true;
                }
                else {
                    return false;
                }
            }
            finally {
                this.allWatches.unlock();
            }
        });
    }
}
