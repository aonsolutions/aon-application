package com.esferalia.aon.gwt.fiscal.client.mod390;

import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.widget.DoubleTextBox;
import com.esferalia.aon.gwt.common.shared.AonUtil;
import com.esferalia.aon.gwt.fiscal.client.mod390.Model390.Mod390CallBack;
import com.esferalia.aon.gwt.fiscal.client.widget.SimplifiedRegimePanel;
import com.esferalia.aon.gwt.fiscal.shared.Mod311Results;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod390.FarmerRegimeActivity;
import com.esferalia.aon.occam.api.model.fiscal.Mod390.SimpliedRegimeActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Page6 extends ResizeComposite implements RequiresResize {

	interface Page6Binder extends UiBinder<Widget, Page6> {
	}
	private static final NumberFormat FMT_4DEC = NumberFormat.getFormat("#0.0000");
	
	private static final Page6Binder page6Binder = GWT
			.create(Page6Binder.class);

	Mod390CallBack callback;

	@UiField
	SimplifiedRegimePanel activity1;

	@UiField
	SimplifiedRegimePanel activity2;

	@UiField
	TextBox f01A;
	@UiField
	DoubleTextBox f01B;
	@UiField
	DoubleTextBox f01C;
	@UiField
	DoubleTextBox f01D;
	@UiField
	DoubleTextBox f01E;
	@UiField
	DoubleTextBox f01K;
	
	@UiField
	TextBox f02A;
	@UiField
	DoubleTextBox f02B;
	@UiField
	DoubleTextBox f02C;
	@UiField
	DoubleTextBox f02D;
	@UiField
	DoubleTextBox f02E;
	@UiField
	DoubleTextBox f02K;

	@UiField
	TextBox f03A;
	@UiField
	DoubleTextBox f03B;
	@UiField
	DoubleTextBox f03C;
	@UiField
	DoubleTextBox f03D;
	@UiField
	DoubleTextBox f03E;
	@UiField
	DoubleTextBox f03K;

	@UiField
	TextBox f04A;
	@UiField
	DoubleTextBox f04B;
	@UiField
	DoubleTextBox f04C;
	@UiField
	DoubleTextBox f04D;
	@UiField
	DoubleTextBox f04E;
	@UiField
	DoubleTextBox f04K;

	@UiField
	TextBox f05A;
	@UiField
	DoubleTextBox f05B;
	@UiField
	DoubleTextBox f05C;
	@UiField
	DoubleTextBox f05D;
	@UiField
	DoubleTextBox f05E;
	@UiField
	DoubleTextBox f05K;

	@UiField
	DoubleTextBox box74;
	
	@UiField
	DoubleTextBox box75;

	@UiField
	DoubleTextBox box76;
	
	@UiField
	DoubleTextBox box77;
	
	@UiField
	DoubleTextBox box78;
	
	@UiField
	DoubleTextBox box79;
	
	@UiField
	DoubleTextBox box80;
	
	@UiField
	DoubleTextBox box81;
	
	@UiField
	DoubleTextBox box82;

	@UiField
	DoubleTextBox box83;
	
	public Page6() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		Model390.RESOURCES.css().ensureInjected();

		Widget ui = page6Binder.createAndBindUi(this);
		initWidget(ui);
		box74.setEnabled(false);
		box75.setEnabled(false);
		box79.setEnabled(false);
		box82.setEnabled(false);
		box83.setEnabled(false);
		
		f01A.setVisibleLength(3);
		f02A.setVisibleLength(3);
		f03A.setVisibleLength(3);
		f04A.setVisibleLength(3);
		f05A.setVisibleLength(3);
		f01C.setVisibleLength(5);
		f02C.setVisibleLength(5);
		f03C.setVisibleLength(5);
		f04C.setVisibleLength(5);
		f05C.setVisibleLength(5);
	}

	public void setValue(Mod390 m390) {
		SimpliedRegimeActivity regime = m390.getSimpRegime1();
		if (regime != null) {
			activity1.setValue(regime);
		} else {
			activity1.empty();
		}
		regime = m390.getSimpRegime2();
		if (regime != null) {
			activity2.setValue(regime);
		} else {
			activity2.empty();
		}
		FarmerRegimeActivity farmer = m390.getFarmerRegime1();
		if (farmer != null) {
			f01A.setValue(farmer.getCodigo());
			f01B.setValue(farmer.getIncomes());
			f01C.setValue(farmer.getQuotaIndex());
			f01D.setValue(farmer.getAccrualQuota());
			f01E.setValue(farmer.getInputQuotas());
			f01K.setValue(farmer.getQuota());
		}
		farmer = m390.getFarmerRegime2();
		if (farmer != null) {
			f02A.setValue(farmer.getCodigo());
			f02B.setValue(farmer.getIncomes());
			f02C.setValue(farmer.getQuotaIndex());
			f02D.setValue(farmer.getAccrualQuota());
			f02E.setValue(farmer.getInputQuotas());
			f02K.setValue(farmer.getQuota());
		}
		farmer = m390.getFarmerRegime3();
		if (farmer != null) {
			f03A.setValue(farmer.getCodigo());
			f03B.setValue(farmer.getIncomes());
			f03C.setValue(farmer.getQuotaIndex());
			f03D.setValue(farmer.getAccrualQuota());
			f03E.setValue(farmer.getInputQuotas());
			f03K.setValue(farmer.getQuota());
		}
		farmer = m390.getFarmerRegime4();
		if (farmer != null) {
			f04A.setValue(farmer.getCodigo());
			f04B.setValue(farmer.getIncomes());
			f04C.setValue(farmer.getQuotaIndex());
			f04D.setValue(farmer.getAccrualQuota());
			f04E.setValue(farmer.getInputQuotas());
			f04K.setValue(farmer.getQuota());
		}
		farmer = m390.getFarmerRegime5();
		if (farmer != null) {
			f05A.setValue(farmer.getCodigo());
			f05B.setValue(farmer.getIncomes());
			f05C.setValue(farmer.getQuotaIndex());
			f05D.setValue(farmer.getAccrualQuota());
			f05E.setValue(farmer.getInputQuotas());
			f05K.setValue(farmer.getQuota());
		}
		
		box74.setValue(m390.getBox74());
		box75.setValue(m390.getBox75());
		box76.setValue(m390.getBox76());
		box77.setValue(m390.getBox77());
		box78.setValue(m390.getBox78());
		box79.setValue(m390.getBox79());
		box80.setValue(m390.getBox80());
		box81.setValue(m390.getBox81());
		box82.setValue(m390.getBox82());
		box83.setValue(m390.getBox83());
		refresh();
	}

	void refresh() {
		box74.setValue(AonUtil.round(activity1.getBoxJ() + activity2.getBoxJ()));
		box75.setValue(AonUtil.round(f01K.getDoubleValue()
				+ f02K.getDoubleValue()
				+ f03K.getDoubleValue()				
				+ f04K.getDoubleValue()				
				+ f05K.getDoubleValue()				
				));
		box79.setValue(AonUtil.round(
				box74.getDoubleValue() +
				box75.getDoubleValue() +
				box76.getDoubleValue() +
				box77.getDoubleValue() +
				box78.getDoubleValue()
				));
		box82.setValue(AonUtil.round(
				box80.getDoubleValue() +
				box81.getDoubleValue() 
				));
		box83.setValue(AonUtil.round(
				box79.getDoubleValue() -
				box82.getDoubleValue() 
				));
	}
	
	@UiHandler("box74")
	void onChangeBox74(ChangeEvent event) {
		refresh();
	}
	@UiHandler("box75")
	void onChangeBox75(ChangeEvent event) {
		refresh();
	}
	@UiHandler("box76")
	void onChangeBox76(ChangeEvent event) {
		refresh();
	}
	@UiHandler("box77")
	void onChangeBox77(ChangeEvent event) {
		refresh();
	}
	@UiHandler("box78")
	void onChangeBox78(ChangeEvent event) {
		refresh();
	}
	@UiHandler("box80")
	void onChangeBox80(ChangeEvent event) {
		refresh();
	}
	@UiHandler("box81")
	void onChangeBox81(ChangeEvent event) {
		refresh();
	}
		
		
	public void populate(Mod390 mod390) {
		SimpliedRegimeActivity regime = activity1.populate();
		mod390.setSimpRegime1((regime != null)?regime:null);
		regime = activity2.populate();
		mod390.setSimpRegime2((regime != null)?regime:null);
		FarmerRegimeActivity farmer = null;
		if (!AonUtil.isEmpty(f01A.getValue()) ) {
			farmer = new FarmerRegimeActivity();
			farmer.setCodigo(f01A.getValue());
			farmer.setIncomes(f01B.getDoubleValue());
			farmer.setQuotaIndex(f01C.getDoubleValue(FMT_4DEC));
			farmer.setAccrualQuota(f01D.getDoubleValue());
			farmer.setInputQuotas(f01E.getDoubleValue());
			farmer.setQuota(f01K.getDoubleValue());
			mod390.setFarmerRegime1(farmer);
		}
		if (!AonUtil.isEmpty(f02A.getValue()) ) {
			farmer = new FarmerRegimeActivity();
			farmer.setCodigo(f02A.getValue());
			farmer.setIncomes(f02B.getDoubleValue());
			farmer.setQuotaIndex(f02C.getDoubleValue(FMT_4DEC));
			farmer.setAccrualQuota(f02D.getDoubleValue());
			farmer.setInputQuotas(f02E.getDoubleValue());
			farmer.setQuota(f02K.getDoubleValue());
			mod390.setFarmerRegime2(farmer);
		}
		if (!AonUtil.isEmpty(f03A.getValue()) ) {
			farmer = new FarmerRegimeActivity();
			farmer.setCodigo(f03A.getValue());
			farmer.setIncomes(f03B.getDoubleValue());
			farmer.setQuotaIndex(f03C.getDoubleValue(FMT_4DEC));
			farmer.setAccrualQuota(f03D.getDoubleValue());
			farmer.setInputQuotas(f03E.getDoubleValue());
			farmer.setQuota(f03K.getDoubleValue());
			mod390.setFarmerRegime3(farmer);
		}
		if (!AonUtil.isEmpty(f04A.getValue()) ) {
			farmer = new FarmerRegimeActivity();
			farmer.setCodigo(f04A.getValue());
			farmer.setIncomes(f04B.getDoubleValue());
			farmer.setQuotaIndex(f04C.getDoubleValue(FMT_4DEC));
			farmer.setAccrualQuota(f04D.getDoubleValue());
			farmer.setInputQuotas(f04E.getDoubleValue());
			farmer.setQuota(f04K.getDoubleValue());
			mod390.setFarmerRegime4(farmer);
		}
		if (!AonUtil.isEmpty(f05A.getValue()) ) {
			farmer = new FarmerRegimeActivity();
			farmer.setCodigo(f05A.getValue());
			farmer.setIncomes(f05B.getDoubleValue());
			farmer.setQuotaIndex(f05C.getDoubleValue(FMT_4DEC));
			farmer.setAccrualQuota(f05D.getDoubleValue());
			farmer.setInputQuotas(f05E.getDoubleValue());
			farmer.setQuota(f05K.getDoubleValue());
			mod390.setFarmerRegime5(farmer);
		}
		mod390.setBox74(box74.getDoubleValue());
		mod390.setBox75(box75.getDoubleValue());
		mod390.setBox76(box76.getDoubleValue());
		mod390.setBox77(box77.getDoubleValue());
		mod390.setBox78(box78.getDoubleValue());
		mod390.setBox79(box79.getDoubleValue());
		mod390.setBox80(box80.getDoubleValue());
		mod390.setBox81(box81.getDoubleValue());
		mod390.setBox82(box82.getDoubleValue());
		mod390.setBox83(box83.getDoubleValue());
	}

	public double getBox83() {
		return box83.getDoubleValue();
	}

	SimplifiedRegimePanel getActivity1() {
		return activity1;
	}
	SimplifiedRegimePanel getActivity2() {
		return activity2;
	}

	public void setFarmerValue(int f, Mod311Results farmer) {
		if (f==0) {
			f01A.setValue(farmer.getCodigo());
			f01B.setValue(farmer.getIncomes());
			f01C.setValue(farmer.getQuotaIndex(),FMT_4DEC);
			f01D.setValue(farmer.getAccrualQuota());
			f01E.setValue(farmer.getInputQuotas());
			f01K.setValue(farmer.getQuota());
		}
		if (f==1) {
			f02A.setValue(farmer.getCodigo());
			f02B.setValue(farmer.getIncomes());
			f02C.setValue(farmer.getQuotaIndex(),FMT_4DEC);
			f02D.setValue(farmer.getAccrualQuota());
			f02E.setValue(farmer.getInputQuotas());
			f02K.setValue(farmer.getQuota());
		}
		if (f==2) {
			f03A.setValue(farmer.getCodigo());
			f03B.setValue(farmer.getIncomes());
			f03C.setValue(farmer.getQuotaIndex(),FMT_4DEC);
			f03D.setValue(farmer.getAccrualQuota());
			f03E.setValue(farmer.getInputQuotas());
			f03K.setValue(farmer.getQuota());
		}
		if (f==3) {
			f04A.setValue(farmer.getCodigo());
			f04B.setValue(farmer.getIncomes());
			f04C.setValue(farmer.getQuotaIndex(),FMT_4DEC);
			f04D.setValue(farmer.getAccrualQuota());
			f04E.setValue(farmer.getInputQuotas());
			f04K.setValue(farmer.getQuota());
		}
		if (f==4) {
			f05A.setValue(farmer.getCodigo());
			f05B.setValue(farmer.getIncomes());
			f05C.setValue(farmer.getQuotaIndex(),FMT_4DEC);
			f05D.setValue(farmer.getAccrualQuota());
			f05E.setValue(farmer.getInputQuotas());
			f05K.setValue(farmer.getQuota());
		}
	}
	
	public void setCallback(Mod390CallBack callback) {
		this.callback = callback;
	}
}
