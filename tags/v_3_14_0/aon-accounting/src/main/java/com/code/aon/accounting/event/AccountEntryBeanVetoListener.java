package com.code.aon.accounting.event;

import java.util.Date;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.dao.IAccountingAlias;
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
            if(toDate.before(pFrom) || toDate.after(pTo))
            	throw new ManagerBeanVetoListenerException("La Fecha del Asiento no está dentro del Periodo "+period.getId());
        } catch (ManagerBeanException e) {
            throw new ManagerBeanVetoListenerException(e);
        }
	}

}
