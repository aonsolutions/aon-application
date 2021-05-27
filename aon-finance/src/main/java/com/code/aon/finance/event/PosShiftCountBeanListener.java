package com.code.aon.finance.event;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.finance.PosShift;
import com.code.aon.finance.PosShiftCount;
import com.code.aon.finance.util.PosBalanceUtils;

public class PosShiftCountBeanListener extends ManagerBeanListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void beanInserted(ManagerBeanEvent evt) throws ManagerBeanException {
		PosShiftCount posShiftCount = (PosShiftCount) evt.getTo();
		if (!posShiftCount.isSkipCheckPosShift()) {
			checkPosShift(posShiftCount.getPosShift());
		}
		posShiftCount.setSkipCheckPosShift(false);
	}

	@Override
	public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException {
		PosShiftCount posShiftCount = (PosShiftCount) evt.getTo();
		if (!posShiftCount.isSkipCheckPosShift()) {
			checkPosShift(posShiftCount.getPosShift());
		}
		posShiftCount.setSkipCheckPosShift(false);
	}

	@Override
	public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException {
		PosShiftCount posShiftCount = (PosShiftCount) evt.getTo();
		if (!posShiftCount.isSkipCheckPosShift()) {
			checkPosShift(posShiftCount.getPosShift());
		}
		posShiftCount.setSkipCheckPosShift(false);
	}

	private void checkPosShift(PosShift posShift) throws ManagerBeanException {
		posShift = (PosShift)BeanManager.getManagerBean(PosShift.class).get(posShift.getId());
		if (posShift.isClosed()) {
			boolean imbalance = PosBalanceUtils.isPosShiftImbalance(posShift);
			if (imbalance != posShift.isImbalance()) {
				posShift.setImbalance(imbalance);
				posShift.setSkipCheckPosShift(true);
				posShift = (PosShift)BeanManager.getManagerBean(PosShift.class).update(posShift);
			}
		}
	}

}
