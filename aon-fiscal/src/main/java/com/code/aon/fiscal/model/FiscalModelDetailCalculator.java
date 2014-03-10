package com.code.aon.fiscal.model;

import java.util.Collection;

import com.code.aon.common.util.CommonUtil;
import com.code.aon.fiscal.FiscalModelDetail;

public class FiscalModelDetailCalculator {

	protected void calculateDetails(Collection<FiscalModelDetail> details) {
		for (FiscalModelDetail detail : details) {
			if (detail.getKey().isDifEnabled() ) {
				detail.setResultAmount(CommonUtil.round(detail.getAccumulatedAmount() - detail.getDeclaredAmount()));	
			} else {
				detail.setResultAmount(CommonUtil.round(detail.getAccumulatedAmount()));
			}
			detail.setAmount(CommonUtil.round(detail.getResultAmount() + detail.getAdjustAmount()));
		}
	}

}
