package com.example.android.bakingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import timber.log.Timber;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import com.example.android.bakingapp.model.Ingredient;
import com.example.android.bakingapp.model.Recipe;

import java.util.ArrayList;

import static com.example.android.bakingapp.RecipeDetailListFragment.ARG_RECIPE_ID;
import static com.example.android.bakingapp.RecipeDetailListFragment.ARG_TWO_PANE;
import static com.example.android.bakingapp.RecipeIngredientsFragment.ARG_INGREDIENTS_LIST;
import static com.example.android.bakingapp.RecipeIngredientsFragment.ARG_RECIPE_NAME;
import static com.example.android.bakingapp.RecipeStepFragment.ARG_RECIPE;
import static com.example.android.bakingapp.RecipeStepFragment.ARG_RECIPE_STEP_INDEX;

public class RecipeActivity extends AppCompatActivity implements RecipeDetailAdapter.StepAdapterOnClickHandler {

    private static final String BUNDLE_STEP_INDEX = "step_index";

    private Recipe mRecipe;
    private boolean mTwoPane;
    private int mStepIndex;
    private View mPreviousView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        if(getResources().getBoolean(R.bool.landscape_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        if (savedInstanceState != null) {
            mStepIndex = savedInstanceState.getInt(BUNDLE_STEP_INDEX);
        } else {
            mStepIndex = -1;
        }

        int recipeId = 0;
        if (getIntent().hasExtra(ARG_RECIPE)) {
            mRecipe = getIntent().getParcelableExtra(ARG_RECIPE);
        } else {
            // Only when accessing from app widget
            if (getIntent().hasExtra(ARG_RECIPE_ID)) {
                recipeId = getIntent().getIntExtra(ARG_RECIPE_ID, 0);
            }
        }
        String queryRecipesUrl = getResources().getString(R.string.recipes_url);
        setupViewModel(mRecipe, queryRecipesUrl, recipeId);

        if (findViewById(R.id.recipe_detail_linear_layout) != null) {
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }

    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        if ( (fragment.getArguments() == null) && getIntent().hasExtra(ARG_RECIPE)) {
            Bundle args = new Bundle();
            if ( getIntent().hasExtra(ARG_RECIPE)) {
                args.putParcelable(ARG_RECIPE, getIntent().getParcelableExtra(ARG_RECIPE));
            } else {
                if ( getIntent().hasExtra(ARG_RECIPE_ID)) {
                    args.putInt(ARG_RECIPE_ID, getIntent().getIntExtra(ARG_RECIPE_ID, 0));
                }
            }
            args.putBoolean(ARG_TWO_PANE, getResources().getBoolean(R.bool.landscape_only));
            fragment.setArguments(args);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(BUNDLE_STEP_INDEX, mStepIndex);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View view, int stepIndex) {
        if (mTwoPane) {
            view.setSelected(true);
            if (mPreviousView != null) {
                mPreviousView.setSelected(false);
            }
        }
        mPreviousView = view;
        mStepIndex = stepIndex;
        if (mTwoPane) {
            FragmentManager fragmentManager = getSupportFragmentManager();

            RecipeStepFragment stepFragment = RecipeStepFragment.newInstance(mRecipe, mStepIndex);
            fragmentManager.beginTransaction()
                    .replace(R.id.recipe_detail_container, stepFragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, RecipeStepActivity.class);
            Bundle args = new Bundle();
            args.putParcelable(ARG_RECIPE, mRecipe);
            args.putInt(ARG_RECIPE_STEP_INDEX, mStepIndex);
            intent.putExtras(args);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View view, ArrayList<Ingredient> ingredients) {
        if (mTwoPane) {
            view.setSelected(true);
            if (mPreviousView != null) {
                mPreviousView.setSelected(false);
            }
        }
        mPreviousView = view;
        mStepIndex = -1;
        if (mTwoPane) {
            FragmentManager fragmentManager = getSupportFragmentManager();

            RecipeIngredientsFragment ingredientsFragment = RecipeIngredientsFragment.newInstance(mRecipe.getName(), (ArrayList<Ingredient>) mRecipe.getIngredients());
            fragmentManager.beginTransaction()
                    .replace(R.id.recipe_detail_container, ingredientsFragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, RecipeIngredientsActivity.class);
            Bundle args = new Bundle();
            args.putString(ARG_RECIPE_NAME, mRecipe.getName());
            args.putParcelableArrayList(ARG_INGREDIENTS_LIST, ingredients);
            intent.putExtras(args);
            startActivity(intent);
        }
    }

    private void setupViewModel(Recipe recipe, String recipesURL, int recipeId) {
        RecipeViewModelFactory factory = new RecipeViewModelFactory(recipe, recipesURL, recipeId);
        RecipeViewModel viewModel = new ViewModelProvider(this, factory).get(RecipeViewModel.class);
        viewModel.getRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe recipe) {
                Timber.tag("JSONLoad").d("Updating recipe JSON from LiveData in ViewModel");
                mRecipe = recipe;
                if (mRecipe == null) {
                    mTwoPane = false;
                }
                createFragments();
            }
        });
    }

    private void createFragments() {

        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment datailListFragment = fragmentManager.findFragmentById(R.id.recipe_detail_list_container);
        if (datailListFragment == null) {
            datailListFragment = RecipeDetailListFragment.newInstance(mRecipe, mTwoPane);
            fragmentManager.beginTransaction()
                    .add(R.id.recipe_detail_list_container, datailListFragment)
                    .commit();
        }

        if (mTwoPane) {

            if (mStepIndex == -1) {
                Fragment ingredientsFragment = fragmentManager.findFragmentById(R.id.recipe_detail_container);
                if (ingredientsFragment == null) {
                    ingredientsFragment = RecipeIngredientsFragment.newInstance(mRecipe.getName(), (ArrayList<Ingredient>) mRecipe.getIngredients());
                    fragmentManager.beginTransaction()
                            .add(R.id.recipe_detail_container, ingredientsFragment)
                            .commit();
                }
            } else {
                Fragment stepFragment = fragmentManager.findFragmentById(R.id.recipe_detail_container);
                if (stepFragment == null) {
                    stepFragment = RecipeStepFragment.newInstance(mRecipe, mStepIndex);
                    fragmentManager.beginTransaction()
                            .add(R.id.recipe_detail_container, stepFragment)
                            .commit();
                }
            }

        }

    }

}