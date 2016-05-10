package com.best_slopes.bestslopes;

import java.util.ArrayList;

/**
 * Created by Michael Humphrey on 4/22/2016.
 */
public class Trail {
    private String name;
    private float rating;
    private int difficulty;
    private ArrayList<String> comments;
    private int id;
    private ArrayList<String> imagePaths; // Jhon: we should also handle with this

    public Trail() {
        this.name = "";
        this.rating = 0.0f;
        this.difficulty = 0;
        this.comments = new ArrayList<String>();
        this.imagePaths = new ArrayList<String>();
        this.id = -1;
    }

    public Trail(String name, float rating, int difficulty) {
        this.name = name;
        this.rating = rating;
        this.difficulty = difficulty;
        this.comments = new ArrayList<String>();
        this.imagePaths = new ArrayList<String>();
        this.id = -1;
    }

    public Trail(String name, float rating, int difficulty, String comments) {
        this.name = name;
        this.rating = rating;
        this.difficulty = difficulty;
        this.comments = new ArrayList<String>();
        this.imagePaths = new ArrayList<String>();
        this.id = -1;
    }

    public Trail(String name, float rating, int difficulty, String[] comments, int id) {
        this.name = name;
        this.rating = rating;
        this.difficulty = difficulty;
        this.comments = new ArrayList<String>();
        this.imagePaths = new ArrayList<String>();
        this.id = id;

        for (String comment : comments) {
            addComment(comment);
        }
    }

    public void addComment(String comment) {
        comments.add(comment);
    }

    public String removeComment(int position) {
        return comments.remove(position);
    }

    public boolean isNew() { return id == -1; }

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

    public String[] getComments() {
        return comments.toArray(new String[0]);
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public ArrayList<String> getImagePaths() {
        return imagePaths;
    }

    public void addImagePath(String imagePath) { imagePaths.add(imagePath); }

    public void removeImagePath(int imagePath) { imagePaths.remove(imagePath); }

    public void removeImagePath(String imagePath) { imagePaths.remove(imagePath); }


    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Rating: " + rating +
                ", Difficulty: " + difficulty + ", Comments: \"" + comments + "\"" +
                "# of ImagePaths: " + imagePaths.size();
    }
}
