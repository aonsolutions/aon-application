// CIFRA DE NEGOCIOS, PERSONAL ASALARIADO, SECRETARIO, GRUPO FISCAL O MERCANTIL
package com.esferalia.aon.gwt.mod200.client.mod200.e2023;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CountryListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.mod200.client.mod200.e2023.Model2002023.Model2002023PageCallback;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.mod200.api.model.DoubleVariableEx;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

public class Page01 extends PageAbs {
	
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
		
		// CIFRA DE NEGOCIOS
		
		basePanel.add(getTitle("Cifra de negocios"));
		
		FlexTable tableVol = addTable();

		tableVol.setWidget(0, 0, new Label("Importe neto de la cifra de negocios de los doce meses anteriores a la fecha de inicio del periodo impositivo"));
		
		// Para este año existen solo 0, 1 y 2, excepto cooperativas, que tienen todos los valores
		ListBox opeVol = new ListBox();
		opeVol.addItem("0 - No consta");
		opeVol.addItem("1 - Inferior a 20 millones de euros");
		if (callback.getMod200Object().getMod200().isChecked(Mod2002023Key.C0017) || callback.getMod200Object().getMod200().isChecked(Mod2002023Key.C0018) || callback.getMod200Object().getMod200().isChecked(Mod2002023Key.C0019)) {
			opeVol.addItem("2 - Al menos 20 millones de euros pero inferior a 60 millones de euros");
			opeVol.addItem("3 - Al menos 60 millones de euros");
		} else {
			opeVol.addItem("2 - Al menos 20 millones de euros");
		}
		DoubleVariableEx dv = callback.getMod200Object().getMod200().getKeysMap().get(Mod2002023Key.VOLOPE);
		int index = 0;
		if (dv != null) {
			index = dv.getValue().intValue();
		}
		opeVol.setSelectedIndex(index);
		opeVol.addChangeHandler( event -> {
			DoubleVariableEx bv = new DoubleVariableEx(Mod2002023Key.VOLOPE);
			bv.setValue((double)opeVol.getSelectedIndex());
			callback.getMod200Object().getMod200().addVariable(bv);
			callback.getMod200Object().doubleValueChanged(Mod2002023Key.VOLOPE, opeVol.getSelectedIndex());
			callback.markAsDirty();
		});
		otherInputs.add(opeVol);
		
		basePanel.add(opeVol);
		tableVol.setWidget(1, 0, opeVol);

		//paintFooterNote(basePanel, "Indique el importe neto de la cifra de negocios de los doce meses anteriores a la fecha de inicio del per\u00EDodo impositivo, a efectos de determinar, si proceden, la aplicaci\u00F3n de la tributaci\u00F3n m\u00EDnima, los l\u00EDmites de compensaci\u00F3n de bases imponibles negativas, correcciones contables sujetas al l\u00EDmite del art. 11.12 LIS y/o los l\u00EDmites para las deducciones por doble imposici\u00F3n previstos en los art\u00EDculos 30 bis, 31, 32, 100.11 y DT 23\u00AA LIS.");
		paintFooterNote(basePanel, "Indique el importe neto de la cifra de negocios de los doce meses anteriores a la fecha de inicio del per\u00EDodo impositivo, a efectos de determinar, si procede, la aplicaci\u00F3n de la tributaci\u00F3n m\u00EDnima (art\u00EDculo 30 bis LIS)");
		
		// PERSONAL ASALARIADO
		
		basePanel.add(getTitle("Personal asalariado"));
		
		AonDisplayTable tab31 = new AonDisplayTable();
		tab31.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab31.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab31);
		
		AonDoubleBox c041 = new AonDoubleBox();
		c041.setMaxLength(8);
		c041.setVisibleLength(8);
		c041.setValue(callback.getMod200Object().getMod200().getDoubleValue(Mod2002023Key.C0041));
		c041.addValueChangeHandler(event -> {			
			callback.getMod200Object().getMod200().setDoubleValue(Mod2002023Key.C0041, c041.getValue());
			callback.markAsDirty();
		});
		otherInputs.add(c041);
		
		AonDoubleBox c042 = new AonDoubleBox();	
		c042.setMaxLength(8);
		c042.setVisibleLength(8);	
		c042.setValue(callback.getMod200Object().getMod200().getDoubleValue(Mod2002023Key.C0042));
		c042.addValueChangeHandler(event -> {
			callback.getMod200Object().getMod200().setDoubleValue(Mod2002023Key.C0042, c042.getValue());
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
		
// ESTE DATO NO APARECE ESTE AÑO 		
//		AonDateBox irnr = new AonDateBox();
//		irnr.setValue(callback.getMod200Object().getMod200().getSecretary().getIrnr());
//		irnr.addValueChangeHandler(event -> {
//			callback.getMod200Object().getMod200().getSecretary().setIrnr(irnr.getValue());
//			callback.markAsDirty();
//		});
//		otherInputs.add(irnr);
		
		tab1.addLabelWidgetRow(AON.MSG.document(), secretaryDocument)
	        .addLabelWidgetRow("Apellidos y Nombre", secretaryName); // SE CAMBIA A "Apellidos y Nombre" ANTES SOLO PONIA Nombre 
// 		    .addLabelWidgetRow(AON.MSG.irnrDate(), irnr);
		
		// GRUPO FISCAL (solo habilitados si caracteres 9 o 10 marcados)
		
		AonTextBox fiscalGroup = new AonTextBox();
		AonDocumentTextBox dominantDocument = new AonDocumentTextBox();
		AonTextBox dominantIdentificationNumber = new AonTextBox();
		
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
				.addCell(new Label("NIF de la entidad representante/dominante (incluida en el grupo fiscal)"), AON.CSS.aonWidth400())
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
					.addCell(new Label("N\u00BA identificaci\u00F3n de la entidad dominante (en el caso de grupos constituidos s\u00F3lo por entidades dependientes)"), AON.CSS.aonWidth400()) 
					.addCell(dominantIdentificationNumber);		    
			}
		}
		
		// GRUPO MERCANTIL (solo habilitados si caracter 81 marcado)
		
		AonDocumentTextBox ultimateDocument = new AonDocumentTextBox();           
		CountryListBox ultimateDocumentCountry = new CountryListBox();
		AonTextBox ultimateName = new AonTextBox();				
		CountryListBox ultimateCountry = new CountryListBox();
		
		if (callback.getMod200Object().getMod200().isChecked(Mod2002023Key.C0081)) {
		
			basePanel.add(getTitle("Grupo mercantil"));
			paintLabel(basePanel, "Datos de la sociedad matriz \u00FAltima:", false);
			
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
			    .addCell(new Label("NIF o equivalente"), AON.CSS.aonWidth200())
				.addCell(ultimateDocument);
			tab3.addRow()
				.addCell(new Label("C\u00F3digo pa\u00EDs"), AON.CSS.aonWidth200())
				.addCell(ultimateDocumentCountry);
			tab3.addRow()
				.addCell(new Label("Nombre o raz\u00F3n social"), AON.CSS.aonWidth200())
				.addCell(ultimateName);
			tab3.addRow()
				.addCell(new Label("Pa\u00EDs o jurisdicci\u00F3n de residencia fiscal"), AON.CSS.aonWidth200())
				.addCell(ultimateCountry);
		
		}
		
	}
	
}
