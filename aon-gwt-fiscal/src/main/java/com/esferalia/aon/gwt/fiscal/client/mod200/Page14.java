package com.esferalia.aon.gwt.fiscal.client.mod200;

import static com.esferalia.aon.gwt.fiscal.client.mod200.Model200.RESOURCES;

import com.esferalia.aon.gwt.common.client.widget.DoubleTextBox;
import com.esferalia.aon.gwt.common.client.widget.IbanTextBox;
import com.esferalia.aon.gwt.common.shared.AonUtil;
import com.esferalia.aon.gwt.fiscal.shared.mod200.DoubleVariable;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

public class Page14 extends PageAbs {

	interface PageBinder extends
			UiBinder<Widget, Page14> {
	}
	
	private static final PageBinder pageBinder = GWT
			.create(PageBinder.class);

	public Page14() {
		super();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
	}
	
	@UiField
	Panel devPanel;
	@UiField
	Panel payPanel;
	@UiField
	Panel zeroPanel;
	
	@UiField
	RadioButton devTypeR;
	@UiField
	RadioButton devTypeT;
	@UiField
	RadioButton payTypeE;
	@UiField
	RadioButton payTypeA;	
	@UiField
	CheckBox zeroQuota;
	@UiField
	DoubleTextBox amountD;
	@UiField
	DoubleTextBox amountP;
	@UiField
	IbanTextBox ibanD;
	@UiField
	IbanTextBox ibanP;
	
	
	@Override
	protected void initializeTable() {
		table.setWidth("100%");
		table.setCellSpacing(0);
		table.getColumnFormatter().setWidth(1, "250px");
		
		Mod200Key key = Mod200Key.LQ552;
		Label desc = new Label(key.getDescription() );
		desc.setStyleName(RESOURCES.css().aonBold());
		table.setWidget(0, 0, desc);
		table.getFlexCellFormatter().setStyleName(0, 0, RESOURCES.css().aonMod200BorderBottom());
		FlowPanel panel = new FlowPanel();
		BoxLabel code = new BoxLabel(key.getCode( mod200Object.getAdministration() ));
		panel.add(code);
		DoubleTextBox text = new DoubleTextBox();
		text.setValue(mod200Object.getDoubleValue(key));
		text.addStyleName(RESOURCES.css().aonMod200MarginLeft());
		text.addStyleName(RESOURCES.css().aonMod200PaddingLeft());
		text.setChangeDisplayStyleName(RESOURCES.css().aonValueChanged());
		text.setEnabled(false);
		panel.add(text);
		getInputs().put(key, text);
		panel.addStyleName(RESOURCES.css().aonMod200PaddingRight());
		table.setWidget(0, 1, panel);
		table.getFlexCellFormatter().addStyleName(0, 1, RESOURCES.css().aonTextRight());
		table.getFlexCellFormatter().addStyleName(0, 1, RESOURCES.css().aonNowrap());
		
		key = Mod200Key.LQ562;
		desc = new Label(key.getDescription() );
		desc.setStyleName(RESOURCES.css().aonBold());
		table.setWidget(1, 0, desc);
		table.getFlexCellFormatter().setStyleName(1, 0, RESOURCES.css().aonMod200BorderBottom());
		panel = new FlowPanel();
		code = new BoxLabel(key.getCode( mod200Object.getAdministration() ));
		panel.add(code);
		text = new DoubleTextBox();
		text.setValue(mod200Object.getDoubleValue(key));
		text.addStyleName(RESOURCES.css().aonMod200MarginLeft());
		text.addStyleName(RESOURCES.css().aonMod200PaddingLeft());
		text.setChangeDisplayStyleName(RESOURCES.css().aonValueChanged());
		text.setEnabled(false);
		panel.add(text);
		getInputs().put(key, text);
		panel.addStyleName(RESOURCES.css().aonMod200PaddingRight());
		table.setWidget(1, 1, panel);
		table.getFlexCellFormatter().addStyleName(1, 1, RESOURCES.css().aonTextRight());
		table.getFlexCellFormatter().addStyleName(1, 1, RESOURCES.css().aonNowrap());
		
		key = Mod200Key.BN621;
		desc = new Label(key.getDescription() );
		desc.setStyleName(RESOURCES.css().aonBold());
		table.setWidget(2, 0, desc);
		table.getFlexCellFormatter().setStyleName(2, 0, RESOURCES.css().aonMod200BorderBottom());
		panel = new FlowPanel();
		code = new BoxLabel(key.getCode( mod200Object.getAdministration() ));
		panel.add(code);
		text = new DoubleTextBox();
		text.setValue(mod200Object.getDoubleValue(key));
		text.addStyleName(RESOURCES.css().aonMod200MarginLeft());
		text.addStyleName(RESOURCES.css().aonMod200PaddingLeft());
		text.setChangeDisplayStyleName(RESOURCES.css().aonValueChanged());
		text.setEnabled(false);
		panel.add(text);
		getInputs().put(key, text);
		table.setWidget(2, 1, panel);
		table.getFlexCellFormatter().addStyleName(2, 1, RESOURCES.css().aonTextRight());
		table.getFlexCellFormatter().addStyleName(2, 1, RESOURCES.css().aonNowrap());
	}

	@Override
	public void dump(Mod200Object mod200) {
		super.dump(mod200);
		
		devTypeR.setValue(false);
		devTypeT.setValue(false);
		amountD.setValue(0.0);
		ibanD.setValue(null);
		payTypeE.setValue(false);
		payTypeA.setValue(false);
		amountP.setValue(0.0);
		ibanP.setValue(null);
		zeroQuota.setValue(false);
		zeroQuota.setEnabled(false);
		
		if (AonUtil.isEmpty(mod200.getMod200().getResultType())) {
			payPanel.setVisible(true);
			zeroPanel.setVisible(true);
			devPanel.setVisible(true);
		} else if ("D".equals( mod200.getMod200().getResultType()) ) {
			payPanel.setVisible(false);
			zeroPanel.setVisible(false);
			devPanel.setVisible(true);
			devTypeR.setValue("R".equals(mod200.getMod200().getDevType()));
			devTypeT.setValue("T".equals(mod200.getMod200().getDevType()));
			ibanD.setValue(mod200.getMod200().getIban());
			amountD.setValue( mod200.getMod200().getAmount() );	
		} else if ("I".equals( mod200.getMod200().getResultType()) ) {
			payPanel.setVisible(true);
			zeroPanel.setVisible(false);
			devPanel.setVisible(false);
			payTypeA.setValue("A".equals(mod200.getMod200().getPayType()));
			payTypeE.setValue("E".equals(mod200.getMod200().getPayType()));
			ibanP.setValue(mod200.getMod200().getIban());
			amountP.setValue( mod200.getMod200().getAmount() );	
		} else if ("C".equals( mod200.getMod200().getResultType()) ) {
			payPanel.setVisible(false);
			zeroPanel.setVisible(true);
			devPanel.setVisible(false);
			zeroQuota.setValue(true);
		}
	}
	
	public void populate(Mod200Object mod200Object) {
		
		DoubleVariable dv =  mod200Object.getMod200().getVariable(Mod200Key.BN621);
		Double value = dv==null?0.0:dv.getValue();
		mod200Object.getMod200().setAmount(AonUtil.round(value));
		if (AonUtil.round(value) == 0.0) {
			mod200Object.getMod200().setResultType("C");
			mod200Object.getMod200().setDevType(null);	
			mod200Object.getMod200().setPayType(null);
			mod200Object.getMod200().setIban(null);
		} else if (AonUtil.round(value) < 0.0) {
			mod200Object.getMod200().setResultType("D");
			mod200Object.getMod200().setDevType(devTypeR.getValue()?"R":"T");
			mod200Object.getMod200().setPayType(null);
			mod200Object.getMod200().setIban(ibanD.getValue());
		} else {
			mod200Object.getMod200().setResultType("I");
			mod200Object.getMod200().setDevType(null);
			mod200Object.getMod200().setPayType(payTypeE.getValue()?"E":"A");
			mod200Object.getMod200().setIban(ibanP.getValue());
		}
	}

	@UiHandler("devTypeR")
	void onDevTypeRClick(ClickEvent event) {
	}
	@UiHandler("devTypeT")
	void onDevTypeTClick(ClickEvent event) {
	}
	@UiHandler("payTypeE")
	void onPayTypeEClick(ClickEvent event) {
	}
	@UiHandler("payTypeA")
	void onPayTypeAClick(ClickEvent event) {
	}
	@UiHandler("payTypeA")
	void onZeroQuota(ClickEvent event) {
	}
	
}
