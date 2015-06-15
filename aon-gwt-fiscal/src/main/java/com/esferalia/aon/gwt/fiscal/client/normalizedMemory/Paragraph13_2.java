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

public class Paragraph13_2 extends ResizeComposite {

	
	@UiField TextBox MA1398000;
	@UiField TextBox MA13980009;
	@UiField TextBox MA1398001;
	@UiField TextBox MA13980019;
	@UiField TextBox MA1398002;
	@UiField TextBox MA13980029;
	@UiField TextBox MA1398003;
	@UiField TextBox MA13980039;
	@UiField TextBox MA1398004;
	@UiField TextBox MA13980049;
	@UiField TextBox MA1398005;
	@UiField TextBox MA13980059;
	@UiField TextBox MA1398006;
	@UiField TextBox MA13980069;
	@UiField TextBox MA1398007;
	@UiField TextBox MA13980079;
	
	
	final INormalizedMemoryAsync inma = GWT.create(INormalizedMemory.class);
	
	
	Map<String, String> map;
	interface Paragraph13_2Binder extends UiBinder<Widget, Paragraph13_2> {
	}
	
	Enterprise enterprise;
	NormalizedMemory normalizedMemory;
	private static final Paragraph13_2Binder binder = GWT
			.create(Paragraph13_2Binder.class);

	private final static AonResources RESOURCES = GWT
			.create(AonResources.class);

	public Paragraph13_2() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();

		Widget ui = binder.createAndBindUi(this);
		initWidget(ui);
	}
	
	public Paragraph13_2(Enterprise enterprise, NormalizedMemory nm) {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();

		this.enterprise = enterprise;
		this.normalizedMemory = nm;		
		MA1398000 = new TextBox();
		MA13980009 = new TextBox();
		MA1398001 = new TextBox();
		MA13980019 = new TextBox();
		MA1398002 = new TextBox();
		MA13980029 = new TextBox();
		MA1398003 = new TextBox();
		MA13980039 = new TextBox();
		MA1398004 = new TextBox();
		MA13980049 = new TextBox();
		MA1398005 = new TextBox();
		MA13980059 = new TextBox();
		MA1398006 = new TextBox();
		MA13980069 = new TextBox();
		MA1398007 = new TextBox();
		MA13980079 = new TextBox();
		
		init();
		
		Widget ui = binder.createAndBindUi(this);
		initWidget(ui);
	}
	
	public void init() {
		inma.getSchema("MA13",enterprise.getDomain(), new AsyncCallback<Map<String, String>>() {
			
			@Override
			public void onSuccess(Map<String, String> result) {
				map = result;
				keyExe(map, "MA1398000", "98000", MA1398000, "text", true);
				keyExe(map, "MA13980009", "980009", MA13980009, "text", true);
				keyExe(map, "MA1398001", "98001", MA1398001, "text", true);
				keyExe(map, "MA13980019", "980019", MA13980019, "text", true);
				keyExe(map, "MA1398002", "98002", MA1398002, "text", true);
				keyExe(map, "MA13980029", "980029", MA13980029, "text", true);
				keyExe(map, "MA1398003", "98003", MA1398003, "text", true);
				keyExe(map, "MA13980039", "980039", MA13980039, "text", true);
				keyExe(map, "MA1398004", "98004", MA1398004, "text", true);
				keyExe(map, "MA13980049", "980049", MA13980049, "text", true);
				keyExe(map, "MA1398005", "98005", MA1398005, "text", true);
				keyExe(map, "MA13980059", "980059", MA13980059, "text", true);
				keyExe(map, "MA1398006", "98006", MA1398006, "text", true);
				keyExe(map, "MA13980069", "980069", MA13980069, "text", true);
				keyExe(map, "MA1398007", "98007", MA1398007, "text", true);
				keyExe(map, "MA13980079", "980079", MA13980079, "text", true);
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
