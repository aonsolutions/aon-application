package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Category.CATEGORY;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.News.NEWS;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.UpdateSetMoreStep;

import com.esferalia.aon.jooq.tables.records.NewsRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.NewsFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.NewsProperties;
import com.esferalia.aon.occam.api.model.news.News;
import com.esferalia.aon.occam.api.model.news.NewsType;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.impl.jooq.dao.CategoryDAO.CategoryFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.DomainFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO.ScopeFiller;
import com.esferalia.aon.watson.server.AonDateUtils;

public class NewsDAO {

	private NewsDAO() {
		  throw new IllegalStateException("Utility class");
    }
	
	private static final NewsPropertiesDAO NEWS_PROPERTIES = new NewsPropertiesDAO();
	
	protected static class NewsPropertiesDAO implements NewsProperties {
		
		protected Select<Record> build(SelectJoinStep<Record> select, NewsFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(NewsFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(NEWS.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(NEWS.DOMAIN);}
		@Override public Property<String> getTitleProperty() {return new FilterDAO.PropertyDAO<>(NEWS.TITLE);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(NEWS.DESCRIPTION);}
		@Override public Property<String> getContentProperty() {return new FilterDAO.PropertyDAO<>(NEWS.CONTENT);}
		@Override public Property<String> getUrlProperty() {return new FilterDAO.PropertyDAO<>(NEWS.URL);}
		@Override public Property<Byte> getActiveProperty() {return new FilterDAO.PropertyDAO<>(NEWS.ACTIVE);}
		@Override public Property<Byte> getRssProperty() {return new FilterDAO.PropertyDAO<>(NEWS.RSS);}
		@Override public Property<Timestamp> getInitDateProperty(){return new FilterDAO.PropertyDAO<>(NEWS.INIT_DATE);}
		@Override public Property<Timestamp> getEndDateProperty() {return new FilterDAO.PropertyDAO<>(NEWS.END_DATE);}
		@Override public Property<Integer> getCategoryProperty() {return new FilterDAO.PropertyDAO<>(NEWS.CATEGORY);}
		@Override public Property<Integer> getRattachProperty() {return new FilterDAO.PropertyDAO<>(NEWS.RATTACH);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(NEWS.SCOPE);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(NEWS.TYPE);}
		@Override public Property<Integer> getTemplateProperty() {return new FilterDAO.PropertyDAO<>(NEWS.TEMPLATE);}
		
		@Override public Property<String> getCategoryNameProperty() {return new FilterDAO.PropertyDAO<>(CATEGORY.NAME);}
		@Override public Property<String> getScopeNameProperty() {return new FilterDAO.PropertyDAO<>(SCOPE.DESCRIPTION);}
	}
	
	public static Stream<News> getStream(AONContext ctx, NewsFilter filter) {
		return getStream(ctx, filter, Optional.empty(), Optional.empty());
	}
	
	public static Stream<News> getStream(AONContext ctx, NewsFilter filter, Integer page, Integer perPage) {
		return getStream(ctx, filter, Optional.of(page), Optional.of(perPage));
	}
	
	public static News get(AONContext ctx, NewsFilter filter) {
		return getStream(ctx, filter).findFirst().orElse(null);
	}

	public static News save(AONContext ctx, News news) {
		return news.getId() !=0 ? update(ctx, news) : insert(ctx, news);
	}
	
	public static void delete(AONContext ctx, Integer id){
		ctx.checkWrite();
		
		delete(ctx, f -> f.getIdProperty().eq(id));
		
		ctx.log().debug("DELETE NEWS id: " + id);	
	}
	
	private static Stream<News> getStream(AONContext ctx, NewsFilter filter, Optional<Integer> page, Optional<Integer> perPage){
		ctx.checkRead();
		SelectConditionStep<Record> condition = 
				ctx.getDslContext()
				.select()
				.from(NEWS)
				.innerJoin(DOMAIN).on(DOMAIN.ID.eq(NEWS.DOMAIN))
				.innerJoin(SCOPE).on(SCOPE.ID.eq(NEWS.SCOPE))
				.leftOuterJoin(CATEGORY).on(CATEGORY.ID.eq(NEWS.CATEGORY))
				.where(NEWS_PROPERTIES.getConditions(filter));

		if(page.isPresent() && perPage.isPresent()) {
			Integer per = perPage.get();
			Integer p = page.get();
			condition.limit(per).offset(per * (p -1));
		}
		
		return condition.fetchInto(NEWS).stream().map(new NewsFiller());
	}
	
	private static News insert(AONContext ctx, News news){
		ctx.checkWrite();

		InsertSetMoreStep<NewsRecord> sets = ctx.getDslContext()
				.insertInto(NEWS)
				.set(NEWS.DOMAIN, news.getDomain().getId())
				.set(NEWS.TITLE, news.getTitle())
				.set(NEWS.CONTENT, news.getContent())
				.set(NEWS.ACTIVE, news.isActive() ? (byte)1 : (byte)0)
				.set(NEWS.RSS, news.isRss() ? (byte)1 : (byte)0)
				.set(NEWS.SCOPE, news.getScope().getId())
				.set(NEWS.TYPE, news.getType().value())
				;
		news.getDescription().ifPresent(d-> sets.set(NEWS.DESCRIPTION, d));
		
		news.getUrl().ifPresent(d-> sets.set(NEWS.URL, d));
		
		news.getInitDate().ifPresent(d-> sets.set(NEWS.INIT_DATE, AonDateUtils.toTimestamp(d)));
		news.getEndDate().ifPresent(d-> sets.set(NEWS.END_DATE, AonDateUtils.toTimestamp(d)));
		
		if(news.getCategory()!=null &&news.getCategory().getId()!=null) {
			sets.set(NEWS.CATEGORY,news.getCategory().getId());
		}

		Integer id = sets.returning(NEWS.ID).fetchOne().getId();
				
		ctx.log().debug("INSERT NEWS id: " + id);		
		
		return news.setId(id);
	}
	
	private static News update(AONContext ctx, News news) {
		ctx.checkWrite();
		
		UpdateSetMoreStep<NewsRecord> sets = ctx.getDslContext()
		.update(NEWS)
		.set(NEWS.TITLE, news.getTitle())
		.set(NEWS.CONTENT, news.getContent())
		.set(NEWS.ACTIVE, (byte)(news.isRss() ? 1:0) )
		.set(NEWS.RSS, (byte)(news.isRss() ? 1:0) )
		.set(NEWS.SCOPE, news.getScope().getId())
		.set(NEWS.TYPE, news.getType().value())
		;
		
		news.getDescription().ifPresent(d-> sets.set(NEWS.DESCRIPTION, d));
		
		news.getUrl().ifPresent(d-> sets.set(NEWS.URL, d));
		
		news.getInitDate().ifPresent(d-> sets.set(NEWS.INIT_DATE, AonDateUtils.toTimestamp(d)));
		news.getEndDate().ifPresent(d-> sets.set(NEWS.END_DATE, AonDateUtils.toTimestamp(d)));
		
		if(news.getCategory()!=null &&news.getCategory().getId()!=null) {
			sets.set(NEWS.CATEGORY,news.getCategory().getId());
		}
		
		sets.where(NEWS.ID.eq(news.getId())).execute();

		ctx.log().debug("UPDATE NEWS id: " + news.getId());		
		return news;
	}
	
	private static void delete(AONContext ctx, NewsFilter filter) {
		ctx.checkWrite();
		ctx.getDslContext().delete(NEWS).where(NEWS_PROPERTIES.getConditions(filter)).execute();
	}
	
	public static class NewsFiller extends Filler implements Function<Record, News> {
		
		public News apply(Record r) {
			return build(r);
		}
	
		public static News build(Record r) {
			return new News()
					.setId(r.getValue(NEWS.ID))
					.setDomain(checkField(r, DOMAIN.ID) ? DomainFiller.build(r): new Domain().setId(r.getValue(NEWS.DOMAIN)))
					.setTitle(r.getValue(NEWS.TITLE))
					.setDescription(r.getValue(NEWS.DESCRIPTION))
					.setContent(r.getValue(NEWS.CONTENT))
					.setUrl(r.getValue(NEWS.URL))
					.setActive(r.getValue(NEWS.ACTIVE) ==(byte)1)
					.setRss(r.getValue(NEWS.RSS) ==(byte)1)
					.setInitDate(r.getValue(NEWS.INIT_DATE))
					.setEndDate(r.getValue(NEWS.END_DATE))
					.setCategory(checkField(r, CATEGORY.ID) ? CategoryFiller.build(r) : new Category() )
					.setScope(checkField(r, SCOPE.ID) ? ScopeFiller.buildScope(r) : new Scope())
					.setType(NewsType.safeValueOf(r.getValue(NEWS.TYPE)))
					;
		}
	}	
}
