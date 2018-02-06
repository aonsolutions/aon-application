package net.aonsolutions.aon.gwt.commercial.client.commission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion;
import com.google.gwt.user.client.ui.SuggestOracle;

public class AonSuggestOracleMap extends SuggestOracle {
	HashMap<String, Integer> map = new HashMap<>();
	
	@Override
	public void requestSuggestions(Request request, Callback callback) {
		String query = request.getQuery();
		if(query.contains(",") && query.contains("@")) {
			Integer pos = query.lastIndexOf(",");
			query = query.substring(pos+1);
		}
		Integer limit = request.getLimit();
		//Window.alert(query + " - " + Integer.toString(limit));
		List<String> words = new Vector<String>();
		for (String string : map.keySet()) {
			//Window.alert(string+" - "+query);
			if (containsIgnoreCase(string, query)) {
				words.add(string);
			}
		}
		//Window.alert(Integer.toString(list.size())+" - " +Integer.toString(words.size()));
	    // Respect limit for number of choices.
	    int numberTruncated = Math.max(0, words.size() - limit);
	    for (int i = words.size() - 1; i > limit; i--) {
	      words.remove(i);
	    }

	    // Convert candidates to suggestions.
	    List<MultiWordSuggestion> suggestions =
	        convertToFormattedSuggestions(query, words,request.getQuery());

	    Response response = new Response(suggestions);
	    response.setMoreSuggestionsCount(numberTruncated);
	    callback.onSuggestionsReady(request, response);
	}

	public void add(String key, Integer value) {
		map.put(key, value);	
	}
	
	 private List<MultiWordSuggestion> convertToFormattedSuggestions(String query,
		      List<String> candidates, String query2) {
		    List<MultiWordSuggestion> suggestions = new ArrayList<MultiWordSuggestion>();

		    for (int i = 0; i < candidates.size(); i++) {
		      String candidate = candidates.get(i);
		      MultiWordSuggestion suggestion;
		      if(!query.equals(query2)){
		    	  Integer pos = query2.lastIndexOf(',');
		    	  String s = query2.substring(0,pos+1)+candidate;
		    	  suggestion = new MultiWordSuggestion(s,candidate);//accum.toSafeHtml().asString());
		      }
		      else suggestion = new MultiWordSuggestion(candidate,candidate);//accum.toSafeHtml().asString());
		      suggestions.add(suggestion);
		    }
		    return suggestions;
		  }
	 
	 private  boolean containsIgnoreCase(String str, String searchStr) {
	        if (str == null || searchStr == null) {
	            return false;
	        }
	        int len = searchStr.length();
	        int max = str.length() - len;
	        for (int i = 0; i <= max; i++) {
	            if (str.regionMatches(true, i, searchStr, 0, len)) {
	                return true;
	            }
	        }
	        return false;
	  }
	
	 public HashMap<String, Integer> getMap(){
		 return map;
	 }
}
