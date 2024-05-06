package com.esferalia.aon.occam.impl.jooq.validation;

import static com.esferalia.aon.jooq.tables.MkActionTarget.MK_ACTION_TARGET;

import java.util.List;
import java.util.function.BiConsumer;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.MarketingActionTarget;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;


public class MarketingActionTargetValidation {
	
	private MarketingActionTargetValidation() {
		
	}
	
	/**
	 * Throws an exception if the question is null
	 */
	private static final BiConsumer<AONContext, MarketingActionTarget> NULL = (ctx, marketingActionTarget) -> {
		if (marketingActionTarget == null)
			throw new AonCoreException(AonError.MARKETING_ACTION_TARGET_NULL.getMessage());
	};
	
	/**
	 * Throws an exception if the domain is null
	 */
	private static final BiConsumer<AONContext, MarketingActionTarget> EMPTY_DOMAIN = (ctx, marketingActionTarget) -> {
		if (marketingActionTarget != null && marketingActionTarget.getActionTargetDomain() == null)
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	/**
	 * Throws an exception if the text is null
	 */
	private static final BiConsumer<AONContext, MarketingActionTarget> NULL_TARGET = (ctx, marketingActionTarget) -> {
		if (marketingActionTarget != null && null == marketingActionTarget.getId())
			throw new AonCoreException(AonError.NULL_MARKETING_ACTION_TARGET.getMessage());
	};
	
	/**
	 * Throws an exception if the text is empty
	 */
	private static final BiConsumer<AONContext, MarketingActionTarget> NULL_MARKETING_ACTION = (ctx, marketingActionTarget) -> {
		if (marketingActionTarget != null && null == marketingActionTarget.getMarketingAction())
			throw new AonCoreException(AonError.NULL_MARKETING_CAMPAIGN_ACTION_TARGET.getMessage());
	};
	
	/**
	 * Throws an exception if the alias is repeated
	 */
	private static final BiConsumer<AONContext, MarketingActionTarget> REPEATED_ACTION_TARGET = (ctx, marketingActionTarget) -> {
		if (null != marketingActionTarget.getId() && checkTargetAction(ctx, marketingActionTarget)) {
			throw new AonCoreException(AonError.REPEATED_ACTION_TARGET.getMessage());
		}
	};
	
	public static void validate(AONContext ctx, MarketingActionTarget marketingActionTarget) throws AonCoreException{
		NULL
		.andThen(EMPTY_DOMAIN)
		.andThen(NULL_TARGET)
		.andThen(NULL_MARKETING_ACTION)
		.andThen(REPEATED_ACTION_TARGET)
		.accept(ctx, marketingActionTarget);
	}
	
	/**
	 * Checks if an alias is repeated
	 * @param ctx the context
	 * @param question the question
	 * @return true if the alias is repeated, false otherwise
	 */
	public static boolean checkTargetAction(AONContext ctx, MarketingActionTarget marketingActionTarget) {
		Condition condition = marketingActionTarget.getActionTargetId() == null ? DSL.trueCondition() : MK_ACTION_TARGET.ID.ne(marketingActionTarget.getActionTargetId());
		List<Record> questionRecords = ctx.getDslContext().select().from(MK_ACTION_TARGET)
			.where(MK_ACTION_TARGET.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
			.and(MK_ACTION_TARGET.ACTION.eq(marketingActionTarget.getMarketingAction().getId()))
			.and(MK_ACTION_TARGET.TARGET.eq(marketingActionTarget.getId()))
		 	.and(condition)
			.fetch();
			
		return !questionRecords.isEmpty();
	}

}
