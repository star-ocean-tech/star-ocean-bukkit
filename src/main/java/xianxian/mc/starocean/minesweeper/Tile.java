package xianxian.mc.starocean.minesweeper;

public class Tile {
    private Type type;
    private State state;
    private final int posX;
    private final int posY;
    private int bombAmountAround;

    public Tile(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
        this.bombAmountAround = 0;
        this.type = Type.NONE;
        this.state = State.NORMAL;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getBombAmountAround() {
        return bombAmountAround;
    }

    public void setBombAmountAround(int bombAmountAround) {
        this.bombAmountAround = bombAmountAround;
    }

    public void increaseBombAmountAround() {
        this.bombAmountAround++;
    }

    @Override
    public String toString() {
        return "Tile [type=" + type + ", state=" + state + ", posX=" + posX + ", posY=" + posY + ", bombAmountAround="
                + bombAmountAround + "]";
    }

    public enum State {
        NORMAL, SHOWN, FLAGGED, MARKED
    }

    public enum Type {
        NONE, BOMB
    }
}
