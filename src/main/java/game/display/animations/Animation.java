package game.display.animations;

import game.display.Display;

public abstract class Animation implements Runnable {

    private float fps = 10f;

    public void setFps(float fps) {
        if (fps > 0) {
            this.fps = fps;
        }
    }

    public abstract Frame generateFrame();

    public abstract boolean isPlaying();

    @Override
    public final void run() {
        while (isPlaying()) {
            Frame frame = generateFrame();
            frame.draw(Display.getRootMenu());
            try {
                Thread.sleep(Math.round((1f/fps) * 1000));
            } catch (InterruptedException e) {
                frame.clear();
                break;
            }
            frame.clear();
            if (Thread.interrupted())
                break;
        }
    }

}
