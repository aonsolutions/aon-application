package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Survey.SURVEY;

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

import com.esferalia.aon.jooq.tables.records.SurveyRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.SurveyFilter;
import com.esferalia.aon.occam.api.model.Properties.SurveyProperties;
import com.esferalia.aon.occam.api.model.Survey;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.DomainFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO.ScopeFiller;
import com.esferalia.aon.watson.server.AonDateUtils;

public class SurveyDAO {

	private SurveyDAO() {
		  throw new IllegalStateException("Utility class");
    }
	
	private static final SurveyPropertiesDAO SURVEY_PROPERTIES = new SurveyPropertiesDAO();
	
	protected static class SurveyPropertiesDAO implements SurveyProperties {
		
		protected Select<Record> build(SelectJoinStep<Record> select, SurveyFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(SurveyFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(SURVEY.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(SURVEY.DOMAIN);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(SURVEY.SCOPE);}
		@Override public Property<String> getScopeNameProperty() {return new FilterDAO.PropertyDAO<>(SCOPE.DESCRIPTION);}
		@Override public Property<Byte> getActiveProperty() {return new FilterDAO.PropertyDAO<>(SURVEY.ACTIVE);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(SURVEY.CREATIONDATE);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(SURVEY.DESCRIPTION);}
	}
	
	public static Stream<Survey> getStream(AONContext ctx, SurveyFilter filter) {
		return getStream(ctx, filter, Optional.empty(), Optional.empty());
	}
	
	public static Stream<Survey> getStream(AONContext ctx, SurveyFilter filter, Integer page, Integer perPage) {
		return getStream(ctx, filter, Optional.of(page), Optional.of(perPage));
	}
	
	public static Survey get(AONContext ctx, SurveyFilter filter) {
		return getStream(ctx, filter).findFirst().orElse(null);
	}

	public static Survey save(AONContext ctx, Survey survey) {
		return survey.getId() !=0 ? update(ctx, survey) : insert(ctx, survey);
	}
	
	public static void delete(AONContext ctx, Survey survey){
		ctx.checkWrite();
		
		Integer id = survey.getId();
		
		delete(ctx, f -> f.getIdProperty().eq(id));
		
		ctx.log().debug("DELETE SURVEY id: " + id);	
	}
	
	private static Stream<Survey> getStream(AONContext ctx, SurveyFilter filter, Optional<Integer> page, Optional<Integer> perPage){
		ctx.checkRead();
		SelectConditionStep<Record> condition = 
				ctx.getDslContext()
				.select()
				.from(SURVEY)
				.innerJoin(DOMAIN).on(DOMAIN.ID.eq(SURVEY.DOMAIN))
				.innerJoin(SCOPE).on(SCOPE.ID.eq(SURVEY.SCOPE))
				.where(SURVEY_PROPERTIES.getConditions(filter));

		if(page.isPresent() && perPage.isPresent()) {
			Integer per = perPage.get();
			Integer p = page.get();
			condition.limit(per).offset(per * (p -1));
		}
		
		return condition.fetch().stream().map(new SurveyFiller());
	}
	
	private static Survey insert(AONContext ctx, Survey survey){
		ctx.checkWrite();
		
		InsertSetMoreStep<SurveyRecord> sets = ctx.getDslContext()
				.insertInto(SURVEY)
				.set(SURVEY.DOMAIN, survey.getDomain().getId())
				.set(SURVEY.SCOPE, survey.getScope().getId())
				.set(SURVEY.ACTIVE, survey.isActive() ? (byte)1 : (byte)0)
				.set(SURVEY.CREATIONDATE, AonDateUtils.toTimestamp(survey.getCreationDate()))
				.set(SURVEY.DESCRIPTION, survey.getDescription())
				;
		
		Integer id = sets.returning(SURVEY.ID).fetchOne().getId();
				
		ctx.log().debug("INSERT SURVEY id: " + id);		
		
		return survey.setId(id);
	}
	
	private static Survey update(AONContext ctx, Survey survey) {
		ctx.checkWrite();
		ctx.getDslContext()
		.update(SURVEY)
			.set(SURVEY.SCOPE, survey.getScope().getId())
			.set(SURVEY.ACTIVE, survey.isActive() ? (byte)1 : (byte)0)
			.set(SURVEY.CREATIONDATE, AonDateUtils.toTimestamp(survey.getCreationDate()))
			.set(SURVEY.DESCRIPTION, survey.getDescription())
			.where(SURVEY.ID.eq(survey.getId()))
			.execute()
		;

		ctx.log().debug("NEWSLETTER SURVEY id: " + survey.getId());		
		return survey;
	}
	
	private static void delete(AONContext ctx, SurveyFilter filter) {
		ctx.checkWrite();
		ctx.getDslContext().delete(SURVEY).where(SURVEY_PROPERTIES.getConditions(filter)).execute();
	}
	
	public static class SurveyFiller extends Filler implements Function<Record, Survey> {
		
		public Survey apply(Record r) {
			return build(r);
		}
	
		public static Survey build(Record r) {
			return new Survey()
					.setId(r.getValue(SURVEY.ID))
					.setDomain(checkField(r, DOMAIN.ID) ? DomainFiller.build(r): new Domain().setId(r.getValue(SURVEY.DOMAIN)))
					.setScope(checkField(r, SCOPE.ID) ? ScopeFiller.buildScope(r) : new Scope())
					.setActive(r.getValue(SURVEY.ACTIVE) == 1)
					.setCreationDate(r.getValue(SURVEY.CREATIONDATE))
					.setDescription(r.getValue(SURVEY.DESCRIPTION))
					;
		}
	}	
}
