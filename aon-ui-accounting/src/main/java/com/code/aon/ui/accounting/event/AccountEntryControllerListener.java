package com.code.aon.ui.accounting.event;

import java.util.Date;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.accounting.controller.entry.AccountEntryController;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;

public class AccountEntryControllerListener extends ControllerAdapter {

    private int index;
    private Date lastDate;
    private Period lastPeriod;
    private SecurityLevel lastSecurityLevel;
    
	public Period getLastPeriod() {
		return lastPeriod;
	}
	public void setLastPeriod(Period lastPeriod) {
		this.lastPeriod = lastPeriod;
	}
	
	public Date getLastDate() {
    	if (lastDate == null) {
    		lastDate = new Date();
    	}
		return lastDate;
	}
	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}
    public SecurityLevel getLastSecurityLevel() {
    	if (lastSecurityLevel == null) {
    		lastSecurityLevel = SecurityLevel.OFFICIAL;
    	}
		return lastSecurityLevel;
	}
	public void setLastSecurityLevel(SecurityLevel lastSecurityLevel) {
		this.lastSecurityLevel = lastSecurityLevel;
	}


	@Override
    public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
        try {
        	AccountEntryController c = (AccountEntryController) event.getController();
	        AccountEntry to = (AccountEntry)event.getController().getTo();
	        to.setType(AccountEntryType.MANUAL);
	        to.setEntryDate(getLastDate());
	        to.setAccountPeriod(getLastPeriod());
	        to.setSecurityLevel(getLastSecurityLevel());
	        if (to.getAccountPeriod() == null) {
		        Period period = AccountingPeriodUtil.getDefaultPeriod();
		        if (period != null) {
		        	to.setAccountPeriod(period);
		        }
	        }
	        c.setTotalCredit(null);
	        c.setTotalDebit(null);
        } catch (ManagerBeanException e) {
            throw new ControllerListenerException(e);
        }
    }

    
    @Override
    public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
        try {
        	checkEntryTypeBeforeRemove(event);
            index = event.getController().getModel().getRowIndex();
        } catch (ManagerBeanException e) {
            throw new ControllerListenerException(e);
        }
    }

    @Override
    public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
        try {
        	AccountEntryController c = (AccountEntryController) event.getController();
            int rowCount = event.getController().getModel().getRowCount();
            if (index > (rowCount-1)) {
                index = rowCount - 1;
            }
	        c.setTotalCredit(null);
	        c.setTotalDebit(null);
            if (rowCount > 0) {
                event.getController().getModel().setRowIndex(index);
                event.getController().onSelect(null);
            } else {
            	event.getController().onEditSearch(null);
            }
        } catch (ManagerBeanException e) {
            throw new ControllerListenerException(e);
        }
    }

    @Override
    public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
        try {
        	AccountEntryController c = (AccountEntryController) event.getController();
        	AccountEntry entry = (AccountEntry)event.getController().getTo();
        	setLastDate(entry.getEntryDate());
        	setLastPeriod(entry.getAccountPeriod());
        	setLastSecurityLevel(entry.getSecurityLevel());
            Integer id = entry.getId();
            IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(entryBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ID), id);
            event.getController().setCriteria(criteria);
            event.getController().onSearch(null);
	        c.setTotalCredit(null);
	        c.setTotalDebit(null);
        } catch (ManagerBeanException e) {
            throw new ControllerListenerException(e);
        }
    }
    
    @Override
    public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
    	AccountEntryController c = (AccountEntryController) event.getController();
    	c.refreshTotals();
    	c.calculateUpdatableFlag();
    }
    
	public void checkEntryTypeBeforeRemove(ControllerEvent event) throws ControllerListenerException, ManagerBeanException {
		AccountEntry to = (AccountEntry) event.getController().getTo();
		if (to.getType() == AccountEntryType.OPENING || 
			to.getType() == AccountEntryType.OPERATING || 
			to.getType() == AccountEntryType.CLOSING) {
			IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
			Period period = (Period) periodBean.get(to.getAccountPeriod().getId());
			AccountingUtil au = new AccountingUtil();
			if (to.getType() == AccountEntryType.OPENING) {
				if (au.existsEntry(period, AccountEntryType.OPERATING, null, to.getId())) {
					throw new ControllerListenerException("Existe un asiento de explotación en el ejericio");
				}
				if (au.existsEntry(period, AccountEntryType.CLOSING, null, to.getId())) {
					throw new ControllerListenerException("Existe un asiento de cierre en el ejericio");
				}
			} else if (to.getType() == AccountEntryType.OPERATING) {
				if (au.existsEntry(period, AccountEntryType.CLOSING, null, to.getId())) {
					throw new ControllerListenerException("Existe un asiento de cierre en el ejericio");
				}
			}
		}
	}
    
        
}
