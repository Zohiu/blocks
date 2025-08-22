package de.zohiu;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


class Renderer {
    enum Color {
        RESET("\033[0m"),

        CYAN("\033[0;30m\033[48;2;0;255;255m"),       // Cyan
        YELLOW("\033[0;30m\033[48;2;255;255;0m"),       // Yellow
        PURPLE("\033[0;97m\033[48;2;160;0;240m"),       // Purple
        GREEN("\033[0;30m\033[48;2;0;255;0m"),         // Green
        RED("\033[0;97m\033[48;2;255;0;0m"),         // Red
        BLUE("\033[0;97m\033[48;2;0;0;255m"),         // Blue
        ORANGE("\033[0;30m\033[48;2;255;165;0m");       // Orange

        private final String code;

        Color(String code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return code;
        }
    }

    public static Color[] colors = {
        Color.CYAN, Color.BLUE, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.PURPLE, Color.RED,
    };

    public void update(Game.State gameState) {
        // Sidebar
        Queue<String> sidebarLines = new LinkedList<>();
        sidebarLines.add("Score: " + gameState.score);

        sidebarLines.add("Next:");
        sidebarLines.add("");
        if (gameState.nextBlock != null) {
            for (int[] row : gameState.nextBlock.getShape()) {
                StringBuilder line = new StringBuilder();
                for (int value : row) {
                    if (value > 0) line.append(colors[value - 1]).append("  ").append("\u001B[0m");
                    else line.append("  ");
                }
                sidebarLines.add(line.toString());
            }
        }

        sidebarLines.add("Holding:");
        sidebarLines.add("");
        if (gameState.holdingBlock != null) {
            for (int[] row : gameState.holdingBlock.getShape()) {
                StringBuilder line = new StringBuilder();
                for (int value : row) {
                    if (value > 0) line.append(colors[value - 1]).append("  ").append("\u001B[0m");
                    else line.append("  ");
                }
                sidebarLines.add(line.toString());
            }
        }

        StringBuilder output = new StringBuilder("\033[H\033[2J");
        // Game board
        for (List<Integer> row : gameState.getVisualBoard()) {
            output.append("█");
            for (int value : row) {
                if (value > 0) {
                    output.append(colors[value - 1]).append("  ").append("\u001B[0m");
                } else if (value == -1) {
                    output.append("::");  // Ghost blocks have the color value -1.
                } else {
                    output.append("  ");  // Color 0 means no block at this location.
                }
            }
            output.append("█ ");

            if (!sidebarLines.isEmpty()) output.append(sidebarLines.remove());
            output.append("\u001B[0m\n");
        }

        System.out.print(output);
        // System.out.flush();
    }
}