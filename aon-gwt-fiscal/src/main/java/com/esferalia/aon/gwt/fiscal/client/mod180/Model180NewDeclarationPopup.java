package com.esferalia.aon.gwt.fiscal.client.mod180;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AdministrationListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod180.Model180.Model180Callback;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class Model180NewDeclarationPopup extends AonCustomDialog {
	
	private AdministrationListBox admonList = new AdministrationListBox();
	private AonIntegerBox yearBox = new AonIntegerBox();
	private CheckBox replacement = new CheckBox();
	private CheckBox complementary = new CheckBox();
	private AonTextBox replacedReceiptBox = new AonTextBox();	
	
	public Model180NewDeclarationPopup(final Mod180 mod180, final Model180Callback callback) {
		this(mod180, false, false, callback);
	}
	
	public Model180NewDeclarationPopup(final Mod180 mod180, boolean duplicate, boolean reset, final Model180Callback callback) {
		setWidth("450px");
		
		// Cuando se duplica, por defecto el ejercicio es el siguiente y 
		// complementaria y sustitutiva están desmarcados
		int oldYear = mod180.getYear();
		if (duplicate) {
			mod180.setYear(oldYear+1);
			mod180.setComplementary(false);
			mod180.setReplacement(false);
			mod180.setReplacedReceipt("");
		}		
		setCaption(AON.MSG.newDeclaration());
		if (reset) setCaption(AON.MSG.resetDeclaration());
		if (duplicate) setCaption(AON.MSG.duplicate());
		setGlassEnabled(true);
		setAnimationEnabled(true);
		
		admonList.setSelectedIndex(mod180.getAdministration().ordinal());
		yearBox.setValue(mod180.getYear());
		complementary.setValue(mod180.isComplementary());
		replacement.setValue(mod180.isReplacement());
		replacedReceiptBox.setValue(mod180.getReplacedReceipt());

		FlowPanel rootPanel = new FlowPanel(); 

		// ADMINISTRATION
		admonList.setEnabled(!reset && !duplicate);
		admonList.addChangeHandler( event -> mod180.setAdministration( admonList.getValue() ));

		// YEAR
		yearBox.setMaxLength(4);
		yearBox.setVisibleLength(4);
		yearBox.setEnabled(!reset);
		yearBox.addValueChangeHandler(event -> {
			mod180.setYear(yearBox.getValue());
			
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
		
		// COMPLEMENTARIA
		complementary.setText(AON.MSG.complementary());
		complementary.setEnabled(!reset && !duplicate); // Por defecto deshabilitada si es duplicar, porque el ejercicio por defecto es el siguiente
		complementary.addValueChangeHandler(event -> {
			mod180.setComplementary(complementary.getValue());

			if (complementary.getValue().booleanValue()) {
				replacement.setValue(false,true);					
			}
				
			replacedReceiptBox.setEnabled(complementary.getValue() || replacement.getValue());
			if (!complementary.getValue().booleanValue() && !replacement.getValue()) {
				replacedReceiptBox.setValue("",true);
			}
			
		});
		
		// SUSTITUTIVA
		replacement.setText(AON.MSG.replacement());
		replacement.setEnabled(!reset && !duplicate); // Por defecto deshabilitada si es duplicar, porque el ejercicio por defecto es el siguiente
		replacement.addValueChangeHandler(event -> {
			mod180.setReplacement(replacement.getValue());
			if (replacement.getValue().booleanValue()) {
				complementary.setValue(false,true);
			}
			replacedReceiptBox.setEnabled(complementary.getValue() || replacement.getValue());
			if (!complementary.getValue().booleanValue() && !replacement.getValue()) {
				replacedReceiptBox.setValue("",true);					
			}				
		});
		
		// NUMERO DE DECLARACION ANTERIOR
		replacedReceiptBox.setMaxLength(13);
		replacedReceiptBox.setVisibleLength(13);
		replacedReceiptBox.setEnabled(mod180.isComplementary() || mod180.isReplacement());  
		replacedReceiptBox.addValueChangeHandler(event -> mod180.setReplacedReceipt(replacedReceiptBox.getValue()));
		
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonMarginTop());
		tab.addStyleName(AON.CSS.aonMarginBottom());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		
		tab.addRow()
			.addCell( new Label( AON.MSG.administration()), AON.CSS. aonTableLabel(),AON.CSS.aonWidth120())
			.addCell( admonList);
		tab.addRow()
			.addCell( new Label( AON.MSG.year()), AON.CSS. aonTableLabel())
			.addCell( yearBox );
		tab.addRow( )
			.addCell( new Label(), AON.CSS. aonTableLabel())
			.addCell( complementary);
		tab.addRow( )
			.addCell( new Label(), AON.CSS. aonTableLabel())
			.addCell( replacement);
		tab.addRow( )	
			.addCell( new Label( AON.MSG.previousDeclaration()), AON.CSS. aonTableLabel())
			.addCell(replacedReceiptBox);

		rootPanel.add(tab);
		
		// MENSAJE DE AVISO PARA INICIALIZAR EL MODELO
		if (reset) {
			Label labelReset = new Label(AON.MSG.resetWarning());
			labelReset.addStyleName(AON.CSS.aonMarginTop());
			labelReset.addStyleName(AON.CSS.aonColorRed());
			rootPanel.add(labelReset);
		}

		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.CSS.aonPadding());
		buttonsPanel.addStyleName(AON.CSS.aonMarginTop());
		buttonsPanel.addStyleName(AON.CSS.aonTextCenter());
		Button acceptButton = new Button();
		acceptButton.setStyleName(AON.CSS.aonOkButton());
		acceptButton.setText( AON.MSG.accept());
		
		acceptButton.addClickHandler(event -> {
			acceptButton.setEnabled(false);
			hide();
			callback.onAccept(mod180);
		});
		buttonsPanel.add(acceptButton);
		Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
		cancelButton.addClickHandler(event -> {
			hide();
			callback.onCancel(mod180);
		});
		buttonsPanel.add(cancelButton);
		rootPanel.add(buttonsPanel);
		add(rootPanel);
	}

}
