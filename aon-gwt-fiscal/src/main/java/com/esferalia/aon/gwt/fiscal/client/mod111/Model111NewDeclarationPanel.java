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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class Model111NewDeclarationPanel extends DockLayoutPanel {
	
	private final AdministrationListBox admonList = new AdministrationListBox();
	private final AonIntegerBox yearBox = new AonIntegerBox();	
	private PeriodListBox periodList = new PeriodListBox(true);
	private final CheckBox replacement = new CheckBox();
	private final CheckBox complementary = new CheckBox();
	private final CheckBox generateFromYearStart = new CheckBox();
		
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
		
		registerHandlers(model,callback);
		paint(model,callback);
	}
	
	private void registerHandlers(Mod111 model, Model111Callback callback) {
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
		
		complementary.addClickHandler(event -> {
			model.setComplementary(complementary.getValue());
			replacement.setEnabled(!complementary.getValue());
			if (AonEnumUtils.getBoolean(complementary.getValue())) {
				replacement.setValue(false);
			}
			
		});

		replacement.addClickHandler(event -> {
			model.setReplacement(replacement.getValue());
			complementary.setEnabled(!replacement.getValue());
			if (AonEnumUtils.getBoolean(replacement.getValue())) {
				complementary.setValue(false);
			}
		});

		generateFromYearStart.addClickHandler(event -> model.setGenerateFromYearStart(generateFromYearStart.getValue()));
	}
		
	private void paint(Mod111 model, Model111Callback callback) {
		headerPanel.setWidget(new AonFiscalModelHeader(model));

		rootPanel.clear();
		
		paintMessages(model,callback);
		
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonMarginTop());
		tab.addStyleName(AON.CSS.aonMarginBottom());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		rootPanel.add(tab);
		
		populate(model);
		paintAdministration(model,callback,tab);
		paintYear(model,callback,tab);
		paintPeriod(model,callback,tab);
		paintComplementary(model,tab);
		paintReplacement(model,tab);
		paintGenerateFromYearStart(model,tab);
		rootPanel.add(getButtonsPanel(model,callback));
	}
	
	private void initialize(Mod111 model, Model111Callback callback) {
		Model111.SERVICE.initialize(callback.getOptions().getOccam(),model,
			new AsyncCallback<Mod111>() {
				@Override
				public void onSuccess(Mod111 m111) {
					paint(m111,callback);
				}
	
				@Override
				public void onFailure(Throwable caught) {
					callback.showError(AON.MSG.unableToInitializeDeclaration(caught.getMessage()));
				}
			}
		);
	}

	private void populate(Mod111 model) {
		admonList.setSelectedIndex( model.getAdministration().ordinal());
		yearBox.setValue(model.getYear());
		periodList.setValue(model.getPeriod());
		complementary.setValue(model.isComplementary());
		replacement.setValue(model.isReplacement());
		generateFromYearStart.setValue(model.isGenerateFromYearStart());
	}
	private void paintMessages(Mod111 model, Model111Callback callback) {
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
	
	private void paintAdministration(Mod111 model, Model111Callback callback, AonDisplayTable tab) {
		tab.addRow()
			.addCell(new Label(AON.MSG.administration()),AON.CSS.aonTableLabel(),AON.CSS.aonWidth120() )
			.addCell(admonList,AON.CSS.aonWidth400());
	}

	private void paintYear(Mod111 model, Model111Callback callback, AonDisplayTable tab) {
		tab.addRow()
			.addCell(new Label(AON.MSG.year()),AON.CSS.aonTableLabel())
			.addCell(yearBox);
		
	}

	private void paintPeriod(Mod111 model, Model111Callback callback, AonDisplayTable tab) {
		tab.addRow()
			.addCell(new Label(AON.MSG.period()),AON.CSS.aonTableLabel())
			.addCell(periodList);
	}
	
	
	private void paintComplementary(Mod111 model, AonDisplayTable tab) {
		if (model.isComplementaryDeclarationAvailable()) {
			complementary.setText(AON.MSG.complementary());
			tab.addRow()
				.addCell(new Label(),AON.CSS.aonTableLabel())
				.addCell(complementary);
		}
	}

	private void paintReplacement(Mod111 model, AonDisplayTable tab) {
		if (model.isReplacementDeclarationAvailable() ) {
			replacement.setText(AON.MSG.replacement());
			tab.addRow()
				.addCell(new Label(),AON.CSS.aonTableLabel())
				.addCell(replacement);
		}
		
	}

	private void paintGenerateFromYearStart(Mod111 model, AonDisplayTable tab) {
		if (model.isGenerateFromYearStartAvailable() ) {
			generateFromYearStart.setText(AON.MSG.generateFromYearStart( model.getYear() ));
			tab.addRow()
				.addCell(new Label(),AON.CSS.aonTableLabel())
				.addCell(generateFromYearStart,AON.CSS.aonWidth400());
		}
	}

	private FlowPanel getButtonsPanel(Mod111 model, final Model111Callback callback) {
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.CSS.aonPadding());
		buttonsPanel.addStyleName(AON.CSS.aonMarginTop());
		buttonsPanel.addStyleName(AON.CSS.aonTextCenter());
		Button acceptButton = new Button();
		acceptButton.setStyleName(AON.CSS.aonOkButton());
		acceptButton.setText( AON.MSG.accept());
		
		acceptButton.addClickHandler(event -> {
			acceptButton.setEnabled(false);
			callback.onAccept(model);
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
}
