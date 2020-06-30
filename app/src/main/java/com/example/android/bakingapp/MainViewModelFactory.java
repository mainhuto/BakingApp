package com.example.android.bakingapp;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final String mRecipesURL;

    public MainViewModelFactory(String recipesURL) {
        this.mRecipesURL = recipesURL;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MainViewModel(mRecipesURL);
    }
}
