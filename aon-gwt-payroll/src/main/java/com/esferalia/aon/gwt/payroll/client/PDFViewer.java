package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;

public class PDFViewer extends Composite {
	

	// -------------------------------------------------- UiBinder --------------------------------------------------
	
	interface PDfViewerUiBinder extends UiBinder<Widget, PDFViewer> {}
	
	private static PDfViewerUiBinder uiBinder = GWT.create(PDfViewerUiBinder.class);
	
	@UiField
	Label titleLabel;
	
	@UiField
	FullViewer pdfViewer;
	
	@UiField
	Panel customToolBarPanel;


	public PDFViewer() {
		
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	
	public void setTitle(String title) {
		this.titleLabel.setText(title);
	}
	
	public String getFileName() {
		return "filenamae";
	};
	
	
	public void setDocument(String url, double scale) {
		pdfViewer.open(url);
	}
	
	public void addCustomToolBarButton(Button button) {
		button.addStyleName(AON.AON_FINDING_TOOLBAR_ITEM);
		customToolBarPanel.add(button);
	}

	public void addCustomToolBarWidget(Widget widget) {
		customToolBarPanel.add(widget);
	}
	
	// -----------------------------------------------------------------------------------------------------------------

	
}
