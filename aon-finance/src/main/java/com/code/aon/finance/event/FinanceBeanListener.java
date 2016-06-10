package com.code.aon.finance.event;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.config.PayMethod;
import com.code.aon.finance.Finance;
import com.code.aon.finance.PosShift;

public class FinanceBeanListener extends ManagerBeanListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void beanInserted(ManagerBeanEvent evt) throws ManagerBeanException {
		Finance finance = (Finance) evt.getTo();
		if (!finance.isSkipCheckPosShift()) {
			if (finance.getInvoice() != null && finance.getInvoice().getPosShift() != null && finance.getInvoice().getPosShift().getId() != null) {
				checkPosShift(finance.getInvoice().getPosShift());
			}
		}
		finance.setSkipCheckPosShift(false);
	}

	@Override
	public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException {
		Finance finance = (Finance) evt.getTo();
		if (!finance.isSkipCheckPosShift()) {
			if (finance.getInvoice() != null && finance.getInvoice().getPosShift() != null && finance.getInvoice().getPosShift().getId() != null) {
				checkPosShift(finance.getInvoice().getPosShift());
			}
		}
		finance.setSkipCheckPosShift(false);
	}

	@Override
	public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException {
		Finance finance = (Finance) evt.getTo();
		if (!finance.isSkipCheckPosShift()) {
			if (finance.getInvoice() != null && finance.getInvoice().getPosShift() != null && finance.getInvoice().getPosShift().getId() != null) {
				checkPosShift(finance.getInvoice().getPosShift());
			}
		}
		finance.setSkipCheckPosShift(false);
	}

	private void checkPosShift(PosShift posShift) throws ManagerBeanException {
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

			if (imbalance != posShift.isImbalance()) {
				posShift.setImbalance(imbalance);
				posShift.setSkipCheckPosShift(true);
				posShift = (PosShift)BeanManager.getManagerBean(PosShift.class).update(posShift);
			}
		}
	}

}
