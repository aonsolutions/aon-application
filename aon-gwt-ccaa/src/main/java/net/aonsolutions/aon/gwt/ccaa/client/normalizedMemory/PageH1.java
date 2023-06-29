package net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory;


import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.Cnae2009Panel;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.Provinces;
import com.esferalia.aon.occam.api.model.type.CNAE2009;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

import net.aonsolutions.aon.gwt.ccaa.client.Deposit2;

public class PageH1 extends PageAbs {

	@UiField HTMLPanel micropymePanel;
	@UiField HTMLPanel unityPanel;
	@UiField HTMLPanel societyPanel;
	@UiField HorizontalPanel LEIPanel;
	@UiField Label LEILabel;
	@UiField TextBox IDA01009; // LEI
	@UiField DocumentTextBox IDA01010; // document 
	@UiField CheckBox IDA01011; // SA
	@UiField CheckBox IDA01012; // SL
	@UiField TextBox IDA01013; // Other enterprise type
	@UiField TextBox IDA01020; // Enterprise name
	@UiField TextBox IDA01022; // Enterprise address
	@UiField TextBox IDA01023; // Municipio
	@UiField ListBox IDA01025; // Province Name
	@UiField TextBox IDA01024; // Código Postal
	@UiField TextBox IDA01031; // Phone
	@UiField TextBox IDA01037; // Enterprise email
	@UiField TextBox IDA01041; // groupMainEnterpriseName;
	@UiField TextBox IDA01040; // groupMainEnterpriseDocument;
	@UiField TextBox IDA01061; // groupLastEnterpriseName;
	@UiField TextBox IDA01060; // groupLastEnterpriseDocument;
	@UiField InlineLabel IDA02009; // enterpriseMainActivity;
	@UiField TextBox IDA02001; // cnaeCode;
	@UiField DoubleBox IDA04001; // fixedCurrentAvg;
	@UiField DoubleBox IDA040019; // fixedPreviousAvg;
	@UiField DoubleBox IDA04002; // noFixedCurrentAvg;
	@UiField DoubleBox IDA040029; // noFixedPreviousAvg;
	@UiField DoubleBox IDA04010; // disabilityCurrentAvg;
	@UiField DoubleBox IDA040109; // disabilityPreviousAvg;
	@UiField DoubleBox IDA04120; // man current
	@UiField DoubleBox IDA04121; // woman current
	@UiField DoubleBox IDA041209; // man previous
	@UiField DoubleBox IDA041219; // woman previous
	@UiField DoubleBox IDA04122; // man current 2 no fixed
	@UiField DoubleBox IDA04123; // woman current 2 no fixed
	@UiField DoubleBox IDA041229; // man previous 2 no fixed
	@UiField DoubleBox IDA041239; // woman previous 2 no fixed
	@UiField DateBox IDA01102; // currentDateInitial
	@UiField DateBox IDA011029; // PreviousDateInitial
	@UiField DateBox IDA01101; // currentDateClose
	@UiField DateBox IDA011019; // PreviousDateClose
	@UiField DoubleBox IDA01901; //numPages
	@UiField TextBox IDA01903; // causa
	@UiField CheckBox IDA01902; // MICROEMPRESAS
	@UiField CheckBox IDA09001; // euros
	@UiField CheckBox IDA09002; // miles de euros
	@UiField CheckBox IDA09003; // millones de euros
	
	@UiField Label current1;
	@UiField Label ant1;
	@UiField Label current2;
	@UiField Label ant2;
	@UiField Label current3;
	@UiField Label ant3;
	
	@UiField
	Button showCnae;

	Cnae2009Panel cnaePanel;

	interface Header1Binder extends UiBinder<Widget, PageH1> {}

	private static final Header1Binder header1Binder = GWT.create(Header1Binder.class);

	public PageH1(Deposit2 deposit) {
		super(deposit);
		initialize();
	}
	
	private void initialize() {
		IDA01009 = new TextBox();
		IDA01010 = new DocumentTextBox();
		IDA01011 = new CheckBox(); 
		IDA01012 = new CheckBox(); 
		IDA01013 = new TextBox(); 
		IDA01020 = new TextBox(); 
		IDA01022 = new TextBox();
		IDA01023 = new TextBox(); 
		IDA01025 = new ListBox();
		
		
		IDA01024 = new TextBox(); 
		IDA01031 = new TextBox(); 
		IDA01037 = new TextBox(); 
		IDA01041 = new TextBox(); 
		IDA01040 = new TextBox(); 
		IDA01061 = new TextBox(); 
		IDA01060 = new TextBox(); 
		IDA02009 = new InlineLabel(); 
		
		IDA02001 = new TextBox(); //TODO CNAE

		IDA04001 = new DoubleBox();
		IDA040019 = new DoubleBox(); 
		IDA04002 = new DoubleBox(); 
		IDA040029 = new DoubleBox(); 
		IDA04010 = new DoubleBox(); 
		IDA040109 = new DoubleBox(); 
		IDA04120 = new DoubleBox();
		IDA04121 = new DoubleBox(); 
		IDA041209 = new DoubleBox(); 
		IDA041219 = new DoubleBox(); 
		IDA04122 = new DoubleBox(); 
		IDA04123 = new DoubleBox(); 
		IDA041229 = new DoubleBox(); 
		IDA041239 = new DoubleBox(); 
		IDA01102 = new DateBox();
		IDA011029  = new DateBox(); 
		IDA01101 = new DateBox(); 
		IDA011019 = new DateBox(); 
		IDA01901 = new DoubleBox(); 
		IDA01903 = new TextBox(); 
		IDA01902 = new CheckBox();
		IDA09001 = new CheckBox(); 
		IDA09002 = new CheckBox(); 
		IDA09003 = new CheckBox(); 

		current1 = new Label();
		ant1 = new Label();
		current2 = new Label();
		ant2 = new Label();
		current3 = new Label();
		ant3 = new Label();

		Widget ui = header1Binder.createAndBindUi(this);
		initWidget(ui);
		initializeTable();
	}
	
	private void listBoxItemAdd(ListBox lb) {
		for(Integer i = 0; i< D2DepositConstants.PROVINCES.length; i++){
			Provinces p = D2DepositConstants.PROVINCES[i];
			lb.addItem(p.getName());
		}
	}
	
	@UiHandler("showCnae")
	void onSelectCnae(ClickEvent event) {
		cnaePanel.onShow();
	}
	
	private void init(){
		cnaePanel = new Cnae2009Panel( new Cnae2009Panel.SelectionCallBack() {
			@Override
			public void onSelect(CNAE2009 selected) {
				IDA02001.setEnabled(false);
				IDA02001.setText(selected.getCodeWithoutPoint());
		
				onEdit("2001", selected.getCodeWithoutPoint());

				IDA02009.setText(selected.getDescription());
				onEdit("2009", selected.getDescription());				
			}
			@Override
			public void onClose() {
				// Nothing
			}
		});
		current1.setText("Ejercicio " + getYear());
		ant1.setText("Ejercicio " + (getYear()-1));
		current2.setText("Ejercicio " + getYear());
		ant2.setText("Ejercicio " + (getYear()-1));
		current3.setText("Ejercicio " + getYear());
		ant3.setText("Ejercicio" + (getYear()-1));

		if(getYear() >= 2015){
			keyExe("1009", IDA01009, "text", true);	
		} else{
			IDA01009.setVisible(false);
			LEILabel.setVisible(false);
			LEIPanel.setVisible(false);
		}	
		
		keyExe("1010", IDA01010, "text", false);	
		keyExe("1011", IDA01011, "check", true);
		keyExe("1012", IDA01012, "check", true);
		keyExe("1013", IDA01013, "text", true);
		keyExe("1020", IDA01020, "text", true);
		keyExe("1022", IDA01022, "text", true);
		keyExe("1023", IDA01023, "text", true);

		listBoxItemAdd(IDA01025);
		keyExe("1025", IDA01025, "list", true);
		keyExe("1024", IDA01024, "text", true);
		keyExe("1031", IDA01031, "text", true);
		keyExe("1037", IDA01037, "text", true);
		keyExe("1041", IDA01041, "text", true);
		keyExe("1040", IDA01040, "text", true);
		keyExe("1061", IDA01061, "text", true);
		keyExe("1060", IDA01060, "text", true);
		keyExe("2009", IDA02009, "label", true);

		IDA02001.setEnabled(false);
		keyExe("2001", IDA02001, "text", false);
		keyExe("4001", IDA04001, "double", true);
		keyExe("40019", IDA040019, "double", true);
		keyExe("4002", IDA04002, "double", true);
		keyExe("40029", IDA040029, "double", true);
		keyExe("4010", IDA04010, "double", true);
		keyExe("40109", IDA040109, "double", true);
		keyExe("4120", IDA04120, "double", true);
		keyExe("4121", IDA04121, "double", true);
		keyExe("41209", IDA041209, "double", true);
		keyExe("41219", IDA041219, "double", true);
		keyExe("4122", IDA04122, "double", true);
		keyExe("4123", IDA04123, "double", true);
		keyExe("41229", IDA041229, "double", true);
		keyExe("41239", IDA041239, "double", true);
		keyExe("1102", IDA01102, "date", true);
		keyExe("11029", IDA011029, "date", true);
		keyExe("1101", IDA01101, "date", true);
		keyExe("11019", IDA011019, "date", true);
		keyExe("1901", IDA01901, "double", true);
		keyExe("1903", IDA01903, "text", true);
		keyExe("1902", IDA01902, "check", true);
		keyExe("9001", IDA09001, "check", true);	
		keyExe("9002", IDA09002, "check", true);		
		keyExe("9003", IDA09003, "check", true);

		if(isPymes()){
			societyPanel.setVisible(false);
			micropymePanel.setVisible(true);
			unityPanel.setVisible(false);
		}

		if(!isPymes()){
			societyPanel.setVisible(true);
			micropymePanel.setVisible(false);
			unityPanel.setVisible(true);
		}
	}
	
	@Override
	protected void initializeTable() {
		init();		
	}
	
	String key2Aux;
	TextBox tAux;
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
					if(key2.equals("1011") || key2.equals("1012")){
						if(key2.equals("1011")){
							IDA01012.setValue(false);
							specialUpdate("1012", "0");
							IDA01013.setValue("");
							specialUpdate("1013", "");
						}
						else{
							IDA01011.setValue(false);
							specialUpdate("1011", "0");
							IDA01013.setValue("");		
							specialUpdate("1013", "");
						}
					}		
					if(key2.equals("9001")){
						IDA09002.setValue(false);
						specialUpdate("9002", "0");
						IDA09003.setValue(false);
						specialUpdate("9003", "0");
					}else if(key2.equals("9002")){
						IDA09001.setValue(false);
						specialUpdate("9001", "0");
						IDA09003.setValue(false);
						specialUpdate("9003", "0");
					}else if(key2.equals("9003")){
						IDA09001.setValue(false);
						specialUpdate("9001", "0");
						IDA09002.setValue(false);		
						specialUpdate("9002", "0");
					}
				}
			});
		}
		if(type.equals("date")){
		    
			//SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
  
			DateBox d = (DateBox) w;
			d.setFormat(new DateBox.DefaultFormat(AON.DATE_FORMAT));
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
	
	
	private void specialUpdate(String key, String value ){
		onEdit(key, value);
	}
	
	protected void onEdit(String key, String value) {
		onEdit(key, value, false);
	}
}
