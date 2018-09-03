package com.kidozh.npuhelper.campusBuildingLoc;

import android.annotation.SuppressLint;
import android.content.SearchRecentSuggestionsProvider;

@SuppressLint("Registered")
public class campusBuildingSearchSuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "CAMPUS_BUILDING_SEARCH_AUTHORITY";

    public final static int MODE = DATABASE_MODE_QUERIES;

    public campusBuildingSearchSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }

}
