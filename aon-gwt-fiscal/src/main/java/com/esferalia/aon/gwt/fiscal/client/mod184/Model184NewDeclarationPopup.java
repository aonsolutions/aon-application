package com.esferalia.aon.gwt.fiscal.client.mod184;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AdministrationListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod184.Model184.Model184Callback;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.Label;

class Model184NewDeclarationPopup extends AonCustomDialog {
	
	protected int row = 0;
	private AdministrationListBox admonList = new AdministrationListBox();
	private AonIntegerBox yearBox = new AonIntegerBox();
	private CheckBox replacement = new CheckBox();
	private CheckBox complementary = new CheckBox();
	private AonTextBox replacedReceiptBox = new AonTextBox();
	
	public Model184NewDeclarationPopup(final Mod184 mod184 ,final Model184Callback callback) {
		this(mod184 ,false, false, callback);
	}
	
	public Model184NewDeclarationPopup(final Mod184 mod184, final boolean duplicate, final boolean reset, final Model184Callback callback) {

		// Cuando se duplica, por defecto el ejercicio es el siguiente y 
		// complementaria y sustitutiva están desmarcados
		int oldYear = mod184.getYear();
		if (duplicate) {
			mod184.setYear(oldYear+1);
			mod184.setComplementary(false);
			mod184.setReplacement(false);
			mod184.setReplacedReceipt("");
		}		
		setCaption(AON.MSG.newDeclaration());
		if (reset) setCaption(AON.MSG.resetDeclaration());
		if (duplicate) setCaption(AON.MSG.duplicate());
		setGlassEnabled(true);
		setAnimationEnabled(true);
		
		FlexTable tab = new FlexTable();
		
		admonList.setSelectedIndex( mod184.getAdministration().ordinal());
		yearBox.setValue(mod184.getYear());
		complementary.setValue(mod184.isComplementary());
		replacement.setValue(mod184.isReplacement());
		replacedReceiptBox.setValue(mod184.getReplacedReceipt());

		FlowPanel rootPanel = new FlowPanel(); 
		tab.setCellPadding(0);
		tab.setCellSpacing(0);
		tab.setStyleName(AON.CSS.aonMarginTop());
		tab.addStyleName(AON.CSS.aonMarginBottom());
		tab.addStyleName(AON.CSS.aonDisplayTable());
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
		admonList.setEnabled(!reset && !duplicate);
		admonList.addChangeHandler( event -> mod184.setAdministration( admonList.getValue() ));
		tab.setWidget(row, 1, admonList);
		row++;

		// YEAR
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel());
		tab.setWidget(row, 0, new Label(AON.MSG.year()));
		
		yearBox.setMaxLength(4);
		yearBox.setVisibleLength(4);
		yearBox.setEnabled(!reset);
		yearBox.addValueChangeHandler(event -> {
			mod184.setYear(yearBox.getValue());
			
			// Cuando se duplica el modelo, solo se puede marcar complementaria o sustitutiva
			// si el ejercicio es el mismo que el modelo que se quiere duplicar
			if (duplicate) {
				complementary.setEnabled(yearBox.getValue()==oldYear);
				replacement.setEnabled(yearBox.getValue()==oldYear);
				if (yearBox.getValue()!=oldYear) {
					complementary.setValue(false,true);
					replacement.setValue(false,true);					
				}
			}
		});
		tab.setWidget(row, 1,yearBox);
		row++;
		
		// COMPLEMENTARIA
		complementary.setText(AON.MSG.complementary());
		complementary.setEnabled(!reset && !duplicate); // Por defecto deshabilitada si es duplicar, porque el ejercicio por defecto es el siguiente
		complementary.addValueChangeHandler(event -> {
			mod184.setComplementary(complementary.getValue());
			
			if (complementary.getValue().booleanValue()) {
				replacement.setValue(false,true);
			}
			
			replacedReceiptBox.setEnabled(complementary.getValue() || replacement.getValue());
			if (!complementary.getValue().booleanValue() && !replacement.getValue().booleanValue()) {
				replacedReceiptBox.setValue("",true);
			}				
		});
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		tab.setWidget(row, 0, complementary);
		row++;
		
		// SUSTITUTIVA
		replacement.setText(AON.MSG.replacement());
		replacement.setEnabled(!reset && !duplicate); // Por defecto deshabilitada si es duplicar, porque el ejercicio por defecto es el siguiente
		replacement.addValueChangeHandler(event -> {
			mod184.setReplacement(replacement.getValue());				
			if (replacement.getValue().booleanValue()) {
				complementary.setValue(false,true);
			}				
			replacedReceiptBox.setEnabled(complementary.getValue() || replacement.getValue());
			if (!complementary.getValue().booleanValue() && !replacement.getValue().booleanValue()) {
				replacedReceiptBox.setValue("",true);					
			}			
		});
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		tab.setWidget(row, 0, replacement);
		row++;
		
		// NUMERO DE DECLARACION ANTERIOR
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.CSS.aonTableLabel());
		tab.setWidget(row, 0, new Label(AON.MSG.previousDeclaration()));
		
		replacedReceiptBox.setMaxLength(13);
		replacedReceiptBox.setVisibleLength(13);
		replacedReceiptBox.setEnabled(false);  // Por defecto deshabilitado porque complementaria y sustitutiva están desmarcados
		replacedReceiptBox.addValueChangeHandler(event -> mod184.setReplacedReceipt(replacedReceiptBox.getValue()));
		tab.setWidget(row, 1, replacedReceiptBox);
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
			callback.onAccept(mod184);
		});
		buttonsPanel.add(acceptButton);
		Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
		cancelButton.addClickHandler(event -> {
			hide();
			callback.onCancel( mod184);
		});
		buttonsPanel.add(cancelButton);
		rootPanel.add(buttonsPanel);
		add(rootPanel);
	}

}
