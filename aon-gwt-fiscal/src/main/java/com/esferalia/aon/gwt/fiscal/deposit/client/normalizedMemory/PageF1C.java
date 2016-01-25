package com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory;

import java.text.ParseException;
import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositFooterKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class PageF1C extends PageAbs {

	interface PageBinder extends UiBinder<Widget, PageF1C> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);

	String codeAux;

	public PageF1C() {
		super();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
	}
	
	public PageF1C(Enterprise enterprise, NormalizedMemory nm) {
		super();
		this.enterprise = enterprise;
		this.normalizedMemory = nm;
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
	}

	private void init(){}
	
	@Override
	protected void initializeTable() {
		init();
		table();
	}
	
	private void table(){
		table.setWidth("100%");
		table.setCellSpacing(0);
		table.getColumnFormatter().setWidth(0, "200px");
		table.getColumnFormatter().setWidth(1, "200px");
		
		int row = 0;
		table.setWidget(row, 0, new Label("Fecha acuerdo"));
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 1, new Label("Transcripci\u00F3n literal del acuerdo"));
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		
		++row;
		for(Integer i = 0; i< D2DepositConstants.A2_ABREVIATE_KEYS.length; i+=2){
			D2DepositFooterKey[] d2 = new D2DepositFooterKey[]{
					D2DepositConstants.A2_ABREVIATE_KEYS[i],
					D2DepositConstants.A2_ABREVIATE_KEYS[i+1],
			};
			for (Integer j = 0; j < d2.length ;j++) {
				if(j == 1)
					paintTextKeyField(table, d2[j], row, j);
				else if(j == 0  )
					paintDateKeyField(table, d2[j], row, j);
			}
			++row;		
		}
	}

	protected int paintKey(FlexTable tab, D2DepositKey[] keys,  int row) {
		for (Integer i = 0; i < keys.length ; i++) {
			paintKeyField(tab,keys[i],row,i+1);
		}
		return  ++row;
	}
	
	private void paintTextKeyField(FlexTable tab, D2DepositFooterKey key,  int row, int col){
		FlowPanel panel = new FlowPanel();
		String codeId = key.getCode();
		
		final TextBox text = new TextBox();
		text.setTitle(codeId);
		text.setStyleName(AON.AON_CSS.aonInputText());
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
					text.setTitle(code);
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
		//text.setEnabled(!disabled);
		panel.add(text);
		
		tab.setWidget(row, col, panel);
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
	}

	private void paintDateKeyField(FlexTable tab, D2DepositFooterKey key,  int row, int col){
		FlowPanel panel = new FlowPanel();
		String codeId = key.getCode();
		
		final DateBox text = new DateBox();
		text.setFormat(new DateBox.DefaultFormat(AON.DATE_FORMAT));
		text.setTitle(codeId);
		text.setStyleName(AON.AON_CSS.aonInputText());
		codeAux = codeId;
		text.addValueChangeHandler(new ValueChangeHandler<Date>() {
			String code = codeAux;

			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				
					
					Date d = text.getValue();
					Integer day = d.getDate();
					Integer month = d.getMonth()+1;
					Integer year = d.getYear()+1900;
					String value = day+"."+month+"."+year;
					
					text.addStyleName(AON.AON_CSS.aonChanged());
					text.setTitle(code);
					normalizedMemory.saveButton.setEnabled(true);
					normalizedMemory.cancelButton.setVisible(true);
					onEdit(code, value);
					
								
			}
		});
		
		if(mapDraft.containsKey(key.getCode())){
			String datestr = mapDraft.get(key.getCode());

			inma.getDate(datestr, new AsyncCallback<Date>() {
				@Override
				public void onSuccess(Date result) {
					text.setValue(result); 
					
				}
				
				@Override
				public void onFailure(Throwable caught) {
					
				}
			} );
		}

		text.addStyleName(AON.AON_CSS.aonFiscalMarginLeft());
		text.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());
		if(!map.get(key.getCode()).equals(mapDraft.get(key.getCode()))){
			text.addStyleName(AON.AON_CSS.aonChanged());
		}
		//text.setEnabled(!disabled);
		panel.add(text);
		
		tab.setWidget(row, col, panel);
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
	}
	
	DoubleBox dlAux;
	@Override
	protected void onEdit(String key, String value) {
		if(mapDraft.containsKey(key))
			mapDraft.remove(key);
		mapDraft.put(key, value);
		normalizedMemory.getD2Deposit2014().setMapDraft(mapDraft);
		normalizedMemory.getD2Deposit2014().setModify(true);
	}
}
