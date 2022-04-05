// COMUNICACION IMPORTE NETO CIFRA DE NEGOCIO
package com.esferalia.aon.gwt.mod200.client.mod200.e2020;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CountryListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.mod200.client.mod200.e2020.Model2002020.Model200PageCallback;
import com.esferalia.aon.occam.api.model.GroupEntitie;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020Key;
import com.esferalia.aon.occam.api.model.type.Country;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

public class Page19 extends PageAbs {

	
	public Page19( Model200PageCallback callback ) {
		super(callback);
		addBasePanel();
		initializeTable();		
	}

	@Override
	protected void initializeTable() {
		paint();
	}

	@Override
	protected void dump() {
		super.dump();
	}
	
	@Override
	protected void populate() {
	}
	
	@Override
	protected boolean isAvailable() {
		boolean av = super.isAvailable()
  		  && (callback.getMod200Object().getMod200().isChecked(Mod2002020Key.C0021) 
		   || callback.getMod200Object().getMod200().isChecked(Mod2002020Key.C0039))
  		  ;
		return av;
	}
	
	private void paint() {
		
		basePanel.clear();
		
		// Grupos de sociedades, art. 42 código de comercio, incluidas entidades de crédito y aseguradoras
		
		FlexTable tab1 = addTable(AON.MSG.bussinessAmount1());		
		paintKey(tab1, Mod2002020Key.CN987, 0);
		
		AonDisplayTable tab2 = new AonDisplayTable();
		tab2.setWidth("50%");
		tab2.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab2);
		
		tab2.addRow()
			.addCell( new Label(AON.MSG.bussinessAmount11()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth150())
			.addCell( new Label(AON.MSG.country()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth150())
			.addCell( new Label(""),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth20());
		
		for (int i = 0; i < callback.getMod200Object().getMod200().getGroupEntities().size(); i++) {
			final int idx = i;
			
			AonDocumentTextBox document = new AonDocumentTextBox();			
			document.setValue(callback.getMod200Object().getMod200().getGroupEntities().get(idx).getDocument());
			document.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getGroupEntities().get(idx).setDocument(document.getValue());
				callback.markAsDirty();
			});
			
			CountryListBox country = new CountryListBox();
			country.setWidth("140px");
			country.setValue(Country.safeValueOf(callback.getMod200Object().getMod200().getGroupEntities().get(idx).getCountry()));
			country.addChangeHandler(new ChangeHandler() {			
				@Override
				public void onChange(ChangeEvent event) {
					callback.getMod200Object().getMod200().getGroupEntities().get(idx).setCountry(Country.safeIso2(country.getValue()));
					callback.markAsDirty();
				}
			});
			
			// Boton borrar linea
			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(event -> {
				callback.getMod200Object().getMod200().getGroupEntities().remove(idx);
				paint();
				callback.markAsDirty();
			});

			tab2.addRow()
				.addCell(document)
				.addCell(country)
				.addCell(deleteButton);
			
		}
		
		// Botón añadir 
		AonTableButton addButton2 = new AonTableButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
		addButton2.addClickHandler(event -> {
			callback.getMod200Object().getMod200().getGroupEntities().add(new GroupEntitie());
			paint();
		});
		tab2.addRow().addCell(addButton2);
		
		// No residentes con más de un establecimiento permanente
		
		FlexTable tab3 = addTable(AON.MSG.bussinessAmount2());
		paintKey(tab3, Mod2002020Key.CN988, 0);
		paintKey(tab3, Mod2002020Key.CNEST, 1);
		
		AonDisplayTable tab4 = new AonDisplayTable();
		tab4.setWidth("30%");
		tab4.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab4);
		
		tab4.addRow()
			.addCell( new Label(AON.MSG.bussinessAmount12()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth150())			
			.addCell( new Label(""),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth20());
		
		for (int i = 0; i < callback.getMod200Object().getMod200().getEstablishments().size(); i++) {
			final int idx = i;
			
			AonDocumentTextBox document = new AonDocumentTextBox();			
			document.setValue(callback.getMod200Object().getMod200().getEstablishments().get(idx));
			document.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getEstablishments().set(idx, document.getValue());
				callback.markAsDirty();
			});
			
			// Boton borrar linea
			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(event -> {
				callback.getMod200Object().getMod200().getEstablishments().remove(idx);
				paint();
				callback.markAsDirty();
			});

			tab4.addRow()
				.addCell(document)
				.addCell(deleteButton);
			
		}
		
		// Botón añadir 
		AonTableButton addButton4 = new AonTableButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
		addButton4.addClickHandler(event -> {
			callback.getMod200Object().getMod200().getEstablishments().add(new String());
			paint();
		});
		tab4.addRow().addCell(addButton4);		
		
	}

}
