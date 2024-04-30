package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.MkAction.MK_ACTION;
import static com.esferalia.aon.jooq.tables.MkActionTarget.MK_ACTION_TARGET;
import static com.esferalia.aon.jooq.tables.MkCampaign.MK_CAMPAIGN;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Survey.SURVEY;
import static com.esferalia.aon.jooq.tables.Target.TARGET;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.sql.Timestamp;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Record;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Filter.MarketingCampaignFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.MarketingAction;
import com.esferalia.aon.occam.api.model.MarketingAction.MarketingActionMediaType;
import com.esferalia.aon.occam.api.model.MarketingActionParams;
import com.esferalia.aon.occam.api.model.MarketingActionTarget;
import com.esferalia.aon.occam.api.model.MarketingActionTargetParams;
import com.esferalia.aon.occam.api.model.MarketingCampaign;
import com.esferalia.aon.occam.api.model.MarketingCompaignParams;
import com.esferalia.aon.occam.api.model.Properties.MarketingCampaignProperties;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO.ScopeFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SurveyDAO.SurveyFiller;
import com.esferalia.aon.occam.impl.jooq.dao.TargetDAO.TargetFiller;
import com.esferalia.aon.occam.impl.jooq.dao.UserDAO.UserFiller;
import com.esferalia.aon.occam.impl.jooq.validation.MarketingActionTargetValidation;
import com.esferalia.aon.occam.impl.jooq.validation.MarketingActionValidation;
import com.esferalia.aon.occam.impl.jooq.validation.MarketingCampaignValidation;
import com.esferalia.aon.watson.util.AonStringUtils;

public class MarketingCampaignDAO {
	
	private MarketingCampaignDAO() {}

	private static final  MarketingCampaignPropertiesDAO MARKETING_CAMPAIGN_PROPERTIES = new MarketingCampaignPropertiesDAO();

	protected static class MarketingCampaignPropertiesDAO implements MarketingCampaignProperties {
		protected Condition[] getConditions(MarketingCampaignFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(MK_CAMPAIGN.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(MK_CAMPAIGN.DOMAIN);}
		@Override public Property<Byte> getActiveProperty() {return new FilterDAO.PropertyDAO<>(MK_CAMPAIGN.ACTIVE);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(MK_CAMPAIGN.DESCRIPTION);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(MK_CAMPAIGN.SCOPE);}
	}
	

	public static MarketingCampaign get(CloseableAONContext ctx, Integer id) {
		Record marketingCampaignRecord = ctx.getDslContext().select().from(MK_CAMPAIGN)
				.leftOuterJoin(SCOPE)
				.on(SCOPE.ID.eq(MK_CAMPAIGN.SCOPE))
				.where(MK_CAMPAIGN.ID.eq(id))
				.fetchOne();
		
		if(null == marketingCampaignRecord) return null;
		
		MarketingCampaign marketingCampaign = new MarketingCampaignFiller().apply(marketingCampaignRecord);
		
		getMarketingCampaignActions(ctx, marketingCampaign);
		
		return marketingCampaign;
	}

	public static List<MarketingCampaign> getList(CloseableAONContext ctx, MarketingCompaignParams params) {
		Condition condition = paramsToCondition(ctx, params);
		
		List<MarketingCampaign> marketingCampaigns = ctx.getDslContext().select().from(MK_CAMPAIGN)
			.leftOuterJoin(SCOPE)
			.on(SCOPE.ID.eq(MK_CAMPAIGN.SCOPE))
			.where(condition)
			.limit(params.getOffset(), params.getLimit())
			.fetch()
			.stream()
			.map(new MarketingCampaignFiller())
			.collect(Collectors.toList());
		
		marketingCampaigns.forEach(marketingCampaign -> getMarketingCampaignActions(ctx, marketingCampaign));
		
		return marketingCampaigns;
	}	
	
	private static void getMarketingCampaignActions(CloseableAONContext ctx, MarketingCampaign marketingCampaign) {
		List<MarketingAction> marketingActions = ctx.getDslContext().select().from(MK_ACTION)
				.leftOuterJoin(SURVEY)
				.on(SURVEY.ID.eq(MK_ACTION.SURVEY))
				.leftOuterJoin(SCOPE)
				.on(SCOPE.ID.eq(SURVEY.SCOPE))
				.where(MK_ACTION.DOMAIN.eq(marketingCampaign.getDomain()))
				.and(MK_ACTION.CAMPAIGN.eq(marketingCampaign.getId()))
				.fetch()
				.stream()
				.map(new MarketingActionFiller(marketingCampaign))
				.collect(Collectors.toList());
		
		marketingCampaign.setActions(marketingActions);
	}

	private static Condition paramsToCondition(CloseableAONContext ctx, MarketingCompaignParams params) {
		Condition condition = MK_CAMPAIGN.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx));
		
		if(AonStringUtils.isNotBlank(params.getDescription()))
			condition = condition.and(MK_CAMPAIGN.DESCRIPTION.like("%" + params.getDescription() + "%"));
		
		if(null != params.getScope())
			condition = condition.and(MK_CAMPAIGN.SCOPE.eq(params.getScope()));
		
		if(null != params.getActive())
			condition = condition.and(MK_CAMPAIGN.ACTIVE.eq(params.getActive()));
		
		return condition;
	}

	public static MarketingCampaign save(AONContext ctx, MarketingCampaign marketingCampaign) {
		MarketingCampaignValidation.validate(ctx, marketingCampaign);
		return marketingCampaign.getId() != null
				? update(ctx, marketingCampaign)
				: insert(ctx, marketingCampaign);
	}
	
	public static MarketingCampaign insert(AONContext ctx, MarketingCampaign marketingCampaign) {
		Integer id = ctx.getDslContext().insertInto(MK_CAMPAIGN)
				.set(MK_CAMPAIGN.DOMAIN, marketingCampaign.getDomain())
				.set(MK_CAMPAIGN.ACTIVE, marketingCampaign.isActive() ? (byte)1 : (byte)0)
				.set(MK_CAMPAIGN.DESCRIPTION, marketingCampaign.getDescription())
				.set(MK_CAMPAIGN.SCOPE, marketingCampaign.getScope() == null ? null : marketingCampaign.getScope().getId())
				.returning(MK_CAMPAIGN.ID)
				.fetchOne()
				.getValue(MK_CAMPAIGN.ID);
		
		ctx.log().debug("INSERT MK_CAMPAIGN id: " +id);
		
		marketingCampaign.setId(id);
		
		return marketingCampaign;
	}
	
	public static MarketingCampaign update(AONContext ctx, MarketingCampaign marketingCampaign) {
		ctx.getDslContext().update(MK_CAMPAIGN)
		.set(MK_CAMPAIGN.DOMAIN, marketingCampaign.getDomain())
		.set(MK_CAMPAIGN.ACTIVE, marketingCampaign.isActive() ? (byte)1 : (byte)0)
		.set(MK_CAMPAIGN.DESCRIPTION, marketingCampaign.getDescription())
		.set(MK_CAMPAIGN.SCOPE, marketingCampaign.getScope() == null ? null : marketingCampaign.getScope().getId())
		.where(MK_CAMPAIGN.ID.eq(marketingCampaign.getId()))
		.execute();
		
		ctx.log().debug("UPDATE MK_CAMPAIGN id:" + marketingCampaign.getId());
		
		return marketingCampaign;
	}

	public static void delete(AONContext ctx, Integer id) {
		ctx.getDslContext().delete(MK_CAMPAIGN)
		.where(MK_CAMPAIGN.ID.eq(id))
		.execute();
		
		ctx.log().debug("DELETE MK_CAMPAIGN id:" + id);
	}
	
	
	// MARKETING ACTION

	public static List<MarketingAction> getActionList(CloseableAONContext ctx, MarketingActionParams params) {
		Condition condition = actionParamsToCondition(ctx, params);
		
		List<MarketingAction> marketingActions = ctx.getDslContext().select().from(MK_ACTION)
			.leftOuterJoin(SURVEY)
			.on(SURVEY.ID.eq(MK_ACTION.SURVEY))
			.leftOuterJoin(SCOPE)
			.on(SCOPE.ID.eq(SURVEY.SCOPE))
			.where(condition)
			.limit(params.getOffset(), params.getLimit())
			.fetch()
			.stream()
			.map(new MarketingActionFiller(params.getMarketingCampaign()))
			.collect(Collectors.toList());
		
		marketingActions.forEach(marketingAction -> getMarketingActionTargets(ctx, marketingAction));
		
		return marketingActions;
	}
	
	public static MarketingAction getAction(CloseableAONContext ctx, Integer id) {
		Record marketingActionRecord = ctx.getDslContext().select().from(MK_ACTION)
			.leftOuterJoin(SURVEY)
			.on(SURVEY.ID.eq(MK_ACTION.SURVEY))
			.leftOuterJoin(SCOPE)
			.on(SCOPE.ID.eq(SURVEY.SCOPE))
			.where(MK_ACTION.ID.eq(id))
			.fetchOne();
		
		MarketingAction marketingAction = MarketingActionFiller.build(marketingActionRecord);
			
		getMarketingActionTargets(ctx, marketingAction);
		
		return marketingAction;
	}

	private static Condition actionParamsToCondition(CloseableAONContext ctx, MarketingActionParams params) {
		Condition condition = MK_ACTION.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx));
		
		if(AonStringUtils.isNotBlank(params.getDescription()))
			condition = condition.and(MK_ACTION.DESCRIPTION.like("%" + params.getDescription() + "%"));
		
		if(null != params.getMediaType())
			condition = condition.and(MK_ACTION.MEDIA_TYPE.eq(params.getMediaType().intValue()));
		
		if(null != params.getStartDate())
			condition = condition.and(MK_ACTION.START_DATE.ge(new Timestamp(params.getStartDate().getTime())));
		
		if(null != params.getEndDate())
			condition = condition.and(MK_ACTION.END_DATE.le(new Timestamp(params.getEndDate().getTime())));
		
		if(null != params.getMarketingCampaign())
			condition = condition.and(MK_ACTION.CAMPAIGN.eq(params.getMarketingCampaign().getId()));
		
		return condition;
	}

	public static void deleteAction(CloseableAONContext ctx, Integer id) {
		ctx.getDslContext().delete(MK_ACTION)
		.where(MK_ACTION.ID.eq(id))
		.execute();
		
		ctx.log().debug("DELETE MK_ACTION id:" + id);
	}

	public static MarketingAction saveAction(CloseableAONContext ctx, MarketingAction marketingAction) {
		MarketingActionValidation.validate(ctx, marketingAction);
		return marketingAction.getId() != null
				? update(ctx, marketingAction)
				: insert(ctx, marketingAction);
	}
	
	public static MarketingAction insert(AONContext ctx, MarketingAction marketingAction) {
		Integer id = ctx.getDslContext().insertInto(MK_ACTION)
				.set(MK_ACTION.DOMAIN, marketingAction.getDomain())
				.set(MK_ACTION.CAMPAIGN, marketingAction.getMarketingCampaign().getId())
				.set(MK_ACTION.MEDIA_TYPE, marketingAction.getMediaType().getValue())
				.set(MK_ACTION.START_DATE, new Timestamp(marketingAction.getStartDate().getTime()))
				.set(MK_ACTION.END_DATE, null == marketingAction.getEndDate() ? null : new Timestamp(marketingAction.getEndDate().getTime()))
				.set(MK_ACTION.SURVEY, marketingAction.getSurvey() == null ? null : marketingAction.getSurvey().getId())
				.set(MK_ACTION.NEWSLETTER, marketingAction.getNewsletter())
				.set(MK_ACTION.DESCRIPTION, marketingAction.getDescription())
				.set(MK_ACTION.NEWS, marketingAction.getNews())
				.returning(MK_ACTION.ID)
				.fetchOne()
				.getValue(MK_ACTION.ID);
		
		ctx.log().debug("INSERT MK_ACTION id: " +id);
		
		marketingAction.setId(id);
		
		return marketingAction;
	}
	
	public static MarketingAction update(AONContext ctx, MarketingAction marketingAction) {
		ctx.getDslContext().update(MK_ACTION)
			.set(MK_ACTION.DOMAIN, marketingAction.getDomain())
			.set(MK_ACTION.CAMPAIGN, marketingAction.getMarketingCampaign().getId())
			.set(MK_ACTION.MEDIA_TYPE, marketingAction.getMediaType().getValue())
			.set(MK_ACTION.START_DATE, new Timestamp(marketingAction.getStartDate().getTime()))
			.set(MK_ACTION.END_DATE, null == marketingAction.getEndDate() ? null : new Timestamp(marketingAction.getEndDate().getTime()))
			.set(MK_ACTION.SURVEY, marketingAction.getSurvey() == null ? null : marketingAction.getSurvey().getId())
			.set(MK_ACTION.NEWSLETTER, marketingAction.getNewsletter())
			.set(MK_ACTION.DESCRIPTION, marketingAction.getDescription())
			.set(MK_ACTION.NEWS, marketingAction.getNews())
			.where(MK_ACTION.ID.eq(marketingAction.getId()))
		.execute();
		
		ctx.log().debug("UPDATE MK_ACTION id:" + marketingAction.getId());
		
		return marketingAction;
	}
	
	// MARKETING ACTION TAGERT
	
	private static void getMarketingActionTargets(CloseableAONContext ctx, MarketingAction marketingAction) {
		List<MarketingActionTarget> marketingActionTargets = ctx.getDslContext().select().from(MK_ACTION_TARGET)
				.join(TARGET)
				.on(TARGET.REGISTRY.eq(MK_ACTION_TARGET.TARGET))
				.leftOuterJoin(USER)
				.on(USER.ID.eq(MK_ACTION_TARGET.USER))
				.where(MK_ACTION_TARGET.DOMAIN.eq(marketingAction.getDomain()))
				.and(MK_ACTION_TARGET.ACTION.eq(marketingAction.getId()))
				.fetch()
				.stream()
				.map(new MarketingActionTargetFiller(marketingAction))
				.collect(Collectors.toList());
		
		marketingAction.setTargets(marketingActionTargets);
	}
	
	public static List<MarketingActionTarget> getActionTargetList(CloseableAONContext ctx, MarketingActionTargetParams params) {
		Condition condition = actionTargetParamsToCondition(ctx, params);
		
		List<MarketingActionTarget> marketingActionTargets = ctx.getDslContext().select().from(MK_ACTION_TARGET)
				.join(TARGET)
				.on(TARGET.REGISTRY.eq(MK_ACTION_TARGET.TARGET))
				.join(TargetDAO.TARGET_ALIAS).on(TargetDAO.TARGET_ALIAS.ID.eq(TARGET.REGISTRY))
				.join(SCOPE).on(SCOPE.ID.eq(TARGET.SCOPE))
				.leftOuterJoin(USER)
				.on(USER.ID.eq(MK_ACTION_TARGET.USER))
				.where(condition)
				.orderBy(TargetDAO.TARGET_ALIAS.NAME)
				.limit(params.getOffset(), params.getLimit())
				.fetch()
				.stream()
				.map(new MarketingActionTargetFiller(params.getMarketingAction()))
				.collect(Collectors.toList());
		
		return marketingActionTargets;
	}
	
	private static Condition actionTargetParamsToCondition(CloseableAONContext ctx, MarketingActionTargetParams params) {
		Condition condition = MK_ACTION_TARGET.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx));
		
		if(AonStringUtils.isNotBlank(params.getDescription()))
			condition = condition.and(TargetDAO.TARGET_ALIAS.NAME.like("%" + params.getDescription() + "%"));
		
		if(null != params.getMarketingAction())
			condition = condition.and(MK_ACTION_TARGET.ACTION.eq(params.getMarketingAction().getId()));
		
		return condition;
	}
	
	public static MarketingActionTarget saveActionTarget(CloseableAONContext ctx, MarketingActionTarget marketingActionTarget) {
		MarketingActionTargetValidation.validate(ctx, marketingActionTarget);
		return marketingActionTarget.getActionTargetId() != null
				? update(ctx, marketingActionTarget)
				: insert(ctx, marketingActionTarget);
	}
	
	public static MarketingActionTarget insert(AONContext ctx, MarketingActionTarget marketingActionTarget) {
		Integer id = ctx.getDslContext().insertInto(MK_ACTION_TARGET)
				.set(MK_ACTION_TARGET.DOMAIN, marketingActionTarget.getActionTargetDomain())
				.set(MK_ACTION_TARGET.ACTION, marketingActionTarget.getMarketingAction().getId())
				.set(MK_ACTION_TARGET.TARGET, marketingActionTarget.getId())
				.set(MK_ACTION_TARGET.STATUS, marketingActionTarget.getActionTargetStatus())
				.set(MK_ACTION_TARGET.SURVEY_RESPONSE, marketingActionTarget.getSurveyResponse())
				.set(MK_ACTION_TARGET.COMMENTS, marketingActionTarget.getComments())
				.set(MK_ACTION_TARGET.USER, null == marketingActionTarget.getUser() ? null : marketingActionTarget.getUser().getId())
				.returning(MK_ACTION_TARGET.ID)
				.fetchOne()
				.getValue(MK_ACTION_TARGET.ID);
		
		ctx.log().debug("INSERT MK_ACTION_TARGET id: " +id);
		
		marketingActionTarget.setActionTargetId(id);
		
		return marketingActionTarget;
	}
	
	public static MarketingActionTarget update(AONContext ctx, MarketingActionTarget marketingActionTarget) {
		ctx.getDslContext().update(MK_ACTION_TARGET)
			.set(MK_ACTION_TARGET.DOMAIN, marketingActionTarget.getActionTargetDomain())
			.set(MK_ACTION_TARGET.ACTION, marketingActionTarget.getMarketingAction().getId())
			.set(MK_ACTION_TARGET.TARGET, marketingActionTarget.getId())
			.set(MK_ACTION_TARGET.STATUS, marketingActionTarget.getActionTargetStatus())
			.set(MK_ACTION_TARGET.SURVEY_RESPONSE, marketingActionTarget.getSurveyResponse())
			.set(MK_ACTION_TARGET.COMMENTS, marketingActionTarget.getComments())
			.set(MK_ACTION_TARGET.USER, null == marketingActionTarget.getUser() ? null : marketingActionTarget.getUser().getId())
			.where(MK_ACTION_TARGET.ID.eq(marketingActionTarget.getActionTargetId()))
		.execute();
		
		ctx.log().debug("UPDATE MK_ACTION_TARGET id:" + marketingActionTarget.getActionTargetId());
		
		return marketingActionTarget;
	}

	public static void deleteActionTarget(CloseableAONContext ctx, Integer id) {
		ctx.getDslContext().delete(MK_ACTION_TARGET)
		.where(MK_ACTION_TARGET.ID.eq(id))
		.execute();
		
		ctx.log().debug("DELETE MK_ACTION_TARGET id:" + id);
	}
	
	// FILLER
	
	public static class MarketingCampaignFiller extends Filler implements Function<Record, MarketingCampaign> {

		@Override
		public MarketingCampaign apply(Record r) {
			return build(r);
		}

		public static MarketingCampaign build(Record r) {
			return new MarketingCampaign()
				.setId(r.getValue(MK_CAMPAIGN.ID))
				.setDomain(r.getValue(MK_CAMPAIGN.DOMAIN))
				.setActive(r.getValue(MK_CAMPAIGN.ACTIVE) == (byte)1)
				.setDescription(r.getValue(MK_CAMPAIGN.DESCRIPTION))
				.setScope(null != r.getValue(MK_CAMPAIGN.SCOPE) ? ScopeFiller.buildScope(r) : null)
				;
		}
	}
	
	public static class MarketingActionFiller extends Filler implements Function<Record, MarketingAction> {

		private static MarketingCampaign marketingCampaign;
		
		public MarketingActionFiller(MarketingCampaign marketingCampaign) {
			MarketingActionFiller.marketingCampaign = marketingCampaign;
		}

		@Override
		public MarketingAction apply(Record r) {
			return build(r);
		}

		public static MarketingAction build(Record r) {
			return new MarketingAction()
				.setId(r.getValue(MK_ACTION.ID))
				.setDomain(r.getValue(MK_ACTION.DOMAIN))
				.setMarketingCampaign(MarketingActionFiller.marketingCampaign)
				.setMediaType(MarketingActionMediaType.getMediaType(r.getValue(MK_ACTION.MEDIA_TYPE)))
				.setStartDate(r.getValue(MK_ACTION.START_DATE))
				.setEndDate(r.getValue(MK_ACTION.END_DATE))
				.setSurvey(r.getValue(MK_ACTION.SURVEY) == null ? null : SurveyFiller.build(r))
				.setNewsletter(r.getValue(MK_ACTION.NEWSLETTER))
				.setDescription(r.getValue(MK_ACTION.DESCRIPTION))
				.setNews(r.getValue(MK_ACTION.NEWS))
				;
		}

	}
	
	public static class MarketingActionTargetFiller extends Filler implements Function<Record, MarketingActionTarget> {

		private static MarketingAction marketingAction;
		
		public MarketingActionTargetFiller(MarketingAction marketingAction) {
			MarketingActionTargetFiller.marketingAction = marketingAction;
		}

		@Override
		public MarketingActionTarget apply(Record r) {
			return build(r);
		}
		
		public static MarketingActionTarget build(Record r) {
			return new MarketingActionTarget()
					.copy(TargetFiller.build(r))
					.setActionTargetId(r.getValue(MK_ACTION_TARGET.ID))
					.setActionTargetDomain(r.getValue(MK_ACTION_TARGET.DOMAIN))
					.setMarketingAction(MarketingActionTargetFiller.marketingAction)
					.setActionTargetStatus(r.getValue(MK_ACTION_TARGET.STATUS))
					.setSurveyResponse(r.getValue(MK_ACTION_TARGET.SURVEY_RESPONSE))
					.setComments(r.getValue(MK_ACTION_TARGET.COMMENTS))
					.setUser(null == r.getValue(USER.ID) ? null : UserFiller.build(r))
				;
		}
	}

	
}
