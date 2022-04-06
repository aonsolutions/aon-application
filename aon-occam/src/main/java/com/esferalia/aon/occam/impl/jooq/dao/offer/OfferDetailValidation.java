package com.esferalia.aon.occam.impl.jooq.dao.offer;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.management.OfferDetail;
import com.esferalia.aon.occam.api.model.type.OfferDetailStatus;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class OfferDetailValidation {

	private OfferDetailValidation() {

	}
	
	public static final BiConsumer<AONContext, OfferDetail> EMPTY_DOMAIN = (ctx, object) -> {
		if(object.getDomain() == null) 
			throw new AonCoreException(AonError.EMPTY_DATA.format("domain"));
	};
	
	public static final BiConsumer<AONContext, OfferDetail> EMPTY_OFFER = (ctx, object) -> {
		if(object.getOffer().getId() == null) 
			throw new AonCoreException(AonError.EMPTY_DATA.format("offer"));
	};

	public static void validate(AONContext ctx, OfferDetail object) throws AonCoreException{
		autocomplete(ctx, object);
		
		EMPTY_DOMAIN
		.andThen(EMPTY_OFFER)
		.accept(ctx, object);
	}
	
	public static final BiConsumer<AONContext, OfferDetail> COMPLETE_LINE = (ctx, object) -> {
//		if(object.getOffer().getId() != null && object.getLine() == null) {
//			// TODO GET NEXT LINE OF OFFER.
//			object.setLine((short) 0);
//		}
	};
	
	public static final BiConsumer<AONContext, OfferDetail> COMPLETE_DISCOUNT = (ctx, object) -> {
		if(object.getDiscountExpression() == null) {
			object.setDiscountExpression("0");
		}
	};
	
	public static final BiConsumer<AONContext, OfferDetail> COMPLETE_STATUS = (ctx, object) -> {
		if(object.getStatus() == null) {
			object.setStatus(OfferDetailStatus.PENDING);
		}
	};
	
	public static void autocomplete(AONContext ctx, OfferDetail object) throws AonCoreException{
		COMPLETE_LINE
		.andThen(COMPLETE_DISCOUNT)
		.andThen(COMPLETE_STATUS)
		.accept(ctx, object);
	}
}
