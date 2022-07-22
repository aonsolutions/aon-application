package net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory;


import java.util.Date;

import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.Provinces;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

import net.aonsolutions.aon.gwt.ccaa.client.Deposit2;

public class PageH7 extends PageAbs {

	@UiField TextBox IDA01010; // document 
	@UiField TextBox IDA01020; // Enterprise name
	@UiField TextBox IDA01022; // Enterprise address
	@UiField TextBox IDA01023; // Municipio
	@UiField ListBox IDA01025; // Province Name
	@UiField TextBox year; // YEAR
	
	@UiField CheckBox CVA8220000;
	@UiField ListBox CVA8220010;
	@UiField ListBox CVA8220020;
	@UiField DoubleBox CVA8220030;
	@UiField DoubleBox CVA8220035;
	@UiField DateBox CVA8220040;
	@UiField DateBox CVA8220050;
	
	@UiField DoubleBox CVA8220060;
	@UiField DoubleBox CVA8220065;
	@UiField DoubleBox CVA8220070;
	
	@UiField ListBox CVA8220080;
	@UiField CheckBox CVA8220090;
	@UiField CheckBox CVA8220100;

	@UiField DoubleBox CVA8220110;
	@UiField DoubleBox CVA8220120;

	@UiField TextArea CVA8220130;

	@UiField CheckBox CVA8220140;
	@UiField CheckBox CVA8220150;
	@UiField CheckBox CVA8220160;
	@UiField CheckBox CVA8220170;
	
	interface PageBinder extends UiBinder<Widget, PageH7> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);

	public PageH7(Deposit2 deposit) {
		super(deposit);
		IDA01010 = new TextBox();
		IDA01020 = new TextBox(); 
		IDA01022 = new TextBox();
		IDA01023 = new TextBox(); 
		IDA01025 = new ListBox();
		this.year = new TextBox();
		
		CVA8220000 = new CheckBox();
		CVA8220010 = new ListBox();
		CVA8220020 = new ListBox();
		
		CVA8220030 = new DoubleBox();
		CVA8220035 = new DoubleBox();
		CVA8220040 = new DateBox();
		CVA8220050 = new DateBox();
		CVA8220060 = new DoubleBox();
		CVA8220065 = new DoubleBox();
		CVA8220070 = new DoubleBox();
		
		CVA8220080 = new ListBox();
		CVA8220090 = new CheckBox();
		CVA8220100 = new CheckBox();
		
		CVA8220110 = new DoubleBox();
		CVA8220120 = new DoubleBox();
		
		CVA8220130 = new TextArea();
		
		CVA8220140 = new CheckBox();
		CVA8220150 = new CheckBox();
		CVA8220160 = new CheckBox();
		CVA8220170 = new CheckBox();
		
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		initializeTable();
	}
	
	private void listBoxItemAdd(ListBox lb) {
		for(Integer i = 0; i< D2DepositConstants.PROVINCES.length; i++){
			Provinces p = D2DepositConstants.PROVINCES[i];
			lb.addItem(p.getName(), p.getId());
		}
	}
	
	private void listBoxItemAdd2(ListBox lb) {
		lb.addItem("", "");
		lb.addItem("Por causa de fuerza mayor", "1");
		lb.addItem("Por causas t\u00e9cnicas-econ\u00f3micas-organizativas", "2");
		lb.addItem("Otras causas", "3");
	}
	
	private void listBoxItemAdd3(ListBox lb) {
		lb.addItem("", "");
		lb.addItem("Suspensi\u00f3n de contratos", "1");
		lb.addItem("Reducci\u00f3n de jornada", "2");
		lb.addItem("Ambos", "3");
	}
	
	private void listBoxItemAdd4(ListBox lb) {
		lb.addItem("", "");
		lb.addItem("No aplica", "0");
		lb.addItem("Rebaja de rentas a los arrendatarios", "1");
		lb.addItem("Reestructuraci\u00f3n de deudas", "2");
		lb.addItem("Ambos", "3");
		lb.addItem("Ninguno de los anteriores", "4");
	}

	private void init(){
		this.year.setText(getYear().toString());
		keyExe("1010", IDA01010, "text", false);	
		keyExe("1020", IDA01020, "text", false);
		keyExe("1022", IDA01022, "text", false);
		keyExe("1023", IDA01023, "text", false);
		listBoxItemAdd(IDA01025);
		keyExe("1025", IDA01025, "list", false);
		
		keyExe("8220000", CVA8220000, "check", true);
		listBoxItemAdd2(CVA8220010);
		keyExe("8220010", CVA8220010, "list", true);
		listBoxItemAdd3(CVA8220020);
		keyExe("8220020", CVA8220020, "list", true);
		keyExe("8220030", CVA8220030, "double", true);
		keyExe("8220035", CVA8220035, "double", true);

		keyExe("8220040", CVA8220040, "date", true);
		keyExe("8220050", CVA8220050, "date", true);

		keyExe("8220060", CVA8220060, "double", true);
		keyExe("8220065", CVA8220065, "double", true);
		keyExe("8220070", CVA8220070, "double", true);

		listBoxItemAdd4(CVA8220080);
		keyExe("8220080", CVA8220080, "list", true);
		keyExe("8220090", CVA8220090, "check", true);
		keyExe("8220100", CVA8220100, "check", true);
		
		keyExe("8220110", CVA8220110, "double", true);
		keyExe("8220120", CVA8220120, "double", true);
		keyExe("8220130", CVA8220130, "area", true);
		
		keyExe("8220140", CVA8220140, "check", true);		
		keyExe("8220150", CVA8220150, "check", true);
		keyExe("8220160", CVA8220160, "check", true);
		keyExe("8220170", CVA8220170, "check", true);
	}
	
	@Override
	protected void initializeTable() {
		init();		
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
			} else {
				onEdit(key2, "1");
			}
			c.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				String key2 = key2Aux;
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					onEdit(key2, event.getValue()?"1":"2");
				}
			});
		}
		
		
		if(type.equals("list")){
			ListBox lb = (ListBox) w;
			if(getMap().containsKey(key2)){
				
				String value = getMap().get(key2);
				
				for (Integer i = 0; i< lb.getItemCount(); i++) {
					if(lb.getValue(i).equals(value)){
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
		
		if(type.equals("date")) {
			DateBox date = (DateBox) w;
			date.setEnabled(enable);
			if(getMap().containsKey(key2)){
				String dStr = getMap().get(key2);
				Date d = AonDateUtils.parse("dd.MM.yyyy", dStr);
				if(d != null) date.setValue(d);		
			}
			date.addValueChangeHandler(new ValueChangeHandler<Date>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Date> event) {
					String value = AonDateUtils.format("dd.MM.yyyy", date.getValue());
					onEdit(key2, value);
				}
			});
			
		}
	}
	

	protected void onEdit(String key, String value) {
		onEdit(key, value, false);
	}
}
