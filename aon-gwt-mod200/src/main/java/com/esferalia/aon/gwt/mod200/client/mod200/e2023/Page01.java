// SECRETARIO, GRUPO FISCAL O MERCANTIL, REPRESENTANTES, ADMINISTRADORES.
package com.esferalia.aon.gwt.mod200.client.mod200.e2023;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CountryListBox;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.mod200.client.mod200.e2023.Model2002023.Model2002023PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.occam.mod200.api.model.Mod200CompanyAdministrator;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;

public class Page01 extends PageAbs {

	private AonDocumentTextBox secretaryDocument;
	private AonTextBox secretaryName;
	private AonDateBox irnr;
	private AonTextBox fiscalGroup;
	private AonDocumentTextBox dominantDocument;
	private AonTextBox dominantIdentificationNumber;
	private AonDocumentTextBox ultimateDocument;           
	private CountryListBox ultimateDocumentCountry;
	private AonTextBox ultimateName;				
	private CountryListBox ultimateCountry;
	
	public Page01( Model2002023PageCallback callback ) {
		super(callback);
	}

	@Override
	protected void initializeTable() {
		paint();
	}
	
	private void paint() {
		
		otherInputs.clear();		
		basePanel.clear();
		
		// SECRETARIO DEL CONSEJO DE ADMINISTRACION

		basePanel.add(getTitle(AON.MSG.secretaryData()));
		
		AonDisplayTable tab1 = new AonDisplayTable();
		tab1.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab1.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab1);
		
		secretaryDocument = new AonDocumentTextBox();
		secretaryDocument.setValue(callback.getMod200Object().getMod200().getSecretary().getDocument());
		secretaryDocument.addValueChangeHandler(event -> {
			callback.getMod200Object().getMod200().getSecretary().setDocument(secretaryDocument.getValue());
			callback.markAsDirty();
		});
		otherInputs.add(secretaryDocument);
		
		secretaryName = new AonTextBox();
		secretaryName.setVisibleLength(40);
		secretaryName.setMaxLength(25);
		secretaryName.setValue(callback.getMod200Object().getMod200().getSecretary().getName());
		secretaryName.addValueChangeHandler(event -> {
			callback.getMod200Object().getMod200().getSecretary().setName(secretaryName.getValue());
			callback.markAsDirty();
		});
		otherInputs.add(secretaryName);
		
		irnr = new AonDateBox();
		irnr.setValue(callback.getMod200Object().getMod200().getSecretary().getIrnr());
		irnr.addValueChangeHandler(event -> {
			callback.getMod200Object().getMod200().getSecretary().setIrnr(irnr.getValue());
			callback.markAsDirty();
		});
		otherInputs.add(irnr);
		
		tab1.addLabelWidgetRow(AON.MSG.document(), secretaryDocument)
		    .addLabelWidgetRow(AON.MSG.name(), secretaryName)
		    .addLabelWidgetRow(AON.MSG.irnrDate(), irnr);
		
		// GRUPO FISCAL (solo habilitados si caracteres 9 o 10 marcados)
		
		fiscalGroup = new AonTextBox();
		dominantDocument = new AonDocumentTextBox();
		dominantIdentificationNumber = new AonTextBox();
		
		if (callback.getMod200Object().getMod200().isChecked(Mod2002023Key.C0009) || 
			callback.getMod200Object().getMod200().isChecked(Mod2002023Key.C0010)) {
		
			basePanel.add(getTitle(AON.MSG.fiscalGroupLabel()));
			
			AonDisplayTable tab2 = new AonDisplayTable();
			tab2.addStyleName(AON.CSS.aonWidthAlmostAll());
			tab2.addStyleName(AON.CSS.aonBlockCenter());
			basePanel.add(tab2);
					
			fiscalGroup.setVisibleLength(7);
			fiscalGroup.setMaxLength(7);
			fiscalGroup.setValue( callback.getMod200Object().getMod200().getFiscalGroup());
			fiscalGroup.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().setFiscalGroup(fiscalGroup.getValue());
				callback.markAsDirty();
			});
			otherInputs.add(fiscalGroup);
			
			dominantDocument.setMaxLength(9);
			dominantDocument.setValue(callback.getMod200Object().getMod200().getDominantDocument());
			dominantDocument.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().setDominantDocument(dominantDocument.getValue());
				callback.markAsDirty();
			});
			otherInputs.add(dominantDocument);
			
			tab2.addRow()
				.addCell(new Label(AON.MSG.fiscalGroup()), AON.CSS.aonWidth400())
				.addCell(fiscalGroup);
			tab2.addRow()
				.addCell(new Label(AON.MSG.groupDocument()), AON.CSS.aonWidth400())
				.addCell(dominantDocument);
			
			if (callback.getMod200Object().getMod200().isChecked(Mod2002023Key.C0010)) {
				dominantIdentificationNumber.setVisibleLength(15);
				dominantIdentificationNumber.setMaxLength(15);
				dominantIdentificationNumber.setValue(callback.getMod200Object().getMod200().getDominantIdentificationNumber());
				dominantIdentificationNumber.addValueChangeHandler(event -> {
					callback.getMod200Object().getMod200().setDominantIdentificationNumber(dominantIdentificationNumber.getValue());
					callback.markAsDirty();
				});					
				otherInputs.add(dominantIdentificationNumber);
				
				tab2.addRow()
					.addCell(new Label(AON.MSG.dominantIdentificationNumber()), AON.CSS.aonWidth400())
					.addCell(dominantIdentificationNumber);		    
			}
		}
		
		// GRUPO MERCANTIL (solo habilitados si caracter 81 marcado)
		
		ultimateDocument = new AonDocumentTextBox();           
		ultimateDocumentCountry = new CountryListBox();
		ultimateName = new AonTextBox();				
		ultimateCountry = new CountryListBox();
		
		if (callback.getMod200Object().getMod200().isChecked(Mod2002023Key.C0081)) {
		
			basePanel.add(getTitle("Grupo mercantil"));
			
			AonDisplayTable tab3 = new AonDisplayTable();
			tab3.addStyleName(AON.CSS.aonWidthAlmostAll());
			tab3.addStyleName(AON.CSS.aonBlockCenter());
			basePanel.add(tab3);
			
			ultimateDocument.setValue(callback.getMod200Object().getMod200().getUltimateDocument());
			ultimateDocument.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().setUltimateDocument(ultimateDocument.getValue());         
				callback.markAsDirty();
			});
			otherInputs.add(ultimateDocument);
			
			ultimateDocumentCountry.setWidth("240px");
			ultimateDocumentCountry.setValue(callback.getMod200Object().getMod200().getUltimateDocumentCountry());
			ultimateDocumentCountry.addChangeHandler( event -> {
				callback.getMod200Object().getMod200().setUltimateDocumentCountry(Country.safeValueOf(ultimateDocumentCountry.getSelectedValue()));
				callback.markAsDirty();				
			});
			otherInputs.add(ultimateDocumentCountry);
			
			ultimateName.setVisibleLength(40); 
			ultimateName.setMaxLength(40);
		    ultimateName.setValue(callback.getMod200Object().getMod200().getUltimateName());
			ultimateName.addValueChangeHandler(event -> {
			    callback.getMod200Object().getMod200().setUltimateName(ultimateName.getValue());
				callback.markAsDirty();
			});
			otherInputs.add(ultimateName);
			
			ultimateCountry.setWidth("240px");
			ultimateCountry.setValue(callback.getMod200Object().getMod200().getUltimateCountry());
			ultimateCountry.addChangeHandler( event -> {						
				callback.getMod200Object().getMod200().setUltimateCountry(Country.safeValueOf(ultimateCountry.getSelectedValue()));
				callback.markAsDirty();				
			});
			otherInputs.add(ultimateCountry);
			
			tab3.addRow()
				.addCell(new Label(AON.MSG.ultimateDocument()), AON.CSS.aonWidth400())
				.addCell(ultimateDocument);
			tab3.addRow()
				.addCell(new Label(AON.MSG.ultimateDocumentCountry()), AON.CSS.aonWidth400())
				.addCell(ultimateDocumentCountry);
			tab3.addRow()
				.addCell(new Label(AON.MSG.ultimateName()), AON.CSS.aonWidth400())
				.addCell(ultimateName);
			tab3.addRow()
				.addCell(new Label(AON.MSG.ultimateCountry()), AON.CSS.aonWidth400())
				.addCell(ultimateCountry);
		
		}
		
		// REPRESENTANTES LEGALES DE LA ENTIDAD
		
		basePanel.add(getTitle(AON.MSG.legalRepresentativeData()));
		
		AonDisplayTable tab4 = addRegistryTable(AON.MSG.document(), AON.MSG.nameAndSurname(), AON.MSG.notary()+"/Otros", AON.MSG.registrationDate());
		
		for (int i = 0; i < callback.getMod200Object().getMod200().getRepresentatives().size(); i++) {
			final int idx = i;
			
			AonDocumentTextBox document = new AonDocumentTextBox();			
			document.setValue(callback.getMod200Object().getMod200().getRepresentatives().get(idx).getDocument());
			document.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getRepresentatives().get(idx).setDocument(document.getValue());
				callback.markAsDirty();
			});
			otherInputs.add(document);
			
			AonTextBox name = new AonTextBox();
			name.setMaxLength(45);
			name.setVisibleLength(45);			
			name.setValue(callback.getMod200Object().getMod200().getRepresentatives().get(idx).getName());
			name.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getRepresentatives().get(idx).setName(name.getValue());
				callback.markAsDirty();
			});
			otherInputs.add(name);
			
			AonTextBox notary = new AonTextBox();
			notary.setMaxLength(20);
			notary.setValue(callback.getMod200Object().getMod200().getRepresentatives().get(idx).getNotary());
			notary.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getRepresentatives().get(idx).setNotary(notary.getValue());
				callback.markAsDirty();
			});
			otherInputs.add(notary);
			
			AonDateBox notaryDate = new AonDateBox();
			notaryDate.setValue(callback.getMod200Object().getMod200().getRepresentatives().get(idx).getNotaryDate());
			notaryDate.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getRepresentatives().get(idx).setNotaryDate(notaryDate.getValue());
				callback.markAsDirty();
			});
			otherInputs.add(notaryDate);
			
			// Boton borrar linea
			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(event -> {
				callback.getMod200Object().getMod200().getRepresentatives().remove(idx);
				paint();
				callback.markAsDirty();
			});
			otherInputs.add(deleteButton);

			tab4.addRow()
				.addCell( document )
				.addCell( name )
				.addCell( notary )
				.addCell( notaryDate )				
				.addCell( deleteButton );
		}
		
		// Botón añadir representante legal
		AonTableButton addButton1 = new AonTableButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
		addButton1.addStyleName(AON.CSS.aonMarginTop());
		addButton1.addStyleName(AON.CSS.aonMarginLeft());
		addButton1.addClickHandler(event -> {
			// No puede haber mas de 3 representantes legales
			if (callback.getMod200Object().getMod200().getRepresentatives().size() == 3) {
				AonMessageDialog.warning("No puede haber en el modelo mas de tres representantes legales.");
			} else {		
				callback.getMod200Object().getMod200().getRepresentatives().add(new LegalRepresentative());
				paint();
				callback.markAsDirty();
			}
		});
		otherInputs.add(addButton1);
		basePanel.add(addButton1);
		
		// RELACION DE ADMINISTRADORES
		
		basePanel.add(getTitle(AON.MSG.administratorList()));
		
		AonDisplayTable tab5 = addRegistryTable(AON.MSG.document(), "Rpte.", "Apellidos y nombre o raz\u00F3n social", AON.MSG.fiscalAddress(), AON.MSG.province());
		
		for (int i = 0; i < callback.getMod200Object().getMod200().getAdministrators().size(); i++) {
			final int idx = i;
			
			AonDocumentTextBox document = new AonDocumentTextBox();			
			document.setValue(callback.getMod200Object().getMod200().getAdministrators().get(idx).getDocument());
			document.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getAdministrators().get(idx).setDocument(document.getValue());
				callback.markAsDirty();
			});
			otherInputs.add(document);
			
			CheckBox rep = new CheckBox();			
			rep.setValue(callback.getMod200Object().getMod200().getAdministrators().get(idx).isRepresentative());
			rep.addClickHandler( event -> { 
				callback.getMod200Object().getMod200().getAdministrators().get(idx).setRepresentative(rep.getValue());
				callback.markAsDirty();				
			});
			otherInputs.add(rep);
			
			AonTextBox name = new AonTextBox();
			name.setMaxLength(40);  
			name.setVisibleLength(45);	
			name.setValue(callback.getMod200Object().getMod200().getAdministrators().get(idx).getName());
			name.addValueChangeHandler( event -> {
				callback.getMod200Object().getMod200().getAdministrators().get(idx).setName(name.getValue());
				callback.markAsDirty();
			});
			otherInputs.add(name);
			
			AonTextBox address = new AonTextBox();
			address.setMaxLength(17); 
			address.setValue(callback.getMod200Object().getMod200().getAdministrators().get(idx).getResidence());
			address.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getAdministrators().get(idx).setResidence(address.getValue());
				callback.markAsDirty();
			});
			otherInputs.add(address);
			
			ProvinceListBox province = new ProvinceListBox();
			province.setValue(Province.safeValueOf(callback.getMod200Object().getMod200().getAdministrators().get(idx).getProvince()));
			province.addChangeHandler(event -> {
				callback.getMod200Object().getMod200().getAdministrators().get(idx).setProvince(province.getSelectedIndex());
				callback.markAsDirty();
			});
			otherInputs.add(province);

			// Boton borrar linea
			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(event -> {
				callback.getMod200Object().getMod200().getAdministrators().remove(idx);
				paint();
				callback.markAsDirty();
			});
			otherInputs.add(deleteButton);

			tab5.addRow()
				.addCell(document)
				.addCell(rep)
				.addCell(name)
				.addCell(address)
				.addCell(province)				
				.addCell(deleteButton);
		}
		
		// Botón añadir administrador
		AonTableButton addButton2 = new AonTableButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
		addButton2.addStyleName(AON.CSS.aonMarginTop());
		addButton2.addStyleName(AON.CSS.aonMarginLeft());
		addButton2.addClickHandler(event -> {
			callback.getMod200Object().getMod200().getAdministrators().add(new Mod200CompanyAdministrator());
			paint();
			callback.markAsDirty();
		});
		otherInputs.add(addButton2);
		basePanel.add(addButton2);
		
	}
	
}
