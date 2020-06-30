package com.example.android.bakingapp;

import android.util.Log;

import com.example.android.bakingapp.model.Recipe;
import com.example.android.bakingapp.util.NetworkExecutor;
import com.example.android.bakingapp.util.RecipeUtil;

import java.io.IOException;
import java.net.URL;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

class RecipeViewModel extends ViewModel {

    private static final String TAG = "TEST-RecipeViewModel";

    private MutableLiveData<Recipe> mRecipe;

    public RecipeViewModel(Recipe recipe, String recipesURL, int recipeId) {
        if (mRecipe == null) {
            mRecipe = new MutableLiveData<Recipe>();
            if (recipe != null) {
                mRecipe.setValue(recipe);
            } else {
                if (recipeId > 0) {
                    loadRecipe(recipesURL, recipeId);
                }
            }
        }
    }

    public MutableLiveData<Recipe> getRecipe() {
        return mRecipe;
    }

    private void loadRecipe(String recipesURL, int recipeId) {
        Log.d(TAG, "loadRecipe: starts");
        NetworkExecutor.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL searchQueryUrl = new URL(recipesURL);
                    String recipesJSON = RecipeUtil.getResponseFromHttpUrl(searchQueryUrl);
                    mRecipe.postValue(RecipeUtil.getRecipeWithId(recipesJSON, recipeId));
                } catch (IOException e) {
                    mRecipe.postValue(null);
                    e.printStackTrace();
                }
            }
        });

    }

}
