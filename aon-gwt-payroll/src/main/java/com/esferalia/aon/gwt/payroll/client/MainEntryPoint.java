package com.esferalia.aon.gwt.payroll.client;

import java.util.Arrays;

import com.esferalia.aon.gwt.common.shared.Constants;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.vaadin.polymer.Polymer;
import com.vaadin.polymer.iron.IronIconsElement;
import com.vaadin.polymer.iron.IronLabelElement;
import com.vaadin.polymer.iron.IronListElement;
import com.vaadin.polymer.paper.PaperDialogElement;
import com.vaadin.polymer.paper.PaperDropdownMenuElement;
import com.vaadin.polymer.paper.PaperIconButtonElement;
import com.vaadin.polymer.paper.PaperInputElement;
import com.vaadin.polymer.paper.PaperItemElement;
import com.vaadin.polymer.paper.PaperMenuElement;
import com.vaadin.polymer.paper.PaperSliderElement;
import com.vaadin.polymer.paper.PaperTextareaElement;
import com.vaadin.polymer.paper.PaperToggleButtonElement;
import com.vaadin.polymer.vaadin.VaadinComboBoxElement;
import net.aonsolutions.polymer.aon.AonComboBoxElement;

public class MainEntryPoint implements EntryPoint {

	interface CodeMirrorResources extends ClientBundle {
		@NotStrict
		@Source("codemirror.css")
		CssResource css();
	}

	@Override
	public void onModuleLoad() {
		Polymer.importHref(Arrays.asList(
				IronIconsElement.SRC,
				PaperInputElement.SRC,
				PaperTextareaElement.SRC,
				PaperDialogElement.SRC,
				VaadinComboBoxElement.SRC,
				AonComboBoxElement.SRC,
				PaperIconButtonElement.SRC,
				IronListElement.SRC,
				PaperToggleButtonElement.SRC,
				PaperSliderElement.SRC,
				IronLabelElement.SRC,
				PaperDropdownMenuElement.SRC,
				PaperMenuElement.SRC,
				PaperItemElement.SRC
		));
		
		Polymer.whenReady(o -> {
			ensureGwtSelector();
			String entryPoint = getParameter(GWT.getModuleName(),
					Constants.ENTRY_POINT_PARAM);

			if (entryPoint.equalsIgnoreCase(Constants.ENTERPRISE_SITE_ENTRY_POINT)) {
				EnterpriseSite enterpriseSite = new EnterpriseSite();
				enterpriseSite.onModuleLoad();
			}

			else if (entryPoint
					.equalsIgnoreCase(Constants.EMPLOYEE_TREE_ENTRY_POINT)) {
				EmployeeTree employeeTree = new EmployeeTree();
				employeeTree.onModuleLoad();
			} else if (entryPoint
					.equalsIgnoreCase(Constants.MAIN_SYSTEM_ENTRY_POINT)) {
				MainSystem mainSystem = new MainSystem();
				mainSystem.onModuleLoad();
			} else if (entryPoint
					.equalsIgnoreCase(Constants.MAIN_CALCULATOR_ENTRY_POINT)) {
				MainCalculator mainCalculator = new MainCalculator();
				mainCalculator.onModuleLoad();
			} else if (entryPoint
					.equalsIgnoreCase(Constants.MAIN_AGREEMENT_ENTRY_POINT)) {
				MainAgreement mainAgreement = new MainAgreement();
				mainAgreement.onModuleLoad();
			}else if (entryPoint
					.equalsIgnoreCase(Constants.MAIN_TRASH_ENTRY_POINT)) {
				MainTrash mainTrash = new MainTrash();
				mainTrash.onModuleLoad();
			}else if (entryPoint
					.equalsIgnoreCase(Constants.MAIN_CRETA_ENTRY_POINT)) {
				MainCreta mainCreta = new MainCreta();
				mainCreta.onModuleLoad();
			} else if (entryPoint
					.equalsIgnoreCase(Constants.ACTIVITY_SUMMARY_ENTRY_POINT)) {
				ActivitySummary activitySummary = new ActivitySummary();
				activitySummary.onModuleLoad();
			}

			return null;
		});
				

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
		for (var i = 0; i < scripts.length; ++i) {
			if (scripts[i].src != null && scripts[i].src.indexOf(search) != -1) {
				var params = scripts[i].src.match(/\w+=\w+/g);
				for (var j = 0; j < params.length; ++j) {
					var keyvalue = params[j].split("=");
					if (keyvalue.length == 2 && keyvalue[0] == parameterName) {
						return unescape(keyvalue[1]);
					}
				}
			}
		}
		return null;
	}-*/;

	public static void ensureGwtSelector() {
		BodyElement body = Document.get().getBody();
		String className = body.getClassName();
		if (StringUtils.isBlank(className)
				|| (className.indexOf("gwt-Selector") == -1))
			body.addClassName("gwt-Selector");

	}

}
