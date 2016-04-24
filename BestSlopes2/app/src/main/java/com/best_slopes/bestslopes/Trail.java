package com.best_slopes.bestslopes;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Michael Humphrey on 4/22/2016.
 */
public class Trail {
    private String name;
    private float rating;
    private int difficulty;
    private String comments;
    private int id;
    private ArrayList<String> imagePaths; // Jhon: we should also handle with this

    public Trail() {
        this.name = "";
        this.rating = 0.0f;
        this.difficulty = 0;
        this.comments = "";
        this.id = -1;
        this.imagePaths = new ArrayList<String>();
    }

    public Trail(String name, float rating, int difficulty) {
        this.name = name;
        this.rating = rating;
        this.difficulty = difficulty;
        this.comments = "";
        this.id = -1;
        this.imagePaths = new ArrayList<String>();
    }

    public Trail(String name, float rating, int difficulty, String comments) {
        this.name = name;
        this.rating = rating;
        this.difficulty = difficulty;
        this.comments = comments;
        this.id = -1;
        this.imagePaths = new ArrayList<String>();
    }

    public Trail(String name, float rating, int difficulty, String comments, int id) {
        this.name = name;
        this.rating = rating;
        this.difficulty = difficulty;
        this.comments = comments;
        this.id = id;
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

    public String getComments() {
        return comments;
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

    public ArrayList<String> getImagePaths() {  /*/ Jhon: It should be filled from database // This is just for debug
        imagePaths = new ArrayList<String>();
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        for (int i = 1; i < 8; i += 1) {
            imagePaths.add(baseDir + File.separator + "DCIM/BestSlopes2/IMG_0" + i + ".JPG");
        }
        return imagePaths;*/
        return imagePaths;
    }

    public void addImagePath(String imagePath) { imagePaths.add(imagePath); }


    public String toString() {
        String result = "ID: " + id + ", Name: " + name + ", Rating: " + rating + ", Difficulty: " +
                difficulty + ", Comments: \"" +comments + "\", Paths: [";

        for (String path : getImagePaths()) {
            result += path;
        }

        result += "]";

        return result;
    }
}
