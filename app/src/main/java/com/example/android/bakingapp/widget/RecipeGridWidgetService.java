package com.example.android.bakingapp.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

import timber.log.Timber;

public class RecipeGridWidgetService extends RemoteViewsService {

    private static final String TAG = "GridWidgetService";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Timber.tag(TAG).d("onGetViewFactory: starts");
        return new RecipeGridRemoteViewsFactory(this.getApplicationContext());
    }

}
