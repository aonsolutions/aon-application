package com.esferalia.aon.occam.impl.jooq.validation;

import static com.esferalia.aon.jooq.tables.MkCampaign.MK_CAMPAIGN;

import java.util.List;
import java.util.function.BiConsumer;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.MarketingCampaign;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;


public class MarketingCampaignValidation {
	
	private MarketingCampaignValidation() {
		
	}
	
	/**
	 * Throws an exception if the question is null
	 */
	private static final BiConsumer<AONContext, MarketingCampaign> NULL = (ctx, marketingCampaign) -> {
		if (marketingCampaign == null)
			throw new AonCoreException(AonError.MARKETING_CAMPAIGN_NULL.getMessage());
	};
	
	/**
	 * Throws an exception if the domain is null
	 */
	private static final BiConsumer<AONContext, MarketingCampaign> EMPTY_DOMAIN = (ctx, marketingCampaign) -> {
		if (marketingCampaign != null && marketingCampaign.getDomain() == null)
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	/**
	 * Throws an exception if the text is null
	 */
	private static final BiConsumer<AONContext, MarketingCampaign> NULL_DESCRIPTION = (ctx, marketingCampaign) -> {
		if (marketingCampaign != null && AonStringUtils.isBlank(marketingCampaign.getDescription()))
			throw new AonCoreException(AonError.NULL_MARKETING_CAMPAIGN_DESCRIPTION.getMessage());
	};
	
	/**
	 * Throws an exception if the text is empty
	 */
	private static final BiConsumer<AONContext, MarketingCampaign> NULL_ACTIVE = (ctx, marketingCampaign) -> {
		if (marketingCampaign != null && null == marketingCampaign.isActive())
			throw new AonCoreException(AonError.NULL_MARKETING_CAMPAIGN_ACTIVE.getMessage());
	};
	
	/**
	 * Throws an exception if the type is null
	 */
	private static final BiConsumer<AONContext, MarketingCampaign> NULL_SCOPE = (ctx, marketingCampaign) -> {
		if (marketingCampaign != null && marketingCampaign.getScope() == null)
			throw new AonCoreException(AonError.NULL_MARKETING_CAMPAIGN_SCOPE.getMessage());
	};
	
	/**
	 * Throws an exception if the size of the question_text is invalid
	 */
	private static final BiConsumer<AONContext, MarketingCampaign> INVALID_SIZE_DESCRIPTION = (ctx, marketingCampaign) -> {
		if (marketingCampaign != null && AonStringUtils.length(marketingCampaign.getDescription()) > MK_CAMPAIGN.DESCRIPTION.getDataType().length())
			throw new AonCoreException(AonError.INVALID_SIZE_MARKETING_CAMPAIGN_DESCRIPTION.getMessage());
	};
	
	/**
	 * Throws an exception if the alias is repeated
	 */
	private static final BiConsumer<AONContext, MarketingCampaign> REPEATED_DESCRIPTION = (ctx, marketingCampaign) -> {
		if (AonStringUtils.isNotBlank(marketingCampaign.getDescription()) && checkDescription(ctx, marketingCampaign)) {
			throw new AonCoreException(AonError.REPEATED_DESCRIPTION.getMessage());
		}
	};
	
	public static void validate(AONContext ctx, MarketingCampaign marketingCampaign) throws AonCoreException{
		NULL
		.andThen(EMPTY_DOMAIN)
		.andThen(NULL_DESCRIPTION)
		.andThen(NULL_ACTIVE)
		.andThen(NULL_SCOPE)
		.andThen(INVALID_SIZE_DESCRIPTION)
		.andThen(REPEATED_DESCRIPTION)
		.accept(ctx, marketingCampaign);
	}
	
	/**
	 * Checks if an alias is repeated
	 * @param ctx the context
	 * @param question the question
	 * @return true if the alias is repeated, false otherwise
	 */
	public static boolean checkDescription(AONContext ctx, MarketingCampaign marketingCampaign) {
		Condition condition = marketingCampaign.getId() == null ? DSL.trueCondition() : MK_CAMPAIGN.ID.ne(marketingCampaign.getId());
		List<Record> questionRecords = ctx.getDslContext().select().from(MK_CAMPAIGN)
			.where(MK_CAMPAIGN.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
			.and(MK_CAMPAIGN.DESCRIPTION.eq(marketingCampaign.getDescription()))
		 	.and(condition)
			.fetch();
			
		return !questionRecords.isEmpty();
	}

}
