package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.news.News;
import com.esferalia.aon.occam.api.model.news.NewsType;

public class NewsJSON {
	
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
			.setInitDate(JsonUtils.getDate(json, "initDate"))
			.setEndDate(JsonUtils.getDate(json, "endDate"))
			.setType( NewsType.safeValueOf(json.optString(IJsonNames.TYPE))) 
			.setCategory(!json.optString(IJsonNames.CATEGORY).isEmpty() ? CategoryJSON.fromJSON(json.optJSONObject(IJsonNames.CATEGORY)) : null)
			.setScope(!json.optString(IJsonNames.SCOPE).isEmpty() ? ScopeJSON.fromJSON(json.optJSONObject(IJsonNames.SCOPE))  : null )
			.setRattach(json.optInt(IJsonNames.RATTACH)!=0 ? json.getInt(IJsonNames.RATTACH) : null )
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
			.put(IJsonNames.CONTENT, news.getContent())
			.put(IJsonNames.ACTIVE, news.isActive())
			.put(IJsonNames.RSS,  news.isRss())
			.put(IJsonNames.CATEGORY, news.getCategory()!=null ? CategoryJSON.toJSON(news.getCategory()) : null)
			.put(IJsonNames.TYPE, news.getType()!=null ? news.getType().getName() : null)
			.put(IJsonNames.SCOPE, news.getScope()!=null ? ScopeJSON.toJSON(news.getScope()) : null)
			;
		
		news.getInitDate().ifPresent(d-> json.put("initDate", d.getTime()));
		news.getEndDate().ifPresent(d-> json.put("endDate", d.getTime()));

		news.getUrl().ifPresent(d-> json.put(IJsonNames.URL, d));
		
		news.getDescription().ifPresent(d-> json.put(IJsonNames.DESCRIPTION, d));
		news.getRattach().ifPresent(d-> json.put(IJsonNames.RATTACH, d));
		
		return json;
	}

}
