package com.code.aon.fiscal.model;

import java.util.Collection;

import com.code.aon.common.util.CommonUtil;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.Mod303Key;

public class FiscalModelDetailCalculator {

	protected void calculateDetails(Collection<FiscalModelDetail> details) {
		for (FiscalModelDetail detail : details) {
			int round = 2;
			if (detail.getKey() == Mod303Key.CAG1_V2 || detail.getKey() == Mod303Key.CAG2_V2) {
				round = 5;
			}
			if (detail.getKey().isDifEnabled() ) {
				detail.setResultAmount(CommonUtil.round(detail.getAccumulatedAmount() - detail.getDeclaredAmount(),round));	
			} else {
				detail.setResultAmount(CommonUtil.round(detail.getAccumulatedAmount(),round));
			}
			detail.setAmount(CommonUtil.round(detail.getResultAmount() + detail.getAdjustAmount(),round));
		}
	}

}
