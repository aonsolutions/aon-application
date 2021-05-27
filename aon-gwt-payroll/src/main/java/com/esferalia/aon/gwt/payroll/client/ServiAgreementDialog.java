package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class ServiAgreementDialog extends AonCustomDialog {

	// -------------------------------------------------- UiBinder --------------------------------------------------
	
	interface ServiAgreementDialogUiBinder extends UiBinder<Widget, ServiAgreementDialog> {}
	
	private static ServiAgreementDialogUiBinder binder = GWT.create(ServiAgreementDialogUiBinder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {}
	
	@UiField
	SuggestBox serviAgreementsSB;
	
	@UiField
	HTMLPanel messagePanel;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	// -------------------------------------------- Variables de la clase---------------------------------------------
	
	private Button closeBtnDialog;
	private Button acceptBtnDialog;
	
	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	private Map<String, String> serviAgreementsMap;
	
	// ------------------------------------------------- CONSTRUCTOR --------------------------------------------------

	public ServiAgreementDialog() {	
		setCaption("Convenios: B" + String.valueOf("\u00FA") + "squeda");
		
		setWidget(binder.createAndBindUi(this));
		
		// Buttos Acept / Cancel
		getButtonsPanel();
		
		// Init view
		impl.getServiAgreements(new AsyncCallback<Map<String, String>>() {
			
			@Override
			public void onSuccess(Map<String, String> result) {
				serviAgreementsMap = new HashMap<String, String>();
				serviAgreementsMap.putAll(result);
				initView();
			}
			
			@Override
			public void onFailure(Throwable caught) {}
			
		});
		
	}
	
	private void initView() {
		List<String> serviAgreementsDescription = new ArrayList<>(serviAgreementsMap.keySet());
		
		List<String> serviAgreementsDescriptionSuggest = new ArrayList<String>();
		for(String serviAgreementDescription : serviAgreementsDescription)
			serviAgreementsDescriptionSuggest.add(serviAgreementDescription+"");
		
		MultiWordSuggestOracle orclServiAgreement = (MultiWordSuggestOracle) serviAgreementsSB.getSuggestOracle();
		orclServiAgreement.addAll(serviAgreementsDescriptionSuggest);
		serviAgreementsSB.setAutoSelectEnabled(true);
	}

	public void setMessageVisible(boolean visible) {
		messagePanel.setVisible(visible);
	}
	
	private void getButtonsPanel() {
		buttonsPanel.clear();
		
		closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText( AON.MSG.cancelAction());
		closeBtnDialog.setAccessKey('C');
		closeBtnDialog.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onCloseDialog(event);
			}
		});
		
		closeBtnDialog.getElement().getStyle().setMarginRight(10, Unit.PX);
		
		buttonsPanel.add(closeBtnDialog);
		
		acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtnDialog.setText( AON.MSG.accept());
		acceptBtnDialog.setAccessKey('A');
		acceptBtnDialog.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onAcceptDialog(event);
			}
		});
		
		buttonsPanel.add(acceptBtnDialog);
	}
	
	private void onCloseDialog(ClickEvent event) {
		hide();
	}
	
	private void onAcceptDialog(ClickEvent event) {
		String agreementSelected = serviAgreementsSB.getValue();
		if(!AonStringUtils.isBlank(agreementSelected)) {
			String serviAgreementCode = serviAgreementsMap.get(agreementSelected);
			onAccept(serviAgreementCode);
		}
		hide();
	}

	protected abstract void onAccept(String serviAgreementCode);

}
