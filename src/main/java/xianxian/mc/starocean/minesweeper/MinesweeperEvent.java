package xianxian.mc.starocean.minesweeper;

public class MinesweeperEvent {
    private final Minesweeper game;

    public MinesweeperEvent(Minesweeper game) {
        this.game = game;
    }

    public Minesweeper getGame() {
        return game;
    }

    public static class TileEvent extends MinesweeperEvent {
        private final Tile tile;

        public TileEvent(Minesweeper game, Tile tile) {
            super(game);
            this.tile = tile;
        }

        public Tile getTile() {
            return tile;
        }
    }

    public static class Dig extends TileEvent {

        public Dig(Minesweeper game, Tile tile) {
            super(game, tile);
        }

    }

    public static class Flag extends TileEvent {

        public Flag(Minesweeper game, Tile tile) {
            super(game, tile);
        }
    }

    public static class Unflag extends TileEvent {

        public Unflag(Minesweeper game, Tile tile) {
            super(game, tile);
        }

    }

    public static class Fail extends MinesweeperEvent {
        public Fail(Minesweeper game) {
            super(game);
        }
    }

    public static class Win extends MinesweeperEvent {
        public Win(Minesweeper game) {
            super(game);
        }
    }

}
