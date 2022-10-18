package diarsid.desktop.ui.mouse.watching;

import static java.lang.Thread.sleep;

public class MauseWatcherTest {

    public static void main(String[] args) throws Exception {
        Watch x = new Watch(
                "x", 
                point -> point.getLocation().getX() == 0, 
                (point, watchActive) -> {
                    if ( watchActive ) {
                        System.out.println("x = 0");
                    }
                    else {
                        System.out.println("x != 0");
                    }
                });
        
        Watch y = new Watch(
                "y", 
                point -> point.getLocation().getY() == 0,
                (point, watchActive) -> {
                    if ( watchActive ) {
                        System.out.println("y = 0");
                    }
                    else {
                        System.out.println("y != 0");
                    }
                });
        
        Watch a = new Watch(
                "A", 
                point -> point.getLocation().getX() != 0 && point.getLocation().getY() != 0,
                (point, watchActive) -> {
                    if ( watchActive ) {
                        System.out.println("A");
                    }
                    else {
                        System.out.println("_");
                    }
                });

        MouseWatcher watcher = new MouseWatcher(10, x, y, a);
        watcher.startWork();

//        sleep(6_000);
//        System.out.println("remove y");
//        watcher.remove("y");
//        System.out.println("removed y");
//        sleep(6_000);
//        System.out.println("add y ");
//        watcher.add(y);
//        System.out.println("added y");
//        sleep(6_000);
//        System.out.println("destroy");
//        watcher.destroy();
//        System.out.println("destroyed");

    }
}
