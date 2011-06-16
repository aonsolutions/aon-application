package com.code.aon.ui.commercial.event;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.ItemComposition;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ui.commercial.controller.OfferDetailController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class OfferDetailCompositeListener extends ControllerAdapter {

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		OfferDetailController controller = (OfferDetailController)event.getController();
		OfferDetail offerDetail = (OfferDetail)controller.getTo();
		if (offerDetail.getItem().getProduct().isComposition()) {
			double quantity = offerDetail.getQuantity();
			try {
				for (ItemComposition composition : offerDetail.getItem().getItemCompositionList()) {
					offerDetail.setId(null);
					offerDetail.setLine(offerDetail.getLine()+1);
					offerDetail.setItem(composition.getCompositionItem());
					offerDetail.setDescription(composition.getDescription());
					offerDetail.setQuantity(CommonUtil.round(quantity * composition.getQuantity(), 3));
					offerDetail.setPrice(obtainCompositionItemPrice(offerDetail, composition, controller.getPriceStrategy()));
					offerDetail.setDiscountExpression(obtainCompositionDiscount(offerDetail, composition));
					offerDetail = (OfferDetail)controller.getManagerBean().insert(offerDetail);
				}
			} catch (ManagerBeanException e) {
				throw new ControllerListenerException(e.getMessage(), e);
			}
		}
	}

	private double obtainCompositionItemPrice(OfferDetail offerDetail, ItemComposition composition, IPriceStrategy priceStrategy) {
		double price = 0;
		if (composition.getItem().getProduct().isCompositionPrice()) {
			Offer offer = offerDetail.getOffer();
			price = priceStrategy.getUnitPrice(offerDetail, offer.getIssueDate(), offer.getTariff());
		}
		return price;
	}

	private DiscountExpression obtainCompositionDiscount(OfferDetail offerDetail, ItemComposition composition) {
		DiscountExpression discountExpr = new DiscountExpression("0.0");
		if (composition.getItem().getProduct().isCompositionPrice()) {
			if (composition.getDiscountExpression() != null && composition.getDiscountExpression().getDiscounts()[0] > 0) {
				discountExpr = composition.getDiscountExpression();
			} else {
				discountExpr = offerDetail.getDiscountExpression();
			}
		}
		return discountExpr;
	}

}
