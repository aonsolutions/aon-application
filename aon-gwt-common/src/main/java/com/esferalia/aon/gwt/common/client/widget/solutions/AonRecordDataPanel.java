package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.registry.RecordData;
import com.esferalia.aon.occam.api.model.registry.RecordDataType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonRecordDataPanel extends HTMLPanel {
	
	// Callback
	
	public static interface AonRecordDataPanelCallback {
		void onAccept(RecordData recordData);
		void onCancel();
	}

	// CommonService
	
	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	// Variables
	
	private final static String EMPTY_STRING = "";
	
	private String domainName;
	private Integer domainId;
	private String user;
	
	private AonRecordDataPanelCallback callback;
	private RecordData recordData;
	
	// Wrokplace Info
	
	private HTMLPanel messagePanel = new HTMLPanel(EMPTY_STRING);
	
	private AonCustomDateBox date = new AonCustomDateBox("F. Creaci\u00f3n");
	private AonCustomListBox type = new AonCustomListBox("Tipo");
	private AonCustomTextBox description = new AonCustomTextBox("Descripci\u00f3n");
	
	private AonCustomDateBox registryDate = new AonCustomDateBox("F. Registro");
	private AonCustomTextBox notary = new AonCustomTextBox("Notario");
	
	private AonCustomTextBox protocol = new AonCustomTextBox("N. Protocolo");
	private AonCustomTextBox tomo = new AonCustomTextBox("Tomo");
	private AonCustomTextBox section = new AonCustomTextBox("Secci\u00f3n");
	private AonCustomTextBox page = new AonCustomTextBox("Folio");
	
	private AonCustomTextBox sheet = new AonCustomTextBox("Hoja");
	private AonCustomTextBox inscription = new AonCustomTextBox("Inscripci\u00f3n");
	
	private AonCustomTextBox file = new AonCustomTextBox("Adjunto");
	
	// Constructor
	
	public AonRecordDataPanel(String domainName, Integer domain, String user, Integer registry, AonRecordDataPanelCallback callback) {
		super(EMPTY_STRING);
		
		this.recordData = new RecordData()
				.setDomain(domain)
				.setRegistry(registry)
				;
		
		aonRDirStaffPanel(domainName, domain, user, callback);
	}
	
	public AonRecordDataPanel(String domainName, Integer domain, String user, RecordData recordData, AonRecordDataPanelCallback callback) {
		super(EMPTY_STRING);
		
		this.recordData = recordData;
		
		aonRDirStaffPanel(domainName, domain, user, callback);
	}
	
	private void aonRDirStaffPanel(String domainName, Integer domain, String user, AonRecordDataPanelCallback callback) {
		initializeCommonService();
		
		this.domainName = domainName;
		this.domainId = domain;
		this.user = user;
		
		this.callback = callback;
		
		show();
	}
	
	public void show() {
		// Message Panel
		setStyleName(AON.CSS.aonFlexColumn2());
		getElement().getStyle().setProperty("padding", "1rem 0");
		add(messagePanel);
		
		HTMLPanel container = new HTMLPanel(EMPTY_STRING);
		container.setStyleName(AON.CSS.aonFlexColumn2());
		container.getElement().getStyle().setProperty("padding", "0 1rem");
		container.getElement().getStyle().setProperty("min-width", "25rem");
		
		// Row 1
		HTMLPanel row = new HTMLPanel(EMPTY_STRING);
		row.setStyleName(AON.CSS.aonItemFlex());
		
		date.setValue(new Date());
		
		type.clearItems();
		for(int i=0; i < RecordDataType.values().length; i++)
			type.addItem(RecordDataType.values()[i].getDescription(), RecordDataType.values()[i].name());
		
		row.add(date);
		row.add(type);
		row.add(description);
		container.add(row);
		
		// Third Row
		HTMLPanel row2 = new HTMLPanel(EMPTY_STRING);
		row2.setStyleName(AON.CSS.aonItemFlex());
		
		row2.add(registryDate);
		row2.add(notary);
		container.add(row2);
		
		// Row 2
		HTMLPanel row3 = new HTMLPanel(EMPTY_STRING);
		row3.setStyleName(AON.CSS.aonItemFlex());
		
		row3.add(protocol);
		row3.add(tomo);
		row3.add(section);
		row3.add(page);
		container.add(row3);
		
		// Third Row
		HTMLPanel row4 = new HTMLPanel(EMPTY_STRING);
		row4.setStyleName(AON.CSS.aonItemFlex());
		
		row4.add(sheet);
		row4.add(inscription);
		container.add(row4);
		
		HTMLPanel row5 = new HTMLPanel(EMPTY_STRING);
		row5.setStyleName(AON.CSS.aonItemFlex());
		
		file.setEnable(false);
		
		row5.add(file);
		container.add(row5);
		
		// Fill info
		if(recordData.getId() != null) {
			date.setValue(recordData.getCreationDate());
			type.setValue(recordData.getType().name());
			description.setValue(recordData.getDescription());
			
			registryDate.setValue(recordData.getRecordDate());
			notary.setValue(recordData.getNotary());
			
			protocol.setValue(recordData.getNumber());
			tomo.setValue(recordData.getVolume());
			section.setValue(recordData.getSection());
			page.setValue(recordData.getPage());
			
			sheet.setValue(recordData.getPage());
			inscription.setValue(recordData.getRegistration());
			
			if(null != recordData.getAttach())
				file.setValue(recordData.getFullAttach().getDescription());
		}
		
		// Buttons
		container.add(createButtonsPanel());
		add(container);	
	}
	
	private Widget createButtonsPanel() {
		HTMLPanel buttonsPanel = new HTMLPanel(EMPTY_STRING);
		buttonsPanel.setStyleName(AON.CSS.aonTextCenter());
		buttonsPanel.getElement().getStyle().setProperty("margin-top", "1rem");
    	
    	Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addClickHandler(e -> {
    		okButton.setEnabled(false);
			
    		recordData
				.setCreationDate(date.getValue())
				.setType(RecordDataType.safeValueOf(type.getValue()))
				.setDescription(description.getValue())
				.setRecordDate(registryDate.getValue())
				.setNotary(notary.getValue())
				.setNumber(protocol.getValue())
				.setVolume(tomo.getValue())
				.setSection(section.getValue())
				.setPage(page.getValue())
				.setSheet(sheet.getValue())
				.setRegistration(inscription.getValue())
				;
    		
			commonService.saveRecordData(domainName, domainId, user, recordData, new AsyncCallback<RecordData>() {
				
				@Override
				public void onSuccess(RecordData result) {
					callback.onAccept(result);
				}
				
				@Override
				public void onFailure(Throwable error) {
					AonMessagePanel.showError(messagePanel, error.getMessage());
					okButton.setEnabled(true);
				}
				
			});
    	});
    	buttonsPanel.add(okButton);
    	
    	final Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
    	cancelButton.addClickHandler(e -> {
    		cancelButton.setEnabled(false);
			callback.onCancel();
    	});
    	buttonsPanel.add(cancelButton);
    	
    	return buttonsPanel;
	}

}
