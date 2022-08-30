// COMUNICACION IMPORTE NETO CIFRA DE NEGOCIO
package com.esferalia.aon.gwt.mod200.client.mod200.e2021;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CountryListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.mod200.client.mod200.e2021.Model2002021.Model200PageCallback;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.mod200.api.model.GroupEntitie;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021Key;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

public class Page19 extends PageAbs {

	public Page19( Model200PageCallback callback ) {
		super(callback);
	}

	@Override
	protected void initializeTable() {
		paint();
	}

	@Override
	protected boolean isAvailable() {
		boolean av = super.isAvailable() && 
				     (callback.getMod200Object().getMod200().isChecked(Mod2002021Key.C0021) || 
				      callback.getMod200Object().getMod200().isChecked(Mod2002021Key.C0039));
		return av;
	}
	
	private void paint() {
		
		basePanel.clear();
		
		// Grupos de sociedades, art. 42 código de comercio, incluidas entidades de crédito y aseguradoras
		
		FlexTable tab1 = addTable(AON.MSG.bussinessAmount1() + " (*)");		
		paintKey(tab1, Mod2002021Key.CN987, 0);
		
		AonDisplayTable tab2 = new AonDisplayTable();
		tab2.setWidth("50%");
		tab2.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab2);
		
		tab2.addRow()
			.addCell( new Label(AON.MSG.bussinessAmount11()+ " (**)"),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth150())
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
			otherInputs.add(document);
			
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
			otherInputs.add(country);
			
			// Boton borrar linea
			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(event -> {
				callback.getMod200Object().getMod200().getGroupEntities().remove(idx);
				paint();
				callback.markAsDirty();
			});
			otherInputs.add(deleteButton);

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
		otherInputs.add(addButton2);
		tab2.addRow().addCell(addButton2);
		
		paintFooterNote(basePanel, "(*) Grupos mercantiles con entidad dominante residente en territorio espa\u00F1ol; s\u00F3lo deber\u00E1 cumplimentar el cuadro dicha entidad dominante.");
		paintFooterNote(basePanel, "(**) NIF de las entidades del grupo (o equivalente al NIF del pa\u00EDs de residencia, si no tiene NIF en Espa\u00F1a) (excepto el de la entidad declarante)");
		
		// No residentes con más de un establecimiento permanente
		
		FlexTable tab3 = addTable(AON.MSG.bussinessAmount2());
		paintKey(tab3, Mod2002021Key.CN988, 0);
		paintKey(tab3, Mod2002021Key.CNEST, 1);
		
		AonDisplayTable tab4 = new AonDisplayTable();
		tab4.setWidth("30%");
		tab4.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab4);
		
		tab4.addRow()
			.addCell( new Label(AON.MSG.bussinessAmount12() + " (*)"),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth150())			
			.addCell( new Label(""),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth20());
		
		for (int i = 0; i < callback.getMod200Object().getMod200().getEstablishments().size(); i++) {
			final int idx = i;
			
			AonDocumentTextBox document = new AonDocumentTextBox();			
			document.setValue(callback.getMod200Object().getMod200().getEstablishments().get(idx));
			document.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getEstablishments().set(idx, document.getValue());
				callback.markAsDirty();
			});
			otherInputs.add(document);
			
			// Boton borrar linea
			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(event -> {
				callback.getMod200Object().getMod200().getEstablishments().remove(idx);
				paint();
				callback.markAsDirty();
			});
			otherInputs.add(deleteButton);

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
		otherInputs.add(addButton4);
		tab4.addRow().addCell(addButton4);		
		
		paintFooterNote(basePanel, "(*) NIF de los establecimientos permanentes, en caso de entidad titular (excepto el del establecimiento permanente al que se refiere esta declaraci\u00F3n).");
		
	}

}
