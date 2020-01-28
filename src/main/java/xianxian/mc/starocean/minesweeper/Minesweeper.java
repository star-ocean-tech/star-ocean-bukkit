package xianxian.mc.starocean.minesweeper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import com.google.common.eventbus.EventBus;

import xianxian.mc.starocean.minesweeper.Tile.State;
import xianxian.mc.starocean.minesweeper.Tile.Type;

public class Minesweeper {
    private final EventBus minesweeperEventBus;
    private MinesweeperMap map;
    private final int sizeX;
    private final int sizeY;
    private final int bombCount;
    public final List<Tile> bombs;
    private final Logger logger;
    private int flaggedMines = 0;
    private int flagsUsed;
    private GameState state;

    public Minesweeper(String gameName, int sizeX, int sizeY, int bombs) {
        this.minesweeperEventBus = new EventBus();
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.bombCount = bombs;
        this.flagsUsed = 0;
        this.bombs = new ArrayList<Tile>();
        this.state = GameState.PREPARING;
        this.logger = Logger.getLogger(gameName + "-Minesweeper");
        this.logger.setFilter(null);
        this.generate();
    }

    public EventBus getMinesweeperEventBus() {
        return minesweeperEventBus;
    }

    public GameState getGameState() {
        return state;
    }

    private void generate() {
        logger.info("Generating a new land with size: " + sizeX + ", " + sizeY);
        map = new MinesweeperMap(sizeX, sizeY);
        map.fill();

        int bombsLeft = bombCount;
        Random random = new Random();

        while (bombsLeft > 0) {
            for (int x = 0; x < sizeX; x++) {
                for (int y = 0; y < sizeY; y++) {
                    Tile tile = map.get(x, y);
                    if (tile.getType().equals(Type.BOMB))
                        continue;
                    if (bombsLeft > 0 && random.nextInt(10) == 0) {
                        tile.setType(Type.BOMB);
                        
                        if (y - 1 >= 0) {
                            if (x - 1 >= 0)
                                map.get(x - 1, y - 1).increaseBombAmountAround();
                            map.get(x, y - 1).increaseBombAmountAround();
                            if (x + 1 < sizeX)
                                map.get(x + 1, y - 1).increaseBombAmountAround();
                        }
                        if (x - 1 >= 0)
                            map.get(x - 1, y).increaseBombAmountAround();
                        map.get(x, y).increaseBombAmountAround();
                        if (x + 1 < sizeX)
                            map.get(x + 1, y).increaseBombAmountAround();
                        if (y + 1 < sizeY) {
                            if (x - 1 >= 0)
                                map.get(x - 1, y + 1).increaseBombAmountAround();
                            map.get(x, y + 1).increaseBombAmountAround();
                            if (x + 1 < sizeX)
                                map.get(x + 1, y + 1).increaseBombAmountAround();
                        }
                        this.bombs.add(tile);
                        bombsLeft--;
                    } else {
                        continue;
                    }
                }
            }
        }
        this.state = GameState.PLAYING;
    }

    public void dig(int x, int y) {
        dig(x, y, false);
    }

    public void dig(int x, int y, boolean recursive) {
        if (this.state != GameState.PLAYING) {
            logger.fine("Attempting to dig a tile but the game is over");
            return;
        }

        if ((x >= 0 && x < sizeX) && (y >= 0 && y < sizeY)) {
            Tile tile = map.get(x, y);
            if (tile.getState().equals(State.FLAGGED)) {
                logger.fine(String.format("Attempting to dig a flagged tile at {%d, %d} ", x, y));
                return;
            }
            if (tile.getState().equals(State.SHOWN)) {
                logger.fine("Attempting to dig a shown tile");
                return;
            }

            if ((recursive && !tile.getType().equals(Type.BOMB)) || !recursive)
                tile.setState(State.SHOWN);
            MinesweeperEvent.Dig event = new MinesweeperEvent.Dig(this, tile);
            this.minesweeperEventBus.post(event);
            if (tile.getBombAmountAround() == 0) {
                if (tile.getPosX() - 1 >= 0)
                    dig(tile.getPosX() - 1, tile.getPosY(), true);
                if (tile.getPosX() + 1 < sizeX)
                    dig(tile.getPosX() + 1, tile.getPosY(), true);
                if (tile.getPosY() - 1 >= 0)
                    dig(tile.getPosX(), tile.getPosY() - 1, true);
                if (tile.getPosY() + 1 < sizeY)
                    dig(tile.getPosX(), tile.getPosY() + 1, true);
            }
            if (!recursive && tile.getType().equals(Type.BOMB)) {
                this.bombs.forEach((t) -> t.setState(State.SHOWN));
                this.gameOver();
            }

        } else {
            logger.fine(String.format("Attempting to dig an invalid position {%d, %d}", x, y));
        }
    }

    public void gameOver() {
        this.state = GameState.FAILED;
        this.minesweeperEventBus.post(new MinesweeperEvent.Fail(this));
    }

    public void win() {
        this.state = GameState.COMPLETED;
        this.minesweeperEventBus.post(new MinesweeperEvent.Win(this));
    }

    public void toggleFlag(int x, int y) {
        if (this.state != GameState.PLAYING) {
            logger.fine("Attempting to flag a tile but the game is over");
            return;
        }

        if ((x >= 0 && x < sizeX) && (y >= 0 && y < sizeY)) {
            Tile tile = map.get(x, y);
            if (tile.getState().equals(State.FLAGGED)) {
                tile.setState(State.NORMAL);
                this.flagsUsed--;
                MinesweeperEvent.Unflag event = new MinesweeperEvent.Unflag(this, tile);
                this.minesweeperEventBus.post(event);
                if (tile.getType().equals(Type.BOMB)) {
                    flaggedMines--;
                }
            } else {
                if ((flagsUsed + 1) > bombCount) {
                    return;
                }
                tile.setState(State.FLAGGED);
                flagsUsed++;
                MinesweeperEvent.Flag event = new MinesweeperEvent.Flag(this, tile);
                this.minesweeperEventBus.post(event);
                if (tile.getType().equals(Type.BOMB)) {
                    flaggedMines++;
                    if (flaggedMines == bombCount) {
                        win();
                    }
                }
            }

        }
    }

    public MinesweeperMap getMap() {
        return map;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public enum GameState {
        PREPARING, FAILED, COMPLETED, PLAYING
    }

    public static class MinesweeperMap {
        private Tile[][] tileArray;
        private int sizeX;
        private int sizeY;

        public MinesweeperMap(int sizeX, int sizeY) {
            this.sizeX = sizeX;
            this.sizeY = sizeY;
            tileArray = new Tile[sizeX][sizeY];
            init();
        }

        private void init() {
            for (int y = 0; y < tileArray.length; y++) {
                tileArray[y] = new Tile[sizeX];
            }
        }

        public void fill() {
            for (int x = 0; x < sizeX; x++) {
                for (int y = 0; y < sizeY; y++) {
                    tileArray[x][y] = new Tile(x, y);
                }
            }
        }

        public Tile get(int posX, int posY) {
            return tileArray[posX][posY];
        }
    }
}
