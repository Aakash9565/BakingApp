package com.androidbeasts.bakingapp;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.androidbeasts.bakingapp.model.Ingredients;
import com.androidbeasts.bakingapp.model.Steps;

import java.util.ArrayList;

/*Class to show recipe details*/
public class RecipeDetailActivity extends AppCompatActivity implements RecipeDetailFragment.OnFragmentInteractionListener {

    public static final String STEPS_PARCELABLE_ARRAYLIST = "stepsArrayList";
    public static final String INGREDIENTS_PARCELABLE_ARRAYLIST = "ingArrayList";

    /*boolean object to assign whether device is a tablet or phone*/
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        Bundle bundle = getIntent().getExtras();
        ArrayList<Steps> stepsArrayList = bundle.getParcelableArrayList(STEPS_PARCELABLE_ARRAYLIST);
        ArrayList<Ingredients> ingredientsArrayList = bundle.getParcelableArrayList(INGREDIENTS_PARCELABLE_ARRAYLIST);
        /*Log.d("RecipeDetail", "Steps ArrayList" + stepsArrayList.toString());*/

        RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();

        // recipeDetailFragment.setRecipeDetails(recipe);
        Bundle args = new Bundle();
        args.putParcelableArrayList(STEPS_PARCELABLE_ARRAYLIST, stepsArrayList);
        args.putParcelableArrayList(INGREDIENTS_PARCELABLE_ARRAYLIST, ingredientsArrayList);
        recipeDetailFragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.recipe_detail_frag_container, recipeDetailFragment)
                .commit();
        // Determine if you're creating a two-pane or single-pane display
        if (findViewById(R.id.tab_layout) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                RecipeStepFragment recipeStepFragment = new RecipeStepFragment();
                // Set the list of steps for the fragment
                recipeStepFragment.setSteps(stepsArrayList);
                //  recipeStepFragment.setListIndex(listIndex);
                fragmentManager.beginTransaction()
                        .add(R.id.recipe_step_frag_container, recipeStepFragment)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(int index, ArrayList<Steps> arrayList) {

        if (mTwoPane) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            RecipeStepFragment recipeStepFragment = new RecipeStepFragment();
            // Set the list of image id's for the head fragment and set the position to the second image in the list
            recipeStepFragment.setSteps(arrayList);
            recipeStepFragment.setListIndex(index);
            fragmentManager.beginTransaction()
                    .replace(R.id.recipe_step_frag_container, recipeStepFragment)
                    .commit();
        }
    }
}
