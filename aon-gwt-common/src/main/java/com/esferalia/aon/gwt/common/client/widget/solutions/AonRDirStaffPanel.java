package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.registry.RDirStaff;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonRDirStaffPanel extends HTMLPanel {
	
	// Callback
	
	public static interface AonRDirStaffPanelCallback {
		void onAccept(RDirStaff rDirStaff);
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
	
	private AonRDirStaffPanelCallback callback;
	private RDirStaff rDirStaff;
	
	// Wrokplace Info
	
	private HTMLPanel messagePanel = new HTMLPanel(EMPTY_STRING);
	
	private AonCustomTextBox nif = new AonCustomTextBox("NIF");
	private AonCustomTextBox name = new AonCustomTextBox("Nombre");
	
	private AonCustomToogleButton legal = new AonCustomToogleButton("R. Legal");
	private AonCustomToogleButton laboral = new AonCustomToogleButton("R. Laboral");
	private AonCustomToogleButton socio = new AonCustomToogleButton("Socio");
	private AonCustomToogleButton admin = new AonCustomToogleButton("Admin.");
	
	private AonCustomNumberBox accionesPer = new AonCustomNumberBox("% Acciones", 2);
	private AonCustomIntegerBox acciones = new AonCustomIntegerBox("N. Acciones");
	private AonCustomNumberBox nominal = new AonCustomNumberBox("V. Nominal", 2);
	private AonCustomDateBox caducidad = new AonCustomDateBox("Caducidad Cargo");
	
	// Constructor
	
	public AonRDirStaffPanel(String domainName, Integer domain, String user, Integer registry, AonRDirStaffPanelCallback callback) {
		super(EMPTY_STRING);
		
		this.rDirStaff = new RDirStaff()
				.setDomain(domain)
				.setRegistry(registry);
		
		aonRDirStaffPanel(domainName, domain, user, callback);
	}
	
	public AonRDirStaffPanel(String domainName, Integer domain, String user, RDirStaff rDirStaff, AonRDirStaffPanelCallback callback) {
		super(EMPTY_STRING);
		
		this.rDirStaff = rDirStaff;
		
		aonRDirStaffPanel(domainName, domain, user, callback);
	}
	
	private void aonRDirStaffPanel(String domainName, Integer domain, String user, AonRDirStaffPanelCallback callback) {
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
		
		row.add(nif);
		row.add(name);
		container.add(row);
		
		// Third Row
		HTMLPanel row3 = new HTMLPanel(EMPTY_STRING);
		row3.setStyleName(AON.CSS.aonItemFlex());
		
		accionesPer.hideNearBy();
		acciones.hideNearBy();
		nominal.hideNearBy();
		
		acciones.setValue(0);
		
		row3.add(accionesPer);
		row3.add(acciones);
		row3.add(nominal);
		row3.add(caducidad);
		container.add(row3);
		
		// Row 2
		HTMLPanel row2 = new HTMLPanel(EMPTY_STRING);
		row2.setStyleName(AON.CSS.aonItemFlex());
		
		row2.add(legal);
		row2.add(laboral);
		row2.add(socio);
		row2.add(admin);
		container.add(row2);
		
		// Fill info
		if(rDirStaff.getId() != null) {
			nif.setValue(rDirStaff.getDocument());
			name.setValue(rDirStaff.getName());
			
			legal.setValue(rDirStaff.getRepresentative());
			laboral.setValue(rDirStaff.getRepresentativeLabor());
			socio.setValue(rDirStaff.getShareHolder());
			admin.setValue(rDirStaff.getDirector());
			
			accionesPer.setValue(rDirStaff.getPercentShare());
			acciones.setValue(rDirStaff.getShareNumber());
			nominal.setValue(rDirStaff.getNominalValue());
			caducidad.setValue(rDirStaff.getDueDate());
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
			
    		rDirStaff
				.setDocument(nif.getValue())
				.setName(name.getValue())
				.setRepresentative(legal.getValue())
				.setRepresentativeLabor(laboral.getValue())
				.setShareHolder(socio.getValue())
				.setDirector(admin.getValue())
				.setPercentShare(accionesPer.getValue())
				.setShareNumber(null == acciones.getValue() ? 0 : acciones.getValue())
				.setNominalValue(nominal.getValue())
				.setDueDate(caducidad.getValue())
				;
    		
			commonService.saveRDirStaff(domainName, domainId, user, rDirStaff, new AsyncCallback<RDirStaff>() {
				
				@Override
				public void onSuccess(RDirStaff result) {
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
