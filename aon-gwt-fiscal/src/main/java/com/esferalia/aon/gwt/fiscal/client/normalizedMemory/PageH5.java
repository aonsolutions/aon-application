package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import java.util.Map;

import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;

public class PageH5 extends PageAbs {

	interface PageBinder extends UiBinder<Widget, PageH5> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);

	@UiField CheckBox IMA8099000;
	@UiField CheckBox IMA8099010;

	public PageH5() {
		super();
		
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		init();

	}
	
	public PageH5(Enterprise enterprise, NormalizedMemory nm) {
		super();
		this.enterprise = enterprise;
		this.normalizedMemory = nm;
		IMA8099000 = new CheckBox();
		IMA8099010 = new CheckBox();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		init();
	}

	private void init(){
	inma.getSchema(enterprise.getDocument(),"IDA",enterprise.getDomain(),false, new AsyncCallback<Map<String, String>>() {
			
			@Override
			public void onSuccess(Map<String, String> result) {
				map = result;
				
		if(map.containsKey(IMA8099000.getName())){
			String value = map.get(IMA8099000.getName());
			IMA8099000.setValue(value.equals("1"));
			
		}
		if(map.containsKey(IMA8099010.getName())){
			String value = map.get(IMA8099010.getName());
			IMA8099010.setValue(value.equals("1"));
			
		}
		IMA8099000.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				normalizedMemory.saveButton.setEnabled(true);
				normalizedMemory.cancelButton.setVisible(true);	
				onEdit("8099000", event.getValue()?"1":"0");
				inma.updateSchema(enterprise.getDocument(),enterprise.getDomain(),"8099000", event.getValue()?"1":"0", new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {}
					@Override
					public void onSuccess(Void result) {}
				});		
				if(event.getValue()){
					IMA8099010.setValue(false);
					specialUpdate("8099010", "0");
				}
			}
		});
		IMA8099010.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				normalizedMemory.saveButton.setEnabled(true);
				normalizedMemory.cancelButton.setVisible(true);	
				onEdit("8099010", event.getValue()?"1":"0");
				inma.updateSchema(enterprise.getDocument(),enterprise.getDomain(),"8099010", event.getValue()?"1":"0", new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {}
					@Override
					public void onSuccess(Void result) {}
				});	
				if(event.getValue()){
					IMA8099000.setValue(false);
					specialUpdate("8099000", "0");
				}
			}
		});
		
		
		
		
			}
			@Override
			public void onFailure(Throwable caught) {
				
			}
		});
	}
	
	@Override
	protected void initializeTable() {
	
		


	}
	
	private void specialUpdate(String key, String value ){
		onEdit(key, value);
		inma.updateSchema(enterprise.getDocument(),enterprise.getDomain(),key, value, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {}
			@Override
			public void onSuccess(Void result) {}
		});
	}
	
	
}
