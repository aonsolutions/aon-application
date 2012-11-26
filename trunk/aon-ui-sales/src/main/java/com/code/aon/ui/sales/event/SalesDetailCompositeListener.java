package com.code.aon.ui.sales.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.ItemComposition;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.sales.controller.SalesDetailController;

public class SalesDetailCompositeListener extends ControllerAdapter {

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		SalesDetailController controller = (SalesDetailController)event.getController();
		SalesDetail salesDetail = (SalesDetail)controller.getTo();
		if (salesDetail.getItem().getProduct().isComposition()) {
			double quantity = salesDetail.getQuantity();
			try {
				for (ItemComposition composition : salesDetail.getItem().getItemCompositionList()) {
					salesDetail.setId(null);
					salesDetail.setLine(salesDetail.getLine()+1);
					salesDetail.setItem(composition.getCompositionItem());
					salesDetail.setDescription(composition.getDescription());
					salesDetail.setQuantity(CommonUtil.round(quantity * composition.getQuantity(), 3));
					salesDetail.setPrice(obtainCompositionItemPrice(salesDetail, composition, controller.getPriceStrategy()));
					salesDetail.setDiscountExpression(obtainCompositionDiscount(salesDetail, composition));
					salesDetail = (SalesDetail)controller.getManagerBean().insert(salesDetail);
				}
			} catch (ManagerBeanException e) {
				throw new ControllerListenerException(e.getMessage(), e);
			}
		}
	}

	private double obtainCompositionItemPrice(SalesDetail salesDetail, ItemComposition composition, IPriceStrategy priceStrategy) {
		double price = 0;
		if (composition.getItem().getProduct().isCompositionPrice()) {
			Sales sales = salesDetail.getSales();
			price = priceStrategy.getUnitPrice(salesDetail, sales.getIssueDate(), sales.getCustomer().getTariff());
		}
		return price;
	}

	private DiscountExpression obtainCompositionDiscount(SalesDetail salesDetail, ItemComposition composition) {
		DiscountExpression discountExpr = new DiscountExpression("0.0");
		if (composition.getItem().getProduct().isCompositionPrice()) {
			if (composition.getDiscountExpression() != null) {
				discountExpr = composition.getDiscountExpression();
			}
		}
		return discountExpr;
	}

}
