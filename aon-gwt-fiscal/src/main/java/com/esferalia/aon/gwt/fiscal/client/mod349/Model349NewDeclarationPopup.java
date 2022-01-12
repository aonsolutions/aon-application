package com.esferalia.aon.gwt.fiscal.client.mod349;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AdministrationListBox;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.common.client.widget.PeriodListBox;
import com.esferalia.aon.gwt.fiscal.client.mod349.Model349.Model349Callback;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
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
import com.google.gwt.user.client.ui.TextBox;

public class Model349NewDeclarationPopup extends CustomDialog {
	
	protected int row = 0;
	private AdministrationListBox admonList = new AdministrationListBox();
	private IntegerBox yearBox = new IntegerBox();
	private PeriodListBox periodList = new PeriodListBox();
	private CheckBox replacement = new CheckBox();
	private CheckBox complementary = new CheckBox();
	private TextBox replacedReceiptBox = new TextBox();
	private CheckBox diffCalculation = new CheckBox();
	
	public Model349NewDeclarationPopup(final Mod349 mod349, final Model349Callback callback) {
		this(mod349, false, false, callback);		
	}
	
	public Model349NewDeclarationPopup(final Mod349 mod349, final boolean duplicate, final boolean reset, final Model349Callback callback) {
		
		// Cuando se duplica, por defecto el ejercicio es el siguiente y 
		// complementaria y sustitutiva están desmarcados
		int oldYear = mod349.getYear();
		Period oldPeriod = mod349.getPeriod();
		if (duplicate) {
			mod349.setYear(oldYear+1);
			mod349.setPeriod(null);
			mod349.setComplementary(false);
			mod349.setReplacement(false);
			mod349.setReplacedNumber("");
		}		
				
		setCaption(reset?AON.MSG.resetDeclaration():duplicate?AON.MSG.duplicate():AON.MSG.newDeclaration());
		setGlassEnabled(true);
		setAnimationEnabled(true);
		
		// Añadimos a periodList tambien el periodo anual, pues este modelo permite todos los periodos posibles
		periodList.addItem(Period.YEAR.getDescription(), Integer.toString(Period.YEAR.ordinal()));
		
		FlexTable tab = new FlexTable();
		
		admonList.setSelectedIndex( mod349.getAdministration().ordinal());
		yearBox.setValue(mod349.getYear());
		periodList.setValue(mod349.getPeriod());
		complementary.setValue(mod349.isComplementary());
		replacement.setValue(mod349.isReplacement());
		replacedReceiptBox.setValue(mod349.getReplacedNumber());		

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
		admonList.setEnabled(!reset && !duplicate);
		admonList.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				mod349.setAdministration( admonList.getValue() );
				
				complementary.setEnabled(true);
				replacement.setEnabled(true);
				
			    // Gipuzkoa, no hay complementarias ni sustitutivas
				if (mod349.getAdministration() == Administration.GIPUZKOA)
				{
					complementary.setEnabled(false);
					complementary.setValue(false);
					mod349.setComplementary(false);
					replacement.setEnabled(false);
					replacement.setValue(false);
					mod349.setReplacement(false);
					replacedReceiptBox.setEnabled(false);
					replacedReceiptBox.setValue("");
					mod349.setReplacedNumber("");
				}
				
				// Bizkaia, no hay complementarias
				if (mod349.getAdministration() == Administration.BIZKAIA)
				{
					complementary.setEnabled(false);
					complementary.setValue(false);
					mod349.setComplementary(false);
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
		yearBox.setEnabled(!reset);
		yearBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				mod349.setYear(yearBox.getValue());
				
				// Cuando se duplica el modelo, solo se puede marcar complementaria o sustitutiva
				// si el ejercicio y periodo es el mismo que el modelo que se quiere duplicar
				if (duplicate) {
					complementary.setEnabled(yearBox.getValue() == oldYear && periodList.getValue() == oldPeriod && mod349.getAdministration() != Administration.GIPUZKOA && mod349.getAdministration() != Administration.BIZKAIA);
					replacement.setEnabled(yearBox.getValue() == oldYear && periodList.getValue() == oldPeriod && mod349.getAdministration() != Administration.GIPUZKOA);
					if (yearBox.getValue() != oldYear || periodList.getValue() != oldPeriod) {
						complementary.setValue(false,true);
						replacement.setValue(false,true);					
					}
				}
			}
		});
		tab.setWidget(row, 1,yearBox);
		row++;
		
		// PERIOD
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.period()));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		periodList.setEnabled(!reset);
		periodList.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				mod349.setPeriod( periodList.getValue() );
				
				// Cuando se duplica el modelo, solo se puede marcar complementaria o sustitutiva
				// si el ejercicio y periodo es el mismo que el modelo que se quiere duplicar
				if (duplicate) {
					complementary.setEnabled(yearBox.getValue() == oldYear && periodList.getValue() == oldPeriod && mod349.getAdministration() != Administration.GIPUZKOA && mod349.getAdministration() != Administration.BIZKAIA);
					replacement.setEnabled(yearBox.getValue() == oldYear && periodList.getValue() == oldPeriod && mod349.getAdministration() != Administration.GIPUZKOA);
					if (yearBox.getValue() != oldYear || periodList.getValue() != oldPeriod) {
						complementary.setValue(false,true);
						replacement.setValue(false,true);					
					}
				}
			}
		});
		tab.setWidget(row, 1, periodList);
		row++;
		
		// COMPLEMENTARIA 		
		complementary.setText(AON.MSG.complementary());
		complementary.setEnabled(!reset && !duplicate && mod349.getAdministration()!=Administration.BIZKAIA && mod349.getAdministration()!=Administration.GIPUZKOA);  // Complementaria solo si no es Bizkaia, ni Gipuzkoa
		complementary.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				mod349.setComplementary(complementary.getValue());
				
				if (complementary.getValue()) {
					replacement.setValue(false,true);
				}
				
				replacedReceiptBox.setEnabled(complementary.getValue() || replacement.getValue());
				if (!complementary.getValue() && !replacement.getValue()) {
					replacedReceiptBox.setValue("",true);
				}				
			}
		});
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		tab.setWidget(row, 0, complementary);
		row++;
		
		// SUSTITUTIVA
		replacement.setText(AON.MSG.replacement());
		replacement.setEnabled(!reset && !duplicate && mod349.getAdministration()!=Administration.GIPUZKOA);  // Sustitutiva solo si no es Gipuzkoa
		replacement.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				mod349.setReplacement(replacement.getValue());				
				if (replacement.getValue()) {
					complementary.setValue(false,true);
				}				
				replacedReceiptBox.setEnabled(complementary.getValue() || replacement.getValue());
				if (!complementary.getValue() && !replacement.getValue()) {
					replacedReceiptBox.setValue("",true);					
				}			
			}
			
		});
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		tab.setWidget(row, 0, replacement);
		row++;
		
		// NUMERO DE DECLARACION ANTERIOR
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		tab.setWidget(row, 0, new Label(AON.MSG.previousDeclaration()));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		
		replacedReceiptBox.setMaxLength(13);
		replacedReceiptBox.setVisibleLength(13);
		replacedReceiptBox.setEnabled(mod349.isComplementary() || mod349.isReplacement());  
		replacedReceiptBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				mod349.setReplacedNumber(replacedReceiptBox.getValue());
			}
		});
		tab.setWidget(row, 1, replacedReceiptBox);
		row++;
		
		// El check inferior solo aparece cuando se crea uno nuevo
		if (!duplicate) {
			
			// CALCULO POR DIFERENCIA (Solo si no está deshabilitado en la parametrización)
			if (mod349.isDiffEnabled()) {
				diffCalculation.setText(AON.MSG.diffCalculation());
				diffCalculation.setValue(mod349.isDiffEnabled());
				diffCalculation.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						mod349.setDiffEnabled(diffCalculation.getValue());
					}
					
				});			
				tab.getFlexCellFormatter().setColSpan(row, 0, 2);
				tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
				tab.setWidget(row, 0, diffCalculation);		
			}
		
		}
		rootPanel.add(tab);
		
		// MENSAJE DE AVISO PARA INICIALIZAR EL MODELO
		if (reset) {
			Label labelReset = new Label(AON.MSG.resetWarning());
			labelReset.addStyleName(AON.CSS.aonMarginTop());
			labelReset.addStyleName(AON.CSS.aonColorRed());
			tab.setWidget(row, 0, labelReset);
			tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		}
		
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
				callback.onAccept(mod349);
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
				callback.onCancel(mod349);
			}
			
		});
		buttonsPanel.add(cancelButton);
		rootPanel.add(buttonsPanel);
		add(rootPanel);
	}

}
