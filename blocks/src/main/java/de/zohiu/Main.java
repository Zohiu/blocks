package de.zohiu;

public class Main {
    static GameLoop update;
    static Game game;
    static UserInterface renderer;

    public static void main(String[] args) {
        game = new Game();
        renderer = new UserInterface();
        update = new GameLoop(game, renderer);
        update.start();
    }
}