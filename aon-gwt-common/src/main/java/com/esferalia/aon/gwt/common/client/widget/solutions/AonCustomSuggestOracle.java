package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion;
import com.google.gwt.user.client.ui.SuggestOracle;

public class AonCustomSuggestOracle extends SuggestOracle {
	private final List<String> data = new ArrayList<>();

    public void setData(List<String> items) {
        data.clear();
        data.addAll(items);
    }

    @Override
    public void requestSuggestions(Request request, Callback callback) {
        List<Suggestion> suggestions = new ArrayList<>();
        String query = request.getQuery().toLowerCase();

        for (String item : data) {
            if (AonStringUtils.isBlank(query) || AonStringUtils.containsIgnoreCase(item, query)) { // Aquí hacemos la búsqueda en cualquier parte
                suggestions.add(new MultiWordSuggestion(item, item));
            }
        }

        callback.onSuggestionsReady(request, new Response(suggestions));
    }
}
