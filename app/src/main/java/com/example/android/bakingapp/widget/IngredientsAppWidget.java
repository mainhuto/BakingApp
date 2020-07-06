package com.example.android.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import com.example.android.bakingapp.BuildConfig;
import com.example.android.bakingapp.R;

import timber.log.Timber;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientsAppWidget extends AppWidgetProvider {

    private static final String TAG = "I-AppWidgetProvider";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String recipeName) {

        if (BuildConfig.DEBUG) {
            if (Timber.treeCount() == 0) {
                Timber.plant(new Timber.DebugTree());
            }
        }

        Timber.tag(TAG).d("updateAppWidget: starts");

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget_grid_view);
        if (TextUtils.isEmpty(recipeName)) {
            views.setViewVisibility(R.id.ingredients_recipe_name_widget, View.INVISIBLE);
        } else {
            String headerText = recipeName + " " + context.getString(R.string.ingredients_label) + ":";
            views.setTextViewText(R.id.ingredients_recipe_name_widget, headerText);
            views.setViewVisibility(R.id.ingredients_recipe_name_widget, View.VISIBLE);
        }

        Intent intent = new Intent(context, IngredientGridWidgetService.class);
        views.setRemoteAdapter(R.id.widget_ingredients_grid_view, intent);
        Timber.tag(TAG).d("updateAppWidget: setRemoteAdapter() done");

        views.setEmptyView(R.id.widget_ingredients_grid_view, R.id.ingredients_empty_view);

        appWidgetManager.updateAppWidget(appWidgetId, views);

        Timber.tag(TAG).d("updateAppWidget: updateAppWidget() done");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Timber.tag(TAG).d("onUpdate: starts");
        IngredientsUpdateService.startActionUpdateIngredientsWidgets(context);
    }

    public static void updateIngredientsWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, String recipeName) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, recipeName);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

