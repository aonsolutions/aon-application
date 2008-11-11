package com.code.aon.ui.finance.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.code.aon.account.AccountEntry;
import com.code.aon.account.bridge.AccountEntryFinanceTracking;
import com.code.aon.account.bridge.writer.AccountEntryFinanceWriter;
import com.code.aon.account.bridge.writer.FinanceRecordingTo;
import com.code.aon.account.enumeration.AccountEntryType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.RegistryBank;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.invoicing.FinanceTrackingWriter;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class FinanceReturnController extends BasicController {
	
	private static final Logger LOGGER = Logger.getLogger(FinanceReturnController.class.getName());
	
	private Boolean payment;
	
	private Date returnDate;
	
	private AccountEntryFinanceWriter writer;
	
	public Boolean getPayment() {
		return payment;
	}

	public void setPayment(Boolean payment) {
		this.payment = payment;
	}

	public Date getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}

	public AccountEntryFinanceWriter getWriter() {
		if(writer == null){
			writer = new AccountEntryFinanceWriter();
		}
		return writer;
	}

	@Override
	public void onEditSearch(ActionEvent event) {
		setPayment(Boolean.FALSE);
		super.onEditSearch(event);
	}
	
	@SuppressWarnings("unused")
	public void onReturn(ActionEvent event) throws ManagerBeanException{
		Finance finance = (Finance)this.getTo();
		finance.setFinanceStatus(FinanceStatus.RETURNED);
		if(finance.getPayMethod().getId() == null){
			finance.setPayMethod(null);
		}
		if(finance.getBank().getId() == null){
			finance.setBank(null);
		}
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		financeBean.update(finance);
		AccountEntry entry = returnFinance(finance);
		ResourceBundle bundle = ResourceBundle.getBundle(AonUtil.getConfigurationController().getApplicationBundles().get("financeBundle"),FacesContext.getCurrentInstance().getViewRoot().getLocale());
		FinanceTracking tracking = FinanceTrackingWriter.addFinanceTracking(finance, FinanceTrackingType.RETURNED, bundle.getString("aon_finance_tracking_recorded") + " " + entry.getId());
		insertAccountEntryFinanceTracking(entry, tracking);
	}
	
	private AccountEntry returnFinance(Finance finance) throws ManagerBeanException {
		try {
			FinanceRecordingTo recordingTo = new FinanceRecordingTo();
			List<Finance> list = new LinkedList<Finance>();
			list.add(finance);
			recordingTo.setRegistryBank(obtainFinanceBatchRegistryBank(finance));
			recordingTo.setFinanceList(list);
			recordingTo.setDate(getReturnDate());
			recordingTo.setType((finance.isPayment()?AccountEntryType.RETURNED_PAYMENT:AccountEntryType.RETURNED_COLLECTION));
			recordingTo.setSecurityLevel(finance.getSecurityLevel()==null?SecurityLevel.OFFICIAL:finance.getSecurityLevel());
			AccountEntry entry = getWriter().returnFinance(recordingTo);
			return entry;
		} catch (ManagerBeanException e) {
			throw new ManagerBeanException("Error Recording Finance with id= " + finance.getId() , e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private RegistryBank obtainFinanceBatchRegistryBank(Finance finance) throws ManagerBeanException {
		IManagerBean fBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(fBatchDetailBean.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_FINANCE_ID), finance.getId());
		criteria.addEqualExpression(fBatchDetailBean.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_STATUS), FinanceStatus.PAID);
		Iterator iter = fBatchDetailBean.getList(criteria).iterator();
		if(iter.hasNext()){
			FinanceBatchDetail detail = (FinanceBatchDetail)iter.next();
			return detail.getFinanceBatch().getRegistryBank();
		}
		return null;
	}

	private void insertAccountEntryFinanceTracking(AccountEntry entry, FinanceTracking tracking) throws ManagerBeanException {
		IManagerBean accountEntryFinanceTrackingBean = BeanManager.getManagerBean(AccountEntryFinanceTracking.class);
		AccountEntryFinanceTracking accEntryTracking = new AccountEntryFinanceTracking();
		accEntryTracking.setAccountEntry(entry);
		accEntryTracking.setFinanceTracking(tracking);
		accountEntryFinanceTrackingBean.insert(accEntryTracking);
	}
}