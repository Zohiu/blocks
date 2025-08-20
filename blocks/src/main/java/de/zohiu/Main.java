package de.zohiu;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class Main {
    private static Game game = new Game();
    private static Renderer renderer = new Renderer();
    private static Input input = new Input();

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledFuture<?> currentTask;
    static long gameStart = System.nanoTime();

    public static void main(String[] args) {
        input.setMoveLeft(() -> { game.gameState.currentBlock.moveLeft(); });
        input.setMoveRight(() -> { game.gameState.currentBlock.moveRight(); });
        input.setRotate(() -> { game.gameState.currentBlock.rotate(); });
        input.setQuickDrop(() -> { });
        input.setInstaDrop(() -> { game.gameState.currentBlock.instaDrop(); });

        input.setUpdate(() -> { renderer.update(game.gameState); });
        input.start();

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
        System.out.print("tick");
        game.update();
        renderer.update(game.gameState);
    }
}