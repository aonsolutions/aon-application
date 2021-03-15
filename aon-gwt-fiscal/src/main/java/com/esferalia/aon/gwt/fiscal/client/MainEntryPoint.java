package com.esferalia.aon.gwt.fiscal.client;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountAnalyticalReport;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountBalanceReport;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountConsolidatedBalanceReport;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleTEDI;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountJournalReport;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountLedgerReport;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountOperatingReport;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountStatementReport;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountTrialBalanceReport;
import com.esferalia.aon.gwt.fiscal.client.accounting.utilities.AccountingUtilities;
import com.esferalia.aon.gwt.fiscal.client.finance.FinanceModule;
import com.esferalia.aon.gwt.fiscal.client.finance.utilities.FinanceUtilities;
import com.esferalia.aon.gwt.fiscal.client.invoice.IRPFReport;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceReport;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceSeriesBreakdown;
import com.esferalia.aon.gwt.fiscal.client.invoice.OperationReport;
import com.esferalia.aon.gwt.fiscal.client.invoice.VatReport;
import com.esferalia.aon.gwt.fiscal.client.matrix.ModelMatrix;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111;
import com.esferalia.aon.gwt.fiscal.client.mod115.Model115;
import com.esferalia.aon.gwt.fiscal.client.mod123.Model123;
import com.esferalia.aon.gwt.fiscal.client.mod130.Model130;
import com.esferalia.aon.gwt.fiscal.client.mod131.Model131;
import com.esferalia.aon.gwt.fiscal.client.mod140.Model140;
import com.esferalia.aon.gwt.fiscal.client.mod180.Model180;
import com.esferalia.aon.gwt.fiscal.client.mod184.Model184;
import com.esferalia.aon.gwt.fiscal.client.mod190.Model190;
import com.esferalia.aon.gwt.fiscal.client.mod193.Model193;
import com.esferalia.aon.gwt.fiscal.client.mod200.Model200;
import com.esferalia.aon.gwt.fiscal.client.mod202.Model202;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303;
import com.esferalia.aon.gwt.fiscal.client.mod347.Model347;
import com.esferalia.aon.gwt.fiscal.client.mod349.Model349;
import com.esferalia.aon.gwt.fiscal.client.mod390.Model390;
import com.esferalia.aon.gwt.fiscal.client.mod390HF.Model390HF;
import com.esferalia.aon.gwt.fiscal.client.rawdoc.RawdocModule;
import com.esferalia.aon.gwt.fiscal.client.registry.CreditorModule;
import com.esferalia.aon.gwt.fiscal.client.registry.CustomerModule;
import com.esferalia.aon.gwt.fiscal.client.registry.SupplierModule;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MainEntryPoint implements EntryPoint {

	final FiscalServiceAsync impl = GWT.create(FiscalService.class);
	
	private static final String ENTRY_POINT_PARAM = "entryPoint";
	//
	//    ================================================================== FISCAL
	//
	private static final String FS_MOD111_ENTRY_POINT = "Model111";
	private static final String FS_MOD115_ENTRY_POINT = "Model115";
	private static final String FS_MOD123_ENTRY_POINT = "Model123";
	private static final String FS_MOD130_ENTRY_POINT = "Model130";
	private static final String FS_MOD131_ENTRY_POINT = "Model131";
	private static final String FS_MOD140_ENTRY_POINT = "Model140";
	private static final String FS_MOD190_ENTRY_POINT = "Model190";
	private static final String FS_MOD193_ENTRY_POINT = "Model193";
	private static final String FS_MOD180_ENTRY_POINT = "Model180";	
	private static final String FS_MOD184_ENTRY_POINT = "Model184";
	private static final String FS_MOD200_ENTRY_POINT = "Model200";
	private static final String FS_MOD202_ENTRY_POINT = "Model202";
	private static final String FS_MOD303_ENTRY_POINT = "Model303";
	private static final String FS_MOD347_ENTRY_POINT = "Model347";
	private static final String FS_MOD349_ENTRY_POINT = "Model349";
	private static final String FS_MOD390_ENTRY_POINT = "Model390";
	private static final String FS_MOD390_HF_ENTRY_POINT = "Model390HF";
	private static final String FS_MODEL_MATRIX_ENTRY_POINT = "ModelMatrix";
	//	
	//    ================================================================== REGISTRY
	//
	private static final String RG_CUSTOMER_ENTRY_POINT = "Customer";
	private static final String RG_SUPPLIER_ENTRY_POINT = "Supplier";
	private static final String RG_CREDITOR_ENTRY_POINT = "Creditor";
	//	
	//    ================================================================== FINANCE
	//
	private static final String FS_FINANCE_ENTRY_POINT = "Finance";
	private static final String FS_INVOICE_REPORT_ENTRY_POINT = "InvoiceReport";
	private static final String FS_INVOICE_SERIES_BREAKDOWN_ENTRY_POINT = "InvoiceSeriesBreakdown";
	private static final String FS_VAT_REPORT_ENTRY_POINT = "VATReport";
	private static final String FS_IRPF_REPORT_ENTRY_POINT = "IRPFReport";
	private static final String FS_FINANCE_UTILITIES_ENTRY_POINT = "FinanceUtilities";
	
	//
	//    ================================================================== ACCOUNTING
	//
	private static final String ACC_ACCOUNT_ENTRY_POINT = "AccountModule";
	private static final String ACC_JOURNAL_REPORT_ENTRY_POINT = "JournalReportModule";
	private static final String ACC_LEDGER_REPORT_ENTRY_POINT = "LedgerReportModule";
	private static final String ACC_STATEMENT_REPORT_ENTRY_POINT = "StatementReportModule";
	private static final String ACC_OPERATING_REPORT_ENTRY_POINT = "AccountOperatingReport";
	private static final String ACC_ANALYTICAL_REPORT_ENTRY_POINT = "AccountAnalyticalReport";
	private static final String ACC_TRIAL_BALANCE_REPORT_ENTRY_POINT = "AccountTrialBalanceReport";
	private static final String ACC_OPERATION_ENTRY_POINT = "OperationReport";
	private static final String ACC_ACCOUNT_ENTRY_ENTRY_POINT = "AccountEntryModule";
	private static final String ACC_ACCOUNT_ENTRY_ENTRY_POINT_TEDI = "AccountEntryModuleTEDI";
	private static final String ACC_ACCOUNTING_UTILITIES_ENTRY_POINT = "AccountingUtilities";
	private static final String ACC_ACCOUNTING_BALANCE_REPORT_ENTRY_POINT = "AccountBalanceReport";
	private static final String ACC_ACCOUNTING_CONSOLIDATED_BALANCE_REPORT_ENTRY_POINT = "AccountConsolidatedBalanceReport";
	//
	//    ================================================================== RAWDOC
	//
	private static final String RAWDOC_ENTRY_POINT = "RawdocModule";

	private static AonData aonData;
	
	@Override
	public void onModuleLoad() {
		String entryPoint = getParameter(GWT.getModuleName(), ENTRY_POINT_PARAM);	
		if(getToken() != null) {
			impl.getAonDataToken(getCurrentDomainName(), getCurrentDomain(), getToken(), new AsyncCallback<AonData>() {
				
				@Override public void onSuccess(AonData result) {
					aonData = result;
					selection(entryPoint);
				}
				
				@Override public void onFailure(Throwable arg0) {}
			});
		} else {
			selection(entryPoint);			
		}

		
	}
	
	private void selection(String entryPoint) {
		if ( entryPoint.equalsIgnoreCase(FS_MOD140_ENTRY_POINT)) {
			GWT.runAsync(Model140.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					Model140 model140 = new Model140();
					model140.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(FS_MOD111_ENTRY_POINT)) {
			GWT.runAsync(Model111.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					Model111 model111 = new Model111();
					model111.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(FS_MOD115_ENTRY_POINT)) {
			GWT.runAsync(Model115.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					Model115 model115 = new Model115();
					model115.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(FS_MOD123_ENTRY_POINT)) {
			GWT.runAsync(Model123.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					Model123 model123 = new Model123();
					model123.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(FS_MOD130_ENTRY_POINT)) {
			GWT.runAsync(Model130.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					Model130 model130 = new Model130();
					model130.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(FS_MOD131_ENTRY_POINT)) {
			GWT.runAsync(Model131.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					Model131 model131 = new Model131();
					model131.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(FS_MOD202_ENTRY_POINT)) {
			GWT.runAsync(Model202.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					Model202 model202 = new Model202();
					model202.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(FS_MOD200_ENTRY_POINT)) {
			GWT.runAsync(Model200.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					Model200 model200 = new Model200();
					model200.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(FS_MOD190_ENTRY_POINT)) {
			GWT.runAsync(Model190.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					Model190 model190 = new Model190();
					model190.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(FS_MOD193_ENTRY_POINT)) {
			GWT.runAsync(Model193.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					Model193 model193 = new Model193();
					model193.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(FS_MOD180_ENTRY_POINT)) {
			GWT.runAsync(Model180.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					Model180 model180 = new Model180();
					model180.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(FS_MOD184_ENTRY_POINT)) {
			GWT.runAsync(Model184.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					Model184 model184 = new Model184();
					model184.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(FS_MOD303_ENTRY_POINT)) {
			GWT.runAsync(Model303.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					Model303 model303 = new Model303();
					model303.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(FS_MOD347_ENTRY_POINT)) {
			GWT.runAsync(Model347.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					Model347 model347 = new Model347();
					model347.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(FS_MOD349_ENTRY_POINT)) {
			GWT.runAsync(Model349.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					Model349 model349 = new Model349();
					model349.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(FS_MOD390_ENTRY_POINT)) {
			GWT.runAsync(Model390.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					Model390 model390 = new Model390();
					model390.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(FS_MOD390_HF_ENTRY_POINT)) {
			GWT.runAsync(Model390HF.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					Model390HF model390HF = new Model390HF();
					model390HF.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(FS_MODEL_MATRIX_ENTRY_POINT)) {
			GWT.runAsync(ModelMatrix.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					ModelMatrix modelMatrix = new ModelMatrix();
					modelMatrix.onModuleLoad();
				}
			});
		} else if ( entryPoint.equalsIgnoreCase(RG_CUSTOMER_ENTRY_POINT)) {
			GWT.runAsync(FinanceModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					CustomerModule customerModule = new CustomerModule();
					customerModule.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(RG_CREDITOR_ENTRY_POINT)) {
			GWT.runAsync(FinanceModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					CreditorModule creditorModule = new CreditorModule();
					creditorModule.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(RG_SUPPLIER_ENTRY_POINT)) {
			GWT.runAsync(FinanceModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					SupplierModule supplierModule = new SupplierModule();
					supplierModule.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(FS_FINANCE_ENTRY_POINT)) {
			GWT.runAsync(FinanceModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					FinanceModule financeModule = new FinanceModule();
					financeModule.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(FS_INVOICE_REPORT_ENTRY_POINT)) {
			GWT.runAsync(InvoiceReport.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					InvoiceReport invoiceReport = new InvoiceReport();
					invoiceReport.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(FS_VAT_REPORT_ENTRY_POINT)) {
			GWT.runAsync(VatReport.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					VatReport vatReport = new VatReport();
					vatReport.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(FS_IRPF_REPORT_ENTRY_POINT)) {
			GWT.runAsync(IRPFReport.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					IRPFReport irpfReport = new IRPFReport(getCurrentDomainName(),getCurrentDomain(),getCurrentUser());
					irpfReport.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(FS_INVOICE_SERIES_BREAKDOWN_ENTRY_POINT)) {
			GWT.runAsync(InvoiceSeriesBreakdown.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					InvoiceSeriesBreakdown invoiceSeriesBreakdown = new InvoiceSeriesBreakdown();
					invoiceSeriesBreakdown.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(FS_FINANCE_UTILITIES_ENTRY_POINT)) {
			GWT.runAsync(FinanceUtilities.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					FinanceUtilities financeUtilities = new FinanceUtilities();
					financeUtilities.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(ACC_ACCOUNT_ENTRY_ENTRY_POINT)) {
			GWT.runAsync(AccountEntryModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					AccountEntryModule accountEntryModule = new AccountEntryModule();
					accountEntryModule.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(ACC_ACCOUNT_ENTRY_ENTRY_POINT_TEDI)) {
			GWT.runAsync(AccountEntryModuleTEDI.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					AccountEntryModuleTEDI accountEntryModuleTEDI = new AccountEntryModuleTEDI();
					accountEntryModuleTEDI.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(ACC_ACCOUNTING_UTILITIES_ENTRY_POINT)) {
			GWT.runAsync(AccountingUtilities.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					AccountingUtilities accountingUtilities = new AccountingUtilities();
					accountingUtilities.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(ACC_JOURNAL_REPORT_ENTRY_POINT)) {
			GWT.runAsync(AccountJournalReport.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					AccountJournalReport accountJournalReport = new AccountJournalReport();
					accountJournalReport.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(ACC_ACCOUNT_ENTRY_POINT)) {
			GWT.runAsync(AccountModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					AccountModule accountModule = new AccountModule();
					accountModule.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(ACC_LEDGER_REPORT_ENTRY_POINT)) {
			GWT.runAsync(AccountJournalReport.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					AccountLedgerReport accountLedgerReport = new AccountLedgerReport();
					accountLedgerReport.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(ACC_STATEMENT_REPORT_ENTRY_POINT)) {
			GWT.runAsync(AccountStatementReport.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					AccountStatementReport accountStatementReport = new AccountStatementReport();
					accountStatementReport.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(ACC_OPERATING_REPORT_ENTRY_POINT)) {
			GWT.runAsync(AccountOperatingReport.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					AccountOperatingReport accountOperatingStatementReport = new AccountOperatingReport();
					accountOperatingStatementReport.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(ACC_ANALYTICAL_REPORT_ENTRY_POINT)) {
			GWT.runAsync(AccountAnalyticalReport.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					AccountAnalyticalReport accountAnalyticalReport = new AccountAnalyticalReport();
					accountAnalyticalReport.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(ACC_TRIAL_BALANCE_REPORT_ENTRY_POINT)) {
			GWT.runAsync(AccountTrialBalanceReport.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					AccountTrialBalanceReport report = new AccountTrialBalanceReport();
					report.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(ACC_ACCOUNTING_BALANCE_REPORT_ENTRY_POINT)) {
			GWT.runAsync(AccountBalanceReport.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					AccountBalanceReport report = new AccountBalanceReport();
					report.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(ACC_ACCOUNTING_CONSOLIDATED_BALANCE_REPORT_ENTRY_POINT)) {
			GWT.runAsync(AccountConsolidatedBalanceReport.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					AccountConsolidatedBalanceReport report = new AccountConsolidatedBalanceReport();
					report.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(ACC_OPERATION_ENTRY_POINT)) {
			GWT.runAsync(OperationReport.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					OperationReport accountOperationReport = new OperationReport(getCurrentDomainName(),getCurrentDomain(),getCurrentUser());
					accountOperationReport.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(RAWDOC_ENTRY_POINT)) {
			GWT.runAsync(RawdocModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					RawdocModule rawdoc  = new RawdocModule();
					rawdoc.onModuleLoad();
				}
				
			});
		}
	}

	public static native String getToken()
	/*-{
		return $wnd.localStorage.getItem("aon_session_id");
	}-*/;
	
	public static native String getCurrentDomainName()
	/*-{
		var token = $wnd.localStorage.getItem("aon_session_id");
		return token ? $wnd.localStorage.getItem("aon_domain_name") : $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		var token = $wnd.localStorage.getItem("aon_session_id");
		return token ? $wnd.localStorage.getItem("aon_domain_id") :  $wnd.getCurrentDomain();
	}-*/;
	
	public static native String getRootPanel()
	/*-{
		return $wnd.localStorage.getItem("rootPanel");
	}-*/;
	
	public static String getCurrentUser() {
		return getToken() != null ? aonData.getUser().getLogin() : getCurrentUserJs();
	};
	
	public static native String getCurrentUserJs()
	/*-{
		var token = $wnd.localStorage.getItem("aon_session_id");
		return token ? "" : $wnd.getCurrentUser();
	}-*/;
	/**
	 * Fetches a parameter passed to the module's nocache script.
	 * 
	 * @param moduleName
	 *            the module's name.
	 * @param parameterName
	 *            the name of the parameter to fetch.
	 * @return the value of the parameter, or <code>null</code> if it was not
	 *         found.
	 */
	public static native String getParameter(String moduleName, String parameterName) /*-{
		var search = "/" + moduleName + ".nocache.js";
		var scripts = $doc.getElementsByTagName("script");
		for ( var i = 0; i < scripts.length; ++i) {
			if (scripts[i].src != null && scripts[i].src.indexOf(search) != -1) {
				var params = scripts[i].src.match(/\w+=\w+/g);
				for ( var j = 0; j < params.length; ++j) {
					var keyvalue = params[j].split("=");
					if (keyvalue.length == 2 && keyvalue[0] == parameterName) {
						return unescape(keyvalue[1]);
					}
				}
			}
		}
		return null;
	}-*/;

}
