package com.code.aon.ui.commercial.controller;

import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.commercial.OfferDetailCommission;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ui.form.BasicController;

public class OfferDetailCommissionController extends BasicController {

	private IPriceStrategy priceStrategy;
	private static final Logger LOGGER = LoggerFactory.getLogger(OfferController.class.getName());
	
	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}
	
	public double getRowAmount() {
		double tb = 0.0;
		try {
			OfferDetailCommission odc = (OfferDetailCommission) getModel().getRowData();
			if (odc != null && odc.getOfferDetail() != null) {
				tb = getPriceStrategy().getBasePrice(odc.getOfferDetail());
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Unable to calculate taxableBase",e);
		}
		return tb;
	}

	public void onChangeCommission(ValueChangeEvent event ) {
		OfferDetailCommission to = (OfferDetailCommission) getTo();
		double com = (Double) event.getNewValue();
		double tb = getPriceStrategy().getBasePrice( to.getOfferDetail() );
		double amount = CommonUtil.round(tb * com / 100 );
		to.setAmount(amount);
	}
}
