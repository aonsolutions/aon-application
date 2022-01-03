package com.esferalia.aon.gwt.fiscal.client.mod303;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AdministrationListBox;
import com.esferalia.aon.gwt.common.client.widget.PeriodListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable.AonDisplayTableRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303.Model303Callback;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

class Model303NewDeclarationPopup extends AonCustomDialog {
	
	private AdministrationListBox admonList = new AdministrationListBox();
	private AonIntegerBox yearBox = new AonIntegerBox();
	private CheckBox replacement = new CheckBox();
	private CheckBox complementary = new CheckBox();
	private CheckBox withoutActivity = new CheckBox();
	private CheckBox diffCalculation = new CheckBox();
	private Label defaultVatRegimeLabel = new Label();
	private ListBox defaultVatRegime = new ListBox();
	private AonDoubleBox previousProrate = new AonDoubleBox(7);
	private AonDoubleBox prorate = new AonDoubleBox(7);
	private CheckBox specialProrate = new CheckBox("Especial");
	private PeriodListBox periodList = new PeriodListBox(true);
	
	private FlowPanel rootPanel;
	private FlowPanel calculateProratePanel = new FlowPanel();	
	
	private Mod303 model; 
	
	protected Model303NewDeclarationPopup(final Mod303 model, final Model303Callback callback) {
		this.model = model;
		
		setCaption(AON.MSG.newDeclaration());
		setGlassEnabled(true);
		setAnimationEnabled(true);
		setWidth("600px");

		rootPanel = new FlowPanel(); 
		rootPanel.setStyleName(AON.CSS.aonWidthAll());
		
		paint(callback);
		add(rootPanel);
	}

	private void paint(Model303Callback callback) {
		rootPanel.clear();
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab.addStyleName(AON.CSS.aonMarginTop());
		tab.addStyleName(AON.CSS.aonMarginBottom());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		rootPanel.add(tab);
		
		populate();
		paintAdministration(callback,tab);
		paintYear(tab);
		paintPeriod(callback,tab);
		paintDefaultVatRegime(tab);
		paintComplementary(tab);
		paintReplacement(tab);
		paintWithoutActivity(tab);
		paintDiffCalculation(tab);
		paintProrrate(callback,tab);
		
		rootPanel.add(calculateProratePanel);		
		rootPanel.add(getButtonsPanel(callback));
	}

	private void paintDiffCalculation(AonDisplayTable tab) {
		diffCalculation.setText(AON.MSG.diffCalculation());
		diffCalculation.setValue(!model.isDiffCalculationDisabled());
		diffCalculation.addClickHandler(event -> model.setDiffCalculationDisabled(!diffCalculation.getValue()));
		tab.addLabelWidgetRow("", diffCalculation);
	}

	private void paintWithoutActivity(AonDisplayTable tab) {
		// SIN ACTIVIDAD
		withoutActivity.setText(AON.MSG.withoutActivity());
		withoutActivity.setValue(model.isWithoutActivity());
		withoutActivity.addClickHandler(event -> model.setWithoutActivity(withoutActivity.getValue()));
		tab.addLabelWidgetRow("", withoutActivity);
	}

	private void paintReplacement(AonDisplayTable tab) {
		if (model.isReplacementDeclarationAvailable() ) {
			replacement.setText(AON.MSG.replacement());
			replacement.addClickHandler(event -> {
				model.setReplacement(replacement.getValue());
				complementary.setEnabled(!replacement.getValue());
				if (AonEnumUtils.getBoolean(replacement.getValue())) {
					complementary.setValue(false);
				}
			});
			tab.addLabelWidgetRow("", replacement);
		}
		
	}

	private void paintComplementary(AonDisplayTable tab) {
		if (model.isComplementaryDeclarationAvailable()) {
			complementary.setText(AON.MSG.complementary());
			complementary.addClickHandler(event -> {
				model.setComplementary(complementary.getValue());
				replacement.setEnabled(!complementary.getValue());
				if (AonEnumUtils.getBoolean(complementary.getValue())) {
					replacement.setValue(false);
				}
				
			});
			tab.addLabelWidgetRow("", complementary);
		}
	}

	private void paintDefaultVatRegime(AonDisplayTable tab) {
		if (model.isAEAT()) {
			defaultVatRegimeLabel.setText("Destinar Fras. sin actividad a");
			defaultVatRegime.addItem(VATRegime.GENERAL.getName());
			defaultVatRegime.addItem(VATRegime.SIMPLIFIED.getName());
			defaultVatRegime.setSelectedIndex(0);
			defaultVatRegime.addChangeHandler(event -> model.setDefaultVatRegime(defaultVatRegime.getSelectedIndex() == 1? VATRegime.SIMPLIFIED: VATRegime.GENERAL));
			tab.addLabelWidgetRow(defaultVatRegimeLabel, defaultVatRegime);
		}
	}

	private void paintProrrate(Model303Callback callback, AonDisplayTable tab) {
// 		PORCENTAJE DE PRORRATA ANTERIOR
		if (model.hasPreviousProrate() && model.getPeriod().isLastPeriod() ) {
			previousProrate.setEnabled(false);
			tab.addLabelWidgetRow(AON.MSG.prorrataYearPercent(), previousProrate);
		}

		// PORCENTAJE DE PRORRATA
		FlowPanel proratePanel = new FlowPanel(); 
		prorate.addValueChangeHandler(event -> {
			if (prorate.getValue() == null) prorate.setValue(100.0,false); 
			model.ensureDetail(model.getProrateKey()).setAmount(prorate.getValue());
			specialProrate.setVisible(model.hasProrate());
			calculateProratePanel.setVisible(model.hasProrate());
			if (!model.hasProrate()) {
				specialProrate.setValue(false);
				model.setSpecialProrateValue( specialProrate.getValue() );
			}
		});
		
		specialProrate.setStyleName(AON.CSS.aonMarginLeft());  
		specialProrate.addClickHandler(event -> {
			model.setSpecialProrateValue( specialProrate.getValue() );
			Period p = periodList.getValue();
			boolean diff = diffCalculation.getValue() != null && diffCalculation.getValue();
			if ( p != null && !p.isFirstPeriod() && diff) {
				String msg = "Si modifica el tipo de prorrata con el c\u00E1lculo por diferencia activo, revise los valores resultantes en IVA deducible.";
				AonMessageDialog.show("AVISO", msg);
			}
		});
		
		
		proratePanel.add(prorate);
		proratePanel.add(specialProrate);
		
		if ((model.hasProrate() || model.hasPreviousProrate()) && model.getPeriod().isLastPeriod() ) {
			paintCalculateProratePanel( callback);
		} else {
			calculateProratePanel.clear();	
		}
		
		tab.addLabelWidgetRow( model.getPeriod().isLastPeriod()
				?AON.MSG.prorrataFinalPercent()
				:AON.MSG.prorrataPercent(), proratePanel);
	}

	private void paintCalculateProratePanel(Model303Callback callback) {
		calculateProratePanel.clear();
		calculateProratePanel.setStyleName(AON.CSS.aonMargin());
		calculateProratePanel.addStyleName(AON.CSS.aonBlockCenter());
		calculateProratePanel.addStyleName(AON.CSS.aonBackgroundLigthGray());
		calculateProratePanel.addStyleName(AON.CSS.aonBorder());
		
		Label proLabel = new Label("Datos utilizados para c\u00E1lculo de la prorrata definitiva");
		proLabel.setStyleName(AON.CSS.aonBold());
		proLabel.addStyleName(AON.CSS.aonTextUnderline());
		proLabel.addStyleName(AON.CSS.aonMarginTop());
		proLabel.addStyleName(AON.CSS.aonMarginBottom());
		calculateProratePanel.add(proLabel);
		
		AonDisplayTable proTab = new AonDisplayTable();
		proTab.addStyleName(AON.CSS.aonWidthAll());
		fillRow(callback,proTab.addRow(),Mod303Key.CM_070);
		fillRow(callback,proTab.addRow(),Mod303Key.CM_071);
		calculateProratePanel.add(proTab);
	}
	
	private void fillRow(Model303Callback callback, AonDisplayTableRow row, Mod303Key key) {
		row.addCell(getLabel(key))
			.addCell(getDoubleBox(callback,key));
	}

	private Label getLabel(Mod303Key key) {
		return new Label(key.getDescription());
	}
	
	
	private AonDoubleBox getDoubleBox(Model303Callback callback,Mod303Key key) {
		AonDoubleBox box = new AonDoubleBox();
		box.setValue(model.ensureDetail(key).getAmount());
		box.addValueChangeHandler( event -> {
			if (box.getValue() == null) box.setValue(0.0,false);
			model.ensureDetail(key).setAmount(box.getValue());	
			calculateProrrate(callback);
		});
		return box;
	}
	
	private void calculateProrrate(Model303Callback callback) {
		Model303.service.calculateProrrate(callback.getOptions().getOccam(), model, 
			new AsyncCallback<Mod303>() {
			
			@Override
			public void onSuccess(Mod303 result) {
				model = result;
				populate();
				paintCalculateProratePanel(callback); 
			}
			
			@Override
			public void onFailure(Throwable caught) {
				callback.showError(caught.getMessage());
			}
		});
	}

	private void paintPeriod(Model303Callback callback, AonDisplayTable tab) {
		periodList.addChangeHandler( event -> {
			model.setPeriod( periodList.getValue());
			paint(callback);
		});
		tab.addLabelWidgetRow(AON.MSG.period(), periodList);
	}

	private void paintYear(AonDisplayTable tab) {
		yearBox.setMaxLength(4);
		yearBox.setVisibleLength(4);
		yearBox.addValueChangeHandler(event -> model.setYear(yearBox.getValue()));
		tab.addLabelWidgetRow(AON.MSG.year(), yearBox);
	}

	private void paintAdministration(Model303Callback callback, AonDisplayTable tab) {
		admonList.addChangeHandler( event -> {
			model.setAdministration( admonList.getValue() );
			Model303.service.declarationChanged(callback.getOptions().getOccam(),model,
					new AsyncCallback<Mod303>() {
						@Override
						public void onSuccess(Mod303 result) {
							model = result;
//							mod303.setPeriod(result.getPeriod());
//							mod303.ensureDetail(result.getProrateKey()).setAmount(result.getProratePercent());
//							mod303.ensureDetail(result.getProrateTypeKey()).setDescription(result.getSpecialProrateValue());
//							mod303.ensureDetail(result.getPreviousProrateKey()).setAmount(result.getPreviousProratePercent());
							paint(callback);
						}
		
						@Override
						public void onFailure(Throwable caught) {
							callback.showError(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
						}
					});
		});
		tab.addRow()
			.addCell(new Label(AON.MSG.administration()), AON.CSS.aonTableLabel(),AON.CSS.aonWidth150())
			.addCell(admonList, AON.CSS.aonTableLabel(),AON.CSS.aonWidthAuto());

	}

	private FlowPanel getButtonsPanel(final Model303Callback callback) {
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.CSS.aonPadding());
		buttonsPanel.addStyleName(AON.CSS.aonMarginTop());
		buttonsPanel.addStyleName(AON.CSS.aonTextCenter());
		Button acceptButton = new Button();
		acceptButton.setStyleName(AON.CSS.aonOkButton());
		acceptButton.setText( AON.MSG.accept());
		
		acceptButton.addClickHandler(event -> {
			hide();
			callback.onAccept(model);
		});
		buttonsPanel.add(acceptButton);
		Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
		cancelButton.addClickHandler(event -> {
			hide();
			callback.onCancel(model);
		});
		buttonsPanel.add(cancelButton);
		return buttonsPanel;
	}

	private void populate() {
		admonList.setSelectedIndex( model.getAdministration().ordinal());
		yearBox.setValue(model.getYear());
		periodList.setValue(model.getPeriod());
		complementary.setValue(model.isComplementary());
		replacement.setValue(model.isReplacement());
		
		previousProrate.setValue(model.ensureDetail(model.getPreviousProrateKey()).getAmount(),false,false);
		specialProrate.setValue(model.isSpecialProrate());
		specialProrate.setVisible(model.hasProrate());
		prorate.setValue(model.ensureDetail(model.getProrateKey()).getAmount(),false,true);
	}
}
