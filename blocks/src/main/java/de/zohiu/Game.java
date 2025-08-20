package de.zohiu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Game {
    public class State {
        public int boardHeight = 10;
        public int boardWidth = 8; // TODO: Make this do something

        public List<List<Integer>> board;
        Block currentBlock = new Block();

        public State() {
            board = new ArrayList<>();
            for (int i = 0; i < boardHeight; i++) {
                board.add(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0));
            }
        }

        public void placeBlock() {
            board = getVisualBoard();
        }

        public List<List<Integer>> getVisualBoard() {
            List<List<Integer>> newBoard = new ArrayList<>();
            for (List<Integer> row : board) {
                List<Integer> newRow = new ArrayList<>();
                for (Integer column : row) {
                    newRow.add(column);
                }
                newBoard.add(newRow);
            }

            int blockX = currentBlock.location.x;
            int blockY = currentBlock.location.y;

            for (int row = 0; row < currentBlock.shape.length; row++) {
                for (int column = 0; column < currentBlock.shape[row].length; column++) {
                    int blockVal = currentBlock.shape[row][column];
                    newBoard.get(blockY + row).set(blockX + column, blockVal);
                }
            }

            return newBoard;
        }
    }

    public class Block {
        public class Location {
            public int x = 0;
            public int y = 0;
        }

        public int[][] shape = new int[][] { { 0, 1, 0 }, { 1, 1, 1 } };
        public int color = 4;

        public int getHeight() {
            return shape.length;
        }

        public int getWidth() {
            return shape[0].length;
        }

        public boolean detectBoardOverlap() {
            for (int row = 0; row < shape.length; row++) {
                for (int column = 0; column < shape[row].length; column++) {
                    int blockVal = shape[row][column];
                    int boardVal = gameState.board.get(location.y + row).get(location.x + column);
                    if (blockVal > 0 && boardVal > 0) {
                        return true;
                    }
                }
            }
            return false;
        }

        public boolean detectOutOfBounds() {
            // floor
            if (gameState.currentBlock.location.y >= gameState.boardHeight - gameState.currentBlock.getHeight()) {
                return true;
            }

            // left
            if (gameState.currentBlock.location.x < 0) {
                return true;
            }

            // right
            if (gameState.currentBlock.location.x >= gameState.boardWidth - getWidth()) {
                return true;
            }

            return false;
        }

        public boolean tryFall() {
            if (detectOutOfBounds()) return false;

            // Detect board
            location.y++;
            if (detectBoardOverlap()) {
                location.y--;
                return false;
            }

            return true;
        }
        public Location location = new Location();
    }

    State gameState = new State();
    long lastTick = System.nanoTime() / 1000000;

    State update(double deltaTime) {
        long now = System.nanoTime() / 1000000;

        if (now - lastTick > 1000) {
            lastTick = now;

            if (!gameState.currentBlock.tryFall()) {
                gameState.placeBlock();
                gameState.currentBlock = new Block();
                if (gameState.currentBlock.detectBoardOverlap()) {
                    // GAME OVER
                }
            }
        }

        return gameState;
    }
}
