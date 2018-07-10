package com.code.aon.ui.warehouse.event;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.ItemComposition;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.IncomeDetailController;
import com.code.aon.warehouse.IncomeDetail;

public class IncomeDetailCompositeListener extends ControllerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanReset(ControllerEvent event) throws ControllerListenerException {
		IncomeDetailController controller = (IncomeDetailController)event.getController();
		controller.getCompositeHandler().reset();;
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		IncomeDetailController controller = (IncomeDetailController)event.getController();
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		if(ds.isAlphaEnabled()){
			controller.getCompositeHandler().acceptItemComposition();
		} else {
			IncomeDetail incomeDetail = (IncomeDetail)controller.getTo();
			if (incomeDetail.getItem().getProduct().isComposition()) {
				double quantity = incomeDetail.getQuantity();
				try {
					for (ItemComposition composition : incomeDetail.getItem().getItemCompositionList()) {
						incomeDetail.setId(null);
						incomeDetail.setLine(incomeDetail.getLine()+1);
						incomeDetail.setItem(composition.getCompositionItem());
						incomeDetail.setDescription(composition.getDescription());
						incomeDetail.setQuantity(CommonUtil.round(quantity * composition.getQuantity(), 3));
						incomeDetail.setPrice(obtainCompositionItemPrice(composition));
						incomeDetail.setDiscountExpression(new DiscountExpression("0.0"));
						incomeDetail = (IncomeDetail)controller.getManagerBean().insert(incomeDetail);
					}
				} catch (ManagerBeanException e) {
					throw new ControllerListenerException(e.getMessage(), e);
				}
			}
		}
	}

	private double obtainCompositionItemPrice(ItemComposition composition) {
		double price = 0;
		if (composition.getItem().getProduct().isCompositionPrice()) {
			price = composition.getCompositionItem().getPurchasePrice();
		}
		return price;
	}

}
