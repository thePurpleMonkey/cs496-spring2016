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
    private ArrayList<String> comments;
    private boolean isNew;
    private ArrayList<String> imagePaths; // Jhon: we should also handle with this

    public Trail() {
        this.name = "";
        this.rating = 0.0f;
        this.difficulty = 0;
        this.comments = new ArrayList<String>();
        this.id = -1;
        this.isNew = true;
        this.imagePaths = new ArrayList<String>();
    }

    public Trail(String name, float rating, int difficulty) {
        this.name = name;
        this.rating = rating;
        this.difficulty = difficulty;
        this.comments = new ArrayList<String>();
        this.id = -1;
        this.isNew = true;
        this.imagePaths = new ArrayList<String>();
    }

    public Trail(String name, float rating, int difficulty, ArrayList<String> comments) {
        this.name = name;
        this.rating = rating;
        this.difficulty = difficulty;
        this.comments = comments;
        this.id = -1;
        this.isNew = true;
        this.imagePaths = new ArrayList<String>();
    }

    public Trail(String name, float rating, int difficulty, ArrayList<String> comments, int id) {
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
        return comments;
    }

    public String getComments() {
        if (comments.isEmpty()) {
            return "";
        }
        String commentsString = comments.get(0);
        for (int i = 1; i < comments.size(); i++) {
            commentsString = commentsString + "###" + comments.get(i);
        }
        return commentsString; }

    public void addComment(String comment) {
        comments.add(comment);
    }

    public void removeComment(String comment) {
        comments.remove(comment);
    }

    public void removeComment(int commentPos) {
        comments.remove(commentPos);
    }

    public void setComments(String commentsString) {
        String[] arr = commentsString.split("###");
        for (String comment: arr) {
            comments.add(comment);
        }
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

    public ArrayList<String> getImagePaths() {  return imagePaths; }

    public void addImagePath(String imagePath) { imagePaths.add(imagePath); }

    public void removeImagePath(String imagePath) { imagePaths.remove(imagePath); }

    public void removeImagePath(int imagePathPos) { imagePaths.remove(imagePathPos); }

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
