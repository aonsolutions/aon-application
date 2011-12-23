package com.code.aon.accounting.event;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.Period;
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
import com.code.aon.ql.Projection;
import com.esferalia.aon.entity.IEntityAlias;

/**
 * @author Consulting & Development
 *
 */
public class AccountEntryBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		AccountEntry to = (AccountEntry)evt.getTo();
		validateEntryDateInPeriod(evt);
		
        try {
	        IManagerBean accEntryBean = BeanManager.getManagerBean(AccountEntry.class);
	        Criteria criteria = new Criteria();
	        criteria.addEqualExpression(accEntryBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ACCOUNT_PERIOD_ID), to.getAccountPeriod().getId()); 
	        Projection projection = Projection.max(accEntryBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_JOURNAL));
	        Object value = accEntryBean.getUniqueResult(projection, criteria);
			int journal = (value != null) ? ((Integer) value).intValue() : 0;
			to.setJournal(++journal);
        } catch (Exception e) {
	    	// Nada, el numero de diario se graba a null y será necesario regenerar después.
	    }
    }
	
	@Override
    public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		validateEntryDateInPeriod(evt);
    }

	private void validateEntryDateInPeriod(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException{
		AccountEntry to = (AccountEntry)evt.getTo();
		Date toDate = to.getEntryDate();
		Period toPeriod = to.getAccountPeriod();
		Date pFrom, pTo;
		try {
			IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
	        Criteria criteria = new Criteria();
            criteria.addEqualExpression(periodBean.getFieldName(IEntityAlias.PERIOD_ID), toPeriod.getId());
            Period period = (Period)periodBean.getList(criteria).get(0);
        	pFrom = period.getInitiationDate();
    		pTo = period.getDeadline();
    		if (!DateUtils.isSameDay(toDate, pFrom) && !DateUtils.isSameDay(toDate, pTo)
    				&& (toDate.before(pFrom) || toDate.after(pTo))
   				) {
            	throw new ManagerBeanVetoListenerException("La Fecha del Asiento no está dentro del periodo asignado al ejercicio "+period.getName());
            }
            if (period.getStatus() == AccountPeriodStatus.INACTIVE) {
            	throw new ManagerBeanVetoListenerException("El Ejercicio "+period.getName() + " está inactivo.");
            }
            if (period.getStatus() == AccountPeriodStatus.OPERATING 
           		&& to.getType() != AccountEntryType.OPERATING // Se puede generar otro asiento de explotacion, dependiendo del SecurityLevel 
           		&& to.getType() != AccountEntryType.CLOSING // Después de explotacion sólo puede haber un asiento de cierre
           		) {
            	throw new ManagerBeanVetoListenerException("No se permite la introducción o modificación de asientos en el ejercicio "+period.getName() + " porque ya se ha realizado el asiento de explotación.");
            }
            if (period.getStatus() == AccountPeriodStatus.CLOSED
            	&& to.getType() != AccountEntryType.CLOSING // Se puede generar otro asiento de explotacion, dependiendo del SecurityLevel
            	) {
            	throw new ManagerBeanVetoListenerException("No se permite la introducción o modificación de asientos en el ejercicio "+period.getName() + " porque ya se ha realizado el asiento de cierre.");
            }
        } catch (ManagerBeanException e) {
            throw new ManagerBeanVetoListenerException(e);
        }
	}

	@Override
	public void vetoableBeanRemoved(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		try {
			/*
			 *  Gestión del estado del ejercicio. Los estados son
			 *  	1) ACTIVE 
			 *  	2) OPENING 
			 *  	3) OPERATING 
			 *  	4) CLOSED.
			 *  Al borrar un apunte de apertura, cierre o explotación se comprueba si existen los 
			 *  correspondientes apuntes para poner el estado del ejercicio correspondiente.
			 */
			AccountEntry to = (AccountEntry)evt.getTo();
			if (to.getType() == AccountEntryType.OPENING || 
				to.getType() == AccountEntryType.OPERATING || 
				to.getType() == AccountEntryType.CLOSING) {
				IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
				Period period = (Period) periodBean.get(to.getAccountPeriod().getId());
				AccountingUtil au = new AccountingUtil();
				if (to.getType() == AccountEntryType.OPENING) {
					if (au.existsEntry(period, AccountEntryType.OPENING, null, to.getId())) {
						// Si después de borrar apertura, existe otro apertura, se mantiene el estado.
						period.setStatus(AccountPeriodStatus.OPENING);
					} else {
						// Si después de borrar apertura, no existe otro apertura.
						period.setStatus(AccountPeriodStatus.ACTIVE);	
					}
					if (au.existsEntry(period, AccountEntryType.OPERATING, null, to.getId())) {
						// Si después de borrar explotación, existe otro explotación, se mantiene el estado.
						period.setStatus(AccountPeriodStatus.OPERATING);
					} else {
						if (au.existsEntry(period, AccountEntryType.OPENING, null)) {
							// Si después de borrar explotación, existe apertura.
							period.setStatus(AccountPeriodStatus.OPENING);	
						} else {
							// Si después de borrar explotación, no existe apertura.
							period.setStatus(AccountPeriodStatus.ACTIVE);	
						}
					}
				} else if (to.getType() == AccountEntryType.CLOSING) {
					if (au.existsEntry(period, AccountEntryType.CLOSING, null, to.getId())) {
						// Si después de borrar cierre, existe otro cierre, se mantiene el estado.
						period.setStatus(AccountPeriodStatus.CLOSED);	
					} else {
						if (au.existsEntry(period, AccountEntryType.OPERATING, null)) {
							// Si después de borrar cierre, existe otro explotación.
							period.setStatus(AccountPeriodStatus.OPERATING);	
						} else {
							if (au.existsEntry(period, AccountEntryType.OPENING, null)) {
								// Si después de borrar cierre, no existe explotación y sí apertura.
								period.setStatus(AccountPeriodStatus.OPENING);
							} else {
								// Si después de borrar cierre, no existe explotación ni apertura.
								period.setStatus(AccountPeriodStatus.ACTIVE);	
							}
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
