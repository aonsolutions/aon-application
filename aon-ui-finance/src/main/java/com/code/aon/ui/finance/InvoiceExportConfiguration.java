package com.code.aon.ui.finance;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.finance.enumeration.InvoiceType;


public class InvoiceExportConfiguration {
	
	private String enterpriseCode;
	
	private String generalJournal;
	
	private String expensesJournal;
	
	private String purchaseJournal;
	
	private String salesJournal;

	private InvoiceExportType type;
	
	public InvoiceExportConfiguration() {
		this.type = obtainType();
		if ( this.type != null ) {
			this.enterpriseCode = AppParamUtil.getValue(AppParam.AON_INVOICE_EXPORT_ENTERPRISE_ID);
			this.generalJournal = AppParamUtil.getValue(AppParam.AON_INVOICE_EXPORT_JOURNAL);
			this.salesJournal = AppParamUtil.getValue(AppParam.AON_INVOICE_EXPORT_JOURNAL_SALES);
			this.purchaseJournal = AppParamUtil.getValue(AppParam.AON_INVOICE_EXPORT_JOURNAL_PURCHASE);
			this.expensesJournal = AppParamUtil.getValue(AppParam.AON_INVOICE_EXPORT_JOURNAL_EXPENSES);
		}
	}
	
	public void save() {
		String value = String.valueOf(getType().ordinal()); 
		AppParamUtil.insertParameter(AppParam.AON_INVOICE_EXPORT_TYE, value);
		AppParamUtil.insertParameter(AppParam.AON_INVOICE_EXPORT_ENTERPRISE_ID, this.enterpriseCode);
		AppParamUtil.insertParameter(AppParam.AON_INVOICE_EXPORT_JOURNAL, this.generalJournal);
		AppParamUtil.insertParameter(AppParam.AON_INVOICE_EXPORT_JOURNAL_SALES, this.salesJournal);
		AppParamUtil.insertParameter(AppParam.AON_INVOICE_EXPORT_JOURNAL_PURCHASE, this.purchaseJournal);
		AppParamUtil.insertParameter(AppParam.AON_INVOICE_EXPORT_JOURNAL_EXPENSES, this.expensesJournal);		
	}
	
	public boolean isConfigured() {
		return (this.type != null) && !StringUtils.isEmpty(this.enterpriseCode);
	}

	public String getEnterpriseCode() {
		return enterpriseCode;
	}

	public void setEnterpriseCode(String enterpriseCode) {
		this.enterpriseCode = enterpriseCode;
	}

	public String getGeneralJournal() {
		return generalJournal;
	}

	public void setGeneralJournal(String generalJournal) {
		this.generalJournal = generalJournal;
	}

	public String getExpensesJournal() {
		return expensesJournal;
	}

	public void setExpensesJournal(String expensesJournal) {
		this.expensesJournal = expensesJournal;
	}

	public String getPurchaseJournal() {
		return purchaseJournal;
	}

	public void setPurchaseJournal(String purchaseJournal) {
		this.purchaseJournal = purchaseJournal;
	}

	public String getSalesJournal() {
		return salesJournal;
	}

	public void setSalesJournal(String salesJournal) {
		this.salesJournal = salesJournal;
	}

	public String getJournal( InvoiceType type ) {
		String journal = this.generalJournal;
		switch ( type ) {
			case SALES:
				journal = this.salesJournal;
				break;
			case PURCHASE:
				journal = this.purchaseJournal;
				break;
			case EXPENSES:
				journal = this.expensesJournal;
				break;
		}
		return journal;
	}
	
	public InvoiceExportType obtainType() {
		InvoiceExportType type = null;
		Integer ordinal = AppParamUtil.getValueAsInteger(AppParam.AON_INVOICE_EXPORT_TYE);
		if ( (ordinal!= null) && (ordinal < InvoiceExportType.values().length) ) {
			type = InvoiceExportType.values()[ordinal];
		}
		return type;
	}
	
	public InvoiceExportType getType() {
		return type;
	}

	public void setType(InvoiceExportType type) {
		this.type = type;
	}
	
	public int getEnterpriseCodeLength() {
		if ( getType() != null ) {
			switch ( getType() ) {
				case GEYCE:
					return 6;
				case A3:
					return 5;
			}
		}
		return 6;
	}
	
}
