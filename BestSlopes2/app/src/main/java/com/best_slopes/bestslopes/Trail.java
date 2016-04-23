package com.best_slopes.bestslopes;

/**
 * Created by Michael Humphrey on 4/22/2016.
 */
public class Trail {
    private String name;
    private float rating;
    private int difficulty;
    private String comments;

    public Trail() {
        this.name = "";
        this.rating = 0.0f;
        this.difficulty = 0;
        this.comments = "";
    }

    public Trail(String name, float rating, int difficulty) {
        this.name = name;
        this.rating = rating;
        this.difficulty = difficulty;
        this.comments = "";
    }

    public Trail(String name, float rating, int difficulty, String comments) {
        this.name = name;
        this.rating = rating;
        this.difficulty = difficulty;
        this.comments = comments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String toString() {
        return "Name: " + name + ", Rating: " + rating + ", Difficulty: " + difficulty + ", Comments: \"" +comments + "\"";
    }
}
