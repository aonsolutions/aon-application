package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.news.News;
import com.esferalia.aon.occam.api.model.news.NewsType;
import com.esferalia.aon.watson.server.AonDateUtils;

public class NewsJSON {
	private static final String FORMAT_DATE = "yyyy-MM-dd"; 
	public static LinkedList<News> fromJSON(JSONArray json) {
		LinkedList<News> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static News fromJSON(JSONObject json) {
		return new News()
			.setId(json.optInt(IJsonNames.ID))
			.setDomain(DomainJSON.fromJSON(json.optJSONObject(IJsonNames.DOMAIN)))
			.setTitle(json.optString(IJsonNames.TITLE))
			.setDescription(json.optString(IJsonNames.DESCRIPTION))
			.setContent(json.optString(IJsonNames.CONTENT))
			.setUrl(json.optString(IJsonNames.URL))
			.setActive(json.optBoolean(IJsonNames.ACTIVE))
			.setRss(json.optBoolean(IJsonNames.RSS))
			.setInitDate(!json.optString("initDate").isEmpty() ? AonDateUtils.parse(json.optString("initDate"), FORMAT_DATE) : null)
			.setEndDate(!json.optString("endDate").isEmpty() ? AonDateUtils.parse(json.optString("endDate"), FORMAT_DATE) : null)
			.setType( NewsType.safeValueOf(json.optString(IJsonNames.TYPE))) 
			.setCategory(!json.optString(IJsonNames.CATEGORY).isEmpty() ? CategoryJSON.fromJSON(json.optJSONObject(IJsonNames.CATEGORY)) : null)
			.setScope(!json.optString(IJsonNames.SCOPE).isEmpty() ? ScopeJSON.fromJSON(json.optJSONObject(IJsonNames.SCOPE))  : null )
			;
	}
	
	public static JSONArray toJSON(LinkedList<News> news) {
		return toJSON(news.stream());
	}
	
	public static JSONArray toJSON(List<News> news) {
		JSONArray array = new JSONArray();
		news.forEach(t -> array.put(toJSON(t)));
		return array;
	}
	
	public static JSONArray toJSON(Stream<News> news) {
		JSONArray array = new JSONArray();
		news.forEach(t -> array.put(toJSON(t)));
		return array;
	}
	
	public static JSONObject toJSON(News news) {
		if(news==null) return new JSONObject();
		JSONObject json = new JSONObject()
			.put(IJsonNames.ID, news.getId())
			.put(IJsonNames.DOMAIN, DomainJSON.toJSON(news.getDomain()))
			.put(IJsonNames.TITLE, news.getTitle())
			.put(IJsonNames.DESCRIPTION, news.getDescription())
			.put(IJsonNames.CONTENT, news.getContent())
			.put(IJsonNames.URL, news.getUrl())
			.put(IJsonNames.ACTIVE, news.isActive())
			.put(IJsonNames.RSS,  news.isRss())
			.put(IJsonNames.CATEGORY, news.getCategory()!=null ? CategoryJSON.toJSON(news.getCategory()) : null)
			.put(IJsonNames.TYPE, news.getType()!=null ? news.getType().getName() : null)
			.put(IJsonNames.SCOPE, news.getScope()!=null ? ScopeJSON.toJSON(news.getScope()) : null)
			;
		
		news.getInitDate().ifPresent(d-> json.put("init_date", d.getTime()));
		news.getEndDate().ifPresent(d-> json.put("end_date", d.getTime()));
		
		return json;
	}

}
