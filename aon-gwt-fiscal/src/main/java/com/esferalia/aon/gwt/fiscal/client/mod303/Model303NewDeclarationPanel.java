package com.esferalia.aon.gwt.fiscal.client.mod303;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AdministrationListBox;
import com.esferalia.aon.gwt.common.client.widget.PeriodListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable.AonDisplayTableRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303.Model303Callback;
import com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelHeader;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.Period;
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

	private void paint(Mod303 model, Model303Callback callback) {
		
		headerPanel.setWidget(new AonFiscalModelHeader(model));

		rootPanel.clear();
		
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonMarginTop());
		tab.addStyleName(AON.CSS.aonMarginBottom());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		rootPanel.add(tab);
		
		populate(model);
		paintAdministration(model,callback,tab);
		paintYear(model,tab);
		paintPeriod(model,callback,tab);
		paintDefaultVatRegime(model,tab);
		paintComplementary(model,tab);
		paintReplacement(model,tab);
		paintWithoutActivity(model,tab);
		paintDiffCalculation(model,tab);
		paintProrrate(model,callback,tab);
		
		rootPanel.add(calculateProratePanel);		
		rootPanel.add(getButtonsPanel(model,callback));
	}
	
	private void paintAdministration(Mod303 model, Model303Callback callback, AonDisplayTable tab) {
		admonList.addChangeHandler( event -> {
			model.setAdministration( admonList.getValue() );
			Model303.service.declarationChanged(callback.getOptions().getOccam(),model,
					new AsyncCallback<Mod303>() {
						@Override
						public void onSuccess(Mod303 result) {
							paint(result,callback);
						}
		
						@Override
						public void onFailure(Throwable caught) {
							callback.showError(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
						}
					});
		});
		tab.addLabelWidgetRow(AON.MSG.administration(), admonList);
	}
	
	private void paintYear(Mod303 model, AonDisplayTable tab) {
		yearBox.setMaxLength(4);
		yearBox.setVisibleLength(4);
		yearBox.addValueChangeHandler(event -> model.setYear(yearBox.getValue()));
		tab.addLabelWidgetRow(AON.MSG.year(), yearBox);
	}

	private void paintPeriod(Mod303 model, Model303Callback callback, AonDisplayTable tab) {
		periodList.addChangeHandler( event -> {
			model.setPeriod( periodList.getValue());
			paint(model,callback);
		});
		tab.addLabelWidgetRow(AON.MSG.period(), periodList);
	}

	private void paintDefaultVatRegime(Mod303 model, AonDisplayTable tab) {
		if (model.isAEAT()) {
			defaultVatRegimeLabel.setText("Destinar Fras. sin actividad a");
			defaultVatRegime.addItem(VATRegime.GENERAL.getName());
			defaultVatRegime.addItem(VATRegime.SIMPLIFIED.getName());
			defaultVatRegime.setSelectedIndex(0);
			defaultVatRegime.addChangeHandler(event -> model.setDefaultVatRegime(defaultVatRegime.getSelectedIndex() == 1? VATRegime.SIMPLIFIED: VATRegime.GENERAL));
			tab.addLabelWidgetRow(defaultVatRegimeLabel, defaultVatRegime);
		}
	}

	private void paintComplementary(Mod303 model, AonDisplayTable tab) {
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

	private void paintReplacement(Mod303 model, AonDisplayTable tab) {
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

	private void paintWithoutActivity(Mod303 model, AonDisplayTable tab) {
		// SIN ACTIVIDAD
		withoutActivity.setText(AON.MSG.withoutActivity());
		withoutActivity.setValue(model.isWithoutActivity());
		withoutActivity.addClickHandler(event -> model.setWithoutActivity(withoutActivity.getValue()));
		tab.addLabelWidgetRow("", withoutActivity);
	}

	private void paintDiffCalculation(Mod303 model, AonDisplayTable tab) {
		diffCalculation.setText(AON.MSG.diffCalculation());
		diffCalculation.setValue(!model.isDiffCalculationDisabled());
		diffCalculation.addClickHandler(event -> model.setDiffCalculationDisabled(!diffCalculation.getValue()));
		tab.addLabelWidgetRow("", diffCalculation);
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




	private FlowPanel getButtonsPanel(Mod303 model, final Model303Callback callback) {
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.CSS.aonPadding());
		buttonsPanel.addStyleName(AON.CSS.aonMarginTop());
		buttonsPanel.addStyleName(AON.CSS.aonTextCenter());
		Button acceptButton = new Button();
		acceptButton.setStyleName(AON.CSS.aonOkButton());
		acceptButton.setText( AON.MSG.accept());
		
		acceptButton.addClickHandler(event -> callback.onAccept(model));
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
		
		previousProrate.setValue(model.ensureDetail(model.getPreviousProrateKey()).getAmount(),false,false);
		specialProrate.setValue(model.isSpecialProrate());
		specialProrate.setVisible(model.hasProrate());
		prorate.setValue(model.ensureDetail(model.getProrateKey()).getAmount(),false,true);
	}
}
