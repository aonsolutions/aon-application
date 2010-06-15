package com.code.aon.accounting.event;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.enumeration.AccountPeriodStatus;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.ql.Criteria;

/**
 * @author Consulting & Development
 *
 */
public class AccountEntryBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		validateEntryDateInPeriod(evt);
    }
	
	@Override
    public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		validateEntryDateInPeriod(evt);
    }

	private void validateEntryDateInPeriod(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException{
		AccountEntry to = (AccountEntry)evt.getTo();
		Date toDate = to.getEntryDate();
		String toPeriod = to.getAccountPeriod();
		Date pFrom, pTo;
		try {
			IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
	        Criteria criteria = new Criteria();
            criteria.addEqualExpression(periodBean.getFieldName(IAccountingAlias.PERIOD_ID), toPeriod);
            Period period = (Period)periodBean.getList(criteria).get(0);
        	pFrom = period.getInitiationDate();
    		pTo = period.getDeadline();
    		if (!DateUtils.isSameDay(toDate, pFrom) && !DateUtils.isSameDay(toDate, pTo)
    				&& (toDate.before(pFrom) || toDate.after(pTo))
   				) {
            	throw new ManagerBeanVetoListenerException("La Fecha del Asiento no está dentro del Periodo "+period.getId());
            }
            if (period.getStatus() == AccountPeriodStatus.INACTIVE) {
            	throw new ManagerBeanVetoListenerException("El Ejercicio "+period.getId() + " está inactivo.");
            }
            if (period.getStatus() == AccountPeriodStatus.OPERATING && to.getType() != AccountEntryType.CLOSING) {
            	throw new ManagerBeanVetoListenerException("No se permite la introducción o modificación de asientos en el ejercicio "+period.getId() + " porque ya se ha realizado el asiento de explotación.");
            }
            if (period.getStatus() == AccountPeriodStatus.CLOSED) {
            	throw new ManagerBeanVetoListenerException("No se permite la introducción o modificación de asientos en el ejercicio "+period.getId() + " porque ya se ha realizado el asiento de cierre.");
            }
        } catch (ManagerBeanException e) {
            throw new ManagerBeanVetoListenerException(e);
        }
	}

	@Override
	public void vetoableBeanRemoved(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		try {
			AccountEntry to = (AccountEntry)evt.getTo();
			if (to.getType() == AccountEntryType.OPENING || 
				to.getType() == AccountEntryType.OPERATING || 
				to.getType() == AccountEntryType.CLOSING) {
				IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
				Period period = (Period) periodBean.get(to.getAccountPeriod());
				AccountingUtil au = new AccountingUtil();
				if (to.getType() == AccountEntryType.OPENING) {
					period.setStatus(AccountPeriodStatus.ACTIVE);
				} else if (to.getType() == AccountEntryType.OPERATING) {
					if (au.existsEntry(period, AccountEntryType.OPENING, to.getSecurityLevel())) {
						period.setStatus(AccountPeriodStatus.OPENING);	
					} else {
						period.setStatus(AccountPeriodStatus.ACTIVE);	
					}
				} else if (to.getType() == AccountEntryType.CLOSING) {
					if (au.existsEntry(period, AccountEntryType.OPERATING, to.getSecurityLevel())) {
						period.setStatus(AccountPeriodStatus.OPERATING);	
					} else {
						if (au.existsEntry(period, AccountEntryType.OPENING, to.getSecurityLevel())) {
							period.setStatus(AccountPeriodStatus.OPENING);
						} else {
							period.setStatus(AccountPeriodStatus.ACTIVE);	
						}
					}
				}
				periodBean.update(period);
			}
	    } catch (ManagerBeanException e) {
	        throw new ManagerBeanVetoListenerException(e);
	    }
	}

}
