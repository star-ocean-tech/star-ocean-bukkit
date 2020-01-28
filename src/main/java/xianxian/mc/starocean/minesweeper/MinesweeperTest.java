package xianxian.mc.starocean.minesweeper;

import com.google.common.eventbus.Subscribe;

import xianxian.mc.starocean.minesweeper.Tile.Type;

public class MinesweeperTest {
    private static Minesweeper minesweeper;

    public static void main(String[] args) {
        int sx = 8;
        int sy = 6;
        minesweeper = new Minesweeper("Test", sx, sy, 8);
        minesweeper.getMinesweeperEventBus().register(new MinesweeperTest());
        print();
        System.out.println();
        // minesweeper.dig(1, 2);
        // minesweeper.dig(minesweeper.bombs.get(0).getPosX(),
        // minesweeper.bombs.get(0).getPosY());
        for (int i = 0, sizeY = minesweeper.getSizeY(); i < sizeY; i++) {
            boolean found = false;
            for (int j = 0, sizeX = minesweeper.getSizeX(); j < sizeX; j++) {
                Tile tile = minesweeper.getMap().get(j, i);
                if (tile.getBombAmountAround() == 0) {
                    minesweeper.dig(tile.getPosX(), tile.getPosY());
                    found = true;
                    break;
                }
            }
            if (found)
                break;
        }
        print();

    }

    @Subscribe
    public void onMinesweeperEvent(MinesweeperEvent event) {
        System.out.println(event);
    }

    private static void print() {
        for (int i = 0, sizeY = minesweeper.getSizeY(); i < sizeY; i++) {
            StringBuilder line = new StringBuilder();
            for (int j = 0, sizeX = minesweeper.getSizeX(); j < sizeX; j++) {
                Tile tile = minesweeper.getMap().get(j, i);
                line.append(tile.getType().equals(Type.NONE)
                        ? " N" + tile.getBombAmountAround() + tile.getState().name().charAt(0) + " "
                        : " B" + tile.getBombAmountAround() + tile.getState().name().charAt(0) + " ");
            }
            System.out.println(line.toString());
        }
    }
}
