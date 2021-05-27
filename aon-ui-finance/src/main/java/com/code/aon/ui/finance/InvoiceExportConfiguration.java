package com.code.aon.ui.finance;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.Tax;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;


public class InvoiceExportConfiguration implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceExportConfiguration.class.getName());
	
	private static final Tax EMPTY_TAX = new Tax();
	
	private String enterpriseCode;
	
	private String generalJournal;
	
	private String expensesJournal;
	
	private String purchaseJournal;
	
	private String salesJournal;

	private InvoiceExportType type;
	
	private Integer accountSize;
	
	private Integer invoiceNumberMaxLength;
	
	private Integer[] vats;
	
	private Tax[] taxs;
	
	public InvoiceExportConfiguration() {
		this.type = obtainType();
		if ( this.type != null ) {
			this.enterpriseCode = AppParamUtil.getValue(AppParam.AON_EXPORT_ENTERPRISE_ID);
			this.generalJournal = AppParamUtil.getValue(AppParam.AON_EXPORT_JOURNAL);
			this.salesJournal = AppParamUtil.getValue(AppParam.AON_EXPORT_JOURNAL_SALES);
			this.purchaseJournal = AppParamUtil.getValue(AppParam.AON_EXPORT_JOURNAL_PURCHASE);
			this.expensesJournal = AppParamUtil.getValue(AppParam.AON_EXPORT_JOURNAL_EXPENSES);
			this.accountSize = AppParamUtil.getValueAsInteger(AppParam.AON_EXPORT_ACCOUNT_SIZE);
			this.invoiceNumberMaxLength = AppParamUtil.getValueAsInteger(AppParam.AON_EXPORT_INVOICE_MAX_LENGTH);
			initAccountSize();
			initVats();
		}
	}
	
	private Tax getTax( String idValue ) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Tax.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TAX_TYPE), TaxType.VAT);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TAX_ID), NumberUtils.toInt(idValue));
			List<ITransferObject> list = bean.getList(criteria);
			if (! list.isEmpty() ) {
				return (Tax) list.get(0);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	public void initVats() {
		if ( this.type == InvoiceExportType.DSI_GESTION ) {
			String value = AppParamUtil.getValue(AppParam.AON_EXPORT_VATS);
			this.vats = new Integer[0];
			if ( value != null ) {				
				String values[] = StringUtils.split(value, ',');
				if (! ArrayUtils.isEmpty(values) ) {
					for( int i = 0; i < values.length; i++) {
						String taxIdValue = values[i++];
						if ( NumberUtils.isDigits(taxIdValue) ) {
							Tax tax = getTax(taxIdValue);
							if ( tax != null ) {
								this.taxs = (Tax[]) ArrayUtils.add(this.taxs, tax);
								Integer vat = NumberUtils.toInt(values[i]);
								this.vats = (Integer[]) ArrayUtils.add(this.vats, vat);
							}
						}
					}
				}
			}
			if ( ArrayUtils.isEmpty(this.vats) ) {
				addEmptyTax();
			}
		}
	}

	public void saveVats() {
		StringBuffer sb = new StringBuffer();
		for( int i = 0; i < vats.length; i++ ) {
			if ( vats[i] != null ) {
				if ( sb.length() > 0 ) {
					sb.append(',');
				}
				sb.append(this.taxs[i].getId()).append(',').append(this.vats[i]);
			}
		}
		AppParamUtil.insertParameter(AppParam.AON_EXPORT_VATS, sb.toString());
	}
	
	public void addEmptyTax() {
		this.taxs = (Tax[]) ArrayUtils.add(this.taxs, EMPTY_TAX);
		this.vats = (Integer[]) ArrayUtils.add(this.vats, null);				
	}
	
	public void removeTax( int index ) {
		this.taxs = (Tax[]) ArrayUtils.remove(this.taxs, index);
		this.vats = (Integer[]) ArrayUtils.remove(this.vats, index);
		if ( ArrayUtils.isEmpty(this.taxs) ) {
			addEmptyTax();
		}
	}
	
	public Tax[] getTaxs() {
		return taxs;
	}
	
	public Integer[] getVats() {
		return vats;
	}

	public void initAccountSize() {
		if (this.accountSize == null) {
			switch (this.type) {
				case A3:
					this.accountSize = 12;
					break;
				case EXCEL:
				case DSI_GESTION:
					this.accountSize = 9;
					break;
				case CONTA_PLUS:
					this.accountSize = 10;
					break;					
				default:
					this.accountSize = null;
			}
		}		
	}
	
	public void save() {
		String value = (getType() != null) ? String.valueOf(getType().ordinal()) : null; 
		AppParamUtil.insertParameter(AppParam.AON_EXPORT_TYPE, value);
		AppParamUtil.insertParameter(AppParam.AON_EXPORT_ENTERPRISE_ID, this.enterpriseCode);
		if ( (type == InvoiceExportType.GEYCE) || (type == InvoiceExportType.DSI_GESTION) ) {
			AppParamUtil.insertParameter(AppParam.AON_EXPORT_JOURNAL, this.generalJournal);
			AppParamUtil.insertParameter(AppParam.AON_EXPORT_JOURNAL_SALES, this.salesJournal);
			AppParamUtil.insertParameter(AppParam.AON_EXPORT_JOURNAL_PURCHASE, this.purchaseJournal);			
		} else {
			AppParamUtil.removeParameter(AppParam.AON_EXPORT_JOURNAL);
			AppParamUtil.removeParameter(AppParam.AON_EXPORT_JOURNAL_SALES);
			AppParamUtil.removeParameter(AppParam.AON_EXPORT_JOURNAL_PURCHASE);			
		}
		if ( this.type == InvoiceExportType.GEYCE ) {
			AppParamUtil.insertParameter(AppParam.AON_EXPORT_JOURNAL_EXPENSES, this.expensesJournal);
		} else {
			AppParamUtil.removeParameter(AppParam.AON_EXPORT_JOURNAL_EXPENSES);			
		}
		AppParamUtil.insertParameter(AppParam.AON_EXPORT_ACCOUNT_SIZE, this.accountSize);
		if ( this.type == InvoiceExportType.DSI_GESTION ) {
			saveVats();
		} else {
			AppParamUtil.removeParameter(AppParam.AON_EXPORT_VATS);
		}
		if ( this.type == InvoiceExportType.DIAMACON ) {
			AppParamUtil.insertParameter(AppParam.AON_EXPORT_INVOICE_MAX_LENGTH, this.invoiceNumberMaxLength);
		} else {
			AppParamUtil.removeParameter(AppParam.AON_EXPORT_INVOICE_MAX_LENGTH);
		}
	}
	
	public boolean isConfigured() {
		if (this.type != null) {
			switch (this.type) {
				case A3:
				case GEYCE:
					return !StringUtils.isEmpty(this.enterpriseCode);
				case APLIFISA:
				case LOGIC_WIN:
				case EXCEL:
				case DSI_GESTION:
				case GLASOF:
				case CONTA_PLUS:
				case DIAMACON:
					return true;
			}
		}
		return false;
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
	
	public Integer getAccountSize() {
		return accountSize;
	}

	public void setAccountSize(Integer accountSize) {
		this.accountSize = accountSize;
	}
	
	public Integer getInvoiceNumberMaxLength() {
		return invoiceNumberMaxLength;
	}

	public void setInvoiceNumberMaxLength(Integer invoiceNumberMaxLength) {
		this.invoiceNumberMaxLength = invoiceNumberMaxLength;
	}

	public String getJournal( InvoiceType type ) {
		String journal = null;
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
			case UNDEDUCTIBLE:
				journal = this.generalJournal;
				break;
		}
		if ( StringUtils.isBlank(journal) ) {
			journal = this.generalJournal;
		}
		return journal;
	}
	
	public InvoiceExportType obtainType() {
		InvoiceExportType type = null;
		Integer ordinal = AppParamUtil.getValueAsInteger(AppParam.AON_EXPORT_TYPE);
		if ( ordinal!=null && ordinal<InvoiceExportType.values().length ) {
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
		int length = 6;
		if ( getType() != null ) {
			switch ( getType() ) {
				case GEYCE:
					length = 6;
					break;
				case A3:
					length = 5;
					break;
				case DSI_GESTION:
					length = 3;
					break;
				default:
					length = 0;
			}
		}
		return length;
	}
	
	public boolean isShowEnterpriseCode() {
		if ( getType() != null ) {
			switch ( getType() ) {		
				case GEYCE:
				case A3:
				case DSI_GESTION:
					return true;
				default:
					return false;
			}
		}
		return false;
	}

	public boolean isShowAccountSize() {
		if ( getType() != null ) {
			switch ( getType() ) {		
				case A3:
				case EXCEL:
					return true;
				default:
					return false;
			}
		}
		return false;
	}
	
}
