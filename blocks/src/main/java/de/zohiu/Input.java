package de.zohiu;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;

import org.jline.utils.InfoCmp.Capability;


public final class Input {
    private final Terminal terminal;
    private final ExecutorService executor;

    AtomicBoolean running;

    public Input() {
        try {
            terminal = TerminalBuilder.builder()
                    .name("Blocks")
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        terminal.enterRawMode();

        running = new AtomicBoolean(true);
        executor = Executors.newSingleThreadExecutor();
        start();
    }

    private static final int LEFT_ARROW = 1000;
    private static final int RIGHT_ARROW = 1001;
    private static final int UP_ARROW = 1002;
    private static final int DOWN_ARROW = 1003;

    public void start() {
        executor.submit(() -> {
            BindingReader reader = new BindingReader(terminal.reader());

            KeyMap<Integer> keyMap = new KeyMap<>();
            keyMap.bind((int)'a', "a");
            keyMap.bind((int)'d', "d");
            keyMap.bind((int)'w', "w");
            keyMap.bind((int)'s', "s");
            keyMap.bind((int)' ', " ");
            keyMap.bind((int)'f', "f");
            keyMap.bind((int)'.', ".");

            keyMap.setNomatch(null); // fallback to raw char

            while (running.get()) {
                Integer key = reader.readBinding(keyMap);

                boolean updateNeeded = true;

                switch (key) {
                    case (int) 'a' -> Main.game.gameState.currentBlock.moveLeft();
                    case (int) 'd' -> Main.game.gameState.currentBlock.moveRight();
                    case (int) '.' -> Main.game.gameState.currentBlock.rotate();
                    case (int) ' ' -> Main.game.gameState.currentBlock.instaDrop();
                    case (int) 's' -> {} // quick drop
                    case (int) 'w' -> Main.game.gameState.holdBlock();
                    default -> updateNeeded = false;
                }

                if (updateNeeded) {
                    Main.renderer.update(Main.game.gameState);
                }
            }
        });
    }

    public void stop() {
        running.set(false);
    }
}
