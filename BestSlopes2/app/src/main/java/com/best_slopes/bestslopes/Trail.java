package com.best_slopes.bestslopes;

import java.util.ArrayList;

/**
 * Created by Michael Humphrey on 4/22/2016.
 */
public class Trail {
    private int id;
    private String name;
    private float rating;
    private int difficulty;
    private String comments;
    private boolean isNew;
    private ArrayList<String> imagePaths; // Jhon: we should also handle with this

    public Trail() {
        this.name = "";
        this.rating = 0.0f;
        this.difficulty = 0;
        this.comments = "";
        this.id = -1;
        this.isNew = true;
        this.imagePaths = new ArrayList<String>();
    }

    public Trail(String name, float rating, int difficulty) {
        this.name = name;
        this.rating = rating;
        this.difficulty = difficulty;
        this.comments = "";
        this.id = -1;
        this.isNew = true;
        this.imagePaths = new ArrayList<String>();
    }

    public Trail(String name, float rating, int difficulty, String comments) {
        this.name = name;
        this.rating = rating;
        this.difficulty = difficulty;
        this.comments = comments;
        this.id = -1;
        this.isNew = true;
        this.imagePaths = new ArrayList<String>();
    }

    public Trail(String name, float rating, int difficulty, String comments, int id) {
        this.name = name;
        this.rating = rating;
        this.difficulty = difficulty;
        this.comments = comments;
        this.id = id;
        this.isNew = true;
        this.imagePaths = new ArrayList<String>();
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

    public ArrayList<String> getCommentsList() { // TODO: John: Comments should be handled.
        comments = "Great#Dangerous#I love it#Fine";
        ArrayList<String> commentsList = new ArrayList<>();
        String[] arr = comments.split("#");
        for (String comment: arr) {
            commentsList.add(comment);
        }
        return commentsList;
    }

    public String getComments() { return comments; }

    public void addComment(String comment) {
        this.comments = comments + "#" + comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setId(long id) { this.id = (int) id; }

    public int getImageByDifficulty() { // Jhon: It works for both screens
        switch (difficulty) {
            default:
                return R.drawable.easy;
            case 2:
                return R.drawable.medium;
            case 3:
                return R.drawable.difficult;
            case 4:
                return R.drawable.extremely_difficult;
        }
    }

    public ArrayList<String> getImagePaths() {  return imagePaths;
    }

    public void addImagePath(String imagePath) { imagePaths.add(imagePath); }

    public boolean isNew() {
        return isNew;
    }

    public void setOld() {
        isNew = false;
    }

    public String toString() {
        String result = "ID: " + id + ", isNew: " + isNew + ", Name: " + name +
                ", Rating: " + rating + ", Difficulty: " +difficulty +
                ", Comments: \"" +comments + "\", Paths: [";

        for (String path : getImagePaths()) {
            result += path;
        }

        result += "]";

        return result;
    }
}
