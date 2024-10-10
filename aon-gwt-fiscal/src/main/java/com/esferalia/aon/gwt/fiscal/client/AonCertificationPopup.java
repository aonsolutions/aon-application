package com.esferalia.aon.gwt.fiscal.client;

import java.io.Serializable;
import java.util.HashMap;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.documental.JsCertificate;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;

public abstract class AonCertificationPopup extends AonCustomDialog {
	
	public static class AonCertificationPopupParams implements Serializable {
		
		private static final long serialVersionUID = 913077124638436678L;
		
		private String name;
		private String document;
		private boolean showName = true;
		private boolean showDocument = true;
		private boolean showNRC;
		private boolean testEnvironment;
		private String infoMessage;
		private String nrc;
		
		public String getName() {
			return name;
		}
		public AonCertificationPopupParams setName(String name) {
			this.name = name;
			return this;
		}
		
		public String getDocument() {
			return document;
		}
		public AonCertificationPopupParams setDocument(String document) {
			this.document = document;
			return this;
		}
		
		public boolean isShowNRC() {
			return showNRC;
		}
		public AonCertificationPopupParams setShowNRC(boolean showNRC) {
			this.showNRC = showNRC;
			return this;
		}
		
		public boolean isShowName() {
			return showName;
		}
		public AonCertificationPopupParams setShowName(boolean showName) {
			this.showName = showName;
			return this;
		}
		
		public boolean isShowDocument() {
			return showDocument;
		}
		public AonCertificationPopupParams setShowDocument(boolean showDocument) {
			this.showDocument = showDocument;
			return this;
		}
		
		public boolean isTestEnvironment() {
			return testEnvironment;
		}
		public AonCertificationPopupParams setTestEnvironment(boolean testEnvironment) {
			this.testEnvironment = testEnvironment;
			return this;
		}
		
		public String getInfoMessage() {
			return infoMessage;
		}
		public AonCertificationPopupParams setInfoMessage(String infoMessage) {
			this.infoMessage = infoMessage;
			return this;
		}
		public String getNrc() {
			return nrc;
		}
		public AonCertificationPopupParams setNrc(String nrc) {
			this.nrc = nrc;
			return this;
		}
	}
	
	private API api;
	public API getAPI() {
		return api;
	}
	private FlowPanel rootPanel; 	
	private AonTextBox name = new AonTextBox();
	private AonTextBox document = new AonTextBox();
	private ListBox certificates = new ListBox();
	private AonTextBox nrc = new AonTextBox();
	private PasswordTextBox password = new PasswordTextBox();
	
	
	protected abstract void onAccept( AEATParams params);
	protected abstract void onCancel();

	protected AonCertificationPopup(API api, AonCertificationPopupParams params) {
		this.api = api;
		setWidth("600px");
		setCaption("Certificado Digital");
		setGlassEnabled(true);
		setAnimationEnabled(true);
		
		rootPanel = new FlowPanel();
		rootPanel.setStyleName(AON.CSS.aonWidthAll());
		rootPanel.add(new AonSplash());
		add(rootPanel);
		
		getAPI().getAttachment().getAeatCertificates(new AsyncCallback<JSON<JsCertificate>>() {
			
			@Override
			public void onSuccess(JSON<JsCertificate> result) {
				if ( result == null || result.getData() == null || result.getData().length() == 0 ) {
					paintError("No se han encontrado certificados");
				} else {
					paintPanel(params, result);
				}
			}

			@Override 
			public void onFailure(Throwable caught) {
				paintError("Se ha producido un error. [" + caught.getMessage() + "]");
			}

		});
		
	}
	
	private void paintError(String message) {
		rootPanel.clear();

		FlowPanel messageContainerPanel = new FlowPanel();
		messageContainerPanel.setStyleName(AON.CSS.aonMarginBottom());
		messageContainerPanel.addStyleName(AON.CSS.aonMarginTop());
		messageContainerPanel.addStyleName(AON.CSS.aonBlockCenter());
		messageContainerPanel.addStyleName(AON.CSS.aonWidthAlmostAll());

		Label messageLabel = new Label(message); 
		messageLabel.addStyleName(AON.CSS.aonBlockCenter());
		messageLabel.addStyleName(AON.CSS.aonBlockMessage());
		messageLabel.addStyleName(AON.CSS.aonBlockErrorMessage());
		
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.CSS.aonPadding());
		buttonsPanel.addStyleName(AON.CSS.aonMarginTop());
		buttonsPanel.addStyleName(AON.CSS.aonTextCenter());

		Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.close());
		cancelButton.addClickHandler(event -> {
			hide();
			onCancel();
		});
		buttonsPanel.add(cancelButton);
		messageContainerPanel.add(messageLabel);
		rootPanel.add(messageContainerPanel);
		rootPanel.add(buttonsPanel);
	}
	
	private void paintPanel(AonCertificationPopupParams params, JSON<JsCertificate> result) {
		rootPanel.clear();
		if (AonStringUtils.isNotBlank( params.getInfoMessage())) {
			FlowPanel messagePanel = new FlowPanel(); 
			messagePanel.setStyleName(AON.CSS.aonMarginBottom());
			messagePanel.addStyleName(AON.CSS.aonMarginTop());
			messagePanel.addStyleName(AON.CSS.aonBlockCenter());
			messagePanel.addStyleName(AON.CSS.aonWidthAlmostAll());
			
			FlowPanel messageContainerPanel = new FlowPanel();
			messageContainerPanel.setStyleName(AON.CSS.aonBlockMessage());
			messageContainerPanel.addStyleName(AON.CSS.aonBlockInfoMessage());
			messageContainerPanel.addStyleName(AON.CSS.aonDisplayFlex());
			
			InlineLabel messageLabel = new InlineLabel( params.getInfoMessage() );
			messageLabel.setStyleName(AON.CSS.aonFlexGrow1());
			messageContainerPanel.add(messageLabel);
			
			InlineLabel serverLabel = new InlineLabel();
			serverLabel.setStyleName(AON.CSS.aonMarginLeft());
			serverLabel.getElement().getStyle().setPadding(2.0, Unit.PX);
			if (params.isTestEnvironment()) {
				serverLabel.setText("[Entorno de pruebas]");
				serverLabel.getElement().getStyle().setBackgroundColor("red");
				serverLabel.addStyleName(AON.CSS.aonColorWhite());
			} else {
				serverLabel.setText("[Presentaci\u00F3n en AEAT]");
				serverLabel.addStyleName(AON.CSS.aonMarginLeft());
				serverLabel.addStyleName(AON.CSS.aonAeatBackgroundColor());
				serverLabel.addStyleName(AON.CSS.aonColorWhite());
			}
			messageContainerPanel.add(serverLabel);
			messagePanel.add(messageContainerPanel);
			rootPanel.add(messagePanel);	
		}
		
		AonDisplayTable table = new AonDisplayTable();
		table.addStyleName(AON.CSS.aonBlockCenter());
		table.addStyleName(AON.CSS.aonMarginTop());

		Label l1 = new Label("Certificado");
		l1.addStyleName(AON.CSS.aonTableLabel());
		certificates.setWidth("350px");
		certificates.addItem("-- Seleccione --");
		HashMap<Integer, Boolean> showPasswordMap = new HashMap<>();

		certificates.addChangeHandler(event -> certificates.removeStyleName(AON.CSS.aonInputTextError()));
		result.getData().stream().forEach(a -> {
			certificates.addItem(a.getName(), a.getId() + "");
			showPasswordMap.put(a.getId(), a.hasPassword());
		});

		table.addRow()
			.addCell(l1)
			.addCell(certificates);
		
		if(params.isShowDocument() || params.isShowName()) {
			Label ldc = new Label("Datos del certificado");
			ldc.setStyleName(AON.CSS.aonInnerLabel());
			ldc.addStyleName(AON.CSS.aonTextUnderline());
			table.addRow()
				.addCell(ldc)
				.addCell(new Label());
		}
		if(params.isShowName()) {
			Label l = new Label("Raz\u00F3n Social / Nombre");
			l.addStyleName(AON.CSS.aonTableLabel());
			name.setVisibleLength(45);
			name.addKeyUpHandler(event -> name.decorateAsValid());
			name.setText(params.getName());
			table.addRow()
				.addCell(l)
				.addCell(name);
		}

		if(params.isShowDocument()) {
			Label l0 = new Label("DNI/NIF");
			l0.addStyleName(AON.CSS.aonTableLabel());
			document.setText(params.getDocument());
			document.setVisibleLength(11);
			document.addKeyUpHandler(event -> document.decorateAsValid());
			table.addRow()
				.addCell(l0)
				.addCell(document);
		}
		Label l2 = new Label("Contrase\u00f1a");
		l2.addStyleName(AON.CSS.aonTableLabel());
		password.setStyleName(AON.CSS.aonInputText());
		password.getElement().setAttribute("autocomplete", "new-password");
		password.addKeyUpHandler(event -> password.removeStyleName(AON.CSS.aonInputTextError()));

		certificates.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				l2.setVisible(!showPasswordMap.get(Integer.parseInt(certificates.getSelectedValue())));
				password.setVisible(!showPasswordMap.get(Integer.parseInt(certificates.getSelectedValue())));
				password.setValue(""); // Dejamos la contraseña en blanco para que se coja del certificado cuando se realice el envío o para que la introduzca de nuevo el usuario, si el certificado no tiene contraseña
			}
		});

		table.addRow()
			.addCell(l2)
			.addCell(password);

		nrc.setVisibleLength(22);
		nrc.setMaxLength(22);
		nrc.setText(params.getNrc());
		if (params.isShowNRC()) {
			Label lx = new Label("NRC");
			lx.addStyleName(AON.CSS.aonTableLabel());			
			table.addRow()
				.addCell(lx)
				.addCell(nrc);
		}
		
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.CSS.aonPadding());
		buttonsPanel.addStyleName(AON.CSS.aonMarginTop());
		buttonsPanel.addStyleName(AON.CSS.aonTextCenter());
		Button acceptButton = new Button();
		acceptButton.setStyleName(AON.CSS.aonOkButton());
		acceptButton.setText( AON.MSG.accept());
		
		acceptButton.addClickHandler(event -> {
			if (certificates.getSelectedIndex() == 0) {
				certificates.addStyleName(AON.CSS.aonInputTextError());
				certificates.setFocus(true);
			} else if(params.isShowName() && AonStringUtils.isBlank(name.getValue())) {
				name.decorateAsError();
				name.selectAll();
				name.setFocus(true);
			} else if(params.isShowDocument() && (AonStringUtils.isBlank(document.getValue()) || !AonDocumentUtil.isValid(document.getValue()))) {
				document.decorateAsError();
				document.selectAll();
				document.setFocus(true);
			} else if(password.isVisible() && AonStringUtils.isBlank(password.getValue())) {
				password.addStyleName(AON.CSS.aonInputTextError());
				password.selectAll();
				password.setFocus(true);
			} else if(params.isShowNRC() && AonStringUtils.isBlank(nrc.getValue())) {
				nrc.addStyleName(AON.CSS.aonInputTextError());
				nrc.selectAll();
				nrc.setFocus(true);
			} else {
				hide();
				onAccept(
						new AEATParams()
						.setName(params.isShowName() ? AonStringUtils.trim(name.getValue()) : "")
						.setDocument(params.isShowDocument() ? AonStringUtils.trim(document.getValue()) : "")
						.setCertificateId( AonNumberUtils.toInteger(certificates.getSelectedValue()) )
						.setPass(AonStringUtils.trim(password.getValue()))
						.setNrc(AonStringUtils.trim(nrc.getValue()))
						.setTest(params.isTestEnvironment())
						);
			}
		});
		
		buttonsPanel.add(acceptButton);
		Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
		cancelButton.addClickHandler(event -> {
			hide();
			onCancel();
		});
		buttonsPanel.add(cancelButton);
		
		rootPanel.add(table);
		rootPanel.add(buttonsPanel);
	}
}
