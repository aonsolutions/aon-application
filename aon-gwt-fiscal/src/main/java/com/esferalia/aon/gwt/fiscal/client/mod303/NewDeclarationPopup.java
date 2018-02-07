package com.esferalia.aon.gwt.fiscal.client.mod303;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AdministrationListBox;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.common.client.widget.PeriodListBox;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303.Model303Callback;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

public class NewDeclarationPopup<T extends FiscalModel> extends CustomDialog {
	
	protected int row = 0;
	private AdministrationListBox admonList = new AdministrationListBox();
	private IntegerBox yearBox = new IntegerBox();
	private CheckBox replacement = new CheckBox();
	private CheckBox complementary = new CheckBox();
	private CheckBox withoutActivity = new CheckBox();
	private CheckBox diffCalculation = new CheckBox();
	private Label defaultVatRegimeLabel = new Label();
	private ListBox defaultVatRegime = new ListBox();
	private DoubleBox prorate = new DoubleBox(7);
	private PeriodListBox periodList = new PeriodListBox(true);
	
	public NewDeclarationPopup(final Mod303 mod303 ,final Model303Callback callback) {
		setCaption(AON.MSG.newDeclaration());
		setGlassEnabled(true);
		setAnimationEnabled(true);
		
		FlexTable tab = new FlexTable();
		populate(mod303);

		FlowPanel rootPanel = new FlowPanel(); 
		tab.setCellPadding(0);
		tab.setCellSpacing(0);
		tab.setStyleName(AON.AON_CSS.aonMarginTop());
		tab.addStyleName(AON.AON_CSS.aonMarginBottom());
		tab.addStyleName(AON.AON_CSS.aonPanelGrid());
		ColumnFormatter cf = tab.getColumnFormatter();
		cf.setWidth(0, "130px");
		cf.addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		cf.addStyleName(0, AON.AON_CSS.aonPaddingRight() );
		cf.setWidth(1, "250px");
		cf.addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		cf.addStyleName(0, AON.AON_CSS.aonPaddingRight() );

		// ADMINISTRATION
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.administration()));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		admonList.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				mod303.setAdministration( admonList.getValue() );
				Model303.SERVICE.declarationChanged(callback.getDomainName(), callback.getUser(), callback.getDomain(),mod303,
						new AsyncCallback<Mod303>() {
							@Override
							public void onSuccess(Mod303 result) {
								replacement.setVisible(mod303.isReplacementDeclarationAvailable());
								complementary.setVisible(mod303.isComplementaryDeclarationAvailable());
								defaultVatRegimeLabel.setVisible(admonList.getValue() == Administration.COMMON_TERRITORY);
								defaultVatRegime.setVisible(admonList.getValue() == Administration.COMMON_TERRITORY);
								mod303.setPeriod(result.getPeriod());
								mod303.ensureDetail(result.getProrateKey()).setAmount(result.getProratePercent());
								populate(result);
							}
			
							@Override
							public void onFailure(Throwable caught) {
								callback.showError(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
							}
						});
				
			}
		});
		tab.setWidget(row, 1, admonList);
		row++;

		// YEAR
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.year()));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		
		yearBox.setMaxLength(4);
		yearBox.setVisibleLength(4);
		yearBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				mod303.setYear(yearBox.getValue());
			}
		});
		tab.setWidget(row, 1,yearBox);
		row++;
		
		// PERIOD
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.period()));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		periodList.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				mod303.setPeriod( periodList.getValue() );
			}
		});
		tab.setWidget(row, 1, periodList);
		row++;
		
		// PORCENTAJE DE PRORRATA
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.prorrataPercent()));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		prorate.addValueChangeHandler(new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				mod303.ensureDetail(mod303.getProrateKey()).setAmount(prorate.getValue());
			}
		});
		tab.setWidget(row, 1, prorate);
		row++;

		// REGIMEN IVA POR DEFECTO
		defaultVatRegimeLabel.setText("Destinar Fras. sin actividad a");
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, defaultVatRegimeLabel);
		defaultVatRegimeLabel.setVisible(mod303.isAEAT());
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		
		defaultVatRegime.addItem(VATRegime.GENERAL.getName());
		defaultVatRegime.addItem(VATRegime.SIMPLIFIED.getName());
		defaultVatRegime.setSelectedIndex(0);
		defaultVatRegime.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				mod303.setDefaultVatRegime(defaultVatRegime.getSelectedIndex() == 1? VATRegime.SIMPLIFIED: VATRegime.GENERAL);
			}
		});
		defaultVatRegime.setVisible(mod303.isAEAT());
		tab.setWidget(row, 1, defaultVatRegime);
		row++;

		// COMPLEMENTARIA
		complementary.setText(AON.MSG.complementary());
		complementary.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				mod303.setComplementary(complementary.getValue());
				replacement.setEnabled(!complementary.getValue());
				if (complementary.getValue()) {
					replacement.setValue(false);
				}
			}
		});
		complementary.setVisible(mod303.isComplementaryDeclarationAvailable());
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		tab.setWidget(row, 0, complementary);
		row++;
		
		// SUSTITUTIVA
		replacement.setText(AON.MSG.replacement());
		replacement.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				mod303.setReplacement(replacement.getValue());
				complementary.setEnabled(!replacement.getValue());
				if (replacement.getValue()) {
					complementary.setValue(false);
				}
			}
		});
		replacement.setVisible(mod303.isReplacementDeclarationAvailable());
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		tab.setWidget(row, 0, replacement);
		row++;
		
		// SIN ACTIVIDAD
		withoutActivity.setText(AON.MSG.withoutActivity());
		withoutActivity.setValue(mod303.isWithoutActivity());
		withoutActivity.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				mod303.setWithoutActivity(withoutActivity.getValue());
			}
		});
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		tab.setWidget(row, 0, withoutActivity);
		row++;
		
		// CALCULO POR DIFERENCIA
		diffCalculation.setText(AON.MSG.diffCalculation());
		diffCalculation.setValue(!mod303.isDiffCalculationDisabled());
		diffCalculation.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				mod303.setDiffCalculationDisabled(!diffCalculation.getValue());
			}
		});
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		tab.setWidget(row, 0, diffCalculation);
		row++;

		rootPanel.add(tab);
		
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.AON_CSS.aonPadding());
		buttonsPanel.addStyleName(AON.AON_CSS.aonMarginTop());
		buttonsPanel.addStyleName(AON.AON_CSS.aonTextCenter());
		Button acceptButton = new Button();
		acceptButton.setStyleName(AON.AON_CSS.aonConfirmDialogOkButton());
		acceptButton.setText( AON.MSG.accept());
		
		acceptButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				hide();
				callback.onAccept(mod303);
			}
		});
		buttonsPanel.add(acceptButton);
		Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.AON_CSS.aonConfirmDialogCancelButton());
    	cancelButton.addStyleName(AON.AON_CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
		cancelButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				hide();
				callback.onCancel();
			}
			
		});
		buttonsPanel.add(cancelButton);
		rootPanel.add(buttonsPanel);
		add(rootPanel);
	}

	private void populate(Mod303 mod303) {
		admonList.setSelectedIndex( mod303.getAdministration().ordinal());
		yearBox.setValue(mod303.getYear());
		periodList.setValue(mod303.getPeriod());
		prorate.setValue(mod303.ensureDetail(mod303.getProrateKey()).getAmount());
	}
}
