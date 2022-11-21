package com.esferalia.aon.gwt.fiscal.client.mod390hf;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AdministrationListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.mod390hf.Model390HF.Model390HFCallback;
import com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelHeader;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class Model390HFNewDeclarationPanel extends DockLayoutPanel {
	
	private AdministrationListBox admonList = new AdministrationListBox(true);
	private AonIntegerBox yearBox = new AonIntegerBox();
	private CheckBox replacement = new CheckBox();
	private CheckBox complementary = new CheckBox();
	private CheckBox withoutActivity = new CheckBox();
	private AonDoubleBox prorate = new AonDoubleBox(7);
	private CheckBox specialProrate = new CheckBox("Especial");
	private CheckBox manualDeclaration = new CheckBox();
	private boolean running;
	
	private FlowPanel rootPanel;
	private FlowPanel calculateProratePanel = new FlowPanel();	
	
	private SimpleLayoutPanel headerPanel = new SimpleLayoutPanel();
	
	public Model390HFNewDeclarationPanel(final Mod390HF model, final Model390HFCallback callback) {
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
	
	private void registerHandlers(Mod390HF model, Model390HFCallback callback) {
		admonList.addChangeHandler( event -> {
			model.setAdministration( admonList.getValue() );
			initialize(model, callback );
		});
		
		yearBox.setMaxLength(4);
		yearBox.setVisibleLength(4);
		yearBox.addValueChangeHandler(event -> model.setYear(yearBox.getValue()));
		
		complementary.setText(AON.MSG.complementary());
		complementary.addClickHandler(event -> {
			model.setComplementary(complementary.getValue());
			replacement.setEnabled(!complementary.getValue());
			if (complementary.getValue().booleanValue()) {
				replacement.setValue(false);
			}
		});
		
		replacement.setText(AON.MSG.replacement());
		replacement.addClickHandler(event -> {
			model.setReplacement(replacement.getValue());
			complementary.setEnabled(!replacement.getValue());
			if (replacement.getValue().booleanValue()) {
				complementary.setValue(false);
			}
		});
		
		withoutActivity.setText(AON.MSG.withoutActivity());
		withoutActivity.addClickHandler(event -> model.setWithoutActivity(withoutActivity.getValue()));
		
		manualDeclaration.setText(AON.MSG.manualDeclaration());
		manualDeclaration.addClickHandler(event -> {
			model.setManualDeclaration(manualDeclaration.getValue());
			if ( model.isManualDeclaration()) {
				model.setProratePercent(0);
//				model.setSpecialProrateValue(false);
			}
			initialize(model, callback );
		});
		
		prorate.addValueChangeHandler(event -> model.setProratePercent(prorate.getValue()));
		
		
		prorate.addValueChangeHandler(event -> {
			if (prorate.getValue() == null) prorate.setValue(0.0,false); 
			model.ensureDetail(model.getProrateKey()).setAmount(prorate.getValue());
			specialProrate.setVisible(model.hasProrate());
			calculateProratePanel.setVisible(model.hasProrate());
			if (!model.hasProrate()) {
				specialProrate.setValue(false);
				model.setSpecialProrateValue( specialProrate.getValue() );
			}
		});

		specialProrate.addClickHandler(event -> model.setSpecialProrateValue( specialProrate.getValue() ));
		
	}

		
	
	private void paint(Mod390HF model, Model390HFCallback callback) {

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
		paintComplementary(model,callback,tab);
		paintReplacement(model,callback,tab);
		paintWithoutActivity(model,callback,tab);
		paintManualDeclaration(model,callback,tab);
		paintProrrate(model,callback,tab);
		
		rootPanel.add(getButtonsPanel(model,callback));
	}
	
	private FlowPanel getButtonsPanel(Mod390HF model, final Model390HFCallback callback) {
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
	

	private void paintAdministration(Mod390HF model, Model390HFCallback callback, AonDisplayTable tab) {
		tab.addLabelWidgetRow(AON.MSG.administration(), admonList);
	}
	
	private void paintYear(Mod390HF model, Model390HFCallback callback, AonDisplayTable tab) {
		tab.addLabelWidgetRow(AON.MSG.year(), yearBox);		
	}
	private void paintComplementary(Mod390HF model, Model390HFCallback callback, AonDisplayTable tab) {
		if (model.isComplementaryDeclarationAvailable()) {
			tab.addLabelWidgetRow("", complementary);
		}
	}
	private void paintReplacement(Mod390HF model, Model390HFCallback callback, AonDisplayTable tab) {
		if (model.isReplacementDeclarationAvailable() ) {
			tab.addLabelWidgetRow("", replacement);
		}
	}
	private void paintWithoutActivity(Mod390HF model, Model390HFCallback callback, AonDisplayTable tab) {
		withoutActivity.setValue(model.isWithoutActivity());
		tab.addLabelWidgetRow("", withoutActivity);
	}
	private void paintProrrate(Mod390HF model, Model390HFCallback callback, AonDisplayTable tab) {
		FlowPanel proratePanel = new FlowPanel();
		proratePanel.setStyleName(AON.CSS.aonFlexBlock());
		specialProrate.setStyleName(AON.CSS.aonMarginLeft());  
		proratePanel.add(prorate);
		proratePanel.add(specialProrate);
		tab.addLabelWidgetRow( model.getPeriod().isLastPeriod()
				?AON.MSG.prorrataFinalPercent()
				:AON.MSG.prorrataPercent(), proratePanel);
	}
	
	private void paintManualDeclaration(Mod390HF model, Model390HFCallback callback, AonDisplayTable tab) {
		manualDeclaration.setText(AON.MSG.manualDeclaration());
		manualDeclaration.addClickHandler(event -> {
			model.setManualDeclaration(manualDeclaration.getValue());
			if ( model.isManualDeclaration()) {
				model.setProratePercent(0);
				model.setSpecialProrateValue(false);
			}
			initialize(model, callback );
		});
	}

	private void populate(Mod390HF model) {
		admonList.setSelectedIndex( model.getAdministration().ordinal());
		yearBox.setValue(model.getYear());
		prorate.setValue(model.ensureDetail(model.getProrateKey()).getAmount());
		complementary.setValue(model.isComplementary());
		replacement.setValue(model.isReplacement());
		withoutActivity.setValue(model.isWithoutActivity());
	}
	
	
	private void initialize(Mod390HF model, Model390HFCallback callback) {
		running = true;
		Model390HF.MOD_SERVICE.initialize(callback.getOptions().getOccam(),model, new AsyncCallback<Mod390HF>() {
				@Override
				public void onSuccess(Mod390HF m390) {
					paint(m390,callback);
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
	
	private void paintMessages(Mod390HF model) {
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
}
