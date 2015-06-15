package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import java.util.Map;

import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Paragraph15 extends ResizeComposite {

	@UiField TextBox MA15947001;
	@UiField TextBox MA159470019;
	@UiField TextBox MA15947002;
	@UiField TextBox MA159470029;
	@UiField TextBox MA15947011;
	@UiField TextBox MA159470119;
	@UiField TextBox MA15947012;
	@UiField TextBox MA159470129;
	@UiField TextBox MA15947021;
	@UiField TextBox MA159470219;
	@UiField TextBox MA15947022;
	@UiField TextBox MA159470229;
	@UiField TextBox MA15947041;
	@UiField TextBox MA159470419;
	@UiField TextBox MA15947042;
	@UiField TextBox MA159470429;
	
	
	final INormalizedMemoryAsync inma = GWT.create(INormalizedMemory.class);
	
	
	Map<String, String> map;
	interface Paragraph15Binder extends UiBinder<Widget, Paragraph15> {
	}
	
	Enterprise enterprise;
	NormalizedMemory normalizedMemory;

	private static final Paragraph15Binder binder = GWT
			.create(Paragraph15Binder.class);

	private final static AonResources RESOURCES = GWT
			.create(AonResources.class);

	public Paragraph15() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();

		Widget ui = binder.createAndBindUi(this);
		initWidget(ui);
	}

	public Paragraph15(Enterprise enterprise, NormalizedMemory nm) {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();

		this.enterprise = enterprise;
		this.normalizedMemory = nm;
		MA15947001 = new TextBox();
		MA159470019 = new TextBox();
		MA15947002 = new TextBox();
		MA159470029 = new TextBox();
		MA15947011 = new TextBox();
		MA159470119 = new TextBox();
		MA15947012 = new TextBox();
		MA159470129 = new TextBox();
		MA15947021 = new TextBox();
		MA159470219 = new TextBox();
		MA15947022 = new TextBox();
		MA159470229 = new TextBox();
		MA15947041 = new TextBox();
		MA159470419 = new TextBox();
		MA15947042 = new TextBox();
		MA159470429 = new TextBox();
		init();
		
		Widget ui = binder.createAndBindUi(this);
		initWidget(ui);
	}
	
	public void init() {
		inma.getSchema("MA15",enterprise.getDomain(), new AsyncCallback<Map<String, String>>() {
			
			@Override
			public void onSuccess(Map<String, String> result) {
				map = result;
				
				keyExe(map, "MA15947001", "947001", MA15947001, "text", true);
				keyExe(map, "MA159470019", "9470019", MA159470019, "text", true);
				keyExe(map, "MA15947002", "947002", MA15947002, "text", true);
				keyExe(map, "MA159470029", "9470029", MA159470029, "text", true);
				keyExe(map, "MA15947011", "947011", MA15947011, "text", true);
				keyExe(map, "MA159470119", "9470119", MA159470119, "text", true);
				keyExe(map, "MA15947012", "947012", MA15947012, "text", true);
				keyExe(map, "MA159470129", "9470129", MA159470129, "text", true);
				keyExe(map, "MA15947021", "947021", MA15947021, "text", true);
				keyExe(map, "MA159470219", "9470219", MA159470219, "text", true);
				keyExe(map, "MA15947022", "947022", MA15947022, "text", true);
				keyExe(map, "MA159470229", "9470229", MA159470229, "text", true);
				keyExe(map, "MA15947041", "947041", MA15947041, "text", true);
				keyExe(map, "MA159470419", "9470419", MA159470419, "text", true);
				keyExe(map, "MA15947042", "947042", MA15947042, "text", true);
				keyExe(map, "MA159470429", "9470429", MA159470429, "text", true);
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
					inma.updateSchema(enterprise.getDomain(),key2, t.getValue(), new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {}
						@Override
						public void onSuccess(Void result) {}
					});
				}
			});
		}
	}
}
