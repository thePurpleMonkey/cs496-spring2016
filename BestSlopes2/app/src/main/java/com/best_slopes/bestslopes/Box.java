package com.best_slopes.bestslopes;

/**
 * Created by pkess_000 on 5/8/2016.
 */
public class Box<Integer> {
    private int value;

    public Box() {
    }

    public Box(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
