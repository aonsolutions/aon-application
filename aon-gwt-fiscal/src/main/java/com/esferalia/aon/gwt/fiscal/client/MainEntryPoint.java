package com.esferalia.aon.gwt.fiscal.client;

import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountAnalyticalReport;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountBalanceReport;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountConsolidatedBalanceReport;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountJournalReport;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountLedgerReport;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountOperatingReport;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountStatementReport;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountTrialBalanceReport;
import com.esferalia.aon.gwt.fiscal.client.accounting.period.AccountingPeriodModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.utilities.AccountingUtilities;
import com.esferalia.aon.gwt.fiscal.client.config.FiscalConfig;
import com.esferalia.aon.gwt.fiscal.client.finance.FinanceModule;
import com.esferalia.aon.gwt.fiscal.client.finance.checkit.CheckItModule;
import com.esferalia.aon.gwt.fiscal.client.finance.paymethod.PayMethodModule;
import com.esferalia.aon.gwt.fiscal.client.finance.utilities.FinanceUtilities;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceReport;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceSeriesBreakdown;
import com.esferalia.aon.gwt.fiscal.client.invoice.OperationReport;
import com.esferalia.aon.gwt.fiscal.client.invoice.VatReport;
import com.esferalia.aon.gwt.fiscal.client.matrix.ModelMatrix;
import com.esferalia.aon.gwt.fiscal.client.mod140.Model140;
import com.esferalia.aon.gwt.fiscal.client.mod240.Model240;
import com.esferalia.aon.gwt.fiscal.client.rawdoc.RawdocModule;
import com.esferalia.aon.gwt.fiscal.client.registry.CreditorModule;
import com.esferalia.aon.gwt.fiscal.client.registry.CustomerModule;
import com.esferalia.aon.gwt.fiscal.client.registry.SupplierModule;
import com.esferalia.aon.gwt.fiscal.client.sii.Sii;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.config.ConfigParams;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MainEntryPoint implements EntryPoint {
	
	private static final String ERROR_MSG = "Error al cargar";
	private static AonConfiguration aonConfiguration;
	
	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw); 
	}
	
	
	private static final String ENTRY_POINT_PARAM = "entryPoint";
	//
	//    ================================================================== CONSOLE
	//
	private enum ConsoleEntryPoint {
		ConsoleModule {
			void run() {
				com.esferalia.aon.gwt.fiscal.client.console.ConsoleModule.run();
			}
		},
		;
		abstract void run();
	}
	
	//
	//    ================================================================== FISCAL
	//
	private enum FiscalEntryPoint {
		Model111 {
			void run() {
				com.esferalia.aon.gwt.fiscal.client.mod111.Model111.run();
			}
		},
		Model115 {
			void run() {
				com.esferalia.aon.gwt.fiscal.client.mod115.Model115.run();
			}
		},
		Model123 {
			void run() {
				com.esferalia.aon.gwt.fiscal.client.mod123.Model123.run();
			}
		},
		Model130 {
			void run() {
				com.esferalia.aon.gwt.fiscal.client.mod130.Model130.run();
			}
		},
		Model131 {
			@Override
			void run() {
				com.esferalia.aon.gwt.fiscal.client.mod131.Model131.run();
			}
		},
		Model180 {
			@Override
			void run() {
				com.esferalia.aon.gwt.fiscal.client.mod180.Model180.run();
			}
		},
		Model184 {
			@Override
			void run() {
				com.esferalia.aon.gwt.fiscal.client.mod184.Model184.run();
			}
		},
		Model190 {
			@Override
			void run() {
				com.esferalia.aon.gwt.fiscal.client.mod190.Model190.run();
			}
		},
		Model193 {
			@Override
			void run() {
				com.esferalia.aon.gwt.fiscal.client.mod193.Model193.run();
			}
		},
		Model202 {
			@Override
			void run() {
				com.esferalia.aon.gwt.fiscal.client.mod202.Model202.run();
			}
		},
		Model303 {
			@Override
			void run() {
				com.esferalia.aon.gwt.fiscal.client.mod303.Model303.run();
			}
		},
		Model347 {
			@Override
			void run() {
				com.esferalia.aon.gwt.fiscal.client.mod347.Model347.run();
			}
		},
		Model349 {
			@Override
			void run() {
				com.esferalia.aon.gwt.fiscal.client.mod349.Model349.run();
			}
		},
		Model390 {
			@Override
			void run() {
				com.esferalia.aon.gwt.fiscal.client.mod390.Model390.run();
			}
		},
		Model390HF {
			@Override
			void run() {
				com.esferalia.aon.gwt.fiscal.client.mod390hf.Model390HF.run();
			}
		},
		InvoiceModelReport {
			@Override
			void run() {
				com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceModelReport.run(); 
			}
		},
		;
		abstract void run();
	}
	//
	//    ================================================================== ACCOUNTING
	//
	private enum AccountingEntryPoint {
		AccountingOperationReport {
			void run() {
				com.esferalia.aon.gwt.fiscal.client.report.OperationReport.run();
			}
		},
		IRPFReport {
			void run() {
				com.esferalia.aon.gwt.fiscal.client.invoice.irpf.IRPFReport.run();
			}
		};
		abstract void run();
	}

	private static final String SII_ENTRY_POINT = "Sii";
	private static final String FS_MOD140_ENTRY_POINT = "Model140";
	private static final String FS_MOD240_ENTRY_POINT = "Model240";
	private static final String FS_MODEL_MATRIX_ENTRY_POINT = "ModelMatrix";
	private static final String FS_CONFIG_POINT = "FiscalConfig";
	//	
	//    ================================================================== REGISTRY
	//
	private static final String RG_CUSTOMER_ENTRY_POINT = "Customer";
	private static final String RG_SUPPLIER_ENTRY_POINT = "Supplier";
	private static final String RG_CREDITOR_ENTRY_POINT = "Creditor";
	//	
	//    ================================================================== FINANCE
	//
	private static final String FS_PAY_METHOD_ENTRY_POINT = "PayMethod";
	private static final String FS_FINANCE_ENTRY_POINT = "Finance";
	private static final String FS_INVOICE_REPORT_ENTRY_POINT = "InvoiceReport";
	private static final String FS_INVOICE_SERIES_BREAKDOWN_ENTRY_POINT = "InvoiceSeriesBreakdown";
	private static final String FS_VAT_REPORT_ENTRY_POINT = "VATReport";
	private static final String FS_FINANCE_UTILITIES_ENTRY_POINT = "FinanceUtilities";
	
	//
	//    ================================================================== ACCOUNTING
	//
	private static final String ACC_ACCOUNT_ENTRY_POINT = "AccountModule";
	private static final String ACC_ACCOUNTING_PERIOD_ENTRY_POINT = "AccountingPeriodModule";
	private static final String ACC_JOURNAL_REPORT_ENTRY_POINT = "JournalReportModule";
	private static final String ACC_LEDGER_REPORT_ENTRY_POINT = "LedgerReportModule";
	private static final String ACC_STATEMENT_REPORT_ENTRY_POINT = "StatementReportModule";
	private static final String ACC_OPERATING_REPORT_ENTRY_POINT = "AccountOperatingReport";
	private static final String ACC_ANALYTICAL_REPORT_ENTRY_POINT = "AccountAnalyticalReport";
	private static final String ACC_TRIAL_BALANCE_REPORT_ENTRY_POINT = "AccountTrialBalanceReport";
	private static final String ACC_OPERATION_ENTRY_POINT = "OperationReport";
	private static final String ACC_ACCOUNT_ENTRY_ENTRY_POINT = "AccountEntryModule";
	private static final String ACC_ACCOUNTING_UTILITIES_ENTRY_POINT = "AccountingUtilities";
	private static final String ACC_ACCOUNTING_BALANCE_REPORT_ENTRY_POINT = "AccountBalanceReport";
	private static final String ACC_ACCOUNTING_CONSOLIDATED_BALANCE_REPORT_ENTRY_POINT = "AccountConsolidatedBalanceReport";
	//
	//    ================================================================== RAWDOC
	//
	private static final String RAWDOC_ENTRY_POINT = "RawdocModule";
	//
	//    ================================================================== CHECKIT
	//
	private static final String CHECKIT_ENTRY_POINT = "CheckItModule";

	@Override
	public void onModuleLoad() {
		String entryPoint = getParameter(GWT.getModuleName(), ENTRY_POINT_PARAM);	
		if(getToken() != null) {
			Occam occam = new Occam()
				.setDomainName(getCurrentDomainName())
				.setDomain(getCurrentDomain())
				.setUser(getCurrentUser());
			ConfigParams params = new ConfigParams().setToken(getToken());
			COMMON_SERVICE.getAonConfiguration(occam, params, new AsyncCallback<AonConfiguration>() {
				
				@Override public void onSuccess(AonConfiguration config) {
					selection(entryPoint,aonConfiguration);
				}
				
				@Override public void onFailure(Throwable arg0) {
					Window.alert(ERROR_MSG);
				}
			});
		} else {
			selection(entryPoint,null);
		}

		
	}
	
	private void selection(String entryPoint,AonConfiguration aonConfiguration) {
		try {
			ConsoleEntryPoint consoleEntryPoint = ConsoleEntryPoint.valueOf(entryPoint);
			consoleEntryPoint.run();
		} catch (IllegalArgumentException e) {
			// De momento nada. Cuando todos los EntryPoint esten en el enumerado, gestionar error. 
		}
		
		try {
			FiscalEntryPoint fiscalEntryPoint = FiscalEntryPoint.valueOf(entryPoint);
			fiscalEntryPoint.run();
		} catch (IllegalArgumentException e) {
			// De momento nada. Cuando todos los EntryPoint esten en el enumerado, gestionar error. 
		}
		try {
			AccountingEntryPoint accountingEntryPoint = AccountingEntryPoint.valueOf(entryPoint);
			accountingEntryPoint.run();
		} catch (IllegalArgumentException e) {
			// De momento nada. Cuando todos los EntryPoint esten en el enumerado, gestionar error. 
		}
		if (FS_MOD140_ENTRY_POINT.equalsIgnoreCase(entryPoint)) {
			GWT.runAsync(Model140.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					new Model140().onModuleLoad();
				}
				
			});
		} else if(FS_MOD240_ENTRY_POINT.equalsIgnoreCase(entryPoint)) {
			GWT.runAsync(Model240.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					new Model240().onModuleLoad();
				}
				
			});
		} 
		else if ( entryPoint.equalsIgnoreCase(FS_MODEL_MATRIX_ENTRY_POINT)) {
			GWT.runAsync(ModelMatrix.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					ModelMatrix modelMatrix = new ModelMatrix();
					modelMatrix.onModuleLoad();
				}
			});
		} else if ( entryPoint.equalsIgnoreCase(FS_CONFIG_POINT)) {
			GWT.runAsync(FiscalConfig.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					FiscalConfig fiscalConfig = new FiscalConfig();
					fiscalConfig.onModuleLoad();
				}
			});
		} else if ( entryPoint.equalsIgnoreCase(RG_CUSTOMER_ENTRY_POINT)) {
			GWT.runAsync(FinanceModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
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
					Window.alert(ERROR_MSG);
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
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					SupplierModule supplierModule = new SupplierModule();
					supplierModule.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(FS_PAY_METHOD_ENTRY_POINT)) {
			GWT.runAsync(PayMethodModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					PayMethodModule payMethodModule = new PayMethodModule();
					payMethodModule.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(FS_FINANCE_ENTRY_POINT)) {
			GWT.runAsync(FinanceModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
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
					Window.alert(ERROR_MSG);
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
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					VatReport vatReport = new VatReport();
					vatReport.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(FS_INVOICE_SERIES_BREAKDOWN_ENTRY_POINT)) {
			GWT.runAsync(InvoiceSeriesBreakdown.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
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
					Window.alert(ERROR_MSG);
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
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					AccountEntryModule accountEntryModule = new AccountEntryModule();
					accountEntryModule.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(ACC_ACCOUNTING_UTILITIES_ENTRY_POINT)) {
			GWT.runAsync(AccountingUtilities.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
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
					Window.alert(ERROR_MSG);
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
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					AccountModule accountModule = new AccountModule();
					accountModule.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(ACC_ACCOUNTING_PERIOD_ENTRY_POINT)) {
			GWT.runAsync(AccountingPeriodModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					AccountingPeriodModule accountingPeriodModule = new AccountingPeriodModule();
					accountingPeriodModule.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(ACC_LEDGER_REPORT_ENTRY_POINT)) {
			GWT.runAsync(AccountJournalReport.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
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
					Window.alert(ERROR_MSG);
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
					Window.alert(ERROR_MSG);
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
					Window.alert(ERROR_MSG);
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
					Window.alert(ERROR_MSG);
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
					Window.alert(ERROR_MSG);
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
					Window.alert(ERROR_MSG);
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
					Window.alert(ERROR_MSG);
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
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					RawdocModule rawdoc  = new RawdocModule();
					rawdoc.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(CHECKIT_ENTRY_POINT)) {
			GWT.runAsync(CheckItModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					CheckItModule rawdoc  = new CheckItModule();
					rawdoc.onModuleLoad();
				}
				
			});
		} else if (SII_ENTRY_POINT.equalsIgnoreCase(entryPoint)) {
			GWT.runAsync(Sii.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					new Sii().onModuleLoad();
				}
				
			});
		}
	}
	protected Occam getOccam() {
		return new Occam()
			.setDomainName(getCurrentDomainName())
			.setDomain(getCurrentDomain())
			.setUser(getCurrentUser());
	}
	
	public static native String getToken()
	/*-{
		return $wnd.localStorage.getItem("aon_session_id");
	}-*/;
	
	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;
	
	public static native String getRootPanel()
	/*-{
		return $wnd.localStorage.getItem("rootPanel");
	}-*/;
	
	public static String getCurrentUser() {
		return getCurrentUserJs();
	}
	
	public static native String getCurrentUserJs()
	/*-{
		return $wnd.getCurrentUser();
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
