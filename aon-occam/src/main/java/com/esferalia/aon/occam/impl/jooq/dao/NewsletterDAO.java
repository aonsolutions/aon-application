package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Newsletter.NEWSLETTER;
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

import com.esferalia.aon.jooq.tables.records.NewsletterRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.NewsletterFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Newsletter;
import com.esferalia.aon.occam.api.model.Properties.NewsletterProperties;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.DomainFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO.ScopeFiller;
import com.esferalia.aon.watson.server.AonDateUtils;

public class NewsletterDAO {

	private NewsletterDAO() {
		  throw new IllegalStateException("Utility class");
    }
	
	private static final NewsletterPropertiesDAO NEWSLETTER_PROPERTIES = new NewsletterPropertiesDAO();
	
	protected static class NewsletterPropertiesDAO implements NewsletterProperties {
		
		protected Select<Record> build(SelectJoinStep<Record> select, NewsletterFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(NewsletterFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(NEWSLETTER.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(NEWSLETTER.DOMAIN);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(NEWSLETTER.NAME);}
		@Override public Property<Timestamp> getDateProperty() {return new FilterDAO.PropertyDAO<>(NEWSLETTER.DATE);}
		@Override public Property<Byte> getLayoutProperty() {return new FilterDAO.PropertyDAO<>(NEWSLETTER.LAYOUT);}
		@Override public Property<Byte> getActiveProperty() {return new FilterDAO.PropertyDAO<>(NEWSLETTER.ACTIVE);}
		@Override public Property<String> getSubjectProperty() {return new FilterDAO.PropertyDAO<>(NEWSLETTER.SUBJECT);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(NEWSLETTER.SCOPE);}
		@Override public Property<String> getScopeNameProperty() {return new FilterDAO.PropertyDAO<>(SCOPE.DESCRIPTION);}
		@Override public Property<Byte> getHighlightFirstProperty() {return new FilterDAO.PropertyDAO<>(NEWSLETTER.HIGHLIGHTFIRST);}
		@Override public Property<Integer> getTemplateProperty() {return new FilterDAO.PropertyDAO<>(NEWSLETTER.TEMPLATE);}
		@Override public Property<Byte> getNewsSeparatorProperty() {return new FilterDAO.PropertyDAO<>(NEWSLETTER.NEWSSEPARATOR);}
	}
	
	public static Stream<Newsletter> getStream(AONContext ctx, NewsletterFilter filter) {
		return getStream(ctx, filter, Optional.empty(), Optional.empty());
	}
	
	public static Stream<Newsletter> getStream(AONContext ctx, NewsletterFilter filter, Integer page, Integer perPage) {
		return getStream(ctx, filter, Optional.of(page), Optional.of(perPage));
	}
	
	public static Newsletter get(AONContext ctx, NewsletterFilter filter) {
		return getStream(ctx, filter).findFirst().orElse(null);
	}

	public static Newsletter save(AONContext ctx, Newsletter newsletter) {
		return newsletter.getId() !=0 ? update(ctx, newsletter) : insert(ctx, newsletter);
	}
	
	public static void delete(AONContext ctx, Newsletter newsletter){
		ctx.checkWrite();
		
		Integer id = newsletter.getId();
		
		delete(ctx, f -> f.getIdProperty().eq(id));
		
		ctx.log().debug("DELETE NEWSLETTER id: " + id);	
	}
	
	private static Stream<Newsletter> getStream(AONContext ctx, NewsletterFilter filter, Optional<Integer> page, Optional<Integer> perPage){
		ctx.checkRead();
		SelectConditionStep<Record> condition = 
				ctx.getDslContext()
				.select()
				.from(NEWSLETTER)
				.innerJoin(DOMAIN).on(DOMAIN.ID.eq(NEWSLETTER.DOMAIN))
				.innerJoin(SCOPE).on(SCOPE.ID.eq(NEWSLETTER.SCOPE))
				.where(NEWSLETTER_PROPERTIES.getConditions(filter));

		if(page.isPresent() && perPage.isPresent()) {
			Integer per = perPage.get();
			Integer p = page.get();
			condition.limit(per).offset(per * (p -1));
		}
		
		return condition.fetch().stream().map(new NewsletterFiller());
	}
	
	private static Newsletter insert(AONContext ctx, Newsletter newsletter){
		ctx.checkWrite();
		
		InsertSetMoreStep<NewsletterRecord> sets = ctx.getDslContext()
				.insertInto(NEWSLETTER)
				.set(NEWSLETTER.DOMAIN, newsletter.getDomain().getId())
				.set(NEWSLETTER.NAME, newsletter.getName())
				.set(NEWSLETTER.DATE, AonDateUtils.toTimestamp(newsletter.getDate()))
				.set(NEWSLETTER.LAYOUT, newsletter.getLayout())
				.set(NEWSLETTER.ACTIVE, newsletter.getActive())
				.set(NEWSLETTER.SUBJECT, newsletter.getSubject())
				.set(NEWSLETTER.SCOPE, newsletter.getScope().getId())
				.set(NEWSLETTER.HIGHLIGHTFIRST, newsletter.getHighlightFirst())
				.set(NEWSLETTER.TEMPLATE, newsletter.getTemplate())
				.set(NEWSLETTER.NEWSSEPARATOR, newsletter.getNewsSeparator())
				;
		
		Integer id = sets.returning(NEWSLETTER.ID).fetchOne().getId();
				
		ctx.log().debug("INSERT NEWSLETTER id: " + id);		
		
		return newsletter.setId(id);
	}
	
	private static Newsletter update(AONContext ctx, Newsletter newsletter) {
		ctx.checkWrite();
		ctx.getDslContext()
		.update(NEWSLETTER)
			.set(NEWSLETTER.NAME, newsletter.getName())
			.set(NEWSLETTER.DATE, AonDateUtils.toTimestamp(newsletter.getDate()))
			.set(NEWSLETTER.LAYOUT, newsletter.getLayout())
			.set(NEWSLETTER.ACTIVE, newsletter.getActive())
			.set(NEWSLETTER.SUBJECT, newsletter.getSubject())
			.set(NEWSLETTER.SCOPE, newsletter.getScope().getId())
			.set(NEWSLETTER.HIGHLIGHTFIRST, newsletter.getHighlightFirst())
			.set(NEWSLETTER.TEMPLATE, newsletter.getTemplate())
			.set(NEWSLETTER.NEWSSEPARATOR, newsletter.getNewsSeparator())
			.where(NEWSLETTER.ID.eq(newsletter.getId()))
			.execute()
		;

		ctx.log().debug("NEWSLETTER NEWS id: " + newsletter.getId());		
		return newsletter;
	}
	
	private static void delete(AONContext ctx, NewsletterFilter filter) {
		ctx.checkWrite();
		ctx.getDslContext().delete(NEWSLETTER).where(NEWSLETTER_PROPERTIES.getConditions(filter)).execute();
	}
	
	public static class NewsletterFiller extends Filler implements Function<Record, Newsletter> {
		
		public Newsletter apply(Record r) {
			return build(r);
		}
	
		public static Newsletter build(Record r) {
			return new Newsletter()
					.setId(r.getValue(NEWSLETTER.ID))
					.setDomain(checkField(r, DOMAIN.ID) ? DomainFiller.build(r): new Domain().setId(r.getValue(NEWSLETTER.DOMAIN)))
					.setName(r.getValue(NEWSLETTER.NAME))
					.setDate(r.getValue(NEWSLETTER.DATE))
					.setLayout(r.getValue(NEWSLETTER.LAYOUT))
					.setActive(r.getValue(NEWSLETTER.ACTIVE))
					.setSubject(r.getValue(NEWSLETTER.SUBJECT))
					.setScope(checkField(r, SCOPE.ID) ? ScopeFiller.buildScope(r) : new Scope())
					.setHighlightFirst(r.getValue(NEWSLETTER.HIGHLIGHTFIRST))
					.setTemplate(r.getValue(NEWSLETTER.TEMPLATE))
					.setNewsSeparator(r.getValue(NEWSLETTER.NEWSSEPARATOR))
					;
		}
	}	
}
