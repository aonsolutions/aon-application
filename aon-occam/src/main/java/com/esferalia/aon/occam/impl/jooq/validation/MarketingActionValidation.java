package com.esferalia.aon.occam.impl.jooq.validation;

import static com.esferalia.aon.jooq.tables.MkAction.MK_ACTION;

import java.util.List;
import java.util.function.BiConsumer;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.MarketingAction;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;


public class MarketingActionValidation {
	
	private MarketingActionValidation() {
		
	}
	
	/**
	 * Throws an exception if the question is null
	 */
	private static final BiConsumer<AONContext, MarketingAction> NULL = (ctx, marketingAction) -> {
		if (marketingAction == null)
			throw new AonCoreException(AonError.MARKETING_ACTION_NULL.getMessage());
	};
	
	/**
	 * Throws an exception if the domain is null
	 */
	private static final BiConsumer<AONContext, MarketingAction> EMPTY_DOMAIN = (ctx, marketingAction) -> {
		if (marketingAction != null && marketingAction.getDomain() == null)
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	/**
	 * Throws an exception if the text is null
	 */
	private static final BiConsumer<AONContext, MarketingAction> NULL_DESCRIPTION = (ctx, marketingAction) -> {
		if (marketingAction != null && AonStringUtils.isBlank(marketingAction.getDescription()))
			throw new AonCoreException(AonError.NULL_MARKETING_ACTION_DESCRIPTION.getMessage());
	};
	
	/**
	 * Throws an exception if the text is empty
	 */
	private static final BiConsumer<AONContext, MarketingAction> NULL_MARKETING_CAMPAIGN = (ctx, marketingAction) -> {
		if (marketingAction != null && null == marketingAction.getMarketingCampaign())
			throw new AonCoreException(AonError.NULL_MARKETING_CAMPAIGN_ACTION.getMessage());
	};
	
	/**
	 * Throws an exception if the type is null
	 */
	private static final BiConsumer<AONContext, MarketingAction> NULL_START_DATE = (ctx, marketingAction) -> {
		if (marketingAction != null && marketingAction.getStartDate() == null)
			throw new AonCoreException(AonError.NULL_MARKETING_ACTION_START_DATE.getMessage());
	};
	
	/**
	 * Throws an exception if the size of the question_text is invalid
	 */
	private static final BiConsumer<AONContext, MarketingAction> INVALID_SIZE_DESCRIPTION = (ctx, marketingAction) -> {
		if (marketingAction != null && AonStringUtils.length(marketingAction.getDescription()) > MK_ACTION.DESCRIPTION.getDataType().length())
			throw new AonCoreException(AonError.INVALID_SIZE_MARKETING_ACTION_DESCRIPTION.getMessage());
	};
	
	/**
	 * Throws an exception if the alias is repeated
	 */
	private static final BiConsumer<AONContext, MarketingAction> REPEATED_DESCRIPTION = (ctx, marketingAction) -> {
		if (AonStringUtils.isNotBlank(marketingAction.getDescription()) && checkDescription(ctx, marketingAction)) {
			throw new AonCoreException(AonError.REPEATED_ACTION_DESCRIPTION.getMessage());
		}
	};
	
	public static void validate(AONContext ctx, MarketingAction marketingAction) throws AonCoreException{
		NULL
		.andThen(EMPTY_DOMAIN)
		.andThen(NULL_DESCRIPTION)
		.andThen(NULL_MARKETING_CAMPAIGN)
		.andThen(NULL_START_DATE)
		.andThen(INVALID_SIZE_DESCRIPTION)
		.andThen(REPEATED_DESCRIPTION)
		.accept(ctx, marketingAction);
	}
	
	/**
	 * Checks if an alias is repeated
	 * @param ctx the context
	 * @param question the question
	 * @return true if the alias is repeated, false otherwise
	 */
	public static boolean checkDescription(AONContext ctx, MarketingAction marketingAction) {
		Condition condition = marketingAction.getId() == null ? DSL.trueCondition() : MK_ACTION.ID.ne(marketingAction.getId());
		List<Record> questionRecords = ctx.getDslContext().select().from(MK_ACTION)
			.where(MK_ACTION.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
			.and(MK_ACTION.DESCRIPTION.eq(marketingAction.getDescription()))
		 	.and(condition)
			.fetch();
			
		return !questionRecords.isEmpty();
	}

}
