package com.esferalia.aon.gwt.fiscal.client.mod347;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AdministrationListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.fiscal.client.mod347.Model347.Model347Callback;
import com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelHeader;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model347NewDeclarationPopup extends DockLayoutPanel {

	private FlowPanel rootPanel;
	private SimpleLayoutPanel headerPanel = new SimpleLayoutPanel();

	private AdministrationListBox admonList = new AdministrationListBox();
	private AonIntegerBox yearBox = new AonIntegerBox();
	private CheckBox complementary = new CheckBox(AON.MSG.complementary());
	private CheckBox replacement = new CheckBox(AON.MSG.replacement());
	private AonTextBox replacedReceiptBox = new AonTextBox();
	private CheckBox excludeOutputNationalZero = new CheckBox("Excluir bases de facturas EMITIDAS con IVA al 0%.");
	private CheckBox excludeInputNationalZero = new CheckBox("Excluir bases de facturas RECIBIDAS con IVA al 0%.");
	private CheckBox excludeRetention = new CheckBox("Excluir facturas con retenci\u00F3n.");
	private CheckBox excludeIntracommunity = new CheckBox("Excluir facturas intracomunitarias.");
	private AonToolbar toolbar;

	public Model347NewDeclarationPopup(final Mod347 model, final Model347Callback callback) {
		super(Unit.PX);

		addNorth(headerPanel, AonFiscalModelHeader.HEIGTH);

		String title = AON.MSG.newDeclaration();
		toolbar = new AonToolbar(title);
		addNorth(toolbar, AonToolbar.HEIGTH);

		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.setStyleName(AON.CSS.aonScrollArea());
		add(scrollPanel);

		rootPanel = new FlowPanel();
		rootPanel.setStyleName(AON.CSS.aonWidthAll());
		scrollPanel.setWidget(rootPanel);

		paint(model, callback);
	}

	protected void setCaption(String caption) {
		toolbar.setTitle(caption);
	}

	private void paint(Mod347 model, Model347Callback callback) {

		headerPanel.setWidget(new AonFiscalModelHeader(model));

		rootPanel.clear();

		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonMarginTop());
		tab.addStyleName(AON.CSS.aonMarginBottom());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		rootPanel.add(tab);

		populate(model);
		paintAdministration(model, callback, tab);
		paintYear(model, tab);
		paintComplementary(model, callback, tab);
		paintReplacement(model, callback, tab);
		paintReplacedReceipt(model, tab);
		paintChecksLabel(tab);
		paintExcludeOutputNationalZero(model, tab);
		paintExcludeInputNationalZero(model, tab);
		paintExcludeRetention(model, tab);
		paintExcludeIntracommunity(model, tab);

		rootPanel.add(getButtonsPanel(model, callback));
	}

	private void populate(Mod347 model) {
		admonList.setSelectedIndex(model.getAdministration().ordinal());
		yearBox.setValue(model.getYear());
		complementary.setValue(model.isComplementary());
		replacement.setValue(model.isReplacement());
		replacedReceiptBox.setValue(model.getReplacedNumber());
		excludeOutputNationalZero.setValue(model.isExcludeOutputNationalZero());
		excludeInputNationalZero.setValue(model.isExcludeInputNationalZero());
		excludeRetention.setValue(model.isExcludeRetention());
		excludeIntracommunity.setValue(model.isExcludeIntracommunity());
	}

	private void paintAdministration(Mod347 model, Model347Callback callback, AonDisplayTable tab) {
		admonList.addChangeHandler(event -> {
			model.setAdministration(admonList.getValue());
			paint(model, callback);
		});
		tab.addLabelWidgetRow(AON.MSG.administration(), admonList);
	}

	private void paintYear(Mod347 model, AonDisplayTable tab) {
		yearBox.setMaxLength(4);
		yearBox.setVisibleLength(4);
		yearBox.addValueChangeHandler(event -> model.setYear(yearBox.getValue()));
		tab.addLabelWidgetRow(AON.MSG.year(), yearBox);
	}

	private void paintComplementary(Mod347 model, Model347Callback callback, AonDisplayTable tab) {
		if (model.isGipuzkoa() || model.isBizkaia()) {
			complementary.setValue(false);
		} else {
			complementary.addValueChangeHandler(event -> {
				model.setComplementary(complementary.getValue());
				if (model.isComplementary()) {
					replacement.setValue(false, true);
				}
				replacedReceiptBox.setEnabled(complementary.getValue() || replacement.getValue());
				if (!model.isComplementary() && !model.isReplacement()) {
					replacedReceiptBox.setValue(null, true);
				}
				paint(model, callback);
			});
			tab.addLabelWidgetRow("", complementary);
		}
	}

	private void paintReplacement(Mod347 model, Model347Callback callback, AonDisplayTable tab) {
		if (model.isGipuzkoa()) {
			replacement.setValue(false);
		} else {
			replacement.addValueChangeHandler(event -> {
				model.setReplacement(replacement.getValue());
				if (model.isReplacement()) {
					complementary.setValue(false, true);
				}
				replacedReceiptBox.setEnabled(complementary.getValue() || replacement.getValue());
				if (!model.isComplementary() && !model.isReplacement()) {
					replacedReceiptBox.setValue(null, true);
				}
				paint(model, callback);
			});
			tab.addLabelWidgetRow("", replacement);
		}
	}

	private void paintReplacedReceipt(Mod347 model, AonDisplayTable tab) {
		if (model.isComplementary() || model.isReplacement()) {
			replacedReceiptBox.setMaxLength(13);
			replacedReceiptBox.setVisibleLength(13);
			replacedReceiptBox.addValueChangeHandler(event -> model.setReplacedNumber(replacedReceiptBox.getValue()));
			tab.addLabelWidgetRow(AON.MSG.previousDeclaration(), replacedReceiptBox);
		}
	}

	private void paintChecksLabel(AonDisplayTable tab) {
		Label label = new Label("Informaci\u00F3n para el c\u00E1lculo");
		label.setStyleName(AON.CSS.aonBold());
		label.addStyleName(AON.CSS.aonTextUnderline());
		tab.addLabelWidgetRow("", label);
	}

	private void paintExcludeOutputNationalZero(Mod347 model, AonDisplayTable tab) {
		excludeOutputNationalZero.addClickHandler(event -> model.setExcludeOutputNationalZero(excludeOutputNationalZero.getValue()));
		tab.addLabelWidgetRow("", excludeOutputNationalZero);
	}

	private void paintExcludeInputNationalZero(Mod347 model, AonDisplayTable tab) {
		excludeInputNationalZero.addClickHandler(event -> model.setExcludeInputNationalZero(excludeInputNationalZero.getValue()));
		tab.addLabelWidgetRow("", excludeInputNationalZero);
	}

	private void paintExcludeRetention(Mod347 model, AonDisplayTable tab) {
		excludeRetention.addClickHandler(event -> model.setExcludeRetention(excludeRetention.getValue()));
		tab.addLabelWidgetRow("", excludeRetention);
	}

	private void paintExcludeIntracommunity(Mod347 model, AonDisplayTable tab) {
		excludeIntracommunity.addClickHandler(event -> model.setExcludeIntracommunity(excludeIntracommunity.getValue()));
		tab.addLabelWidgetRow("", excludeIntracommunity);
	}

	private Widget getButtonsPanel(Mod347 model, Model347Callback callback) {
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.CSS.aonPadding());
		buttonsPanel.addStyleName(AON.CSS.aonMarginTop());
		buttonsPanel.addStyleName(AON.CSS.aonTextCenter());
		Button acceptButton = new Button();
		acceptButton.setStyleName(AON.CSS.aonOkButton());
		acceptButton.setText(AON.MSG.accept());

		acceptButton.addClickHandler(event -> callback.onAccept(model));
		buttonsPanel.add(acceptButton);
		Button cancelButton = new Button();
		cancelButton.setStyleName(AON.CSS.aonCancelButton());
		cancelButton.addStyleName(AON.CSS.aonMarginLeft());
		cancelButton.setText(AON.MSG.cancelAction());
		cancelButton.addClickHandler(event -> callback.onCancel(model));
		buttonsPanel.add(cancelButton);
		return buttonsPanel;
	}

}
