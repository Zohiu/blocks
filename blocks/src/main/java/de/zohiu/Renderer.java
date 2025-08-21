package de.zohiu;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


class Renderer {
    enum Color {
        //Color end string, color reset
        RESET("\033[0m"),

        // Dark BG → Bright contrasting FG
        BLACK   ("\033[0;97m\033[40m"),   // Bright White on Black
        RED     ("\033[0;97m\033[41m"),   // Bright White on Red
        GREEN   ("\033[0;97m\033[42m"),   // Bright White on Green
        YELLOW  ("\033[0;30m\033[43m"),   // Black on Yellow
        BLUE    ("\033[0;97m\033[44m"),   // Bright White on Blue
        MAGENTA ("\033[0;97m\033[45m"),   // Bright White on Magenta
        CYAN    ("\033[0;30m\033[46m"),   // Black on Cyan
        WHITE   ("\033[0;30m\033[47m"),   // Black on White

        // Bright BG → Dark contrasting FG
        BLACK_BRIGHT   ("\033[0;30m\033[0;100m"),  // Black on Bright Black (Gray BG)
        RED_BRIGHT     ("\033[0;30m\033[0;101m"),  // Black on Bright Red
        GREEN_BRIGHT   ("\033[0;30m\033[0;102m"),  // Black on Bright Green
        YELLOW_BRIGHT  ("\033[0;30m\033[0;103m"),  // Black on Bright Yellow
        BLUE_BRIGHT    ("\033[0;30m\033[0;104m"),  // Black on Bright Blue
        MAGENTA_BRIGHT ("\033[0;30m\033[0;105m"),  // Black on Bright Magenta
        CYAN_BRIGHT    ("\033[0;30m\033[0;106m");  // Black on Bright Cyan


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
        Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE, Color.MAGENTA, Color.CYAN,
        Color.RED_BRIGHT, Color.GREEN_BRIGHT, Color.YELLOW_BRIGHT, Color.BLUE_BRIGHT, Color.MAGENTA_BRIGHT, Color.CYAN_BRIGHT,
    };

    public void update(Game.State gameState) {
        // Sidebar
        Queue<String> sidebarLines = new LinkedList<>();
        sidebarLines.add("Holding:");
        if (gameState.holdingBlock != null) {
            for (int[] row : gameState.holdingBlock.getShape()) {
                StringBuilder line = new StringBuilder();
                for (int value : row) {
                    if (value > 0) line.append(colors[gameState.holdingBlock.color - 1]).append("  ").append("\u001B[0m");
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