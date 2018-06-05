package com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.Deposit;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.Cities;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositFooterKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.Provinces;
import com.esferalia.aon.watson.util.AonMathUtils;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class PageF2 extends PageAbs {

	interface PageBinder extends UiBinder<Widget, PageF2> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);
	
	@UiField Label IDA01010;
	@UiField Label IDA01020;
	
	@UiField Label IDA01101;
	
	@UiField ListBox PR8081001 ; // ciudad / provinci
	@UiField TextBox PR8081002 ; // tomo
	@UiField TextBox PR8081003 ; // folio
	@UiField TextBox PR8081004 ; // num hojas registral
	
	@UiField CheckBox PR8080805 ; //CHECKBOX Abreviado - memoria
	@UiField CheckBox PR8080854 ; //CHECKBOX Abreviado - ecpn
	@UiField CheckBox PR8080801 ; //CHECKBOX Abreviado - balance
	@UiField CheckBox PR8080803 ; //CHECKBOX Abreviado - pyg
	@UiField CheckBox PR8080811 ; //CHECKBOX Abreviado - certificacion acuerdo
	@UiField CheckBox PR8080800 ; //CHECKBOX Abreviado - hoja identificacion
	@UiField CheckBox PR8080819 ; //CHECKBOX Abreviado - declaracion medioambiental
	
	@UiField CheckBox PR8080855 ; //CHECKBOX Pymes - ecpn
	@UiField CheckBox PR8080850 ; //CHECKBOX Abreviado - balance
	@UiField CheckBox PR8080851 ; //CHECKBOX Abreviado - pyg
	@UiField CheckBox PR8080852 ; //CHECKBOX Abreviado - memoria
	
	@UiField CheckBox PR8080807 ; //CHECKBOX  INFORME DE GESTION
	@UiField CheckBox PR8080817 ; //CHECKBOX  INFORME DE AUDITORIA
	@UiField CheckBox PR8080809 ; //CHECKBOX  MODELO DE AUTOCARTERA
	@UiField CheckBox PR8080823 ; //CHECKBOX  ANUNCIOS DE CONVOCATORIA
	@UiField CheckBox PR8080821 ; //CHECKBOX  SICAV
	
	@UiField TextBox PR8081201 ; //Nombre y apellidos
	@UiField TextBox PR8081202 ; //  dni
	@UiField TextBox PR8081203 ; // domicilio
	@UiField TextBox PR8081204 ; // ciudad
	@UiField TextBox PR8081205 ; // codigo postal
	@UiField ListBox PR8081206 ; // provincia
	@UiField TextBox PR8081207 ; // fax
	@UiField TextBox PR8081208 ; // telefono
	@UiField TextBox PR8081209 ; // email
	
	@UiField TextBox ROAC; // ROAC
	@UiField Label ROACLabel; // ROAC
	
	public PageF2(Deposit deposit) {
		super(deposit);
		
		IDA01010 = new Label();
		IDA01020 = new Label();
		
		IDA01101 = new Label();
		
		PR8081001 = new ListBox(); // ciudad / provinci
		PR8081002 = new TextBox(); // tomo
		PR8081003 = new TextBox(); // folio
		PR8081004 = new TextBox(); // num hojas registral
		
		PR8080805 = new CheckBox(); //CHECKBOX Abreviado - memoria
		PR8080854 = new CheckBox(); //CHECKBOX Abreviado - ecpn

		PR8080801 = new CheckBox(); //CHECKBOX Abreviado - balance
		PR8080803 = new CheckBox(); //CHECKBOX Abreviado - pyg
		PR8080811 = new CheckBox(); //CHECKBOX Abreviado - certificacion acuerdo
		PR8080800 = new CheckBox(); //CHECKBOX Abreviado - hoja identificacion
		PR8080819 = new CheckBox(); //CHECKBOX Abreviado - declaracion medioambiental
		
		PR8080855 = new CheckBox(); //CHECKBOX Pymes - ecpn
		PR8080850 = new CheckBox(); //CHECKBOX Pymes - balance
		PR8080851 = new CheckBox(); //CHECKBOX Pymes - pyg
		PR8080852 = new CheckBox(); //CHECKBOX Pymes - memoria
		
		PR8080807 = new CheckBox(); //CHECKBOX  INFORME DE GESTION
		PR8080817 = new CheckBox(); //CHECKBOX  INFORME DE AUDITORIA
		PR8080809 = new CheckBox(); //CHECKBOX  MODELO DE AUTOCARTERA
		PR8080823 = new CheckBox(); //CHECKBOX  ANUNCIOS DE CONVOCATORIA
		PR8080821 = new CheckBox(); //CHECKBOX  SICAV

		PR8081201 = new TextBox(); //Nombre y apellidos
		PR8081202 = new TextBox(); //  dni
		PR8081203 = new TextBox(); // domicilio
		PR8081204 = new TextBox(); // ciudad
		PR8081205 = new TextBox(); // codigo postal
		PR8081206 = new ListBox(); // provincia
		PR8081207 = new TextBox(); // fax
		PR8081208 = new TextBox(); // telefono
		PR8081209 = new TextBox(); // email
		
		ROAC = new TextBox();
		ROACLabel = new Label();
		
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		
		initializeTable();
	}

	private void init() {
		if(getMap().containsKey(D2DepositHeaderKey.IDA01010.getCode())){
			IDA01010.setText(getMap().get(D2DepositHeaderKey.IDA01010.getCode()));// "IDA01010"));
		}
		
		if(getMap().containsKey(D2DepositHeaderKey.IDA01020.getCode())){
			IDA01020.setText(getMap().get(D2DepositHeaderKey.IDA01020.getCode()));// "IDA01020"));
		}
		
		if(getMap().containsKey(D2DepositHeaderKey.IDA01101.getCode())){ 
			IDA01101.setText(getMap().get(D2DepositHeaderKey.IDA01101.getCode()));// "IDA01101"));
		}
				
		listBoxItemAddCities(PR8081001);
		keyExe("8081001", PR8081001, "list", true);

		keyExe("8081002", PR8081002, "text", true);
		keyExe("8081003", PR8081003, "text", true);
		keyExe("8081004", PR8081004, "text", true);

		keyExe(D2DepositFooterKey.PR8080805.getCode(), PR8080805, "check", false);
		keyExe(D2DepositFooterKey.PR8080854.getCode(), PR8080854, "check", false);
		keyExe(D2DepositFooterKey.PR8080801.getCode(), PR8080801, "check", false);
		keyExe(D2DepositFooterKey.PR8080803.getCode(), PR8080803, "check", false);
		keyExe(D2DepositFooterKey.PR8080811.getCode(), PR8080811, "check", true);
		keyExe(D2DepositFooterKey.PR8080800.getCode(), PR8080800, "check", false);
		keyExe(D2DepositFooterKey.PR8080819.getCode(), PR8080819, "check", false);

		keyExe(D2DepositFooterKey.PR8080855.getCode(), PR8080855, "check", false); //CHECKBOX Pymes - ecpn
		keyExe(D2DepositFooterKey.PR8080850.getCode(), PR8080850, "check", false); //CHECKBOX Abreviado - balance
		keyExe(D2DepositFooterKey.PR8080851.getCode(), PR8080851, "check", false); //CHECKBOX Abreviado - pyg
		keyExe(D2DepositFooterKey.PR8080852.getCode(), PR8080852, "check", false); //CHECKBOX Abreviado - memoria
		
		keyExe(D2DepositFooterKey.PR8080807.getCode(), PR8080807, "check", false);
		keyExe(D2DepositFooterKey.PR8080817.getCode(), PR8080817, "check", false); 
		keyExe(D2DepositFooterKey.PR8080809.getCode(), PR8080809, "check", false);
		keyExe(D2DepositFooterKey.PR8080823.getCode(), PR8080823, "check", false); 
		keyExe(D2DepositFooterKey.PR8080821.getCode(), PR8080821, "check", false); 

		PR8081201.setWidth("99%");
		keyExe("8081201", PR8081201, "text", true);
		PR8081202.setWidth("99%");
		keyExe("8081202", PR8081202, "text", true);
		PR8081203.setWidth("99%");
		keyExe("8081203", PR8081203, "text", true);
		PR8081204.setWidth("99%");
		keyExe("8081204", PR8081204, "text", true);
		PR8081205.setWidth("99%");
		keyExe("8081205", PR8081205, "text", true);
		PR8081206.setWidth("99%");
		listBoxItemAdd(PR8081206);
		keyExe("8081206", PR8081206, "list", true);
		PR8081207.setWidth("99%");
		keyExe("8081207", PR8081207, "text", true);
		PR8081208.setWidth("99%");
		keyExe("8081208", PR8081208, "text", true);
		PR8081209.setWidth("99%");
		keyExe("8081209", PR8081209, "text", true);
		
		if(getYear() > 2014) {
			ROACLabel.setText("Codigo ROAC del Auditor firmante");
			keyExe("8081320", ROAC, "text", true);
		}
		else {
			ROAC.setVisible(false);
			ROACLabel.setVisible(false);
		}
		
		if(getYear() >= 2016) {
			PR8080854.setVisible(false);
			PR8080855.setVisible(false);
		}
	}
	
	@Override
	protected void initializeTable() {
		init();
	}
	

	protected int paintKey(FlexTable tab, D2DepositKey[] keys,  int row) {
		paintKeyDescription(tab, keys[0], row, 0);
		for (Integer i = 0; i < keys.length ; i++) {
			paintKeyField(tab,keys[i],row,i+1);
		}
		return  ++row;
	}

	String key2Aux;
	TextBox tAux;
	DateBox dAux;
	ListBox lbAux;
	DoubleBox dlAux;
	private void keyExe(String key2, Widget w, String type, Boolean enable) {
		key2Aux = key2;
		if(type.equals("text")) {
			TextBox t = (TextBox) w;
			if(getMap().containsKey(key2)){
				t.setValue(getMap().get(key2));
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
		if(type.equals("date")){
		    DateBox d = (DateBox) w;
			dAux = d;
			if(getMap().containsKey(key2)){
				
				String datestr = getMap().get(key2);
				getDeposit().getInma().getDate(datestr, new AsyncCallback<Date>() {
					DateBox d = dAux;
					@Override
					public void onSuccess(Date result) {
						d.setValue(result); 
						
					}
					
					@Override
					public void onFailure(Throwable caught) {
						
					}
				} );
				d.setEnabled(enable);
			}
			
			d.addValueChangeHandler(new ValueChangeHandler<Date>() {
				String key2 = key2Aux;
				DateBox d = dAux;
				@Override
				public void onValueChange(ValueChangeEvent<Date> event) {					
					Integer day = d.getValue().getDate();
					Integer month = d.getValue().getMonth()+1;
					Integer year = d.getValue().getYear()+1900;
					String value = day+"."+month+"."+year;
					onEdit(key2, value);
					
					String dayKey = null;
					String monthKey = null;
					String yearKey = null;
					if(key2.equals("1102")){
						yearKey = "11021";
						monthKey = "11022";
						dayKey = "11023";
					}
					if(key2.equals("11029")){
						yearKey = "110219";
						monthKey = "110229";
						dayKey = "110239";
					}
					if(key2.equals("1101")){
						yearKey = "11011";
						monthKey = "11012";
						dayKey = "11013";
					}
					if(key2.equals("11019")){
						yearKey = "110119";
						monthKey = "110129";
						dayKey = "110139";
					}
					if(dayKey != null){
						onEdit(dayKey, day.toString());
						
					}
					if(monthKey != null){
						onEdit(monthKey, month.toString());
						
					}
					if(yearKey != null){
						onEdit(yearKey, year.toString());
					}
				}
			});
		}
		
		
		if(type.equals("list")){
			ListBox lb = (ListBox) w;
			if(getMap().containsKey(key2)){
				String value = getMap().get(key2);
				String value2 = "";
				if(key2.equals("8081206")){
					for(Integer i = 0;i< D2DepositConstants.PROVINCES.length; i++){
						if(D2DepositConstants.PROVINCES[i].getId().equals(value)){
							value2 = D2DepositConstants.PROVINCES[i].getName();
						}
					}
				}
				if(key2.equals("8081001")){
					for(Integer i = 0;i< D2DepositConstants.CITIES.length; i++){
						if(D2DepositConstants.CITIES[i].getId().equals(value)){
							value2 = D2DepositConstants.CITIES[i].getName();
						}
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
					if(key2.equals("8081206")){
						for(Integer i = 0;i< D2DepositConstants.PROVINCES.length; i++){
							if(D2DepositConstants.PROVINCES[i].getName().equals(lb.getSelectedItemText())){
								value = D2DepositConstants.PROVINCES[i].getId(); 
							}
						}
					}
					if(key2.equals("8081001")){
						for(Integer i = 0;i< D2DepositConstants.CITIES.length; i++){
							if(D2DepositConstants.CITIES[i].getName().equals(lb.getSelectedItemText())){
								value = D2DepositConstants.CITIES[i].getId(); 
							}
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
					onEdit(key2, Double.toString(AonMathUtils.round(dl.getValue())));
				}
			});
		}
	}
	private void listBoxItemAdd(ListBox lb) {
		for(Integer i = 0; i< D2DepositConstants.PROVINCES.length; i++){
			Provinces p = D2DepositConstants.PROVINCES[i];
			lb.addItem(p.getName());
		}
	}
	private void listBoxItemAddCities(ListBox lb) {
		for(Integer i = 0; i< D2DepositConstants.CITIES.length; i++){
			Cities p = D2DepositConstants.CITIES[i];
			lb.addItem(p.getName());
		}
	}
	
	@Override
	protected void onEdit(String key, String value) {
		Map<String, String> m = new HashMap<String, String>();
		for (String k : getMap().keySet()) {
			m.put(k, getMap().get(k));
		}
		getDeposit().getUndoStack().push(m);
		getDeposit().getRedoStack().clear();
		
		getMap().put(key, value);
		getDeposit().getInma().saveDeposit(getAonData(), getMap(), getYear(), new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				getDeposit().refreshPage();
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
}
