package couch.joycouch.analog;

/**
 * This enum presents all the possible positions the analog stick of either side can be in.
 *
 * A 1 in the x or y means it is in the positive Cartesian direction.
 * A -1 in the x or y means it is in the negative Cartesian direction.
 * A 0 means it is not in that direction.
 *
 * Example: UP is in the positive Cartesian y direction, but not the x; so therefore, it's x value is 0, and y is 1;
 * Example: LEFT is in the negative Cartesian x direction, but not the y; so therefore, it's x value is -1, and y is 0;
 * Example: UP_RIGHT is in both the positive x and y Cartesian directions; so therefore, both x and y values are 1;
 * Example: DOWN_LEFT is in both the negative x and y Cartesian directions; so therefore, both x and y values are -1.
 */
public enum AnalogStickPos {

    RIGHT(1, 0, 0),
    DOWN_RIGHT(1, -1, 1),
    DOWN(0, -1, 2),
    DOWN_LEFT(-1, -1, 3),
    LEFT(-1, 0, 4),
    UP_LEFT(-1, 1, 5),
    UP(0, 1, 6),
    UP_RIGHT(1, 1, 7),
    CENTER(0, 0, 8)
    ;

    private int x, y, inVal;

    AnalogStickPos(int x, int y, int inVal){
        this.x = x;
        this.y = y;
        this.inVal = inVal;
    }

    public int getX(){ return x; }
    public int getY(){ return y; }
    public int getInputValue(){ return this.inVal; }
}
