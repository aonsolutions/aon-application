// DATOS IDENTIFICATIVOS Y DEVENGO
package com.esferalia.aon.gwt.fiscal.client.mod425.e2025;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod425.e2025.Model4252025.Model4252025Callback;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

class Page00 extends PageAbs {

	private AonDocumentTextBox document = new AonDocumentTextBox();     // NIF
	private AonTextBox name = new AonTextBox(); 					    // Apellidos y nombre o Razón Social
	private AonTextBox streetInitial = new AonTextBox();			    // S.G.
	private AonTextBox streetName = new AonTextBox();    			    // Nombre de la vía pública
	private AonTextBox streetNumber = new AonTextBox();  			    // Número  	
	private AonTextBox streetStair = new AonTextBox();   			    // Esc.
	private AonTextBox streetFloor = new AonTextBox();   			    // Piso
	private AonTextBox streetDoor = new AonTextBox(); 				    // Puerta
	private AonTextBox phone = new AonTextBox(); 					    // Teléfono
	private ProvinceListBox province = new ProvinceListBox(); 		    // Provincia
	private AonTextBox town = new AonTextBox(); 			  		    // Municipio
	private AonTextBox townCode = new AonTextBox(); 				    // Código Municipio
	private AonTextBox zip = new AonTextBox(); 						    // Código Postal
	private CheckBox taxRefund = new CheckBox(); 					    // Registro de devolución mensual en algún periodo del ejercicio
	private CheckBox repep = new CheckBox(); 						    // Régimen especial del pequeño empresario o profesional
	private CheckBox replacement = new CheckBox(); 					    // Declaración sustitutiva
	private CheckBox replacementDueInsolvencyState = new CheckBox();	// Declaración sustitutiva por rectificación de cuotas en caso de concurso de acreedores
	private AonTextBox replacedReceipt = new AonTextBox(); 			    // N.º de justificante de la declaración anterior
	
	public Page00(Model4252025Callback callback) {
		super(callback);
		paint();
		setValue();
	}

	protected void paint() {
		ScrollPanel scroll = new ScrollPanel();
		FlowPanel basePanel = new FlowPanel();
		scroll.add(basePanel);
		setWidget(scroll);
		
		basePanel.add(getTitle(AON.MSG.deponentData()));
		basePanel.add(getSubtitle(AON.MSG.identification()));
		
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab);
		
		document.setEnabled(false);		
		name.setVisibleLength(45);
		name.setMaxLength(45);
		
		tab.addLabelWidgetRow(AON.MSG.document(), document)
           .addLabelWidgetRow("Apellidos y nombre / Raz\u00F3n social", name);
		
		FlowPanel address1 = new FlowPanel();
		InlineLabel streetTypeLabel = new InlineLabel(AON.MSG.streetType());
		streetTypeLabel.setStyleName(AON.CSS.aonInnerLabel());
		address1.add(streetTypeLabel);
		streetInitial.setVisibleLength(2);
		streetInitial.setMaxLength(2);
		address1.add(streetInitial);
		InlineLabel streetNameLabel = new InlineLabel(AON.MSG.streetName());
		streetNameLabel.setStyleName(AON.CSS.aonInnerLabel());
		address1.add(streetNameLabel);
		streetName.setVisibleLength(30);
		streetName.setMaxLength(40);
		address1.add(streetName);
		tab.addRow()
			.addCell(new Label("Domicilio"))
			.addCell(address1);
		
		FlowPanel address2 = new FlowPanel();
		InlineLabel streetNumberLabel = new InlineLabel(AON.MSG.streetNumber());
		streetNumberLabel.setStyleName(AON.CSS.aonInnerLabel());
		address2.add(streetNumberLabel);
		streetNumber.setVisibleLength(5);
		streetNumber.setMaxLength(5);
		address2.add(streetNumber);
		InlineLabel streetStairLabel = new InlineLabel(AON.MSG.streetStair());
		streetStairLabel.setStyleName(AON.CSS.aonInnerLabel());
		address2.add(streetStairLabel);
		streetStair.setVisibleLength(2);
		streetStair.setMaxLength(2);
		address2.add(streetStair);
		InlineLabel streetFloorLabel = new InlineLabel(AON.MSG.streetFloor());
		streetFloorLabel.setStyleName(AON.CSS.aonInnerLabel());
		address2.add(streetFloorLabel);
		streetFloor.setVisibleLength(2);
		streetFloor.setMaxLength(2);
		address2.add(streetFloor);
		InlineLabel streetDoorLabel = new InlineLabel(AON.MSG.streetDoor());
		streetDoorLabel.setStyleName(AON.CSS.aonInnerLabel());
		address2.add(streetDoorLabel);
		streetDoor.setVisibleLength(2);
		streetDoor.setMaxLength(2);
		address2.add(streetDoor);
		tab.addRow()
			.addCell(new Label(),AON.CSS.aonTableLabel())
			.addCell(address2);
		
		town.setVisibleLength(35);
		town.setMaxLength(35);
		tab.addRow()
			.addCell(new Label(AON.MSG.town())) 
			.addCell(town);
		
		townCode.setVisibleLength(5);
		townCode.setMaxLength(5);
		tab.addRow()
			.addCell(new Label(AON.MSG.townCode())) 
			.addCell(townCode);

		tab.addRow()
			.addCell(new Label(AON.MSG.province())) 
			.addCell(province);

		zip.setVisibleLength(5);
		zip.setMaxLength(5);
		tab.addRow()
			.addCell(new Label(AON.MSG.zip())) 
			.addCell(zip);
		
		phone.setVisibleLength(9);
		phone.setMaxLength(9);
		tab.addRow()
			.addCell(new Label(AON.MSG.phone())) 
			.addCell(phone);
		
		AonDisplayTable tab1 = new AonDisplayTable();
		tab1.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab1.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab1);
		
		replacedReceipt.setVisibleLength(13);
		replacedReceipt.setMaxLength(13);
		
		tab1.addRow()
			.addCell(new Label(AON.MSG.taxRefund()), AON.CSS.aonWidth400())
		 	.addCell(taxRefund);
		tab1.addRow()
			.addCell(new Label("R\u00E9gimen especial del peque\u00F1o empresario o profesional"), AON.CSS.aonWidth400())
		 	.addCell(repep);
		tab1.addRow()
			.addCell(new Label("Declaraci\u00F3n sustitutiva"), AON.CSS.aonWidth400())
		 	.addCell(replacement);
		tab1.addRow()
			.addCell(new Label("Declaraci\u00F3n sustitutiva por rectificaci\u00F3n de cuotas en caso de concurso de acreedores"), AON.CSS.aonWidth400())
		 	.addCell(replacementDueInsolvencyState);
		tab1.addRow()
			.addCell(new Label("N\u00FAmero de justificante de la declaraci\u00F3n sustituida"), AON.CSS.aonWidth400())
		 	.addCell(replacedReceipt);
		
		name.addValueChangeHandler(event ->{
			getModel().setName(name.getValue());
			markAsDirty();
		});
		
		streetInitial.addValueChangeHandler(event -> {
			getModel().setStreetInitial(streetInitial.getValue());
			markAsDirty();
		});
		
		streetName.addValueChangeHandler(event -> {
			getModel().setStreetName(streetName.getValue());
			markAsDirty();
		});
		
		streetNumber.addValueChangeHandler(event -> {
			getModel().setStreetNumber(streetNumber.getValue());
			markAsDirty();
		});
		
		streetStair.addValueChangeHandler(event -> {
			getModel().setStreetStair(streetStair.getValue());
			markAsDirty();
		});
		
		streetFloor.addValueChangeHandler(event -> {
			getModel().setStreetFloor(streetFloor.getValue());
			markAsDirty();
		});
		
		streetDoor.addValueChangeHandler(event -> {
			getModel().setStreetDoor(streetDoor.getValue());
			markAsDirty();
		});
		
		town.addValueChangeHandler(event -> {
			getModel().setTown(town.getValue());
			markAsDirty();
		});
		
		province.addChangeHandler(event -> {
			getModel().setProvinceCode(AonStringUtils.leftPad(Integer.toString(province.getSelectedIndex()), 2, '0')); // Se graba el código de provincia 
			markAsDirty();
		});
		
		zip.addValueChangeHandler(event -> {
			getModel().setZip(zip.getValue());
			markAsDirty();
		});
		
		townCode.addValueChangeHandler(event -> {
			getModel().setTownCode(townCode.getValue());
			markAsDirty();
		});
		
		phone.addValueChangeHandler(event ->{
			getModel().setContactPhone(phone.getValue());
			markAsDirty();
		});
		
		taxRefund.addClickHandler(event ->{
			getModel().setTaxRefund(taxRefund.getValue());
			markAsDirty();
		});
		repep.addClickHandler(event ->{
			getModel().setSpecialRegime(repep.getValue());
			markAsDirty();
		});
		replacement.addClickHandler(event ->{
			getModel().setReplacement(replacement.getValue());
			markAsDirty();
		});
		replacementDueInsolvencyState.addClickHandler(event ->{
			getModel().setReplacementDueInsolvencyState(replacementDueInsolvencyState.getValue());
			markAsDirty();
		});
		replacedReceipt.addValueChangeHandler(event ->{
			getModel().setReplacedReceipt(replacedReceipt.getValue());
			markAsDirty();
		});
		
	}

	@Override
	protected void setValue() {
		
		document.setValue(getModel().getDocument(),false);
		name.setValue(getModel().getName(),false);
		streetInitial.setValue(getModel().getStreetInitial(),false);
		streetName.setValue(getModel().getStreetName(),false);
		streetNumber.setValue(getModel().getStreetNumber(),false);
		streetStair.setValue(getModel().getStreetStair(),false);
		streetFloor.setValue(getModel().getStreetFloor(),false);
		streetDoor.setValue(getModel().getStreetDoor(),false);
		town.setValue(getModel().getTown(),false);
		townCode.setValue(getModel().getTownCode(),false);
		province.setSelectedIndex(Province.safeValueOf(getModel().getProvinceCode()) == null ? 0 : Province.safeValueOf(getModel().getProvinceCode()).ordinal()); // Código de provincia
		zip.setValue(getModel().getZip(),false);
		phone.setValue(getModel().getContactPhone(),false);
		taxRefund.setValue(getModel().isTaxRefund(),false);
		repep.setValue(getModel().isSpecialRegime(),false);
		replacement.setValue(getModel().isReplacement(),false);
		replacementDueInsolvencyState.setValue(getModel().isReplacementDueInsolvencyState(),false);
		replacedReceipt.setValue(getModel().getReplacedReceipt(),false);

	}

}
