package com.example.android.bakingapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.model.Recipe;

public class RecipeDetailListFragment extends Fragment {

    public static final String ARG_RECIPE = "recipe";
    public static final String ARG_RECIPE_ID = "recipe_id";
    public static final String ARG_TWO_PANE= "two_pane";

    private RecipeDetailAdapter.StepAdapterOnClickHandler mActivity;
    private Recipe mRecipe;
    private boolean mTwoPane;

    public RecipeDetailListFragment() {
        // Required empty public constructor
    }

    public static RecipeDetailListFragment newInstance(Recipe recipe, boolean twoPane) {
        RecipeDetailListFragment fragment = new RecipeDetailListFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_RECIPE, recipe);
        args.putBoolean(ARG_TWO_PANE, twoPane);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRecipe = getArguments().getParcelable(ARG_RECIPE);
            mTwoPane = getArguments().getBoolean(ARG_TWO_PANE, false);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_detail_list, container, false);

        RecyclerView recipeDetailRecyclerView = rootView.findViewById(R.id.recipe_detail_rv);

        Activity activity = getActivity();
        if (activity != null) {
            GridLayoutManager layoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 1);
            recipeDetailRecyclerView.setLayoutManager(layoutManager);
            RecipeDetailAdapter recipeDetailAdapter = new RecipeDetailAdapter(mActivity);
            recipeDetailRecyclerView.setAdapter(recipeDetailAdapter);

            if (mRecipe != null) {
                recipeDetailAdapter.setRecipe(mRecipe, mTwoPane);
                setActionBarTitle();
            } else {
                TextView noConnectionTextView = rootView.findViewById(R.id.no_connection_tv);
                recipeDetailRecyclerView.setVisibility(View.INVISIBLE);
                noConnectionTextView.setVisibility(View.VISIBLE);
            }
        }

        return rootView;
    }

    private void setActionBarTitle() {
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        if (activity != null) {
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(mRecipe.getName());
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.mActivity = (RecipeDetailAdapter.StepAdapterOnClickHandler) context;
        }
    }

}