package com.androidbeasts.bakingapp;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.androidbeasts.bakingapp.model.Steps;

import java.util.ArrayList;

/*Activity to show steps*/
public class StepActivity extends AppCompatActivity {
    public static final String STEPS_PARCELABLE_ARRAYLIST = "steps";
    public static final String STEP_LIST_INDEX = "index";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        Bundle bundle = getIntent().getExtras();
        ArrayList<Steps> steps = bundle.getParcelableArrayList(STEPS_PARCELABLE_ARRAYLIST);
        int listIndex = bundle.getInt(STEP_LIST_INDEX);
        RecipeStepFragment recipeStepFragment = new RecipeStepFragment();
        recipeStepFragment.setSteps(steps);
        recipeStepFragment.setListIndex(listIndex);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.recipe_step_frag_container, recipeStepFragment)
                .commit();
    }
}
