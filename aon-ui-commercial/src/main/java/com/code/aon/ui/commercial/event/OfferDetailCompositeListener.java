package com.code.aon.ui.commercial.event;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferDetail;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.ItemComposition;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ui.commercial.controller.OfferDetailController;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class OfferDetailCompositeListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		OfferDetailController controller = (OfferDetailController)event.getController();
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		if(ds.isBetaEnabled()){
			controller.getCompositeHandler().acceptItemComposition();
		} else {
			Offer offer = (Offer)controller.getMasterController().getTo();
			OfferDetail offerDetail = (OfferDetail)controller.getTo();
			if (offerDetail.getItem() != null && offerDetail.getItem().getId() != null && offerDetail.getItem().getProduct().isComposition()) {
				double quantity = offerDetail.getQuantity();
				try {
					for (ItemComposition composition : offerDetail.getItem().getItemCompositionList()) {
						offerDetail.setId(null);
						offerDetail.setLine(offerDetail.getLine()+1);
						offerDetail.setItem(composition.getCompositionItem());
						offerDetail.setDescription(composition.getDescription());
						offerDetail.setQuantity(CommonUtil.round(quantity * composition.getQuantity(), 3));
						offerDetail.setPrice(obtainCompositionItemPrice(offerDetail, offer, composition, controller.getPriceStrategy()));
						offerDetail.setDiscountExpression(obtainCompositionDiscount(composition));
						offerDetail = (OfferDetail)controller.getManagerBean().insert(offerDetail);
					}
				} catch (ManagerBeanException e) {
					throw new ControllerListenerException(e.getMessage(), e);
				}
			}
		}
	}

	private double obtainCompositionItemPrice(OfferDetail offerDetail, Offer offer, ItemComposition composition, IPriceStrategy priceStrategy) {
		double price = 0;
		if (composition.getItem().getProduct().isCompositionPrice()) {
			price = priceStrategy.getUnitPrice(offerDetail, offer.getDate(), offer.getTarget());
		}
		return price;
	}

	private DiscountExpression obtainCompositionDiscount(ItemComposition composition) {
		DiscountExpression discountExpr = new DiscountExpression("0.0");
		if (composition.getItem().getProduct().isCompositionPrice() && composition.getDiscountExpression() != null) {
			discountExpr = composition.getDiscountExpression();
		}
		return discountExpr;
	}

}
