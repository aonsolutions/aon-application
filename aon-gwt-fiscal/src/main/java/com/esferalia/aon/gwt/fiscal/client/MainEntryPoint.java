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
import com.esferalia.aon.gwt.fiscal.client.accounting.AmortizationType;
import com.esferalia.aon.gwt.fiscal.client.accounting.CostCenterModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.InvestAssetModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.period.AccountingPeriodModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.utilities.AccountingUtilities;
import com.esferalia.aon.gwt.fiscal.client.booking.BookingCustomerPanel;
import com.esferalia.aon.gwt.fiscal.client.booking.BookingPanel;
import com.esferalia.aon.gwt.fiscal.client.booking.CustomerBookingResumeModule;
import com.esferalia.aon.gwt.fiscal.client.config.FiscalConfig;
import com.esferalia.aon.gwt.fiscal.client.customer.CustomerActivityModule;
import com.esferalia.aon.gwt.fiscal.client.customer.CustomerInvoiceModule;
import com.esferalia.aon.gwt.fiscal.client.customer.CustomerSupportAgentModule;
import com.esferalia.aon.gwt.fiscal.client.customer.CustomerSyncDomainModule;
import com.esferalia.aon.gwt.fiscal.client.customer.SyncSigCustomerDomainModule;
import com.esferalia.aon.gwt.fiscal.client.finance.FBatchPaymentModule;
import com.esferalia.aon.gwt.fiscal.client.finance.FBatchPaymentModule.FBATCH_TYPE;
import com.esferalia.aon.gwt.fiscal.client.finance.FinanceModule;
import com.esferalia.aon.gwt.fiscal.client.finance.checkit.CheckItModule;
import com.esferalia.aon.gwt.fiscal.client.finance.nordigen.NordigenModule;
import com.esferalia.aon.gwt.fiscal.client.finance.paymethod.PayMethodModule;
import com.esferalia.aon.gwt.fiscal.client.finance.utilities.FinanceUtilitiesModule;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceReport;
import com.esferalia.aon.gwt.fiscal.client.invoice.vat.VatReport;
import com.esferalia.aon.gwt.fiscal.client.mailaccount.MailAccountEntryModule;
import com.esferalia.aon.gwt.fiscal.client.mailaccount.SignatureEntryModule;
import com.esferalia.aon.gwt.fiscal.client.mod140.Model140;
import com.esferalia.aon.gwt.fiscal.client.mod240.Model240;
import com.esferalia.aon.gwt.fiscal.client.product.ProductCatalogueModule;
import com.esferalia.aon.gwt.fiscal.client.product.ProductModule;
import com.esferalia.aon.gwt.fiscal.client.registry.CreditorModuleNew;
import com.esferalia.aon.gwt.fiscal.client.registry.CustomerFee;
import com.esferalia.aon.gwt.fiscal.client.registry.CustomerModuleNew;
import com.esferalia.aon.gwt.fiscal.client.registry.DomainBookingResumeModule;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryCompanyEntryModule;
import com.esferalia.aon.gwt.fiscal.client.registry.SupplierModuleNew;
import com.esferalia.aon.gwt.fiscal.client.sales.SalesModule;
import com.esferalia.aon.gwt.fiscal.client.sii.Sii;
import com.esferalia.aon.gwt.fiscal.client.target.TargetEnterpriseModule;
import com.esferalia.aon.gwt.fiscal.client.tariff.TariffModule;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.config.ConfigParams;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.Document;
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
	private static final String ELEMENT_TARGET = "elementTarget";
		
	//
	//    =============================================================== DOMINA STAT
	//
	private enum DomainStatEntryPoint {
		DomainInvoiceStat {
			void run() {
				com.esferalia.aon.gwt.fiscal.client.domainstat.DomainInvoiceStatModule.run();
			}
		},
		;
		abstract void run();
	}
	
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
	//    ================================================================== CONSOLE
	//
	private enum FinanceEntryPoint {
		InvoiceConsoleModule {
			void run() {
				com.esferalia.aon.gwt.fiscal.client.invoice.console.InvoiceConsoleModule.run();
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
		Model369 {
			@Override
			void run() {				
				com.esferalia.aon.gwt.fiscal.client.mod369.Model369.run();
			}
		},
		Model421 {
			@Override
			void run() {				
				com.esferalia.aon.gwt.fiscal.client.mod421.Model421.run();
			}
		}
		;
		abstract void run();
	}
	//
	//    ================================================================== ACCOUNTING
	//
	private enum AccountingEntryPoint {
		AccountingOperationReport {
			void run() {
				com.esferalia.aon.gwt.fiscal.client.report.OperationReportNew.run();
			}
		},
		AmortizationModule {
			void run() {
				com.esferalia.aon.gwt.fiscal.client.accounting.amortization.AmortizationModule.run();
			}
		},
		IRPFReport {
			void run() {
				com.esferalia.aon.gwt.fiscal.client.invoice.irpf.IRPFReport.run();
			}
		};
		abstract void run();
	}
	
	//
	//    ================================================================== RAWDOC
	//
	private enum RawdocEntryPoint {
		RawdocModule{
			@Override
			void run() {
				com.esferalia.aon.gwt.fiscal.client.rawdoc.RawdocModule.run();
			}
		},
		;
		abstract void run();
	}

	private static final String SII_ENTRY_POINT = "Sii";
	private static final String FS_MOD140_ENTRY_POINT = "Model140";
	private static final String FS_MOD240_ENTRY_POINT = "Model240";
	private static final String FS_CONFIG_POINT = "FiscalConfig";
	//	
	//    ================================================================== REGISTRY
	//
	private static final String RG_CUSTOMER_ENTRY_POINT = "Customer";
	private static final String RG_CUSTOMER_FEE_ENTRY_POINT = "CustomerFee";
	private static final String RG_SUPPLIER_ENTRY_POINT = "Supplier";
	private static final String RG_CREDITOR_ENTRY_POINT = "Creditor";
	private static final String RG_BOOKING_ENTRY_POINT = "BookingPanel";
	private static final String RG_BOOKING_CUSTOMER_ENTRY_POINT = "BookingCustomer";
	//	
	//    ================================================================== FINANCE
	//
	private static final String FS_PAY_METHOD_ENTRY_POINT = "PayMethod";
	private static final String FS_FINANCE_ENTRY_POINT = "Finance";
	private static final String FS_FINANCE_PAYROLL_ENTRY_POINT = "FinancePayroll";
	private static final String FS_INVOICE_REPORT_ENTRY_POINT = "InvoiceReport";
	private static final String FS_VAT_REPORT_ENTRY_POINT = "VATReport";
	private static final String FS_FINANCE_UTILITIES_ENTRY_POINT = "FinanceUtilities";
	private static final String FS_FBATCH_PAYMENT_PAYROLL_ENTRY_POINT = "FBatchPaymentPayroll";
	private static final String FS_FBATCH_PAYMENT_TREASURY_ENTRY_POINT = "FBatchPaymentTreasury";
	
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
	private static final String ACC_ACCOUNT_ENTRY_ENTRY_POINT = "AccountEntryModule";
	private static final String ACC_ACCOUNTING_UTILITIES_ENTRY_POINT = "AccountingUtilities";
	private static final String ACC_ACCOUNTING_BALANCE_REPORT_ENTRY_POINT = "AccountBalanceReport";
	private static final String ACC_ACCOUNTING_CONSOLIDATED_BALANCE_REPORT_ENTRY_POINT = "AccountConsolidatedBalanceReport";
	private static final String ACC_AMORTIZATION_TYPE_ENTRY_POINT = "AmortizationType";
	private static final String ACC_COST_CENTER_ENTRY_POINT = "CostCenterModule";
	private static final String INVEST_ASSET_ENTRY_POINT = "InvestAssetModule";

	//
	//    ================================================================== CHECKIT
	//
	private static final String CHECKIT_ENTRY_POINT = "CheckItModule";
	//
	//    ================================================================== NORDIGEN
	//
	private static final String NORDIGEN_ENTRY_POINT = "NordigenModule";
	//
	//    ================================================================== BOOKING RESUME
	//
	private static final String DOMAIN_BOOKING_RESUME_ENTRY_POINT = "DomainBookingResume";
	private static final String CUSTOMER_BOOKING_RESUME_ENTRY_POINT = "CustomerBookingResume";
	//
	//    ================================================================== CUSTOMER
	//
	private static final String CUSTOMER_SUPPORT_AGENT_ENTRY_POINT = "CustomerSupportAgent";
	//
	//    ================================================================== PRODRUCT
	//
	private static final String PRODUCT_MODULE_ENTRY_POINT = "ProductModule";
	//
	//    ================================================================== TARIFF
	//
	private static final String TARIFF_MODULE_ENTRY_POINT = "TariffModule";
	//  ================================================================== SALES
	//
	private static final String SALES_MODULE_ENTRY_POINT = "SalesModule";
	//  ================================================================== TARGET ENTERPRISE
	//
	private static final String TARGET_ENTERPRISE_MODULE_ENTRY_POINT = "TargetEnterpriseModule";
	//  ================================================================== TARGET ENTERPRISE
	//
	private static final String CUSTOMER_INVOICE_MODULE_ENTRY_POINT = "CustomerInvoiceModule";
	//  ============================================================= CUSTOMER LINKED ACTIVITY
	//
	private static final String CUSTOMER_LINKED_ACTIVITY_ENTRY_POINT = "CustomerLinkedActivity";
	//  ================================================================= CUSTOMER SYNC DOMAIN
	//
	private static final String CUSTOMER_SYNC_DOMAIN_ENTRY_POINT = "CustomerSyncDomainModule";
	//  ================================================================= CUSTOMER SYNC DOMAIN
	//
	private static final String SYNC_SIG_CUSTOMER_DOMAIN_MODULE_ENTRY_POINT = "SyncSigCustomerDomainModule";
	//
	//    ================================================================== PRODRUCT CATALOGUE
	//
	private static final String PRODUCT_CATALOGUE_MODULE_ENTRY_POINT = "ProductCatalogueModule";
	//
	//    ================================================================== REGISTRY ENTRY
	//
	private static final String REGISTRY_COMPANY_ENTRY_MODULE_ENTRY_POINT = "RegistryCompanyEntryModule";
	//
	//    ================================================================== MAIL ACCOUNT ENTRY
	//
	private static final String MAIL_ACCOUNT_MODULE_ENTRY_POINT = "MailAccountEntryModule";
	//
	//    ================================================================== SIGNATURE ENTRY
	//
	private static final String SIGNATURE_MODULE_ENTRY_POINT = "SignatureEntryModule";
	
	
	
	@Override
	public void onModuleLoad() {
		ensureGwtSelector();
		
		String entryPoint = getParameter(GWT.getModuleName(), ENTRY_POINT_PARAM);	
		String elementTarget = getParameter(GWT.getModuleName(), ELEMENT_TARGET);
		if(getToken() != null) {
			Occam occam = new Occam()
				.setDomainName(getCurrentDomainName())
				.setDomain(getCurrentDomain())
				.setUser(getCurrentUser());
			ConfigParams params = new ConfigParams().setToken(getToken());
			COMMON_SERVICE.getAonConfiguration(occam, params, new AsyncCallback<AonConfiguration>() {
				
				@Override public void onSuccess(AonConfiguration config) {
					selection(entryPoint,elementTarget,aonConfiguration);
				}
				
				@Override public void onFailure(Throwable arg0) {
					Window.alert(ERROR_MSG);
				}
			});
		} else {
			selection(entryPoint,elementTarget,null);
		}

		
	}
	
	private void selection(String entryPoint,String elementTarget,AonConfiguration aonConfiguration) {
		try {
			DomainStatEntryPoint dsEntryPoint = DomainStatEntryPoint.valueOf(entryPoint);
			dsEntryPoint.run();
		} catch (IllegalArgumentException e) {
			// De momento nada. Cuando todos los EntryPoint esten en el enumerado, gestionar error. 
		}
		
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
		
		try {
			FinanceEntryPoint financeEntryPoint = FinanceEntryPoint.valueOf(entryPoint);
			financeEntryPoint.run();
		} catch (IllegalArgumentException e) {
			// De momento nada. Cuando todos los EntryPoint esten en el enumerado, gestionar error. 
		}
		
		
		try {
			RawdocEntryPoint rawdocEntryPoint = RawdocEntryPoint.valueOf(entryPoint);
			rawdocEntryPoint.run();
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
		else if ( entryPoint.equalsIgnoreCase(FS_CONFIG_POINT)) {
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
					CustomerModuleNew customerModule = new CustomerModuleNew();
					customerModule.onModuleLoad();
//					CustomerModule customerModule = new CustomerModule();
//					customerModule.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(RG_CUSTOMER_FEE_ENTRY_POINT)) {
			GWT.runAsync(FinanceModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					CustomerFee customerFee = new CustomerFee();
					customerFee.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(RG_BOOKING_ENTRY_POINT)) {
			GWT.runAsync(FinanceModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					BookingPanel bookingPanel = new BookingPanel();
					bookingPanel.onModuleLoad();
				}
				
			});
		}  else if ( entryPoint.equalsIgnoreCase(RG_BOOKING_CUSTOMER_ENTRY_POINT)) {
			GWT.runAsync(FinanceModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					BookingCustomerPanel bcp = new BookingCustomerPanel();
					bcp.onModuleLoad();
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
					CreditorModuleNew creditorModule = new CreditorModuleNew();
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
					SupplierModuleNew supplierModule = new SupplierModuleNew();
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
		} else if ( entryPoint.equalsIgnoreCase(FS_FINANCE_PAYROLL_ENTRY_POINT)) {
			GWT.runAsync(FinanceModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					FinanceModule financeModule = new FinanceModule();
					financeModule.setIsPayroll(true);
					financeModule.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(FS_FBATCH_PAYMENT_PAYROLL_ENTRY_POINT)) {
			GWT.runAsync(FinanceModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					FBatchPaymentModule fBatchPaymentPayrollModule = new FBatchPaymentModule(FBATCH_TYPE.PAYROLL_PAYMENT);
					fBatchPaymentPayrollModule.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(FS_FBATCH_PAYMENT_TREASURY_ENTRY_POINT)) {
			GWT.runAsync(FinanceModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					FBatchPaymentModule fBatchPaymentPayrollModule = new FBatchPaymentModule(FBATCH_TYPE.PAYMENT);
					fBatchPaymentPayrollModule.onModuleLoad();
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
		} else if ( entryPoint.equalsIgnoreCase(FS_FINANCE_UTILITIES_ENTRY_POINT)) {
			GWT.runAsync(FinanceUtilitiesModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					FinanceUtilitiesModule financeUtilities = new FinanceUtilitiesModule();
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
		} else if ( entryPoint.equalsIgnoreCase(ACC_AMORTIZATION_TYPE_ENTRY_POINT)) {
			GWT.runAsync(AmortizationType.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					AmortizationType amortizationType = new AmortizationType();
					amortizationType.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(INVEST_ASSET_ENTRY_POINT)) {
			GWT.runAsync(InvestAssetModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					InvestAssetModule investAssetModule = new InvestAssetModule();
					investAssetModule.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(ACC_COST_CENTER_ENTRY_POINT)) {
			GWT.runAsync(CostCenterModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					CostCenterModule costCenterModule = new CostCenterModule();
					costCenterModule.onModuleLoad();
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
					CheckItModule checkItModule  = new CheckItModule();
					checkItModule.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(NORDIGEN_ENTRY_POINT)) {
			GWT.runAsync(NordigenModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					NordigenModule nordigenModule  = new NordigenModule();
					nordigenModule.onModuleLoad();
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
		}  else if ( entryPoint.equalsIgnoreCase(DOMAIN_BOOKING_RESUME_ENTRY_POINT)) {
			GWT.runAsync(DomainBookingResumeModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					DomainBookingResumeModule dbrm = new DomainBookingResumeModule();
					dbrm.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(CUSTOMER_BOOKING_RESUME_ENTRY_POINT)) {
			GWT.runAsync(CustomerBookingResumeModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					CustomerBookingResumeModule dbrm = new CustomerBookingResumeModule();
					dbrm.onModuleLoad();
				}
				
			});
		} else if( entryPoint.equalsIgnoreCase(CUSTOMER_SUPPORT_AGENT_ENTRY_POINT) ) {
			GWT.runAsync(CustomerSupportAgentModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					CustomerSupportAgentModule dbrm = new CustomerSupportAgentModule();
					dbrm.onModuleLoad();
				}
				
			});
		} else if( entryPoint.equalsIgnoreCase(PRODUCT_MODULE_ENTRY_POINT) ) {
			GWT.runAsync(ProductModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					ProductModule productModule = new ProductModule();
					productModule.onModuleLoad();
				}
				
			});
		} else if( entryPoint.equalsIgnoreCase(TARIFF_MODULE_ENTRY_POINT) ) {
			GWT.runAsync(ProductModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					TariffModule tariffModule = new TariffModule();
					tariffModule.onModuleLoad();
				}
				
			});
		}  else if( entryPoint.equalsIgnoreCase(SALES_MODULE_ENTRY_POINT) ) {
			GWT.runAsync(SalesModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					SalesModule salesModule = new SalesModule();
					salesModule.onModuleLoad();
				}
				
			});
		}  else if( entryPoint.equalsIgnoreCase(TARGET_ENTERPRISE_MODULE_ENTRY_POINT) ) {
			GWT.runAsync(TargetEnterpriseModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					TargetEnterpriseModule targetEnterpriseModule = new TargetEnterpriseModule();
					targetEnterpriseModule.onModuleLoad();
				}
				
			});
		}  else if( entryPoint.equalsIgnoreCase(CUSTOMER_INVOICE_MODULE_ENTRY_POINT) ) {
			GWT.runAsync(CustomerInvoiceModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					CustomerInvoiceModule customerInvoiceModule = new CustomerInvoiceModule();
					customerInvoiceModule.onModuleLoad();
				}
				
			});
		} else if( entryPoint.equalsIgnoreCase(CUSTOMER_LINKED_ACTIVITY_ENTRY_POINT) ) {
			GWT.runAsync(CustomerActivityModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					CustomerActivityModule customerActivityModule = new CustomerActivityModule();
					customerActivityModule.onModuleLoad();
				}
				
			});
		} else if( entryPoint.equalsIgnoreCase(CUSTOMER_SYNC_DOMAIN_ENTRY_POINT) ) {
			GWT.runAsync(CustomerSyncDomainModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					CustomerSyncDomainModule CustomerSyncDomainModule = new CustomerSyncDomainModule();
					CustomerSyncDomainModule.onModuleLoad();
				}
				
			});
		} else if( entryPoint.equalsIgnoreCase(SYNC_SIG_CUSTOMER_DOMAIN_MODULE_ENTRY_POINT) ) {
			GWT.runAsync(SyncSigCustomerDomainModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					SyncSigCustomerDomainModule syncSigCustomerDomainModule = new SyncSigCustomerDomainModule();
					syncSigCustomerDomainModule.onModuleLoad();
				}
				
			});
		} else if( entryPoint.equalsIgnoreCase(PRODUCT_CATALOGUE_MODULE_ENTRY_POINT) ) {
			GWT.runAsync(ProductCatalogueModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					ProductCatalogueModule productCatalogueModule = new ProductCatalogueModule();
					productCatalogueModule.onModuleLoad();
				}
				
			});
		} else if( entryPoint.equalsIgnoreCase(REGISTRY_COMPANY_ENTRY_MODULE_ENTRY_POINT) ) {
			GWT.runAsync(RegistryCompanyEntryModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					RegistryCompanyEntryModule registryEntryModule = new RegistryCompanyEntryModule();
					registryEntryModule.onModuleLoad();
				}
				
			});
		} else if( entryPoint.equalsIgnoreCase(MAIL_ACCOUNT_MODULE_ENTRY_POINT) ) {
			GWT.runAsync(MailAccountEntryModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					MailAccountEntryModule mailAccountEntryModule = new MailAccountEntryModule();
					mailAccountEntryModule.onModuleLoad();
				}
				
			});
		} else if( entryPoint.equalsIgnoreCase(SIGNATURE_MODULE_ENTRY_POINT) ) {
			GWT.runAsync(SignatureEntryModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert(ERROR_MSG);
				}

				@Override
				public void onSuccess() {
					SignatureEntryModule signatureEntryModule = new SignatureEntryModule();
					signatureEntryModule.onModuleLoad();
				}
				
			});
		} 
		
		
	}
	
	public static void ensureGwtSelector() {
		BodyElement body = Document.get().getBody();
		String className = body.getClassName();
		if (AonStringUtils.isBlank(className)
				|| (className.indexOf("gwt-Selector") == -1))
			body.addClassName("gwt-Selector");

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
	
	public static native int getCustomer()
	/*-{
		return $wnd.localStorage.getItem("customer");
	}-*/;
	
	public static native int removeCustomer()
	/*-{
		return $wnd.localStorage.removeItem("customer");
	}-*/;
	
	public static String getCurrentUser() {
		return getCurrentUserJs();
	}
	
	public static native String getCurrentUserJs()
	/*-{
		return $wnd.getCurrentUser();
	}-*/;
	
	public static native int getOfficeDomain()
	/*-{
		return $wnd.localStorage.getItem("officeDomain");
	}-*/;
	
	public static native int removeOfficeDomain()
	/*-{
		return $wnd.localStorage.removeItem("officeDomain");
	}-*/;
	
	public static native boolean isSig()
	/*-{
		return $wnd.localStorage.getItem("isSig");
	}-*/;
	
	public static native int getBookingDomainId()
	/*-{
		return $wnd.localStorage.getItem("booking_domain_id");
	}-*/;
	
	public static native String getBookingDomainName()
	/*-{
		return $wnd.localStorage.getItem("booking_domain_name");
	}-*/;
	
	public static native int removeBookingDomainId()
	/*-{
		return $wnd.localStorage.removeItem("booking_domain_id");
	}-*/;
	
	public static native String removeBookingDomainName()
	/*-{
		return $wnd.localStorage.removeItem("booking_domain_name");
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
