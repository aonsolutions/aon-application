package com.esferalia.aon.gwt.stat.client.panel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;

/**
 * Build a Form Panel in order to allow to download the table content in an excel format.<br>
 * Dont forget to add an iframe in your original html file :<br>
 * < iframe src="javascript:''" id="gwt-table-to-excel-target" class="invisible">< /iframe><br>
 * and to declare the sevlet in your web.xml :<br>
 * 
 * @see com.googlecode.gwtTableToExcel.server.TableToExcelServlet
 * @author Francois Wauquier 'wokier'
 */
public class TableToExcelClient {

	private final FormPanel formPanel = new FormPanel("_blank");

	protected TableToExcelClient(final com.google.gwt.dom.client.Element tableElement, String fileName) {
		
		formPanel.setAction(GWT.getModuleBaseURL() + "_GWTexcel");
		// formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanel.setMethod(FormPanel.METHOD_POST);
		formPanel.addStyleName("gwt-table-to-excel-form");
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.addStyleName("gwt-table-to-excel-panel");
		formPanel.setWidget(flowPanel);
		final Hidden contentHidden = new Hidden("html");
		flowPanel.add(contentHidden);
		final Hidden fileNameHidden = new Hidden("fileName", fileName);
		flowPanel.add(fileNameHidden);
		contentHidden.setValue(tableElement.getString());
	}

	/**
	 * Give the created form widget
	 * 
	 * @return
	 */
	public FormPanel getExportFormWidget() {
		return formPanel;
	}

}