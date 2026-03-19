package com.esferalia.aon.gwt.fiscal.client.mod202;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AdministrationListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.mod202.Model202.Model202Callback;
import com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelHeader;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
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

public class Model202NewDeclarationPanel extends DockLayoutPanel {
	
	private AdministrationListBox admonList;
	private AonIntegerBox yearBox;	
	private PeriodListBox periodList;
	private CheckBox replacement;
	private CheckBox complementary;
	private ListBox r21Box;

	private class PeriodListBox extends ListBox {

		private PeriodListBox() {
			setWidth("90px");
			addItem( "1\u00BA Periodo.", Period.T1.name() );
			addItem( "2\u00BA Periodo.", Period.T2.name() );
			addItem( "3\u00BA Periodo.", Period.T3.name() );
		}

		public Period getValue() {
			return Period.valueOf( getSelectedValue() );
		}

		public void setValue(Period period) {
			if (period == Period.T3) setSelectedIndex(2);
			else if (period == Period.T2) setSelectedIndex(1);
			else setSelectedIndex(0);
		}
	}

	private FlowPanel rootPanel;
	private SimpleLayoutPanel headerPanel = new SimpleLayoutPanel();
	
	public Model202NewDeclarationPanel(Model202Callback cbk) {
		super( Unit.PX );
		
		addNorth(headerPanel, AonFiscalModelHeader.HEIGTH);

		AonToolbar toolbar = new AonToolbar(AON.MSG.newDeclaration());
		AonToolbarButton cancelButton = new AonToolbarButton(AON.MSG.backAction(),AON.CSS.aonIconBack());
		cancelButton.addClickHandler(event ->  cbk.onCancel( cbk.getModel() ) );
		toolbar.add(cancelButton);
		addNorth(toolbar, AonToolbar.HEIGTH);
		
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.setStyleName(AON.CSS.aonScrollArea());
		add(scrollPanel);
		
		rootPanel = new FlowPanel(); 
		rootPanel.setStyleName(AON.CSS.aonWidthAll());
		scrollPanel.setWidget(rootPanel);
		
		paint(cbk);
	}
	
	private void paint(Model202Callback cbk) {
		cbk.hideError();

		registerHandlers(cbk);
		
		headerPanel.setWidget(new AonFiscalModelHeader(cbk.getModel()));

		rootPanel.clear();
		
		paintMessages(cbk);
		
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonMarginTop());
		tab.addStyleName(AON.CSS.aonMarginBottom());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		rootPanel.add(tab);
		
		populate(cbk.getModel());
		paintAdministration(cbk,tab);
		paintYear(cbk,tab);
		paintPeriod(cbk,tab);
		paintComplementary(cbk,tab);
		paintReplacement(cbk,tab);
		paintR21(cbk,tab);
		rootPanel.add(getButtonsPanel(cbk));
	}

	private void registerHandlers(Model202Callback cbk) {
		admonList = new AdministrationListBox();
		yearBox = new AonIntegerBox();	
		periodList = new PeriodListBox();
		replacement = new CheckBox();
		complementary = new CheckBox();
		r21Box = new ListBox();
		r21Box.setWidth("350px");
		r21Box.addItem(AON.MSG.calculation0(), "0");
		r21Box.addItem(AON.MSG.calculation1(), "1");
		r21Box.addItem(AON.MSG.calculation2(), "2");
		r21Box.addChangeHandler( event -> cbk.getModel().putAmount(Mod202Key.X00, r21Box.getSelectedIndex()));

		admonList.setEnabled(false);

		yearBox.setMaxLength(4);
		yearBox.setVisibleLength(4);
		yearBox.addValueChangeHandler(event -> {
			cbk.getModel().setYear(yearBox.getValue());
			initialize( cbk );
		});

		periodList.addChangeHandler( event -> {
			cbk.getModel().setPeriod( periodList.getValue());
			initialize( cbk );
		});

//		complementary.setText(AON.MSG.complementary());
		complementary.addClickHandler(event -> {
			cbk.getModel().setComplementary(complementary.getValue());
			replacement.setEnabled(!complementary.getValue());
			if (AonEnumUtils.getBoolean(complementary.getValue())) {
				replacement.setValue(false);
				cbk.getModel().setReplacement(false);
			}
			
		});

//		replacement.setText(AON.MSG.replacement());
		replacement.addClickHandler(event -> {
			cbk.getModel().setReplacement(replacement.getValue());
			complementary.setEnabled(!replacement.getValue());
			if (AonEnumUtils.getBoolean(replacement.getValue())) {
				complementary.setValue(false);
				cbk.getModel().setComplementary(false);
			}
		});

	}
	
	private void paintMessages(Model202Callback cbk) {
		if (cbk.getModel().getMessages() != null && !cbk.getModel().getMessages().isEmpty()) {
			FlowPanel messages = new FlowPanel();
			messages.setStyleName( AON.CSS.aonTextCenter() );
			messages.addStyleName( AON.CSS.aonMarginBottom() );
			messages.addStyleName( AON.CSS.aonBorder());
			messages.addStyleName( AON.CSS.aonPadding());
			messages.addStyleName( AON.CSS.aonBackgroundHighlightedOrange());
			for (String msg : cbk.getModel().getMessages()) {
				Label message = new Label(msg);
				message.setStyleName(AON.CSS.aonLabelWithIcon());
				message.addStyleName(AON.CSS.aonIconWarning());
				message.addStyleName(AON.CSS.aonBold());
				messages.add(message);
			}
			rootPanel.add(messages);
		}
	}
	
	private void populate(Mod202 model) {
		admonList.setSelectedIndex( model.getAdministration().ordinal());
		yearBox.setValue(model.getYear());
		periodList.setValue( model.getPeriod() );
		complementary.setValue(model.isComplementary());
		replacement.setValue(model.isReplacement());
		int r21 = AonNumberUtils.toint(model.getAmount(Mod202Key.X00));
		r21Box.setSelectedIndex((r21 < 0 || r21 > 2)?0:r21);
	}
	
	private void paintAdministration(Model202Callback callback, AonDisplayTable tab) {
		tab.addRow()
			.addCell(new Label(AON.MSG.administration()),AON.CSS.aonTableLabel(),AON.CSS.aonWidth120() )
			.addCell(admonList,AON.CSS.aonWidth400());
	}
	
	private void paintYear(Model202Callback callback, AonDisplayTable tab) {
		tab.addRow()
			.addCell(new Label(AON.MSG.year()),AON.CSS.aonTableLabel())
			.addCell(yearBox);
	}

	private void paintPeriod(Model202Callback callback, AonDisplayTable tab) {
		tab.addRow()
			.addCell(new Label(AON.MSG.period()),AON.CSS.aonTableLabel())
			.addCell(periodList);
	}
	
	private void paintComplementary(Model202Callback callback, AonDisplayTable tab) {
		if (callback.getModel().isComplementaryDeclarationAvailable()) {
			tab.addRow()
				.addCell(new Label(AON.MSG.complementary()),AON.CSS.aonTableLabel())
				.addCell(complementary);
		}
	}

	private void paintReplacement(Model202Callback callback, AonDisplayTable tab) {
		if (callback.getModel().isReplacementDeclarationAvailable() ) {
			tab.addRow()
				.addCell(new Label(AON.MSG.replacement()),AON.CSS.aonTableLabel())
				.addCell(replacement);
		}
		
	}
	
	private void paintR21(Model202Callback callback, AonDisplayTable tab) {
		tab.addRow()
			.addCell(new Label(Mod202Key.X00.getDescription()),AON.CSS.aonTableLabel(),AON.CSS.aonWidth120() )
			.addCell(r21Box,AON.CSS.aonWidth400());
	}
	
	private FlowPanel getButtonsPanel(final Model202Callback callback) {
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.CSS.aonPadding());
		buttonsPanel.addStyleName(AON.CSS.aonMarginTop());
		buttonsPanel.addStyleName(AON.CSS.aonTextCenter());
		Button acceptButton = new Button();
		acceptButton.setStyleName(AON.CSS.aonOkButton());
		acceptButton.setText( AON.MSG.accept());
		
		acceptButton.addClickHandler(event -> {
			acceptButton.setEnabled(false);
			callback.onAccept(callback.getModel());
		});
		buttonsPanel.add(acceptButton);
		Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
		cancelButton.addClickHandler(event -> callback.onCancel(callback.getModel()));
		buttonsPanel.add(cancelButton);
		return buttonsPanel;
	}

	private void initialize(Model202Callback callback) {
		Model202.SERVICE.initialize(callback.getOptions().getOccam(),callback.getModel(),
			new AsyncCallback<Mod202>() {
				@Override
				public void onSuccess(Mod202 m202) {
					callback.setModel(m202);
					paint(callback);
				}
	
				@Override
				public void onFailure(Throwable caught) {
					callback.showError(AON.MSG.unableToInitializeDeclaration(caught.getMessage()));
				}
			}
		);
	}
}
