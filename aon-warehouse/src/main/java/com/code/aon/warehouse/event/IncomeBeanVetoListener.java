package com.code.aon.warehouse.event;

import java.util.Date;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.enumeration.IncomeStatus;

public class IncomeBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Income income = (Income) evt.getTo();
		setDefaultValues(income);
		checkIncome(income);
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Income income = (Income) evt.getTo();
		checkIncome(income);
	}

	private void setDefaultValues(Income income) {
		if (income.getSecurityLevel() == null) {
			income.setSecurityLevel(SecurityLevel.OFFICIAL);
		}
		if (income.getStatus() == null) {
			income.setStatus(IncomeStatus.PENDING);
		}
		if (income.getScope() == null || income.getScope().getId() == null) {
			income.setScope(income.getSupplier().getScope());
		}
	}

	private void checkIncome(Income income) throws ManagerBeanVetoListenerException {
		int thisYear = CommonUtil.getYear(new Date());
		int incomeYear = CommonUtil.getYear(income.getIssueTime());
		if (incomeYear < (thisYear-5) || incomeYear > (thisYear+1)) {
			throw new ManagerBeanVetoListenerException("La Fecha del Albaran no es correcta.");
		}
	}

}