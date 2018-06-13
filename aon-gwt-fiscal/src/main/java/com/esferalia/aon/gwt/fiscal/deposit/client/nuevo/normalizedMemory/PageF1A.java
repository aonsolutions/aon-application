package com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory;

import java.text.ParseException;
import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.Deposit;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositFooterKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class PageF1A extends PageAbs {

	interface PageBinder extends UiBinder<Widget, PageF1A> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);

	@UiField DoubleBox A18009010;
	@UiField DoubleBox A18009020;
	@UiField DoubleBox A18009030;
	@UiField DoubleBox A18009040;
	
	String codeAux;
	
	public PageF1A(Deposit deposit) {
		super(deposit);
		
		A18009010 = new DoubleBox();
		A18009020 = new DoubleBox();
		A18009030 = new DoubleBox();
		A18009040 = new DoubleBox();

		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		
		initializeTable();
	}

	private void init(){
		keyExe("8009010", A18009010, "double", true);
		keyExe("8009020", A18009020, "double", true);
		keyExe("8009030", A18009030, "double", true);
		keyExe("8009040", A18009040, "double", true);
	}
	
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
		table.getColumnFormatter().setWidth(2, "200px");
		table.getColumnFormatter().setWidth(3, "200px");
		table.getColumnFormatter().setWidth(4, "200px");
		table.getColumnFormatter().setWidth(5, "200px");
		table.getColumnFormatter().setWidth(6, "200px");
		table.getColumnFormatter().setWidth(7, "200px");
	
		int row = 0;
		table.setWidget(row, 0, new Label("Fecha"));
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 1, new Label("Concepto"));
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 2, new Label("Fecha de acuerdo de junta general"));
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 3, new Label("N\u00AA de acciones / participaciones"));
		table.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 4, new Label("Nominal"));
		table.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonTextCenter());
		
		table.setWidget(row, 5, new Label("Capital social porcentaje"));
		table.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 6, new Label("Precio o contraprestaci\u00F3n"));
		table.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 7, new Label("Saldo despu\u00E9s de la operaci\u00F3n"));
		table.getFlexCellFormatter().addStyleName(row, 7, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 7, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 7, AON.AON_CSS.aonTextCenter());
	
		++row;

		for(Integer i = 0; i< D2DepositConstants.A1_ABREVIATE_KEYS_1.length; i+=8){
			D2DepositFooterKey[] d2 = new D2DepositFooterKey[]{
					D2DepositConstants.A1_ABREVIATE_KEYS_1[i],
					D2DepositConstants.A1_ABREVIATE_KEYS_1[i+1],
					D2DepositConstants.A1_ABREVIATE_KEYS_1[i+2],
					D2DepositConstants.A1_ABREVIATE_KEYS_1[i+3],
					D2DepositConstants.A1_ABREVIATE_KEYS_1[i+4],
					D2DepositConstants.A1_ABREVIATE_KEYS_1[i+5],
					D2DepositConstants.A1_ABREVIATE_KEYS_1[i+6],
					D2DepositConstants.A1_ABREVIATE_KEYS_1[i+7],

			};
			for (Integer j = 0; j < d2.length ;j++) {
				if(j == 0 || j == 2)
					paintDateKeyField(table, d2[j], row, j);
				else if(j == 1)
					paintListKeyField(table, d2[j], row, j);
				else
					paintDoubleKeyField(table ,d2[j],row,j);
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
				text.setTitle(code);
				onEdit(code, DATE_FORMAT.format(text.getValue()));	
			}
		});
		
		if(getMap().containsKey(key.getCode())){
			String datestr = getMap().get(key.getCode());

			getDeposit().getInma().getDate(datestr, new AsyncCallback<Date>() {
				@Override
				public void onSuccess(Date result) {
					text.setValue(result); 
				}
				
				@Override
				public void onFailure(Throwable caught) {}
			} );
		}

		text.addStyleName(AON.AON_CSS.aonFiscalMarginLeft());
		text.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());
		
		panel.add(text);
		
		tab.setWidget(row, col, panel);
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
	}
	
	private void paintDoubleKeyField(FlexTable tab, D2DepositFooterKey key,  int row, int col){
		FlowPanel panel = new FlowPanel();
		String codeId = key.getCode();
		
		final DoubleBox text = new DoubleBox();
		text.setTitle(codeId);
		codeAux = codeId;
		text.addChangeHandler(new ChangeHandler() {
			String code = codeAux;
			@Override
			public void onChange(ChangeEvent event) {
				try {
					if (AonStringUtils.isEmpty(text.getText())) {
						text.setValue(0.0,false);
					}
					Double d = text.getValueOrThrow();
					text.setTitle(code);
					onEdit(code, Double.toString(AonMathUtils.round(d)).toString());
					
				} catch (ParseException e) {
					// nothing.
				}
			}
		});
		if(getMap().containsKey(key.getCode())){
			Double d =Double.parseDouble(getMap().get(key.getCode()));
			text.setValue(d);
		}
		else text.setValue(0.0);
		text.addStyleName(AON.AON_CSS.aonFiscalMarginLeft());
		text.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());
		
		panel.add(text);
		
		tab.setWidget(row, col, panel);
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
	}
 	
	private void paintListKeyField(FlexTable tab, D2DepositFooterKey key,  int row, int col){
		FlowPanel panel = new FlowPanel();
		String codeId = key.getCode();
		
		final ListBox text = new ListBox();
		conceptListBox(text);
		codeAux = codeId;
		text.setTitle(codeId);
		text.addChangeHandler(new ChangeHandler() {
			String code = codeAux;
			@Override
			public void onChange(ChangeEvent event) {
		
					if (AonStringUtils.isEmpty(text.getSelectedItemText())) {
						text.setSelectedIndex(0);
					}
					String d = text.getSelectedItemText();
					
					onEdit(code, d.toString());		
			}
		});
		if(getMap().containsKey(key.getCode())){
			String d = getMap().get(key.getCode());
			for(Integer i = 0; i< text.getItemCount(); i++){
				if(text.getItemText(i).equals(d))
					text.setSelectedIndex(i);
			}
		}
		else text.setSelectedIndex(0);
		text.addStyleName(AON.AON_CSS.aonFiscalMarginLeft());
		text.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());
		
		panel.add(text);
		
		tab.setWidget(row, col, panel);
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
	}
	
	String key2Aux;DoubleBox dlAux;
	private void keyExe( String key2, Widget w, String type, Boolean enable) {
		key2Aux = key2;
		if(type.equals("double")){
			DoubleBox dl = (DoubleBox) w;
			if(getMap().containsKey(key2)){
				Double d = Double.parseDouble(getMap().get(key2));
				dl.setValue(d);
				dl.setEnabled(enable);
			}
			dlAux = dl;
			dl.addChangeHandler(new ChangeHandler() {
				String key2 = key2Aux ;
				DoubleBox dl = dlAux;
				@Override
				public void onChange(ChangeEvent event) {
					onEdit(key2,Double.toString(AonMathUtils.round(dl.getValue())));
				}
			});
		}
	}
	
	private void conceptListBox(ListBox lb ){
		lb.addItem("");
		lb.addItem("AO");
		lb.addItem("AD");
		lb.addItem("AI");
		lb.addItem("AL");
		lb.addItem("ED");
		lb.addItem("EL");
		lb.addItem("RD");
		lb.addItem("RL");
		lb.addItem("AG");
		lb.addItem("AG");
		lb.addItem("PR");
	}
	
	
	protected void onEdit(String key, String value) {
		onEdit(key, value, false);
	}
}
