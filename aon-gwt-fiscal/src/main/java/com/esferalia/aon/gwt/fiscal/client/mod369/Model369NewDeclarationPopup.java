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
	Label labelWarning = new Label();
	
	public Model369NewDeclarationPopup(final Mod369 mod369, final Model369Callback callback) {
		setWidth("450px");
		
		setCaption(AON.MSG.newDeclaration());
		setGlassEnabled(true);
		setAnimationEnabled(true);
		
		if (mod369.getRegime()==null)
			mod369.setRegime(Mod369Regime.UNION);
		
		admonList.setSelectedIndex(mod369.getAdministration().ordinal());
		yearBox.setValue(mod369.getYear());
		periodList.setValue(mod369.getPeriod());
		regimeList.setSelectedIndex(mod369.getRegime().value());
		
		FlowPanel rootPanel = new FlowPanel();
		
		// ADMINISTRATION

		admonList.setEnabled(false);  // POR AHORA SOLO AEAT
		admonList.addChangeHandler( event -> mod369.setAdministration( admonList.getValue() ));

		// EJERCICIO
		
		yearBox.setMaxLength(4);
		yearBox.setVisibleLength(4);
		yearBox.addValueChangeHandler(event -> {
			mod369.setYear(yearBox.getValue()==null?0:yearBox.getValue());
			labelWarning.setVisible(false);
		});
		
		// PERIODO
		
		periodList.addChangeHandler( event -> {
			mod369.setPeriod(periodList.getValue());
			labelWarning.setVisible(false);			
		});
		
		// REGIMEN
		
		for (Mod369Regime p : Mod369Regime.values()) {
			regimeList.addItem(p.getDescription(), Integer.toString(p.ordinal()));	
		}
		regimeList.addChangeHandler(event -> {
			mod369.setRegime(Mod369Regime.safeValueOf(regimeList.getSelectedIndex()));
		});

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
		
		rootPanel.add(tab);
		
		// MENSAJE DE AVISO SI NO SE CUMPLIMENTAN LOS DATOS EJERCICIO Y PERIODO O EL PERIODO NO ES ACORDE CON EL REGIMEN		
		
		labelWarning.setStyleName(AON.CSS.aonMarginTop());
		labelWarning.addStyleName(AON.CSS.aonTextCenter());
		labelWarning.addStyleName(AON.CSS.aonBold());		
		labelWarning.addStyleName(AON.CSS.aonColorRed());
		labelWarning.setVisible(false);		
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
				labelWarning.setText("DEBE CUMPLIMENTAR TODOS LOS DATOS");
				labelWarning.setVisible(true);
				acceptButton.setEnabled(true);
			}
			else if ((mod369.getRegime() == Mod369Regime.UNION || mod369.getRegime() == Mod369Regime.OUTSIDE) && !mod369.getPeriod().isQuarterPeriod()) {
				labelWarning.setText("EL PERIODO PARA ESTE REGIMEN DEBE SER TRIMESTRAL");
				labelWarning.setVisible(true);
				acceptButton.setEnabled(true);			
			}
			else if (mod369.getRegime() == Mod369Regime.IMPORT && !mod369.getPeriod().isMonthPeriod()) {
				labelWarning.setText("EL PERIODO PARA ESTE REGIMEN DEBE SER MENSUAL");
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
