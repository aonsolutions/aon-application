package com.code.aon.ui.account.controller;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.account.AccountEntryDetail;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.menu.jsf.MenuEvent;

public class AccountEntryReportController extends BasicController {
	
	private static final Logger LOGGER = Logger.getLogger(AccountEntryReportController.class.getName());
	
	private boolean daybookMode;
    private boolean abstractMode;
    private String period;
    private Date fromDate;
	private Date toDate;
	
    public boolean isDaybookMode() {
        return daybookMode;
    }

    public void setDaybookMode(boolean daybookMode) {
        this.daybookMode = daybookMode;
    }
    
	public boolean isAbstractMode() {
		return abstractMode;
	}
	
	public void setAbstractMode(boolean abstractMode) {
		this.abstractMode = abstractMode;
	}

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public void addEntryDateFromExpression(ValueChangeEvent event){
		if(event.getNewValue() != null){
			try {
				IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
				getCriteria().addGreaterThanOrEqualExpression(accountEntryDetailBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ENTRY_DATE), event.getNewValue());
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error adding FROM date expression", e);
			}
		}
	}
	
	public void addEntryDateToExpression(ValueChangeEvent event){
		if(event.getNewValue() != null){
			try {
				IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
				getCriteria().addLessThanOrEqualExpression(accountEntryDetailBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ENTRY_DATE), event.getNewValue());
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error adding TO date expression", e);
			}
		}
	}
	
    public void addAccountExpression(ValueChangeEvent event){
        if(event.getNewValue() != null && !"".equals(event.getNewValue())){
            try {
                IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
                getCriteria().addEqualExpression(accountEntryDetailBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ID), event.getNewValue());
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error adding account expression", e);
            }
        }
    }
    
    public void addAccountFromExpression(ValueChangeEvent event){
        if(event.getNewValue() != null && !"".equals(event.getNewValue())){
            try {
                IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
                getCriteria().addGreaterThanOrEqualExpression(accountEntryDetailBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ID), event.getNewValue());
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error adding account expression", e);
            }
        }
    }
    
    public void addAccountToExpression(ValueChangeEvent event){
        if(event.getNewValue() != null && !"".equals(event.getNewValue())){
            try {
                IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
                getCriteria().addLessThanOrEqualExpression(accountEntryDetailBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ID), event.getNewValue());
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error adding account expression", e);
            }
        }
    }
    
	@SuppressWarnings("unused")
	public void onDayBook(MenuEvent event) throws ManagerBeanException {
		this.setDaybookMode(true);
		this.setAbstractMode(false);
		this.onEditSearch(null);
	}

    @SuppressWarnings("unused")
    public void onAbstract(MenuEvent event) throws ManagerBeanException{
        this.setDaybookMode(false);
        this.setAbstractMode(true);
        this.onEditSearch(null);
    }

    @SuppressWarnings("unused")
    public void onProfitAndLoss(MenuEvent event) throws ManagerBeanException{
        this.setDaybookMode(false);
        this.setAbstractMode(false);
        this.onEditSearch(null);
    }

	@Override
	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
		this.setFromDate(null);
		this.setToDate(null);
		this.setPeriod(null);
	}

}
