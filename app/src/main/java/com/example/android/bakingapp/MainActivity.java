package com.example.android.bakingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.IdlingResource;
import timber.log.Timber;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.bakingapp.IdlingResource.SimpleIdlingResource;
import com.example.android.bakingapp.model.Recipe;

import java.util.List;

import static com.example.android.bakingapp.RecipeDetailListFragment.ARG_RECIPE;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.RecipeAdapterOnClickHandler {

    private static final String TAG = "MainActivity";

    private RecipeAdapter mRecipeAdapter;
    private RecyclerView mRecipesRecyclerView;
    private ProgressBar mProgressBar;
    private TextView mNoConnectionTextView;

    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getIdlingResource();

        if(getResources().getBoolean(R.bool.landscape_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        mProgressBar = (ProgressBar) findViewById(R.id.progress_pb);
        mNoConnectionTextView = (TextView) findViewById(R.id.no_connection_tv);

        mRecipesRecyclerView = (RecyclerView) findViewById(R.id.recipes_rv);

        int posterWidth = (int)getResources().getDimension(R.dimen.recipe_card_width);
        int spanCount = calculateBestSpanCount(posterWidth);

        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), spanCount);
        mRecipesRecyclerView.setLayoutManager(layoutManager);
        mRecipeAdapter = new RecipeAdapter(this);
        mRecipesRecyclerView.setAdapter(mRecipeAdapter);

        setupViewModel();

        if (BuildConfig.DEBUG) {
            if (Timber.treeCount() == 0) {
                Timber.plant(new Timber.DebugTree());
            }
        }

        Timber.tag(TAG).d("Activity Created");

    }

    private void setupViewModel() {

        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(false);
        }

        mProgressBar.setVisibility(View.VISIBLE);

        String queryRecipesUrl = getResources().getString(R.string.recipes_url);
        MainViewModelFactory factory = new MainViewModelFactory(queryRecipesUrl);
        MainViewModel viewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);
        viewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                Timber.tag("JSONLoad").d("Updating recipe JSON from LiveData in ViewModel");
                if ( (recipes != null) && (recipes.size() > 0) ) {
                    Timber.tag("JSONLoad").d("%d recipes has been retrieved", recipes.size());
                    mRecipesRecyclerView.setVisibility(View.VISIBLE);
                    mRecipeAdapter.setRecipes(recipes);
                } else {
                    Timber.tag("JSONLoad").d("no recipes has been retrieved");
                    showNoConnectionMessage();
                }
                mProgressBar.setVisibility(View.INVISIBLE);
                if (mIdlingResource != null) {
                    mIdlingResource.setIdleState(true);
                }

            }
        });
    }

    private void showNoConnectionMessage() {
        mNoConnectionTextView.setVisibility(View.VISIBLE);
        mRecipesRecyclerView.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onClick(Recipe recipe) {
        Timber.d("%s with id %d selected", recipe.getName(), recipe.getId());
        Intent intent = new Intent(this, RecipeActivity.class);
        Bundle args = new Bundle();
        args.putParcelable(ARG_RECIPE, recipe);
        intent.putExtras(args);
        startActivity(intent);
    }

    private int calculateBestSpanCount(int posterWidth) {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float screenWidth = outMetrics.widthPixels;
        return Math.round(screenWidth / posterWidth);
    }

}