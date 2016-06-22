package com.code.aon.ui.finance.controller;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.model.SelectItem;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.BankConcept;
import com.code.aon.finance.Pos;
import com.code.aon.finance.enumeration.BillingPeriod;
import com.code.aon.finance.enumeration.CreditorStatus;
import com.code.aon.finance.enumeration.FinanceBatchStatus;
import com.code.aon.finance.enumeration.FinanceBatchType;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.PosDisplayMode;
import com.code.aon.finance.enumeration.PrepaymentCollect;
import com.code.aon.finance.enumeration.Shift;
import com.code.aon.finance.enumeration.StatementConcept;
import com.code.aon.finance.enumeration.StatementStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.finance.InvoiceExportType;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class FinanceCollectionsController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private List<SelectItem> billingPeriods;
	private List<SelectItem> creditorStatuses;
	private List<SelectItem> financeTrackingTypes;
	private List<SelectItem> financeBatchStatus;
	private List<SelectItem> financeBatchPaymentTypes;
	private List<SelectItem> financeBatchChargeTypes;
	private List<SelectItem> financeBatchPayrollTypes;
	private List<SelectItem> financeStatuses;
	private List<SelectItem> invoiceTypes;
	private List<SelectItem> invoiceStatuses;
	private List<SelectItem> statementConcepts;
	private List<SelectItem> statementStatuses;
	private List<SelectItem> posDisplayModes;
	private List<SelectItem> shifts;
	private List<SelectItem> prepaymentCollects;
	private List<SelectItem> invoiceExportTypes;

	public List<SelectItem> getBillingPeriods() {
		if (billingPeriods == null) {
			Locale locale = AonUtil.getCurrentLocale();
			billingPeriods = new LinkedList<SelectItem>();
			for (BillingPeriod period : BillingPeriod.values()) {
				SelectItem item = new SelectItem(period, period.getName(locale));
				billingPeriods.add(item);			
			}
		}
		return billingPeriods;
	}

	public List<SelectItem> getCreditorStatuses() {
		if (creditorStatuses == null) {
			Locale locale = AonUtil.getCurrentLocale();
			creditorStatuses = new LinkedList<SelectItem>();
			for (CreditorStatus status : CreditorStatus.values()) {
				SelectItem item = new SelectItem(status, status.getName(locale));
				creditorStatuses.add(item);
			}
		}
		return creditorStatuses;
	}

	public List<SelectItem> getFinanceTrackingTypes() {
		if (financeTrackingTypes == null) {
			Locale locale = AonUtil.getCurrentLocale();
			financeTrackingTypes = new LinkedList<SelectItem>();
			for (FinanceTrackingType type : FinanceTrackingType.values()) {
				SelectItem item = new SelectItem(type, type.getName(locale));
				financeTrackingTypes.add(item);
			}
		}
		return financeTrackingTypes;
	}

	public List<SelectItem> getFinanceBatchStatus() {
		if (financeBatchStatus == null) {
			Locale locale = AonUtil.getCurrentLocale();
			financeBatchStatus = new LinkedList<SelectItem>();
			for (FinanceBatchStatus status : FinanceBatchStatus.values()) {
				SelectItem item = new SelectItem(status, status.getName(locale));
				financeBatchStatus.add(item);
			}
		}
		return financeBatchStatus;
	}

	public List<SelectItem> getFinanceBatchPaymentTypes() {
		DomainSwitcher domainSwitcher = (DomainSwitcher)AonUtil.getRegisteredBean(ConfigConstants.DOMAIN_SWITCHER);
		if (financeBatchPaymentTypes == null) {
			Locale locale = AonUtil.getCurrentLocale();
			financeBatchPaymentTypes = new LinkedList<SelectItem>();
			for (FinanceBatchType type : FinanceBatchType.values()) {
				if ((type.isPayment() == null || type.isPayment()) && !type.isPayroll()) {
					if(!type.equals(FinanceBatchType.SEPA_58_ANTICIPO_XML) || 
							(type.equals(FinanceBatchType.SEPA_58_ANTICIPO_XML) && domainSwitcher.isBetaDomain())){
						SelectItem item = new SelectItem(type, type.getName(locale));
						financeBatchPaymentTypes.add(item);
					}
				}
			}
		}
		return financeBatchPaymentTypes;
	}

	public List<SelectItem> getFinanceBatchChargeTypes() {
		DomainSwitcher domainSwitcher = (DomainSwitcher)AonUtil.getRegisteredBean(ConfigConstants.DOMAIN_SWITCHER);
		if (financeBatchChargeTypes == null) {
			Locale locale = AonUtil.getCurrentLocale();
			financeBatchChargeTypes = new LinkedList<SelectItem>();
			for (FinanceBatchType type : FinanceBatchType.values()) {
				if (type.isPayment() == null || !type.isPayment()) {
					if(!type.equals(FinanceBatchType.SEPA_58_ANTICIPO_XML) || 
							(type.equals(FinanceBatchType.SEPA_58_ANTICIPO_XML) && domainSwitcher.isBetaDomain())){
						SelectItem item = new SelectItem(type, type.getName(locale));
						financeBatchChargeTypes.add(item);
					}
				}
			}
		}
		return financeBatchChargeTypes;
	}

	public List<SelectItem> getFinanceBatchPayrollTypes() {
		DomainSwitcher domainSwitcher = (DomainSwitcher)AonUtil.getRegisteredBean(ConfigConstants.DOMAIN_SWITCHER);
		if (financeBatchPayrollTypes == null) {
			Locale locale = AonUtil.getCurrentLocale();
			financeBatchPayrollTypes = new LinkedList<SelectItem>();
			for (FinanceBatchType type : FinanceBatchType.values()) {
				if ((type.isPayment() == null || type.isPayment()) && type.isPayroll()) {
					if(!type.equals(FinanceBatchType.SEPA_58_ANTICIPO_XML) || 
						(type.equals(FinanceBatchType.SEPA_58_ANTICIPO_XML) && domainSwitcher.isBetaDomain())){
						SelectItem item = new SelectItem(type, type.getName(locale));
						financeBatchPayrollTypes.add(item);
					}
				}
			}
		}
		return financeBatchPayrollTypes;
	}


	public List<SelectItem> getFinanceStatuses() {
		if (financeStatuses == null) {
			Locale locale = AonUtil.getCurrentLocale();
			financeStatuses = new LinkedList<SelectItem>();
			for (FinanceStatus status : FinanceStatus.values()) {
				SelectItem item = new SelectItem(status, status.getName(locale));
				financeStatuses.add(item);
			}
		}
		return financeStatuses;
	}

	public List<SelectItem> getInvoiceTypes() {
		if (invoiceTypes == null) {
			Locale locale = AonUtil.getCurrentLocale();
			invoiceTypes = new LinkedList<SelectItem>();
			for (InvoiceType type : InvoiceType.values()) {
				SelectItem item = new SelectItem(type, type.getName(locale));
				invoiceTypes.add(item);
			}
		}
		return invoiceTypes;
	}

	public List<SelectItem> getInvoiceStatuses() {
		if (invoiceStatuses == null) {
			Locale locale = AonUtil.getCurrentLocale();
			invoiceStatuses = new LinkedList<SelectItem>();
			for (InvoiceStatus status : InvoiceStatus.values()) {
				SelectItem item = new SelectItem(status, status.getName(locale));
				invoiceStatuses.add(item);
			}
		}
		return invoiceStatuses;
	}
	
	public List<SelectItem> getStatementConcepts() {
		if (statementConcepts == null) {
			Locale locale = AonUtil.getCurrentLocale();
			statementConcepts = new LinkedList<SelectItem>();
			for (StatementConcept concept : StatementConcept.values()) {
				SelectItem item = new SelectItem(concept, concept.getName(locale));
				statementConcepts.add(item);
			}
		}
		return statementConcepts;
	}
	
	public List<SelectItem> getStatementStatuses() {
		if (statementStatuses == null) {
			Locale locale = AonUtil.getCurrentLocale();
			statementStatuses = new LinkedList<SelectItem>();
			for (StatementStatus status : StatementStatus.values()) {
				SelectItem item = new SelectItem(status, status.getName(locale));
				statementStatuses.add(item);
			}
		}
		return statementStatuses;
	}
	
	public List<SelectItem> getBankConcepts() throws ManagerBeanException {
		List<SelectItem> bankConcepts = new LinkedList<SelectItem>();
		IManagerBean bankConceptBean = BeanManager.getManagerBean(BankConcept.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(bankConceptBean.getFieldName(IEntityAlias.BANK_CONCEPT_NAME));
		Iterator<ITransferObject> iter = bankConceptBean.getList(criteria).iterator();
		while(iter.hasNext()){
			BankConcept bankConcept = (BankConcept)iter.next();
			SelectItem item = new SelectItem(bankConcept, bankConcept.getName());
			bankConcepts.add(item);
		}
		return bankConcepts;
	}		

	public List<SelectItem> getCurrentUserPos() throws ManagerBeanException {
		List<SelectItem> poses = new LinkedList<SelectItem>();
    	for(ITransferObject to : getCurrentUserPosList()){
    		Pos pos = (Pos)to;
    		poses.add(new SelectItem(pos, pos.getName()));
    	}
		return poses;
	}	

	public int getCurrentUserPosCount() throws ManagerBeanException {
		IManagerBean posBean = BeanManager.getManagerBean(Pos.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(posBean.getFieldName(IEntityAlias.POS_ACTIVE), true);
		UserUtils.getInstance().addScopeFilterToCriteria(criteria, posBean.getFieldName(IEntityAlias.POS_WORK_PLACE_SCOPE_ID));
		return posBean.getCount(criteria);
	}
	
	public List<ITransferObject> getCurrentUserPosList() throws ManagerBeanException {
		IManagerBean posBean = BeanManager.getManagerBean(Pos.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(posBean.getFieldName(IEntityAlias.POS_ACTIVE), true);
		UserUtils.getInstance().addScopeFilterToCriteria(criteria, posBean.getFieldName(IEntityAlias.POS_WORK_PLACE_SCOPE_ID));
		criteria.addOrder(posBean.getFieldName(IEntityAlias.POS_NAME));
		return posBean.getList(criteria);
	}
	
	public List<Integer> getCurrentUserPosIds() throws ManagerBeanException {
		List<Integer> list = new LinkedList<Integer>();
		for(ITransferObject to : getCurrentUserPosList()){
			Pos pos = (Pos)to;
			list.add(pos.getId());
		}
		return list;
	}
	
	public List<SelectItem> getPosDisplayModes() {
		if (posDisplayModes == null) {
			Locale locale = AonUtil.getCurrentLocale();
			posDisplayModes = new LinkedList<SelectItem>();
			for (PosDisplayMode posDisplayMode : PosDisplayMode.values()) {
				SelectItem item = new SelectItem(posDisplayMode, posDisplayMode.getName(locale));
				posDisplayModes.add(item);
			}
		}
		return posDisplayModes;
	}

	public List<SelectItem> getShifts() {
		if (shifts == null) {
			Locale locale = AonUtil.getCurrentLocale();
			shifts = new LinkedList<SelectItem>();
			for (Shift shift : Shift.values()) {
				SelectItem item = new SelectItem(shift, shift.getName(locale));
				shifts.add(item);
			}
		}
		return shifts;
	}

	public List<SelectItem> getPrepaymentCollects() {
		if (prepaymentCollects == null) {
			Locale locale = AonUtil.getCurrentLocale();
			prepaymentCollects = new LinkedList<SelectItem>();
			for (PrepaymentCollect prepaymentCollect : PrepaymentCollect.values()) {
				SelectItem item = new SelectItem(prepaymentCollect, prepaymentCollect.getName(locale));
				prepaymentCollects.add(item);
			}
		}
		return prepaymentCollects;
	}

	public List<SelectItem> getInvoiceExportTypes() {
		if (invoiceExportTypes == null) {
			Locale locale = AonUtil.getCurrentLocale();
			invoiceExportTypes = new LinkedList<SelectItem>();
			for (InvoiceExportType type : InvoiceExportType.values()) {
				SelectItem item = new SelectItem(type, type.getName(locale));
				invoiceExportTypes.add(item);
			}
		}
		return invoiceExportTypes;
	}
	
}