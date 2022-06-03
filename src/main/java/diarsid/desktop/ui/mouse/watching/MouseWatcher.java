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

import diarsid.support.concurrency.stateful.workers.AbstractStatefulPausableDestroyableWorker;
import diarsid.support.concurrency.threads.ConstantThreadsNaming;
import diarsid.support.concurrency.threads.NamedThreadFactory;
import diarsid.support.concurrency.threads.ThreadsNaming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import static diarsid.support.concurrency.threads.ThreadsUtil.shutdownAndWait;

public class MouseWatcher extends AbstractStatefulPausableDestroyableWorker {

    private static final Logger log = LoggerFactory.getLogger(MouseWatcher.class);

    public static final String THREAD_NAME_PREFIX = MouseWatcher.class.getCanonicalName() + ".";

    private final Runnable watcherProbe;
    private final Runnable probeReceiving;
    private final long tick;
    private final Object monitor;
    private final List<Watch> watches;
    private final List<WatchThread> watchThreads;
    private final List<WatchThread> watchThreadsToAwake;
    private final List<WatchThread> watchThreadsAwakened;
    private final Object allWatchesMonitor;
    private final AtomicBoolean isWorking;
    private final NamedThreadFactory watcherThreadFactory;

    private ScheduledExecutorService watcherThread;
    private Thread receiverThread;
    private ScheduledFuture watcherThreadSchedule;
    private Point pointReceived;

    public MouseWatcher(long tick, Watch... watches) {
        this(tick, asList(watches));
    }

    public MouseWatcher(long tick, List<Watch> watches) {
        super();
        this.tick = tick;
        this.monitor = new Object();
        this.isWorking = new AtomicBoolean(false);
        this.watches = new ArrayList<>(watches);
        this.watchThreads = new ArrayList<>();
        this.watchThreadsToAwake = new ArrayList<>();
        this.watchThreadsAwakened = new ArrayList<>();
        this.allWatchesMonitor = new Object();

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
            synchronized ( this.monitor ) {
                if ( ! point.equals(this.pointReceived) ) {
                    this.pointReceived = point;
                    this.monitor.notify();
                }
            }
        };

        this.probeReceiving = () -> {
            while ( this.isWorking.get() ) {
                try {
                    Point point;

                    synchronized ( this.monitor ) {
                        this.monitor.wait();
                        point = this.pointReceived;
                    }

                    if ( ! this.isWorking.get() ) {
                        break;
                    }

                    if ( point == null ) {
                        continue;
                    }

                    WatchThread watchThread;

                    synchronized ( this.allWatchesMonitor) {
                        for (int i = 0; i < this.watchThreads.size(); i++ ) {
                            watchThread = this.watchThreads.get(i);
                            if ( watchThread.watch.predicate.test(point) ) {
                                this.watchThreadsToAwake.add(watchThread);
                            }
                        }
                    }

                    for (int i = 0; i < this.watchThreadsToAwake.size(); i++ ) {
                        watchThread = this.watchThreadsToAwake.get(i);
                        if ( this.watchThreadsAwakened.contains(watchThread) ) {
                            continue;
                        }
                        synchronized ( watchThread.monitor ) {
                            watchThread.point = point;
                            watchThread.predicateValue = true;
                            watchThread.monitor.notify();
                        }
                    }

                    for (int i = 0; i < this.watchThreadsAwakened.size(); i++ ) {
                        watchThread = this.watchThreadsAwakened.get(i);
                        if ( ! this.watchThreadsToAwake.contains(watchThread) ) {
                            synchronized ( watchThread.monitor ) {
                                watchThread.point = point;
                                watchThread.predicateValue = false;
                                watchThread.monitor.notify();
                            }
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

        synchronized ( this.allWatchesMonitor) {
            for ( var reaction : this.watches) {
                this.createAndStartReactionThreadFor(reaction);
            }
        }

        this.watcherThread = Executors.newSingleThreadScheduledExecutor(this.watcherThreadFactory);
        this.watcherThreadSchedule = this.watcherThread.scheduleAtFixedRate(this.watcherProbe, tick, tick, MILLISECONDS);

        this.receiverThread = new Thread(this.probeReceiving, THREAD_NAME_PREFIX + "receiver");
        this.receiverThread.start();

        return true;
    }

    private void createAndStartReactionThreadFor(Watch watch) {
        WatchThread watchThread = new WatchThread(watch, this.isWorking);
        this.watchThreads.add(watchThread);
        watchThread.start();
    }

    @Override
    protected boolean doSynchronizedPauseWork() {
        this.isWorking.set(false);

        synchronized ( this.monitor ) {
            this.monitor.notify();
        }

        this.watchThreadsAwakened.clear();

        synchronized ( this.allWatchesMonitor) {
            for ( WatchThread watchThread : this.watchThreads) {
                watchThread.signalToStop();
            }
            this.watchThreads.clear();
        }

        this.watcherThreadSchedule.cancel(false);
        shutdownAndWait(this.watcherThread);

        return true;
    }

    @Override
    protected boolean doSynchronizedDestroy() {
        shutdownAndWait(this.watcherThread);
        return true;
    }

    public boolean add(Watch watch) {
        synchronized ( this.allWatchesMonitor) {
            if ( this.watches.contains(watch) ) {
                return false;
            }

            this.watches.add(watch);
            this.createAndStartReactionThreadFor(watch);
        }
        return false;
    }

    public boolean remove(String name) {
        WatchThread watchThreadToRemove = null;
        synchronized ( this.allWatchesMonitor) {
            for ( var reactionThread : this.watchThreads) {
                if ( reactionThread.watch.name.equals(name) ) {
                    watchThreadToRemove = reactionThread;
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
    }
}
