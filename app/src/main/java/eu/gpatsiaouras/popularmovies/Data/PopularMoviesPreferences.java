package eu.gpatsiaouras.popularmovies.Data;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import eu.gpatsiaouras.popularmovies.R;

public final class PopularMoviesPreferences {

    public static String getListTypeSelected(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String popular = context.getString(R.string.pref_type_list_popular_value);
        String list_type_key = context.getString(R.string.pref_type_list_popular_value);
        return sp.getString(list_type_key, popular);
    }
}
