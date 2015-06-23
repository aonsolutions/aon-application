package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import java.util.Map;

import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Header1 extends ResizeComposite {

	@UiField DocumentTextBox IDA01010; // document 
	@UiField CheckBox IDA01011; // SA
	@UiField CheckBox IDA01012; // SL
	@UiField TextBox IDA01013; // Other enterprise type
	@UiField TextBox IDA01020; // Enterprise name
	@UiField TextBox IDA01022; // Enterprise address
	@UiField TextBox IDA01023; // Municipio
	@UiField TextBox IDA01025; // Province Name
	@UiField TextBox IDA01024; // Código Postal
	@UiField TextBox IDA01031; // Phone
	@UiField TextBox IDA01037; // Enterprise email
	@UiField TextBox IDA01041; // groupMainEnterpriseName;
	@UiField TextBox IDA01040; // groupMainEnterpriseDocument;
	@UiField TextBox IDA01061; // groupLastEnterpriseName;
	@UiField TextBox IDA01060; // groupLastEnterpriseDocument;
	@UiField TextBox IDA02009; // enterpriseMainActivity;
	@UiField TextBox IDA02001; // cnaeCode;
	@UiField TextBox IDA04001; // fixedCurrentAvg;
	@UiField TextBox IDA040019; // fixedPreviousAvg;
	@UiField TextBox IDA04002; // noFixedCurrentAvg;
	@UiField TextBox IDA040029; // noFixedPreviousAvg;
	@UiField TextBox IDA04010; // disabilityCurrentAvg;
	@UiField TextBox IDA040109; // disabilityPreviousAvg;
	@UiField TextBox IDA04120; // man current
	@UiField TextBox IDA04121; // woman current
	@UiField TextBox IDA041209; // man previous
	@UiField TextBox IDA041219; // woman previous
	@UiField TextBox IDA04122; // man current 2 no fixed
	@UiField TextBox IDA04123; // woman current 2 no fixed
	@UiField TextBox IDA041229; // man previous 2 no fixed
	@UiField TextBox IDA041239; // woman previous 2 no fixed
	@UiField TextBox IDA01102; // currentDateInitial
	@UiField TextBox IDA011029; // PreviousDateInitial
	@UiField TextBox IDA01101; // currentDateClose
	@UiField TextBox IDA011019; // PreviousDateClose
	@UiField TextBox IDA01901; //numPages
	@UiField TextBox IDA01903; // causa
	@UiField CheckBox IDA09001; // euros
	@UiField CheckBox IDA09002; // miles de euros
	@UiField CheckBox IDA09003; // millones de euros
	
	final INormalizedMemoryAsync inma = GWT.create(INormalizedMemory.class);
	
	
	Map<String, String> map;
	interface Header1Binder extends UiBinder<Widget, Header1> {
	}

	private static final Header1Binder header1Binder = GWT
			.create(Header1Binder.class);

	private final static AonResources RESOURCES = GWT
			.create(AonResources.class);
	
	NormalizedMemory normalizedMemory;
	Enterprise enterprise;
	public Header1(Enterprise enterprise, NormalizedMemory nm) {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();
		normalizedMemory = nm;
		this.enterprise = enterprise;
		IDA01010 = new DocumentTextBox();
		IDA01011 = new CheckBox(); 
		IDA01012 = new CheckBox(); 
		IDA01013 = new TextBox(); 
		IDA01020 = new TextBox(); 
		IDA01022 = new TextBox();
		IDA01023 = new TextBox(); 
		IDA01025 = new TextBox(); 
		IDA01024 = new TextBox(); 
		IDA01031 = new TextBox(); 
		IDA01037 = new TextBox(); 
		IDA01041 = new TextBox(); 
		IDA01040 = new TextBox(); 
		IDA01061 = new TextBox(); 
		IDA01060 = new TextBox(); 
		IDA02009 = new TextBox(); 
		IDA02001 = new TextBox();
		IDA04001 = new TextBox();
		IDA040019 = new TextBox(); 
		IDA04002 = new TextBox(); 
		IDA040029 = new TextBox(); 
		IDA04010 = new TextBox(); 
		IDA040109 = new TextBox(); 
		IDA04120 = new TextBox();
		IDA04121 = new TextBox(); 
		IDA041209 = new TextBox(); 
		IDA041219 = new TextBox(); 
		IDA04122 = new TextBox(); 
		IDA04123 = new TextBox(); 
		IDA041229 = new TextBox(); 
		IDA041239 = new TextBox(); 
		IDA01102 = new TextBox();
		IDA011029  = new TextBox(); 
		IDA01101 = new TextBox(); 
		IDA011019 = new TextBox(); 
		IDA01901 = new TextBox(); 
		IDA01903 = new TextBox(); 
		IDA09001 = new CheckBox(); 
		IDA09002 = new CheckBox(); 
		IDA09003 = new CheckBox(); 

		init();
		Widget ui = header1Binder.createAndBindUi(this);
		initWidget(ui);
	}



	public void init(){
		inma.getSchema(enterprise.getDocument(),"IDA",enterprise.getDomain(),false, new AsyncCallback<Map<String, String>>() {
			
			@Override
			public void onSuccess(Map<String, String> result) {
				map = result;
				
				keyExe(map, "IDA01010", "1010", IDA01010, "text", false);
				
				keyExe(map, "IDA01011", "1011", IDA01011, "check", true);
				
				keyExe(map, "IDA01012", "1012", IDA01012, "check", true);
				
				keyExe(map, "IDA01013", "1013", IDA01013, "text", true);
				
				keyExe(map, "IDA01020", "1020", IDA01020, "text", true);
				
				keyExe(map, "IDA01022", "1022", IDA01022, "text", true);
				
				keyExe(map, "IDA01023", "1023", IDA01023, "text", true);
				
				keyExe(map, "IDA01025", "1025", IDA01025, "text", true);
				
				keyExe(map, "IDA01024", "1024", IDA01024, "text", true);
				
				keyExe(map, "IDA01031", "1031", IDA01031, "text", true);
				
				keyExe(map, "IDA01037", "1037", IDA01037, "text", true);
				
				keyExe(map, "IDA01041", "1041", IDA01041, "text", true);
				
				keyExe(map, "IDA01040", "1040", IDA01040, "text", true);
				
				keyExe(map, "IDA01061", "1061", IDA01061, "text", true);
				
				keyExe(map, "IDA01060", "1060", IDA01060, "text", true);
				
				keyExe(map, "IDA02009", "2009", IDA02009, "text", true);
				
				keyExe(map, "IDA02001", "2001", IDA02001, "text", true);
				
				keyExe(map, "IDA04001", "4001", IDA04001, "text", true);

				keyExe(map, "IDA040019", "40019", IDA040019, "text", true);

				keyExe(map, "IDA04002", "4002", IDA04002, "text", true);

				keyExe(map, "IDA040029", "40029", IDA040029, "text", true);

				keyExe(map, "IDA04010", "4010", IDA04010, "text", true);

				keyExe(map, "IDA040109", "40109", IDA040109, "text", true);

				keyExe(map, "IDA04120", "4120", IDA04120, "text", true);

				keyExe(map, "IDA04121", "4121", IDA04121, "text", true);

				keyExe(map, "IDA041209", "41209", IDA041209, "text", true);

				keyExe(map, "IDA041219", "41219", IDA041219, "text", true);

				keyExe(map, "IDA04122", "4122", IDA04122, "text", true);

				keyExe(map, "IDA04123", "4123", IDA04123, "text", true);

				keyExe(map, "IDA041229", "41229", IDA041229, "text", true);

				keyExe(map, "IDA041239", "41239", IDA041239, "text", true);

				keyExe(map, "IDA01102", "1102", IDA01102, "text", true);

				keyExe(map, "IDA011029", "11029", IDA011029, "text", true);

				keyExe(map, "IDA01101", "1101", IDA01101, "text", true);

				keyExe(map, "IDA011019", "11019", IDA011019, "text", true);

				keyExe(map, "IDA01901", "1901", IDA01901, "text", true);

				keyExe(map, "IDA01903", "1903", IDA01903, "text", true);
			
				keyExe(map, "IDA09001", "9001", IDA09001, "check", true);
				
				keyExe(map, "IDA09002", "9002", IDA09002, "check", true);
				
				keyExe(map, "IDA09003", "9003", IDA09003, "check", true);
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				
			}
		});	
	}
	
	String key2Aux;
	TextBox tAux;
	private void keyExe(Map<String, String> map, String key, String key2, Widget w, String type, Boolean enable) {
		key2Aux = key2;
		if(type.equals("text")) {
			TextBox t = (TextBox) w;
			if(map.containsKey(key)){
				t.setValue(map.get(key));
				t.setEnabled(enable);
			}
			tAux = t;
			t.addChangeHandler(new ChangeHandler() {
				String key2 = key2Aux ;
				TextBox t = tAux;
				@Override
				public void onChange(ChangeEvent event) {

					normalizedMemory.saveButton.setEnabled(true);
					normalizedMemory.cancelButton.setVisible(true);
					inma.updateSchema(enterprise.getDocument(),enterprise.getDomain(),key2, t.getValue(), new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {}
						@Override
						public void onSuccess(Void result) {}
					});
					if(key2.equals("1013")){
						IDA01011.setValue(false);
						specialUpdate("1011", "False");
						IDA01012.setValue(false);
						specialUpdate("1012", "False");
					}
				}
			});
		}
		
		if(type.equals("check")) {
			CheckBox c = (CheckBox) w;
			if(map.containsKey(key)){
				c.setValue(map.get(key).equals("True")); 
				c.setEnabled(enable);
			}
			c.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				String key2 = key2Aux;
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					normalizedMemory.saveButton.setEnabled(true);
					normalizedMemory.cancelButton.setVisible(true);		
					inma.updateSchema(enterprise.getDocument(),enterprise.getDomain(),key2, event.getValue()?"True":"False", new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {}
						@Override
						public void onSuccess(Void result) {}
					});					
					if(key2.equals("1011") || key2.equals("1012")){
						if(key2.equals("1011")){
							IDA01012.setValue(false);
							specialUpdate("1012", "False");
							IDA01013.setValue("");
							specialUpdate("1013", "");
						}
						else{
							IDA01011.setValue(false);
							specialUpdate("1011", "False");
							IDA01013.setValue("");		
							specialUpdate("1013", "");
						}
					}
					if(key2.equals("9001")){
						IDA09002.setValue(false);
						specialUpdate("9002", "False");
						IDA09003.setValue(false);
						specialUpdate("9003", "False");
					}else if(key2.equals("9002")){
						IDA09001.setValue(false);
						specialUpdate("9001", "False");
						IDA09003.setValue(false);
						specialUpdate("9003", "False");
					}else if(key2.equals("9003")){
						IDA09001.setValue(false);
						specialUpdate("9001", "False");
						IDA09002.setValue(false);		
						specialUpdate("9002", "False");
					}
				}
			});
		}
	}
	
	
	private void specialUpdate(String key, String value ){
		inma.updateSchema(enterprise.getDocument(),enterprise.getDomain(),key, value, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {}
			@Override
			public void onSuccess(Void result) {}
		});
	}
	
	
}
