// DOCUMENTO SOBRE SERVICIOS A TERCEROS
package net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory;


import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
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
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

import net.aonsolutions.aon.gwt.ccaa.client.Deposit2;

public class PageH6 extends PageAbs {

	@UiField HTMLPanel panel1;
	@UiField HTMLPanel panel2;
	
	@UiField TextBox IDA01010; // document 
	@UiField TextBox IDA01020; // Enterprise name
	@UiField TextBox IDA01022; // Enterprise address
	@UiField TextBox IDA01023; // Municipio
	@UiField ListBox IDA01025; // Province Name
	@UiField TextBox year; // YEAR
	
	@UiField CheckBox SRP8080831;
	@UiField CheckBox SRP8080832; 
	
	@UiField TextBox SRP831001;
	@UiField TextArea SRP831002;
	@UiField TextArea SRP831003;
	@UiField CheckBox SRP831004;
	
	@UiField ListBox SRP831001B;
	@UiField TextArea SRP831002B;
	@UiField TextArea SRP831003B;
	@UiField CheckBox SRP831004B;
	@UiField TextArea SRP831005B;
	@UiField CheckBox SRP831006B;
	
	@UiField TextBox SRP831070;
	@UiField TextBox SRP8310709;
	@UiField CheckBox SRP831071;
	
	@UiField FlexTable table2;
	
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
		
		if(getYear() >= 2022) {
			SRP8080832 = new CheckBox();		
			SRP831001B = new ListBox();
			SRP831002B = new TextArea();
			SRP831003B = new TextArea();
			SRP831004B = new CheckBox();
			SRP831005B = new TextArea();
			SRP831006B = new CheckBox();
			
			SRP831070 = new TextBox();
			SRP8310709 = new TextBox();
			SRP831071 = new CheckBox();

			table2 = new FlexTable();
		} else {
			SRP8080831 = new CheckBox();		
			SRP831001 = new TextBox();
			SRP831002 = new TextArea();
			SRP831003 = new TextArea();
			SRP831004 = new CheckBox();
		}
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		initializeTable();
	}
	
	private void listBoxItemAdd(ListBox lb) {
//		for(Integer i = 0; i< D2DepositConstants.PROVINCES.length; i++){
//			Provinces p = D2DepositConstants.PROVINCES[i];
//			lb.addItem(p.getName());
//		}
		for (Provinces p : Provinces.values()) {
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
		
		if(getYear() >= 2022) {
			panel1.setVisible(false);
			keyExe("8080832", SRP8080832, "check", true);
			keyExe("831001", SRP831001B, "list2", true);
			keyExe("831005", SRP831005B, "area", true);
			keyExe("831003", SRP831003B, "area", true);
			keyExe("831002", SRP831002B, "area", true);
			keyExe("831004", SRP831004B, "check", true);
			keyExe("831006", SRP831006B, "check", true);
			
			keyExe("831070", SRP831070, "text", true);
			keyExe("8310709", SRP8310709, "text", true);
			keyExe("831071", SRP831071, "check", true);
		} else {
			panel2.setVisible(false);
			keyExe("8080831", SRP8080831, "check", true);
			keyExe("831001", SRP831001, "text", true);
			keyExe("831002", SRP831002, "area", true);
			keyExe("831003", SRP831003, "area", true);
			keyExe("831004", SRP831004, "check", true);	
		}
	}
	
	@Override
	protected void initializeTable() {
		init();		
		if(getYear() >= 2022) {
			defineTable2(table2, "", D2PDepositConstants.SRP_KEYS_2);
		} else defineTable(table,"",D2PDepositConstants.SRP_KEYS);
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
//				for (Integer i = 0 ; i< D2DepositConstants.PROVINCES.length; i++){
//					if(D2DepositConstants.PROVINCES[i].getId().equals(value)){
//						value2 = D2DepositConstants.PROVINCES[i].getName();
//					}
//				}
				for (Provinces p : Provinces.values()) {
					if (p.getId().equals(value)) {
						value2 = p.getName();
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
//					for(Integer i = 0; i< D2DepositConstants.PROVINCES.length;i++){
//						Provinces p = D2DepositConstants.PROVINCES[i];
//						if(lb.getSelectedItemText().equals(p.getName())){
//							value = p.getId(); 
//						}
//					}
					for (Provinces p : Provinces.values()) {
						if (lb.getSelectedItemText().equals(p.getName())) {
							value = p.getId();
						}
					}
					onEdit(key2, value);
				}
			});
		}
		
		if(type.equals("list2")){
			ListBox lb = (ListBox) w;
			lb.addItem("Municipal", "1");
			lb.addItem("Provincial", "2");
			lb.addItem("Autonomo", "3");
			lb.addItem("Nacional", "4");
			lb.addItem("Internacional", "5");
			
			if(getMap().containsKey(key2)){
				String value = getMap().get(key2);
				Integer v = Integer.parseInt(value);
				lb.setSelectedIndex(v-1) ;
			}
			lbAux = lb;
			lb.addChangeHandler(new ChangeHandler() {
				String key2 = key2Aux ;
				ListBox lb = lbAux;
				@Override
				public void onChange(ChangeEvent event) {
					onEdit(key2, lb.getSelectedValue());					
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
	
	protected void defineTable2( FlexTable tab, String title, D2DepositHeaderKey[][] keys){
		Integer row = 0;

		tab.setWidth("100%");
		tab.setCellSpacing(0);
		tab.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonWidthAuto());
		tab.getColumnFormatter().addStyleName(1, AON.AON_CSS.aonWidth140());

		tab.setWidget(row, 0, new Label(title));
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());

		tab.setWidget(row, 1, new Label("N\u00famero de Operaciones"));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextRight());
		++row;

		for (D2DepositHeaderKey[] innerKeys : keys) {
			row = paintKey2(tab, innerKeys , row);
		}
	}
	
	protected int paintKey(FlexTable tab, D2DepositHeaderKey[] keys,  int row) {
		paintKeyDescription(tab, keys[0], row, 0);
		paintKeyField(tab,keys[0], row, 1, false);
		paintKeyField(tab,keys[1],row,2,false);
		paintKeyField(tab,keys[2],row,3,false);
		return  ++row;
	}
	
	protected int paintKey2(FlexTable tab, D2DepositHeaderKey[] keys,  int row) {
		paintKeyDescription(tab, keys[0], row, 0);
		paintKeyField(tab,keys[0], row, 1, false);
		return  ++row;
	}
	
	protected void onEdit(String key, String value) {
		onEdit(key, value, false);
	}
}
