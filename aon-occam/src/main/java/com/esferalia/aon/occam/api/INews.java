package com.esferalia.aon.occam.api;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Filter.NewsFilter;
import com.esferalia.aon.occam.api.model.news.News;

public interface INews {
	
	public News getNews(AONContext ctx, NewsFilter filter);
	
	public Stream<News> getNewsStream(AONContext ctx, NewsFilter filter);	
	
	public Stream<News> getNewsStream(AONContext ctx, NewsFilter filter, Integer page, Integer perPage);	
	
	public News saveNews(AONContext ctx, News category);
	
	public void deleteNews(AONContext ctx, News news);
}
