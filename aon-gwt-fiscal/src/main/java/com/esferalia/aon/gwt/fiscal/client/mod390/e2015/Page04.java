package com.esferalia.aon.gwt.fiscal.client.mod390.e2015;

import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2015.Model3902015.IMod3902015CallBack;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2015.Model3902015.IMod3902015Page;
import com.esferalia.aon.gwt.fiscal.shared.Mod311Results;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015.FarmerRegimeActivity;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015.SimpliedRegimeActivity;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Page04 extends ResizeComposite implements RequiresResize , IMod3902015Page {

	interface PageBinder extends UiBinder<Widget, Page04> {
	}

	private static final PageBinder BINDER = GWT
			.create(PageBinder.class);

	IMod3902015CallBack callback;

	@UiField
	SimplifiedRegimePanel activity1;

	@UiField
	SimplifiedRegimePanel activity2;

	@UiField
	TextBox f01A;
	@UiField
	DoubleBox f01B;
	@UiField(provided=true)
	DoubleBox f01C;
	@UiField
	DoubleBox f01D;
	@UiField
	DoubleBox f01E;
	@UiField
	DoubleBox f01K;
	
	@UiField
	TextBox f02A;
	@UiField
	DoubleBox f02B;
	@UiField(provided=true)
	DoubleBox f02C;
	@UiField
	DoubleBox f02D;
	@UiField
	DoubleBox f02E;
	@UiField
	DoubleBox f02K;

	@UiField
	TextBox f03A;
	@UiField
	DoubleBox f03B;
	@UiField(provided=true)
	DoubleBox f03C;
	@UiField
	DoubleBox f03D;
	@UiField
	DoubleBox f03E;
	@UiField
	DoubleBox f03K;

	@UiField
	TextBox f04A;
	@UiField
	DoubleBox f04B;
	@UiField(provided=true)
	DoubleBox f04C;
	@UiField
	DoubleBox f04D;
	@UiField
	DoubleBox f04E;
	@UiField
	DoubleBox f04K;

	@UiField
	TextBox f05A;
	@UiField
	DoubleBox f05B;
	@UiField(provided=true)
	DoubleBox f05C;
	@UiField
	DoubleBox f05D;
	@UiField
	DoubleBox f05E;
	@UiField
	DoubleBox f05K;

	@UiField
	DoubleBox box74;
	
	@UiField
	DoubleBox box75;

	@UiField
	DoubleBox box76;
	
	@UiField
	DoubleBox box77;
	
	@UiField
	DoubleBox box78;
	
	@UiField
	DoubleBox box79;
	
	@UiField
	DoubleBox box80;
	
	@UiField
	DoubleBox box81;
	
	@UiField
	DoubleBox box82;

	@UiField
	DoubleBox box83;
	
	public Page04(Mod3902015 m390) {
		f01C = new DoubleBox(DoubleBox.VISIBLE_LENGTH, 4);
		f02C = new DoubleBox(DoubleBox.VISIBLE_LENGTH, 4);
		f03C = new DoubleBox(DoubleBox.VISIBLE_LENGTH, 4);
		f04C = new DoubleBox(DoubleBox.VISIBLE_LENGTH, 4);
		f05C = new DoubleBox(DoubleBox.VISIBLE_LENGTH, 4);
		
		Widget ui = BINDER.createAndBindUi(this);
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
		setValue(m390);
	}

	private void setValue(Mod3902015 m390) {
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
		} else {
			f01K.setValue(0.0);
		}
		farmer = m390.getFarmerRegime2();
		if (farmer != null) {
			f02A.setValue(farmer.getCodigo());
			f02B.setValue(farmer.getIncomes());
			f02C.setValue(farmer.getQuotaIndex());
			f02D.setValue(farmer.getAccrualQuota());
			f02E.setValue(farmer.getInputQuotas());
			f02K.setValue(farmer.getQuota());
		} else {
			f02K.setValue(0.0);
		}
		farmer = m390.getFarmerRegime3();
		if (farmer != null) {
			f03A.setValue(farmer.getCodigo());
			f03B.setValue(farmer.getIncomes());
			f03C.setValue(farmer.getQuotaIndex());
			f03D.setValue(farmer.getAccrualQuota());
			f03E.setValue(farmer.getInputQuotas());
			f03K.setValue(farmer.getQuota());
		} else {
			f03K.setValue(0.0);
		}
		farmer = m390.getFarmerRegime4();
		if (farmer != null) {
			f04A.setValue(farmer.getCodigo());
			f04B.setValue(farmer.getIncomes());
			f04C.setValue(farmer.getQuotaIndex());
			f04D.setValue(farmer.getAccrualQuota());
			f04E.setValue(farmer.getInputQuotas());
			f04K.setValue(farmer.getQuota());
		} else {
			f04K.setValue(0.0);
		}
		farmer = m390.getFarmerRegime5();
		if (farmer != null) {
			f05A.setValue(farmer.getCodigo());
			f05B.setValue(farmer.getIncomes());
			f05C.setValue(farmer.getQuotaIndex());
			f05D.setValue(farmer.getAccrualQuota());
			f05E.setValue(farmer.getInputQuotas());
			f05K.setValue(farmer.getQuota());
		} else {
			f05K.setValue(0.0);
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
		box74.setValue(AonMathUtils.round(activity1.getBoxJ() + activity2.getBoxJ()));
		box75.setValue(AonMathUtils.round(
				  f01K.getValue()
				+ f02K.getValue()
				+ f03K.getValue()				
				+ f04K.getValue()				
				+ f05K.getValue()				
				));
		box79.setValue(AonMathUtils.round(
				box74.getValue() +
				box75.getValue() +
				box76.getValue() +
				box77.getValue() +
				box78.getValue()
				));
		box82.setValue(AonMathUtils.round(
				box80.getValue() +
				box81.getValue() 
				));
		box83.setValue(AonMathUtils.round(
				box79.getValue() -
				box82.getValue() 
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
		
		
	@Override
	public void populate(Mod3902015 mod390) {
		SimpliedRegimeActivity regime = activity1.populate();
		mod390.setSimpRegime1((regime != null)?regime:null);
		regime = activity2.populate();
		mod390.setSimpRegime2((regime != null)?regime:null);
		FarmerRegimeActivity farmer = null;
		if (!AonStringUtils.isEmpty(f01A.getValue()) ) {
			farmer = new FarmerRegimeActivity();
			farmer.setCodigo(f01A.getValue());
			farmer.setIncomes(f01B.getValue());
			farmer.setQuotaIndex(f01C.getValue());
			farmer.setAccrualQuota(f01D.getValue());
			farmer.setInputQuotas(f01E.getValue());
			farmer.setQuota(f01K.getValue());
			mod390.setFarmerRegime1(farmer);
		}
		if (!AonStringUtils.isEmpty(f02A.getValue()) ) {
			farmer = new FarmerRegimeActivity();
			farmer.setCodigo(f02A.getValue());
			farmer.setIncomes(f02B.getValue());
			farmer.setQuotaIndex(f02C.getValue());
			farmer.setAccrualQuota(f02D.getValue());
			farmer.setInputQuotas(f02E.getValue());
			farmer.setQuota(f02K.getValue());
			mod390.setFarmerRegime2(farmer);
		}
		if (!AonStringUtils.isEmpty(f03A.getValue()) ) {
			farmer = new FarmerRegimeActivity();
			farmer.setCodigo(f03A.getValue());
			farmer.setIncomes(f03B.getValue());
			farmer.setQuotaIndex(f03C.getValue());
			farmer.setAccrualQuota(f03D.getValue());
			farmer.setInputQuotas(f03E.getValue());
			farmer.setQuota(f03K.getValue());
			mod390.setFarmerRegime3(farmer);
		}
		if (!AonStringUtils.isEmpty(f04A.getValue()) ) {
			farmer = new FarmerRegimeActivity();
			farmer.setCodigo(f04A.getValue());
			farmer.setIncomes(f04B.getValue());
			farmer.setQuotaIndex(f04C.getValue());
			farmer.setAccrualQuota(f04D.getValue());
			farmer.setInputQuotas(f04E.getValue());
			farmer.setQuota(f04K.getValue());
			mod390.setFarmerRegime4(farmer);
		}
		if (!AonStringUtils.isEmpty(f05A.getValue()) ) {
			farmer = new FarmerRegimeActivity();
			farmer.setCodigo(f05A.getValue());
			farmer.setIncomes(f05B.getValue());
			farmer.setQuotaIndex(f05C.getValue());
			farmer.setAccrualQuota(f05D.getValue());
			farmer.setInputQuotas(f05E.getValue());
			farmer.setQuota(f05K.getValue());
			mod390.setFarmerRegime5(farmer);
		}
		mod390.setBox74(box74.getValue());
		mod390.setBox75(box75.getValue());
		mod390.setBox76(box76.getValue());
		mod390.setBox77(box77.getValue());
		mod390.setBox78(box78.getValue());
		mod390.setBox79(box79.getValue());
		mod390.setBox80(box80.getValue());
		mod390.setBox81(box81.getValue());
		mod390.setBox82(box82.getValue());
		mod390.setBox83(box83.getValue());
	}

	public double getBox83() {
		return box83.getValue();
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
			f01C.setValue(farmer.getQuotaIndex());
			f01D.setValue(farmer.getAccrualQuota());
			f01E.setValue(farmer.getInputQuotas());
			f01K.setValue(farmer.getQuota());
		}
		if (f==1) {
			f02A.setValue(farmer.getCodigo());
			f02B.setValue(farmer.getIncomes());
			f02C.setValue(farmer.getQuotaIndex());
			f02D.setValue(farmer.getAccrualQuota());
			f02E.setValue(farmer.getInputQuotas());
			f02K.setValue(farmer.getQuota());
		}
		if (f==2) {
			f03A.setValue(farmer.getCodigo());
			f03B.setValue(farmer.getIncomes());
			f03C.setValue(farmer.getQuotaIndex());
			f03D.setValue(farmer.getAccrualQuota());
			f03E.setValue(farmer.getInputQuotas());
			f03K.setValue(farmer.getQuota());
		}
		if (f==3) {
			f04A.setValue(farmer.getCodigo());
			f04B.setValue(farmer.getIncomes());
			f04C.setValue(farmer.getQuotaIndex());
			f04D.setValue(farmer.getAccrualQuota());
			f04E.setValue(farmer.getInputQuotas());
			f04K.setValue(farmer.getQuota());
		}
		if (f==4) {
			f05A.setValue(farmer.getCodigo());
			f05B.setValue(farmer.getIncomes());
			f05C.setValue(farmer.getQuotaIndex());
			f05D.setValue(farmer.getAccrualQuota());
			f05E.setValue(farmer.getInputQuotas());
			f05K.setValue(farmer.getQuota());
		}
	}
	
	@Override
	public void setCallback(IMod3902015CallBack callback) {
		this.callback = callback;
	}
	
	@Override
	public void refresh(Mod3902015 m390) {
		setValue(m390);
	}
}
