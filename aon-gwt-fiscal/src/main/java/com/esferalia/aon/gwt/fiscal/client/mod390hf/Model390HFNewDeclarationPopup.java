package com.esferalia.aon.gwt.fiscal.client.mod390hf;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AdministrationListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.fiscal.client.mod390hf.Model390HF.Model390HFCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

public class Model390HFNewDeclarationPopup extends AonCustomDialog {
	
	protected int row = 0;
	private AdministrationListBox admonList = new AdministrationListBox(true);
	private AonIntegerBox yearBox = new AonIntegerBox();
	private CheckBox replacement = new CheckBox();
	private CheckBox complementary = new CheckBox();
	private CheckBox withoutActivity = new CheckBox();
	private Label defaultVatRegimeLabel = new Label();
	private ListBox defaultVatRegime = new ListBox();
	private AonDoubleBox prorate = new AonDoubleBox(7);
	
	public Model390HFNewDeclarationPopup(final Mod390HF mod390, final Model390HFCallback callback) {
		this(mod390, false, callback);	 	
	}
	
	public Model390HFNewDeclarationPopup(final Mod390HF mod390, final boolean reset, final Model390HFCallback callback) {
		setCaption(reset?AON.MSG.resetDeclaration():AON.MSG.newDeclaration());
		setGlassEnabled(true);
		setAnimationEnabled(true);
		
		FlexTable tab = new FlexTable();
		populate(mod390);

		FlowPanel rootPanel = new FlowPanel(); 
		tab.setCellPadding(0);
		tab.setCellSpacing(0);
		tab.setStyleName(AON.CSS.aonMarginTop());
		tab.addStyleName(AON.CSS.aonMarginBottom());
		tab.addStyleName(AON.CSS.aonTable());
		ColumnFormatter cf = tab.getColumnFormatter();
		cf.setWidth(0, "130px");
		cf.addStyleName(0, AON.CSS.aonPaddingLeft() );
		cf.addStyleName(0, AON.CSS.aonPaddingRight() );
		cf.setWidth(1, "250px");
		cf.addStyleName(0, AON.CSS.aonPaddingLeft() );
		cf.addStyleName(0, AON.CSS.aonPaddingRight() );

		// ADMINISTRATION
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel());
		tab.setWidget(row, 0, new Label(AON.MSG.administration()));
		admonList.setEnabled(!reset);
		admonList.addChangeHandler( event -> {
			mod390.setAdministration( admonList.getValue() );
			Model390HF.MOD_SERVICE.declarationChanged(callback.getOptions().getOccam(),mod390,
					new AsyncCallback<Mod390HF>() {
						@Override
						public void onSuccess(Mod390HF result) {
							replacement.setVisible(mod390.isReplacementDeclarationAvailable());
							complementary.setVisible(mod390.isComplementaryDeclarationAvailable());
							defaultVatRegimeLabel.setVisible(admonList.getValue() == Administration.COMMON_TERRITORY);
							defaultVatRegime.setVisible(admonList.getValue() == Administration.COMMON_TERRITORY);
							mod390.setPeriod(result.getPeriod());
							mod390.ensureDetail(result.getProrateKey()).setAmount(result.getProratePercent());
							populate(result);
						}
		
						@Override
						public void onFailure(Throwable caught) {
							callback.showError(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
						}
					});
			
		});
		tab.setWidget(row, 1, admonList);
		row++;

		// YEAR
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel());
		tab.setWidget(row, 0, new Label(AON.MSG.year()));
		
		yearBox.setMaxLength(4);
		yearBox.setVisibleLength(4);
		yearBox.setEnabled(!reset);
		yearBox.addValueChangeHandler(event -> mod390.setYear(yearBox.getValue()));
		tab.setWidget(row, 1,yearBox);
		row++;
		
		// PORCENTAJE DE PRORRATA
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel());
		tab.setWidget(row, 0, new Label(AON.MSG.prorrataPercent()));
		prorate.addValueChangeHandler(event -> mod390.ensureDetail(mod390.getProrateKey()).setAmount(prorate.getValue()));
		tab.setWidget(row, 1, prorate);
		row++;

		// COMPLEMENTARIA
		complementary.setText(AON.MSG.complementary());
		complementary.setEnabled(!reset);
		complementary.addClickHandler(event -> {
			mod390.setComplementary(complementary.getValue());
			replacement.setEnabled(!complementary.getValue());
			if (complementary.getValue().booleanValue()) {
				replacement.setValue(false);
			}
		});
		complementary.setVisible(mod390.isComplementaryDeclarationAvailable());
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		tab.setWidget(row, 0, complementary);
		row++;
		
		// SUSTITUTIVA
		replacement.setText(AON.MSG.replacement());
		replacement.setEnabled(!reset);
		replacement.addClickHandler(event -> {
			mod390.setReplacement(replacement.getValue());
			complementary.setEnabled(!replacement.getValue());
			if (replacement.getValue().booleanValue()) {
				complementary.setValue(false);
			}
		});
		replacement.setVisible(mod390.isReplacementDeclarationAvailable());
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		tab.setWidget(row, 0, replacement);
		row++;
		
		// SIN ACTIVIDAD
		withoutActivity.setText(AON.MSG.withoutActivity());
		withoutActivity.setValue(mod390.isWithoutActivity());
		withoutActivity.addClickHandler(event -> mod390.setWithoutActivity(withoutActivity.getValue()));
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		tab.setWidget(row, 0, withoutActivity);
		row++;
		
		// MENSAJE DE AVISO PARA INICIALIZAR EL MODELO
		if (reset) {
			Label labelReset = new Label(AON.MSG.resetWarning());
			labelReset.addStyleName(AON.CSS.aonMarginTop());
			labelReset.addStyleName(AON.CSS.aonColorRed());
			tab.setWidget(row, 0, labelReset);
			tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		}
		
		rootPanel.add(tab);
		
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.CSS.aonPadding());
		buttonsPanel.addStyleName(AON.CSS.aonMarginTop());
		buttonsPanel.addStyleName(AON.CSS.aonTextCenter());
		Button acceptButton = new Button();
		acceptButton.setStyleName(AON.CSS.aonOkButton());
		acceptButton.setText( AON.MSG.accept());
		
		acceptButton.addClickHandler(event -> {
			hide();
			callback.onAccept(mod390);
		});
		buttonsPanel.add(acceptButton);
		Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
		cancelButton.addClickHandler(event -> {
			hide();
			callback.onCancel(mod390);
		});
		buttonsPanel.add(cancelButton);
		rootPanel.add(buttonsPanel);
		add(rootPanel);
	}

	private void populate(Mod390HF mod390) {
		admonList.setSelectedIndex( mod390.getAdministration().ordinal());
		yearBox.setValue(mod390.getYear());
		prorate.setValue(mod390.ensureDetail(mod390.getProrateKey()).getAmount());
		complementary.setValue(mod390.isComplementary());
		replacement.setValue(mod390.isReplacement());
	}
}
