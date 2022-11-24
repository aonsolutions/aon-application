package com.esferalia.aon.gwt.fiscal.client.mod303;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AdministrationListBox;
import com.esferalia.aon.gwt.common.client.widget.PeriodListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable.AonDisplayTableRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303.Model303Callback;
import com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelHeader;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

class Model303NewDeclarationPanel extends DockLayoutPanel {
	
	private boolean running;

	private AdministrationListBox admonList;
	private AonIntegerBox yearBox;
	private CheckBox replacement;
	private CheckBox complementary;
	private CheckBox withoutActivity;
	private Label defaultVatRegimeLabel;
	private ListBox defaultVatRegime;
	private CheckBox generateFromYearStart;
	private CheckBox diffCalculationMandatory;
	private CheckBox forceDiffCalculation;
	private AonDoubleBox previousProrate;
	private AonDoubleBox prorate;
	private CheckBox specialProrate;
	private PeriodListBox periodList;
	private CheckBox manualDeclaration;
	
	private FlowPanel rootPanel;
	private FlowPanel calculateProratePanel = new FlowPanel();	
	
	private SimpleLayoutPanel headerPanel = new SimpleLayoutPanel();
	
	protected Model303NewDeclarationPanel(Mod303 model, final Model303Callback callback) {
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
		
		paint(model,callback);
		
	}
	
	private void registerHandlers(Mod303 model, Model303Callback callback) {
		admonList = new AdministrationListBox();
		yearBox = new AonIntegerBox();
		replacement = new CheckBox();
		complementary = new CheckBox();
		withoutActivity = new CheckBox();
		defaultVatRegimeLabel = new Label();
		defaultVatRegime = new ListBox();
		generateFromYearStart = new CheckBox();
		diffCalculationMandatory = new CheckBox();
		forceDiffCalculation = new CheckBox();
		previousProrate = new AonDoubleBox(7);
		prorate = new AonDoubleBox(7);
		specialProrate = new CheckBox("Especial");
		periodList = new PeriodListBox(true);
		manualDeclaration = new CheckBox();
		
		
		
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
			replacement.setEnabled(!complementary.getValue());
			if (AonEnumUtils.getBoolean(complementary.getValue())) {
				replacement.setValue(false);
			}
			initialize(model, callback );
		});
		
		replacement.setText(AON.MSG.replacement());
		replacement.addClickHandler(event -> {
			model.setReplacement(replacement.getValue());
			complementary.setEnabled(!replacement.getValue());
			if (AonEnumUtils.getBoolean(replacement.getValue())) {
				complementary.setValue(false);
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
			if ( model.isManualDeclaration()) {
				model.setProratePercent(0);
				model.setSpecialProrateValue(false);
			}
			initialize(model, callback );
		});

		defaultVatRegimeLabel.setText("Destinar Fras. sin actividad a");
		if (defaultVatRegime.getItemCount() == 0) {
			defaultVatRegime.addItem(VATRegime.GENERAL.getName());
			defaultVatRegime.addItem(VATRegime.SIMPLIFIED.getName());
		}
		defaultVatRegime.addChangeHandler(event -> {
			model.setDefaultVatRegime(defaultVatRegime.getSelectedIndex() == 1? VATRegime.SIMPLIFIED: VATRegime.GENERAL);
			initialize(model, callback );	
		});

		generateFromYearStart.setText(AON.MSG.generateFromYearStartInv( model.getYear() ));
		generateFromYearStart.addClickHandler(event -> model.setGenerateFromYearStart(generateFromYearStart.getValue()));

		prorate.addValueChangeHandler(event -> {
			if (prorate.getValue() == null) prorate.setValue(100.0,false); 
			model.ensureDetail(model.getProrateKey()).setAmount(prorate.getValue());
			specialProrate.setVisible(model.hasProrate());
			calculateProratePanel.setVisible(((model.hasProrate() || model.hasPreviousProrate()) && model.getPeriod().isLastPeriod()));
			if (!model.hasProrate()) {
				specialProrate.setValue(false);
				model.setSpecialProrateValue( specialProrate.getValue() );
			}
		});

		specialProrate.addClickHandler(event -> model.setSpecialProrateValue( specialProrate.getValue() ));
		
		forceDiffCalculation.addClickHandler(event -> {
			model.setDiffCalculationMandatory(forceDiffCalculation.getValue());
			model.setDiffCalculationDisabled(!forceDiffCalculation.getValue());
		});
		
	}

	private void paint(Mod303 model, Model303Callback callback) {
		
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
		paintReplacement(model,callback,tab);
		paintWithoutActivity(model,callback,tab);
		
		paintManualDeclaration(model,callback,tab);
		
		if ( !model.isManualDeclaration() ) {
			paintDefaultVatRegime(model,callback,tab);
			paintGenerateFromYearStart(model,tab);
			paintDiffCalculationMandatory(model,tab);
			paintProrrate(model,callback,tab);
			rootPanel.add(calculateProratePanel);		
		}
		
		rootPanel.add(getButtonsPanel(model,callback));
	}
	
	private void paintMessages(Mod303 model) {
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

	private void initialize(Mod303 model, Model303Callback callback) {
		running = true;
		Model303.service.initialize(callback.getOptions().getOccam(),model,
			new AsyncCallback<Mod303>() {
				@Override
				public void onSuccess(Mod303 m303) {
					paint(m303,callback);
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

	private void paintAdministration(Mod303 model, Model303Callback callback, AonDisplayTable tab) {
		tab.addLabelWidgetRow(AON.MSG.administration(), admonList);
	}
	
	private void paintYear(Mod303 model, Model303Callback callback, AonDisplayTable tab) {
		tab.addLabelWidgetRow(AON.MSG.year(), yearBox);
	}

	private void paintPeriod(Mod303 model, Model303Callback callback, AonDisplayTable tab) {
		tab.addLabelWidgetRow(AON.MSG.period(), periodList);
	}

	private void paintDefaultVatRegime(Mod303 model, Model303Callback callback, AonDisplayTable tab) {
		if (model.isAEAT()) {
			defaultVatRegime.setSelectedIndex( model.getDefaultVATRegime() == VATRegime.SIMPLIFIED? 1 : 0);
			tab.addLabelWidgetRow(defaultVatRegimeLabel, defaultVatRegime);
		}
	}

	private void paintComplementary(Mod303 model, Model303Callback callback, AonDisplayTable tab) {
		if (model.isComplementaryDeclarationAvailable()) {
			tab.addLabelWidgetRow("", complementary);
		}
	}

	private void paintReplacement(Mod303 model, Model303Callback callback, AonDisplayTable tab) {
		if (model.isReplacementDeclarationAvailable() ) {
			tab.addLabelWidgetRow("", replacement);
		}
		
	}

	private void paintManualDeclaration(Mod303 model, Model303Callback callback, AonDisplayTable tab) {
		manualDeclaration.setValue(model.isManualDeclaration());
		tab.addLabelWidgetRow("", manualDeclaration);
	}

	private void paintWithoutActivity(Mod303 model, Model303Callback callback, AonDisplayTable tab) {
		withoutActivity.setValue(model.isWithoutActivity());
		tab.addLabelWidgetRow("", withoutActivity);
	}


	private void paintProrrate(Mod303 model, Model303Callback callback, AonDisplayTable tab) {
// 		PORCENTAJE DE PRORRATA ANTERIOR
		if (model.hasPreviousProrate() && model.getPeriod().isLastPeriod() ) {
			previousProrate.setEnabled(false);
			tab.addLabelWidgetRow(AON.MSG.prorrataYearPercent(), previousProrate);
		}

		// PORCENTAJE DE PRORRATA
		FlowPanel proratePanel = new FlowPanel();
		proratePanel.setStyleName(AON.CSS.aonFlexBlock());
		
		specialProrate.setStyleName(AON.CSS.aonMarginLeft());  
		
		proratePanel.add(prorate);
		proratePanel.add(specialProrate);
		
		if ((model.hasProrate() || model.hasPreviousProrate()) && model.getPeriod().isLastPeriod() ) {
			AonTableButton showProrrateInfo = new AonTableButton("Mostrar informaci\u00F3n sobre el c\u00E1lculo",AON.CSS.aonIconInfo());
			showProrrateInfo.addStyleName(AON.CSS.aonMarginLeft());
			AonTableButton hideProrrateInfo = new AonTableButton("Ocultar informaci\u00F3n sobre el c\u00E1lculo",AON.CSS.aonIconClose());
			hideProrrateInfo.addStyleName(AON.CSS.aonMarginLeft());
			hideProrrateInfo.setVisible(false);
			
			hideProrrateInfo.addClickHandler(event -> {
				showProrrateInfo.setVisible(true);
				hideProrrateInfo.setVisible(false);
				calculateProratePanel.clear();
			});
			showProrrateInfo.addClickHandler(event -> {
				showProrrateInfo.setVisible(false);
				hideProrrateInfo.setVisible(true);
				paintCalculateProratePanel(model,callback);
			});
			
			proratePanel.add(showProrrateInfo);	
			proratePanel.add(hideProrrateInfo);
		}
		
//		if ((model.hasProrate() || model.hasPreviousProrate()) && model.getPeriod().isLastPeriod() ) {
//			paintCalculateProratePanel(model,callback);
//		} else {
//			calculateProratePanel.clear();	
//		}
		
		tab.addLabelWidgetRow( model.getPeriod().isLastPeriod()
				?AON.MSG.prorrataFinalPercent()
				:AON.MSG.prorrataPercent(), proratePanel);
	}

	private void paintCalculateProratePanel(Mod303 model, Model303Callback callback) {
		calculateProratePanel.clear();
		calculateProratePanel.setStyleName(AON.CSS.aonMargin());
		calculateProratePanel.addStyleName(AON.CSS.aonBlockCenter());
		
		Label proLabel = new Label("Datos utilizados para c\u00E1lculo de la prorrata definitiva");
		proLabel.setStyleName(AON.CSS.aonBold());
		proLabel.addStyleName(AON.CSS.aonTextUnderline());
		proLabel.addStyleName(AON.CSS.aonTextCenter());
		proLabel.addStyleName(AON.CSS.aonMarginTop());
		proLabel.addStyleName(AON.CSS.aonMarginBottom());
		calculateProratePanel.add(proLabel);
		
		AonDisplayTable proTab = new AonDisplayTable();
		proTab.addStyleName(AON.CSS.aonBlockCenter());
		fillRow(model,callback,proTab.addRow(),Mod303Key.CM_070);
		fillRow(model,callback,proTab.addRow(),Mod303Key.CM_071);
		calculateProratePanel.add(proTab);
		Label calcLabel = new Label();
		calcLabel.setStyleName(AON.CSS.aonMarginTop());
		calcLabel.addStyleName(AON.CSS.aonBorderTop());
		calcLabel.addStyleName(AON.CSS.aonTextCenter());
		calcLabel.addStyleName(AON.CSS.aonBold());
		double c70 = model.ensureDetail(Mod303Key.CM_070).getAmount();
		double c71 = model.ensureDetail(Mod303Key.CM_071).getAmount();
		String calc =  AON.FMT.format(c70) 
			+ " * " 
			+ AON.FMT.format(c71)
			+ " / 100 = "
			+ AON.FMT.format(model.getProratePercent())
			+ " % ";
		calcLabel.setText(calc);
		calculateProratePanel.add(calcLabel);
	}
	
	private void fillRow(Mod303 model,Model303Callback callback, AonDisplayTableRow row, Mod303Key key) {
		row.addCell(getLabel(key), AON.CSS.aonWidth400())
			.addCell(getDoubleBox(model,callback,key));
	}

	private Label getLabel(Mod303Key key) {
		return new Label(key.getDescription());
	}
	
	
	private AonDoubleBox getDoubleBox(Mod303 model, Model303Callback callback,Mod303Key key) {
		AonDoubleBox box = new AonDoubleBox();
		box.setValue(model.ensureDetail(key).getAmount());
		box.addValueChangeHandler( event -> {
			if (box.getValue() == null) box.setValue(0.0,false);
			model.ensureDetail(key).setAmount(box.getValue());	
			calculateProrrate(model,callback);
		});
		return box;
	}
	
	private void calculateProrrate(Mod303 model, Model303Callback callback) {
		Model303.service.calculateProrrate(callback.getOptions().getOccam(), model, 
			new AsyncCallback<Mod303>() {
			
			@Override
			public void onSuccess(Mod303 result) {
				populate(result);
				paintCalculateProratePanel(result,callback); 
			}
			
			@Override
			public void onFailure(Throwable caught) {
				callback.showError(caught.getMessage());
			}
		});
	}

	private void paintGenerateFromYearStart(Mod303 model, AonDisplayTable tab) {
		if (model.isGenerateFromYearStartAvailable() ) {
			tab.addRow()
				.addCell(new Label(),AON.CSS.aonTableLabel())
				.addCell(generateFromYearStart,AON.CSS.aonWidth400());
		}
	}
	
	private void paintDiffCalculationMandatory(Mod303 model, AonDisplayTable tab) {
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
	

	private FlowPanel getButtonsPanel(Mod303 model, final Model303Callback callback) {
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

	private void populate(Mod303 model) {
		admonList.setSelectedIndex( model.getAdministration().ordinal());
		yearBox.setValue(model.getYear());
		periodList.setValue(model.getPeriod());
		complementary.setValue(model.isComplementary());
		replacement.setValue(model.isReplacement());
		withoutActivity.setValue(model.isWithoutActivity());
		manualDeclaration.setValue(model.isManualDeclaration());
		generateFromYearStart.setValue(model.isGenerateFromYearStart());
		previousProrate.setValue(model.ensureDetail(model.getPreviousProrateKey()).getAmount(),false,false);
		specialProrate.setValue(model.isSpecialProrate());
		specialProrate.setVisible(model.hasProrate());
		prorate.setValue(model.ensureDetail(model.getProrateKey()).getAmount(),false,true);
	}
}
