package com.code.aon.ui.warehouse.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.ItemComposition;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.warehouse.controller.DeliveryDetailController;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;

public class DeliveryDetailCompositeListener extends ControllerAdapter {

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		DeliveryDetailController controller = (DeliveryDetailController)event.getController();
		DeliveryDetail deliveryDetail = (DeliveryDetail)controller.getTo();
		if (deliveryDetail.getItem().getProduct().isComposition()) {
			double quantity = deliveryDetail.getQuantity();
			try {
				for (ItemComposition composition : deliveryDetail.getItem().getItemCompositionList()) {
					deliveryDetail.setId(null);
					deliveryDetail.setLine(deliveryDetail.getLine()+1);
					deliveryDetail.setItem(composition.getCompositionItem());
					deliveryDetail.setDescription(composition.getDescription());
					deliveryDetail.setQuantity(CommonUtil.round(quantity * composition.getQuantity(), 3));
					deliveryDetail.setPrice(obtainCompositionItemPrice(deliveryDetail, composition, controller.getPriceStrategy()));
					deliveryDetail.setDiscountExpression(obtainCompositionDiscount(deliveryDetail, composition));
					deliveryDetail = (DeliveryDetail)controller.getManagerBean().insert(deliveryDetail);
				}
			} catch (ManagerBeanException e) {
				throw new ControllerListenerException(e.getMessage(), e);
			}
		}
	}

	private double obtainCompositionItemPrice(DeliveryDetail deliveryDetail, ItemComposition composition, IPriceStrategy priceStrategy) {
		double price = 0;
		if (composition.getItem().getProduct().isCompositionPrice()) {
			Delivery delivery = deliveryDetail.getDelivery();
			price = priceStrategy.getUnitPrice(deliveryDetail, delivery.getIssueTime(), delivery.getCustomer().getTariff());
		}
		return price;
	}

	private DiscountExpression obtainCompositionDiscount(DeliveryDetail deliveryDetail, ItemComposition composition) {
		DiscountExpression discountExpr = new DiscountExpression("0.0");
		if (composition.getItem().getProduct().isCompositionPrice()) {
			if (composition.getDiscountExpression() != null) {
				discountExpr = composition.getDiscountExpression();
			}
		}
		return discountExpr;
	}

}
