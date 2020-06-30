package com.example.android.bakingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.android.bakingapp.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class RecipeIngredientsFragment extends Fragment {

    public static final String ARG_INGREDIENTS_LIST = "ingredients_list";
    public static final String ARG_RECIPE_NAME = "recipe_name";

    private List<Ingredient> mIngredients;
    private String mRecipeName;

    public RecipeIngredientsFragment() {
    }

    public static RecipeIngredientsFragment newInstance(String recipeName, ArrayList<Ingredient> ingredients) {
        RecipeIngredientsFragment fragment = new RecipeIngredientsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RECIPE_NAME, recipeName);
        args.putParcelableArrayList(ARG_INGREDIENTS_LIST, ingredients);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(ARG_INGREDIENTS_LIST)) {
                mRecipeName = getArguments().getString(ARG_RECIPE_NAME);
                mIngredients = getArguments().getParcelableArrayList(ARG_INGREDIENTS_LIST);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_recipe_ingredient, container, false);

        GridView gridView = rootView.findViewById(R.id.recipe_ingredients_grid_view);

        RecipeIngredientAdapter mAdapter = new RecipeIngredientAdapter(mIngredients);

        gridView.setAdapter(mAdapter);

        AppCompatActivity activity = (AppCompatActivity)getActivity();
        if (activity != null) {
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(mRecipeName);
                actionBar.setSubtitle(getString(R.string.ingredients_label));
            }
        }

        return rootView;
    }
}
