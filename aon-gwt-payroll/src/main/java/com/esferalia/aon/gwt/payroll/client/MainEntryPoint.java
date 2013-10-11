package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.ImageResource;

public class MainEntryPoint implements EntryPoint {

	interface GWTResources extends ClientBundle {
		@NotStrict
		@Source("gwt.css")
		CssResource css();
	
		@Source("warn.png")
		ImageResource warn();
	
		@Source("aon-menuBar.png")
		ImageResource menuBar();
	
		@Source("aon-tabBar.png")
		ImageResource tabBar();
	
		@Source("checkyes.png")
		ImageResource checkYes();
	
		@Source("button.png")
		ImageResource button();
	
		@Source("public.png")
		ImageResource publiC();
	
		@Source("private.png")
		ImageResource privatE();
	
		@Source("protected.png")
		ImageResource protecteD();
	
	}

	interface AonResources extends ClientBundle {
		@NotStrict
		@Source("aon.css")
		CssResource css();
	
		@Source("draft.png")
		ImageResource draft();
	
		@Source("salaries.png")
		ImageResource salaries();
	
		@Source("ine.png")
		ImageResource ine();
	
		@Source("workplace.png")
		ImageResource workplace();
	
		@Source("employee.png")
		ImageResource employee();
	
		@Source("agreement.png")
		ImageResource agreement();
	}

	private static final String MAIN_AGREEMENT_ENTRY_POINT = "MainAgreement";
	private static final String MAIN_CALCULATOR_ENTRY_POINT = "MainCalculator";
	private static final String EMPLOYEE_TREE_ENTRY_POINT = "EmployeeTree";
	private static final String ENTERPRISE_SITE_ENTRY_POINT = "EnterpriseSite";
	private static final String ENTRY_POINT_PARAM = "entryPoint";

	@Override
	public void onModuleLoad() {

		String entryPoint = getParameter(GWT.getModuleName(), ENTRY_POINT_PARAM);

		
		if ( entryPoint.equalsIgnoreCase(ENTERPRISE_SITE_ENTRY_POINT)) {
			EnterpriseSite enterpriseSite = new EnterpriseSite();
			enterpriseSite.onModuleLoad();
		}
		
		else if ( entryPoint.equalsIgnoreCase(EMPLOYEE_TREE_ENTRY_POINT)) {
			EmployeeTree employeeTree = new EmployeeTree();
			employeeTree.onModuleLoad();
		}
		else if ( entryPoint.equalsIgnoreCase(MAIN_CALCULATOR_ENTRY_POINT)) {
			MainCalculator mainCalculator = new MainCalculator();
			mainCalculator.onModuleLoad();
		}
		else if ( entryPoint.equalsIgnoreCase(MAIN_AGREEMENT_ENTRY_POINT)) {
			MainAgreement mainAgreement = new MainAgreement();
			mainAgreement.onModuleLoad();
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
