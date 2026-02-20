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
//	private CheckBox replacement;
	private CheckBox complementary;
	private CheckBox withoutActivity;
//	private Label defaultVatRegimeLabel;
//	private ListBox defaultVatRegime;
	private CheckBox generateFromYearStart;
	private CheckBox diffCalculationMandatory;
	private CheckBox forceDiffCalculation;
//	private CheckBox prorate; // Aplicar prorrata (solo a partir de 2026)
//	private AonDoubleBox previousProratePercent;
//	private AonDoubleBox proratePercent;
//	private CheckBox specialProrate;
	private PeriodListBox periodList;
	private CheckBox manualDeclaration;
	
	private FlowPanel rootPanel;
//	private FlowPanel calculateProratePanel = new FlowPanel();	
	
	private SimpleLayoutPanel headerPanel = new SimpleLayoutPanel();

//	private FlowPanel proratePanel;

//	private Label labelProratePanel;

//	private Label labelPreviousProratePercent;
	
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
//		replacement = new CheckBox();
		complementary = new CheckBox();
		withoutActivity = new CheckBox();
//		defaultVatRegimeLabel = new Label();
//		defaultVatRegime = new ListBox();
		generateFromYearStart = new CheckBox();
		diffCalculationMandatory = new CheckBox();
		forceDiffCalculation = new CheckBox();
//		if (model.getYear() >= 2026) {
//			prorate = new CheckBox("Aplicar prorrata."); // Aplicar prorrata
//		}
//		previousProratePercent = new AonDoubleBox(7);
//		proratePercent = new AonDoubleBox(7);
//		specialProrate = new CheckBox("Especial");
		periodList = new PeriodListBox(true);
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
//			replacement.setEnabled(!complementary.getValue());
			if (AonEnumUtils.getBoolean(complementary.getValue())) {
//				replacement.setValue(false);
				model.setReplacement(false);
			}
			initialize(model, callback );
		});
		
//		replacement.setText(AON.MSG.replacement());
//		replacement.addClickHandler(event -> {
//			model.setReplacement(replacement.getValue());
//			complementary.setEnabled(!replacement.getValue());
//			if (AonEnumUtils.getBoolean(replacement.getValue())) {
//				complementary.setValue(false);
//				model.setComplementary(false);
//			}
//			initialize(model, callback );
//		});
		
		withoutActivity.setText(AON.MSG.withoutActivity());
		withoutActivity.addClickHandler(event -> {
			model.setWithoutActivity(withoutActivity.getValue());
			initialize(model, callback );
		});

		manualDeclaration.setText(AON.MSG.manualDeclaration());
		manualDeclaration.addClickHandler(event -> {
			model.setManualDeclaration(manualDeclaration.getValue());
			if (model.isManualDeclaration()) {
//				model.setProratePercent(0);
//				model.setSpecialProrateValue(false);
//				model.setProrate(false);
			}
			initialize(model, callback);
		});

//		defaultVatRegimeLabel.setText("Destinar Fras. sin actividad a");
//		if (defaultVatRegime.getItemCount() == 0) {
//			defaultVatRegime.addItem(VATRegime.GENERAL.getName());
//			defaultVatRegime.addItem(VATRegime.SIMPLIFIED.getName());
//		}
//		defaultVatRegime.addChangeHandler(event -> {
//			model.setDefaultVatRegime(defaultVatRegime.getSelectedIndex() == 1 ? VATRegime.SIMPLIFIED : VATRegime.GENERAL);
//			initialize(model, callback );	
//		});

		generateFromYearStart.setText(AON.MSG.generateFromYearStartInv( model.getYear() ));
		generateFromYearStart.addClickHandler(event -> model.setGenerateFromYearStart(generateFromYearStart.getValue()));

//		if (model.getYear() >= 2026) {
//			prorate.addClickHandler(event -> {
//				model.setProrate(prorate.getValue());
//				if (model.hasProrate()) {
//					initialize(model, callback);
//				} else {
//					model.setProratePercent(0.0);
//					model.setSpecialProrateValue(false);
//					model.setPreviousProratePercent(0.0);
//					labelProratePanel.setVisible(false);
//					proratePanel.setVisible(false);
//					labelPreviousProratePercent.setVisible(false);
//					previousProratePercent.setVisible(false);
//					calculateProratePanel.clear();
//				}
//			});
//		}
		
//		proratePercent.addValueChangeHandler(event -> {
//			if (proratePercent.getValue() == null) 
//				proratePercent.setValue(100.0,false); 
//			model.ensureDetail(model.getProratePercentKey()).setAmount(proratePercent.getValue());
//			specialProrate.setVisible(model.hasProrate());
//			calculateProratePanel.setVisible(((model.hasProrate() || model.hasPreviousProrate()) && model.getPeriod().isLastPeriod()));
//			if (!model.hasProrate()) {
//				specialProrate.setValue(false);
//				model.setSpecialProrateValue(specialProrate.getValue());
//			}
//		});
//
//		specialProrate.addClickHandler(event -> model.setSpecialProrateValue(specialProrate.getValue()));
		
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
//		paintReplacement(model,callback,tab);
		paintWithoutActivity(model,callback,tab);
		paintManualDeclaration(model,callback,tab);
		
		if ( !model.isManualDeclaration() ) {
//			paintDefaultVatRegime(model,callback,tab);
			paintGenerateFromYearStart(model,tab);
			paintDiffCalculationMandatory(model,tab);
//			paintProrrate(model,callback,tab);
//			rootPanel.add(calculateProratePanel);		
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

//	private void paintDefaultVatRegime(Mod421 model, Model421Callback callback, AonDisplayTable tab) {
//		if (model.isAEAT()) {
//			defaultVatRegime.setSelectedIndex( model.getDefaultVATRegime() == VATRegime.SIMPLIFIED? 1 : 0);
//			tab.addLabelWidgetRow(defaultVatRegimeLabel, defaultVatRegime);
//		}
//	}

	private void paintComplementary(Mod421 model, Model421Callback callback, AonDisplayTable tab) {
		if (model.isComplementaryDeclarationAvailable()) {
			tab.addLabelWidgetRow("", complementary);
		}
	}

//	private void paintReplacement(Mod421 model, Model421Callback callback, AonDisplayTable tab) {
//		if (model.isReplacementDeclarationAvailable() ) {
//			tab.addLabelWidgetRow("", replacement);
//		}
//		
//	}

	private void paintManualDeclaration(Mod421 model, Model421Callback callback, AonDisplayTable tab) {
		manualDeclaration.setValue(model.isManualDeclaration());
		tab.addLabelWidgetRow("", manualDeclaration);
	}

	private void paintWithoutActivity(Mod421 model, Model421Callback callback, AonDisplayTable tab) {
		withoutActivity.setValue(model.isWithoutActivity());
		tab.addLabelWidgetRow("", withoutActivity);
	}


//	private void paintProrrate(Mod421 model, Model421Callback callback, AonDisplayTable tab) {
//		
//		// Aplicar prorrata (solo a partir de 2026)
//		if (model.getYear() >= 2026) {
//			tab.addLabelWidgetRow("", prorate);	
//		}
//		
//		// PORCENTAJE DE PRORRATA ANTERIOR
//		if ((model.getYear() >= 2026 && model.hasProrate() && model.getPeriod().isLastPeriod()) || 
//		    (model.getYear() < 2026 && model.hasPreviousProrate() && model.getPeriod().isLastPeriod())) {
//			previousProratePercent.setEnabled(false);
//			labelPreviousProratePercent = new Label(model.getYear() >= 2026 ? "Porcentaje de prorrata \u00FAltimo periodo" : AON.MSG.prorrataYearPercent());			
//			tab.addLabelWidgetRow(labelPreviousProratePercent, previousProratePercent);
//		}
//
//		// PORCENTAJE DE PRORRATA
//		proratePanel = new FlowPanel();
//		proratePanel.setStyleName(AON.CSS.aonFlexBlock());
//		
//		specialProrate.setStyleName(AON.CSS.aonMarginLeft());
//		
//		proratePanel.add(proratePercent);
//		proratePanel.add(specialProrate);
//		
//		if (model.getYear() >= 2026 && !model.hasProrate()) {
//			calculateProratePanel.clear();
//		}
//		
//		if ((model.hasProrate() || model.hasPreviousProrate()) && model.getPeriod().isLastPeriod() ) {
//			AonTableButton showProrrateInfo = new AonTableButton("Mostrar informaci\u00F3n sobre el c\u00E1lculo",AON.CSS.aonIconInfo());
//			showProrrateInfo.addStyleName(AON.CSS.aonMarginLeft());
//			AonTableButton hideProrrateInfo = new AonTableButton("Ocultar informaci\u00F3n sobre el c\u00E1lculo",AON.CSS.aonIconClose());
//			hideProrrateInfo.addStyleName(AON.CSS.aonMarginLeft());
//			hideProrrateInfo.setVisible(false);
//			
//			hideProrrateInfo.addClickHandler(event -> {
//				showProrrateInfo.setVisible(true);
//				hideProrrateInfo.setVisible(false);
//				calculateProratePanel.clear();
//			});
//			showProrrateInfo.addClickHandler(event -> {
//				showProrrateInfo.setVisible(false);
//				hideProrrateInfo.setVisible(true);
//				paintCalculateProratePanel(model,callback);
//			});
//			
//			proratePanel.add(showProrrateInfo);	
//			proratePanel.add(hideProrrateInfo);
//		}
//		
//		if (model.getYear() < 2026 || (model.getYear() >= 2026 && model.hasProrate())) {
//			labelProratePanel = new Label(model.getPeriod().isLastPeriod() ? AON.MSG.prorrataFinalPercent() : AON.MSG.prorrataPercent());
//			tab.addLabelWidgetRow(labelProratePanel, proratePanel);
//		}
//	}

//	private void paintCalculateProratePanel(Mod421 model, Model421Callback callback) {
//		calculateProratePanel.clear();
//		calculateProratePanel.setStyleName(AON.CSS.aonMargin());
//		calculateProratePanel.addStyleName(AON.CSS.aonBlockCenter());
//		
//		Label proLabel = new Label("Datos utilizados para c\u00E1lculo de la prorrata definitiva");
//		proLabel.setStyleName(AON.CSS.aonBold());
//		proLabel.addStyleName(AON.CSS.aonTextUnderline());
//		proLabel.addStyleName(AON.CSS.aonTextCenter());
//		proLabel.addStyleName(AON.CSS.aonMarginTop());
//		proLabel.addStyleName(AON.CSS.aonMarginBottom());
//		calculateProratePanel.add(proLabel);
//		
//		AonDisplayTable proTab = new AonDisplayTable();
//		proTab.addStyleName(AON.CSS.aonBlockCenter());
//		fillRow(model,callback,proTab.addRow(),Mod421Key.CM_070);
//		fillRow(model,callback,proTab.addRow(),Mod421Key.CM_071);
//		calculateProratePanel.add(proTab);
//		Label calcLabel = new Label();
//		calcLabel.setStyleName(AON.CSS.aonMarginTop());
//		calcLabel.addStyleName(AON.CSS.aonBorderTop());
//		calcLabel.addStyleName(AON.CSS.aonTextCenter());
//		calcLabel.addStyleName(AON.CSS.aonBold());
//		double c70 = model.ensureDetail(Mod421Key.CM_070).getAmount();
//		double c71 = model.ensureDetail(Mod421Key.CM_071).getAmount();
//		String calc =  AON.FMT.format(c70) 
//			+ " * " 
//			+ AON.FMT.format(c71)
//			+ " / 100 = "
//			+ AON.FMT.format(model.getProratePercent())
//			+ " % ";
//		calcLabel.setText(calc);
//		calculateProratePanel.add(calcLabel);
//	}
	
//	private void fillRow(Mod421 model,Model421Callback callback, AonDisplayTableRow row, Mod421Key key) {
//		row.addCell(getLabel(key), AON.CSS.aonWidth400())
//			.addCell(getDoubleBox(model,callback,key));
//	}

//	private Label getLabel(Mod421Key key) {
//		return new Label(key.getDescription());
//	}
	
//	private AonDoubleBox getDoubleBox(Mod421 model, Model421Callback callback,Mod421Key key) {
//		AonDoubleBox box = new AonDoubleBox();
//		box.setValue(model.ensureDetail(key).getAmount());
//		box.addValueChangeHandler( event -> {
//			if (box.getValue() == null) box.setValue(0.0,false);
//			model.ensureDetail(key).setAmount(box.getValue());	
//			calculateProrrate(model);
//		});
//		return box;
//	}
	
//	private void calculateProrrate(Mod421 mod421) {
//		if (mod421.hasProrate() || mod421.hasPreviousProrate()) {
//			double c70 = AonMathUtils.round(mod421.ensureDetail(Mod421Key.CM_070).getAmount());
//			double c71 = AonMathUtils.round(mod421.ensureDetail(Mod421Key.CM_071).getAmount());
//			if (AonMathUtils.isNotZero(c71)) {
//				double prorrate = (c70 * 100 / c71);
//				prorrate = AonMathUtils.ceil(prorrate,0);
//				if (AonMathUtils.isGreatherThan(prorrate,100.0)) prorrate = 100.0;
//				mod421.ensureDetail(mod421.getProratePercentKey()).setAmount( prorrate );
//				proratePercent.setValue(prorrate);
//			}
//			mod421.ensureDetail(Mod421Key.CM_070).setAmount( c70 );
//			mod421.ensureDetail(Mod421Key.CM_071).setAmount( c71 );
//		}
//	}

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
//		replacement.setValue(model.isReplacement());
		withoutActivity.setValue(model.isWithoutActivity());
		manualDeclaration.setValue(model.isManualDeclaration());
		generateFromYearStart.setValue(model.isGenerateFromYearStart());
//		if (model.getYear() >= 2026) {
//			prorate.setValue(model.hasProrate());
//		}
//		previousProratePercent.setValue(model.ensureDetail(model.getPreviousProratePercentKey()).getAmount(),false,false);
//		proratePercent.setValue(model.ensureDetail(model.getProratePercentKey()).getAmount(),false,true);
//		specialProrate.setValue(model.isSpecialProrate());
//		specialProrate.setVisible(model.hasProrate());		
	}
}
