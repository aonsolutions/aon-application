package com.esferalia.aon.gwt.fiscal.client.mod421;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AdministrationListBox;
import com.esferalia.aon.gwt.common.client.widget.PeriodListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.mod421.Model421.Model421Callback;
import com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelHeader;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

class Model421NewDeclarationPanel extends DockLayoutPanel {
	
	private boolean running;
	private AdministrationListBox admonList;
	private AonIntegerBox yearBox;
	private CheckBox complementary;
	private CheckBox withoutActivity;
	private CheckBox generateFromYearStart;
	private CheckBox diffCalculationMandatory;
	private CheckBox forceDiffCalculation;
	private PeriodListBox periodList;
	private CheckBox manualDeclaration;
	private FlowPanel rootPanel;
	private SimpleLayoutPanel headerPanel = new SimpleLayoutPanel();
	
	protected Model421NewDeclarationPanel(Mod421 model, final Model421Callback callback) {
		super( Unit.PX );
		
		addNorth(headerPanel, AonFiscalModelHeader.HEIGTH);

		AonToolbar toolbar = new AonToolbar(AON.MSG.newDeclaration());
		AonToolbarButton cancelButton = new AonToolbarButton(AON.MSG.backAction(),AON.CSS.aonIconBack());
		cancelButton.addClickHandler(event ->  callback.onCancel(model) );
		toolbar.add(cancelButton);
		addNorth(toolbar, AonToolbar.HEIGTH);
		
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.setStyleName(AON.CSS.aonScrollArea());
		add(scrollPanel);
		
		rootPanel = new FlowPanel(); 
		rootPanel.setStyleName(AON.CSS.aonWidthAll());
		scrollPanel.setWidget(rootPanel);
		
		if (model == null) {
			initialize(model, callback);
		} else {
			paint(model,callback);
		}
		
	}
	
	private void registerHandlers(Mod421 model, Model421Callback callback) {
		admonList = new AdministrationListBox();
		yearBox = new AonIntegerBox();
		complementary = new CheckBox();
		withoutActivity = new CheckBox();
		generateFromYearStart = new CheckBox();
		diffCalculationMandatory = new CheckBox();
		forceDiffCalculation = new CheckBox();
		periodList = new PeriodListBox(false);
		manualDeclaration = new CheckBox();
		
		admonList.setEnabled(false);
		admonList.addChangeHandler( event -> {
			model.setAdministration( admonList.getValue() );
			initialize(model, callback );
		});
		
		yearBox.setMaxLength(4);
		yearBox.setVisibleLength(4);
		yearBox.addValueChangeHandler(event -> {
			model.setYear(yearBox.getValue());
			initialize(model, callback );
		});
		
		periodList.addChangeHandler( event -> {
			model.setPeriod( periodList.getValue());
			initialize(model, callback );
		});
		
		complementary.setText(AON.MSG.complementary());
		complementary.addClickHandler(event -> {
			model.setComplementary(complementary.getValue());
			if (AonEnumUtils.getBoolean(complementary.getValue())) {
				model.setReplacement(false);
			}
			initialize(model, callback );
		});
		
		withoutActivity.setText(AON.MSG.withoutActivity());
		withoutActivity.addClickHandler(event -> {
			model.setWithoutActivity(withoutActivity.getValue());
			initialize(model, callback );
		});

		manualDeclaration.setText(AON.MSG.manualDeclaration());
		manualDeclaration.addClickHandler(event -> {
			model.setManualDeclaration(manualDeclaration.getValue());
			if (model.isManualDeclaration()) {
			}
			initialize(model, callback);
		});

		generateFromYearStart.setText(AON.MSG.generateFromYearStartInv( model.getYear() ));
		generateFromYearStart.addClickHandler(event -> model.setGenerateFromYearStart(generateFromYearStart.getValue()));

		forceDiffCalculation.addClickHandler(event -> {
			model.setDiffCalculationMandatory(forceDiffCalculation.getValue());
			model.setDiffCalculationDisabled(!forceDiffCalculation.getValue());
		});
		
	}

	private void paint(Mod421 model, Model421Callback callback) {
		callback.hideError();
		
		registerHandlers(model,callback);
		
		headerPanel.setWidget(new AonFiscalModelHeader(model));

		rootPanel.clear();
		
		paintMessages(model);
		
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonMarginTop());
		tab.addStyleName(AON.CSS.aonMarginBottom());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		rootPanel.add(tab);
		
		populate(model);
		paintAdministration(model,callback,tab);
		paintYear(model,callback,tab);
		paintPeriod(model,callback,tab);
		paintComplementary(model,callback,tab);
		paintWithoutActivity(model,callback,tab);
		paintManualDeclaration(model,callback,tab);
		
		if ( !model.isManualDeclaration() ) {
			paintGenerateFromYearStart(model,tab);
			paintDiffCalculationMandatory(model,tab);
		}
		
		rootPanel.add(getButtonsPanel(model,callback));
	}
	
	private void paintMessages(Mod421 model) {
		if (model.getMessages() != null && !model.getMessages().isEmpty()) {
			FlowPanel messages = new FlowPanel();
			messages.setStyleName( AON.CSS.aonTextCenter() );
			messages.addStyleName( AON.CSS.aonMarginBottom() );
			messages.addStyleName( AON.CSS.aonBorder());
			messages.addStyleName( AON.CSS.aonPadding());
			messages.addStyleName( AON.CSS.aonBackgroundHighlightedOrange());
			for (String msg : model.getMessages()) {
				Label message = new Label(msg);
				message.setStyleName(AON.CSS.aonLabelWithIcon());
				message.addStyleName(AON.CSS.aonIconWarning());
				message.addStyleName(AON.CSS.aonBold());
				messages.add(message);
			}
			rootPanel.add(messages);
		}
		
	}

	private void initialize(Mod421 model, Model421Callback callback) {
		running = true;
		Model421.service.initialize(callback.getOptions().getOccam(),model,
			new AsyncCallback<Mod421>() {
				@Override
				public void onSuccess(Mod421 m421) {
					paint(m421,callback);
					running = false;
				}
	
				@Override
				public void onFailure(Throwable caught) {
					running = false;
					callback.showError(AON.MSG.unableToInitializeDeclaration(caught.getMessage()));
				}
			}
		);
	}

	private void paintAdministration(Mod421 model, Model421Callback callback, AonDisplayTable tab) {
		tab.addLabelWidgetRow(AON.MSG.administration(), admonList);
	}
	
	private void paintYear(Mod421 model, Model421Callback callback, AonDisplayTable tab) {
		tab.addLabelWidgetRow(AON.MSG.year(), yearBox);
	}

	private void paintPeriod(Mod421 model, Model421Callback callback, AonDisplayTable tab) {
		tab.addLabelWidgetRow(AON.MSG.period(), periodList);
	}

	private void paintComplementary(Mod421 model, Model421Callback callback, AonDisplayTable tab) {
		if (model.isComplementaryDeclarationAvailable()) {
			tab.addLabelWidgetRow("", complementary);
		}
	}

	private void paintManualDeclaration(Mod421 model, Model421Callback callback, AonDisplayTable tab) {
		manualDeclaration.setValue(model.isManualDeclaration());
		tab.addLabelWidgetRow("", manualDeclaration);
	}

	private void paintWithoutActivity(Mod421 model, Model421Callback callback, AonDisplayTable tab) {
		withoutActivity.setValue(model.isWithoutActivity());
		tab.addLabelWidgetRow("", withoutActivity);
	}

	private void paintGenerateFromYearStart(Mod421 model, AonDisplayTable tab) {
		if (model.isGenerateFromYearStartAvailable() ) {
			tab.addRow()
				.addCell(new Label(),AON.CSS.aonTableLabel())
				.addCell(generateFromYearStart,AON.CSS.aonWidth400());
		}
	}
	
	private void paintDiffCalculationMandatory(Mod421 model, AonDisplayTable tab) {
		if (model.isDiffCalculationMandatory() ) {
			diffCalculationMandatory.setText( "CALCULO POR DIFERENCIA OBLIGATORIO" );
			diffCalculationMandatory.setValue(true);
			diffCalculationMandatory.setEnabled(false);
			tab.addRow()
				.addCell(new Label(),AON.CSS.aonTableLabel())
				.addCell(diffCalculationMandatory,AON.CSS.aonWidth400());
		} else {
			forceDiffCalculation.setText( "Forzar c\u00E1lculo por diferencia" );
			forceDiffCalculation.setValue(false);
			forceDiffCalculation.setEnabled(true);
			tab.addRow()
				.addCell(new Label(),AON.CSS.aonTableLabel())
				.addCell(forceDiffCalculation,AON.CSS.aonWidth400());
		}
	}

	private FlowPanel getButtonsPanel(Mod421 model, final Model421Callback callback) {
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.CSS.aonPadding());
		buttonsPanel.addStyleName(AON.CSS.aonMarginTop());
		buttonsPanel.addStyleName(AON.CSS.aonTextCenter());
		
		Button acceptButton = new Button();		
		acceptButton.setStyleName(AON.CSS.aonOkButton());
		acceptButton.setText( AON.MSG.accept() + " & " + AON.MSG.saveAction());
		
		acceptButton.addClickHandler(event -> {
			if (!running) {
				acceptButton.setEnabled(false);
				callback.onAccept(model);
			}
		});
		buttonsPanel.add(acceptButton);
		Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
		cancelButton.addClickHandler(event -> callback.onCancel(model));
		buttonsPanel.add(cancelButton);
		return buttonsPanel;
	}

	private void populate(Mod421 model) {
		admonList.setSelectedIndex( model.getAdministration().ordinal());
		yearBox.setValue(model.getYear());
		periodList.setValue(model.getPeriod());
		complementary.setValue(model.isComplementary());
		withoutActivity.setValue(model.isWithoutActivity());
		manualDeclaration.setValue(model.isManualDeclaration());
		generateFromYearStart.setValue(model.isGenerateFromYearStart());
	}
	
}
