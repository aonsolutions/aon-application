package com.esferalia.aon.gwt.document.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import antlr.StringUtils;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion;
import com.google.gwt.user.client.ui.SuggestOracle;

public class AonSuggestOracle extends SuggestOracle {
	List<String> list = new Vector<String>();

	@Override
	public void requestSuggestions(Request request, Callback callback) {
		String query = request.getQuery();
		Integer limit = request.getLimit();
		//Window.alert(query + " - " + Integer.toString(limit));
		List<String> words = new Vector<String>();
		for (String string : list) {
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
	        convertToFormattedSuggestions(query, words);

	    Response response = new Response(suggestions);
	    response.setMoreSuggestionsCount(numberTruncated);

	    callback.onSuggestionsReady(request, response);
	}

	public void add(String suggestion) {
		list.add(suggestion);
	}
	
	 private List<MultiWordSuggestion> convertToFormattedSuggestions(String query,
		      List<String> candidates) {
		    List<MultiWordSuggestion> suggestions = new ArrayList<MultiWordSuggestion>();
	        SafeHtmlBuilder accum = new SafeHtmlBuilder();

		    for (int i = 0; i < candidates.size(); i++) {
		      String candidate = candidates.get(i);
		      /*Integer pos = indexOf(candidate, query);
		      if(pos.equals(0)){
		    	  String part1 = candidate.substring(0,query.length());
		    	  String part2 = candidate.substring(query.length());
		          accum.appendHtmlConstant("<strong>");
		          accum.appendEscaped(part1);
		          accum.appendHtmlConstant("</strong>");
		          accum.appendEscaped(part2);
		      }
		      else{
		    	  String part1 = candidate.substring(0,pos);
		    	  String part2 = candidate.substring(pos,pos+query.length());
		    	  String part3 ="";
		    	  if(candidate.length()>pos+query.length()) part3 = candidate.substring(pos+query.length());
		          accum.appendEscaped(part1);
		          accum.appendHtmlConstant("<strong>");
		          accum.appendEscaped(part2);
		          accum.appendHtmlConstant("</strong>");
		          accum.appendEscaped(part3);

		      }*/
		      MultiWordSuggestion suggestion = new MultiWordSuggestion(candidate,candidate);//accum.toSafeHtml().asString());
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
	 private Integer indexOf(String str, String searchStr) {
	        if (str == null || searchStr == null) {
	            return -1;
	        }
	        
	        return str.indexOf(searchStr);
	    }

}
