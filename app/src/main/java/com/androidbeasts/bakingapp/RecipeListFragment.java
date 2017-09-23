package com.androidbeasts.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidbeasts.bakingapp.adapter.RecipeListAdapter;
import com.androidbeasts.bakingapp.model.Ingredients;
import com.androidbeasts.bakingapp.model.Recipe;
import com.androidbeasts.bakingapp.model.Steps;
import com.androidbeasts.bakingapp.utils.ConnectionUtil;
import com.androidbeasts.bakingapp.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Fragment to show recipe list
 */
public class RecipeListFragment extends Fragment implements RecipeListAdapter.OnListItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final String TAG = RecipeListFragment.class.getClass().getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private final String RECIPE_LIST_PARCELABLE_STRING = "recipes";
    @BindString(R.string.no_internet_message)
    protected String NO_INTERNET_MESSAGE;

    private OnFragmentInteractionListener mListener;

    private RecipeListAdapter mRecipeListAdapter;
    private ArrayList<Recipe> recipeArrayList;

    private RequestQueue requestQueue;
    private Gson gson;
    @BindView(R.id.progressBar)
    protected ProgressBar mProgressBar;
    @BindView(R.id.recipe_reyclerview)
    protected RecyclerView mRecipesRecyclerView;

    private ConnectionUtil connectionUtil;
    private boolean mTwoPane;

    public RecipeListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecipeListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipeListFragment newInstance(String param1, String param2) {
        RecipeListFragment fragment = new RecipeListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (recipeArrayList != null) {
            outState.putParcelableArrayList(RECIPE_LIST_PARCELABLE_STRING, recipeArrayList);
        }
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        ButterKnife.bind(this, rootView);

        connectionUtil = new ConnectionUtil(getActivity());
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();


        boolean is_phone = getActivity().getResources().getBoolean(R.bool.is_phone);
        if (is_phone) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            mRecipesRecyclerView.setLayoutManager(linearLayoutManager);

        } else {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
            mRecipesRecyclerView.setLayoutManager(gridLayoutManager);
        }
        mRecipesRecyclerView.setHasFixedSize(true);
        if (savedInstanceState == null || !savedInstanceState.containsKey(RECIPE_LIST_PARCELABLE_STRING)) {
            recipeArrayList = new ArrayList<>();
            if (connectionUtil.isOnline()) {
                //Fetch the list of recipes
                fetchRecipes();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), NO_INTERNET_MESSAGE, Toast.LENGTH_LONG).show();
                /*Snackbar.make(mainLayout, NO_INTERNET_MESSAGE, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                mProgressBar.setVisibility(View.GONE);*/
            }
        } else {
            recipeArrayList = savedInstanceState.getParcelableArrayList(RECIPE_LIST_PARCELABLE_STRING);
        }

        return rootView;
    }

    /*Method to fetch the list of recipes*/
    private void fetchRecipes() {
        mRecipesRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.GET, Constants.BASE_URL, onListLoaded, onListError);
        requestQueue.add(request);
    }

    /*When the list is loaded*/
    private final Response.Listener<String> onListLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.i(TAG, response);
            recipeArrayList.addAll(Arrays.asList(gson.fromJson(response, Recipe[].class)));
            Log.i(TAG, recipeArrayList.size() + " recipes loaded.");
            /*for (Recipe recipes : recipe) {
                Log.i("PostActivity", recipes.getId() + ": " + recipes.getRecipe_name());
              }
            */
            if (mRecipeListAdapter == null) {
                mRecipeListAdapter = new RecipeListAdapter(recipeArrayList, getActivity(),
                        recipeArrayList.size(), RecipeListFragment.this);
                mRecipesRecyclerView.setAdapter(mRecipeListAdapter);
            } else {
                mRecipeListAdapter.notifyDataSetChanged();
            }
            mRecipesRecyclerView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    };

    /*In case of any error in volley request*/
    private final Response.ErrorListener onListError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(TAG, error.toString());
            mRecipesRecyclerView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    };


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       /* if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Log.d(TAG, "Clicked index " + clickedItemIndex);
        Recipe recipe = recipeArrayList.get(clickedItemIndex);
//        Log.d(TAG, "Steps ArrayList" + recipe.getStepsArrayList().toString());
        ArrayList<Steps> stepsArrayList = new ArrayList<>();
        stepsArrayList.addAll(recipe.getStepsArrayList());
        ArrayList<Ingredients> ingredientsArrayList = new ArrayList<>();
        ingredientsArrayList.addAll(recipe.getIngredientsArrayList());
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(RecipeDetailActivity.STEPS_PARCELABLE_ARRAYLIST, stepsArrayList);
        bundle.putParcelableArrayList(RecipeDetailActivity.INGREDIENTS_PARCELABLE_ARRAYLIST, ingredientsArrayList);
        Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
