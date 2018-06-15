package com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory;

import java.text.ParseException;
import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.Deposit;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
import com.esferalia.aon.occam.api.model.type.Country;
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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class PageITR extends PageAbs {

	interface PageBinder extends UiBinder<Widget, PageITR> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);
	
	private static final String ITR8080829TXT = "La entidad est\u00e1 sujeta a la obligaci\u00f3n de identificar al titular real proque no cotiza en mercados regulados";

	@UiField Label ITR8080829lbl;
	@UiField CheckBox ITR8080829;
	
	@UiField(provided = true) FlexTable table2;
	@UiField(provided = true) FlexTable table3;
	
	String codeAux;
	
	public PageITR(Deposit deposit) {
		super(deposit);
		
		table2 = new FlexTable();
		table3 = new FlexTable();
		
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		
		initializeTable();
	}

	private void init(){
		ITR8080829lbl.setText(ITR8080829TXT);
		if(getMap().containsKey(D2DepositHeaderKey.ITR8080829.getCode())){
			ITR8080829.setValue(getMap().get(D2DepositHeaderKey.ITR8080829.getCode()).equals("1")); 
		}

		ITR8080829.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				onEdit(D2DepositHeaderKey.ITR8080829.getCode(), ITR8080829.getValue() ? "1" : "2", false);
			}
		});
	}
	
	@Override
	protected void initializeTable() {
		init();
		table();
		table2();
		table3();
	}
	
	private void table(){
		table.setWidth("100%");
		table.setCellSpacing(0);
		table.getColumnFormatter().setWidth(0, "200px");
		table.getColumnFormatter().setWidth(1, "200px");
		table.getColumnFormatter().setWidth(2, "70px");
		table.getColumnFormatter().setWidth(3, "70px");
		table.getColumnFormatter().setWidth(4, "70px");
		table.getColumnFormatter().setWidth(5, "70px");
		table.getColumnFormatter().setWidth(6, "70px");
	
		int row = 0;
		table.setWidget(row, 0, new Label("Nombre y Apellidos"));
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 1, new Label("DNI / C\u00f3digo de Identificaci\u00f3n Extranjero"));
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 2, new Label("Fecha de Nacimiento"));
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 3, new Label("Nacionalidad"));
		table.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 4, new Label("Pa\u00eds de Residencia"));
		table.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonTextCenter());
		
		table.setWidget(row, 5, new Label("% Participaci\u00f3n Directa"));
		table.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 6, new Label("% Participaci\u00f3n Indirecta"));
		table.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonTextCenter());
	
		++row;
		
		for(Integer i = 0; i< D2DepositConstants.ITR_KEYS_1.length; i+=7){
			D2DepositHeaderKey[] d2 = new D2DepositHeaderKey[]{
					D2DepositConstants.ITR_KEYS_1[i],
					D2DepositConstants.ITR_KEYS_1[i+1],
					D2DepositConstants.ITR_KEYS_1[i+2],
					D2DepositConstants.ITR_KEYS_1[i+3],
					D2DepositConstants.ITR_KEYS_1[i+4],
					D2DepositConstants.ITR_KEYS_1[i+5],
					D2DepositConstants.ITR_KEYS_1[i+6]
			};
			for (Integer j = 0; j < d2.length ;j++) {
				if(j == 0 || j == 1) {
					paintTextKeyField(table, d2[j], row, j);
				} else if(j == 2) {
					paintDateKeyField(table, d2[j], row, j);
				} else if(j == 3 || j == 4) {
					paintListKeyField(table, d2[j], row, j);
				} else if(j == 5 || j == 6) {
					paintDoubleKeyField(table, d2[j], row, j);
				}
			}
			++row;		
		}
	}
	
	private void table2(){
		table2.setWidth("100%");
		table2.setCellSpacing(0);
		table2.getColumnFormatter().setWidth(0, "200px");
		table2.getColumnFormatter().setWidth(1, "200px");
		table2.getColumnFormatter().setWidth(2, "70px");
		table2.getColumnFormatter().setWidth(3, "70px");
		table2.getColumnFormatter().setWidth(4, "70px");
	
		int row = 0;
		table2.setWidget(row, 0, new Label("Nombre y Apellidos"));
		table2.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
		table2.setWidget(row, 1, new Label("DNI / C\u00f3digo de Identificaci\u00f3n Extranjero"));
		table2.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table2.setWidget(row, 2, new Label("Fecha de Nacimiento"));
		table2.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		table2.setWidget(row, 3, new Label("Nacionalidad"));
		table2.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextCenter());
		table2.setWidget(row, 4, new Label("Pa\u00eds de Residencia"));
		table2.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonTextCenter());
		
		++row;
		
		for(Integer i = 0; i< D2DepositConstants.ITR_KEYS_2.length; i+=5){
			D2DepositHeaderKey[] d2 = new D2DepositHeaderKey[]{
					D2DepositConstants.ITR_KEYS_2[i],
					D2DepositConstants.ITR_KEYS_2[i+1],
					D2DepositConstants.ITR_KEYS_2[i+2],
					D2DepositConstants.ITR_KEYS_2[i+3],
					D2DepositConstants.ITR_KEYS_2[i+4]
			};
			for (Integer j = 0; j < d2.length ;j++) {
				if(j == 0 || j == 1) {
					paintTextKeyField(table2, d2[j], row, j);
				} else if(j == 2) {
					paintDateKeyField(table2, d2[j], row, j);
				} else if(j == 3 || j == 4) {
					paintListKeyField(table2, d2[j], row, j);
				}
			}
			++row;		
		}
	}
	
	
	
	private void table3(){
		table3.setWidth("100%");
		table3.setCellSpacing(0);
		table3.getColumnFormatter().setWidth(0, "150px");
		table3.getColumnFormatter().setWidth(1, "70px");
		table3.getColumnFormatter().setWidth(2, "200px");
		table3.getColumnFormatter().setWidth(3, "150px");
		table3.getColumnFormatter().setWidth(4, "70px");
		table3.getColumnFormatter().setWidth(5, "200px");
		table3.getColumnFormatter().setWidth(6, "150px");
	
		int row = 0;
		table3.setWidget(row, 0, new Label("DNI / C\u00f3digo de Identificaci\u00f3n Extranjero"));
		table3.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table3.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table3.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
		table3.setWidget(row, 1, new Label("Nivel en la cadena de control"));
		table3.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table3.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table3.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table3.setWidget(row, 2, new Label("Denominaci\u00f3n Social"));
		table3.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table3.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table3.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		table3.setWidget(row, 3, new Label("NIF / C\u00f3digo de Identificaci\u00f3n Extranjero"));
		table3.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBold());
		table3.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBorderBottom());
		table3.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextCenter());
		table3.setWidget(row, 4, new Label("Nacionalidad"));
		table3.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBold());
		table3.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBorderBottom());
		table3.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonTextCenter());
		
		table3.setWidget(row, 5, new Label("Domicilio Social"));
		table3.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonBold());
		table3.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonBorderBottom());
		table3.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonTextCenter());
		table3.setWidget(row, 6, new Label("Datos Registrales / LEI"));
		table3.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonBold());
		table3.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonBorderBottom());
		table3.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonTextCenter());
	
		++row;
		
		for(Integer i = 0; i< D2DepositConstants.ITR_KEYS_3.length; i+=7){
			D2DepositHeaderKey[] d2 = new D2DepositHeaderKey[]{
					D2DepositConstants.ITR_KEYS_3[i],
					D2DepositConstants.ITR_KEYS_3[i+1],
					D2DepositConstants.ITR_KEYS_3[i+2],
					D2DepositConstants.ITR_KEYS_3[i+3],
					D2DepositConstants.ITR_KEYS_3[i+4],
					D2DepositConstants.ITR_KEYS_3[i+5],
					D2DepositConstants.ITR_KEYS_3[i+6]
			};
			for (Integer j = 0; j < d2.length ;j++) {
				if(j == 4) {
					paintListKeyField(table3, d2[j], row, j);	
				} else {
					paintTextKeyField(table3, d2[j], row, j);
				}
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
	
	private void paintTextKeyField(FlexTable tab, D2DepositHeaderKey key,  int row, int col){
		FlowPanel panel = new FlowPanel();
		String codeId = key.getCode();
		
		final TextBox text = new TextBox();
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
					onEdit(code, d, false);
				} catch (ParseException e) {
					// nothing.
				}
			}
		});
		if(getMap().containsKey(key.getCode())){
			String d = getMap().get(key.getCode());
			text.setValue(d);
		} else text.setValue("");
		text.addStyleName(AON.AON_CSS.aonFiscalMarginLeft());
		text.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());

		panel.add(text);
		
		tab.setWidget(row, col, panel);
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
	}
	
	private void paintDateKeyField(FlexTable tab, D2DepositHeaderKey key,  int row, int col){
		FlowPanel panel = new FlowPanel();
		String codeId = key.getCode();
		
		final DateBox text = new DateBox();
		text.setWidth("70px");
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
	
	private void paintDoubleKeyField(FlexTable tab, D2DepositHeaderKey key,  int row, int col){
		FlowPanel panel = new FlowPanel();
		String codeId = key.getCode();
		
		final DoubleBox text = new DoubleBox();
		text.setWidth("50px");
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
 	
	private void paintListKeyField(FlexTable tab, D2DepositHeaderKey key,  int row, int col){
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
	
	private void conceptListBox(ListBox lb){
		lb.addItem("");
		for(Integer i = 0 ; i < Country.values().length; i++) {
			lb.addItem(Country.values()[i].getIso2());
		}
	}
	
	protected void onEdit(String key, String value) {
		onEdit(key, value, false);
	}
}
