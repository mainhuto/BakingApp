package com.example.android.bakingapp.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.model.Ingredient;
import com.example.android.bakingapp.model.Recipe;
import com.example.android.bakingapp.util.RecipeUtil;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;

import timber.log.Timber;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class IngredientsGridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = "GridRemoteViews";

    private Context mContext;
    private List<Ingredient> mIngredients;

    public IngredientsGridRemoteViewsFactory(Context context) {
        Timber.tag(TAG).d("IngredientsGridRemoteViewsFactory: starts");
        mContext = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        Timber.tag(TAG).d("onDataSetChanged: starts");

        SharedPreferences sharedPref = getDefaultSharedPreferences(mContext);
        String recipeIdKey = mContext.getString(R.string.preference_recipe_id_key);
        if (sharedPref.contains(recipeIdKey)) {
            try {
                String queryRecipesUrl = mContext.getResources().getString(R.string.recipes_url);
                URL searchQueryUrl = new URL(queryRecipesUrl);
                String recipesJSON = RecipeUtil.getResponseFromHttpUrl(searchQueryUrl);
                Recipe recipe = RecipeUtil.getRecipeWithId(recipesJSON, sharedPref.getInt(recipeIdKey, 0));
                if (recipe != null) {
                    mIngredients = recipe.getIngredients();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (mIngredients == null) {
            return 0;
        }
        return mIngredients.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.ingredient_widget_provider);

        if (mIngredients != null) {
            Ingredient ingredient = mIngredients.get(position);
            if (ingredient != null) {
                String quantity = new DecimalFormat("#.##").format(ingredient.getQuantity());
                String ingredientInfo = String.format("%s %s %s", ingredient.getDescription(), quantity, ingredient.getMeasure());
                views.setTextViewText(R.id.ingredient_name_widget, ingredientInfo);
            }
        } else {
            views.setTextViewText(R.id.ingredient_name_widget, "No ingredients");
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
