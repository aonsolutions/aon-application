package com.esferalia.aon.gwt.fiscal.client.mod200.e2016;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.IbanTextBox;
import com.esferalia.aon.gwt.common.client.widget.IbanTextBox.IbanSuggestion;
import com.esferalia.aon.gwt.fiscal.client.mod200.e2016.Mod2002016Object.IMod200ChangeListener;
import com.esferalia.aon.gwt.fiscal.client.mod200.e2016.Model2002016.Model200PageCallback;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.DoubleVariable2016;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.Widget;

public class Page19 extends PageAbs {

	interface PageBinder extends
			UiBinder<Widget, Page19> {
	}
	
	private static final PageBinder pageBinder = GWT
			.create(PageBinder.class);

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
	RadioButton payTypeU;	
	@UiField
	CheckBox zeroQuota;
	@UiField
	DoubleBox amountD;
	@UiField
	DoubleBox amountP;
	@UiField(provided=true)
	IbanTextBox ibanD;
	@UiField(provided=true)
	IbanTextBox ibanP;
	
	
	public Page19( Model200PageCallback callback ) {
		super(callback);
		ibanD = new IbanTextBox(getSuggestOracle(),true);
		ibanP = new IbanTextBox(getSuggestOracle(),true);
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		
		callback.getMod200Object().register( new IMod200ChangeListener() {
			
			@Override
			public void mod200Changed(Mod2002016 mod200) {
				dumpPay(mod200);
			}
		});
	}

	@Override
	protected void initializeTable() {
		table.setWidth("100%");
		table.setCellSpacing(0);
		table.getColumnFormatter().setWidth(1, "200px");
		
		Mod2002016Key key = Mod2002016Key.LQ552;
		Label desc = new Label(key.getDescription() );
		desc.setStyleName(AON.AON_CSS.aonBold());
		table.setWidget(0, 0, desc);
		table.getFlexCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonFiscalBorderBottom());
		FlowPanel panel = new FlowPanel();
		BoxLabel code = new BoxLabel(key.getCode( callback.getMod200Object().getAdministration() ));
		panel.add(code);
		DoubleBox text = new DoubleBox();
		text.setValue(callback.getMod200Object().getDoubleValue(key));
		text.addStyleName(AON.AON_CSS.aonFiscalMarginLeft());
		text.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());
		text.setEnabled(false);
		panel.add(text);
		getInputs().put(key, text);
		panel.addStyleName(AON.AON_CSS.aonFiscalPaddingRight());
		table.setWidget(0, 1, panel);
		table.getFlexCellFormatter().addStyleName(0, 1, AON.AON_CSS.aonTextRight());
		table.getFlexCellFormatter().addStyleName(0, 1, AON.AON_CSS.aonNowrap());
		
		key = Mod2002016Key.LQ562;
		desc = new Label(key.getDescription() );
		desc.setStyleName(AON.AON_CSS.aonBold());
		table.setWidget(1, 0, desc);
		table.getFlexCellFormatter().setStyleName(1, 0, AON.AON_CSS.aonFiscalBorderBottom());
		panel = new FlowPanel();
		code = new BoxLabel(key.getCode( callback.getMod200Object().getAdministration() ));
		panel.add(code);
		text = new DoubleBox();
		text.setValue(callback.getMod200Object().getDoubleValue(key));
		text.addStyleName(AON.AON_CSS.aonFiscalMarginLeft());
		text.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());
		text.setEnabled(false);
		panel.add(text);
		getInputs().put(key, text);
		panel.addStyleName(AON.AON_CSS.aonFiscalPaddingRight());
		table.setWidget(1, 1, panel);
		table.getFlexCellFormatter().addStyleName(1, 1, AON.AON_CSS.aonTextRight());
		table.getFlexCellFormatter().addStyleName(1, 1, AON.AON_CSS.aonNowrap());
		
		key = Mod2002016Key.BN621;
		desc = new Label(key.getDescription() );
		desc.setStyleName(AON.AON_CSS.aonBold());
		table.setWidget(2, 0, desc);
		table.getFlexCellFormatter().setStyleName(2, 0, AON.AON_CSS.aonFiscalBorderBottom());
		panel = new FlowPanel();
		code = new BoxLabel(key.getCode( callback.getMod200Object().getAdministration() ));
		panel.add(code);
		text = new DoubleBox();
		text.setValue(callback.getMod200Object().getDoubleValue(key));
		text.addStyleName(AON.AON_CSS.aonFiscalMarginLeft());
		text.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());
		text.setEnabled(false);
		panel.add(text);
		getInputs().put(key, text);
		table.setWidget(2, 1, panel);
		table.getFlexCellFormatter().addStyleName(2, 1, AON.AON_CSS.aonTextRight());
		table.getFlexCellFormatter().addStyleName(2, 1, AON.AON_CSS.aonNowrap());
	}

	@Override
	public void dump() {
		super.dump();
		dumpPay(callback.getMod200Object().getMod200());
	}
	
	private void dumpPay(Mod2002016 mod200) {
		devTypeR.setValue(false);
		devTypeT.setValue(false);
		amountD.setValue(0.0);
		ibanD.setValue(null);
		payTypeE.setValue(false);
		payTypeU.setValue(false);
		amountP.setValue(0.0);
		ibanP.setValue(null);
		zeroQuota.setValue(false);
		zeroQuota.setEnabled(false);
		if (AonStringUtils.isEmpty(mod200.getResultType())) {
			payPanel.setVisible(true);
			zeroPanel.setVisible(true);
			devPanel.setVisible(true);
		} else if ("D".equals( mod200.getResultType()) ) {
			payPanel.setVisible(false);
			zeroPanel.setVisible(false);
			devPanel.setVisible(true);
			devTypeR.setValue("R".equals(mod200.getDevType()));
			devTypeT.setValue("D".equals(mod200.getDevType()));
			ibanD.setValue(mod200.getIban(),mod200.getBic());
			amountD.setValue( mod200.getAmount() );	
		} else if ("I".equals( mod200.getResultType()) ) {
			payPanel.setVisible(true);
			zeroPanel.setVisible(false);
			devPanel.setVisible(false);
			payTypeU.setValue("U".equals(mod200.getPayType()));
			payTypeE.setValue("H".equals(mod200.getPayType()));
			ibanP.setValue(mod200.getIban(),mod200.getBic());
			amountP.setValue( mod200.getAmount() );	
		} else if ("C".equals( mod200.getResultType()) ) {
			payPanel.setVisible(false);
			zeroPanel.setVisible(true);
			devPanel.setVisible(false);
			zeroQuota.setValue(true);
		}
	}

	public void populate(Mod2002016Object mod200Object) {
		
		DoubleVariable2016 dv =  mod200Object.getMod200().getVariable(Mod2002016Key.BN621);
		Double value = dv==null?0.0:dv.getValue();
		if (AonMathUtils.round(value) == 0.0) {
			mod200Object.getMod200().setAmount(AonMathUtils.round(value));
			mod200Object.getMod200().setResultType("C");
			mod200Object.getMod200().setDevType(null);	
			mod200Object.getMod200().setPayType(null);
			mod200Object.getMod200().setIban(null);
			mod200Object.getMod200().setBic(null);
		} else if (AonMathUtils.round(value) < 0.0) {
			mod200Object.getMod200().setAmount(AonMathUtils.round(value * -1));
			mod200Object.getMod200().setResultType("D");
			mod200Object.getMod200().setDevType(devTypeR.getValue()?"R":"D");
			mod200Object.getMod200().setPayType(null);
			mod200Object.getMod200().setIban(ibanD.getValue());
			mod200Object.getMod200().setBic(ibanD.getBic());
		} else {
			mod200Object.getMod200().setAmount(AonMathUtils.round(value));
			mod200Object.getMod200().setResultType("I");
			mod200Object.getMod200().setDevType(null);
			mod200Object.getMod200().setPayType(payTypeE.getValue()?"H":"U");
			mod200Object.getMod200().setIban(ibanP.getValue());
			mod200Object.getMod200().setBic(ibanP.getBic());
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
	@UiHandler("payTypeU")
	void onPayTypeUClick(ClickEvent event) {
	}
	@UiHandler("payTypeU")
	void onZeroQuota(ClickEvent event) {
	}
	
	class EnterpriseSuggestOracle extends MultiWordSuggestOracle {
		@Override
		public void requestSuggestions(final Request request,
				final Callback callback) {
			
			Page19.this.callback.getMod200Object().getCompanyBanks ( 
					new AsyncCallback<LinkedList<CompanyBank>>() {

						public void onFailure(Throwable caught) {
							Window.alert("Error while getting suggestions.");
						}

						public void onSuccess(LinkedList<CompanyBank> result) {
							LinkedList<Suggestion> suggestions = new LinkedList<Suggestion>();
							if (result != null) {
								for (final CompanyBank cb : result) {
									suggestions.add(new IbanTextBox.IbanSuggestion(cb));
								}
							}
							Response resp = new Response(suggestions);
							callback.onSuggestionsReady(request, resp);
						}
					});
		}
	}
	
	private SuggestOracle getSuggestOracle() {
		return new EnterpriseSuggestOracle();
	}
	
	@UiHandler("ibanD")
	void onIbanD(SelectionEvent<Suggestion> event) {
		Suggestion suggestion = event.getSelectedItem();
		if (suggestion instanceof IbanSuggestion) {
			IbanSuggestion is = (IbanSuggestion) suggestion;
			ibanD.setValue(is.getIbanContainer().getIBan(), is.getIbanContainer().getBic());	
		} else {
			ibanD.setValue(suggestion.getReplacementString());	
		}
		
	}
	@UiHandler("ibanP")
	void onIbanP(SelectionEvent<Suggestion> event) {
		Suggestion suggestion = event.getSelectedItem();
		if (suggestion instanceof IbanSuggestion) {
			IbanSuggestion is = (IbanSuggestion) suggestion;
			ibanP.setValue(is.getIbanContainer().getIBan(), is.getIbanContainer().getBic());	
		} else {
			ibanP.setValue(suggestion.getReplacementString());	
		}
	}

	@Override
	protected void populate() {}
}
