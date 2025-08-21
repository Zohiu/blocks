package de.zohiu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Game {
    private enum Shape {
        I(new int[][][] {
                {{0, 0, 0, 0}, {1, 1, 1, 1}, {0, 0, 0, 0}, {0, 0, 0, 0}},
                {{0, 0, 1, 0}, {0, 0, 1, 0}, {0, 0, 1, 0}, {0, 0, 1, 0}},
                {{0, 0, 0, 0}, {0, 0, 0, 0}, {1, 1, 1, 1}, {0, 0, 0, 0}},
                {{0, 1, 0, 0}, {0, 1, 0, 0}, {0, 1, 0, 0}, {0, 1, 0, 0}},
        }),

        J(new int[][][] {
                {{1, 0, 0}, {1, 1, 1}, {0, 0, 0}},
                {{0, 1, 1}, {0, 1, 0}, {0, 1, 0}},
                {{0, 0, 0}, {1, 1, 1}, {0, 0, 1}},
                {{0, 1, 0}, {0, 1, 0}, {1, 1, 0}},
        }),

        L(new int[][][] {
                {{0, 0, 1}, {1, 1, 1}, {0, 0, 0}},
                {{0, 1, 0}, {0, 1, 0}, {0, 1, 1}},
                {{0, 0, 0}, {1, 1, 1}, {1, 0, 0}},
                {{1, 1, 0}, {0, 1, 0}, {0, 1, 0}},
        }),

        O(new int[][][] {
                {{0, 1, 1, 0}, {0, 1, 1, 0}, {0, 0, 0, 0}},
        }),

        S(new int[][][] {
                {{0, 1, 1}, {1, 1, 0}, {0, 0, 0}},
                {{0, 1, 0}, {0, 1, 1}, {0, 0, 1}},
                {{0, 0, 0}, {0, 1, 1}, {1, 1, 0}},
                {{1, 0, 0}, {1, 1, 0}, {0, 1, 0}},
        }),

        T(new int[][][] {
                {{0, 1, 0}, {1, 1, 1}, {0, 0, 0}},
                {{0, 1, 0}, {0, 1, 1}, {0, 1, 0}},
                {{0, 0, 0}, {1, 1, 1}, {0, 1, 0}},
                {{0, 1, 0}, {1, 1, 0}, {0, 1, 0}},
        }),

        Z(new int[][][] {
                {{1, 1, 0}, {0, 1, 1}, {0, 0, 0}},
                {{0, 0, 1}, {0, 1, 1}, {0, 1, 0}},
                {{0, 0, 0}, {1, 1, 0}, {0, 1, 1}},
                {{0, 1, 0}, {1, 1, 0}, {1, 0, 0}},
        });

        private final int[][][] rotations;
        Shape(int[][][] rotations) {
            this.rotations = rotations;
        }

        private static final List<Shape> VALUES =
                Collections.unmodifiableList(Arrays.asList(values()));
        private static final int SIZE = VALUES.size();
        private static final Random RANDOM = new Random();
        public static Shape random()  {
            return VALUES.get(RANDOM.nextInt(SIZE));
        }
    }

    public class State {
        public int boardHeight;
        public int boardWidth;

        private List<List<Integer>> board;
        public Block currentBlock;
        public Block holdingBlock;

        private State(int width, int height) {
            boardHeight = height;
            boardWidth = width;

            board = new ArrayList<>();
            for (int i = 0; i < height; i++) {
                board.add(new ArrayList<>(Collections.nCopies(width, 0)));
            }

            spawnBlock();
        }

        public final boolean spawnBlock() {
            currentBlock = new Block(this, Shape.random());
            return false;
        }

        private void clearFullRows() {
            boolean rowCleared;
            do {
                rowCleared = false;
                for (int row = 0; row < boardHeight; row++) {
                    List<Integer> rowVal = board.get(row);
                    if (rowVal.stream().allMatch((val -> val > 0))) {
                        // Move above rows down
                        for (int rowAbove = row - 1; rowAbove >= 0; rowAbove--) {
                            board.set(rowAbove + 1, board.get(rowAbove));
                        }

                        rowCleared = true;
                    }
                }
            } while (rowCleared);
        }

        private void placeBlock() {
            board = getBoardWithBlock();

            clearFullRows();
            spawnBlock();

            if (gameState.currentBlock.detectOverlap()) {
                board = getBoardWithBlock();
                gameOver.run();
            }
        }

        public void holdBlock() {
            Block previousHolding = holdingBlock;
            holdingBlock = currentBlock;
            holdingBlock.resetLocation();
            holdingBlock.resetRotation();
            if (previousHolding != null) currentBlock = previousHolding;
            else spawnBlock();


        }

        public List<List<Integer>> copyBoard() {
            List<List<Integer>> newBoard = new ArrayList<>();
            for (List<Integer> row : board) {
                List<Integer> newRow = new ArrayList<>(row);
                newBoard.add(newRow);
            }
            return newBoard;
        }

        // Falling blocks are not part of the board. This returns a copy of the board including falling blocks.
        public List<List<Integer>> getBoardWithBlock() {
            List<List<Integer>> newBoard = copyBoard();
            Block.Location location = currentBlock.location;

            for (int row = 0; row < currentBlock.getShape().length; row++) {
                for (int column = 0; column < currentBlock.getShape()[row].length; column++) {
                    int blockVal = currentBlock.getShape()[row][column];
                    if (blockVal > 0) {
                        newBoard.get(location.y + row).set(location.x + column, blockVal * currentBlock.color);
                    }
                }
            }

            return newBoard;
        }

        // The visual board contains extras like ghost blocks
        public List<List<Integer>> getVisualBoard() {
            List<List<Integer>> newBoard = getBoardWithBlock();
            Block.Location ghostLocation = currentBlock.getGhostLocation();

            for (int row = 0; row < currentBlock.getShape().length; row++) {
                for (int column = 0; column < currentBlock.getShape()[row].length; column++) {
                    int blockVal = currentBlock.getShape()[row][column];
                    if (blockVal > 0) {
                        int boardVal = newBoard.get(ghostLocation.y + row).get(ghostLocation.x + column);
                        if (boardVal == 0) newBoard.get(ghostLocation.y + row).set(ghostLocation.x + column, -1);
                    }
                }
            }
            return newBoard;
        }
    }

    public final class Block {
        public static class Location {
            public int x;
            public int y;

            public Location(int x, int y) {
                this.x = x;
                this.y = y;
            }
        }

        // Random based on all available colors in Renderer
        public int color = (int) Math.max(1, Math.round(Math.random() * Renderer.colors.length));

        private final Shape shape;
        private int currentRotation = 0;
        private Location location;
        private final State state;

        private Block(State state, Shape shape) {
            this.state = state;
            this.shape = shape;
            resetLocation();
        }

        public void resetLocation() {
            this.location = new Location((state.boardWidth / 2) - (getShape()[0].length / 2), 0);
        }

        public void resetRotation() {
            this.currentRotation = 0;
        }

        public int[][] getShape() {
            return shape.rotations[currentRotation];
        }

        private boolean detectOverlap() {
            for (int row = getShape().length - 1; row >= 0; row--) {
                for (int column = getShape()[row].length - 1; column >= 0; column--) {
                    int blockVal = getShape()[row][column];
                    if (blockVal > 0) {
                        // Wall overlap
                        if (location.y + row > gameState.boardHeight - 1) return true;
                        if (location.x + column > gameState.boardWidth - 1  || location.x + column < 0) return true;

                        // Board overlap
                        int boardVal = gameState.board.get(location.y + row).get(location.x + column);
                        if (boardVal > 0) return true;
                    }
                }
            }
            return false;
        }

        public Location getGhostLocation() {
            int yBefore = location.y;
            while (!detectOverlap()) {
                location.y++;
            }
            int yAfter = Math.max(0, location.y - 1);
            location.y = yBefore;
            return new Location(location.x, yAfter);
        }

        public void instaDrop() {
            location.y = getGhostLocation().y;
            gameState.placeBlock();
        }

        private void fall() {
            location.y++;
            if (detectOverlap()) {
                location.y--;
                gameState.placeBlock();
            }
        }

        public void moveLeft() {
            location.x--;
            if (detectOverlap()) location.x++;
        }

        public void moveRight() {
            location.x++;
            if (detectOverlap()) location.x--;
        }

        public void rotate() {
            int rotationBefore = currentRotation;
            currentRotation++;
            if (currentRotation >= shape.rotations.length) currentRotation = 0;
            if (detectOverlap()) currentRotation = rotationBefore;
        }
    }

    public State gameState;
    private Runnable gameOver;

    public void setGameOverCallback(Runnable gameOver) {
        this.gameOver = gameOver;
    }

    public Game() {
        gameState = new State(10, 24);
    }

    void update() {
        gameState.currentBlock.fall();
    }
}
