package com.esferalia.aon.occam.impl.jooq;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.INews;
import com.esferalia.aon.occam.api.model.Filter.NewsFilter;
import com.esferalia.aon.occam.api.model.news.News;
import com.esferalia.aon.occam.impl.jooq.dao.NewsDAO;

public class NewsImpl implements INews {

	@Override
	public News getNews(AONContext ctx, NewsFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> NewsDAO.get(ctx, filter));
	}

	@Override
	public Stream<News> getNewsStream(AONContext ctx, NewsFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> NewsDAO.getStream(ctx, filter));
	}
	
	@Override
	public Stream<News> getNewsStream(AONContext ctx, NewsFilter filter, Integer page, Integer perPage) {
		return ctx.getDslContext().transactionResult(configuration -> NewsDAO.getStream(ctx, filter, page, perPage));
	}

	@Override
	public News saveNews(AONContext ctx, News news) {
		return ctx.getDslContext().transactionResult(configuration -> NewsDAO.save(ctx, news));
	}

	@Override
	public void deleteNews(AONContext ctx, Integer id) {
		ctx.getDslContext().transaction(configuration -> NewsDAO.delete(ctx, id));
	}
}
