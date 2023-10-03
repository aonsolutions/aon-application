package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Question.QUESTION;
import static com.esferalia.aon.jooq.tables.QuestionValue.QUESTION_VALUE;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rprofile.RPROFILE;
import static com.esferalia.aon.jooq.tables.Survey.SURVEY;
import static com.esferalia.aon.jooq.tables.SurveyQuestion.SURVEY_QUESTION;

import java.sql.Timestamp;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Record;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.QuestionFilter;
import com.esferalia.aon.occam.api.model.Properties.QuestionProperties;
import com.esferalia.aon.occam.api.model.Question;
import com.esferalia.aon.occam.api.model.QuestionParams;
import com.esferalia.aon.occam.api.model.QuestionValue;
import com.esferalia.aon.occam.api.model.registry.QuestionType;
import com.esferalia.aon.occam.impl.jooq.validation.QuestionValidation;
import com.esferalia.aon.watson.util.AonStringUtils;

public class QuestionDAO {
	
	private QuestionDAO() {}

	private static final  QuestionPropertiesDAO QUESTION_PROPERTIES = new QuestionPropertiesDAO();

	protected static class QuestionPropertiesDAO implements QuestionProperties {
		protected Condition[] getConditions(QuestionFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(QUESTION.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(QUESTION.DOMAIN);}
		@Override public Property<Byte> getActiveProperty() {return new FilterDAO.PropertyDAO<>(QUESTION.ACTIVE);}
		@Override public Property<String> getQuestionTextProperty() {return new FilterDAO.PropertyDAO<>(QUESTION.QUESTION_TEXT);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(QUESTION.TYPE);}
		@Override public Property<String> getArgumentProperty() {return new FilterDAO.PropertyDAO<>(QUESTION.ARGUMENT);}
		@Override public Property<String> getAliasProperty() {return new FilterDAO.PropertyDAO<>(QUESTION.ALIAS);}
	}
	
	public static Boolean checkAlias(CloseableAONContext ctx, String alias) {
		List<Record> questionRecords = ctx.getDslContext().select().from(QUESTION)
				.where(QUESTION.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
				.and(QUESTION.ALIAS.eq(alias))
				.fetch();
		
		return !questionRecords.isEmpty();
	}

	public static Question get(CloseableAONContext ctx, Integer id) {
		Record questionRecord = ctx.getDslContext().select().from(QUESTION)
				.where(QUESTION.ID.eq(id))
				.fetchOne();
		
		if(null == questionRecord) return null;
		
		Question question = new QuestionFiller().apply(questionRecord);
		getQuestionValues(ctx, question);
		hasSurvey(ctx, question);
		hasRProfile(ctx, question);
		
		return question;
	}
	
	public static List<Question> getList(CloseableAONContext ctx, QuestionParams params) {
		Condition condition = paramsToCondition(ctx, params);
		
		List<Question> questions = ctx.getDslContext().select().from(QUESTION)
			.where(condition)
			.fetch()
			.stream()
			.map(new QuestionFiller())
			.collect(Collectors.toList());
		
		questions.forEach(question -> {
			getQuestionValues(ctx, question);
			hasSurvey(ctx, question);
			hasRProfile(ctx, question);
		});
		
		return questions;
	}	
	
	private static void hasSurvey(CloseableAONContext ctx, Question question) {
		List<String> surveyRecords = ctx.getDslContext().selectDistinct(SURVEY.DESCRIPTION).from(SURVEY)
			.join(SURVEY_QUESTION)
			.on(SURVEY_QUESTION.SURVEY.eq(SURVEY.ID))
			.where(SURVEY_QUESTION.QUESTION.eq(question.getId()))
			.fetch(SURVEY.DESCRIPTION);
		
		question.setHasSurvey(!surveyRecords.isEmpty());
		question.setSurveyDescriptions(surveyRecords);
	}
	
	private static void hasRProfile(CloseableAONContext ctx, Question question) {
		List<String> rprofileRecords = ctx.getDslContext().selectDistinct(REGISTRY.NAME).from(REGISTRY)
			.join(RPROFILE)
			.on(RPROFILE.REGISTRY.eq(REGISTRY.ID))
			.where(RPROFILE.QUESTION.eq(question.getId()))
			.fetch(REGISTRY.NAME);
		
		question.setRprofile(!rprofileRecords.isEmpty());
		question.setRprofileNames(rprofileRecords);
	}

	private static void getQuestionValues(CloseableAONContext ctx, Question question) {
		List<QuestionValue> questionValues = ctx.getDslContext().select().from(QUESTION_VALUE)
		.where(QUESTION_VALUE.QUESTION.eq(question.getId()))
		.fetch()
		.stream()
		.map(new QuestionValueFiller().andThen(qtv -> qtv.setQuestion(question)))
		.collect(Collectors.toList());
		
		question.setValues(questionValues);
	}

	private static Condition paramsToCondition(CloseableAONContext ctx, QuestionParams params) {
		Condition condition = QUESTION.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx));
		
		if(AonStringUtils.isNotBlank(params.getAlias()))
			condition = condition.and(QUESTION.ALIAS.like("%" + params.getAlias() + "%"));
		
		if(null != params.getType())
			condition = condition.and(QUESTION.TYPE.eq(params.getType()));
		
		if(null != params.getActive())
			condition = condition.and(QUESTION.ACTIVE.eq(params.getActive()));
		
		return condition;
	}

	public static Question save(AONContext ctx, Question question) {
		QuestionValidation.validate(ctx, question);
		return question.getId() != null
				? update(ctx, question)
				: insert(ctx, question);
	}
	
	public static Question insert(AONContext ctx, Question question) {
		Integer id = ctx.getDslContext().insertInto(QUESTION)
				.set(QUESTION.DOMAIN, question.getDomain())
				.set(QUESTION.ACTIVE, question.isActive() ? (byte)1 : (byte)0)
				.set(QUESTION.QUESTION_TEXT, question.getText())
				.set(QUESTION.TYPE, (byte) question.getType().ordinal())
				.set(QUESTION.ARGUMENT, question.getArgument())
				.set(QUESTION.ALIAS, question.getAlias())
				.returning(QUESTION.ID)
				.fetchOne()
				.getValue(QUESTION.ID);
		
		ctx.log().debug("INSERT QUESTION id: " +id);
		
		question.setId(id);
		
		question.getValues().forEach(qtv -> qtv.setQuestion(question));
		
		save(ctx, question.getValues());
		
		return question;
	}
	
	public static Question update(AONContext ctx, Question question) {
		ctx.getDslContext().update(QUESTION)
		.set(QUESTION.DOMAIN, question.getDomain())
		.set(QUESTION.ACTIVE, question.isActive() ? (byte)1 : (byte)0)
		.set(QUESTION.QUESTION_TEXT, question.getText())
		.set(QUESTION.TYPE, (byte) question.getType().ordinal())
		.set(QUESTION.ARGUMENT, question.getArgument())
		.set(QUESTION.ALIAS, question.getAlias())
		.where(QUESTION.ID.eq(question.getId()))
		.execute();
		
		ctx.log().debug("UPDATE QUESTION id:" + question.getId());
		
		save(ctx, question.getValues());
		
		return question;
	}

	private static void save(AONContext ctx, List<QuestionValue> questionValues) {
		if(questionValues == null || questionValues.isEmpty()) return;
		
		questionValues.forEach(qtv -> {
			if(qtv.isDeleted()) delete(ctx, qtv);
			else if(qtv.getId() != null) update(ctx, qtv);
			else insert(ctx, qtv);
		});
	}

	private static void insert(AONContext ctx, QuestionValue qtv) {
		Integer id = ctx.getDslContext().insertInto(QUESTION_VALUE)
				.set(QUESTION_VALUE.DOMAIN, qtv.getDomain())
				.set(QUESTION_VALUE.QUESTION, qtv.getQuestion().getId())
				.set(QUESTION_VALUE.VALUE_TEXT, qtv.getValueText())
				.set(QUESTION_VALUE.VALUE_NUMBER, qtv.getValueNumber())
				.set(QUESTION_VALUE.VALUE_DATE, qtv.getValueDate() == null ? null : new Timestamp(qtv.getValueDate().getTime()))
				.returning(QUESTION_VALUE.ID)
				.fetchOne()
				.getValue(QUESTION_VALUE.ID);
		
		ctx.log().debug("INSERT QUESTION VALUE id: " +id);
		
		qtv.setId(id);
	}

	private static void update(AONContext ctx, QuestionValue qtv) {
		ctx.getDslContext().update(QUESTION_VALUE)
				.set(QUESTION_VALUE.DOMAIN, qtv.getDomain())
				.set(QUESTION_VALUE.QUESTION, qtv.getQuestion().getId())
				.set(QUESTION_VALUE.VALUE_TEXT, qtv.getValueText())
				.set(QUESTION_VALUE.VALUE_NUMBER, qtv.getValueNumber())
				.set(QUESTION_VALUE.VALUE_DATE, qtv.getValueDate() == null ? null : new Timestamp(qtv.getValueDate().getTime()))
				.where(QUESTION_VALUE.ID.eq(qtv.getId()))
				.execute();
		
		ctx.log().debug("UPDATE QUESTION VALUE id: " + qtv.getId());
	}
	


	private static void delete(AONContext ctx, QuestionValue qtv) {
		ctx.getDslContext().delete(QUESTION_VALUE)
			.where(QUESTION_VALUE.ID.eq(qtv.getId()))
			.and(QUESTION_VALUE.DOMAIN.eq(qtv.getDomain()))
			.execute();
		
		ctx.getDslContext().delete(RPROFILE)
		.where(RPROFILE.QUESTION.eq(qtv.getId()))
		.and(RPROFILE.DOMAIN.eq(qtv.getDomain()))
		.execute();
		
		ctx.getDslContext().delete(QUESTION)
			.where(QUESTION.ID.eq(qtv.getId()))
			.execute();
		
		ctx.log().debug("DELETE QUESTION VALUE id: " + qtv.getId());
	}

	public static void delete(AONContext ctx, Integer id) {
		ctx.getDslContext().delete(QUESTION_VALUE)
		.where(QUESTION_VALUE.QUESTION.eq(id))
		.and(QUESTION_VALUE.DOMAIN.eq(ctx.getDomainId()))
		.execute();
		
		ctx.getDslContext().delete(RPROFILE)
		.where(RPROFILE.QUESTION.eq(id))
		.and(RPROFILE.DOMAIN.eq(ctx.getDomainId()))
		.execute();
		
		ctx.getDslContext().delete(QUESTION)
		.where(QUESTION.ID.eq(id))
		.execute();
		
		ctx.log().debug("DELETE QUESTION id:" + id);
	}

	public static class QuestionFiller extends Filler implements Function<Record, Question> {

		@Override
		public Question apply(Record r) {
			return build(r);
		}

		public static Question build(Record r) {
			return new Question()
				.setId(r.getValue(QUESTION.ID))
				.setDomain(r.getValue(QUESTION.DOMAIN))
				.setActive(r.getValue(QUESTION.ACTIVE) == (byte)1)
				.setText(r.getValue(QUESTION.QUESTION_TEXT))
				.setType(QuestionType.safeValueOf(r.getValue(QUESTION.TYPE)))
				.setArgument(r.getValue(QUESTION.ARGUMENT))
				.setAlias(r.getValue(QUESTION.ALIAS))
				.setHasSurvey(false)
				.setRprofile(false);
		}
	}
	
	public static class QuestionValueFiller extends Filler implements Function<Record, QuestionValue> {

		@Override
		public QuestionValue apply(Record r) {
			return build(r);
		}

		public static QuestionValue build(Record r) {
			return new QuestionValue()
				.setId(r.getValue(QUESTION_VALUE.ID))
				.setDomain(r.getValue(QUESTION_VALUE.DOMAIN))
				.setValueText(r.getValue(QUESTION_VALUE.VALUE_TEXT))
				.setValueNumber(r.getValue(QUESTION_VALUE.VALUE_NUMBER))
				.setValueDate(r.getValue(QUESTION_VALUE.VALUE_DATE))
				;
		}
	}
}
