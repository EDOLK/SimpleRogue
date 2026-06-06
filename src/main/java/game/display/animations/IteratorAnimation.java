package game.display.animations;

import java.util.Iterator;

public class IteratorAnimation extends Animation {

    private Iterator<Frame> iterator;

    public IteratorAnimation(Iterator<Frame> iterator) {
        this.iterator = iterator;
    }

    public IteratorAnimation(Iterable<Frame> frameIterable) {
        this(frameIterable.iterator());
    }

    @Override
    public Frame generateFrame() {
        return iterator.next();
    }

    @Override
    public boolean isPlaying() {
        return iterator.hasNext();
    }
}
