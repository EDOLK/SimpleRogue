package game.gameobjects.terrains.projectiles;

import java.util.Iterator;

public class LimitedIterator<T> implements Iterator<T> {

    private final Iterator<T> iterator;
    private int limit;

    public LimitedIterator(Iterator<T> iterator, int limit) {
        this.iterator = iterator;
        this.limit = limit;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext() && limit > 0;
    }

    @Override
    public T next() {
        T next = iterator.next();
        limit--;
        return next;
    }

}
