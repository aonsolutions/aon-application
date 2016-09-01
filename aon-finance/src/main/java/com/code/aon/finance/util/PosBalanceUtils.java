package com.code.aon.finance.util;

import com.code.aon.config.PayMethod;
import com.code.aon.finance.PosShift;

public class PosBalanceUtils {

	public static boolean isPosShiftImbalance(PosShift posShift) {
		posShift.setTotalShiftCountMap(null);
		boolean imbalance = false;
		for (PayMethod payMethod : posShift.getTotalShiftCountMap().keySet()) {
			double[] totals = posShift.getTotalShiftCountMap().get(payMethod);
			if (totals[0] != totals[1]) {
				imbalance = true;
				break;
			}
		}
		return imbalance;
	}

}
