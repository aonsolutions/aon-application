package com.code.aon.ui.finance;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.Account;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.finance.Finance;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.report.ReportException;
import com.code.aon.report.poi.ExcelReportExporter;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.util.AonUtil;

public class ExcelWriter extends BasicExporter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelWriter.class.getName());
	
	private ExcelReportExporter exporter;
	
	private boolean invoiceHeaderAdded;
	
	private int financeCounter;
	
	public ExcelWriter(InvoiceExportConfiguration configuration) {
		super(configuration);
		initExcel();
	}	

	@Override
	public InvoiceExportType getType() {
		return InvoiceExportType.EXCEL;
	}
	
	@Override
	protected boolean isSkipAccount(Account account) {
		return false;
	}	
	
	private void initExcel() {
		this.exporter = new ExcelReportExporter();
		try {
			this.exporter.startExport(AonUtil.getMessage("aon_invoices"));
			this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.TYPE));
			this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.DOCUMENT_NUMBER));
			this.exporter.addHeaderCell(AonUtil.getMessage("accounting_journal"));			
			this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.FINANCIAL_YEAR));
			this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.DATE));
			this.exporter.addHeaderCell(AonUtil.getMessage("account_account"));
			this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.AON_DESCRIPTION));
			this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.CONCEPT));
			this.exporter.addHeaderCell(AonUtil.getMessage("account_debit"));
			this.exporter.addHeaderCell(AonUtil.getMessage("account_credit"));
			this.exporter.addHeaderCell(AonUtil.getMessage("finance_balancing_account"));			
		} catch (ReportException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	private void addInvoiceHeader() {
		if (! this.invoiceHeaderAdded) {
			this.financeCounter = 0;
			this.exporter.addHeaderCell(AonUtil.getMessage("finance_invoice"));
			this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.DATE));	
			this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.TOTAL));
			this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.COMPANY_NAME));
			this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.TYPE));
			this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.DOCUMENT_COUNTRY));
			this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.DOCUMENT));
			this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.ADDRESS));
			this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.POSTAL_CODE));
			this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.REGISTRY_CITY));
			this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.STATE));
			this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.TRANSACTION_TYPE));
			this.exporter.addHeaderCell(AonUtil.getMessage("invoice_investment"));
			this.exporter.addHeaderCell(AonUtil.getMessage("finance_tax_date"));			
			this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.STATUS));
			for( int i = 0; i < 3; i++ ) {
				this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.TAXABLE_BASE));
				this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.VAT));
				this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.VAT_QUOTA));
				this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.SURCHARGE));
				this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.SURCHARGE_QUOTA));					
			}
			for( int i = 0; i < 2; i++ ) {
				this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.TAXABLE_BASE));
				this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.RETENTION));
				this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.RETENTION_QUOTA));
			}
			this.invoiceHeaderAdded = true;
		}
	}
	
	private void addEmptyCells( int count ) {
		for( int i = 0; i < count; i++ ) {
			this.exporter.addCell();
		}
	}
	
	private void addStringCell( String value ) {
		if (! StringUtils.isBlank(value) ) {
			this.exporter.addStringCell(value);
		} else {
			this.exporter.addCell();
		}
	}

	private void addDateCell( Date value ) {
		if ( value != null ) {
			this.exporter.addDateCell(value);
		} else {
			this.exporter.addCell();
		}
	}
	
	private void addTax( TaxBreakDown tbd ) {
		this.exporter.addDecimalCell( tbd.getBase() );
		this.exporter.addDecimalCell( tbd.getTaxPercent() );
		this.exporter.addDecimalCell( tbd.getTaxQuota() );
		if ( tbd.getTaxType() == TaxType.VAT ) {
			this.exporter.addDecimalCell( tbd.getSurchargePercent() );
			this.exporter.addDecimalCell( tbd.getSurchargeQuota() );			
		}
	}	
	
	private void addDetail( AccountEntry accountEntry, AccountEntryDetail aed ) {
		Locale locale = AonUtil.getCurrentLocale();
		addStringCell( accountEntry.getType().getName(locale) );
		addStringCell( aed.getDocumentNumber() );
		this.exporter.addNumberCell( getJournal(accountEntry) );
		if ( accountEntry.getAccountPeriod() != null ) {
			addStringCell( accountEntry.getAccountPeriod().getName() );
		} else {
			this.exporter.addCell();
		}				
		addDateCell( accountEntry.getEntryDate() );
		addStringCell( getAccountCode(aed.getAccount().getCode()) );
		addStringCell( aed.getAccount().getDescription() );
		addStringCell( aed.getConcept() );
		this.exporter.addDecimalCell( aed.getDebit() );
		this.exporter.addDecimalCell( aed.getCredit() );
		if ( aed.getBalancingAccount() != null ) {
			addStringCell( getAccountCode(aed.getBalancingAccount().getCode()) );
		} else {
			this.exporter.addCell();
		}		
	}	
	
	private void addInvoice( AccountEntry accountEntry ) {
		Locale locale = AonUtil.getCurrentLocale();
		addStringCell( getReferenceCode() );
		addDateCell( getDate() );
		this.exporter.addDecimalCell( getTotal() );
		addStringCell( getRegistryName() );
		if ( getRegistryDocument() != null ) {
			addStringCell( getRegistryDocument().getType().getName(locale) );
			addStringCell( getRegistryDocument().getCountry().getName(locale) );
			addStringCell( getRegistryDocument().getDocument() );
		} else {
			addEmptyCells(3);
		}
		RegistryAddress address = getRegistryAddress();
		if ( getRegistryAddress() != null ) {
			addStringCell( address.getFullAddress() );
			addStringCell( address.getZip() );
			addStringCell( address.getCity() );
			if ( address.getGeozone() != null ) {
				addStringCell( address.getGeozone().getName() );
			} else {
				this.exporter.addCell();	
			}
		} else {
			addEmptyCells(4);				
		}
		addStringCell( getTransaction().getName(locale) );
		this.exporter.addBooleanCell( isInvestment() );			
		addDateCell( getTaxDate() );
		addStringCell( getInvoice().getStatus().getName(locale) );	
	}

	private List<TaxBreakDown> getTaxes( TaxType type ) {
		List<TaxBreakDown> list = new LinkedList<TaxBreakDown>();
		for( TaxBreakDown tbd : getTaxBreakDowns() ) {
			if ( tbd.getTaxType() == type ) {
				list.add(tbd);
			}
		}
		return list;
	}
	
	private void addTaxes() {
		List<TaxBreakDown> vats = getTaxes(TaxType.VAT);
		for( TaxBreakDown tbd : vats ) {
			addTax(tbd);
		}
		for( int i = 0; i < 5*(3-vats.size()); i++ ) {
			this.exporter.addDecimalCell( 0 );
		}
		List<TaxBreakDown> retentions = getTaxes(TaxType.RETENTION);
		for( TaxBreakDown tbd : retentions ) {
			addTax(tbd);
		}
		for( int i = 0; i < 3*(2-retentions.size()); i++ ) {
			this.exporter.addDecimalCell( 0 );
		}
	}
	
	private void addFinanceHeader() {
		financeCounter++;
		this.exporter.addHeaderCell(AonUtil.getMessage("finance_finance_date"));
		this.exporter.addHeaderCell(AonUtil.getMessage("finance_payMethod"));
		this.exporter.addHeaderCell(AonUtil.getMessage("aon_bank_account"));
		this.exporter.addHeaderCell(AonUtil.getMessage(ICommonMessages.STATUS));
		this.exporter.addHeaderCell(AonUtil.getMessage("aon_amount"));
	}	
	
	private void addFinance( int index, Finance finance ) {
		if ( index > financeCounter ) {
			addFinanceHeader();
		}
		Locale locale = AonUtil.getCurrentLocale();
		addDateCell( finance.getDueDate() );
		if ( finance.getPayMethod() != null ) {
			addStringCell( finance.getPayMethod().getName() );	
		} else {
			this.exporter.addCell();
		}
		addStringCell( finance.getBankDescription() );
		addStringCell( finance.getFinanceStatus().getName(locale) );
		this.exporter.addDecimalCell( finance.getAmount() );
	}
	
	private void addFirstLine( AccountEntry accountEntry ) throws ReportException {
		this.exporter.startLine();
		if ( getRegistryDetail() != null ) {
			addDetail(accountEntry, getRegistryDetail());	
		}
		if ( isInvoiceExport() ) {
			addInvoiceHeader();
			addInvoice(accountEntry);
			addTaxes();
			int i = 0;
			for( Finance finance : getFinances() ) {
				addFinance(++i, finance);
			}
		}
		this.exporter.endLine();
	}
	
	@Override
	public void write( AccountEntry accountEntry ) throws IOException, ManagerBeanException {
		try {		
			addFirstLine(accountEntry);
			while (! getDetails().isEmpty() ) {
				this.exporter.startLine();
				addDetail(accountEntry, getNextDetail());
				this.exporter.endLine();
			}
		} catch (ReportException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	private byte[] getExcelData() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			exporter.autoSizeColumns();
			exporter.endExport(bos);
		} catch (ReportException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return bos.toByteArray();
	}
	
	@Override
	public Map<String, File> getDataMap() {
		Map<String, File> map = new HashMap<String, File>();
		addData(map, "aon", ".xls", getExcelData());
		return map;
	}
	
}