// SECRETARIO, GRUPOS FISCALES, REPRESENTANTES, ADMINISTRADORES.
package com.esferalia.aon.gwt.mod200.client.mod200.e2021;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CountryListBox;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.mod200.client.mod200.e2021.Model2002021.Model200PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.occam.mod200.api.model.Mod200CompanyAdministrator;
import com.esferalia.aon.occam.mod200.api.model.Secretary;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021Key;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;

public class Page01 extends PageAbs {

	private AonDocumentTextBox secretaryDocument = new AonDocumentTextBox();
	private AonTextBox secretaryName = new AonTextBox();
	private AonDateBox irnr = new AonDateBox();
	private AonTextBox fiscalGroup = new AonTextBox();
	private AonDocumentTextBox dominantDocument = new AonDocumentTextBox();
	private AonTextBox dominantIdentificationNumber = new AonTextBox();
	private AonDocumentTextBox ultimateDocument = new AonDocumentTextBox();           
	private CountryListBox ultimateDocumentCountry = new CountryListBox();
	private AonTextBox ultimateName = new AonTextBox();;				
	private CountryListBox ultimateCountry = new CountryListBox(); 

	public Page01( Model200PageCallback callback ) {
		super(callback);
		addBasePanel();
		initializeTable();
		paint();
	}

	@Override
	public void dump() {
		super.dump();
				
		this.fiscalGroup.setValue( callback.getMod200Object().getMod200().getFiscalGroup());
		this.dominantDocument.setValue(callback.getMod200Object().getMod200().getDominantDocument());
		this.dominantIdentificationNumber.setValue(callback.getMod200Object().getMod200().getDominantIdentificationNumber());

		this.ultimateDocument.setValue(callback.getMod200Object().getMod200().getUltimateDocument());
		this.ultimateDocumentCountry.setValue(callback.getMod200Object().getMod200().getUltimateDocumentCountry());
		
	    this.ultimateName.setValue(callback.getMod200Object().getMod200().getUltimateName());
		this.ultimateCountry.setValue(callback.getMod200Object().getMod200().getUltimateCountry());
	
		Secretary secretary = callback.getMod200Object().getMod200().getSecretary();
		if (secretary != null) {
			this.secretaryDocument.setValue(secretary.getDocument());
			this.secretaryName.setValue(secretary.getName());
			this.irnr.setValue(secretary.getIrnr());
		} else {
			this.secretaryDocument.setValue(null);
			this.secretaryName.setValue(null);
			this.irnr.setValue(null);
		}
		
	}
	
	@Override
	public void populate() {
		Secretary secretary = callback.getMod200Object().getMod200().getSecretary();
		if (secretary == null) {
			secretary = new Secretary();
			callback.getMod200Object().getMod200().setSecretary(secretary);	
		}
		secretary.setDocument(this.secretaryDocument.getValue());
		secretary.setName(this.secretaryName.getValue());
		secretary.setIrnr(this.irnr.getValue());
		
		callback.getMod200Object().getMod200().setFiscalGroup(this.fiscalGroup.getValue());
		callback.getMod200Object().getMod200().setDominantDocument(this.dominantDocument.getValue());
		callback.getMod200Object().getMod200().setDominantIdentificationNumber(dominantIdentificationNumber.getValue());
		
		callback.getMod200Object().getMod200().setUltimateDocument(this.ultimateDocument.getValue());         
	    callback.getMod200Object().getMod200().setUltimateName(this.ultimateName.getValue());
	}

	@Override
	protected void initializeTable() {
	}
	
	private void paint() {
		
		basePanel.clear();
		
		// SECRETARIO DEL CONSEJO DE ADMINISTRACION

		basePanel.add(getTitle(AON.MSG.secretaryData()));
		
		AonDisplayTable tab1 = new AonDisplayTable();
		tab1.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab1.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab1);
		
		secretaryDocument.addValueChangeHandler(event -> {
			callback.markAsDirty();
		});
		
		secretaryName.setVisibleLength(40);
		secretaryName.setMaxLength(25);
		secretaryName.addValueChangeHandler(event -> {
			callback.markAsDirty();
		});
		
		irnr.addValueChangeHandler(event -> {
			callback.markAsDirty();
		});
		
		tab1.addLabelWidgetRow(AON.MSG.document(), secretaryDocument)
		    .addLabelWidgetRow(AON.MSG.name(), secretaryName)
		    .addLabelWidgetRow(AON.MSG.irnrDate(), irnr);
		
		// GRUPO FISCAL (solo habilitados si caracteres 9 o 10 marcados)
		
		if (callback.getMod200Object().getMod200().isChecked(Mod2002021Key.C0009) || 
			callback.getMod200Object().getMod200().isChecked(Mod2002021Key.C0010)) {
		
			basePanel.add(getTitle(AON.MSG.fiscalGroupLabel()));
			
			AonDisplayTable tab2 = new AonDisplayTable();
			tab2.addStyleName(AON.CSS.aonWidthAlmostAll());
			tab2.addStyleName(AON.CSS.aonBlockCenter());
			basePanel.add(tab2);
					
			fiscalGroup.setVisibleLength(7);
			fiscalGroup.setMaxLength(7);
//			fiscalGroup.setEnabled(callback.getMod200Object().getMod200().isChecked(Mod2002021Key.C0009) || 
//					               callback.getMod200Object().getMod200().isChecked(Mod2002021Key.C0010));
			fiscalGroup.addValueChangeHandler(event -> {
				callback.markAsDirty();
			});
			
//			dominantDocument.setEnabled(fiscalGroup.isEnabled());
			dominantDocument.setMaxLength(9);
			dominantDocument.addValueChangeHandler(event -> {
				callback.markAsDirty();
			});
			
			tab2.addRow()
				.addCell(new Label(AON.MSG.fiscalGroup()), AON.CSS.aonWidth400())
				.addCell(fiscalGroup);
			tab2.addRow()
				.addCell(new Label(AON.MSG.groupDocument()), AON.CSS.aonWidth400())
				.addCell(dominantDocument);
			
			if (callback.getMod200Object().getMod200().isChecked(Mod2002021Key.C0010)) {
				dominantIdentificationNumber.setVisibleLength(15);
				dominantIdentificationNumber.setMaxLength(15);
//				dominantIdentificationNumber.setEnabled(callback.getMod200Object().getMod200().isChecked(Mod2002021Key.C0010));		
				dominantIdentificationNumber.addValueChangeHandler(event -> {
					callback.markAsDirty();
				});						
				
				tab2.addRow()
					.addCell(new Label(AON.MSG.dominantIdentificationNumber()), AON.CSS.aonWidth400())
					.addCell(dominantIdentificationNumber);		    
			}
		}
		
		// GRUPO MERCANTIL (solo habilitados si caracter 81 marcado)
		
		if (callback.getMod200Object().getMod200().isChecked(Mod2002021Key.C0081)) {
		
			basePanel.add(getTitle("Grupo mercantil"));
			
			AonDisplayTable tab3 = new AonDisplayTable();
			tab3.addStyleName(AON.CSS.aonWidthAlmostAll());
			tab3.addStyleName(AON.CSS.aonBlockCenter());
			basePanel.add(tab3);
			
//			ultimateDocument.setEnabled(callback.getMod200Object().getMod200().isChecked(Mod2002021Key.C0081));
			ultimateDocument.addValueChangeHandler(event -> {
				callback.markAsDirty();
			});
			
			ultimateDocumentCountry.setWidth("240px");
//			ultimateDocumentCountry.setEnabled(callback.getMod200Object().getMod200().isChecked(Mod2002021Key.C0081));
			ultimateDocumentCountry.addChangeHandler(new ChangeHandler() {			
				@Override
				public void onChange(ChangeEvent event) {
					callback.getMod200Object().getMod200().setUltimateDocumentCountry(Country.safeValueOf(ultimateDocumentCountry.getSelectedValue()));
					callback.markAsDirty();
				}
			});
			
//			ultimateName.setEnabled(callback.getMod200Object().getMod200().isChecked(Mod2002021Key.C0081));
			ultimateName.setVisibleLength(40); 
			ultimateName.setMaxLength(40);
			ultimateName.addValueChangeHandler(event -> {
				callback.markAsDirty();
			});
			
			ultimateCountry.setWidth("240px");
//			ultimateCountry.setEnabled(callback.getMod200Object().getMod200().isChecked(Mod2002021Key.C0081));
			ultimateCountry.addChangeHandler(new ChangeHandler() {			
				@Override
				public void onChange(ChangeEvent event) {
					callback.getMod200Object().getMod200().setUltimateCountry(Country.safeValueOf(ultimateCountry.getSelectedValue()));
					callback.markAsDirty();
				}
			});
			
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
		
		AonDisplayTable tab4 = new AonDisplayTable();
		tab4.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab4.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab4);
		
		tab4.addRow()
			.addCell( new Label(AON.MSG.document()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth100())
			.addCell( new Label(AON.MSG.nameAndSurname()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth300())
			.addCell( new Label(AON.MSG.notary()+"/Otros"),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth200())
			.addCell( new Label(AON.MSG.registrationDate()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth100())
			.addCell( new Label(""),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth20());
		
		for (int i = 0; i < callback.getMod200Object().getMod200().getRepresentatives().size(); i++) {
			final int idx = i;
			
			AonDocumentTextBox document = new AonDocumentTextBox();			
			document.setValue(callback.getMod200Object().getMod200().getRepresentatives().get(idx).getDocument());
			document.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getRepresentatives().get(idx).setDocument(document.getValue());
				callback.markAsDirty();
			});
			
			AonTextBox name = new AonTextBox();
			name.setMaxLength(45);
			name.setVisibleLength(45);			
			name.setValue(callback.getMod200Object().getMod200().getRepresentatives().get(idx).getName());
			name.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getRepresentatives().get(idx).setName(name.getValue());
				callback.markAsDirty();
			});
			
			AonTextBox notary = new AonTextBox();
			notary.setMaxLength(20);
			notary.setValue(callback.getMod200Object().getMod200().getRepresentatives().get(idx).getNotary());
			notary.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getRepresentatives().get(idx).setNotary(notary.getValue());
				callback.markAsDirty();
			});
			
			AonDateBox notaryDate = new AonDateBox();
			notaryDate.setValue(callback.getMod200Object().getMod200().getRepresentatives().get(idx).getNotaryDate());
			notaryDate.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getRepresentatives().get(idx).setNotaryDate(notaryDate.getValue());
				callback.markAsDirty();
			});
			
			// Boton borrar linea
			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(event -> {
				callback.getMod200Object().getMod200().getRepresentatives().remove(idx);
				paint();
				callback.markAsDirty();
			});

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
				AonMessageDialog.warning("No se pueden poner en el modelo mas de tres representantes legales.");
			} else {		
				callback.getMod200Object().getMod200().getRepresentatives().add(new LegalRepresentative());
				paint();
			}
		});
		basePanel.add(addButton1);
		
		// RELACION DE ADMINISTRADORES
		
		basePanel.add(getTitle(AON.MSG.administratorList()));
		
		AonDisplayTable tab5 = new AonDisplayTable();
		tab5.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab5.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab5);
		
		tab5.addRow()
			.addCell( new Label(AON.MSG.document()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth100())
			.addCell( new Label("Rpte."),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth40())			
			.addCell( new Label(AON.MSG.companyName()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth300())
			.addCell( new Label(AON.MSG.fiscalAddress()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth200())
			.addCell( new Label(AON.MSG.province()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth200())
			.addCell( new Label(""),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth20());
		
		for (int i = 0; i < callback.getMod200Object().getMod200().getAdministrators().size(); i++) {
			final int idx = i;
			
			AonDocumentTextBox document = new AonDocumentTextBox();			
			document.setValue(callback.getMod200Object().getMod200().getAdministrators().get(idx).getDocument());
			document.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getAdministrators().get(idx).setDocument(document.getValue());
				callback.markAsDirty();
			});
			
			CheckBox rep = new CheckBox();
			rep.setValue(callback.getMod200Object().getMod200().getAdministrators().get(idx).isRepresentative());
			rep.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					callback.getMod200Object().getMod200().getAdministrators().get(idx).setRepresentative(rep.getValue());
					callback.markAsDirty();
				}
			});
			
			AonTextBox name = new AonTextBox();
			name.setMaxLength(40);  
			name.setVisibleLength(45);			
			name.setValue(callback.getMod200Object().getMod200().getAdministrators().get(idx).getName());
			name.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getAdministrators().get(idx).setName(name.getValue());
				callback.markAsDirty();
			});
			
			AonTextBox address = new AonTextBox();
			address.setMaxLength(17); 
			address.setValue(callback.getMod200Object().getMod200().getAdministrators().get(idx).getResidence());
			address.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getAdministrators().get(idx).setResidence(address.getValue());
				callback.markAsDirty();
			});
			
			ProvinceListBox province = new ProvinceListBox();
			province.setValue(Province.safeValueOf(callback.getMod200Object().getMod200().getAdministrators().get(idx).getProvince()));
			province.addChangeHandler(new ChangeHandler() {			
				@Override
				public void onChange(ChangeEvent event) {
					callback.getMod200Object().getMod200().getAdministrators().get(idx).setProvince(province.getSelectedIndex());
					callback.markAsDirty();
				}
			});

			// Boton borrar linea
			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(event -> {
				callback.getMod200Object().getMod200().getAdministrators().remove(idx);
				paint();
				callback.markAsDirty();
			});

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
		});
		basePanel.add(addButton2);
		
	}
	
}
