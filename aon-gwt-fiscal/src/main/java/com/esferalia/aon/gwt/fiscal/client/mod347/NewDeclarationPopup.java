package com.esferalia.aon.gwt.fiscal.client.mod347;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AdministrationListBox;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.fiscal.client.mod347.Model347.Model347Callback;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.Label;

public class NewDeclarationPopup extends CustomDialog {
	
	public NewDeclarationPopup(final Mod347 mod347 ,final Model347Callback callback) {
		setCaption(AON.MSG.newDeclaration());
		setGlassEnabled(true);
		setAnimationEnabled(true);
		
		int row = 0;
		AdministrationListBox admonList = new AdministrationListBox();
		IntegerBox yearBox = new IntegerBox();
		CheckBox replacement = new CheckBox();
		CheckBox complementary = new CheckBox();
		
		CheckBox excludeOutputNationalZero = new CheckBox();
		CheckBox excludeInputNationalZero = new CheckBox();
//		CheckBox excludeMod180Declared = new CheckBox();
//		CheckBox excludeMod190Declared = new CheckBox();
//		Label retentionLabel = new Label("Se excluyen facturas con retenci\u00F3n");
		CheckBox excludeRetention = new CheckBox();
		CheckBox excludeIntracommunity = new CheckBox();

		FlexTable tab = new FlexTable();
		admonList.setSelectedIndex( mod347.getAdministration().ordinal());
		yearBox.setValue(mod347.getYear());

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
		cf.setWidth(1, "400px");
		cf.addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		cf.addStyleName(0, AON.AON_CSS.aonPaddingRight() );

		// ADMINISTRATION
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.administration()));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		admonList.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				mod347.setAdministration( admonList.getValue() );
				complementary.setEnabled(true);
				replacement.setEnabled(true);
				
			    // Gipuzkoa, no hay complementarias ni sustitutivas
				if (mod347.getAdministration() == Administration.GIPUZKOA)
				{
					complementary.setEnabled(false);
					complementary.setValue(false);
					mod347.setComplementary(false);
					replacement.setEnabled(false);
					replacement.setValue(false);
					mod347.setReplacement(false);
				}
				
				// Bizkaia, no hay complementarias
				if (mod347.getAdministration() == Administration.BIZKAIA)
				{
					complementary.setEnabled(false);
					complementary.setValue(false);
					mod347.setComplementary(false);
				}
				
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
				mod347.setYear(yearBox.getValue());
			}
		});
		tab.setWidget(row, 1,yearBox);
				
		// COMPLEMENTARIA		
		row++;
		complementary.setText(AON.MSG.complementary());
		complementary.setEnabled(mod347.getAdministration()!=Administration.BIZKAIA && mod347.getAdministration()!=Administration.GIPUZKOA);  // Complementaria solo si no es Bizkaia, ni Gipuzkoa
		complementary.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				mod347.setComplementary(complementary.getValue());
				replacement.setEnabled(!complementary.getValue());
				if (complementary.getValue()) {
					replacement.setValue(false);
				}
			}
		});
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		tab.setWidget(row, 0, complementary);
		row++;
		
		
		// SUSTITUTIVA
		replacement.setText(AON.MSG.replacement());
		replacement.setEnabled(mod347.getAdministration()!=Administration.GIPUZKOA);  // Sustitutiva solo si no es Gipuzkoa
		replacement.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				mod347.setReplacement(replacement.getValue());
				complementary.setEnabled(!replacement.getValue());
				if (replacement.getValue()) {
					complementary.setValue(false);
				}
			}
		});
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		tab.setWidget(row, 0, replacement);
		row++;
		
		// info
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label("Informaci\u00F3n para el c\u00E1lculo"));
		row++;

		// excludeOutputNationalZero
		excludeOutputNationalZero.setText("Excluir bases de facturas EMITIDAS con IVA al 0%.");
		excludeOutputNationalZero.setValue(mod347.isExcludeOutputNationalZero());
		excludeOutputNationalZero.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				mod347.setExcludeOutputNationalZero(excludeOutputNationalZero.getValue());
			}
		});
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		tab.setWidget(row, 0, excludeOutputNationalZero);
		row++;
		
		// excludeInputNationalZero
		excludeInputNationalZero.setText("Excluir bases de facturas RECIBIDAS con IVA al 0%.");
		excludeInputNationalZero.setValue(mod347.isExcludeInputNationalZero());
		excludeInputNationalZero.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				mod347.setExcludeInputNationalZero(excludeInputNationalZero.getValue());
			}
		});
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		tab.setWidget(row, 0, excludeInputNationalZero);
		row++;
		
//		// excludeMod180Declared
//		excludeMod180Declared.setText("Excluir facturas con retenci\u00F3n de perceptores declarados en el modelo 180.");
//		excludeMod180Declared.setValue(mod347.isExcludeMod180Declared());
//		excludeMod180Declared.addClickHandler(new ClickHandler() {
//			
//			@Override
//			public void onClick(ClickEvent event) {
//				mod347.setExcludeMod180Declared(excludeMod180Declared.getValue());
//				retentionLabel.setVisible(!mod347.isExcludeMod180Declared() && !mod347.isExcludeMod190Declared() );
//			}
//		});
//		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
//		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
//		tab.setWidget(row, 0, excludeMod180Declared);
//		row++;
		
		// excludeRetention
		excludeRetention.setText("Excluir facturas con retenci\u00F3n.");
		excludeRetention.setValue(mod347.isExcludeRetention());
		excludeRetention.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				mod347.setExcludeRetention(excludeRetention.getValue());				
			}
		});
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		tab.setWidget(row, 0, excludeRetention);
		row++;

//		// excludeMod190Declared
//		excludeMod190Declared.setText("Excluir facturas con retenci\u00F3n de perceptores declarados en el modelo 190.");
//		excludeMod190Declared.setValue(mod347.isExcludeMod190Declared());
//		excludeMod190Declared.addClickHandler(new ClickHandler() {
//			
//			@Override
//			public void onClick(ClickEvent event) {
//				mod347.setExcludeMod190Declared(excludeMod190Declared.getValue());
//				retentionLabel.setVisible(!mod347.isExcludeMod180Declared() && !mod347.isExcludeMod190Declared() );
//			}
//		});
//		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
//		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
//		tab.setWidget(row, 0, excludeMod190Declared);
//		row++;
		
		// excludeIntracommunity
		excludeIntracommunity.setText("Excluir facturas intracomunitarias.");
		excludeIntracommunity.setValue(mod347.isExcludeIntracommunity());
		excludeIntracommunity.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				mod347.setExcludeIntracommunity(excludeIntracommunity.getValue());
			}
		});
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		tab.setWidget(row, 0, excludeIntracommunity);
		row++;

//		// info
//		retentionLabel.setVisible(!mod347.isExcludeMod180Declared() && !mod347.isExcludeMod190Declared() );
//		retentionLabel.setStyleName(AON.AON_CSS.aonMarginLeft());
//		retentionLabel.addStyleName(AON.AON_CSS.aonBold());
//		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
//		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
//		tab.setWidget(row, 0, retentionLabel);
//		row++;

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
				callback.onAccept(mod347);
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

}
