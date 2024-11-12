package com.esferalia.aon.gwt.fiscal.client.mod369;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AdministrationListBox;
import com.esferalia.aon.gwt.common.client.widget.PeriodListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.fiscal.client.mod369.Model369.Model369Callback;
import com.esferalia.aon.occam.api.model.fiscal.Mod369;
import com.esferalia.aon.occam.api.model.fiscal.Mod369Regime;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

class Model369NewDeclarationPopup extends AonCustomDialog {
	
	private AdministrationListBox admonList = new AdministrationListBox();
	private AonIntegerBox yearBox = new AonIntegerBox();
	private PeriodListBox periodList = new PeriodListBox();
	private ListBox regimeList = new ListBox();
	
//	private CheckBox replacement = new CheckBox();
//	private CheckBox complementary = new CheckBox();
//	private AonTextBox replacedReceiptBox = new AonTextBox();
	
	public Model369NewDeclarationPopup(final Mod369 mod369 ,final Model369Callback callback) {
		this(mod369 ,false, false, callback);
	}
	
	public Model369NewDeclarationPopup(final Mod369 mod369, final boolean duplicate, final boolean reset, final Model369Callback callback) {
		setWidth("450px");
		
		int oldYear = mod369.getYear();
		if (duplicate) {
			mod369.setYear(oldYear);
//			mod369.setComplementary(false);
//			mod369.setReplacement(false);
//			mod369.setReplacedReceipt("");
		}		
		setCaption(AON.MSG.newDeclaration());
		if (reset) setCaption(AON.MSG.resetDeclaration());
		if (duplicate) setCaption(AON.MSG.duplicate());
		setGlassEnabled(true);
		setAnimationEnabled(true);
		
		if (mod369.getRegime()==null)
			mod369.setRegime(Mod369Regime.UNION);
		
		admonList.setSelectedIndex(mod369.getAdministration().ordinal());
		yearBox.setValue(mod369.getYear());
		periodList.setValue(mod369.getPeriod());
		regimeList.setSelectedIndex(mod369.getRegime().value());
		
//		complementary.setValue(mod369.isComplementary());
//		replacement.setValue(mod369.isReplacement());
//		replacedReceiptBox.setValue(mod369.getReplacedReceipt());

		FlowPanel rootPanel = new FlowPanel();
		
		// MENSAJE DE AVISO SI NO SE CUMPLIMENTAN LOS DATOS EJERCICIO Y PERIODO
		
		Label labelWarning = new Label("DEBE CUMPLIMENTAR TODOS LOS DATOS");
		labelWarning.setStyleName(AON.CSS.aonMarginTop());
		labelWarning.addStyleName(AON.CSS.aonTextCenter());
		labelWarning.addStyleName(AON.CSS.aonBold());		
		labelWarning.addStyleName(AON.CSS.aonColorRed());
		labelWarning.setVisible(false);

		// ADMINISTRATION

		//admonList.setEnabled(!reset && !duplicate);
		admonList.setEnabled(false);  // POR AHORA SOLO AEAT
		admonList.addChangeHandler( event -> mod369.setAdministration( admonList.getValue() ));

		// EJERCICIO
		
		yearBox.setMaxLength(4);
		yearBox.setVisibleLength(4);
		yearBox.setEnabled(!reset);
		yearBox.addValueChangeHandler(event -> {
			mod369.setYear(yearBox.getValue()==null?0:yearBox.getValue());
			labelWarning.setVisible(false);
		});
		
		// PERIODO
		
		periodList.addChangeHandler( event -> {
			mod369.setPeriod(periodList.getValue());
			labelWarning.setVisible(false);			
		});
		
		//periodList.addChangeHandler( event -> mod369.setPeriod( periodList.getValue() ));
		//tab.addLabelWidgetRow(AON.MSG.period(), periodList);
		
		// REGIMEN
		
		for (Mod369Regime p : Mod369Regime.values()) {
			regimeList.addItem(p.getDescription(), Integer.toString(p.ordinal()));	
		}
		regimeList.addChangeHandler(event -> {
			mod369.setRegime(Mod369Regime.safeValueOf(regimeList.getSelectedIndex()));
		});

		
		
//		defaultVatRegimeLabel.setText("Destinar Fras. sin actividad a");
//		if (defaultVatRegime.getItemCount() == 0) {
//			defaultVatRegime.addItem(VATRegime.GENERAL.getName());
//			defaultVatRegime.addItem(VATRegime.SIMPLIFIED.getName());
//		}
//		defaultVatRegime.addChangeHandler(event -> {
//			model.setDefaultVatRegime(defaultVatRegime.getSelectedIndex() == 1? VATRegime.SIMPLIFIED: VATRegime.GENERAL);
//			initialize(model, callback );	
//		});
//		
//		defaultVatRegime.setSelectedIndex( model.getDefaultVATRegime() == VATRegime.SIMPLIFIED? 1 : 0);
//		tab.addLabelWidgetRow(defaultVatRegimeLabel, defaultVatRegime);


		
		// COMPLEMENTARIA
//		complementary.setText(AON.MSG.complementary());
//		complementary.setEnabled(!reset && !duplicate); // Por defecto deshabilitada si es duplicar, porque el ejercicio por defecto es el siguiente
//		complementary.addValueChangeHandler(event -> {
//			mod369.setComplementary(complementary.getValue());
//			
//			if (complementary.getValue().booleanValue()) {
//				replacement.setValue(false,true);
//			}
//			
//			replacedReceiptBox.setEnabled(complementary.getValue() || replacement.getValue());
//			if (!complementary.getValue().booleanValue() && !replacement.getValue().booleanValue()) {
//				replacedReceiptBox.setValue("",true);
//			}				
//		});

		// SUSTITUTIVA
//		replacement.setText(AON.MSG.replacement());
//		replacement.setEnabled(!reset && !duplicate); // Por defecto deshabilitada si es duplicar, porque el ejercicio por defecto es el siguiente
//		replacement.addValueChangeHandler(event -> {
//			mod369.setReplacement(replacement.getValue());				
//			if (replacement.getValue().booleanValue()) {
//				complementary.setValue(false,true);
//			}				
//			replacedReceiptBox.setEnabled(complementary.getValue() || replacement.getValue());
//			if (!complementary.getValue().booleanValue() && !replacement.getValue().booleanValue()) {
//				replacedReceiptBox.setValue("",true);					
//			}			
//		});
		
		// NUMERO DE DECLARACION ANTERIOR
//		replacedReceiptBox.setMaxLength(13);
//		replacedReceiptBox.setVisibleLength(13);
//		replacedReceiptBox.setEnabled(false);  // Por defecto deshabilitado porque complementaria y sustitutiva están desmarcados
//		replacedReceiptBox.addValueChangeHandler(event -> mod369.setReplacedReceipt(replacedReceiptBox.getValue()));

		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonMarginTop());
		tab.addStyleName(AON.CSS.aonMarginBottom());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		
		tab.addRow()
			.addCell( new Label(AON.MSG.administration()), AON.CSS.aonTableLabel(), AON.CSS.aonWidth120())
			.addCell( admonList);
		tab.addRow()
			.addCell( new Label(AON.MSG.year()), AON.CSS.aonTableLabel())
			.addCell( yearBox );
		tab.addRow()
			.addCell( new Label(AON.MSG.period()), AON.CSS.aonTableLabel())
			.addCell( periodList );
		tab.addRow()
			.addCell( new Label("R\u00E9gimen"), AON.CSS.aonTableLabel())
			.addCell( regimeList );

//		tab.addRow( )
//			.addCell( new Label(), AON.CSS. aonTableLabel())
//			.addCell( complementary);
//		tab.addRow( )
//			.addCell( new Label(), AON.CSS. aonTableLabel())
//			.addCell( replacement);
//		tab.addRow( )	
//			.addCell( new Label( AON.MSG.previousDeclaration()), AON.CSS. aonTableLabel())
//			.addCell(replacedReceiptBox);
		
		rootPanel.add(tab);
		
		// MENSAJE DE AVISO PARA INICIALIZAR EL MODELO
		if (reset) {
			Label labelReset = new Label(AON.MSG.resetWarning());
			labelReset.addStyleName(AON.CSS.aonMarginTop());
			labelReset.addStyleName(AON.CSS.aonColorRed());
			rootPanel.add(labelReset);
		}
		
		rootPanel.add(labelWarning);		
		
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.CSS.aonPadding());
		buttonsPanel.addStyleName(AON.CSS.aonMarginTop());
		buttonsPanel.addStyleName(AON.CSS.aonTextCenter());
		
		Button acceptButton = new Button();
		acceptButton.setStyleName(AON.CSS.aonOkButton());
		acceptButton.setText( AON.MSG.accept());		
		acceptButton.addClickHandler(event -> {
			acceptButton.setEnabled(false);
			if (mod369.getYear() == 0 || mod369.getPeriod() == null) {
				labelWarning.setVisible(true);
				acceptButton.setEnabled(true);
			} else {
				hide();
				callback.onAccept(mod369);
			}
		});		
		buttonsPanel.add(acceptButton);
		
		Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
		cancelButton.addClickHandler(event -> {
			hide();
			callback.onCancel(mod369);
		});
		buttonsPanel.add(cancelButton);
		
		rootPanel.add(buttonsPanel);
		add(rootPanel);
	}

}
