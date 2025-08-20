package de.zohiu;

public class GameLoop extends Thread {
    int TARGET_FPS = 30;
    Game game;
    UserInterface renderer;
    
    public GameLoop(Game game, UserInterface renderer) {
        this.game = game;
        this.renderer = renderer;
    }

    public void run() {
        double deltaTime = 0.0;

        while (true) {
            long startTime = System.nanoTime();
            Game.State gameState = game.update(deltaTime);
            renderer.update(gameState);
            long endTime = System.nanoTime();

            try {
                int deltaMS = (int) ((endTime - startTime) / 1000000);
                int delay = Math.max(0, (int) (1000 / TARGET_FPS) - deltaMS);
                sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            deltaTime = (double) ((System.nanoTime() - startTime) / 1000000000.0);
        }
    }
}
