package com.androidbeasts.bakingapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/*Model class for recipe*/
public class Recipe implements Parcelable {
    private String id;

    @SerializedName("name")
    private String recipe_name;

    @SerializedName("ingredients")
    private List<Ingredients> ingredientsArrayList;

    @SerializedName("steps")
    private List<Steps> stepsArrayList;

    protected Recipe(Parcel in) {
        id = in.readString();
        recipe_name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(recipe_name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecipe_name() {
        return recipe_name;
    }

    public void setRecipe_name(String recipe_name) {
        this.recipe_name = recipe_name;
    }

    public List<Ingredients> getIngredientsArrayList() {
        return ingredientsArrayList;
    }

    public void setIngredientsArrayList(ArrayList<Ingredients> ingredientsArrayList) {
        this.ingredientsArrayList = ingredientsArrayList;
    }

    public List<Steps> getStepsArrayList() {
        return stepsArrayList;
    }

    public void setStepsArrayList(ArrayList<Steps> stepsArrayList) {
        this.stepsArrayList = stepsArrayList;
    }

    public Recipe(String id, String recipe_name, ArrayList<Ingredients> ingredientsArrayList, ArrayList<Steps> stepsArrayList) {
        this.id = id;
        this.recipe_name = recipe_name;
        this.ingredientsArrayList = ingredientsArrayList;
        this.stepsArrayList = stepsArrayList;
    }
}
