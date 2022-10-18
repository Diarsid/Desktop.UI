package diarsid.desktop.ui.mouse.watching;

import java.awt.Point;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import static java.util.UUID.randomUUID;

public class Watch {

    public final String name;
    public final Predicate<Point> predicate;
    final BiConsumer<Point, Boolean> actionOnPredicateChange;

    public Watch(
            Predicate<Point> predicate,
            BiConsumer<Point, Boolean> actionOnPredicateChange) {
        this.name = randomUUID().toString();
        this.predicate = predicate;
        this.actionOnPredicateChange = actionOnPredicateChange;
    }

    public Watch(
            String name,
            Predicate<Point> predicate,
            BiConsumer<Point, Boolean> actionOnPredicateChange) {
        this.name = name;
        this.predicate = predicate;
        this.actionOnPredicateChange = actionOnPredicateChange;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Watch)) return false;
        Watch watch = (Watch) o;
        return name.equals(watch.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
