package net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory;


import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2PDepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.Provinces;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

import net.aonsolutions.aon.gwt.ccaa.client.Deposit2;

public class PageH6 extends PageAbs {

	@UiField TextBox IDA01010; // document 
	@UiField TextBox IDA01020; // Enterprise name
	@UiField TextBox IDA01022; // Enterprise address
	@UiField TextBox IDA01023; // Municipio
	@UiField ListBox IDA01025; // Province Name
	@UiField TextBox year; // YEAR
	@UiField CheckBox SRP8080831; // YEAR
	@UiField TextBox SRP831001;
	@UiField TextArea SRP831002;
	@UiField TextArea SRP831003;
	@UiField CheckBox SRP831004;
	interface PageBinder extends UiBinder<Widget, PageH6> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);

	public PageH6(Deposit2 deposit) {
		super(deposit);
		IDA01010 = new TextBox();
		IDA01020 = new TextBox(); 
		IDA01022 = new TextBox();
		IDA01023 = new TextBox(); 
		IDA01025 = new ListBox();
		this.year = new TextBox();
		SRP8080831 = new CheckBox();
		
		SRP831001 = new TextBox();
		SRP831002 = new TextArea();
		SRP831003 = new TextArea();
		SRP831004 = new CheckBox();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		initializeTable();
	}
	
	private void listBoxItemAdd(ListBox lb) {
		for(Integer i = 0; i< D2DepositConstants.PROVINCES.length; i++){
			Provinces p = D2DepositConstants.PROVINCES[i];
			lb.addItem(p.getName());
		}
	}

	private void init(){
		this.year.setText(getYear().toString());
		keyExe("1010", IDA01010, "text", false);	
		keyExe("1020", IDA01020, "text", false);
		keyExe("1022", IDA01022, "text", false);
		keyExe("1023", IDA01023, "text", false);
		listBoxItemAdd(IDA01025);
		keyExe("1025", IDA01025, "list", false);
		keyExe("8080831", SRP8080831, "check", true);
		keyExe("831001", SRP831001, "text", true);
		keyExe("831002", SRP831002, "area", true);
		keyExe("831003", SRP831003, "area", true);
		keyExe("831004", SRP831004, "check", true);	
	}
	
	@Override
	protected void initializeTable() {
		init();		
		defineTable(table,"",D2PDepositConstants.SRP_KEYS);
	}
	
	String key2Aux;
	TextBox tAux;
	TextArea taAux;

	DateBox dAux;
	ListBox lbAux;
	DoubleBox dlAux;
	private void keyExe(String key2, Widget w, String type, Boolean enable) {
		key2Aux = key2;
		
		if(type.equals("label")){
			InlineLabel t = (InlineLabel) w;
			if(getMap().containsKey(key2)){
				t.setText(getMap().get(key2));
			}
		}
		if(type.equals("text")) {
			TextBox t = (TextBox) w;
			if(getMap().containsKey(key2)){
				t.setValue(getMap().get(key2) != null ? getMap().get(key2) : "");
				t.setEnabled(enable);
				
			}
			tAux = t;
			t.addChangeHandler(new ChangeHandler() {
				String key2 = key2Aux ;
				TextBox t = tAux;
				@Override
				public void onChange(ChangeEvent event) {
					onEdit(key2, t.getValue());
				}
			});
		}
		if(type.equals("area")) {
			TextArea ta = (TextArea) w;
			if(getMap().containsKey(key2)){
				ta.setValue(getMap().get(key2) != null ? getMap().get(key2) : "");
				ta.setEnabled(enable);
				
			}
			taAux = ta;
			ta.addChangeHandler(new ChangeHandler() {
				String key2 = key2Aux ;
				TextArea ta = taAux;
				@Override
				public void onChange(ChangeEvent event) {
					onEdit(key2, ta.getValue());
				}
			});
		}
		
		if(type.equals("check")) {
			CheckBox c = (CheckBox) w;
			if(getMap().containsKey(key2)){
				c.setValue(getMap().get(key2).equals("1")); 
				c.setEnabled(enable);
			}
			c.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				String key2 = key2Aux;
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					onEdit(key2, event.getValue()?"1":"0");
				}
			});
		}
		
		
		if(type.equals("list")){
			ListBox lb = (ListBox) w;
			if(getMap().containsKey(key2)){
				
				String value = getMap().get(key2);
				String value2 ="";
				for (Integer i = 0 ; i< D2DepositConstants.PROVINCES.length; i++){
		
					if(D2DepositConstants.PROVINCES[i].getId().equals(value)){
						value2 = D2DepositConstants.PROVINCES[i].getName();
					}
				}
				for (Integer i = 0; i< lb.getItemCount(); i++) {
					if(lb.getItemText(i).equals(value2)){
						lb.setSelectedIndex(i);
					}
				}
				lb.setEnabled(enable);
			}
			lbAux = lb;
			lb.addChangeHandler(new ChangeHandler() {
				String key2 = key2Aux ;
				ListBox lb = lbAux;
				@Override
				public void onChange(ChangeEvent event) {
					String value ="";
					for(Integer i = 0; i< D2DepositConstants.PROVINCES.length;i++){
						Provinces p = D2DepositConstants.PROVINCES[i];
						if(lb.getSelectedItemText().equals(p.getName())){
							value = p.getId(); 
						}
					}
					onEdit(key2, value);
					
					
				}
			});
		}
		
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
					onEdit(key2, dl.getValue().toString());
					
				}
			});
		}
	}
	
	protected void defineTable( FlexTable tab, String title, D2DepositHeaderKey[][] keys){
		Integer col = 0;
		Integer row = 0;
		tab.getColumnFormatter().addStyleName(col, AON.AON_CSS.aonWidthAuto());
		tab.getColumnFormatter().addStyleName(col, AON.AON_CSS.aonNowrap());
		col++;
		tab.getFlexCellFormatter().setColSpan(row, col, 2);
		tab.getColumnFormatter().addStyleName(col, AON.AON_CSS.aonWidth140());
		tab.setWidget(row, col, new Label("Volumen de negocio"));	
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBorderBottom());
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
	
		row++;
		
		String current_ej = AON.MSG.fiscalYear() + " " + getYear(); 
		String ant_ej = AON.MSG.fiscalYear() + " " + (getYear() -1);
		
		tab.setWidth("100%");
		tab.setCellSpacing(0);
		tab.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonWidthAuto());
		tab.getColumnFormatter().addStyleName(1, AON.AON_CSS.aonWidth130());
		tab.getColumnFormatter().addStyleName(2, AON.AON_CSS.aonWidth140());
		tab.getColumnFormatter().addStyleName(3, AON.AON_CSS.aonWidth140());

		tab.setWidget(row, 0, new Label(title));
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
		tab.setWidget(row, 1, new Label(current_ej));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextRight());
		tab.setWidget(row, 2, new Label(ant_ej));
		tab.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		tab.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		tab.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextRight());
		tab.setWidget(row, 3, new Label("N\u00famero de Operaciones"));
		tab.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBold());
		tab.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBorderBottom());
		tab.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextRight());
		++row;

		for (D2DepositHeaderKey[] innerKeys : keys) {
			row = paintKey(tab, innerKeys , row);
		}
	}
	
	protected int paintKey(FlexTable tab, D2DepositHeaderKey[] keys,  int row) {
		paintKeyDescription(tab, keys[0], row, 0);
		paintKeyField(tab,keys[0], row, 1, false);
		paintKeyField(tab,keys[1],row,2,false);
		paintKeyField(tab,keys[2],row,3,false);
		return  ++row;
	}
	
	protected void onEdit(String key, String value) {
		onEdit(key, value, false);
	}
}
