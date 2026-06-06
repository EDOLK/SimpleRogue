package game.display.animations;

public class ArrayAnimation extends Animation {

    private int index;
    private final Frame[] array;

    public ArrayAnimation(Frame[] array) {
        this.array = array;
    }

    @Override
    public final Frame generateFrame() {
        return array[index++];
    }

    @Override
    public final boolean isPlaying() {
        return index < array.length;
    }

    
}
