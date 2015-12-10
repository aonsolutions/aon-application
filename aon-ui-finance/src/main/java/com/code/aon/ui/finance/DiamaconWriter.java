package com.code.aon.ui.finance;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.account.Account;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.finance.Finance;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.report.ReportException;
import com.code.aon.report.poi.ExcelReportExporter;
import com.code.aon.report.poi.ReportColumnMetadata;
import com.code.aon.report.poi.ReportMetadata;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.util.AonUtil;

public class DiamaconWriter extends BasicExporter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(DiamaconWriter.class.getName());
	
	private CustomExcelReportExporter exporter;
	
	private boolean invoiceHeaderAdded;
	
	private int financeCounter;
	
	public DiamaconWriter(InvoiceExportConfiguration configuration) {
		super(configuration);
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
	
	private void initExcel() {
		this.exporter = new CustomExcelReportExporter();
		try {
			this.exporter.startExport("Ventas");
			ReportMetadata salesColumnMetadata = getSalesColumnMetadata();
			exporter.exportHeader(salesColumnMetadata);
			
//			TODO compras - gastos
//			this.exporter.startSheet("Compras-Gastos");
			ReportMetadata purchaseColumnMetadata = getPurchaseColumnMetadata();
//			exporter.exportHeader(purchaseColumnMetadata);

//			TODO cobros - pagos
//			this.exporter.startSheet("Cobros-Pagos");
			ReportMetadata paymentExpenseColumnMetadata = getPaymentExpenseColumnMetadata();
//			exporter.exportHeader(paymentExpenseColumnMetadata);
			
		} catch (ReportException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	private ReportMetadata getSalesColumnMetadata() throws ReportException {
		ReportMetadata metadata = new ReportMetadata();
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.DATE,"FECHA",10));
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.INTEGER,"COD.CLIENTE",10));
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.VARCHAR,"Nº FRA.",10));
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.NVARCHAR,"COMENTARIO",10));
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.DOUBLE,"TOTAL FRA.",10));
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.INTEGER,"COD. VENTAS",10));
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.DOUBLE,"TIPO IVA",10));
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.DOUBLE,"REC. EQ.",10));
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.INTEGER,"SECCION",10));
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.DOUBLE,"BASE IMP.",10));
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.DOUBLE,"CUOTA IVA",10));
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.DOUBLE,"CUOTA REC.",10));
		return metadata;
	}
	
	private ReportMetadata getPurchaseColumnMetadata() throws ReportException {
		ReportMetadata metadata = new ReportMetadata();
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.DATE,"FECHA",10));
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.INTEGER,"COD.PROVEED",10));
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.VARCHAR,"Nº FRA.",10));
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.NVARCHAR,"COMENTARIO",10));
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.DOUBLE,"TOTAL FRA.",10));
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.INTEGER,"COD. GASTOS",10));
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.DOUBLE,"TIPO IVA",10));
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.INTEGER,"SECCION",10));
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.DOUBLE,"BASE IMP.",10));
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.DOUBLE,"CUOTA IVA",10));
		return metadata;
	}
	
	private ReportMetadata getPaymentExpenseColumnMetadata() throws ReportException {
		ReportMetadata metadata = new ReportMetadata();
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.DATE,"FECHA",10));
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.INTEGER,"COD.BANCO",10));
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.VARCHAR,"Nº DOC.",10));
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.DOUBLE,"COBRO",10));
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.DOUBLE,"PAGO",10));
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.INTEGER,"CONTRAPART.",10));
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.NVARCHAR,"COMENTARIO",10));
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.VARCHAR,"Nº FRA.",10));
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.INTEGER,"SECCION",10));
		metadata.getColumns().add(new ReportColumnMetadata("remuneration",Types.DOUBLE,"SALDO",10));
		return metadata;
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
//		Locale locale = AonUtil.getCurrentLocale();
//		addStringCell( accountEntry.getType().getName(locale) );
//		addStringCell( aed.getDocumentNumber() );
//		this.exporter.addNumberCell( getJournal(accountEntry) );
//		if ( accountEntry.getAccountPeriod() != null ) {
//			addStringCell( accountEntry.getAccountPeriod().getName() );
//		} else {
//			this.exporter.addCell();
//		}				
//		addDateCell( accountEntry.getEntryDate() );
//		addStringCell( getAccountCode(aed.getAccount().getCode()) );
//		addStringCell( aed.getAccount().getDescription() );
//		addStringCell( aed.getConcept() );
//		this.exporter.addDecimalCell( aed.getDebit() );
//		this.exporter.addDecimalCell( aed.getCredit() );
//		if ( aed.getBalancingAccount() != null ) {
//			addStringCell( getAccountCode(aed.getBalancingAccount().getCode()) );
//		} else {
//			this.exporter.addCell();
//		}
		
//		FECHA
		addDateCell(accountEntry.getEntryDate());
//		COD.CLIENTE
		addStringCell( getAccountCode(aed.getBalancingAccount().getCode()) );
//		Nº FRA.
		addStringCell( aed.getDocumentNumber() );
//		COMENTARIO
		addStringCell(accountEntry.getComments());
//		TOTAL FRA.
		this.exporter.addDecimalCell( getTotal() );
//		COD. VENTAS
		addStringCell( getAccountCode(aed.getAccount().getCode()) );
		
		List<TaxBreakDown> vats = getTaxes(TaxType.VAT);
		if(vats!=null && vats.size()>0){
			TaxBreakDown tbd = vats.get(0);
			
//			TIPO IVA
			this.exporter.addDecimalCell( tbd.getTaxPercent() );
//			REC. EQ.
			if ( tbd.getTaxType() == TaxType.VAT ) {
				this.exporter.addDecimalCell( tbd.getSurchargePercent() );
			} else {
				this.exporter.addCell();
			}
//			SECCION
			this.exporter.addCell();
//			BASE IMP.
			this.exporter.addDecimalCell( tbd.getBase() );
//			CUOTA IVA
			this.exporter.addDecimalCell( tbd.getTaxQuota() );			
//			CUOTA REC.
			if ( tbd.getTaxType() == TaxType.VAT ) {
				this.exporter.addDecimalCell( tbd.getSurchargeQuota() );			
			} else {
				this.exporter.addCell();
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
	
	private void addFirstLine( AccountEntry accountEntry ) throws ReportException {
//		this.exporter.startLine();
//		if ( getRegistryDetail() != null ) {
//			addDetail(accountEntry, getRegistryDetail());	
//		}
//		if ( isInvoiceExport() ) {
//			addInvoiceHeader();
//			addInvoice(accountEntry);
//			addTaxes();
//			int i = 0;
//			for( Finance finance : getFinances() ) {
//				addFinance(++i, finance);
//			}
//		}
//		this.exporter.endLine();
	}
	
	@Override
	public void write( AccountEntry accountEntry ) throws IOException, ManagerBeanException {
		try {
			addFirstLine(accountEntry);
			while (! getDetails().isEmpty() ) {
				if(accountEntry.getType()==AccountEntryType.SALES_INVOICE){
					this.exporter.startLine();
					addDetail(accountEntry, getNextDetail());
					this.exporter.endLine();
				} else {
					getNextDetail();
					LOGGER.error("Linea omitida. Actualmente solo se exportan las ventas.");
				}
			}
			
//			this.exporter.startSheet("Ventas");
//			addFirstLine(accountEntry);
//			while (! getDetails().isEmpty() ) {
//				if(accountEntry.getType()==AccountEntryType.SALES_INVOICE){
//					this.exporter.startLine();
////					addDetail(accountEntry, getNextDetail());
//					this.exporter.endLine();
//				}
//			}
//			this.exporter.startSheet("Compras/Gastos");
//			addFirstLine(accountEntry);
//			while (! getDetails().isEmpty() ) {
//				if(accountEntry.getType()==AccountEntryType.PURCHASE_INVOICE || accountEntry.getType()==AccountEntryType.EXPENSE_INVOICE){
//					this.exporter.startLine();
////					addDetail(accountEntry, getNextDetail());
//					this.exporter.endLine();
//				}
//			}
//			this.exporter.startSheet("Cobros/Pagos");
//			addFirstLine(accountEntry);
//			while (! getDetails().isEmpty() ) {
//				if(accountEntry.getType()==AccountEntryType.PAYMENT || accountEntry.getType()==AccountEntryType.EXPENSES){
//					this.exporter.startLine();
////					addDetail(accountEntry, getNextDetail());
//					this.exporter.endLine();
//				}
//			}
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
	
	
	public class CustomExcelReportExporter extends ExcelReportExporter {
		public void startSheet(String name){
			Field sheetField = null;
			Field workbookField = null;
			Field columnCountField = null;
			Field rowCountField = null;
			Field cellCountField = null;
			try {
				// create new sheet in the workbook
				workbookField = ExcelReportExporter.class.getDeclaredField("workbook");
				workbookField.setAccessible(true);
				Workbook workbook = (Workbook) workbookField.get(this);
				Sheet sheet = workbook.createSheet(name);
				
				// init sheet and context
				sheetField = ExcelReportExporter.class.getDeclaredField("sheet");
				sheetField.setAccessible(true);
				sheetField.set(this, sheet);
				columnCountField = ExcelReportExporter.class.getDeclaredField("columnCount");
				columnCountField.setAccessible(true);
				columnCountField.set(this, 0);
			    rowCountField = ExcelReportExporter.class.getDeclaredField("rowCount");
			    rowCountField.setAccessible(true);
			    rowCountField.set(this, 0);
			    cellCountField = ExcelReportExporter.class.getDeclaredField("cellCount");
			    cellCountField.setAccessible(true);
			    cellCountField.set(this, 0);
				
			} catch (SecurityException e) {
				LOGGER.error(e.getMessage());
			} catch (NoSuchFieldException e) {
				LOGGER.error(e.getMessage());
			} catch (IllegalArgumentException e) {
				LOGGER.error(e.getMessage());
			} catch (IllegalAccessException e) {
				LOGGER.error(e.getMessage());
			}
		}
	}
	
	
}