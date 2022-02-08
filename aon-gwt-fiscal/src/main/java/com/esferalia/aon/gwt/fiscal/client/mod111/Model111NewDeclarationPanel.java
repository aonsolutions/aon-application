package com.esferalia.aon.gwt.fiscal.client.mod111;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AdministrationListBox;
import com.esferalia.aon.gwt.common.client.widget.PeriodListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111.Model111Callback;
import com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelHeader;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class Model111NewDeclarationPanel extends DockLayoutPanel {
	
	private final AdministrationListBox admonList = new AdministrationListBox();
	private final AonIntegerBox yearBox = new AonIntegerBox();	
	private PeriodListBox periodList = new PeriodListBox(true);
	private final CheckBox replacement = new CheckBox();
	private final CheckBox complementary = new CheckBox();
	private final CheckBox diffCalculation = new CheckBox();
		
	private FlowPanel rootPanel;
	private SimpleLayoutPanel headerPanel = new SimpleLayoutPanel();
	
	public Model111NewDeclarationPanel(Mod111 model,Model111Callback callback) {
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
	
		
	private void paint(Mod111 model, Model111Callback callback) {
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
		paintComplementary(model,tab);
		paintReplacement(model,tab);
		paintDiffCalculation(model,tab);
		rootPanel.add(getButtonsPanel(model,callback));
	}
	
	private void populate(Mod111 model) {
		admonList.setSelectedIndex( model.getAdministration().ordinal());
		yearBox.setValue(model.getYear());
		periodList.setValue(model.getPeriod());
		complementary.setValue(model.isComplementary());
		replacement.setValue(model.isReplacement());
	}
	
	private void paintAdministration(Mod111 model, Model111Callback callback, AonDisplayTable tab) {
		admonList.addChangeHandler( event -> {
			model.setAdministration( admonList.getValue() );
			paint(model,callback);
		});
		tab.addLabelWidgetRow(AON.MSG.administration(), admonList);
	}

	private void paintYear(Mod111 model, AonDisplayTable tab) {
		yearBox.setMaxLength(4);
		yearBox.setVisibleLength(4);
		yearBox.addValueChangeHandler(event -> model.setYear(yearBox.getValue()));
		tab.addLabelWidgetRow(AON.MSG.year(), yearBox);
	}

	private void paintPeriod(Mod111 model, Model111Callback callback, AonDisplayTable tab) {
		periodList.addChangeHandler( event -> {
			model.setPeriod( periodList.getValue());
			paint(model,callback);
		});
		tab.addLabelWidgetRow(AON.MSG.period(), periodList);
	}
	
	
	private void paintComplementary(Mod111 model, AonDisplayTable tab) {
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

	private void paintReplacement(Mod111 model, AonDisplayTable tab) {
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

	private void paintDiffCalculation(Mod111 model, AonDisplayTable tab) {
		diffCalculation.setText(AON.MSG.diffCalculation());
		diffCalculation.setValue(!model.isDiffCalculationDisabled());
		diffCalculation.addClickHandler(event -> model.setDiffCalculationDisabled(!diffCalculation.getValue()));
		tab.addLabelWidgetRow("", diffCalculation);
	}

	private FlowPanel getButtonsPanel(Mod111 model, final Model111Callback callback) {
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
