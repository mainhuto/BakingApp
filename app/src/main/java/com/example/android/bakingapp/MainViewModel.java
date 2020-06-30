package com.example.android.bakingapp;

import com.example.android.bakingapp.model.Recipe;
import com.example.android.bakingapp.util.NetworkExecutor;
import com.example.android.bakingapp.util.RecipeUtil;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import timber.log.Timber;

public class MainViewModel extends ViewModel {

    private MutableLiveData<List<Recipe>> mRecipes;

    public MainViewModel(String recipesURL) {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        if (mRecipes == null) {
            mRecipes = new MutableLiveData<>();
            loadRecipeJSON(recipesURL);
        }
    }

    public MutableLiveData<List<Recipe>> getRecipes() {
        return mRecipes;
    }

    private void loadRecipeJSON(String recipesURL) {
        Timber.tag("JSONLoad").d("loadRecipeJSON: starts");
        NetworkExecutor.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL searchQueryUrl = new URL(recipesURL);
                    Timber.tag("JSONLoad").d("load URL: %s", searchQueryUrl.toString());
                    String recipesJSON = RecipeUtil.getResponseFromHttpUrl(searchQueryUrl);
                    Timber.tag("JSONLoad").d("load done");
                    mRecipes.postValue(RecipeUtil.createRecipeList(recipesJSON));
                } catch (IOException e) {
                    mRecipes.postValue(null);
                    e.printStackTrace();
                }
            }
        });

    }
}
