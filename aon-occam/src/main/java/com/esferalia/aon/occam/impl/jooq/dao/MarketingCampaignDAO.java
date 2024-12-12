package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.MkAction.MK_ACTION;
import static com.esferalia.aon.jooq.tables.MkActionTarget.MK_ACTION_TARGET;
import static com.esferalia.aon.jooq.tables.MkCampaign.MK_CAMPAIGN;
import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.ProjectCommercial.PROJECT_COMMERCIAL;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Survey.SURVEY;
import static com.esferalia.aon.jooq.tables.Tag.TAG;
import static com.esferalia.aon.jooq.tables.Target.TARGET;
import static com.esferalia.aon.jooq.tables.TaskHolder.TASK_HOLDER;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.jooq.tables.Workgroup.WORKGROUP;
import static com.esferalia.aon.occam.impl.jooq.dao.TaskHolderDAO.TASK_HOLDER_ALIAS;

import java.sql.Timestamp;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.jooq.tables.records.AppParamRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Filter.MarketingCampaignFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.MarketingAction;
import com.esferalia.aon.occam.api.model.MarketingAction.MarketingActionMediaType;
import com.esferalia.aon.occam.api.model.MarketingAction.MarketingSellerDistribution;
import com.esferalia.aon.occam.api.model.MarketingActionParams;
import com.esferalia.aon.occam.api.model.MarketingActionTarget;
import com.esferalia.aon.occam.api.model.MarketingActionTargetParams;
import com.esferalia.aon.occam.api.model.MarketingCampaign;
import com.esferalia.aon.occam.api.model.MarketingCompaignParams;
import com.esferalia.aon.occam.api.model.Properties.MarketingCampaignProperties;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO.ScopeFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SurveyDAO.SurveyFiller;
import com.esferalia.aon.occam.impl.jooq.dao.TargetDAO.TargetFiller;
import com.esferalia.aon.occam.impl.jooq.dao.TaskDAO.TagFiller;
import com.esferalia.aon.occam.impl.jooq.dao.TaskHolderDAO.TaskHolderFiller;
import com.esferalia.aon.occam.impl.jooq.dao.UserDAO.UserFiller;
import com.esferalia.aon.occam.impl.jooq.dao.WorkgroupDAO.WorkgroupFiller;
import com.esferalia.aon.occam.impl.jooq.validation.MarketingActionTargetValidation;
import com.esferalia.aon.occam.impl.jooq.validation.MarketingActionValidation;
import com.esferalia.aon.occam.impl.jooq.validation.MarketingCampaignValidation;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class MarketingCampaignDAO {
	
	private MarketingCampaignDAO() {}

	private static final String MARKETING_ACTION_DISTRIBUTION = "MK_ACTION_SELLER_DIST_";
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
				.leftOuterJoin(SCOPE).on(SCOPE.ID.eq(MK_CAMPAIGN.SCOPE))
				.leftOuterJoin(WORKGROUP).on(WORKGROUP.ID.eq(MK_CAMPAIGN.WORKGROUP))
				.leftOuterJoin(TASK_HOLDER).on(TASK_HOLDER.REGISTRY.eq(MK_CAMPAIGN.TASK_HOLDER))
				.leftOuterJoin(TASK_HOLDER_ALIAS).on(TASK_HOLDER_ALIAS.ID.eq(TASK_HOLDER.REGISTRY))
				.where(MK_CAMPAIGN.ID.eq(id))
				.fetchOne();
		
		if(null == marketingCampaignRecord) return null;
		
		MarketingCampaign marketingCampaign = new MarketingCampaignFiller().apply(marketingCampaignRecord);
		
		getMarketingCampaignActions(ctx, marketingCampaign);
		
		return marketingCampaign;
	}
	
	public static MarketingCampaign getByAction(CloseableAONContext ctx, Integer actionId) {
		Record marketingCampaignRecord = ctx.getDslContext().select().from(MK_ACTION)
				.join(MK_CAMPAIGN).on(MK_CAMPAIGN.ID.eq(MK_ACTION.CAMPAIGN))
				.leftOuterJoin(SCOPE).on(SCOPE.ID.eq(MK_CAMPAIGN.SCOPE))
				.leftOuterJoin(WORKGROUP).on(WORKGROUP.ID.eq(MK_CAMPAIGN.WORKGROUP))
				.leftOuterJoin(TASK_HOLDER).on(TASK_HOLDER.REGISTRY.eq(MK_CAMPAIGN.TASK_HOLDER))
				.leftOuterJoin(TASK_HOLDER_ALIAS).on(TASK_HOLDER_ALIAS.ID.eq(TASK_HOLDER.REGISTRY))
				.where(MK_ACTION.ID.eq(actionId))
				.fetchOne();
		
		if(null == marketingCampaignRecord) return null;
		
		MarketingCampaign marketingCampaign = new MarketingCampaignFiller().apply(marketingCampaignRecord);
		
		getMarketingCampaignActions(ctx, marketingCampaign);
		
		return marketingCampaign;
	}

	public static List<MarketingCampaign> getList(CloseableAONContext ctx, MarketingCompaignParams params) {
		Condition condition = paramsToCondition(ctx, params);
		
		SelectConditionStep<Record> select = ctx.getDslContext().select().from(MK_CAMPAIGN)
			.leftOuterJoin(SCOPE).on(SCOPE.ID.eq(MK_CAMPAIGN.SCOPE))
			.leftOuterJoin(WORKGROUP).on(WORKGROUP.ID.eq(MK_CAMPAIGN.WORKGROUP))
			.leftOuterJoin(TASK_HOLDER).on(TASK_HOLDER.REGISTRY.eq(MK_CAMPAIGN.TASK_HOLDER))
			.leftOuterJoin(TASK_HOLDER_ALIAS).on(TASK_HOLDER_ALIAS.ID.eq(TASK_HOLDER.REGISTRY))
			.where(condition);
		
		if(params.isAsc()) {
			if(AonStringUtils.equals(params.getOrderBy(), "name"))
				select.orderBy(MK_CAMPAIGN.DESCRIPTION);
			else if(AonStringUtils.equals(params.getOrderBy(), "budget"))
				select.orderBy(MK_CAMPAIGN.BUDGET);
			else if(AonStringUtils.equals(params.getOrderBy(), "expense"))
				select.orderBy(MK_CAMPAIGN.EXPENSE);
		} else {
			if(AonStringUtils.equals(params.getOrderBy(), "name"))
				select.orderBy(MK_CAMPAIGN.DESCRIPTION.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "budget"))
				select.orderBy(MK_CAMPAIGN.BUDGET.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "expense"))
				select.orderBy(MK_CAMPAIGN.EXPENSE.desc());
		}
		
		List<MarketingCampaign> marketingCampaigns = select.limit(params.getOffset(), params.getLimit())
			.fetch()
			.stream()
			.map(new MarketingCampaignFiller())
			.collect(Collectors.toList());
		
		marketingCampaigns.forEach(marketingCampaign -> getMarketingCampaignActions(ctx, marketingCampaign));
		
		return marketingCampaigns;
	}	
	
	private static void getMarketingCampaignActions(CloseableAONContext ctx, MarketingCampaign marketingCampaign) {
		List<MarketingAction> marketingActions = ctx.getDslContext().select().from(MK_ACTION)
				.leftOuterJoin(SURVEY).on(SURVEY.ID.eq(MK_ACTION.SURVEY))
				.leftOuterJoin(SCOPE).on(SCOPE.ID.eq(SURVEY.SCOPE))
				.leftOuterJoin(WORKGROUP).on(WORKGROUP.ID.eq(MK_ACTION.WORKGROUP))
				.leftOuterJoin(TASK_HOLDER).on(TASK_HOLDER.REGISTRY.eq(MK_ACTION.TASK_HOLDER))
				.leftOuterJoin(TASK_HOLDER_ALIAS).on(TASK_HOLDER_ALIAS.ID.eq(TASK_HOLDER.REGISTRY))
				.leftOuterJoin(TAG).on(TAG.ID.eq(MK_ACTION.TAG))
				.where(MK_ACTION.DOMAIN.eq(marketingCampaign.getDomain()))
				.and(MK_ACTION.CAMPAIGN.eq(marketingCampaign.getId()))
				.fetch()
				.stream()
				.map(new MarketingActionFiller(marketingCampaign))
				.collect(Collectors.toList());
		
		marketingActions.forEach(marketingAction -> getMarketingActionSellerDistribution(ctx, marketingAction));
		
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
		
		if(params.isBetweenBudgetNumbers()) {
			if(params.getGTBudget() != null && AonMathUtils.isNotZero(params.getGTBudget()))
				condition = condition.and(MK_CAMPAIGN.BUDGET.ge(params.getGTBudget()));
			if(params.getLTBudget() != null && AonMathUtils.isNotZero(params.getLTBudget()))
				condition = condition.and(MK_CAMPAIGN.BUDGET.le(params.getLTBudget()));
		} else {
			if (params.getBudget() != null && AonMathUtils.isNotZero(params.getBudget())) {
				condition = condition.and(MK_CAMPAIGN.BUDGET.eq(params.getBudget()));	
			}
		}
		
		if(params.isBetweenExpenseNumbers()) {
			if(params.getGTExpense() != null && AonMathUtils.isNotZero(params.getGTExpense()))
				condition = condition.and(MK_CAMPAIGN.EXPENSE.ge(params.getGTExpense()));
			if(params.getLTExpense() != null && AonMathUtils.isNotZero(params.getLTExpense()))
				condition = condition.and(MK_CAMPAIGN.EXPENSE.le(params.getLTExpense()));
		} else {
			if (params.getExpense() != null && AonMathUtils.isNotZero(params.getExpense())) {
				condition = condition.and(MK_CAMPAIGN.EXPENSE.eq(params.getExpense()));	
			}
		}
		
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
				.set(MK_CAMPAIGN.BUDGET, marketingCampaign.getBudget())
				.set(MK_CAMPAIGN.EXPENSE, marketingCampaign.getExpense())
				.set(MK_CAMPAIGN.WORKGROUP, marketingCampaign.getWorkgroup() == null ? null : marketingCampaign.getWorkgroup().getId())
				.set(MK_CAMPAIGN.TASK_HOLDER, marketingCampaign.getTaskHolder() == null ? null : marketingCampaign.getTaskHolder().getRegistry())
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
		.set(MK_CAMPAIGN.BUDGET, marketingCampaign.getBudget())
		.set(MK_CAMPAIGN.EXPENSE, marketingCampaign.getExpense())
		.set(MK_CAMPAIGN.WORKGROUP, marketingCampaign.getWorkgroup() == null ? null : marketingCampaign.getWorkgroup().getId())
		.set(MK_CAMPAIGN.TASK_HOLDER, marketingCampaign.getTaskHolder() == null ? null : marketingCampaign.getTaskHolder().getRegistry())
		.where(MK_CAMPAIGN.ID.eq(marketingCampaign.getId()))
		.execute();
		
		ctx.log().debug("UPDATE MK_CAMPAIGN id:" + marketingCampaign.getId());
		
		return marketingCampaign;
	}

	public static void delete(CloseableAONContext ctx, Integer id) {
		List<Integer> marketingActionIds = ctx.getDslContext().select(MK_ACTION.ID).from(MK_ACTION).where(MK_ACTION.CAMPAIGN.eq(id)).fetch(MK_ACTION.ID);
		marketingActionIds.forEach(marketingActionId -> deleteAction(ctx, marketingActionId));
		
		ctx.getDslContext().delete(MK_CAMPAIGN)
			.where(MK_CAMPAIGN.ID.eq(id))
			.execute();
		
		ctx.log().debug("DELETE MK_CAMPAIGN id:" + id);
	}
	
	
	// MARKETING ACTION

	public static List<MarketingAction> getActionList(CloseableAONContext ctx, MarketingActionParams params) {
		Condition condition = actionParamsToCondition(ctx, params);
		
		SelectConditionStep<Record> select = ctx.getDslContext().select().from(MK_ACTION)
			.leftOuterJoin(SURVEY).on(SURVEY.ID.eq(MK_ACTION.SURVEY))
			.leftOuterJoin(SCOPE).on(SCOPE.ID.eq(SURVEY.SCOPE))
			.leftOuterJoin(WORKGROUP).on(WORKGROUP.ID.eq(MK_ACTION.WORKGROUP))
			.leftOuterJoin(TASK_HOLDER).on(TASK_HOLDER.REGISTRY.eq(MK_ACTION.TASK_HOLDER))
			.leftOuterJoin(TASK_HOLDER_ALIAS).on(TASK_HOLDER_ALIAS.ID.eq(TASK_HOLDER.REGISTRY))
			.leftOuterJoin(TAG).on(TAG.ID.eq(MK_ACTION.TAG))
			.where(condition);
		
		if(params.isAsc()) {
			if(AonStringUtils.equals(params.getOrderBy(), "name"))
				select.orderBy(MK_ACTION.DESCRIPTION);
			else if(AonStringUtils.equals(params.getOrderBy(), "media"))
				select.orderBy(MK_ACTION.MEDIA_TYPE);
			else if(AonStringUtils.equals(params.getOrderBy(), "budget"))
				select.orderBy(MK_ACTION.BUDGET);
			else if(AonStringUtils.equals(params.getOrderBy(), "expense"))
				select.orderBy(MK_ACTION.EXPENSE);
			else if(AonStringUtils.equals(params.getOrderBy(), "start"))
				select.orderBy(MK_ACTION.START_DATE);
			else if(AonStringUtils.equals(params.getOrderBy(), "end"))
				select.orderBy(MK_ACTION.END_DATE);
		} else {
			if(AonStringUtils.equals(params.getOrderBy(), "name"))
				select.orderBy(MK_ACTION.DESCRIPTION.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "media"))
				select.orderBy(MK_ACTION.MEDIA_TYPE.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "budget"))
				select.orderBy(MK_ACTION.BUDGET.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "expense"))
				select.orderBy(MK_ACTION.EXPENSE.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "start"))
				select.orderBy(MK_ACTION.START_DATE.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "end"))
				select.orderBy(MK_ACTION.END_DATE.desc());
		}
		
		List<MarketingAction> marketingActions = select.limit(params.getOffset(), params.getLimit())
			.fetch()
			.stream()
			.map(new MarketingActionFiller(params.getMarketingCampaign()))
			.collect(Collectors.toList());
		
		marketingActions.forEach(marketingAction -> {
			getMarketingActionTargets(ctx, marketingAction);
			getMarketingActionSellerDistribution(ctx, marketingAction);
		});
		
		return marketingActions;
	}
	
	public static MarketingAction getAction(CloseableAONContext ctx, Integer id) {
		Record marketingActionRecord = ctx.getDslContext().select().from(MK_ACTION)
			.leftOuterJoin(SURVEY).on(SURVEY.ID.eq(MK_ACTION.SURVEY))
			.leftOuterJoin(SCOPE).on(SCOPE.ID.eq(SURVEY.SCOPE))
			.leftOuterJoin(WORKGROUP).on(WORKGROUP.ID.eq(MK_ACTION.WORKGROUP))
			.leftOuterJoin(TASK_HOLDER).on(TASK_HOLDER.REGISTRY.eq(MK_ACTION.TASK_HOLDER))
			.leftOuterJoin(TASK_HOLDER_ALIAS).on(TASK_HOLDER_ALIAS.ID.eq(TASK_HOLDER.REGISTRY))
			.leftOuterJoin(TAG).on(TAG.ID.eq(MK_ACTION.TAG))
			.where(MK_ACTION.ID.eq(id))
			.fetchOne();
		
		MarketingAction marketingAction = MarketingActionFiller.build(marketingActionRecord);
		
		if(marketingAction.getMarketingCampaign() == null) {
			marketingAction.setMarketingCampaign(getByAction(ctx, marketingAction.getId()));
		}
			
		getMarketingActionTargets(ctx, marketingAction);
		getMarketingActionSellerDistribution(ctx, marketingAction);
		
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
		
		if(params.isBetweenBudgetNumbers()) {
			if(params.getGTBudget() != null && AonMathUtils.isNotZero(params.getGTBudget()))
				condition = condition.and(MK_ACTION.BUDGET.ge(params.getGTBudget()));
			if(params.getLTBudget() != null && AonMathUtils.isNotZero(params.getLTBudget()))
				condition = condition.and(MK_ACTION.BUDGET.le(params.getLTBudget()));
		} else {
			if (params.getBudget() != null && AonMathUtils.isNotZero(params.getBudget())) {
				condition = condition.and(MK_ACTION.BUDGET.eq(params.getBudget()));	
			}
		}
		
		if(params.isBetweenExpenseNumbers()) {
			if(params.getGTExpense() != null && AonMathUtils.isNotZero(params.getGTExpense()))
				condition = condition.and(MK_ACTION.EXPENSE.ge(params.getGTExpense()));
			if(params.getLTExpense() != null && AonMathUtils.isNotZero(params.getLTExpense()))
				condition = condition.and(MK_ACTION.EXPENSE.le(params.getLTExpense()));
		} else {
			if (params.getExpense() != null && AonMathUtils.isNotZero(params.getExpense())) {
				condition = condition.and(MK_ACTION.EXPENSE.eq(params.getExpense()));	
			}
		}
		
		return condition;
	}
	
	private static void getMarketingActionSellerDistribution(CloseableAONContext ctx, MarketingAction marketingAction) {
		AppParamRecord record = ctx.getDslContext().selectFrom(APP_PARAM)
			.where(APP_PARAM.DOMAIN.eq(marketingAction.getDomain()))
			.and(APP_PARAM.NAME.eq(MARKETING_ACTION_DISTRIBUTION + marketingAction.getId()))
			.fetchOne();
		
		if(null == record) marketingAction.setSellerDistribution(MarketingSellerDistribution.MANUAL);
		else marketingAction.setSellerDistribution(MarketingSellerDistribution.getSellerDistribution(Integer.parseInt(record.getValue())));
	}

	public static void deleteAction(CloseableAONContext ctx, Integer id) {
		List<Integer> marketingActionTargetIds = ctx.getDslContext().select(MK_ACTION_TARGET.ID).from(MK_ACTION_TARGET).where(MK_ACTION_TARGET.ACTION.eq(id)).fetch(MK_ACTION_TARGET.ID);
		marketingActionTargetIds.forEach(marketingActionTargetId -> deleteActionTarget(ctx, marketingActionTargetId));
		
		ctx.getDslContext().delete(APP_PARAM)
			.where(APP_PARAM.DOMAIN.eq(ctx.getDomainId()))
			.and(APP_PARAM.NAME.eq(MARKETING_ACTION_DISTRIBUTION + id))
			.execute();
		
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
		// Check tag
		if(null != marketingAction.getTag() && null == marketingAction.getTag().getId() && AonStringUtils.isNotBlank(marketingAction.getTag().getName())) {
			// Insert tag
			Tag insertedTag = TagDAO.insertTag(ctx, marketingAction.getTag()); 
			marketingAction.getTag().setId(insertedTag.getId());
		}
		
		// Check Seller Distribution
		checkMarketingActionSellerDistribution(ctx, marketingAction);
		
		Integer id = ctx.getDslContext().insertInto(MK_ACTION)
				.set(MK_ACTION.DOMAIN, marketingAction.getDomain())
				.set(MK_ACTION.CAMPAIGN, marketingAction.getMarketingCampaign().getId())
				.set(MK_ACTION.MEDIA_TYPE, marketingAction.getMediaType().getValue())
				.set(MK_ACTION.TAG, null == marketingAction.getTag() ? null : marketingAction.getTag().getId())
				.set(MK_ACTION.START_DATE, new Timestamp(marketingAction.getStartDate().getTime()))
				.set(MK_ACTION.END_DATE, null == marketingAction.getEndDate() ? null : new Timestamp(marketingAction.getEndDate().getTime()))
				.set(MK_ACTION.SURVEY, marketingAction.getSurvey() == null ? null : marketingAction.getSurvey().getId())
				.set(MK_ACTION.NEWSLETTER, marketingAction.getNewsletter())
				.set(MK_ACTION.DESCRIPTION, marketingAction.getDescription())
				.set(MK_ACTION.NEWS, marketingAction.getNews())
				.set(MK_ACTION.BUDGET, marketingAction.getBudget())
				.set(MK_ACTION.EXPENSE, marketingAction.getExpense())
				.set(MK_ACTION.WORKGROUP, marketingAction.getWorkgroup() == null ? null : marketingAction.getWorkgroup().getId())
				.set(MK_ACTION.TASK_HOLDER, marketingAction.getTaskHolder() == null ? null : marketingAction.getTaskHolder().getRegistry())
				.returning(MK_ACTION.ID)
				.fetchOne()
				.getValue(MK_ACTION.ID);
		
		ctx.log().debug("INSERT MK_ACTION id: " + id);
		
		marketingAction.setId(id);
		
		return marketingAction;
	}
	
	public static MarketingAction update(AONContext ctx, MarketingAction marketingAction) {
		// Check tag
		if(null != marketingAction.getTag() && null == marketingAction.getTag().getId() && AonStringUtils.isNotBlank(marketingAction.getTag().getName())) {
			// Insert tag
			Tag insertedTag = TagDAO.insertTag(ctx, marketingAction.getTag()); 
			marketingAction.getTag().setId(insertedTag.getId());
		}
		
		// Check Seller Distribution
		checkMarketingActionSellerDistribution(ctx, marketingAction);
		
		ctx.getDslContext().update(MK_ACTION)
			.set(MK_ACTION.DOMAIN, marketingAction.getDomain())
			.set(MK_ACTION.CAMPAIGN, marketingAction.getMarketingCampaign().getId())
			.set(MK_ACTION.MEDIA_TYPE, marketingAction.getMediaType().getValue())
			.set(MK_ACTION.TAG, null == marketingAction.getTag() ? null : marketingAction.getTag().getId())
			.set(MK_ACTION.START_DATE, new Timestamp(marketingAction.getStartDate().getTime()))
			.set(MK_ACTION.END_DATE, null == marketingAction.getEndDate() ? null : new Timestamp(marketingAction.getEndDate().getTime()))
			.set(MK_ACTION.SURVEY, marketingAction.getSurvey() == null ? null : marketingAction.getSurvey().getId())
			.set(MK_ACTION.NEWSLETTER, marketingAction.getNewsletter())
			.set(MK_ACTION.DESCRIPTION, marketingAction.getDescription())
			.set(MK_ACTION.NEWS, marketingAction.getNews())
			.set(MK_ACTION.BUDGET, marketingAction.getBudget())
			.set(MK_ACTION.EXPENSE, marketingAction.getExpense())
			.set(MK_ACTION.WORKGROUP, marketingAction.getWorkgroup() == null ? null : marketingAction.getWorkgroup().getId())
			.set(MK_ACTION.TASK_HOLDER, marketingAction.getTaskHolder() == null ? null : marketingAction.getTaskHolder().getRegistry())
			.where(MK_ACTION.ID.eq(marketingAction.getId()))
		.execute();
		
		ctx.log().debug("UPDATE MK_ACTION id:" + marketingAction.getId());
		
		return marketingAction;
	}
	
	// MARKETING ACTION TAGERT
	
	private static void checkMarketingActionSellerDistribution(AONContext ctx, MarketingAction marketingAction) {
		AppParamRecord record = ctx.getDslContext().selectFrom(APP_PARAM)
				.where(APP_PARAM.DOMAIN.eq(marketingAction.getDomain()))
				.and(APP_PARAM.NAME.eq(MARKETING_ACTION_DISTRIBUTION + marketingAction.getId()))
				.fetchOne();
			
			if(null == record) 
				ctx.getDslContext().insertInto(APP_PARAM)
					.set(APP_PARAM.DOMAIN, marketingAction.getDomain())
					.set(APP_PARAM.NAME, MARKETING_ACTION_DISTRIBUTION + marketingAction.getId())
					.set(APP_PARAM.VALUE,marketingAction.getSellerDistribution().getValue().toString())
					.execute();
			else 
				ctx.getDslContext().update(APP_PARAM)
					.set(APP_PARAM.VALUE,marketingAction.getSellerDistribution().getValue().toString())
					.where(APP_PARAM.ID.eq(record.getId()))
					.execute();
	}

	private static void getMarketingActionTargets(CloseableAONContext ctx, MarketingAction marketingAction) {
		List<MarketingActionTarget> marketingActionTargets = ctx.getDslContext().select().from(MK_ACTION_TARGET)
				.join(TARGET)
				.on(TARGET.REGISTRY.eq(MK_ACTION_TARGET.TARGET))
				.join(TargetDAO.TARGET_ALIAS).on(TargetDAO.TARGET_ALIAS.ID.eq(TARGET.REGISTRY))
				.join(SCOPE).on(SCOPE.ID.eq(TARGET.SCOPE))
				.leftOuterJoin(USER)
				.on(USER.ID.eq(MK_ACTION_TARGET.USER))
				.leftOuterJoin(PROJECT)
				.on(PROJECT.ID.eq(MK_ACTION_TARGET.PROJECT).and(PROJECT.NAME.eq(marketingAction.getDescription())))
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
		
		SelectConditionStep<Record> select = ctx.getDslContext().select().from(MK_ACTION_TARGET)
				.join(TARGET)
				.on(TARGET.REGISTRY.eq(MK_ACTION_TARGET.TARGET))
				.join(TargetDAO.TARGET_ALIAS).on(TargetDAO.TARGET_ALIAS.ID.eq(TARGET.REGISTRY))
				.join(SCOPE).on(SCOPE.ID.eq(TARGET.SCOPE))
				.leftOuterJoin(USER)
				.on(USER.ID.eq(MK_ACTION_TARGET.USER))
				.leftOuterJoin(PROJECT_COMMERCIAL)
				.on(PROJECT_COMMERCIAL.TARGET.eq(TARGET.REGISTRY))
				.leftOuterJoin(PROJECT)
				.on(PROJECT.ID.eq(MK_ACTION_TARGET.PROJECT).and(PROJECT.NAME.eq(params.getMarketingAction().getDescription())))
				.where(condition);
		
		if(params.isAsc()) {
			if(AonStringUtils.equals(params.getOrderBy(), "name"))
				select.orderBy(TargetDAO.TARGET_ALIAS.NAME);
			else if(AonStringUtils.equals(params.getOrderBy(), "status"))
				select.orderBy(MK_ACTION_TARGET.STATUS);
		} else {
			if(AonStringUtils.equals(params.getOrderBy(), "name"))
				select.orderBy(TargetDAO.TARGET_ALIAS.NAME.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "status"))
				select.orderBy(MK_ACTION_TARGET.STATUS.desc());
		}
		
		List<MarketingActionTarget> marketingActionTargets = select.groupBy(TARGET.REGISTRY)
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
		
		if(null != params.getStatus())
			condition = condition.and(MK_ACTION_TARGET.STATUS.eq(params.getStatus()));
		
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
				.set(MK_ACTION_TARGET.PROJECT, marketingActionTarget.getProject())
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
			.set(MK_ACTION_TARGET.PROJECT, marketingActionTarget.getProject())
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
				.setBudget(r.getValue(MK_CAMPAIGN.BUDGET))
				.setExpense(r.getValue(MK_CAMPAIGN.EXPENSE))
				.setWorkgroup(null == r.getValue(MK_CAMPAIGN.WORKGROUP) ? null : WorkgroupFiller.build(r))
				.setTaskHolder(null == r.getValue(MK_CAMPAIGN.TASK_HOLDER) ? null : TaskHolderFiller.build(r))
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
				.setTag(null == r.get(TAG.ID) ? null : TagFiller.build(r))
				.setStartDate(r.getValue(MK_ACTION.START_DATE))
				.setEndDate(r.getValue(MK_ACTION.END_DATE))
				.setSurvey(r.getValue(MK_ACTION.SURVEY) == null ? null : SurveyFiller.build(r))
				.setNewsletter(r.getValue(MK_ACTION.NEWSLETTER))
				.setDescription(r.getValue(MK_ACTION.DESCRIPTION))
				.setNews(r.getValue(MK_ACTION.NEWS))
				.setBudget(r.getValue(MK_ACTION.BUDGET))
				.setExpense(r.getValue(MK_ACTION.EXPENSE))
				.setWorkgroup(null == r.getValue(MK_ACTION.WORKGROUP) ? null : WorkgroupFiller.build(r))
				.setTaskHolder(null == r.getValue(MK_ACTION.TASK_HOLDER) ? null : TaskHolderFiller.build(r))
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
					.setProject(r.get(MK_ACTION_TARGET.PROJECT))
					.setSurveyResponse(r.getValue(MK_ACTION_TARGET.SURVEY_RESPONSE))
					.setComments(r.getValue(MK_ACTION_TARGET.COMMENTS))
					.setUser(null == r.getValue(USER.ID) ? null : UserFiller.build(r))
				;
		}
	}

	
}
