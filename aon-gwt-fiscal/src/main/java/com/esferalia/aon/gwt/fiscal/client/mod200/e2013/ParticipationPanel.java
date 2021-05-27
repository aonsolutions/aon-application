package com.esferalia.aon.gwt.fiscal.client.mod200.e2013;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.ProvinceCountryListBox;
import com.esferalia.aon.occam.api.model.CompanyParticipation;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Province;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ParticipationPanel extends CustomDialog {

	interface ParticipationPanelBinder extends UiBinder<Widget, ParticipationPanel> {
	}
	private static final ParticipationPanelBinder participationPanelBinder = GWT
			.create(ParticipationPanelBinder.class);

	private CompanyParticipation companyParticipation;
	
	@UiField
	TextBox document;
	@UiField
	TextBox  name;
	@UiField
	ProvinceCountryListBox  province;
	
	@UiField
	DoubleBox percent;
	@UiField
	DoubleBox nominalValue;
	@UiField
	DoubleBox bookValue;
	@UiField
	DoubleBox incomes;
	
	@UiField
	DoubleBox aValue;
	@UiField
	DoubleBox bValue;
	@UiField
	DoubleBox cValue;
	@UiField
	DoubleBox dValue;
	
	@UiField
	DoubleBox capital;
	@UiField
	DoubleBox reserve;
	@UiField
	DoubleBox otherAmounts;
	@UiField
	DoubleBox result;

	private CellTable<CompanyParticipation> table;

	public ParticipationPanel() {
		setVisible(false);
		setAnimationEnabled(true);
		setGlassEnabled(true);
		setModal(true);
		setCaption(AON.MSG.participationsOut());
		
		
		List<String> options = new LinkedList<String>();
		for (Province prov : Province.values()) {
			options.add( prov.getName() );
		}
		Widget ui = participationPanelBinder.createAndBindUi(this);
		setWidget(ui);
	}

	protected void setTable(CellTable<CompanyParticipation> tableOut) {
		this.table = tableOut;
	}	

	public void dump(CompanyParticipation cp) {
		this.companyParticipation = cp;
		this.document.setValue(companyParticipation.getDocument());
		this.name.setValue(companyParticipation.getName());
		int idx = companyParticipation.getProvince();
		if (idx > 0 && idx < Province.values().length) {
			this.province.setSelectedIndex(idx);
		} else {
			Country c = Country.safeValueOf(companyParticipation.getCountry());
			if (c != null) {
				this.province.setSelectedIndex(c.ordinal() + Province.values().length);
			}
		}
		this.percent.setValue(companyParticipation.getPercent());
		this.nominalValue.setValue(companyParticipation.getNominalValue());
		this.bookValue.setValue(companyParticipation.getBookValue());
		this.incomes.setValue(companyParticipation.getIncomes());
		this.aValue.setValue(companyParticipation.getaValue());
		this.bValue.setValue(companyParticipation.getbValue());
		this.cValue.setValue(companyParticipation.getcValue());
		this.dValue.setValue(companyParticipation.getdValue());
		this.capital.setValue(companyParticipation.getCapital());
		this.reserve.setValue(companyParticipation.getReserve());
		this.otherAmounts.setValue(companyParticipation.getOtherAmounts());
		this.result.setValue(companyParticipation.getResult());
	}

	@Override
	public void onClose() {
		companyParticipation.setDocument(this.document.getValue());
		companyParticipation.setName(this.name.getValue());
		if (this.province.getSelectedIndex() < Province.values().length) {
			companyParticipation.setProvince(this.province.getSelectedIndex());
		} else {
			Country c = Country.values()[this.province.getSelectedIndex() - Province.values().length];
			companyParticipation.setCountry(c.getIso2());
		}
		companyParticipation.setPercent(this.percent.getValue());
		companyParticipation.setNominalValue(this.nominalValue.getValue());
		companyParticipation.setBookValue(this.bookValue.getValue());
		companyParticipation.setIncomes(this.incomes.getValue());
		companyParticipation.setaValue(this.aValue.getValue());
		companyParticipation.setbValue(this.bValue.getValue());
		companyParticipation.setcValue(this.cValue.getValue());
		companyParticipation.setdValue(this.dValue.getValue());
		companyParticipation.setCapital(this.capital.getValue());
		companyParticipation.setReserve(this.reserve.getValue());
		companyParticipation.setOtherAmounts(this.otherAmounts.getValue());
		companyParticipation.setResult(this.result.getValue());
		
		table.redraw();
		this.hide();
	}

}
