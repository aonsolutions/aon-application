package com.code.aon.ui.accounting.event;

import java.util.Date;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.accounting.controller.AccountEntryController;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AccountEntryControllerListener extends ControllerAdapter {

    private int index;
    private Date lastDate;
    private String lastPeriod;
    

    public String getLastPeriod() {
		return lastPeriod;
	}
	public void setLastPeriod(String lastPeriod) {
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


	@Override
    public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
        try {
        	AccountEntryController c = (AccountEntryController) event.getController();
	        AccountEntry to = (AccountEntry)event.getController().getTo();
	        to.setType(AccountEntryType.MANUAL);
	        to.setEntryDate(getLastDate());
	        to.setAccountPeriod(getLastPeriod());
	        if (to.getAccountPeriod() == null) {
		        Period period = AccountingPeriodUtil.getDefaultPeriod();
		        if (period != null) {
		        	to.setAccountPeriod(period.getId());
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
            Integer id = entry.getId();
            IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(entryBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ID), id);
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
    }
}
