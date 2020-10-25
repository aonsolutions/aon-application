package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Agreement.Level;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.ContractJourneyDuration;
import com.esferalia.aon.gwt.payroll.shared.ContractType;
import com.esferalia.aon.gwt.payroll.shared.ContractType.ContractTypeRecord;
import com.esferalia.aon.gwt.payroll.shared.ContractType.ModelRecord;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.Iban;
import com.esferalia.aon.gwt.payroll.shared.JourneyDuration;
import com.esferalia.aon.gwt.payroll.shared.Municipalities;
import com.esferalia.aon.gwt.payroll.shared.Rbank;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
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


	public PDFViewer() {
		
		initWidget(uiBinder.createAndBindUi(this));
		
		initZoomList();
	}
	
	
	public void setTitle(String title) {
		this.titleLabel.setText(title);
	}
	
	
	
	public void setDocument(String url, double scale) {
		pdfViewer.setDocument(url, scale);
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
	
	String getFileName() {
		return "filenamae";
	};
		
	
	
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
