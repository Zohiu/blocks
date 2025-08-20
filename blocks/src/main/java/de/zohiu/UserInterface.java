package de.zohiu;

import java.util.List;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.input.UserAction;

import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;

class UserInterface extends GameApplication {
    public String[] colors = {
        "\u001B[32m", "\u001B[32m", "\u001B[33m", "\u001B[34m", "\u001B[35m", "\u001B[36m"
    };

    public UserInterface() {
        launch(new String[] {});
        canvas = new Canvas(600, 600);
        FXGL.getGameScene().addUINode(canvas);
    }

    public Canvas canvas;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(600);
        settings.setHeight(600);
        settings.setTitle("Blocks");
        settings.setVersion("0.1");
    }

    @Override
    protected void initInput() {
        com.almasb.fxgl.input.Input input = FXGL.getInput();

        input.addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {
                // Nothing yet.
            }
        }, KeyCode.D);
    }
    

    public void update(Game.State gameState) {
        var g = canvas.getGraphicsContext2D();

        String output = "\033[H\033[2J ";
        for (List<Integer> row : gameState.getVisualBoard()) {
            for (int value : row) {
                if (value > 0) {
                    output += colors[value - 1] + " ■ " + "\u001B[0m";
                } else {
                    output += " ■ ";
                }
            }
            output += "\n ";
        }
        System.out.print(output);
        System.out.flush();
    }
}