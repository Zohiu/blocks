package de.zohiu;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Input {
    private Runnable update;
    private Runnable moveLeft;
    private Runnable moveRight;
    private Runnable rotate;
    private Runnable quickDrop;
    private Runnable instaDrop;

    private final NonBlockingReader reader;
    private final ExecutorService executor;

    public void setUpdate(Runnable update) { this.update = update; }
    public void setMoveLeft(Runnable moveLeft) { this.moveLeft = moveLeft; }
    public void setMoveRight(Runnable moveRight) { this.moveRight = moveRight; }
    public void setRotate(Runnable rotate) { this.rotate = rotate; }
    public void setQuickDrop(Runnable quickDrop) { this.quickDrop = quickDrop; }
    public void setInstaDrop(Runnable instaDrop) { this.instaDrop = instaDrop; }

    AtomicBoolean running;

    public Input() {
        Terminal terminal;
        try {
            terminal = TerminalBuilder.builder()
                    .name("Blocks")
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        terminal.enterRawMode();

        running = new AtomicBoolean(true);
        reader = terminal.reader();
        executor = Executors.newSingleThreadExecutor();
    }

    public void start() {
        executor.submit(() -> {
            try {
                // Continuously read input
                while (running.get()) {
                    int c = reader.read(100);
                    if (c != -1) {
                        boolean updateNeeded = true;

                        if (c == 'a') moveLeft.run();
                        else if (c == 'd') moveRight.run();
                        else if (c == 'w') rotate.run();
                        else if (c == ' ') instaDrop.run();
                        else updateNeeded = false;

                        if (updateNeeded) update.run();
                    }
                }
            } catch (IOException ignored) { }
        });
    }

    public void stop() {
        running.set(false);
    }
}
