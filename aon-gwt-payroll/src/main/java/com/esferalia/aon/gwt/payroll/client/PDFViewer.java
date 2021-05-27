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

import net.aonsolutions.gwt.pdfjs.client.Viewer;

public class PDFViewer extends Composite {
	

	// -------------------------------------------------- UiBinder --------------------------------------------------
	
	interface PDfViewerUiBinder extends UiBinder<Widget, PDFViewer> {}
	
	private static PDfViewerUiBinder uiBinder = GWT.create(PDfViewerUiBinder.class);
	
	@UiField
	Label titleLabel;
	
	@UiField
	Viewer pdfViewer;
	
	@UiField
	ListBox zoomListBox;
	
	@UiField
	Button downloadButton;
	
	@UiField
	Panel customToolBarPanel;


	public PDFViewer() {
		
		initWidget(uiBinder.createAndBindUi(this));
		
		initZoomList();
	}
	
	
	public void setTitle(String title) {
		this.titleLabel.setText(title);
	}
	
	public String getFileName() {
		return "filenamae";
	};
	
	
	public void setDocument(String url, double scale) {
		pdfViewer.setDocument(url, scale);
	}
	
	public void addCustomToolBarButton(Button button) {
		button.addStyleName(AON.AON_FINDING_TOOLBAR_ITEM);
		customToolBarPanel.add(button);
	}

	public void addCustomToolBarWidget(Widget widget) {
		customToolBarPanel.add(widget);
	}
	
	// -------------------------------------------------- UiHandlers --------------------------------------------------
	

	@UiHandler("zoomListBox")
	void onZoomListBoxChange(ChangeEvent event) {
		int index =zoomListBox.getSelectedIndex();
		String text = zoomListBox.getItemText(index);
		int zoom = (int) (Constants.PERCENT_FORMAT.parse(text));
		pdfViewer.scale(zoom / 100.00);
	}	
	
	@UiHandler("downloadButton")
	void onDownloadClick(ClickEvent event) {
		pdfViewer.download(getFileName());
	}
	
	// -----------------------------------------------------------------------------------------------------------------
	
		
	
	
	private void initZoomList() {

		for (int zoom = Constants.MIN_ZOOM; zoom < Constants.DEFAULT_ZOOM; zoom += Constants.ZOOM_STEP)
			zoomListBox.addItem(Constants.PERCENT_FORMAT.format((double) zoom / 100));
		int selectedIndex = zoomListBox.getItemCount();
		for (int zoom = Constants.DEFAULT_ZOOM; zoom < Constants.MAX_ZOOM; zoom += Constants.ZOOM_STEP)
			zoomListBox.addItem(Constants.PERCENT_FORMAT.format((double) zoom / 100));
		zoomListBox.addItem(Constants.PERCENT_FORMAT.format((double) Constants.MAX_ZOOM / 100));
		zoomListBox.setSelectedIndex(selectedIndex);
		
	}

	
}
