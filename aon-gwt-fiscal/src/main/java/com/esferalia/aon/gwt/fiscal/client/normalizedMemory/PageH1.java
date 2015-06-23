package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import java.util.Map;

import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.fiscal.client.tree.node.D2DepositTreeObject;
import com.esferalia.aon.gwt.fiscal.client.tree.node.Mod2002014TreeObject;
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

public class PageH1 extends PageAbs {

	
	
	final INormalizedMemoryAsync inma = GWT.create(INormalizedMemory.class);
	
	
	
	Map<String, String> map;
	interface Header1Binder extends UiBinder<Widget, PageH1> {
	}

	private static final Header1Binder header1Binder = GWT
			.create(Header1Binder.class);

	private final static AonResources RESOURCES = GWT
			.create(AonResources.class);
	
	NormalizedMemory normalizedMemory;
	Enterprise enterprise;
	public PageH1(Enterprise enterprise, NormalizedMemory nm) {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();
		normalizedMemory = nm;
		this.enterprise = enterprise;

		
		
		Widget ui = header1Binder.createAndBindUi(this);
		initWidget(ui);
		
		initializeTable();
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
					/*if(key2.equals("1013")){
						IDA01011.setValue(false);
						specialUpdate("1011", "False");
						IDA01012.setValue(false);
						specialUpdate("1012", "False");
					}*/
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
					/*if(key2.equals("1011") || key2.equals("1012")){
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
					}*/
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
	
	public void dump(D2DepositTreeObject d2DepositObject){
		
	}
	
	@Override
	protected void initializeTable() {
		
	}
	
}
