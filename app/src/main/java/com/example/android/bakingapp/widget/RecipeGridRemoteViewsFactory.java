package com.example.android.bakingapp.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.model.Recipe;
import com.example.android.bakingapp.util.RecipeUtil;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import timber.log.Timber;

import static com.example.android.bakingapp.RecipeDetailListFragment.ARG_RECIPE_ID;

public class RecipeGridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = "GridRemoteViewsFact";

    private Context mContext;
    private List<Recipe> mRecipes;

    public RecipeGridRemoteViewsFactory(Context context) {
        Timber.tag(TAG).d("GridRemoteViewsFactory: starts");
        mContext = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        Timber.tag(TAG).d("onDataSetChanged: starts");
        try {
            String queryRecipesUrl = mContext.getResources().getString(R.string.recipes_url);
            URL searchQueryUrl = new URL(queryRecipesUrl);
            String recipesJSON = RecipeUtil.getResponseFromHttpUrl(searchQueryUrl);
            mRecipes = RecipeUtil.createRecipeList(recipesJSON);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mRecipes != null) {
            for (Recipe recipe: mRecipes) {
                Timber.tag(TAG).d("onDataSetChanged: %s", recipe.getName());
            }
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (mRecipes == null) {
            return 0;
        }
        return mRecipes.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.recipe_widget_provider);

        if (mRecipes != null) {
            Recipe recipe = mRecipes.get(position);
            Intent fillInIntent = new Intent();
            if (recipe != null) {
                views.setTextViewText(R.id.recipe_name_widget, recipe.getName());
                Bundle extras = new Bundle();
                extras.putInt(ARG_RECIPE_ID, recipe.getId());
                fillInIntent.putExtras(extras);
            }
            views.setOnClickFillInIntent(R.id.recipe_widget, fillInIntent);
        }

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
