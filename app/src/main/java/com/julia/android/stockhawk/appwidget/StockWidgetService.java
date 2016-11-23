package com.julia.android.stockhawk.appwidget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * WidgetService is the {@link RemoteViewsService} that will return our RemoteViewsFactory
 */
public class StockWidgetService extends RemoteViewsService{
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StockWidgetDataProvider(this);
    }
}
