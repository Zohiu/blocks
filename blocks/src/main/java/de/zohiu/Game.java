package de.zohiu;

import java.util.*;

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

        public State(int width, int height) {
            boardHeight = height;
            boardWidth = width;

            board = new ArrayList<>();
            for (int i = 0; i < height; i++) {
                board.add(new ArrayList<>(Collections.nCopies(width, 0)));
            }

            spawnBlock();
        }

        public void spawnBlock() {
            currentBlock = new Block(this, Shape.random());
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
            board = getVisualBoard();

            clearFullRows();
            spawnBlock();

            if (gameState.currentBlock.detectOverlap()) {
                throw new RuntimeException("GAME OVER!");
            }
        }

        // Falling blocks are not part of the board. This returns a copy of the board including falling blocks.
        public List<List<Integer>> getVisualBoard(boolean includeGhost) {
            // deep copy!
            List<List<Integer>> newBoard = new ArrayList<>();
            for (List<Integer> row : board) {
                List<Integer> newRow = new ArrayList<>(row);
                newBoard.add(newRow);
            }

            // Add block and ghost to board
            Block.Location location = currentBlock.location;
            Block.Location ghostLocation = currentBlock.getGhostLocation();

            for (int row = 0; row < currentBlock.getShape().length; row++) {
                for (int column = 0; column < currentBlock.getShape()[row].length; column++) {
                    int blockVal = currentBlock.getShape()[row][column];
                    if (blockVal > 0) {
                        if (includeGhost) newBoard.get(ghostLocation.y + row).set(ghostLocation.x + column, -1);
                        newBoard.get(location.y + row).set(location.x + column, blockVal * currentBlock.color);
                    }
                }
            }
            return newBoard;
        }

        // The ghost only matters for rendering, so it's not included by default.
        public List<List<Integer>> getVisualBoard() {
            return getVisualBoard(false);
        }
    }

    public class Block {
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
        private final Location location;

        public Block(State state, Shape shape) {
            this.shape = shape;
            this.location = new Location((state.boardWidth / 2) - (getShape()[0].length / 2), 0);
        }

        private int[][] getShape() {
            return shape.rotations[currentRotation];
        }

        private boolean detectOverlap() {
            for (int row = 0; row < getShape().length; row++) {
                for (int column = 0; column < getShape()[row].length; column++) {
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
                System.out.println(location.y);
            }
            int yAfter = location.y - 1;
            location.y = yBefore;
            System.out.println("DROPPED");
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

    State gameState;

    public Game() {
        gameState = new State(10, 24);
    }

    void update() {
        gameState.currentBlock.fall();
    }
}
