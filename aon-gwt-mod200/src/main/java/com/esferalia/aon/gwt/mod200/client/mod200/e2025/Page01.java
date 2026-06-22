// CIFRA DE NEGOCIOS, PERSONAL ASALARIADO, SECRETARIO, GRUPO FISCAL, GRUPO MERCANTIL
package com.esferalia.aon.gwt.mod200.client.mod200.e2025;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CountryListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.mod200.client.mod200.e2025.Model2002025.Model2002025PageCallback;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.mod200.api.model.DoubleVariableEx;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025Key;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

public class Page01 extends PageAbs {
	
	public Page01( Model2002025PageCallback callback ) {
		super(callback);
	}

	@Override
	protected void initializeTable() {
		paint();
	}
	
	private void paint() {
		
		otherInputs.clear();		
		basePanel.clear();
		
		// CIFRA DE NEGOCIOS
		
		basePanel.add(getTitle("Cifra de negocios"));
		
		FlexTable tableVol = addTable();

		tableVol.setWidget(0, 0, new Label("Importe neto de la cifra de negocios de los doce meses anteriores a la fecha de inicio del periodo impositivo"));
		
		ListBox opeVol = new ListBox();
		opeVol.addItem("0 - No consta");
		opeVol.addItem("1 - Inferior a 20 millones de euros");
		opeVol.addItem("2 - Al menos 20 millones de euros pero inferior a 60 millones de euros");
		opeVol.addItem("3 - Al menos 60 millones de euros");
		DoubleVariableEx dv = callback.getMod200Object().getMod200().getKeysMap().get(Mod2002025Key.VOLOPE);
		int index = 0;
		if (dv != null) {
			index = dv.getValue().intValue();
		}
		opeVol.setSelectedIndex(index);
		opeVol.addChangeHandler( event -> {
			DoubleVariableEx bv = new DoubleVariableEx(Mod2002025Key.VOLOPE);
			bv.setValue((double)opeVol.getSelectedIndex());
			callback.getMod200Object().getMod200().addVariable(bv);
			callback.getMod200Object().doubleValueChanged(Mod2002025Key.VOLOPE, opeVol.getSelectedIndex());
			callback.markAsDirty();
		});
		otherInputs.add(opeVol);
		
		basePanel.add(opeVol);
		tableVol.setWidget(1, 0, opeVol);

		paintFooterNote(basePanel, "Indique el importe neto de la cifra de negocios de los doce meses anteriores a la fecha de inicio del periodo impositivo, a efectos de determinar si proceden, la aplicaci\u00F3n de la tributaci\u00F3n m\u00EDnima del art. 30 bis LIS, los l\u00EDmites de compensaci\u00F3n de bases imponibles negativas, los l\u00EDmites de compensaci\u00F3n de cuotas en el r\u00E9gimen de cooperativas, las correcciones contables sujetas al l\u00EDmite del art. 11.12 LIS y/o los l\u00EDmites para las deducciones por doble imposici\u00F3n previstos en los arts, 31, 32, 100.10\u00BA y DT 23\u00AA LIS (l\u00EDmites aplicables de acuerdo con la DA 8\u00AA Ley 20/1990 y DA 15\u00AA LIS)");		
		
		// PERSONAL ASALARIADO
		
		basePanel.add(getTitle("Personal asalariado"));
		
		paintLabel(basePanel, "Consigne la cifra media del ejercicio", false);
		
		AonDisplayTable tab31 = new AonDisplayTable();
		tab31.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab31.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab31);
		
		AonDoubleBox c041 = new AonDoubleBox();
		c041.setMaxLength(8);
		c041.setVisibleLength(8);
		c041.setValue(callback.getMod200Object().getMod200().getDoubleValue(Mod2002025Key.C0041));
		c041.addValueChangeHandler(event -> {			
			callback.getMod200Object().getMod200().setDoubleValue(Mod2002025Key.C0041, c041.getValue());
			callback.markAsDirty();
		});
		otherInputs.add(c041);
		
		AonDoubleBox c042 = new AonDoubleBox();	
		c042.setMaxLength(8);
		c042.setVisibleLength(8);	
		c042.setValue(callback.getMod200Object().getMod200().getDoubleValue(Mod2002025Key.C0042));
		c042.addValueChangeHandler(event -> {
			callback.getMod200Object().getMod200().setDoubleValue(Mod2002025Key.C0042, c042.getValue());
			callback.markAsDirty();
		});
		otherInputs.add(c042);
		
		tab31.addLabelWidgetRow(AON.MSG.fixedPersonal(), c041)
 	    	 .addLabelWidgetRow(AON.MSG.nonFixedPersonal(), c042);
		
		// SECRETARIO DEL CONSEJO DE ADMINISTRACION

		basePanel.add(getTitle(AON.MSG.secretaryData()));
		
		AonDisplayTable tab1 = new AonDisplayTable();
		tab1.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab1.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab1);
		
		AonDocumentTextBox secretaryDocument = new AonDocumentTextBox();
		secretaryDocument.setValue(callback.getMod200Object().getMod200().getSecretary().getDocument());
		secretaryDocument.addValueChangeHandler(event -> {
			callback.getMod200Object().getMod200().getSecretary().setDocument(secretaryDocument.getValue());
			callback.markAsDirty();
		});
		otherInputs.add(secretaryDocument);
		
		AonTextBox secretaryName = new AonTextBox();
		secretaryName.setVisibleLength(40);
		secretaryName.setMaxLength(25);
		secretaryName.setValue(callback.getMod200Object().getMod200().getSecretary().getName());
		secretaryName.addValueChangeHandler(event -> {
			callback.getMod200Object().getMod200().getSecretary().setName(secretaryName.getValue());
			callback.markAsDirty();
		});
		otherInputs.add(secretaryName);
		
		tab1.addLabelWidgetRow(AON.MSG.document(), secretaryDocument)
	        .addLabelWidgetRow("Apellidos y Nombre", secretaryName);  
		
		// GRUPO FISCAL (solo habilitados si caracteres 9 o 10 marcados)
		
		AonTextBox fiscalGroup = new AonTextBox();
		AonDocumentTextBox dominantDocument = new AonDocumentTextBox();
		AonTextBox dominantIdentificationNumber = new AonTextBox();
		
		if (callback.getMod200Object().getMod200().isChecked(Mod2002025Key.C0009) || 
			callback.getMod200Object().getMod200().isChecked(Mod2002025Key.C0010)) {
		
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
				.addCell(new Label("NIF de la entidad representante/dominante (incluida en el grupo fiscal)"), AON.CSS.aonWidth400())
				.addCell(dominantDocument);
			
			if (callback.getMod200Object().getMod200().isChecked(Mod2002025Key.C0010)) {
				dominantIdentificationNumber.setVisibleLength(15);
				dominantIdentificationNumber.setMaxLength(15);
				dominantIdentificationNumber.setValue(callback.getMod200Object().getMod200().getDominantIdentificationNumber());
				dominantIdentificationNumber.addValueChangeHandler(event -> {
					callback.getMod200Object().getMod200().setDominantIdentificationNumber(dominantIdentificationNumber.getValue());
					callback.markAsDirty();
				});					
				otherInputs.add(dominantIdentificationNumber);
				
				tab2.addRow()
					.addCell(new Label("N\u00BA identificaci\u00F3n de la entidad dominante (en el caso de grupos constituidos s\u00F3lo por entidades dependientes)"), AON.CSS.aonWidth400()) 
					.addCell(dominantIdentificationNumber);		    
			}
		}
		
		// GRUPO MERCANTIL (solo habilitados si caracteres 81 o 82)
		
		AonDocumentTextBox ultimateDocument = new AonDocumentTextBox(false);           
		AonTextBox ultimateName = new AonTextBox();
		AonTextBox ultimateGroupName = new AonTextBox();
		CountryListBox ultimateResidenceCountry = new CountryListBox();
		AonDocumentTextBox ultimateResidenceDocument = new AonDocumentTextBox(false);
		
		if (isCheckedOr(Mod2002025Key.C0081,Mod2002025Key.C0082)) {
		
			basePanel.add(getTitle("Grupo mercantil"));
			
			paintLabel(basePanel, "Datos identificativos de la sociedad matriz \u00FAltima:", false);
			
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
			
			ultimateName.setVisibleLength(40); 
			ultimateName.setMaxLength(40);
		    ultimateName.setValue(callback.getMod200Object().getMod200().getUltimateName());
			ultimateName.addValueChangeHandler(event -> {
			    callback.getMod200Object().getMod200().setUltimateName(ultimateName.getValue());
				callback.markAsDirty();
			});
			otherInputs.add(ultimateName);
			
			ultimateGroupName.setVisibleLength(40); 
			ultimateGroupName.setMaxLength(40);
		    ultimateGroupName.setValue(callback.getMod200Object().getMod200().getUltimateGroupName());
			ultimateGroupName.addValueChangeHandler(event -> {
			    callback.getMod200Object().getMod200().setUltimateGroupName(ultimateGroupName.getValue());
				callback.markAsDirty();
			});
			otherInputs.add(ultimateGroupName);
			
			tab3.addRow()				
		    	.addCell(new Label("NIF"), AON.CSS.aonWidth200())
		    	.addCell(ultimateDocument);
			tab3.addRow()
				.addCell(new Label("Raz\u00F3n social"), AON.CSS.aonWidth200())
				.addCell(ultimateName);
			tab3.addRow()
				.addCell(new Label("Nombre de grupo"), AON.CSS.aonWidth200())
				.addCell(ultimateGroupName);
			
			paintLabel(basePanel, "Identificaci\u00F3n fiscal del pa\u00EDs de residencia:", false);
			
			AonDisplayTable tab4 = new AonDisplayTable();
			tab4.addStyleName(AON.CSS.aonWidthAlmostAll());
			tab4.addStyleName(AON.CSS.aonBlockCenter());
			basePanel.add(tab4);
			
			ultimateResidenceCountry.setWidth("240px");
			ultimateResidenceCountry.setValue(callback.getMod200Object().getMod200().getUltimateResidenceCountry());
			ultimateResidenceCountry.addChangeHandler( event -> {						
				callback.getMod200Object().getMod200().setUltimateResidenceCountry(Country.safeValueOf(ultimateResidenceCountry.getSelectedValue()));
				callback.markAsDirty();				
			});
			otherInputs.add(ultimateResidenceCountry);
			
			ultimateResidenceDocument.setValue(callback.getMod200Object().getMod200().getUltimateResidenceDocument());
			ultimateResidenceDocument.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().setUltimateResidenceDocument(ultimateResidenceDocument.getValue());         
				callback.markAsDirty();
			});
			otherInputs.add(ultimateResidenceDocument);
			
			tab4.addRow()
				.addCell(new Label("Pa\u00EDs de residencia"), AON.CSS.aonWidth200())
				.addCell(ultimateResidenceCountry);
			tab4.addRow()				
		    	.addCell(new Label("NIF en el pa\u00EDs de residencia (TIN)"), AON.CSS.aonWidth200())
		    	.addCell(ultimateResidenceDocument);
			
			paintFooterNote(basePanel, "Para entidades que hayan marcado las claves 00081 o 00082 de caracteres de la declaraci\u00F3n, a los efectos de los arts. 13 y 14 del Real Decreto 634/2015, de 10 de julio, por el que " +
										"se aprueba el Reglamento del Impuesto sobre Sociedades, y el art. 6 de la Ley 7/2024, de 20 de diciembre, por la que se establece un Impuesto Complementario para garantizar un nivel " +
										"m\u00EDnimo global de imposici\u00F3n para los grupos multinacionales y los grupos nacionales de gran magnitud.");
		}
		
	}
	
}
