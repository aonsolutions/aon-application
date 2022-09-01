package net.aonsolutions.aon.api.servlet.news;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.NewsProperties;
import com.esferalia.aon.occam.api.model.news.NewsType;

import net.aonsolutions.aon.api.ewok.AonApiData;

public class NewsFilter {

	private NewsFilter() {
	    throw new IllegalStateException("Utility class");
	}
	
	public static Filter filter(AonApiData api, NewsProperties f, Domain domain) {
		JSONObject params  = api.getData();
		String search      = params.optString(IJsonNames.SEARCH);
	
		Filter filter      = f.getDomainProperty().eq(domain.getId()).and(f.getTypeProperty().eq(NewsType.COMMUNICATION.value()));
		
		/*if(!search.isEmpty()) {
			System.out.println(search);
			filter = filter.and(getSearchCombination(f, search));
		}*/

		return filter;
	}	
	
	private static Filter getSearchCombination(NewsProperties f, String search) {
		Filter joinSequence = null;
		String[] comb = search.trim().split("\\s");
		
		for (String word : comb) {
			StringBuilder processed = new StringBuilder("%").append(word).append("%");
			if(joinSequence == null) {
				joinSequence = getSearch(f, processed.toString()); 
			} else {
				joinSequence = joinSequence.and(getSearch(f, processed.toString()));		
			}
		}			
		return joinSequence;
	}
	
	private static Filter getSearch(NewsProperties f, String search) {
		return f.getDescriptionProperty().like(search)
		.or(f.getTitleProperty().like(search))
		.or(f.getContentProperty().like(search)) 
		.or(f.getUrlProperty().like(search))
		.or(f.getCategoryNameProperty().like(search))
		.or(f.getScopeNameProperty().like(search))
		;
	}	
}
