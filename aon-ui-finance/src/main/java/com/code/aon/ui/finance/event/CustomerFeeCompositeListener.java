package com.code.aon.ui.finance.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.CustomerFee;
import com.code.aon.product.ItemComposition;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ui.finance.controller.CustomerFeeController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class CustomerFeeCompositeListener extends ControllerAdapter {

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		CustomerFeeController controller = (CustomerFeeController)event.getController();
		CustomerFee customerFee = (CustomerFee)controller.getTo();
		if (customerFee.getItem().getProduct().isComposition()) {
			double quantity = customerFee.getQuantity();
			try {
				for (ItemComposition composition : customerFee.getItem().getItemCompositionList()) {
					customerFee.setId(null);
					customerFee.setLine(customerFee.getLine()+1);
					customerFee.setItem(composition.getCompositionItem());
					customerFee.setDescription(composition.getDescription());
					customerFee.setQuantity(CommonUtil.round(quantity * composition.getQuantity(), 3));
					customerFee.setPrice(obtainCompositionItemPrice(customerFee, composition, controller.getPriceStrategy()));
					customerFee.setDiscountExpression(obtainCompositionDiscount(composition));
					customerFee = (CustomerFee)controller.getManagerBean().insert(customerFee);
				}
			} catch (ManagerBeanException e) {
				throw new ControllerListenerException(e.getMessage(), e);
			}
		}
	}

	private double obtainCompositionItemPrice(CustomerFee customerFee, ItemComposition composition, IPriceStrategy priceStrategy) {
		double price = 0;
		if (composition.getItem().getProduct().isCompositionPrice()) {
			price = priceStrategy.getUnitPrice(customerFee, customerFee.getInitialDate(), customerFee.getCustomer());
		}
		return price;
	}

	private DiscountExpression obtainCompositionDiscount(ItemComposition composition) {
		DiscountExpression discountExpr = new DiscountExpression("0.0");
		if (composition.getItem().getProduct().isCompositionPrice()) {
			if (composition.getDiscountExpression() != null) {
				discountExpr = composition.getDiscountExpression();
			}
		}
		return discountExpr;
	}

}
