package com.esferalia.aon.gwt.fiscal.client;

import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.SalaryEntryModule;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceReport;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceSeriesBreakdown;
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
import com.esferalia.aon.gwt.fiscal.client.mod390.Model390;
import com.esferalia.aon.gwt.fiscal.client.stats.StatControlPanel;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;

public class MainEntryPoint implements EntryPoint {

	
	private static final String ENTRY_POINT_PARAM = "entryPoint";
	//
	//    ================================================================== FISCAL
	//
	private static final String FS_FISCAL_PANEL = "FiscalPanel";
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
	private static final String FS_MOD390_ENTRY_POINT = "Model390";
	private static final String FS_MOD200_ENTRY_POINT = "Model200";
	
	//	
	//    ================================================================== FINANCE
	//
	private static final String FS_INVOICE_REPORT_ENTRY_POINT = "InvoiceReport";
	private static final String FS_INVOICE_SERIES_BREAKDOWN_ENTRY_POINT = "InvoiceSeriesBreakdown";
	
	//
	//    ================================================================== ACCOUNTING
	//
	private static final String ACC_ACCOUNT_ENTRY_ENTRY_POINT = "AccountEntryModule";
	private static final String ACC_SALARY_ENTRY_ENTRY_POINT = "SalaryEntryModule";

	//	
	//    ================================================================== STATS
	//
	private static final String ST_STATS_ENTRY_POINT = "InvoiceStats";
	
	@Override
	public void onModuleLoad() {
		String entryPoint = getParameter(GWT.getModuleName(), ENTRY_POINT_PARAM);
		if ( entryPoint.equalsIgnoreCase(FS_FISCAL_PANEL)) {
			GWT.runAsync(FiscalTree.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					FiscalTree panel = new FiscalTree();
					panel.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(FS_MOD140_ENTRY_POINT)) {
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
		} else if ( entryPoint.equalsIgnoreCase(ACC_SALARY_ENTRY_ENTRY_POINT)) {
			GWT.runAsync(SalaryEntryModule.class, new RunAsyncCallback() {

				@Override
				public void onFailure(Throwable reason) {
					Window.alert("Error al cargar");
				}

				@Override
				public void onSuccess() {
					SalaryEntryModule salaryEntryModule = new SalaryEntryModule();
					salaryEntryModule.onModuleLoad();
				}
				
			});
		} else if ( entryPoint.equalsIgnoreCase(ST_STATS_ENTRY_POINT)) {
//			GWT.runAsync(InvoiceStats.class, new RunAsyncCallback() {
//
//				@Override
//				public void onFailure(Throwable reason) {
//					Window.alert("Error al cargar");
//				}
//
//				@Override
//				public void onSuccess() {
					new StatControlPanel().onModuleLoad();
//				}
//				
//			});
		}

	}

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
	public static native String getParameter(String moduleName,
			String parameterName) /*-{
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
