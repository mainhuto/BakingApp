package com.example.android.bakingapp.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.android.bakingapp.R;

import androidx.annotation.Nullable;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class IngredientsUpdateService extends IntentService {

    public static final String ACTION_UPDATE_INGREDIENTS = "com.example.android.bakingapp.widget.action.update_ingredients_widget";
    public static final String EXTRA_RECIPE_NAME = "com.example.android.bakingapp.widget.extra.recipe_name";

    public IngredientsUpdateService() {
        super(IngredientsUpdateService.class.getName());
    }

    public static void startActionUpdateIngredientsWidgets(Context context) {
        SharedPreferences sharedPref = getDefaultSharedPreferences(context);
        Intent intent = new Intent(context, IngredientsUpdateService.class);
        intent.setAction(ACTION_UPDATE_INGREDIENTS);
        String recipeNameKey = context.getString(R.string.preference_recipe_name_key);
        if (sharedPref.contains(recipeNameKey)) {
            intent.putExtra(EXTRA_RECIPE_NAME, sharedPref.getString(recipeNameKey, ""));
        }
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_INGREDIENTS.equals(action)) {
                final String recipeName = intent.getStringExtra(EXTRA_RECIPE_NAME);
                handleActionUpdateIngredients(recipeName);
            }
        }
    }

    private void handleActionUpdateIngredients(String recipeName) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, IngredientsAppWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_ingredients_grid_view);
        IngredientsAppWidget.updateIngredientsWidgets(this, appWidgetManager, appWidgetIds, recipeName);
    }

}
