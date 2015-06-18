package com.code.aon.finance.event;

import com.code.aon.AonVersion;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.config.PayMethod;
import com.code.aon.finance.PosShift;

public class PosShiftBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
    @Override
    public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		PosShift posShift = (PosShift) evt.getTo();
		if (!posShift.isSkipCheckPosShift()) {
			checkPosShift(posShift);
		}
		posShift.setSkipCheckPosShift(false);
    }

	private void checkPosShift(PosShift posShift) {
		if (posShift.isClosed()) {
			posShift.setTotalShiftCountMap(null);
			boolean imbalance = false;
			for (PayMethod payMethod : posShift.getTotalShiftCountMap().keySet()) {
				double[] totals = posShift.getTotalShiftCountMap().get(payMethod);
				if (totals[0] != totals[1]) {
					imbalance = true;
					break;
				}
			}
			posShift.setImbalance(imbalance);
		}
	}

}
