package de.zohiu;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class Main {
    public static Game game = new Game();
    public static Renderer renderer = new Renderer();

    private static Input input = new Input();
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledFuture<?> currentTask;
    private static long gameStart = System.nanoTime();

    public static void main(String[] args) {
        game.setGameOverCallback(() -> {
            input.stop();
            currentTask.cancel(true); 
            renderer.update(game.gameState);
            System.exit(0);
        });

        scheduleTask(250);
    }

    private static void scheduleTask(long interval) {
        currentTask = scheduler.scheduleAtFixedRate(Main::tick, 0, interval, TimeUnit.MILLISECONDS);
    }

    public static void changeInterval(long newIntervalMillis) {
        if (currentTask != null && !currentTask.isCancelled()) {
            currentTask.cancel(false);
        }
        scheduleTask(newIntervalMillis);
    }

    public static void tick() {
        game.update();
        renderer.update(game.gameState);
    }
}