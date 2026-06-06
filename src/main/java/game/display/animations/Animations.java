package game.display.animations;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class Animations {

    private static Queue<Animation> animationQueue = new LinkedList<>();

    private static ExecutorService threadPool = Executors.newCachedThreadPool();

    private static List<Future<?>> runningAnimations = new ArrayList<>();

    public static void run(Animation animation){
        runningAnimations.add(threadPool.submit(animation));
    }

    public static void stop(){
        runningAnimations.forEach((an) -> an.cancel(true));
        runningAnimations.clear();
    }

    public static void enqueue(Animation animation){
        animationQueue.add(animation);
    }

    public static void play(){
        animationQueue.forEach(Animations::run);
        animationQueue.clear();
    }

}
