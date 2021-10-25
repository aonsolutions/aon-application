package com.esferalia.aon.gwt.fiscal.client;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.documental.JsAttach;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;

public abstract class AonCertificationPopup extends AonCustomDialog {
	
	private API api;
	public API getAPI() {
		return api;
	}
	
	private AonTextBox name = new AonTextBox();
	private AonTextBox document = new AonTextBox();
	private ListBox certificates = new ListBox();
	private AonTextBox nrc = new AonTextBox();
	private PasswordTextBox password = new PasswordTextBox();
	
	
	protected abstract void onAccept( AEATParams params);
	protected abstract void onCancel();

	protected AonCertificationPopup(API api, String nam, String doc, boolean showNRC) {
		this.api = api;
		setWidth("600px");
		setCaption("Certificado Digital");
		setGlassEnabled(true);
		setAnimationEnabled(true);
		
		FlowPanel rootPanel = new FlowPanel();  
		
		AonDisplayTable table = new AonDisplayTable();
		table.addStyleName(AON.CSS.aonWidthAlmostAll());
		table.addStyleName(AON.CSS.aonBlockCenter());
		table.addStyleName(AON.CSS.aonMarginTop());
		
		Label l1 = new Label("Certificado");
		l1.addStyleName(AON.CSS.aonTableLabel());
		certificates.setWidth("350px");
		certificates.addItem("-- Seleccione --");
		certificates.addChangeHandler(event -> certificates.removeStyleName(AON.CSS.aonInputTextError()));
		getAPI().getAttachment().getCertificates(new AsyncCallback<JSON<JsAttach>>() {
			
			@Override
			public void onSuccess(JSON<JsAttach> result) {
				result.getData().stream().forEach(a -> certificates.addItem(a.getTitle(), a.getId() + ""));
			}

			@Override 
			public void onFailure(Throwable caught) {
				// Nothing
			}
		});
		table.addRow()
			.addCell(l1)
			.addCell(certificates);

		Label ldc = new Label("Datos del certificado");
		ldc.setStyleName(AON.CSS.aonInnerLabel());
		ldc.addStyleName(AON.CSS.aonTextUnderline());
		table.addRow()
			.addCell(ldc)
			.addCell(new Label());

		Label l = new Label("Raz\u00F3n Social / Nombre");
		l.addStyleName(AON.CSS.aonTableLabel());
		name.setVisibleLength(45);
		name.addKeyUpHandler(event -> name.decorateAsValid());
		name.setText(nam);
		table.addRow()
			.addCell(l)
			.addCell(name);
		
		Label l0 = new Label("DNI/NIF");
		l0.addStyleName(AON.CSS.aonTableLabel());
		document.setText(doc);
		document.setVisibleLength(11);
		document.addKeyUpHandler(event -> document.decorateAsValid());
		table.addRow()
			.addCell(l0)
			.addCell(document);
	
		Label l2 = new Label("Contrase\u00f1a");
		l2.addStyleName(AON.CSS.aonTableLabel());
		password.setStyleName(AON.CSS.aonInputText());
		password.addKeyUpHandler(event -> password.removeStyleName(AON.CSS.aonInputTextError()));
		table.addRow()
			.addCell(l2)
			.addCell(password);
		
		if (showNRC) {
			Label lx = new Label("NRC");
			lx.addStyleName(AON.CSS.aonTableLabel());
			nrc.setText("");
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
			} else  if (AonStringUtils.isBlank(name.getValue())) {
				name.decorateAsError();
				name.selectAll();
				name.setFocus(true);
			} else if (AonStringUtils.isBlank(document.getValue()) || !AonDocumentUtil.isValid(document.getValue()) ) {
				document.decorateAsError();
				document.selectAll();
				document.setFocus(true);
			} else if (AonStringUtils.isBlank(password.getValue())) {
				password.addStyleName(AON.CSS.aonInputTextError());
				password.selectAll();
				password.setFocus(true);
			} else if (showNRC && AonStringUtils.isBlank(nrc.getValue())) {
				nrc.addStyleName(AON.CSS.aonInputTextError());
				nrc.selectAll();
				nrc.setFocus(true);
			} else {
				hide();
				onAccept(
						new AEATParams()
						.setName( AonStringUtils.trim(name.getValue()))
						.setDocument( AonStringUtils.trim(document.getValue()))
						.setCertificateId( AonNumberUtils.toInteger(certificates.getSelectedValue()) )
						.setPass(AonStringUtils.trim(password.getValue()))
						.setNrc(AonStringUtils.trim(nrc.getValue()))
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
		add(rootPanel);
	}
}
