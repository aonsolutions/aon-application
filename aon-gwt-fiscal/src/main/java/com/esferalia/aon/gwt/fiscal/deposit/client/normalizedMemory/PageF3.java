package com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory;

import java.text.ParseException;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositFooterKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PageF3 extends PageAbs {

	interface PageBinder extends UiBinder<Widget, PageF3> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);

	public PageF3() {
		super();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
	}
	
	public PageF3(Enterprise enterprise, NormalizedMemory nm) {
		super();
		this.enterprise = enterprise;
		this.normalizedMemory = nm;

		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
	}

	@Override
	protected void initializeTable() {
		table.setWidth("100%");
		table.setCellSpacing(0);
		table.getColumnFormatter().setWidth(1, "200px");
		table.getColumnFormatter().setWidth(2, "200px");
		table.getColumnFormatter().setWidth(3, "200px");

		for(Integer i = 0; i< D2DepositConstants.H_ABREVIATE_KEYS.length; i++){
			paintTextKeyField(table,D2DepositConstants.H_ABREVIATE_KEYS[i] , i, 0);
		}
	}
	
	protected int paintKey(FlexTable tab, D2DepositKey[] keys,  int row) {
		paintKeyDescription(tab, keys[0], row, 0);
		for (Integer i = 0; i < keys.length ; i++) {
			paintKeyField(tab,keys[i],row,i+1);
		}
		return  ++row;
	}

	String codeAux;
	private void paintTextKeyField(FlexTable tab, D2DepositFooterKey key,  int row, int col){
		FlowPanel panel = new FlowPanel();
		String codeId = key.getCode();
		
		final TextBox text = new TextBox();
		text.setStyleName(AON.AON_CSS.aonInputText());
		text.setWidth("99%");
		codeAux = codeId;
		text.addChangeHandler(new ChangeHandler() {
			String code = codeAux;
			@Override
			public void onChange(ChangeEvent event) {
				try {
					if (AonStringUtils.isEmpty(text.getText())) {
						text.setValue("",false);
					}
					String d = text.getValueOrThrow();
					text.addStyleName(AON.AON_CSS.aonChanged());
					
					normalizedMemory.saveButton.setEnabled(true);
					normalizedMemory.cancelButton.setVisible(true);
					onEdit(code, d);
				
				} catch (ParseException e) {
					// nothing.
				}
			}
		});
		if(mapDraft.containsKey(key.getCode())){
			String d =mapDraft.get(key.getCode());
			text.setValue(d);
		}
		else text.setValue("");
		text.addStyleName(AON.AON_CSS.aonFiscalMarginLeft());
		text.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());
		if(!map.get(key.getCode()).equals(mapDraft.get(key.getCode()))){
			text.addStyleName(AON.AON_CSS.aonChanged());
		}
		panel.add(text);
		
		tab.setWidget(row, col, panel);
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
	}
}
