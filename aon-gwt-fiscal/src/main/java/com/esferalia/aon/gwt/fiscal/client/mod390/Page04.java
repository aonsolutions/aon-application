package com.esferalia.aon.gwt.fiscal.client.mod390;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.Model390.Mod390CallBack;
import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014.Address;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Page04 extends ResizeComposite {

	interface Page4Binder extends
			UiBinder<Widget, Page04> {
	}

	private static final Page4Binder page4Binder = GWT
			.create(Page4Binder.class);

	Mod390CallBack callback;

	@UiField
	DocumentTextBox rdocument;
	@UiField
	TextBox rname;
	@UiField
	TextBox rstreetType;
	@UiField
	TextBox rstreetName;
	@UiField
	TextBox rstreetNumber;
	@UiField
	TextBox rstreetStair;
	@UiField
	TextBox rstreetFloor;
	@UiField
	TextBox rstreetDoor;
	@UiField
	TextBox rphone;
	@UiField
	TextBox rtown;
	@UiField
	ProvinceListBox rprovince;
	@UiField
	TextBox rzip;
	
	@UiField
	TextBox name1;
	@UiField
	DocumentTextBox document1;
	@UiField
	DateBoxEx notaryDate1;
	@UiField
	TextBox notary1;
	
	@UiField
	TextBox name2;
	@UiField
	DocumentTextBox document2;
	@UiField
	DateBoxEx notaryDate2;
	@UiField
	TextBox notary2;

	@UiField
	TextBox name3;
	@UiField
	DocumentTextBox document3;
	@UiField
	DateBoxEx notaryDate3;
	@UiField
	TextBox notary3;

	public Page04() {
		Widget ui = page4Binder.createAndBindUi(this);
		initWidget(ui);

	}

	public void setValue(Mod3902014 m390) {
		if (m390.getAddress() != null) {
			rdocument.setValue(m390.getAddress().getRdocument());
			rname.setValue(m390.getAddress().getRname());
			rstreetType.setValue(m390.getAddress().getRstreetType());
			rstreetName.setValue(m390.getAddress().getRstreetName());
			rstreetNumber.setValue(m390.getAddress().getRstreetNumber());
			rstreetStair.setValue(m390.getAddress().getRstreetStair());
			rstreetFloor.setValue(m390.getAddress().getRstreetFloor());
			rstreetDoor.setValue(m390.getAddress().getRstreetDoor());
			rphone.setValue(m390.getAddress().getRphone());
			rtown.setValue(m390.getAddress().getRtown());
			rprovince.setSelectedIndex(m390.getAddress().getRprovince());
			rzip.setValue(m390.getAddress().getRzip());
		} else {
			rdocument.setValue(null);
			rname.setValue(null);
			rstreetType.setValue(null);
			rstreetName.setValue(null);
			rstreetNumber.setValue(null);
			rstreetStair.setValue(null);
			rstreetFloor.setValue(null);
			rstreetDoor.setValue(null);
			rphone.setValue(null);
			rtown.setValue(null);
			rprovince.setSelectedIndex(0);
			rzip.setValue(null);
		}
		if (m390.getLegalRepr1() != null) {
			name1.setValue(m390.getLegalRepr1().getName());
			document1.setValue(m390.getLegalRepr1().getDocument());
			notary1.setValue(m390.getLegalRepr1().getNotary());
//			Date date = notaryDate1.getFormat().parse(notaryDate1, m390.getLegalRepr1().getNotaryDate(), false);
			Date date = m390.getLegalRepr1().getNotaryDate();
			notaryDate1.setValue(date);
		} else {
			name1.setValue(null);
			document1.setValue(null);
			notary1.setValue(null);
			notaryDate1.setValue(null);
		}
		if (m390.getLegalRepr2() != null) {
			name2.setValue(m390.getLegalRepr2().getName());
			document2.setValue(m390.getLegalRepr2().getDocument());
			notary2.setValue(m390.getLegalRepr2().getNotary());
//			Date date = notaryDate2.getFormat().parse(notaryDate2, m390.getLegalRepr2().getNotaryDate(), false);
			Date date = m390.getLegalRepr2().getNotaryDate();
			notaryDate2.setValue(date);
		} else {
			name2.setValue(null);
			document2.setValue(null);
			notary2.setValue(null);
			notaryDate2.setValue(null);
		}
		if (m390.getLegalRepr3() != null) {
			name3.setValue(m390.getLegalRepr3().getName());
			document3.setValue(m390.getLegalRepr3().getDocument());
			notary3.setValue(m390.getLegalRepr3().getNotary());
//			Date date = notaryDate3.getFormat().parse(notaryDate3, m390.getLegalRepr3().getNotaryDate(), false);
			Date date = m390.getLegalRepr3().getNotaryDate();
			notaryDate3.setValue(date);
		} else {
			name3.setValue(null);
			document3.setValue(null);
			notary3.setValue(null);
			notaryDate3.setValue(null);
		}
	}

	public void populate(Mod3902014 mod390) {
		Address address = new Address();
		address.setRdocument(rdocument.getValue());
		address.setRname(rname.getValue());
		address.setRstreetType(rstreetType.getValue());
		address.setRstreetName(rstreetName.getValue());
		address.setRstreetNumber(rstreetNumber.getValue());
		address.setRstreetStair(rstreetStair.getValue());
		address.setRstreetFloor(rstreetFloor.getValue());
		address.setRstreetDoor(rstreetDoor.getValue());
		address.setRphone(rphone.getValue());
		address.setRtown(rtown.getValue());
		address.setRprovince(rprovince.getSelectedIndex());
		address.setRzip(rzip.getValue());
		mod390.setAddress(address);
		
		if (!AonStringUtils.isEmpty( document1.getValue() ) ) {
			LegalRepresentative legalRepr = new LegalRepresentative();
			legalRepr.setDocument(document1.getValue());
			legalRepr.setName(name1.getValue());
			legalRepr.setNotary(notary1.getValue());
			legalRepr.setNotaryDate(notaryDate1.getValue());
			mod390.setLegalRepr1(legalRepr);
			
		}
		if (!AonStringUtils.isEmpty( document2.getValue() ) ) {
			LegalRepresentative legalRepr = new LegalRepresentative();
			legalRepr.setDocument(document2.getValue());
			legalRepr.setName(name2.getValue());
			legalRepr.setNotary(notary2.getValue());
			legalRepr.setNotaryDate(notaryDate2.getValue());
			mod390.setLegalRepr2(legalRepr);
		}
		if (!AonStringUtils.isEmpty( document3.getValue() ) ) {
			LegalRepresentative legalRepr = new LegalRepresentative();
			legalRepr.setDocument(document3.getValue());
			legalRepr.setName(name3.getValue());
			legalRepr.setNotary(notary3.getValue());
			legalRepr.setNotaryDate(notaryDate3.getValue());
			mod390.setLegalRepr3(legalRepr);
		}
	}
	
	public void setCallback(Mod390CallBack callback) {
		this.callback = callback;
	}

}
