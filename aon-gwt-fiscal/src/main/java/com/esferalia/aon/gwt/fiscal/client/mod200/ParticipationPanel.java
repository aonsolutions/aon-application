package com.esferalia.aon.gwt.fiscal.client.mod200;

import static com.esferalia.aon.gwt.fiscal.client.mod200.Model200.MSG;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.DoubleTextBox;
import com.esferalia.aon.gwt.common.client.widget.ProvinceCountryListBox;
import com.esferalia.aon.gwt.common.shared.CommonEnum.Province;
import com.esferalia.aon.gwt.common.shared.CompanyParticipation;
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
	DoubleTextBox percent;
	@UiField
	DoubleTextBox nominalValue;
	@UiField
	DoubleTextBox bookValue;
	@UiField
	DoubleTextBox incomes;
	
	@UiField
	DoubleTextBox aValue;
	@UiField
	DoubleTextBox bValue;
	@UiField
	DoubleTextBox cValue;
	@UiField
	DoubleTextBox dValue;
	
	@UiField
	DoubleTextBox capital;
	@UiField
	DoubleTextBox reserve;
	@UiField
	DoubleTextBox otherAmounts;
	@UiField
	DoubleTextBox result;

	private CellTable<CompanyParticipation> table;

	public ParticipationPanel() {
		setVisible(false);
		setAnimationEnabled(true);
		setGlassEnabled(true);
		setModal(true);
		setCaption(MSG.participationsOut());
		
		
		List<String> options = new LinkedList<String>();
		for (Province prov : Province.values()) {
			options.add( MSG.provinceName(prov) );
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
		this.province.setSelectedIndex(companyParticipation.getProvince());
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
		companyParticipation.setProvince(this.province.getSelectedIndex());
		companyParticipation.setPercent(this.percent.getDoubleValue());
		companyParticipation.setNominalValue(this.nominalValue.getDoubleValue());
		companyParticipation.setBookValue(this.bookValue.getDoubleValue());
		companyParticipation.setIncomes(this.incomes.getDoubleValue());
		companyParticipation.setaValue(this.aValue.getDoubleValue());
		companyParticipation.setbValue(this.bValue.getDoubleValue());
		companyParticipation.setcValue(this.cValue.getDoubleValue());
		companyParticipation.setdValue(this.dValue.getDoubleValue());
		companyParticipation.setCapital(this.capital.getDoubleValue());
		companyParticipation.setReserve(this.reserve.getDoubleValue());
		companyParticipation.setOtherAmounts(this.otherAmounts.getDoubleValue());
		companyParticipation.setResult(this.result.getDoubleValue());
		
		table.redraw();
		this.hide();
	}

}
