package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class ServiAgreementDialog extends AonCustomDialog {

	// -------------------------------------------------- UiBinder
	
	interface ServiAgreementDialogUiBinder extends UiBinder<Widget, ServiAgreementDialog> {}
	
	private static ServiAgreementDialogUiBinder binder = GWT.create(ServiAgreementDialogUiBinder.class);
	
	// -------------------------------------------------- UiFields
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String flex();
		String loadingPanel();
	}
	
	@UiField
	SuggestBox serviAgreementsSB;
	
	@UiField
	HTMLPanel datesPanel;
	
	@UiField
	HTMLPanel messagePanel;
	
	@UiField
	HTMLPanel loadingPanel;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	// -------------------------------------------------- Variables
	
	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	private Map<String, String> serviAgreementsMap;
	private Button acceptBtnDialog;
	
	// -------------------------------------------------- Constructor

	protected ServiAgreementDialog() {	
		setCaption("Convenios: B\u00FAsqueda");
		
		setWidget(binder.createAndBindUi(this));
		
		// Buttos Acept / Cancel
		getButtonsPanel();
		
		// Init view
		impl.getServiAgreements(new AsyncCallback<Map<String, String>>() {
			
			@Override
			public void onSuccess(Map<String, String> result) {
				serviAgreementsMap = new HashMap<>();
				serviAgreementsMap.putAll(result);
				initView();
				showDialog();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Nothing to do here
			}
			
		});
	}
	
	// -------------------------------------------------- View Methods
	
	private void initView() {
		List<String> serviAgreementsDescription = new ArrayList<>(serviAgreementsMap.keySet());
		
		List<String> serviAgreementsDescriptionSuggest = new ArrayList<>();
		for(String serviAgreementDescription : serviAgreementsDescription)
			serviAgreementsDescriptionSuggest.add(serviAgreementDescription+"");
		
		MultiWordSuggestOracle orclServiAgreement = (MultiWordSuggestOracle) serviAgreementsSB.getSuggestOracle();
		orclServiAgreement.addAll(serviAgreementsDescriptionSuggest);
		serviAgreementsSB.setAutoSelectEnabled(true);
		
		serviAgreementsSB.addSelectionHandler(e -> {
			String agreementSelected = serviAgreementsSB.getValue();
			String serviAgreementCode = serviAgreementsMap.get(agreementSelected);
			
			createLoadingPanel();
			
			acceptBtnDialog.setEnabled(false);
			
			impl.getServiAgreementDates(serviAgreementCode, new AsyncCallback<List<Integer>>() {

				@Override
				public void onFailure(Throwable caught) {
					AonDialog errorDialog = new AonDialog("Error obtenci\u00f3n XML", new HTMLPanel(caught.getMessage()));
					errorDialog.warning();
				}

				@Override
				public void onSuccess(List<Integer> agreementDates) {
					createDatesPanel(agreementDates);
					loadingPanel.clear();
					acceptBtnDialog.setEnabled(true);
				}

			});
		});
	}
	
	private void createDatesPanel(List<Integer> agreementDates) {
		datesPanel.setVisible(true);
		datesPanel.clear();
		
		Label datesLabel = new Label("Seleccione los tramos que desea importar:");
		datesLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		datesPanel.add(datesLabel);
		
		agreementDates.forEach(date -> {
			CheckBox cb = new CheckBox();
			cb.getElement().getStyle().setMarginLeft(1, Unit.EM);
			cb.setText(date.toString());
			cb.addValueChangeHandler(e -> {
				if(Boolean.TRUE.equals(e.getValue())) {
					acceptBtnDialog.setEnabled(true);
					loadingPanel.clear();
				}
					
			});
			datesPanel.add(cb);
		});
	}

	public void setMessageVisible(boolean visible) {
		messagePanel.setVisible(visible);
	}
	
	// -------------------------------------------------- Buttons Panel
	
	private void getButtonsPanel() {
		buttonsPanel.clear();
		
		Button closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText( AON.MSG.cancelAction());
		closeBtnDialog.addClickHandler(e -> hide());
		
		buttonsPanel.add(closeBtnDialog);
		
		acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtnDialog.setText( AON.MSG.accept());
		acceptBtnDialog.addClickHandler(e -> onAcceptDialog());
		
		buttonsPanel.add(acceptBtnDialog);
	}
	
	private void createLoadingPanel() {
		loadingPanel.clear();
		
		AonTableButton loadingBtn = new AonTableButton("", AON.CSS.aonIconRenew());
		loadingBtn.addStyleName(style.loadingPanel());
		
		loadingPanel.add(loadingBtn);
		
		Label loadingLabel = new Label("Obteniento tramos disponibles del convenio...");
		loadingLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		loadingPanel.add(loadingLabel);
	}
	
	private void createErrorDatesPanel() {
		loadingPanel.clear();
		
		AonTableButton loadingBtn = new AonTableButton("", AON.CSS.aonIconWarning());
		loadingPanel.add(loadingBtn);
		
		Label loadingLabel = new Label("Debe seleccionar al menos un tramo para importar");
		loadingLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		loadingPanel.add(loadingLabel);
	}
	
	private void onAcceptDialog() {
		if(notDateSelected()) {
			acceptBtnDialog.setEnabled(false);
			createErrorDatesPanel();
		} else {
			acceptBtnDialog.setEnabled(true);
			loadingPanel.clear();
			
			String agreementSelected = serviAgreementsSB.getValue();
			if(!AonStringUtils.isBlank(agreementSelected)) {
				String serviAgreementCode = serviAgreementsMap.get(agreementSelected);
				List<Integer> selectedDates = getSelectedDates();
				onAccept(serviAgreementCode, selectedDates);
			}
			hide();
		}
	}
	
	private boolean notDateSelected() {
		for(int i = 1; i < datesPanel.getWidgetCount(); i++){
			CheckBox cb = (CheckBox) datesPanel.getWidget(i);
			if(Boolean.TRUE.equals(cb.getValue()))
				return false;
		}
		
		return true;
	}

	private List<Integer> getSelectedDates() {
		List<Integer> dates = new ArrayList<>();
		
		for(int i = 1; i < datesPanel.getWidgetCount(); i++){
			CheckBox cb = (CheckBox) datesPanel.getWidget(i);
			if(Boolean.TRUE.equals(cb.getValue()))
				dates.add(Integer.parseInt(cb.getText()));
		}
		
		return dates;
	}
	
	public void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
			serviAgreementsSB.setFocus(true);
		});
	}

	// -------------------------------------------------- Abstract Methods

	protected abstract void onAccept(String serviAgreementCode, List<Integer> selectedDates);

}
