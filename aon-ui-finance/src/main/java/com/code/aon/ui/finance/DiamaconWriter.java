package com.code.aon.ui.finance;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.account.Account;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.report.ReportException;
import com.code.aon.report.poi.ExcelReportExporter;
import com.code.aon.report.poi.ReportColumnMetadata;
import com.code.aon.report.poi.ReportMetadata;
import com.code.aon.ui.util.AonUtil;

public class DiamaconWriter extends BasicExporter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(DiamaconWriter.class.getName());
	
	private final String SHEET_SALES = "Ventas";
	private final String SHEET_PURCHASE = "Compras";
	private final String SHEET_SALARY = "Estandar";
	private final String SHEET_BANK = "Banco 1";
	
	private ExcelReportExporter exporter;
	
	private String defaultSalesAccount;
	private String defaultPurchaseAccount;
	
	public DiamaconWriter(InvoiceExportConfiguration configuration) {
		super(configuration);
		
		initDefaultAccounts();
		
		initExcel();
	}	

	@Override
	public InvoiceExportType getType() {
		return InvoiceExportType.DIAMACON;
	}
	
	@Override
	protected boolean isSkipAccount(Account account) {
		return false;
	}
		
	private void initDefaultAccounts() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Account.class);
			String accountId = null;
			Account account = null;

			accountId = AppParamUtil.getParameter(AppParam.ACC_DEFAULT_SALES_ACC).getValue();
			account = (Account) bean.get(Integer.valueOf(accountId));
			defaultSalesAccount = account !=null?account.getCode():"700000000";
			
			accountId = AppParamUtil.getParameter(AppParam.ACC_DEFAULT_PURCHASE_ACC).getValue();
			account = (Account) bean.get(Integer.valueOf(accountId));
			defaultPurchaseAccount = account !=null?account.getCode():"600000000";
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	private void initExcel() {
		this.exporter = new ExcelReportExporter();
		try {
//			ventas
			this.exporter.startExport(SHEET_SALES);
			ReportMetadata salesColumnMetadata = getSalesColumnMetadata();
			this.exporter.exportHeader(salesColumnMetadata);
			
//			compras y gastos
			ReportMetadata purchaseColumnMetadata = getPurchaseColumnMetadata();
			this.exporter.setSheet(this.exporter.createSheet(SHEET_PURCHASE));
			this.exporter.exportHeader(purchaseColumnMetadata);

//			nominas
			ReportMetadata salaryColumnMetadata = getSalaryColumnMetadata();
			this.exporter.setSheet(this.exporter.createSheet(SHEET_SALARY));
			this.exporter.exportHeader(salaryColumnMetadata);
			
//			bancos (cobros y pagos)
			ReportMetadata bankColumnMetadata = getBankColumnMetadata();
			this.exporter.setSheet(this.exporter.createSheet(SHEET_BANK));
			this.exporter.exportHeader(bankColumnMetadata);
			
		} catch (ReportException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	private ReportMetadata getSalesColumnMetadata() throws ReportException {
		ReportMetadata metadata = new ReportMetadata();
		metadata.getColumns().add(new ReportColumnMetadata("col1",Types.DATE,"FECHA",10));
		metadata.getColumns().add(new ReportColumnMetadata("col2",Types.INTEGER,"COD.CLIENTE",10));
		metadata.getColumns().add(new ReportColumnMetadata("col3",Types.VARCHAR,"Nº FRA.",10));
		metadata.getColumns().add(new ReportColumnMetadata("col4",Types.NVARCHAR,"COMENTARIO",10));
		metadata.getColumns().add(new ReportColumnMetadata("col5",Types.DOUBLE,"TOTAL FRA.",10));
		metadata.getColumns().add(new ReportColumnMetadata("col6",Types.INTEGER,"COD. VENTAS",10));
		metadata.getColumns().add(new ReportColumnMetadata("col7",Types.DOUBLE,"TIPO IVA",10));
		metadata.getColumns().add(new ReportColumnMetadata("col8",Types.DOUBLE,"REC. EQ.",10));
		metadata.getColumns().add(new ReportColumnMetadata("col9",Types.INTEGER,"SECCION",10));
		metadata.getColumns().add(new ReportColumnMetadata("col10",Types.DOUBLE,"BASE IMP.",10));
		metadata.getColumns().add(new ReportColumnMetadata("col11",Types.DOUBLE,"CUOTA IVA",10));
		metadata.getColumns().add(new ReportColumnMetadata("col12",Types.DOUBLE,"CUOTA REC.",10));
		return metadata;
	}
	
	private ReportMetadata getPurchaseColumnMetadata() throws ReportException {
		ReportMetadata metadata = new ReportMetadata();
		metadata.getColumns().add(new ReportColumnMetadata("col1",Types.DATE,"FECHA",10));
		metadata.getColumns().add(new ReportColumnMetadata("col2",Types.INTEGER,"COD.PROVEED",10));
		metadata.getColumns().add(new ReportColumnMetadata("col3",Types.VARCHAR,"Nº FRA.",10));
		metadata.getColumns().add(new ReportColumnMetadata("col4",Types.NVARCHAR,"COMENTARIO",10));
		metadata.getColumns().add(new ReportColumnMetadata("col5",Types.DOUBLE,"TOTAL FRA.",10));
		metadata.getColumns().add(new ReportColumnMetadata("col6",Types.INTEGER,"COD. GASTOS",10));
		metadata.getColumns().add(new ReportColumnMetadata("col7",Types.DOUBLE,"TIPO IVA",10));
		metadata.getColumns().add(new ReportColumnMetadata("col8",Types.INTEGER,"SECCION",10));
		metadata.getColumns().add(new ReportColumnMetadata("col9",Types.DOUBLE,"BASE IMP.",10));
		metadata.getColumns().add(new ReportColumnMetadata("col10",Types.DOUBLE,"CUOTA IVA",10));
		return metadata;
	}
	
	private ReportMetadata getSalaryColumnMetadata() throws ReportException {
		ReportMetadata metadata = new ReportMetadata();
		metadata.getColumns().add(new ReportColumnMetadata("col1",Types.DATE,"FECHA",10));
		metadata.getColumns().add(new ReportColumnMetadata("col2",Types.VARCHAR,"Nº DOC.",10));
		metadata.getColumns().add(new ReportColumnMetadata("col3",Types.INTEGER,"CUENTA DEBE",10));
		metadata.getColumns().add(new ReportColumnMetadata("col4",Types.DOUBLE,"IMPORTE D.",10));
		metadata.getColumns().add(new ReportColumnMetadata("col5",Types.INTEGER,"CUENTA HABER",10));
		metadata.getColumns().add(new ReportColumnMetadata("col6",Types.DOUBLE,"IMPORTE H.",10));
		metadata.getColumns().add(new ReportColumnMetadata("col7",Types.NVARCHAR,"COMENTARIO",10));
		metadata.getColumns().add(new ReportColumnMetadata("col8",Types.INTEGER,"SECCION",10));
		return metadata;
	}
	
	private ReportMetadata getBankColumnMetadata() throws ReportException {
		ReportMetadata metadata = new ReportMetadata();
		metadata.getColumns().add(new ReportColumnMetadata("col1",Types.DATE,"FECHA",10));
		metadata.getColumns().add(new ReportColumnMetadata("col2",Types.INTEGER,"COD.BANCO",10));
		metadata.getColumns().add(new ReportColumnMetadata("col3",Types.VARCHAR,"Nº DOC.",10));
		metadata.getColumns().add(new ReportColumnMetadata("col4",Types.DOUBLE,"COBRO",10));
		metadata.getColumns().add(new ReportColumnMetadata("col5",Types.DOUBLE,"PAGO",10));
		metadata.getColumns().add(new ReportColumnMetadata("col6",Types.INTEGER,"CONTRAPART.",10));
		metadata.getColumns().add(new ReportColumnMetadata("col7",Types.NVARCHAR,"COMENTARIO",10));
		metadata.getColumns().add(new ReportColumnMetadata("col8",Types.VARCHAR,"Nº FRA.",10));
		metadata.getColumns().add(new ReportColumnMetadata("col9",Types.INTEGER,"SECCION",10));
		metadata.getColumns().add(new ReportColumnMetadata("col10",Types.DOUBLE,"SALDO",10));
		return metadata;
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
	
	private void addSaleInvoiceDetail( AccountEntry accountEntry, Account account ) {
		if ( isInvoiceExport() ) {
			String codCliente = "";
			String comentario = "";
			if(getDetails().get(0).getBalancingAccount()!=null){
				codCliente = getDetails().get(0).getBalancingAccount().getCode();
				comentario = getDetails().get(0).getBalancingAccount().getDescription();
			}
			Double credit = getDetails().stream()
				.filter(a -> a.getAccount().getId().equals(account.getId()) )
				.mapToDouble(AccountEntryDetail::getCredit).sum();
			Double debit = getDetails().stream()
				.filter(a -> a.getAccount().getId().equals(account.getId()) )
				.mapToDouble(AccountEntryDetail::getDebit).sum();
			double base = credit - debit; 
			double taxPercent = -0.0;
			double surchargePercent = -0.0;
			double taxQuota = 0.0;
			double surchargeQuota = 0.0;
			double total = 0.0;
			List<TaxBreakDown> vats = getTaxes(TaxType.VAT);
			for( TaxBreakDown tbd : vats ) {
				taxPercent = Double.compare(taxPercent, 0.0)<0?tbd.getTaxPercent():((taxPercent+tbd.getTaxPercent())/2);
				surchargePercent = Double.compare(surchargePercent,0.0)<0?tbd.getSurchargePercent():((surchargePercent+tbd.getSurchargePercent())/2);
			}
			taxQuota = base * taxPercent / 100;
			surchargeQuota = base * surchargePercent / 100;
			total = base + taxQuota + surchargeQuota;
			
			
			this.exporter.startLine();		
//			FECHA
			addDateCell(accountEntry.getEntryDate());
//			COD.CLIENTE
			addStringCell( getAccountCode(codCliente) );
//			Nº FRA.
			if( getConfiguration().getInvoiceNumberMaxLength() != null 
				&& getConfiguration().getInvoiceNumberMaxLength() > 0 ){
				addStringCell(getInvoiceSeries()
						+ StringUtils.leftPad(
								getInvoiceNumber().toString(),
								(getConfiguration()
										.getInvoiceNumberMaxLength() - getInvoiceSeries()
										.length()), "0"));
			} else {
				addStringCell( getInvoice().getDocumentNumber() );
			}
//			COMENTARIO
			addStringCell( comentario );
//			TOTAL FRA.
			this.exporter.addDecimalCell( total );
//			COD. VENTAS
			if(account!=null){
				this.exporter.addStringCell( account.getCode() );
			} else {
				this.exporter.addStringCell( defaultSalesAccount );
			}
//			TIPO IVA
			this.exporter.addDecimalCell( CommonUtil.round(taxPercent) );
//			REC. EQ.
			this.exporter.addDecimalCell( CommonUtil.round(surchargePercent) );
//			SECCION
			if(getInvoice().getProject()!=null){
				this.exporter.addStringCell(getInvoice().getProject().getName());
			} else {
				this.exporter.addCell();
			}
//			BASE IMP.
			this.exporter.addDecimalCell( CommonUtil.round(base) );
//			CUOTA IVA
			this.exporter.addDecimalCell( CommonUtil.round(taxQuota) );
//			CUOTA REC.
			this.exporter.addDecimalCell( CommonUtil.round(surchargeQuota) );
			
			this.exporter.endLine();
		}
	}
	
	private void addPurchaseInvoiceDetail( AccountEntry accountEntry, Account account ) {
		if ( isInvoiceExport() ) {			
			String codProveed = "";
			String comentario = "";
			if(getDetails().get(0).getBalancingAccount()!=null){
				codProveed = getDetails().get(0).getBalancingAccount().getCode();
				comentario = getDetails().get(0).getBalancingAccount().getDescription();
			}
			Double credit = getDetails().stream()
				.filter(a -> a.getAccount().getId().equals(account.getId()) )
				.mapToDouble(AccountEntryDetail::getCredit).sum();
			Double debit = getDetails().stream()
				.filter(a -> a.getAccount().getId().equals(account.getId()) )
				.mapToDouble(AccountEntryDetail::getDebit).sum();
			double base = - credit + debit; 
			double taxPercent = -0.0;
			double taxQuota = 0.0;
			double total = 0.0;
			List<TaxBreakDown> vats = getTaxes(TaxType.VAT);
			for( TaxBreakDown tbd : vats ) {
				taxPercent = Double.compare(taxPercent, 0.0)<0?tbd.getTaxPercent():((taxPercent+tbd.getTaxPercent())/2);
			}
			taxQuota = base * taxPercent / 100;
			total = base + taxQuota;
			
			
			
			this.exporter.startLine();
//			FECHA
			addDateCell(accountEntry.getEntryDate());
//			COD.PROVEED
			addStringCell( getAccountCode(codProveed) );
//			Nº FRA.
			if(getConfiguration().getInvoiceNumberMaxLength()!=null){
				addStringCell( StringUtils.right(getReferenceCode(), getConfiguration().getInvoiceNumberMaxLength()) );
			} else {
				addStringCell( getReferenceCode() );
			}
//			COMENTARIO
			addStringCell( comentario );
//			TOTAL FRA.
			this.exporter.addDecimalCell( total );
//			COD. GASTOS
			if(account!=null){
				this.exporter.addStringCell( account.getCode() );
			} else {
				this.exporter.addStringCell( defaultPurchaseAccount );
			}
//			TIPO IVA
			this.exporter.addDecimalCell( taxPercent );
//			SECCION
			if(getInvoice().getProject()!=null){
				this.exporter.addStringCell(getInvoice().getProject().getName());
			} else {
				this.exporter.addCell();
			}
//			BASE IMP.
			this.exporter.addDecimalCell( base );
//			CUOTA IVA
			this.exporter.addDecimalCell( taxQuota );			
			
			this.exporter.endLine();
		}
	}
	
	private void addBankDetail( AccountEntry accountEntry, AccountEntryDetail aed ) {
		
		aed = getNextDetail();
		
		this.exporter.startLine();
		
//		FECHA
		addDateCell(accountEntry.getEntryDate());
//		COD.BANCO
		if(aed.getBalancingAccount()!=null){
			addStringCell( getAccountCode(aed.getBalancingAccount().getCode()) );
		} else {
			this.exporter.addCell();
		}
//		TODO Nº DOC.
		this.exporter.addCell();
//		COBRO
		this.exporter.addDecimalCell( aed.getCredit() );
//		PAGO
		this.exporter.addDecimalCell( aed.getDebit() );
//		CONTRAPART.
		if(aed.getAccount()!=null){
			addStringCell( getAccountCode(aed.getAccount().getCode()) );
		} else {
			this.exporter.addCell();
		}
//		COMENTARIO
		if(aed.getBalancingAccount()!=null){
			addStringCell( aed.getBalancingAccount().getDescription() );
		} else {
			this.exporter.addCell();
		}
//		Nº FRA.
		addStringCell( aed.getDocumentNumber() );
//		SECCION
		if(getInvoice().getProject()!=null){
			this.exporter.addStringCell(getInvoice().getProject().getName());
		} else {
			this.exporter.addCell();
		}
//		TODO SALDO
		this.exporter.addCell();
		
		this.exporter.endLine();
		
	}
	
	private void forwardNextInvoice(String documentNumber) {
		if(!getDetails().isEmpty()){
			AccountEntryDetail aed = getNextDetail();
			while ( !getDetails().isEmpty() && aed.getDocumentNumber().equals(documentNumber) ) {
				getNextDetail();
			}
		}
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
	
	@Override
	public void write( AccountEntry accountEntry ) throws IOException, ManagerBeanException {
			
		while (! getDetails().isEmpty() ) {
			
			try {
				
				if(accountEntry.getType()==AccountEntryType.SALES_INVOICE){
					
					this.exporter.restoreSheet(SHEET_SALES);
					getDetails().stream()
						.map(AccountEntryDetail::getAccount)
						.filter(account -> (!account.getCode().startsWith("4")))
						.distinct()
						.forEach(account -> {
							addSaleInvoiceDetail(accountEntry, account);
						});
					forwardNextInvoice(getInvoice().getDocumentNumber());
					
				} else if(accountEntry.getType()==AccountEntryType.PURCHASE_INVOICE || accountEntry.getType()==AccountEntryType.EXPENSE_INVOICE){
					
					this.exporter.restoreSheet(SHEET_PURCHASE);
					getDetails().stream()
						.map(AccountEntryDetail::getAccount)
						.filter(account -> (!account.getCode().startsWith("4")))
						.distinct()
						.forEach(account -> {
							addPurchaseInvoiceDetail(accountEntry, account);
						});
					forwardNextInvoice(getInvoice().getDocumentNumber());
					
				} else if(accountEntry.getType()==AccountEntryType.PAYMENT || accountEntry.getType()==AccountEntryType.EXPENSES){
					
					this.exporter.restoreSheet(SHEET_BANK);
					addBankDetail(accountEntry, getNextDetail());
					
				} else {
					
					LogPanelController.getInstance().error("Linea omitida. La entrada no se reconoce."
							+ " (" + accountEntry.getType().getName(AonUtil.getCurrentLocale()) + ". " 
							+ getDetails().get(0).getDocumentNumber() + ")");
					getNextDetail();
					
				}
				
			} catch (ReportException e) {
				LOGGER.error(e.getMessage());
			}
			
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