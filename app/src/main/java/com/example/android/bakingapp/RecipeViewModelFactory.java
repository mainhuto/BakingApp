package com.example.android.bakingapp;

import com.example.android.bakingapp.model.Recipe;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

class RecipeViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final Recipe mRecipe;
    private final String mRecipesURL;
    private final int mRecipeId;

    public RecipeViewModelFactory(Recipe recipe, String recipesURL, int recipeId) {
        this.mRecipe = recipe;
        this.mRecipesURL = recipesURL;
        this.mRecipeId = recipeId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new RecipeViewModel(mRecipe, mRecipesURL, mRecipeId);
    }
}
