package com.androidbeasts.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidbeasts.bakingapp.adapter.RecipeStepAdapter;
import com.androidbeasts.bakingapp.model.Ingredients;
import com.androidbeasts.bakingapp.model.Steps;
import com.androidbeasts.bakingapp.widget.UpdateBakingService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragment to show Recipe Detail
 */
public class RecipeDetailFragment extends Fragment implements RecipeStepAdapter.OnListItemClickListener {

    private static final String RECIPE_STEP_PARCELABLE_STRING = "steps";
    private static final String RECIPE_INGREDIENT_PARCELABLE_STRING = "ingredients";

    private static final String TAG = RecipeDetailFragment.class.getClass().getSimpleName();

    private RecipeStepAdapter mRecipeStepAdapter;
    private ArrayList<Steps> stepsArrayList;
    private ArrayList<Ingredients> ingredientsArrayList;

    @BindView(R.id.ingredients_tv)
    protected TextView mIngredientsTv;
    @BindView(R.id.steps_reyclerview)
    protected RecyclerView mStepsRecyclerView;

    private OnFragmentInteractionListener mListener;

    public RecipeDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecipeDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipeDetailFragment newInstance(String param1, String param2) {
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (stepsArrayList != null) {
            outState.putParcelableArrayList(RECIPE_STEP_PARCELABLE_STRING, stepsArrayList);
        }
        if (ingredientsArrayList != null) {
            outState.putParcelableArrayList(RECIPE_INGREDIENT_PARCELABLE_STRING, ingredientsArrayList);
        }
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        ButterKnife.bind(this, rootView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mStepsRecyclerView.setLayoutManager(linearLayoutManager);
        mStepsRecyclerView.setHasFixedSize(true);
        mStepsRecyclerView.setFocusable(false);
        mStepsRecyclerView.setFocusableInTouchMode(false);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mStepsRecyclerView.setNestedScrollingEnabled(false);
        }

        if (savedInstanceState == null || !savedInstanceState.containsKey(RECIPE_STEP_PARCELABLE_STRING) || !savedInstanceState.containsKey(RECIPE_INGREDIENT_PARCELABLE_STRING)) {
            stepsArrayList = new ArrayList<>();
            ingredientsArrayList = new ArrayList<>();
        } else {
            stepsArrayList = savedInstanceState.getParcelableArrayList(RECIPE_STEP_PARCELABLE_STRING);
            ingredientsArrayList = savedInstanceState.getParcelableArrayList(RECIPE_INGREDIENT_PARCELABLE_STRING);
        }
        //
        setRecipeDetails();
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onFrag1ListItemClicked(int index, ArrayList<Steps> stepsArrayList) {
        if (mListener != null) {
            mListener.onFragmentInteraction(index, stepsArrayList);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        // Create new fragment and transaction
        /*RecipeStepFragment recipeStepFragment = new RecipeStepFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(RecipeStepFragment.STEPS_PARCELABLE_STRING, stepsArrayList.get(clickedItemIndex));
        recipeStepFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        transaction.add(R.id.recipe_detail_frag_container, recipeStepFragment);
        transaction.addToBackStack(null);
        transaction.hide(this);

        // Commit the transaction
        transaction.commit();*/
        boolean mTwoPane = getActivity().getResources().getBoolean(R.bool.is_phone);
        if (mTwoPane) {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(StepActivity.STEPS_PARCELABLE_ARRAYLIST, stepsArrayList);
            bundle.putInt(StepActivity.STEP_LIST_INDEX, clickedItemIndex);
            Intent intent = new Intent(getActivity(), StepActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            onFrag1ListItemClicked(clickedItemIndex, stepsArrayList);
        }

        /**/
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(int index, ArrayList<Steps> arrayList);
    }

    /*Populate details in recipe list*/
    public void setRecipeDetails() {
        Bundle bundle = getArguments();
        stepsArrayList = bundle.getParcelableArrayList(RecipeDetailActivity.STEPS_PARCELABLE_ARRAYLIST);
        ingredientsArrayList = bundle.getParcelableArrayList(RecipeDetailActivity.INGREDIENTS_PARCELABLE_ARRAYLIST);
        Log.d(TAG, stepsArrayList.size() + " is steps size.");
        if (mRecipeStepAdapter == null) {
            mRecipeStepAdapter = new RecipeStepAdapter(stepsArrayList, getActivity(),
                    stepsArrayList.size(), this);
            mStepsRecyclerView.setAdapter(mRecipeStepAdapter);
        } else {
            mRecipeStepAdapter.notifyDataSetChanged();
        }

        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<String> recipeIngredientsForWidgets= new ArrayList<>();
        for (Ingredients ingredients : ingredientsArrayList) {
            stringBuilder.append(" \u2605 " + ingredients.getQuantity() + ingredients.getMeasure() + " of " + ingredients.getIngredient() + "\n");
            recipeIngredientsForWidgets.add(ingredients.getIngredient()+"\n"+
                    "Quantity: "+ingredients.getQuantity().toString()+"\n"+
                    "Measure: "+ingredients.getMeasure()+"\n");
        }

        mIngredientsTv.setText(stringBuilder.toString());

        //update widget
        UpdateBakingService.startBakingService(getContext(),recipeIngredientsForWidgets);


    }
}
