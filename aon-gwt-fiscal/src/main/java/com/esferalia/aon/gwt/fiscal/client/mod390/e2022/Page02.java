package com.esferalia.aon.gwt.fiscal.client.mod390.e2022;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2022.Model3902022.Model3902022Callback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class Page02 extends PageAbs {

	AonDocumentTextBox rdocument = new AonDocumentTextBox();
	AonTextBox rname = new AonTextBox();
	AonTextBox rstreetType = new AonTextBox();
	AonTextBox rstreetName = new AonTextBox();
	AonTextBox rstreetNumber = new AonTextBox();
	AonTextBox rstreetStair = new AonTextBox();
	AonTextBox rstreetFloor = new AonTextBox();
	AonTextBox rstreetDoor = new AonTextBox();
	AonTextBox rphone = new AonTextBox();
	AonTextBox rtown = new AonTextBox();
	ProvinceListBox rprovince = new ProvinceListBox();
	AonTextBox rzip = new AonTextBox();
	
	AonTextBox name1 = new AonTextBox();
	AonDocumentTextBox document1 = new AonDocumentTextBox();
	AonDateBox notaryDate1 = new AonDateBox();
	AonTextBox notary1 = new AonTextBox();
	
	AonTextBox name2 = new AonTextBox();
	AonDocumentTextBox document2 = new AonDocumentTextBox();
	AonDateBox notaryDate2 = new AonDateBox();
	AonTextBox notary2 = new AonTextBox();

	AonTextBox name3 = new AonTextBox();
	AonDocumentTextBox document3 = new AonDocumentTextBox();
	AonDateBox notaryDate3 = new AonDateBox();
	AonTextBox notary3 = new AonTextBox();

	public Page02(Model3902022Callback callback) {
		super(callback);
		paint();
		setValue();
	}
	
	@Override
	protected void setValue() {
		if (getModel().getAddress() != null) {
			rdocument.setValue(getModel().getAddress().getRdocument(),false);
			rname.setValue(getModel().getAddress().getRname(),false);
			rstreetType.setValue(getModel().getAddress().getRstreetType(),false);
			rstreetName.setValue(getModel().getAddress().getRstreetName(),false);
			rstreetNumber.setValue(getModel().getAddress().getRstreetNumber(),false);
			rstreetStair.setValue(getModel().getAddress().getRstreetStair(),false);
			rstreetFloor.setValue(getModel().getAddress().getRstreetFloor(),false);
			rstreetDoor.setValue(getModel().getAddress().getRstreetDoor(),false);
			rphone.setValue(getModel().getAddress().getRphone(),false);
			rtown.setValue(getModel().getAddress().getRtown(),false);
			rprovince.setSelectedIndex(getModel().getAddress().getRprovince());
			rzip.setValue(getModel().getAddress().getRzip(),false);
		} else {
			rdocument.setValue(null,false);
			rname.setValue(null,false);
			rstreetType.setValue(null,false);
			rstreetName.setValue(null,false);
			rstreetNumber.setValue(null,false);
			rstreetStair.setValue(null,false);
			rstreetFloor.setValue(null,false);
			rstreetDoor.setValue(null,false);
			rphone.setValue(null,false);
			rtown.setValue(null,false);
			rprovince.setSelectedIndex(0);
			rzip.setValue(null,false);
		}
		
		if (getModel().getLegalRepr1() != null) {
			name1.setValue(getModel().getLegalRepr1().getName(),false);
			document1.setValue(getModel().getLegalRepr1().getDocument(),false);
			notary1.setValue(getModel().getLegalRepr1().getNotary(),false);
			Date date = getModel().getLegalRepr1().getNotaryDate();
			notaryDate1.setValue(date,false);
		} else {
			name1.setValue(null,false);
			document1.setValue(null,false);
			notary1.setValue(null,false);
			notaryDate1.setValue(null,false);
		}
		if (getModel().getLegalRepr2() != null) {
			name2.setValue(getModel().getLegalRepr2().getName(),false);
			document2.setValue(getModel().getLegalRepr2().getDocument(),false);
			notary2.setValue(getModel().getLegalRepr2().getNotary(),false);
			Date date = getModel().getLegalRepr2().getNotaryDate();
			notaryDate2.setValue(date,false);
		} else {
			name2.setValue(null);
			document2.setValue(null);
			notary2.setValue(null);
			notaryDate2.setValue(null);
		}
		if (getModel().getLegalRepr3() != null) {
			name3.setValue(getModel().getLegalRepr3().getName());
			document3.setValue(getModel().getLegalRepr3().getDocument());
			notary3.setValue(getModel().getLegalRepr3().getNotary());
			Date date = getModel().getLegalRepr3().getNotaryDate();
			notaryDate3.setValue(date);
		} else {
			name3.setValue(null);
			document3.setValue(null);
			notary3.setValue(null);
			notaryDate3.setValue(null);
		}
	}

	private void paint() {
		ScrollPanel scroll = new ScrollPanel();
		FlowPanel basePanel = new FlowPanel();
		scroll.add(basePanel);
		setWidget(scroll);
		
		basePanel.add(getTitle(AON.MSG.representativeData()));
		basePanel.add(getSubtitle(AON.MSG.nonLegalEntities()));
		
		AonDisplayTable tab0 = new AonDisplayTable();
		tab0.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab0.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab0);
		tab0.addRow()
			.addCell(new Label(AON.MSG.representativeData()), AON.CSS.aonWidth150() )
			.addCell(new Label(AON.MSG.name()));
		
		rname.setVisibleLength(42);
		rname.setMaxLength(40);
		tab0.addRow()
			.addCell(rdocument)
			.addCell(rname);
		
		AonDisplayTable tab1 = new AonDisplayTable();
		tab1.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab1.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab1);
		tab1.addRow()
			.addCell(new Label(AON.MSG.representativeData()), AON.CSS.aonWidth150() )
			.addCell(new Label(AON.MSG.name()) );
		tab1.addRow()
			.addCell(new Label(AON.MSG.streetType()), AON.CSS.aonWidth80() )
			.addCell(new Label(AON.MSG.streetName()), AON.CSS.aonWidth150() )
			.addCell(new Label(AON.MSG.streetNumber()), AON.CSS.aonWidth80() )
			.addCell(new Label(AON.MSG.streetStair()), AON.CSS.aonWidth80() )
			.addCell(new Label(AON.MSG.streetFloor()), AON.CSS.aonWidth80() )
			.addCell(new Label(AON.MSG.streetDoor()), AON.CSS.aonWidth80() )
			.addCell(new Label(AON.MSG.phone()), AON.CSS.aonWidthAuto() );
		rstreetType.setVisibleLength(4);
		rstreetType.setMaxLength(4);
		rstreetName.setVisibleLength(30);
		rstreetName.setMaxLength(30);
		rstreetNumber.setVisibleLength(5);
		rstreetNumber.setMaxLength(5);
		rstreetStair.setVisibleLength(3);
		rstreetStair.setMaxLength(3);
		rstreetFloor.setVisibleLength(3);
		rstreetFloor.setMaxLength(3);
		rstreetDoor.setVisibleLength(3);
		rstreetDoor.setMaxLength(3);
		rphone.setVisibleLength(9);
		rphone.setMaxLength(9);
		tab1.addRow()
			.addCell(rstreetType)
			.addCell(rstreetName)
			.addCell(rstreetNumber) 
			.addCell(rstreetStair)
			.addCell(rstreetFloor) 
			.addCell(rstreetDoor)
			.addCell(rphone);
		
		AonDisplayTable tab2 = new AonDisplayTable();
		tab2.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab2.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab2);
		tab2.addRow()
			.addCell(new Label(AON.MSG.town()), AON.CSS.aonWidth150() )
			.addCell(new Label(AON.MSG.province()), AON.CSS.aonWidth150() )
			.addCell(new Label(AON.MSG.zip()), AON.CSS.aonWidthAuto() )
			;
		rtown.setVisibleLength(30);
		rtown.setMaxLength(30);
		rzip.setVisibleLength(5);
		rzip.setMaxLength(5);
		tab2.addRow()
			.addCell(rtown)
			.addCell(rprovince )
			.addCell(rzip);
		
		basePanel.add(getSubtitle(AON.MSG.legalEntities()));
		
		name1.setVisibleLength(25);
		name1.setMaxLength(40);
		name2.setVisibleLength(25);
		name2.setMaxLength(40);
		name3.setVisibleLength(25);
		name3.setMaxLength(40);

		notary1.setVisibleLength(20);
		notary1.setMaxLength(20);
		notary2.setVisibleLength(20);
		notary2.setMaxLength(20);
		notary3.setVisibleLength(20);
		notary3.setMaxLength(20);
		
		AonDisplayTable tab3 = new AonDisplayTable();
		tab3.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab3.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab3);
		tab3.addRow()
			.addCell(new Label(""), AON.CSS.aonWidth20() )
			.addCell(new Label("D."), AON.CSS.aonWidth150() )
			.addCell(new Label("NIF"), AON.CSS.aonWidth150() )
			.addCell(new Label(AON.MSG.registrationDate()), AON.CSS.aonWidth150() )
			.addCell(new Label(AON.MSG.notary()), AON.CSS.aonWidthAuto() )
			;
		tab3.addRow()
			.addCell(new Label("(1)"), AON.CSS.aonWidth20() )
			.addCell(name1)
			.addCell(document1)
			.addCell(notaryDate1)
			.addCell(notary1)
			;
		tab3.addRow()
			.addCell(new Label("(2)"), AON.CSS.aonWidth20() )
			.addCell(name2)
			.addCell(document2)
			.addCell(notaryDate2)
			.addCell(notary2)
			;
		tab3.addRow()
			.addCell(new Label("(3)"), AON.CSS.aonWidth20() )
			.addCell(name3)
			.addCell(document3)
			.addCell(notaryDate3)
			.addCell(notary3)
			;
		
		rdocument.addValueChangeHandler( event -> {
			getModel().ensureAddress().setRdocument(rdocument.getValue());
			markAsDirty();
		});
		rname.addValueChangeHandler( event -> {
			getModel().ensureAddress().setRname(rname.getValue());
			markAsDirty();
		});
		rstreetType.addValueChangeHandler( event -> {
			getModel().ensureAddress().setRstreetType(rstreetType.getValue());
			markAsDirty();
		});
		rstreetName.addValueChangeHandler( event -> {
			getModel().ensureAddress().setRstreetName(rstreetName.getValue());
			markAsDirty();
		});
		rstreetNumber.addValueChangeHandler( event -> {
			getModel().ensureAddress().setRstreetNumber(rstreetNumber.getValue());
			markAsDirty();
		});
		rstreetStair.addValueChangeHandler( event -> {
			getModel().ensureAddress().setRstreetStair(rstreetStair.getValue());
			markAsDirty();
		});
		rstreetFloor.addValueChangeHandler( event -> {
			getModel().ensureAddress().setRstreetFloor(rstreetFloor.getValue());	
			markAsDirty();
		});
		rstreetDoor.addValueChangeHandler( event -> {
			getModel().ensureAddress().setRstreetDoor(rstreetDoor.getValue());
			markAsDirty();
		});
		rphone.addValueChangeHandler( event -> {
			getModel().ensureAddress().setRphone(rphone.getValue());
			markAsDirty();
		});
		rtown.addValueChangeHandler( event -> {
			getModel().ensureAddress().setRtown(rtown.getValue());
			markAsDirty();
		});
		rprovince.addChangeHandler( event -> {
			getModel().ensureAddress().setRprovince(rprovince.getSelectedIndex());
			markAsDirty();
		});
		rzip.addValueChangeHandler( event -> {
			getModel().ensureAddress().setRzip(rzip.getValue());
			markAsDirty();
		});
		
		document1.addValueChangeHandler( event -> {
			getModel().ensureLegalRepr1().setDocument(document1.getValue());
			markAsDirty();
		});
		name1.addValueChangeHandler( event -> {
			getModel().ensureLegalRepr1().setName(name1.getValue());
			markAsDirty();
		});
		notary1.addValueChangeHandler( event -> {
			getModel().ensureLegalRepr1().setNotary(notary1.getValue());
			markAsDirty();
		});
		notaryDate1.addValueChangeHandler( event -> {
			getModel().ensureLegalRepr1().setNotaryDate(notaryDate1.getValue());
			markAsDirty();
		});

		document2.addValueChangeHandler( event -> {
			getModel().ensureLegalRepr2().setDocument(document2.getValue());
			markAsDirty();
		});
		name2.addValueChangeHandler( event -> {
			getModel().ensureLegalRepr2().setName(name2.getValue());
			markAsDirty();
		});
		notary2.addValueChangeHandler( event -> {
			getModel().ensureLegalRepr2().setNotary(notary2.getValue());
			markAsDirty();
		});
		notaryDate2.addValueChangeHandler( event -> {
			getModel().ensureLegalRepr2().setNotaryDate(notaryDate2.getValue());
			markAsDirty();
		});
		
		document3.addValueChangeHandler( event -> {
			getModel().ensureLegalRepr3().setDocument(document3.getValue());
			markAsDirty();
		});
		name3.addValueChangeHandler( event -> {
			getModel().ensureLegalRepr3().setName(name3.getValue());
			markAsDirty();
		});
		notary3.addValueChangeHandler( event -> {
			getModel().ensureLegalRepr3().setNotary(notary3.getValue());
			markAsDirty();
		});
		notaryDate3.addValueChangeHandler( event -> {
			getModel().ensureLegalRepr3().setNotaryDate(notaryDate3.getValue());
			markAsDirty();
		});
	}
}
