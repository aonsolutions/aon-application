package com.esferalia.aon.gwt.fiscal.client.mod349;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AdministrationListBox;
import com.esferalia.aon.gwt.common.client.widget.PeriodListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.mod349.Model349.Model349Callback;
import com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelHeader;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.type.Period;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model349NewDeclarationPopup extends DockLayoutPanel {
	
	private FlowPanel rootPanel;
	private SimpleLayoutPanel headerPanel = new SimpleLayoutPanel();

	private AdministrationListBox admonList = new AdministrationListBox();
	private AonIntegerBox yearBox = new AonIntegerBox();
	private PeriodListBox periodList = new PeriodListBox();
	private CheckBox complementary = new CheckBox(AON.MSG.complementary());
	private CheckBox replacement = new CheckBox(AON.MSG.replacement());
	private AonTextBox replacedReceiptBox = new AonTextBox();
	private CheckBox diffCalculation = new CheckBox();
	private AonToolbar toolbar;
	private CheckBox manualDeclaration = new CheckBox();
	
	protected Model349NewDeclarationPopup(final Mod349 model, final Model349Callback callback) {
		super( Unit.PX );
		
		addNorth(headerPanel, AonFiscalModelHeader.HEIGTH);
		
		String title = AON.MSG.newDeclaration();
		toolbar = new AonToolbar(title);
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
		
		// Añadimos a periodList tambien el periodo anual, pues este modelo permite todos los periodos posibles
		periodList.addItem(Period.YEAR.getDescription(), Integer.toString(Period.YEAR.ordinal()));

		paint(model,callback);
	}
	
	protected void setCaption(String caption) {
		toolbar.setTitle(caption);
	}
	
	private void paint(Mod349 model, Model349Callback callback) {
		
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
		paintPeriod(model,tab);
		paintComplementary(model,callback,tab);
		paintReplacement(model,callback,tab);
		paintReplacedReceipt(model,tab);
		paintDiffCalculation(model,tab);
		paintManualDeclaration(model,tab);
		rootPanel.add(getButtonsPanel(model,callback));
	}

	private void populate(Mod349 model) {
		admonList.setSelectedIndex( model.getAdministration().ordinal());
		yearBox.setValue(model.getYear());
		periodList.setValue(model.getPeriod());
		complementary.setValue(model.isComplementary());
		replacement.setValue(model.isReplacement());
		replacedReceiptBox.setValue(model.getReplacedNumber());		
	}
	
	private void paintAdministration(Mod349 model, Model349Callback callback, AonDisplayTable tab) {
		admonList.addChangeHandler( event -> {
			model.setAdministration( admonList.getValue() );
			paint(model,callback);	
		});
		tab.addLabelWidgetRow(AON.MSG.administration(), admonList);
	}
	
	private void paintYear(Mod349 model, AonDisplayTable tab) {
		yearBox.setMaxLength(4);
		yearBox.setVisibleLength(4);
		yearBox.addValueChangeHandler(event -> model.setYear(yearBox.getValue()));
		tab.addLabelWidgetRow(AON.MSG.year(), yearBox);		
	}
	
	private void paintPeriod(Mod349 model, AonDisplayTable tab) {
		periodList.addChangeHandler( event -> model.setPeriod( periodList.getValue() ));
		tab.addLabelWidgetRow(AON.MSG.period(), periodList);
	}

	private void paintComplementary(Mod349 model, Model349Callback callback, AonDisplayTable tab) {
		if (model.isGipuzkoa() || model.isBizkaia()) {
			complementary.setValue(false);
		} else {
			complementary.addValueChangeHandler(event -> {
				model.setComplementary(complementary.getValue());
				if (complementary.getValue().booleanValue()) {
					replacement.setValue(false,false);
				}
				if (!model.isComplementary() && !model.isReplacement()) {
					replacedReceiptBox.setValue(null,true);
				}
				paint(model,callback);				
			});
			tab.addLabelWidgetRow("", complementary);		
		}
	}

	private void paintReplacement(Mod349 model, Model349Callback callback, AonDisplayTable tab) {
		if (model.isGipuzkoa()) {
			replacement.setValue(false);
		} else {
			replacement.addValueChangeHandler(event -> {
				model.setReplacement(replacement.getValue());				
				if (replacement.getValue().booleanValue()) {
					complementary.setValue(false,false);
				}				
				if (!model.isComplementary() && !model.isReplacement()) {
					replacedReceiptBox.setValue(null,true);					
				}
				paint(model,callback);
			});
			tab.addLabelWidgetRow("", replacement);
		}
		
	}

	private void paintReplacedReceipt(Mod349 model, AonDisplayTable tab) {
		if (model.isComplementary() || model.isReplacement()) {
			replacedReceiptBox.setMaxLength(13);
			replacedReceiptBox.setVisibleLength(13);
			replacedReceiptBox.addValueChangeHandler(event -> model.setReplacedNumber(replacedReceiptBox.getValue()));
			tab.addLabelWidgetRow(AON.MSG.previousDeclaration(), replacedReceiptBox);
		}
	}
	
	private void paintDiffCalculation(Mod349 model, AonDisplayTable tab) {
		diffCalculation.setText(AON.MSG.diffCalculation());
		diffCalculation.setValue(model.isDiffEnabled());
		diffCalculation.addClickHandler(event -> model.setDiffEnabled(diffCalculation.getValue()));
		tab.addLabelWidgetRow("", diffCalculation);
	}
	
	private void paintManualDeclaration(Mod349 model, AonDisplayTable tab) {
		manualDeclaration.setText(AON.MSG.manualDeclaration());
		manualDeclaration.setValue(model.isManualDeclaration());
		manualDeclaration.addClickHandler(event -> model.setManualDeclaration(manualDeclaration.getValue()));
		tab.addLabelWidgetRow("", manualDeclaration);
	}

	private Widget getButtonsPanel(Mod349 model, Model349Callback callback) {
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

}
